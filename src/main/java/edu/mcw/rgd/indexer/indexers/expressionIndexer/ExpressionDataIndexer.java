package edu.mcw.rgd.indexer.indexers.expressionIndexer;

import com.google.gson.Gson;
import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.expression.ExpressionDataIndexObject;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermDagEdge;
import edu.mcw.rgd.indexer.dao.IndexDAO;

import edu.mcw.rgd.indexer.model.IndexDocument;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExpressionDataIndexer implements Runnable{

    private Gene gene;
    private  String species;
    private List<GeneExpression> records;
    private Map<String, Set<String>> parentAccIds;

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
            if(records.size()>0){
                this.parentAccIds=getParentEdges();
                index();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void mapGene(ExpressionDataIndexObject object){
        object.setGeneRgdId(gene.getRgdId());
        object.setGeneSymbol(gene.getSymbol());
        object.setGeneSymbolWithRgdId(gene.getSymbol()+"-RGD:"+gene.getRgdId());
//        try {
//            object.setMapDataList(indexDAO.getMapData(gene.getRgdId()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
    Map<String, Set<String>> getParentEdges(){
        Map<String, Set<String>> parentAccIds=new HashMap<>();
        for(String id:getTissueAccIds()){
          getParentEdges(parentAccIds, id);
        }
        for(String id:getStrainAccIds()){
            getParentEdges(parentAccIds, id);
        }
        for(String id:getConditionAccIds()){
            getParentEdges(parentAccIds, id);
        }
         return parentAccIds;
    }

    private void getParentEdges(Map<String, Set<String>> parentAccIds, String id) {
        try {
            List<TermDagEdge> parentTermEdges = xdao.getAllParentEdges(id);

            Set<String> parentTermAccIds = parentTermEdges.stream().map(TermDagEdge::getParentTermAcc).collect(Collectors.toSet());
            parentAccIds.put(id, parentTermAccIds);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Set<String> getStrainAccIds(){
       return records.stream().map(r->r.getSample().getStrainAccId()).collect(Collectors.toSet());
    }
    Set<String> getTissueAccIds(){
        return records.stream().map(r->r.getSample().getTissueAccId()).collect(Collectors.toSet());
    }
    Set<String> getConditionAccIds(){
        return records.stream().map(r->r.getGeneExpressionRecord().getConditionAccId()).collect(Collectors.toSet());
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
    //   indexDenormalized();
        indexDenormalizedForExpressionTool();
    }
//    void indexDenormalized(){
//        if(records!=null && records.size()>0) {
//        //    DecimalFormat df=new DecimalFormat("#.####");
//            for(GeneExpression record:records) {
//                ExpressionDataIndexObject object = new ExpressionDataIndexObject();
//                object.setGeoSeriesAcc(record.getGeoSeriesAcc());
//                object.setStudyId(record.getStudyId().toString());
//                object.setSpecies(species);
//                System.out.println("MAPKEY:"+ record.getGeneExpressionRecordValue().getMapKey());
//                object.setMapKey(record.getGeneExpressionRecordValue().getMapKey());
//                object.setStrainAcc(record.getSample().getStrainAccId());
//                try {
//                    if (object.getStrainAcc() != null && !object.getStrainAcc().equals(""))
//                        object.setStrainTerm(record.getSample().getStrainTerm());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                object.setTissueAcc(record.getSample().getTissueAccId());
//
//                try {
//                    if (object.getTissueAcc() != null && !object.getTissueAcc().equals(""))
//                        object.setTissueTerm(record.getSample().getTissueTerm());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                        object.setCondition(record.getGeneExpressionRecord().getExperimentCondition());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                object.setTraitOntId(record.getGeneExpressionRecord().getTraitOntId());
//
////                try {
////                    if(object.getTraitOntId()!=null && !object.getTraitOntId().equals(""))
////                        object.setTraitTerm(record.getGeneExpressionRecord().getTraitTerm());
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//                object.setExpressionLevel(record.getGeneExpressionRecordValue().getExpressionLevel());
//                object.setExpressionValue(record.getGeneExpressionRecordValue().getExpressionValue());
//                mapGene(object);
//                IndexDocument.index(object);
//
//
//            }
//
//        }
//    }
    void indexDenormalizedForExpressionTool() throws Exception {
        if(records!=null && records.size()>0) {
            //    DecimalFormat df=new DecimalFormat("#.####");
            for(GeneExpression record:records) {
                ExpressionDataIndexObject object = new ExpressionDataIndexObject();
                object.setGeoSeriesAcc(record.getGeoSeriesAcc());
                object.setStudyId(record.getStudyId().toString());
                object.setRecordId(record.getGeneExpressionRecord().getId());
                object.setSampleId(String.valueOf(record.getSample().getId()));
                object.setSpecies(species);
                object.setStrainAcc(record.getSample().getStrainAccId());
                object.setStrainTerm(record.getSample().getStrainTerm());
                object.setTissueAcc(record.getSample().getTissueAccId());
                object.setTissueTerm(record.getSample().getTissueTerm());
                object.setLifeStage(record.getSample().getLifeStage());
                object.setSex(record.getSample().getSex());
                object.setComputedSex(record.getSample().getComputedSex());
                object.setGeoSampleAcc(record.getSample().getGeoSampleAcc());
                object.setBioSampleId(record.getSample().getBioSampleId());
                object.setCondition(record.getGeneExpressionRecord().getConditionAccId());
                object.setConditionTerm(record.getGeneExpressionRecord().getExperimentCondition());
                object.setTraitOntId(record.getGeneExpressionRecord().getTraitOntId());
                object.setTraitTerm(record.getGeneExpressionRecord().getTraitTerm());
                object.setExpressionLevel(record.getGeneExpressionRecordValue().getExpressionLevel());
                object.setExpressionValue(record.getGeneExpressionRecordValue().getExpressionValue());
                object.setExpressionUnit(record.getGeneExpressionRecordValue().getExpressionUnit());
                object.setMapKey(record.getGeneExpressionRecordValue().getMapKey());

                Set<String> parentTermAccIds=new HashSet<>();
                Set<String> tissueParentTermAccIds=parentAccIds.get(record.getSample().getTissueAccId());
                if(tissueParentTermAccIds!=null && tissueParentTermAccIds.size()>0)
                    parentTermAccIds.addAll(tissueParentTermAccIds);

                Set<String> strainParentTermAccIds=parentAccIds.get(record.getSample().getStrainAccId());
                if(strainParentTermAccIds!=null && strainParentTermAccIds.size()>0)
                    parentTermAccIds.addAll(strainParentTermAccIds);
                Set<String> conditionParentTermAccIds=parentAccIds.get(record.getGeneExpressionRecord().getConditionAccId());
                if(conditionParentTermAccIds!=null && conditionParentTermAccIds.size()>0)
                    parentTermAccIds.addAll(conditionParentTermAccIds);
                object.setParentTermAccIds(parentTermAccIds);
                mapGene(object);
                IndexDocument.index(object);


            }

        }
    }
//    void indexNormalised(){
//        if(records!=null && records.size()>0) {
//            DecimalFormat df=new DecimalFormat("#.####");
//            for(String sampleId:getStrainAccIds()){
//                for(String tissueId:getTissueAccIds()){
//                    List<GeneExpression> filteredRecords=getFilteredRecords(sampleId, tissueId);
//                    if(filteredRecords.size()>0){
//                        ExpressionDataIndexObject object = new ExpressionDataIndexObject();
//                        object.setSpecies(species);
//                        object.setStrainAcc(sampleId);
//                        try {
//                            if (object.getStrainAcc() != null && !object.getStrainAcc().equals(""))
//                                object.setStrainTerm(getTerm(object.getStrainAcc()));
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        object.setTissueAcc(tissueId);
//
//                        try {
//                            if (object.getTissueAcc() != null && !object.getTissueAcc().equals(""))
//                                object.setTissueTerm(getTerm(object.getTissueAcc()));
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        List<Double> values=new ArrayList<>();
//                        Set<String> level=new HashSet<>();
//                        double valueSum=0;
//                        for (GeneExpression record:filteredRecords) {
//                            Double val=record.getGeneExpressionRecordValue().getExpressionValue();
//                            valueSum+=val;
//                            values.add(val);
//                            level.add(record.getGeneExpressionRecordValue().getExpressionLevel());
//
//                        }
//                        double valueMean= Double.parseDouble(df.format(valueSum / filteredRecords.size()));
//                        object.setExpressionLevel(level.toString());
//                        object.setExpressionValue(values.get(0));
//                        object.setValueMean(valueMean);
//                        if(valueMean>0){
//                           object.setLogValue(Math.log(valueMean));
//                        }
//                        mapGene(object);
//                        IndexDocument.index(object);
//                    }
//
//
//                }}}
//    }
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
