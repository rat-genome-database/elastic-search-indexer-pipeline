package edu.mcw.rgd.indexer.dao.phenominer;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.Condition;
import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;
import edu.mcw.rgd.indexer.dao.phenominer.utils.PhenominerProcess;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PhenominerNormalizedThread implements Runnable {

    private String index;
    private Logger log;
    PhenominerDAO phenominerDAO = new PhenominerDAO();
    OntologyXDAO xdao = new OntologyXDAO();
    StrainDAO strainDAO=new StrainDAO();
    PhenominerProcess process=new PhenominerProcess();
    public PhenominerNormalizedThread() {
    }

    public PhenominerNormalizedThread(String index, Logger log) {
        this.index = index;
        this.log = log;
    }
    @Override
    public void run() {
        List<Record> records = new ArrayList<>();
        try {
       records = phenominerDAO.getFullRecords();
        //    records = phenominerDAO.getFullRecordsByCMO("CMO:0000709");
            System.out.println("RECORDSSIZE:"+ records.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> ontologies = new ArrayList<>(Arrays.asList("RS", "CMO", "XCO", "MMO"));
        List<PhenominerIndexObject> indexObjects = new ArrayList<>();

        for (Record record : records) {
            //      Record record=records.get(0);
     //       if (record.getClinicalMeasurement().getAccId().equalsIgnoreCase("CMO:0000783")) {
                Map<String, Set<String>> synomyms = new HashMap<>();
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
                    try {
                        // ontMaps.put(rootTerm.getOntologyId(), process.mapOntology(record,rootTerm));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        synomyms.put(rootTerm.getOntologyId(), process.mapSynonyms(record, rootTerm));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                PhenominerIndexObject object = new PhenominerIndexObject();
                object.setRecordId(record.getId());
                object.setRsTermAcc(record.getSample().getStrainAccId());
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
                try {
                    object.setXcoTermAcc(xcoAccId.stream().collect(Collectors.toList()));
                 /*   if(xcoTerm.size()==0 || (xcoTerm.size()==1 && xcoTerm.iterator().next().equals(""))){
                        object.setXcoTerm("No condition");
                    }else*/
                 //   object.setXcoTerm(xcoTerm.stream().collect(Collectors.joining(" and ")));
                    if(record.getConditionDescription().equals("")){
                        object.setXcoTerm("No Condition");
                    }else
                    object.setXcoTerm(record.getConditionDescription());
                }catch (Exception e){System.err.println("No XCO term " + record.getId());}

                Set<String> cmoSynonyms = new HashSet<>(synomyms.get("CMO"));
                Set<String> mmoSynonyms = new HashSet<>(synomyms.get("MMO"));
                Set<String> rsSynonyms = new HashSet<>(synomyms.get("RS"));
                String strainRgdId=new String();
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
                    System.out.println("NO STRAIN RGD ID:"+ record.getSample().getStrainAccId());
                System.out.println("RGD ID:"+ rgdId);
            Set<String> xcoSynonyms = new HashSet<>(synomyms.get("XCO"));
                object.setCmoTerms(new ArrayList<>(cmoSynonyms));
                object.setMmoTerms(new ArrayList<>(mmoSynonyms));
                object.setRsTerms(new ArrayList<>(rsSynonyms));
                object.setXcoTerms(new ArrayList<>(xcoSynonyms));
                try {
                    object.setSex(record.getSample().getSex());
                }catch (Exception e){System.err.println("No sex for record "+ record.getId());}
                try {
                    object.setAgeLowBound(record.getSample().getAgeDaysFromLowBound());
                }catch (Exception e){System.err.println("No age low bound for record "+ record.getId());}
                try {
                    object.setAgeHighBound(record.getSample().getAgeDaysFromHighBound());
                }catch (Exception e){System.err.println("NO age high bound for record "+ record.getId());}
                try {
                    object.setUnits(record.getMeasurementUnits());
                } catch (Exception e){System.out.println("No measurement units for record "+ record.getId());}
                try {
                    double roundOffSD = Math.round(Double.parseDouble(record.getMeasurementSD()) * 100.0) / 100.0;
                    object.setSd(String.valueOf(roundOffSD));

                }catch (Exception e){System.err.println("No SD for record "+ record.getId());}
                try {
                    double roundOffSem = Math.round(Double.parseDouble(record.getMeasurementSem()) * 100.0) / 100.0;
                    object.setSem(String.valueOf(roundOffSem));
                }catch (Exception e){}
                try {
                    object.setAverageType(record.getClinicalMeasurement().getAverageType());
                }catch (Exception e){}
                try {
                    double roundOffValue = Math.round(Double.parseDouble(record.getMeasurementValue()) * 100.0) / 100.0;
                    object.setValue(String.valueOf(roundOffValue));
                }catch (Exception e){}
                try {
                    object.setStudyId(record.getStudyId());
                }catch (Exception e){}
                try {
                    object.setStudy(record.getStudyName());
                }catch ( Exception e){}
                try {
                    object.setSampleNotes(record.getSample().getNotes());
                }catch (Exception e){}
                if(record.getMeasurementMethod()!=null) {
                    try {
                        object.setPostInsultType(record.getMeasurementMethod().getPiType());
                        object.setPostInsultTimeValue(record.getMeasurementMethod().getPiTimeValue());
                        object.setPostInsultTimeUnit(record.getMeasurementMethod().getPiTypeUnit());
                    }catch (Exception e){
                        System.err.println("No measurement method PI for RECORD ID:"+ record.getId());
                    }
                }
                try {
                    object.setNumberOfAnimals(record.getSample().getNumberOfAnimals());
                }catch ( Exception e){}
                try {
                    object.setMethodSite(record.getMeasurementMethod().getSite());
                }catch (Exception e){}
                try {
                    object.setMethodNotes(record.getMeasurementMethod().getNotes());
                }catch (Exception e){}
                try {
                    object.setMethodDuration(record.getMeasurementMethod().getDuration());
                }catch ( Exception e){}
                try {
                    object.setFormula(record.getClinicalMeasurement().getFormula());
                }catch (Exception e){}
                try {
                    object.setExperimentNotes(record.getExperimentNotes());
                }catch (Exception e){}
                try {
                    object.setExperimentName(record.getExperimentName().trim());
                }catch (Exception e){}
                try {
                    object.setClinicalMeasurementNotes(record.getClinicalMeasurement().getNotes());
                }catch (Exception e){}
            try {
                object.setRefRgdId(record.getRefRgdId());
            }catch (Exception e){}
                indexObjects.add(object);

         //   }

    }
        if (indexObjects.size() > 0) {
            try {
                process.indexObjects(indexObjects, index, "");
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
