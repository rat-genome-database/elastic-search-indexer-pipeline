package edu.mcw.rgd.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;

import edu.mcw.rgd.indexer.model.ExpressionDataIndexObject;
import edu.mcw.rgd.indexer.model.JacksonConfiguration;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;

import java.util.List;
import java.util.stream.Collectors;

public class ExpressionDataIndexer implements Runnable{

    private Gene gene;
    private  String species;
    private List<GeneExpression> records;

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
            index();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void mapGene(ExpressionDataIndexObject object){
        object.setGeneRgdId(String.valueOf(gene.getRgdId()));
        object.setGeneSymbol(gene.getSymbol());
    }
    void setExpressionRecords()  {
        try {
            this.records= geneExpressionDAO.getGeneExpressionObjectsByRgdIdUnit(gene.getRgdId(), "TPM")
                    .stream().filter(r->
                            ( r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("high") ||
                                    r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("low") ||
                                    r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("medium") )).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void index() throws Exception {
        if(records!=null && records.size()>0) {
            for (GeneExpression record:records) {
                ExpressionDataIndexObject object = buildIndexObject(record);

                try {
                    String json = JacksonConfiguration.MAPPER.writeValueAsString(object);
                    IndexRequest request = new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON);
                    BulkIndexProcessor.bulkProcessor.add(request);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    ExpressionDataIndexObject buildIndexObject(GeneExpression record) throws Exception {
        ExpressionDataIndexObject object=new ExpressionDataIndexObject();
        mapGene(object);
        object.setStrainAcc(record.getSample().getStrainAccId());
        object.setStrainTerm(getTerm(object.getStrainAcc()));
        object.setTissueAcc(record.getSample().getTissueAccId());
        object.setTissueTerm(getTerm(object.getTissueAcc()));
        object.setExpressionValue(record.getGeneExpressionRecordValue().getExpressionValue());
        object.setExpressionLevel(record.getGeneExpressionRecordValue().getExpressionLevel());
        return object;
    }
    String getTerm(String accId) throws Exception {
        Term term = xdao.getTerm(accId);
          return term.getTerm();

    }
}
