package edu.mcw.rgd.indexer.index;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.ClinicalMeasurement;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import edu.mcw.rgd.indexer.model.JacksonConfiguration;
import edu.mcw.rgd.indexer.model.expression.ExpressionIndexObject;
import edu.mcw.rgd.services.ClientInit;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.recycler.Recycler;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IndexExpression implements Runnable{
    private Gene gene;
    List<GeneExpression> records;
    private ExpressionIndexObject object;
    GeneExpressionDAO expressionDAO=new GeneExpressionDAO();
    IndexDAO indexDAO=new IndexDAO();
    OntologyXDAO xdao=new OntologyXDAO();

    public IndexExpression(Gene gene){this.gene=gene;
    this.object=new ExpressionIndexObject();
    }
    @Override
    public void run() {
        int speciesTypeKey = gene.getSpeciesTypeKey();
        String species = SpeciesType.getCommonName(speciesTypeKey);
        boolean isSearchable=SpeciesType.isSearchable(speciesTypeKey);
        if (!isSearchable) return;

        object.setCategory("Expression");
        object.setSpecies(species);
        setExpressionRecords();
        mapGene();
        try {
            index();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        print();
    }
    public void mapGene(){
        object.setTerm_acc(String.valueOf(gene.getRgdId()));
        object.setSymbol(gene.getSymbol());
        object.setType(gene.getType());
        object.setName(gene.getName());

    }
    void setExpressionRecords()  {
        try {
            this.records= expressionDAO.getGeneExpressionObjectsByRgdIdUnit(gene.getRgdId(), "TPM");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void index() throws Exception {
        buildIndexObject();
        Gson gson=new Gson();
//        for(GeneExpression ge:records){
//            System.out.println(gson.toJson(ge));
//        }
        System.out.println(gson.toJson(object));
        try {
          String  json = JacksonConfiguration.MAPPER.writeValueAsString(object);
            IndexRequest request= new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON);
            BulkIndexProcessor.bulkProcessor.add(request);
//            IndexResponse response= ClientInit.getClient().index(request, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
    void buildIndexObject() throws Exception {
        setMap();
        setBioSampleId();
        setGeoSampleAcc();
        setExpressionUnit();
        setExpressionLevel();
        setSex();
        setLifeStage();
        setClinicalMeasurement();
        setTissueAcc();
        setTissueTerms();
        setStrainAcc();
        setStrainTerms();
        setClinicalMeasurementTerms();
    }
    void setGeoSampleAcc(){
       object.setGeoSampleAcc( records.stream().map(r->r.getSample().getGeoSampleAcc()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setBioSampleId(){
        object.setBioSampleId( records.stream().map(r->r.getSample().getBioSampleId()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setLifeStage(){
        object.setLifeStage( records.stream().map(r->r.getSample().getLifeStage()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setSex(){
        object.setSex( records.stream().map(r->r.getSample().getSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setStrainAcc(){
        object.setStrainAccId( records.stream().map(r->r.getSample().getStrainAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setStrainTerms() throws Exception {
        if(object.getStrainAccId().size()>0) {
            String[] arrayIds = object.getStrainAccId().toArray(new String[0]);
            List<Term> terms = xdao.getTermByAccId(arrayIds);
            if (terms.size() > 0)
                object.setStrainTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
        }
    }
    void setTissueAcc(){
        object.setTissueAccId( records.stream().map(r->r.getSample().getTissueAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setTissueTerms() throws Exception {
        if(object.getTissueAccId().size()>0) {
            String[] arrayIds = object.getTissueAccId().toArray(new String[0]);
            List<Term> terms = xdao.getTermByAccId(arrayIds);
            if (terms.size() > 0)
                object.setTissueTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
        }
    }
    void setExpressionLevel(){
        object.setExpressionLevel( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionLevel()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setExpressionUnit(){
        object.setExpressionUnit( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionUnit()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setMap(){
        object.setMapKey( records.stream().map(r->r.getGeneExpressionRecordValue().getMapKey()).filter(obj -> true).collect(Collectors.toSet()));

    }
    void setClinicalMeasurement(){
//        System.out.println("CMO IDS:"+records.stream().map(r->r.getGeneExpressionRecord().getClinicalMeasurementId()).collect(Collectors.toSet()));
        object.setClinicalMeasurementId( records.stream().filter(r->r.getGeneExpressionRecord().getClinicalMeasurementId()>0).map(r->String.valueOf(r.getGeneExpressionRecord().getClinicalMeasurementId())).collect(Collectors.toSet()));

    }
    void setClinicalMeasurementTerms() throws Exception {
//        if(object.getClinicalMeasurementId().size()>0) {
//            String[] arrayIds = object.getClinicalMeasurementId().toArray(new String[0]);
//            List<Term> terms = xdao.getTermByAccId(arrayIds);
//            if (terms.size() > 0)
//                object.setClinicalMeasurementTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
//        }
    }
    void print(){
        Gson gson=new Gson();
//        for(GeneExpression ge:records){
//            System.out.println(gson.toJson(ge));
//        }
        System.out.println(gson.toJson(object));
    }

}
