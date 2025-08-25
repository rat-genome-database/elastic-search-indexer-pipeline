package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;


import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.Study;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ExpressionStudyDetails extends ObjectDetails<Study> {
    private final List<GeneExpression> records;
   public ExpressionStudyDetails(Study study, IndexObject object, List<GeneExpression> records) {
        super(study, object);
        this.records=records;
        if(records.size()>0) {
           setMetaData();
        }
    }
   synchronized void setMetaData(){
        setMap();
        setGeneSymbols();
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
        /**** TODO
         setExperimentId();
         setSampleId();
         setVTAcc();
         setStudySource();

         setRGDReference();
         ****/
        setClinicalMeasurementTerms();
    }

    @Override
    public synchronized int getRgdId() {
        return 0;
    }

    @Override
    public synchronized int getSpeciesTypeKey() {
        Set<Integer> speciesTypeKeys= records.stream().map(r->r.getGeneExpressionRecord().getSpeciesTypeKey()).collect(Collectors.toSet());
        if(speciesTypeKeys.size()>0)
            return speciesTypeKeys.stream().toList().get(0);
        return 0;
    }

    @Override
    public synchronized void mapObject() {
        obj.setTerm_acc(String.valueOf(t.getId()));
        obj.setName(t.getName());
        obj.setSource(t.getSource());
        obj.setType(t.getType());
        if(t.getGeoSeriesAcc()!=null && !t.getGeoSeriesAcc().equals(""))
            obj.setXdbIdentifiers(Collections.singletonList(t.getGeoSeriesAcc()));
   }

    @Override
    public synchronized void mapAnnotations() {

    }

    @Override
    public synchronized void mapAssociations() {

    }


    @Override
    public synchronized void index()  {

       mapObject();
       mapSpecies();


        IndexDocument.index(obj);
    }
    synchronized void setGeneSymbols(){
        obj.setExpressedGeneSymbols( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressedGeneSymbol()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    synchronized void setGeoSampleAcc(){
       obj.setGeoSampleAcc( records.stream().map(r->r.getSample().getGeoSampleAcc()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    synchronized void setBioSampleId(){
        obj.setBioSampleId( records.stream().map(r->r.getSample().getBioSampleId()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    synchronized void setLifeStage(){
        obj.setLifeStage( records.stream().map(r->r.getSample().getLifeStage()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    synchronized void setSex(){
        obj.setSex( records.stream().map(r->r.getSample().getSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    synchronized void setStrainAcc(){
        obj.setStrainAccId( records.stream().map(r->r.getSample().getStrainAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    synchronized void setStrainTerms() throws Exception {
        if(obj.getStrainAccId().size()>0) {
            String[] arrayIds = obj.getStrainAccId().toArray(new String[0]);
            List<Term> terms = ontologyXDAO.getTermByAccId(arrayIds);
            if (terms!=null && terms.size() > 0)
                obj.setStrainTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
        }
    }
    synchronized void setCellTypeAcc(){
        obj.setCellTypeAccId( records.stream().map(r->r.getSample().getCellTypeAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    synchronized void setCellTypeTerms() {
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
    synchronized void setTissueAcc(){
        obj.setTissueAccId( records.stream().map(r->r.getSample().getTissueAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    synchronized void setTissueTerms() {
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
    synchronized void setExpressionLevel(){
        obj.setExpressionLevel( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionLevel()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    synchronized void setExpressionUnit(){
        obj.setExpressionUnit( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionUnit()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    synchronized void setExperimentId(){
        obj.setExperimentId( records.stream().map(r->r.getGeneExpressionRecord().getExperimentId()).collect(Collectors.toSet()));

    }
    synchronized void setGEOSeriesAcc(){
       Set<String> geoSeriesAccIds=records.stream().map(GeneExpression::getGeoSeriesAcc).collect(Collectors.toSet());
       geoSeriesAccIds.add(t.getGeoSeriesAcc());
        obj.setGeoSeriesAcc( geoSeriesAccIds);

    }
    synchronized void setSample()  {
        Set<String> sampleIds=records.stream().map(r->String.valueOf(r.getSample().getGeoSampleAcc())).collect(Collectors.toSet());
        if(sampleIds.size()>0){
            obj.setSample( sampleIds);
        }


    }
    synchronized void setMap(){
        obj.setExpressionMapKey( records.stream().map(r->r.getGeneExpressionRecordValue().getMapKey()).filter(obj -> true).collect(Collectors.toSet()));

    }
    synchronized void setClinicalMeasurement(){
//        System.out.println("CMO IDS:"+records.stream().map(r->r.getGeneExpressionRecord().getClinicalMeasurementId()).collect(Collectors.toSet()));
        obj.setClinicalMeasurementId( records.stream().filter(r->r.getGeneExpressionRecord().getClinicalMeasurementId()>0).map(r->String.valueOf(r.getGeneExpressionRecord().getClinicalMeasurementId())).collect(Collectors.toSet()));

    }
    synchronized void setClinicalMeasurementTerms(){
//        if(obj.getClinicalMeasurementId().size()>0) {
//            String[] arrayIds = obj.getClinicalMeasurementId().toArray(new String[0]);
//            List<Term> terms = xdao.getTermByAccId(arrayIds);
//            if (terms.size() > 0)
//                obj.setClinicalMeasurementTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
//        }
    }


}
