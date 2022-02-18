package edu.mcw.rgd.indexer.dao.phenominer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.dao.spring.StringMapQuery;
import edu.mcw.rgd.datamodel.ontologyx.Relation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.datamodel.pheno.Condition;
import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;
import edu.mcw.rgd.indexer.model.IndexObject;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PhenominerThread implements Runnable  {
    private String index;
    private Logger log;
    PhenominerDAO phenominerDAO=new PhenominerDAO();
    OntologyXDAO xdao=new OntologyXDAO();
    StrainDAO strainDAO=new StrainDAO();
    AssociationDAO associationDAO=new AssociationDAO();
    IndexDAO indexDAO=new IndexDAO();
    public PhenominerThread(String index,Logger log){
        this.index=index;
        this.log=log;
    }
   @Override
    public void run() {
        try {
     //     List<Record> records= phenominerDAO.getFullRecords();
            List<PhenominerIndexObject> indexObjects=new ArrayList<>();
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
          String rsRootTermAcc=  xdao.getRootTerm("RS");
          Term rsRootTerm=xdao.getTerm(rsRootTermAcc);
          String rsRootTermName=rsRootTerm.getTerm();
     //   for(Record record:records){
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

             indexObjects.addAll( mapRS(new Record(),  rsRootTerm,20));
        //  }
            indexObjects(indexObjects,index,"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<PhenominerIndexObject> mapRS(Record record, Term rsRootTerm, int maxAncestorDepth) throws Exception {
    //   Term rsTerm= xdao.getTerm(record.getSample().getStrainAccId());
       String rsTermAcc="RS:0001782";
    //    String rsTermAcc=rsTerm.getAccId();
        String rootTerm= rsRootTerm.getTerm();

        List<StringMapQuery.MapPair> rsTopLevelTerms= xdao.getTopLevelTerms(rsTermAcc);
        List<PhenominerIndexObject> indexObjects=new ArrayList<>();

        if(rsTopLevelTerms.size()>0){

                Map<String, String > hierarchyMap=new HashMap<>();
                getParentHierarchy(rsTermAcc, maxAncestorDepth,hierarchyMap);
                System.out.println(hierarchyMap);


            for(StringMapQuery.MapPair topLevelPair: rsTopLevelTerms) {
                PhenominerIndexObject obj = new PhenominerIndexObject();
                obj.setRecordId(record.getId());
                obj.setRsRootTermAcc(rsRootTerm.getAccId());
                obj.setRsRootTerm(rootTerm);
                obj.setRsTermAcc(rsTermAcc);
                obj.setRsTerm(xdao.getTerm(rsTermAcc).getTerm());
                obj.setRsTopLevelTerm(topLevelPair.stringValue);
                obj.setRsTopLevelTermAcc(topLevelPair.keyValue);
                obj.setHierarchyMap(hierarchyMap);
                indexObjects.add(obj);
            }
        }

        return indexObjects;

    }
    public void getParentHierarchy(String termAcc, int maxAncestorDepth,Map<String, String > hierarchyMap) throws Exception {
        List<Term> ancestors=xdao.getAllActiveTermAncestors(termAcc);
        List<Term> terms= xdao.getParentTerm(termAcc);
        int i=(maxAncestorDepth)-(ancestors.size());
       /* if(terms.size()>1){
            i=i+terms.size();
        }*/
        for(Term term:terms){
            hierarchyMap.put("level"+i, term.getTerm());
            getParentHierarchy(term.getAccId(),maxAncestorDepth, hierarchyMap);
        }
    }
    public boolean isInTopLevelTerms(Map<String, String> topLevelTerms, Term pt){
        for(Map.Entry topTerm:topLevelTerms.entrySet()){
            if(pt.getTerm().equalsIgnoreCase(topTerm.getValue().toString())){
                return true;
            }
        }
        return false;
    }


    public void indexObjects(List<PhenominerIndexObject> objs, String index, String type) throws ExecutionException, InterruptedException, IOException {
        // BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkRequest bulkRequest=new BulkRequest();
        //  bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        bulkRequest.timeout(TimeValue.timeValueMinutes(2));
        bulkRequest.timeout("2m");
        int docCount=0;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        for (PhenominerIndexObject o : objs) {
            docCount++;

            byte[] json = new byte[0];
            try {
                json = mapper.writeValueAsBytes(o);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            //     bulkRequestBuilder.add(new IndexRequest(index, type,o.getTerm_acc()).source(json, XContentType.JSON));
            bulkRequest.add(new IndexRequest(index).source(json, XContentType.JSON));
            if(docCount%100==0){
                ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                bulkRequest= new BulkRequest();
                bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                bulkRequest.timeout(TimeValue.timeValueMinutes(2));
                bulkRequest.timeout("2m");
            }else{
                if(docCount>objs.size()-100 && docCount==objs.size()){

                    ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    bulkRequest= new BulkRequest();
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                    bulkRequest.timeout(TimeValue.timeValueMinutes(2));
                    bulkRequest.timeout("2m");
                }
            }
        }
        //   ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);

        //   BulkResponse response=       bulkRequestBuilder.get();

        //  ESClient.getClient().admin().indices().refresh(refreshRequest()).actionGet();
        RefreshRequest refreshRequest=new RefreshRequest();
        ESClient.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
    }
}
