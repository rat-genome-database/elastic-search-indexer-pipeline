package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ExpressionDetails extends GeneDetails{

    private final List<GeneExpression> records;
    public ExpressionDetails(Gene gene, IndexObject obj,  List<GeneExpression> records) {
        super(gene, obj);
        this.records=records;
    }

    @Override
    public void index() {
        if(isSearchable()) {
            mapObject();
            mapSpecies();
            mapAssembly();
//            mapAnnotations();
//            mapAssociations();
            mapExternalDataBaseIdentifiers();
//            mapTranscripts();
            mapSynonyms();
            setExpressionLevel();
            setExpressionUnit();
            setSex();
            setLifeStage();

            setGeoSampleAcc();
            setBioSampleId();
            setComputedSex();
            setClinicalMeasurement();
            setTissueAcc();
            setTissueTerms();
            setStrainAcc();
            try {
                setStrainTerms();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            setCellTypeAcc();
            setCellTypeTerms();
            setExperimentId();
            setGEOSeriesAcc();
            setSample();
            setClinicalMeasurementTerms();
            setConditionIds();
            setConditionTerms();
            setTraitTerms();
            setRGDReference();
            setStudySource();
            IndexDocument.index(obj);
        }
    }

    void setLifeStage(){
        obj.setLifeStage( records.stream().map(r->r.getSample().getLifeStage()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setSex(){
        obj.setSex( records.stream().map(r->r.getSample().getSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    void setExpressionLevel(){
        obj.setExpressionLevel( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionLevel()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setExpressionUnit(){
        obj.setExpressionUnit( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionUnit()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }

    void setGeoSampleAcc(){
        obj.setGeoSampleAcc( records.stream().map(r->r.getSample().getGeoSampleAcc()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setBioSampleId(){
        obj.setBioSampleId( records.stream().map(r->r.getSample().getBioSampleId()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    void setComputedSex(){
        obj.setSex( records.stream().map(r->r.getSample().getComputedSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setStrainAcc(){
        obj.setStrainAccId( records.stream().map(r->r.getSample().getStrainAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setStrainTerms() throws Exception {
        if(obj.getStrainAccId().size()>0) {
            String[] arrayIds = obj.getStrainAccId().toArray(new String[0]);
            List<Term> terms = ontologyXDAO.getTermByAccId(arrayIds);
            if (terms!=null && terms.size() > 0)
                obj.setStrainTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
        }
    }
    void setCellTypeAcc(){
        obj.setCellTypeAccId( records.stream().map(r->r.getSample().getCellTypeAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setCellTypeTerms() {
        if(obj.getCellTypeAccId().size()>0) {
            String[] arrayIds = obj.getCellTypeAccId().toArray(new String[0]);
            List<Term> terms = null;
            try {
                terms = ontologyXDAO.getTermByAccId(arrayIds);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (terms!=null && terms.size() > 0)
                obj.setCellTypeTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
        }
    }
    void setTissueAcc(){
        obj.setTissueAccId( records.stream().map(r->r.getSample().getTissueAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setTissueTerms() {
        if(obj.getTissueAccId().size()>0) {
            String[] arrayIds = obj.getTissueAccId().toArray(new String[0]);
            List<Term> terms = null;
            try {
                terms = ontologyXDAO.getTermByAccId(arrayIds);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (terms!=null && terms.size() > 0)
                obj.setTissueTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
        }
    }
    synchronized void setExperimentId(){
        obj.setExperimentId( records.stream().map(r->r.getGeneExpressionRecord().getExperimentId()).collect(Collectors.toSet()));

    }
    void setGEOSeriesAcc(){
        Set<String> geoSeriesAccIds=records.stream().map(GeneExpression::getGeoSeriesAcc).collect(Collectors.toSet());
        obj.setGeoSeriesAcc( geoSeriesAccIds);

    }
    void setSample()  {
        Set<String> sampleIds=records.stream().map(r->String.valueOf(r.getSample().getGeoSampleAcc())).collect(Collectors.toSet());
        if(sampleIds.size()>0){
            obj.setSample( sampleIds);
        }


    }
    void setClinicalMeasurement(){
//        System.out.println("CMO IDS:"+records.stream().map(r->r.getGeneExpressionRecord().getClinicalMeasurementId()).collect(Collectors.toSet()));
        obj.setClinicalMeasurementId( records.stream().filter(r->r.getGeneExpressionRecord().getClinicalMeasurementId()>0).map(r->String.valueOf(r.getGeneExpressionRecord().getClinicalMeasurementId())).collect(Collectors.toSet()));
    }
    void setClinicalMeasurementTerms(){
        if(obj.getClinicalMeasurementId().size()>0) {
            obj.setClinicalMeasurementTerms(records.stream().filter(r->r.getGeneExpressionRecord().getMeasurementTerm()!=null).map(r->r.getGeneExpressionRecord().getMeasurementTerm()).collect(Collectors.toSet()));
        }
    }
    void setConditionIds(){

        obj.setConditionAccId(records.stream().filter(r->r.getGeneExpressionRecord().getConditionAccId()!=null).map(r->r.getGeneExpressionRecord().getConditionAccId()).collect(Collectors.toSet()));

    }
    void setConditionTerms(){

        obj.setConditionTerms(records.stream().filter(r->r.getGeneExpressionRecord().getExperimentCondition()!=null).map(r->r.getGeneExpressionRecord().getExperimentCondition()).collect(Collectors.toSet()));

    }
    void setTraitTerms(){

        obj.setTraitTerms(records.stream().filter(r->r.getGeneExpressionRecord().getTraitTerm()!=null).map(r->r.getGeneExpressionRecord().getTraitTerm()).collect(Collectors.toSet()));

    }
    void setRGDReference(){

        obj.setRefRgdId(records.stream().map(GeneExpression::getRefRgdId).filter(refRgdId -> refRgdId >0).collect(Collectors.toSet()));

    }
    void setStudySource(){
        obj.setExpressionSource(records.stream().map(GeneExpression::getStudySource).collect(Collectors.toSet()));

    }
}
