package edu.mcw.rgd.indexer.dao.phenominer;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.Condition;
import edu.mcw.rgd.datamodel.pheno.IndividualRecord;
import edu.mcw.rgd.datamodel.pheno.PhenominerUnitTable;
import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;
import edu.mcw.rgd.indexer.dao.phenominer.utils.PhenominerProcess;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import edu.mcw.rgd.process.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PhenominerNormalizedThread implements Runnable {

    private final Logger log = LogManager.getLogger("phenominer");

    private String index;
    private BulkIndexProcessor bulkIndexProcessor;
    PhenominerDAO phenominerDAO = new PhenominerDAO();
    OntologyXDAO xdao = new OntologyXDAO();
    StrainDAO strainDAO=new StrainDAO();
    PhenominerProcess process=new PhenominerProcess();
    public PhenominerNormalizedThread() {
    }

    public PhenominerNormalizedThread(String index, BulkIndexProcessor bulkIndexProcessor) {
        this.index = index;
        this.bulkIndexProcessor=bulkIndexProcessor;
    }
    @Override
    public void run() {
        List<Record> records = new ArrayList<>();
        try {
            records.addAll(phenominerDAO.getFullRecords());
            records.addAll(phenominerDAO.getChincillaFullRecords());
        //    records = phenominerDAO.getFullRecordsByCMO("CMO:0000709");
            log.info("RECORDSSIZE:"+ records.size());
        } catch (Exception e) {
            Utils.printStackTrace(e, log);
        }
        List<String> ontologies = new ArrayList<>(Arrays.asList("RS", "CMO", "XCO", "MMO","VT"));
        List<PhenominerIndexObject> indexObjects = new ArrayList<>();

        for (Record record : records) {
       //     if(record.getId()==17881){
            //      Record record=records.get(0);
     //       if (record.getClinicalMeasurement().getAccId().equalsIgnoreCase("CMO:0000783")) {
                Map<String, Set<String>> synonyms = new HashMap<>();
                for (String ontology : ontologies) {
                    String rootTermAcc = null;
                    try {
                        rootTermAcc = xdao.getRootTerm(ontology);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Term rootTerm = new Term();
                    try {
                        rootTerm = xdao.getTerm(rootTermAcc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    try {
//                        // ontMaps.put(rootTerm.getOntologyId(), process.mapOntology(record,rootTerm));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    try {
                        synonyms.put(rootTerm.getOntologyId(), process.mapSynonyms(record, rootTerm));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                PhenominerIndexObject object = new PhenominerIndexObject();
                object.setRecordId(record.getId());
                object.setRsTermAcc(record.getSample().getStrainAccId());
                if(object.getRsTermAcc().contains("CS")){
                    object.setSpeciesTypeKey(4);
                    object.setSpecies("Chinchilla");
                }else{
                    object.setSpeciesTypeKey(3);
                    object.setSpecies("Rat");
                }
                try {
                    object.setRsTerm(xdao.getTerm(record.getSample().getStrainAccId()).getTerm());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                object.setCmoTermAcc(record.getClinicalMeasurement().getAccId());
                try {
                    String cmoTerm=xdao.getTerm(record.getClinicalMeasurement().getAccId()).getTerm();
                    object.setCmoTerm(cmoTerm);
                    if(record.getMeasurementUnits()!=null){
                        object.setCmoTermWithUnits(cmoTerm +" ("+record.getMeasurementUnits()+")");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                object.setMmoTermAcc(record.getMeasurementMethod().getAccId());
                try {
                    object.setMmoTerm(xdao.getTerm(record.getMeasurementMethod().getAccId()).getTerm());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                object.setVtTermAcc(record.getTraitId());
            try {
                object.setVtTerm(xdao.getTerm(record.getTraitId()).getTerm());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(record.getTrait3Id()!=null) {
                object.setVtTerm3Acc(record.getTrait3Id());

                try {
                    object.setVtTerm3(xdao.getTerm(record.getTrait3Id()).getTerm());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(record.getTrait2Id()!=null) {
                object.setVtTerm2Acc(record.getTrait2Id());
                try {
                    object.setVtTerm2(xdao.getTerm(record.getTrait2Id()).getTerm());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                Set<String> xcoAccId = new HashSet<>();
                Set<String> xcoTerm = new HashSet<>();

                for (Condition condition : record.getConditions()) {
                    xcoAccId.add(condition.getOntologyId());
                    try {
                        xcoTerm.add(xdao.getTerm(condition.getOntologyId()).getTerm());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                object.setXcoTerm(xcoTerm);
                try {
                    object.setXcoTermAcc(xcoAccId.stream().collect(Collectors.toList()));
                 /*   if(xcoTerm.size()==0 || (xcoTerm.size()==1 && xcoTerm.iterator().next().equals(""))){
                        object.setXcoTerm("No condition");
                    }else*/
                 //   object.setXcoTerm(xcoTerm.stream().collect(Collectors.joining(" and ")));
                    if(record.getConditionDescription().equals("")){
                        object.setXcoConditionDescription("No Condition");
                    }else
                    object.setXcoConditionDescription(record.getConditionDescription());
                }catch (Exception e){System.err.println("No XCO term " + record.getId());}

                Set<String> cmoSynonyms = new HashSet<>(synonyms.get("CMO"));
                Set<String> mmoSynonyms = new HashSet<>(synonyms.get("MMO"));
                Set<String> rsSynonyms = new HashSet<>(synonyms.get("RS"));
            Set<String> vtSynonyms = new HashSet<>(synonyms.get("VT"));
                String strainRgdId="";
                for(String rsSynonym:rsSynonyms){
                    if(rsSynonym.contains("RGD")){
                        strainRgdId=rsSynonym;
                    }
                }
                int rgdId=0;
                if(!strainRgdId.equals("")){
                    rgdId=Integer.parseInt(strainRgdId.substring(strainRgdId.indexOf(":")+1).trim());
                    if(rgdId>0){
                        try {
                          Strain strain=  strainDAO.getStrain(rgdId);
                          object.setRsTopLevelTerm(strain.getStrain());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    log.debug("NO STRAIN RGD ID:"+ record.getSample().getStrainAccId());
                    log.debug("RGD ID:"+ rgdId);
            Set<String> xcoSynonyms = new HashSet<>(synonyms.get("XCO"));
                object.setCmoTerms(new ArrayList<>(cmoSynonyms));
                object.setMmoTerms(new ArrayList<>(mmoSynonyms));
                object.setRsTerms(new ArrayList<>(rsSynonyms));
                object.setXcoTerms(new ArrayList<>(xcoSynonyms));
            object.setVtTerms(new ArrayList<>(vtSynonyms));

                try {
                    object.setSex(record.getSample().getSex());
                }catch (Exception e){log.debug("No sex for record "+ record.getId());}

                try {
                    object.setAgeLowBound(record.getSample().getAgeDaysFromLowBound());
                }catch (Exception e){log.debug("No age low bound for record "+ record.getId());}

                try {
                    object.setAgeHighBound(record.getSample().getAgeDaysFromHighBound());
                }catch (Exception e){log.debug("NO age high bound for record "+ record.getId());}

                try {
                    object.setUnits(record.getMeasurementUnits());
                } catch (Exception e){
                    log.debug("No measurement units for record "+ record.getId());
                }

                try {
                    double roundOffSD = Math.round(Double.parseDouble(record.getMeasurementSD()) * 100.0) / 100.0;
                    object.setSd(String.valueOf(roundOffSD));
                }catch (Exception e){
                    log.debug("No SD for record "+ record.getId());
                }

                try {
                    double roundOffSem = Math.round(Double.parseDouble(record.getMeasurementSem()) * 100.0) / 100.0;
                    object.setSem(String.valueOf(roundOffSem));
                }catch (Exception ignored){}
                try {
                    object.setAverageType(record.getClinicalMeasurement().getAverageType());
                }catch (Exception ignored){}
                try {
                    double roundOffValue = Math.round(Double.parseDouble(record.getMeasurementValue()) * 100.0) / 100.0;
                    object.setValue(String.valueOf(roundOffValue));
                }catch (Exception ignored){}
                try {
                    object.setStudyId(record.getStudyId());
                }catch (Exception ignored){}
                try {
                    object.setStudy(record.getStudyName());
                }catch ( Exception ignored){}
                try {
                    object.setSampleNotes(record.getSample().getNotes());
                }catch (Exception ignored){}
                if(record.getMeasurementMethod()!=null) {
                    try {
                        object.setPostInsultType(record.getMeasurementMethod().getPiType());
                        object.setPostInsultTimeValue(record.getMeasurementMethod().getPiTimeValue());
                        object.setPostInsultTimeUnit(record.getMeasurementMethod().getPiTypeUnit());
                    }catch (Exception e){
                        log.debug("No measurement method PI for RECORD ID:"+ record.getId());
                    }
                }
                try {
                    object.setNumberOfAnimals(record.getSample().getNumberOfAnimals());
                }catch ( Exception ignored){}
                try {
                    object.setMethodSite(record.getMeasurementMethod().getSite());
                }catch (Exception ignored){}
                try {
                    object.setMethodNotes(record.getMeasurementMethod().getNotes());
                }catch (Exception ignored){}
                try {
                    object.setMethodDuration(record.getMeasurementMethod().getDuration());
                }catch ( Exception ignored){}
                try {
                    object.setFormula(record.getClinicalMeasurement().getFormula());
                }catch (Exception ignored){}
                try {
                    object.setExperimentNotes(record.getExperimentNotes());
                }catch (Exception ignored){}
                try {
                    object.setExperimentName(record.getExperimentName().trim());
                }catch (Exception ignored){}
                try {
                    object.setClinicalMeasurementNotes(record.getClinicalMeasurement().getNotes());
                }catch (Exception ignored){}
            try {
                object.setRefRgdId(record.getRefRgdId());
            }catch (Exception e){e.printStackTrace();}
            try{
               if( record.getHasIndividualRecord()){
                 //  System.out.print("RECORD ID:"+record.getId());
                  List<PhenominerUnitTable> unitTables= phenominerDAO.getConversionFactorToStandardUnits(record.getId());

                  List<IndividualRecord> individualRecordsTmp= phenominerDAO.getIndividualRecords(record.getId());
                    List<IndividualRecord> individualRecords=new ArrayList<>();
                   if(unitTables!=null && unitTables.size()>0){
                       PhenominerUnitTable unitTable=unitTables.get(0);
                       for(IndividualRecord individualRecord:individualRecordsTmp){
                         float indiVal=  Float.parseFloat(individualRecord.getMeasurementValue());
                          float conversionFactor= unitTable.getTermSpecificScale();
                          float val=indiVal*conversionFactor;
                          individualRecord.setMeasurementValue(String.valueOf(val));
                          individualRecords.add(individualRecord);
                       }
                   }

                 // System.out.print("\t"+ individualRecords.size());
                  object.setIndividualRecords(individualRecords);
               }

            }catch (Exception e){e.printStackTrace();}
                indexObjects.add(object);
        //    break;
       //    }

    }
        if (indexObjects.size() > 0) {
            try {
                process.indexObject(indexObjects, index);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
