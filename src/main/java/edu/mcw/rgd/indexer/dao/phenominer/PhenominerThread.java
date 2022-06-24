package edu.mcw.rgd.indexer.dao.phenominer;


import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;

import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.Record;

import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;
import edu.mcw.rgd.indexer.dao.phenominer.utils.PhenominerProcess;

import org.apache.log4j.Logger;

import java.util.*;


public class PhenominerThread implements Runnable {
    private String index;
    private Logger log;
    PhenominerDAO phenominerDAO = new PhenominerDAO();
    OntologyXDAO xdao = new OntologyXDAO();
    StrainDAO strainDAO = new StrainDAO();
    AssociationDAO associationDAO = new AssociationDAO();
    IndexDAO indexDAO = new IndexDAO();
    PhenominerProcess process=new PhenominerProcess();
    public PhenominerThread() {
    }

    public PhenominerThread(String index, Logger log) {
        this.index = index;
        this.log = log;
    }

    @Override
    public void run() {
        try {
            List<Record> records= phenominerDAO.getFullRecords();
            List<String> ontologies=new ArrayList<>(Arrays.asList("RS", "CMO","XCO","MMO"));
        /*    Set<Integer> depth=new HashSet<>();
            List<Integer> depthList=new ArrayList<>();
            for(Record record:records){
                List<Term> ancestors=xdao.getAllActiveTermAncestors(record.getSample().getStrainAccId());
                depth.add(ancestors.size());
                depthList.add(ancestors.size());
            }
            System.out.println("MAX DEPTH:"+ Collections.max(depth));
            for(int d:depthList)
            System.out.println(d);*/
         /*   String rsRootTermAcc = xdao.getRootTerm("RS");
            Term rsRootTerm = xdao.getTerm(rsRootTermAcc);
            String cmoRootTermAcc=xdao.getRootTerm("CMO");
            Term cmoRootTerm=xdao.getTerm(cmoRootTermAcc);*/
            List<PhenominerIndexObject> indexObjects = new ArrayList<>();

         for(Record record:records){
        //    if(records.size()>0){
          //   Record record=records.get(0);
               /* Term cmoTerm=xdao.getTerm(record.getClinicalMeasurement().getAccId());
                String cmoRootTerm =xdao.getRootTerm("CMO");
               List<StringMapQuery.MapPair> cmoTopLevelMap= xdao.getTopLevelTerms(cmoTerm.getAccId());

                Term mmoTerm=xdao.getTerm(record.getMeasurementMethod().getAccId());
                String mmoRootTerm =xdao.getRootTerm("MMO");
                List<Term> conditions=new ArrayList<>();

                String xcoRootTerm =xdao.getRootTerm("XCO");
              for(Condition condition: record.getConditions()){
                   Term conditionTerm=xdao.getTerm(condition.getOntologyId());
                   conditions.add(conditionTerm);
               }*/

                //   mapRS(new Record(), rsRootTerm, 20, indexObjects);
                Map<String, List<LinkedHashMap<String, String>>> ontMaps=new HashMap<>();
                Map<String, Set<String>> synomyms=new HashMap<>();
                for(String ontology:ontologies){
                    String rootTermAcc=xdao.getRootTerm(ontology);
                    Term rootTerm=xdao.getTerm(rootTermAcc);
                    ontMaps.put(rootTerm.getOntologyId(),process. mapOntology(record,rootTerm));
                    synomyms.put(rootTerm.getOntologyId(),process. mapSynonyms(record,rootTerm));
                }
                for(LinkedHashMap<String, String> cmoMap:ontMaps.get("CMO")){
                    for(LinkedHashMap<String, String> rsMap:ontMaps.get("RS")){
                        for(LinkedHashMap<String, String> mmoMap:ontMaps.get("MMO")) {
                            for (LinkedHashMap<String, String> xcoMap : ontMaps.get("XCO")) {
                                PhenominerIndexObject object = new PhenominerIndexObject();
                                object.setRecordId(record.getId());
                                Set<String> cmoSynonyms = new HashSet<>();
                                Set<String> mmoSynonyms = new HashSet<>();
                                Set<String> rsSynonyms = new HashSet<>();
                                Set<String> xcoSynonyms = new HashSet<>();
                                object.setCmoHierarchyMap(cmoMap);

                                cmoSynonyms.addAll(cmoMap.keySet());
                                cmoSynonyms.addAll(synomyms.get("CMO"));

                                mmoSynonyms.addAll(mmoMap.keySet());
                                mmoSynonyms.addAll(synomyms.get("MMO"));

                                rsSynonyms.addAll(rsMap.keySet());
                                rsSynonyms.addAll(synomyms.get("RS"));

                                xcoSynonyms.addAll(xcoMap.keySet());
                                xcoSynonyms.addAll(synomyms.get("XCO"));

                                object.setCmoTerms(new ArrayList<>(cmoSynonyms));
                                object.setMmoHierarchyMap(mmoMap);
                                object.setMmoTerms(new ArrayList<>(mmoSynonyms));

                                object.setRsHierarchyMap(rsMap);
                                object.setRsTerms(new ArrayList<>(rsSynonyms));

                                object.setXcoHierarchyMap(xcoMap);
                                object.setXcoTerms(new ArrayList<>(xcoSynonyms));

                                indexObjects.add(object);
                            }
                        }
                    }
                }
            }
            System.out.println("INDEX OBJeCTS SIZE:" + indexObjects.size());

            if (indexObjects.size() > 0)
                process.indexObjects(indexObjects, index, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
