package edu.mcw.rgd.indexer.dao.phenominer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.dao.spring.StringMapQuery;

import edu.mcw.rgd.datamodel.ontologyx.Term;

import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;

import edu.mcw.rgd.indexer.dao.phenominer.model.TreeNode;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.model.RgdIndex;
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
import java.util.stream.Collectors;

public class PhenominerThread implements Runnable  {
    private String index;
    private Logger log;
    PhenominerDAO phenominerDAO=new PhenominerDAO();
    OntologyXDAO xdao=new OntologyXDAO();
    StrainDAO strainDAO=new StrainDAO();
    AssociationDAO associationDAO=new AssociationDAO();
    IndexDAO indexDAO=new IndexDAO();
    public PhenominerThread(){}
    public PhenominerThread(String index,Logger log){
        this.index=index;
        this.log=log;
    }

   @Override
    public void run() {
        try {
     //     List<Record> records= phenominerDAO.getFullRecords();

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
            List<PhenominerIndexObject> indexObjects=new ArrayList<>();

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

              mapRS(new Record(),  rsRootTerm,20,indexObjects);
        //  }
            System.out.println("INDEX OBJeCTS SIZE:"+ indexObjects.size());

            indexObjects(indexObjects,index,"");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void mapRS(Record record, Term rsRootTerm, int maxAncestorDepth,    List<PhenominerIndexObject> indexObjects) throws Exception {
    //   Term rsTerm= xdao.getTerm(record.getSample().getStrainAccId());
       String rsTermAcc="RS:0001782";
    //    String rsTermAcc=rsTerm.getAccId();
        String rootTerm= rsRootTerm.getTerm();

        List<StringMapQuery.MapPair> rsTopLevelTerms= xdao.getTopLevelTerms(rsTermAcc);

        Map<Integer, List<Term>> parentTerms = new HashMap<>();
        getParentTerms(rsTermAcc, parentTerms);
        Map<String, String> hierarchyMap = new HashMap<>();
        if(rsTopLevelTerms.size()>0) {
            for (StringMapQuery.MapPair topLevelPair : rsTopLevelTerms) {

                List<Map<String, String>> maps = new ArrayList<>();

                int maxKey = Collections.max(parentTerms.keySet());
                int maxParentDepth = 17;
                int i = maxParentDepth - maxKey;
                for (Map.Entry e : parentTerms.entrySet()) {
                    int key = (int) e.getKey();
                    List<Term> terms = (List<Term>) e.getValue();
                    if (key == maxKey) {
                        if (terms.size() > 1) {
                            boolean first = true;
                            for (Term term : terms) {
                                if (first) {
                                    hierarchyMap.put("level" + i, term.getTerm());
                                    maps.add(hierarchyMap);
                                    first = false;
                                } else {
                                    Map<String, String> hierarchyMap1 = new HashMap<>();
                                    hierarchyMap1.put("level" + i, term.getTerm());

                                    maps.add(hierarchyMap1);

                                }


                            }
                        } else {
                            hierarchyMap.put("level" + i, terms.get(0).getTerm());
                            maps.add(hierarchyMap);
                        }
                    }

                    for (Map<String, String> map : maps) {
                        PhenominerIndexObject obj = new PhenominerIndexObject();
                        obj.setRecordId(0);
                        obj.setRsRootTermAcc(rsRootTerm.getAccId());
                        obj.setRsRootTerm(rsRootTerm.getTerm());
                        obj.setRsTermAcc(rsTermAcc);
                        obj.setRsTerm(xdao.getTerm(rsTermAcc).getTerm());
                        obj.setRsTopLevelTerm(topLevelPair.stringValue);
                        obj.setRsTopLevelTermAcc(topLevelPair.keyValue);
                        obj.setHierarchyMap(map);
                        indexObjects.add(obj);

                        //  }
                    }
                    for (Term term : terms) {
                        System.out.println(e.getKey() + "\t=>" + term.getTerm());
                    }
                    i++;
                }
            }
        }

    }
    public  TreeNode getParentHierarchy(String termAcc, int maxAncestorDepth,LinkedHashMap< String, Integer > hierarchyMap) throws Exception {
        List<Term> terms= xdao.getParentTerm(termAcc);
        TreeNode rootNode=new TreeNode(xdao.getTerm(termAcc));
        for(Term t: terms){
            TreeNode parentNode=getParentHierarchy(t.getAccId(),0,null);
            rootNode.getParents().add(parentNode);
        }


        return rootNode;
    }
    public  void getParentTerms(String termAcc,Map<Integer,List<Term>> parentTerms) throws Exception {
        List<Term> terms= xdao.getParentTerm(termAcc);
        if(terms.size()>0) {
            int count = 1;
            count = count + parentTerms.size();
            parentTerms.put(count, terms);
           if(terms.size()==1)
            for (Term term : terms) {
                PhenominerIndexObject object = new PhenominerIndexObject();
                object.setRsTerm(term.getTerm());
                getParentTerms(term.getAccId(), parentTerms);
            }
        }

    }
    public static void main(String[] args) throws Exception {
        OntologyXDAO xdao=new OntologyXDAO();
        PhenominerThread t=new PhenominerThread();
        List<PhenominerIndexObject> indexObjects=new ArrayList<>();
        String rsTermAcc="RS:0001782";
        String rsTerm=xdao.getTerm(rsTermAcc).getTerm();
        String rsRootTermAcc=xdao.getRootTerm("RS");
        String rsRootTerm=xdao.getTerm(rsRootTermAcc).getTerm();
   //     List<StringMapQuery.MapPair> rsTopLevelTerms=xdao.getTopLevelTerms(rsTermAcc);

    //    for(StringMapQuery.MapPair topLevelPair: rsTopLevelTerms) { }
        LinkedHashMap<String, List<String>> map=new LinkedHashMap<>();
       TreeNode rootNode= t.getParentHierarchy(rsTermAcc,0, null);
        Queue<TreeNode> queue = new LinkedList<>();
        Stack<TreeNode> stack=new Stack<>();
        queue.offer(rootNode);
        while(!queue.isEmpty()){
            int len = queue.size();
            for(int i=0;i<len;i++) { // so that we can reach each level
                TreeNode node = queue.poll();
                stack.push(node);
                System.out.print(node.getTerm().getTerm() + " ");
                for (TreeNode item : node.getParents()) { // for-Each loop to iterate over all childrens
                    queue.offer(item);
                }
            }
            System.out.println();
        }
        System.out.println("************STAC**************:"+ stack.size());
        while(!stack.empty()){
            TreeNode node=stack.pop();
            List<String> terms=new ArrayList<>();
            if(map.keySet().contains( node.getTerm().getAccId())){
                terms.addAll(map.get(node.getTerm().getAccId()));
            }
            terms.addAll(node.getParents().stream().map(n->n.getTerm().getAccId()).collect(Collectors.toList()));

            map.put(node.getTerm().getAccId(), terms);
           System.out.print(node.getTerm().getTerm()+"\t");
            System.out.println();
        }
        Gson gson=new Gson();
        String mapStr=gson.toJson(map);
        LinkedHashMap<String, Set<String>> parentChildMap=new LinkedHashMap<>();
        for(Map.Entry e:map.entrySet()){
            String rootKey= (String) e.getKey();
            Set<String> childAccIds=new HashSet<>();
            for(Map.Entry entry:map.entrySet()){
                List<String> values= (List<String>) entry.getValue();
                if(values.contains(rootKey)){
                    childAccIds.add((String) entry.getKey());
                }
            }
            parentChildMap.put(rootKey, childAccIds);
        }

        List<PhenominerIndexObject> objects1=new ArrayList<>();
        List<String> keys=new ArrayList<>();
        List<LinkedHashMap<String, String>> maps=new ArrayList<>();
        LinkedHashMap<Integer, Set<String>> hierarchyMap=new LinkedHashMap<>();

        hierarchyMap.put(1,new HashSet<>(Arrays.asList(rsRootTermAcc)));
        t.addChildren(hierarchyMap,rsRootTermAcc, parentChildMap);
        LinkedHashMap<Integer,Map<String, Set<String>>> hierarchyMap1=new LinkedHashMap<>();
        Map<String, Set<String>> map1=new HashMap<>();
        map1.put("",new HashSet<>(Arrays.asList(rsRootTermAcc)));
        hierarchyMap1.put(1,map1);
        t.addChildren1(hierarchyMap1,rsRootTermAcc,parentChildMap);
        List< LinkedHashMap<String, String>> hmaps=new ArrayList<>();
        LinkedHashMap<String, String> hMap=new LinkedHashMap<>();
        hMap.put(rsRootTermAcc,"");
        hmaps.add(hMap);
        Set<Integer> keySet= (Set<Integer>) hierarchyMap1.keySet();


            for(int key:keySet){

                for(Map.Entry e:hierarchyMap1.get(key).entrySet()) {
                    String parent = (String) e.getKey();

                    if (!parent.equals("")) {
                        Set<String> children = (Set<String>) e.getValue();
                        LinkedHashMap<String, String> map2 = new LinkedHashMap<>();
                        List<Map<String, String>> tmpMap=new ArrayList<>();
                        //   Iterator hmapsIterator=hmaps.iterator();
                     //   for(Map<String, String> map3:hmaps)
                     //   for(int i=0;i<hmaps.size();i++){
                        for(int i=0;i<hmaps.size();i++){
                            LinkedHashMap<String, String> map3= hmaps.get(i);
                           for(Map.Entry entry:map3.entrySet()){
                               String k= (String) entry.getKey();
                               if(k.equalsIgnoreCase(parent)){
                                   map2=map3;
                                   tmpMap.add(map3);
                                   break;
                               }
                           }


                        }

                        if(map2.size()>0)
                        for (String child : children) {
                            LinkedHashMap<String, String> hMap1 = new LinkedHashMap<>(map2);
                            hMap1.put(child, "");
                            hmaps.add(hMap1);

                        }
                    //   t.removeMapEntries(hmaps, tmpMap);
                    }
                }
        }
        // indexObjects.add(phenominerIndexObject);
     //  System.out.println(gson.toJson(hierarchyMap));
        System.out.println(gson.toJson(hmaps));
    }
    public void removeMapEntries(List<Map<String, String>> maps, List<Map<String,String>> tmpMaps){
        Iterator<Map<String, String>> mapIterator=maps.iterator();
        while(mapIterator.hasNext()){
            Map<String, String> map=mapIterator.next();
            Iterator<Map<String, String>> tmpIterator=tmpMaps.iterator();
            while(tmpIterator.hasNext()){
                Map<String, String> tmpMap=tmpIterator.next();
                if(map.equals(tmpMap)){
                    mapIterator.remove();
                }
            }
        }
    }
    public  List<LinkedHashMap<String, String>>  getIndexObjects(LinkedHashMap<Integer, Set<String>> hierarchyMap,LinkedHashMap<String, Set<String>> parentChildMap ){
        List<LinkedHashMap<String, String>> maps=new ArrayList<>();
        LinkedHashMap<String, String> map=new LinkedHashMap<>();
        for(Map.Entry e:hierarchyMap.entrySet()) {
            Set<String> terms = (Set<String>) e.getValue();
            if (terms.size() > 0) {
                if(terms.size()==1) {
                    map.put(new ArrayList<>(terms).get(0), "");
                }else{

                    for (int i=0;i<terms.size();i++) {
                    LinkedHashMap<String, String> m = new LinkedHashMap<>(map);
                    m.put(new ArrayList<>(terms).get(i), "");

                    maps.add(m);
                }
            }}
        }

        return maps;
    }
    public void addChildren(LinkedHashMap<Integer, Set<String>> hierarchyMap,String termAcc, LinkedHashMap<String, Set<String>> parentChildMap ){

        Set<String> accIds=hierarchyMap.get(hierarchyMap.size());
        Set<String> children=new HashSet<>();
        for(String accId:accIds){
            children.addAll(parentChildMap.get(accId));
        }
        if( children.size()>0) {
            int level=hierarchyMap.size()+1;
            hierarchyMap.put(level, children);
           addChildren(hierarchyMap,termAcc,parentChildMap);
        }

    }
    public void addChildren1(LinkedHashMap<Integer, Map<String, Set<String>>> hierarchyMap,String termAcc, LinkedHashMap<String, Set<String>> parentChildMap ){

        Map<String, Set<String>> accIds=hierarchyMap.get(hierarchyMap.size());
        Map<String, Set<String>> children=new HashMap<>();
        for(Map.Entry entry:accIds.entrySet()){
            Set<String> ids= (Set<String>) entry.getValue();
            for(String accId:ids) {
                children.put(accId, parentChildMap.get(accId));
            }
        }
        if( children.size()>0) {
            int level=hierarchyMap.size()+1;
            hierarchyMap.put(level, children);
            addChildren1(hierarchyMap,termAcc,parentChildMap);
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
                    bulkRequest.timeout(TimeValue.timeValueMillis(2));
                   // bulkRequest.timeout("2m");
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
