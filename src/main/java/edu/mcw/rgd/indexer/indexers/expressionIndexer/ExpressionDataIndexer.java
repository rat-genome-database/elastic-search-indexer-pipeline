package edu.mcw.rgd.indexer.indexers.expressionIndexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.GeneExpressionRecord;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;

import edu.mcw.rgd.indexer.model.ExpressionDataIndexObject;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.JacksonConfiguration;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.xcontent.XContentType;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExpressionDataIndexer implements Runnable{

    private Gene gene;
    private  String species;
    private List<GeneExpression> records;
    IndexDAO indexDAO=new IndexDAO();

    GeneExpressionDAO geneExpressionDAO=new GeneExpressionDAO();
    OntologyXDAO xdao=new OntologyXDAO();
    public ExpressionDataIndexer(Gene gene) {
        this.gene=gene;
    }

    @Override
    public void run() {
        int speciesTypeKey = gene.getSpeciesTypeKey();
        String species = SpeciesType.getCommonName(speciesTypeKey);
        boolean isSearchable=SpeciesType.isSearchable(speciesTypeKey);
        if (!isSearchable) return;
        this.species=species;
        setExpressionRecords();

        try {
            if(records.size()>0)
            index();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void mapGene(ExpressionDataIndexObject object){
        object.setGeneRgdId(String.valueOf(gene.getRgdId()));
        object.setGeneSymbol(gene.getSymbol());
        object.setGeneSymbolWithRgdId(gene.getSymbol()+"-RGD:"+gene.getRgdId());
        try {
            object.setMapDataList(indexDAO.getMapData(gene.getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void setExpressionRecords()  {
        try {
            this.records= geneExpressionDAO.getGeneExpressionObjectsByRgdIdUnit(gene.getRgdId(), "TPM")
                    .stream().filter(r->r.getGeneExpressionRecordValue().getExpressionLevel()!=null).filter(r->
                            ( r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("high") ||
                                    r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("low") ||
                                    r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("medium") )).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    Set<String> getStrainAccIds(){
       return records.stream().map(r->r.getSample().getStrainAccId()).collect(Collectors.toSet());
    }
    Set<String> getTissueAccIds(){
        return records.stream().map(r->r.getSample().getTissueAccId()).collect(Collectors.toSet());
    }
    List<GeneExpression> getFilteredRecords(String strainAccId, String tissueAccId){
        List<GeneExpression> filteredRecs=new ArrayList<>();
        for(GeneExpression rec:records){
            if(rec.getSample()!=null && rec.getSample().getStrainAccId()!=null &&  rec.getSample().getTissueAccId()!=null &&
                    rec.getSample().getStrainAccId().equalsIgnoreCase(strainAccId) && rec.getSample().getTissueAccId().equalsIgnoreCase(tissueAccId)){
                filteredRecs.add(rec);
            }
        }
        return filteredRecs;
    }
    void index() throws Exception {
//       indexNormalised();
       indexDenormalized();
    }
    void indexDenormalized(){
        if(records!=null && records.size()>0) {
        //    DecimalFormat df=new DecimalFormat("#.####");
            for(GeneExpression record:records) {
                ExpressionDataIndexObject object = new ExpressionDataIndexObject();
                object.setSpecies(species);
                object.setStrainAcc(record.getSample().getStrainAccId());
                try {
                    if (object.getStrainAcc() != null && !object.getStrainAcc().equals(""))
                        object.setStrainTerm(getTerm(object.getStrainAcc()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                object.setTissueAcc(record.getSample().getTissueAccId());

                try {
                    if (object.getTissueAcc() != null && !object.getTissueAcc().equals(""))
                        object.setTissueTerm(getTerm(object.getTissueAcc()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                object.setExpressionLevel(new HashSet<>(Collections.singleton(record.getGeneExpressionRecordValue().getExpressionLevel())));
                object.setExpressionValue(new ArrayList<>(Collections.singleton(record.getGeneExpressionRecordValue().getExpressionValue())));
                mapGene(object);
                IndexDocument.index(object);


            }
                }
    }
    void indexNormalised(){
        if(records!=null && records.size()>0) {
            DecimalFormat df=new DecimalFormat("#.####");
            for(String sampleId:getStrainAccIds()){
                for(String tissueId:getTissueAccIds()){
                    List<GeneExpression> filteredRecords=getFilteredRecords(sampleId, tissueId);
                    if(filteredRecords.size()>0){
                        ExpressionDataIndexObject object = new ExpressionDataIndexObject();
                        object.setSpecies(species);
                        object.setStrainAcc(sampleId);
                        try {
                            if (object.getStrainAcc() != null && !object.getStrainAcc().equals(""))
                                object.setStrainTerm(getTerm(object.getStrainAcc()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        object.setTissueAcc(tissueId);

                        try {
                            if (object.getTissueAcc() != null && !object.getTissueAcc().equals(""))
                                object.setTissueTerm(getTerm(object.getTissueAcc()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        List<Double> values=new ArrayList<>();
                        Set<String> level=new HashSet<>();
                        double valueSum=0;
                        for (GeneExpression record:filteredRecords) {
                            Double val=record.getGeneExpressionRecordValue().getExpressionValue();
                            valueSum+=val;
                            values.add(val);
                            level.add(record.getGeneExpressionRecordValue().getExpressionLevel());

                        }
                        double valueMean= Double.parseDouble(df.format(valueSum / filteredRecords.size()));
                        object.setExpressionLevel(level);
                        object.setExpressionValue(values);
                        object.setValueMean(valueMean);
                        if(valueMean>0){
                           object.setLogValue(Math.log(valueMean));
                        }
                        mapGene(object);
                        IndexDocument.index(object);
                    }


                }}}
    }
//    void index(ExpressionDataIndexObject object){
//        try {
//            byte[] json = JacksonConfiguration.MAPPER.writeValueAsBytes(object);
//            IndexRequest request = new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON);
//            BulkIndexProcessor.bulkProcessor.add(request);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }



    String getTerm(String accId) throws Exception {
        Term term = xdao.getTerm(accId);
          return term.getTerm();

    }
}
