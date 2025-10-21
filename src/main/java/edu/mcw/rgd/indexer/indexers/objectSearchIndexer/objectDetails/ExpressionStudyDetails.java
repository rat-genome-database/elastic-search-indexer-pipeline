package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;


import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.Study;
import edu.mcw.rgd.indexer.dao.variants.VariantIndexingThread;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.*;
import java.util.stream.Collectors;

public class ExpressionStudyDetails extends ObjectDetails<Study> {
    private final List<GeneExpression> records;
    private  Set<Integer> recordIds;
    private List<List<Integer>> batches;
    GeneExpressionDAO geneExpressionDAO=new GeneExpressionDAO();
   public ExpressionStudyDetails(Study study, IndexObject object, List<GeneExpression> records) {
        super(study, object);
        this.records=records;
        this.recordIds=records.stream().map(r->r.getGeneExpressionRecord().getId()).collect(Collectors.toSet());
       try {
           this.batches=split(recordIds.stream().map(id->id).collect(Collectors.toList()), 500);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
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
        setComputedSex();
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


         ****/
        setClinicalMeasurementTerms();
       setConditionIds();
       setConditionTerms();
       setTraitTerms();
       setRGDReference();
    }

    @Override
    public  int getRgdId() {
        return 0;
    }

    @Override
    public  int getSpeciesTypeKey() {
        Set<Integer> speciesTypeKeys= records.stream().map(r->r.getGeneExpressionRecord().getSpeciesTypeKey()).collect(Collectors.toSet());
        if(speciesTypeKeys.size()>0)
            return speciesTypeKeys.stream().toList().get(0);
        return 0;
    }

    @Override
    public  void mapObject() {
        obj.setTerm_acc(String.valueOf(t.getId()));
        obj.setName(t.getName());
        obj.setSource(t.getSource());
        obj.setType(t.getType());
        if(t.getGeoSeriesAcc()!=null && !t.getGeoSeriesAcc().equals(""))
            obj.setXdbIdentifiers(Collections.singletonList(t.getGeoSeriesAcc()));
   }

    @Override
    public  void mapAnnotations() {

    }

    @Override
    public  void mapAssociations() {

    }


    @Override
    public  void index()  {

       mapObject();
       mapSpecies();
       IndexDocument.index(obj);
    }
    synchronized void setGeneSymbols() {
        try {
            Set<String> symbols =new HashSet<>();
            try {
                batches = split((List<Integer>) recordIds, 500);
                for (List<Integer> batch : batches) {
                   symbols.addAll(geneExpressionDAO.getAnnotatedObjectsByRecordIds(new HashSet<>(batch)));
                }
            } catch (Exception e) {
            }


            obj.setExpressedGeneSymbols(symbols);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
     void setGeoSampleAcc(){
       obj.setGeoSampleAcc( records.stream().map(r->r.getSample().getGeoSampleAcc()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
     void setBioSampleId(){
        obj.setBioSampleId( records.stream().map(r->r.getSample().getBioSampleId()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
     void setLifeStage(){
        obj.setLifeStage( records.stream().map(r->r.getSample().getLifeStage()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
     void setSex(){
        obj.setSex( records.stream().map(r->r.getSample().getSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
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
     void setExpressionLevel(){
      //  obj.setExpressionLevel( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionLevel()).filter(Objects::nonNull).collect(Collectors.toSet()));
         try {
             Set<String> levels =new HashSet<>();
             try {

                 for (List<Integer> batch : batches) {
                     levels.addAll(geneExpressionDAO.getExpressionLevelByRecordIds((Set<Integer>) batch));
                 }
             } catch (Exception ignored) {}


             obj.setExpressionLevel(levels);

         } catch (Exception e) {
             throw new RuntimeException(e);
         }
//         try {
//             List<String> levels=geneExpressionDAO.getExpressionLevelByRecordIds(recordIds);
//             obj.setExpressionLevel(new HashSet<>(levels));
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }

     }
     synchronized void setExpressionUnit(){
//        obj.setExpressionUnit( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionUnit()).filter(Objects::nonNull).collect(Collectors.toSet()));

//         try {
//             List<String> units=geneExpressionDAO.getExpressionUnitByRecordIds(recordIds);
//             obj.setExpressionUnit(new HashSet<>(units));
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
         try {
             Set<String> units =new HashSet<>();
             try {

                 for (List<Integer> batch : batches) {
                     units.addAll(geneExpressionDAO.getExpressionUnitByRecordIds((Set<Integer>) batch));
                 }
             } catch (Exception ignored) {}


             obj.setExpressionLevel(units);

         } catch (Exception e) {
             throw new RuntimeException(e);
         }
    }
    synchronized void setExperimentId(){
        obj.setExperimentId( records.stream().map(r->r.getGeneExpressionRecord().getExperimentId()).collect(Collectors.toSet()));

    }
     void setGEOSeriesAcc(){
       Set<String> geoSeriesAccIds=records.stream().map(GeneExpression::getGeoSeriesAcc).collect(Collectors.toSet());
       geoSeriesAccIds.add(t.getGeoSeriesAcc());
        obj.setGeoSeriesAcc( geoSeriesAccIds);

    }
     void setSample()  {
        Set<String> sampleIds=records.stream().map(r->String.valueOf(r.getSample().getGeoSampleAcc())).collect(Collectors.toSet());
        if(sampleIds.size()>0){
            obj.setSample( sampleIds);
        }


    }
     void setMap(){
        obj.setExpressionMapKey( records.stream().map(r->r.getGeneExpressionRecordValue().getMapKey()).filter(obj -> true).collect(Collectors.toSet()));

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

}
