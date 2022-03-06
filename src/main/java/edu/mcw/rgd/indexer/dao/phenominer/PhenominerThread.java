package edu.mcw.rgd.indexer.dao.phenominer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;

import edu.mcw.rgd.datamodel.ontologyx.Term;

import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;

import edu.mcw.rgd.indexer.dao.phenominer.model.TreeNode;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;


import javax.swing.text.html.parser.ContentModel;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PhenominerThread implements Runnable {
    private String index;
    private Logger log;
    PhenominerDAO phenominerDAO = new PhenominerDAO();
    OntologyXDAO xdao = new OntologyXDAO();
    StrainDAO strainDAO = new StrainDAO();
    AssociationDAO associationDAO = new AssociationDAO();
    IndexDAO indexDAO = new IndexDAO();

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

            //  for(Record record:records){
            if(records.size()>0){
                Record record=records.get(0);
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

                for(String ontology:ontologies){
                    String rootTermAcc=xdao.getRootTerm(ontology);
                    Term rootTerm=xdao.getTerm(rootTermAcc);
                    ontMaps.put(rootTerm.getOntologyId(), mapOntology(record,rootTerm,0,indexObjects));
                }
                for(LinkedHashMap<String, String> cmoMap:ontMaps.get("CMO")){
                    for(LinkedHashMap<String, String> rsMap:ontMaps.get("RS")){
                        for(LinkedHashMap<String, String> mmoMap:ontMaps.get("MMO")){
                            PhenominerIndexObject object=new PhenominerIndexObject();
                            object.setRecordId(record.getId());
                            object.setCmoHierarchyMap(cmoMap);
                            object.setCmoTerms(new ArrayList<>(cmoMap.keySet()));
                            object.setMmoHierarchyMap(mmoMap);
                            object.setMmoTerms(new ArrayList<>(mmoMap.keySet()));

                            object.setRsHierarchyMap(rsMap);
                            object.setRsTerms(new ArrayList<>(rsMap.keySet()));

                            indexObjects.add(object);
                        }
                    }
                }
            }
            System.out.println("INDEX OBJeCTS SIZE:" + indexObjects.size());

            if (indexObjects.size() > 0)
                indexObjects(indexObjects, index, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<LinkedHashMap<String, String>> mapOntology(Record record, Term rootTerm, int maxAncestorDepth, List<PhenominerIndexObject> indexObjects) throws Exception {

        String termAcc = new String();
        List<LinkedHashMap<String, String>> mapList=new ArrayList<>();
        List<LinkedHashMap<String, String>> termUpdatedMapList=new ArrayList<>();
        switch (rootTerm.getOntologyId()){
            case "RS":
                termAcc= record.getSample().getStrainAccId();
                break;
            case "CMO":
                termAcc= record.getClinicalMeasurement().getAccId();
                break;
            case "MMO":
                termAcc=record.getMeasurementMethod().getAccId();
                break;
            case "XCO":

                //  termAcc=record.getConditions().get(0).getOntologyId();
                break;
            default:

        }
        if(termAcc!=null && !termAcc.equals("")) {
            Map<Integer, List<Term>> parentTerms = new HashMap<>();
            getParentTerms(termAcc, parentTerms);
            mapList= getTermHierarchyMaps(termAcc, rootTerm.getAccId());
            for(LinkedHashMap<String, String> map:mapList){
                LinkedHashMap<String, String> mapUpdated=new LinkedHashMap<>();
                for(String key:map.keySet()){
                    Term term=  xdao.getTerm(key);
                    mapUpdated.put(term.getTerm(), term.getAccId());
                }
                termUpdatedMapList.add(mapUpdated);
            }
        }
        return termUpdatedMapList;
    }

    public TreeNode getParentHierarchy(String termAcc, int maxAncestorDepth, LinkedHashMap<String, Integer> hierarchyMap) throws Exception {
        List<Term> terms = xdao.getParentTerm(termAcc);
        TreeNode rootNode=null;
        try {
            rootNode = new TreeNode(xdao.getTerm(termAcc));
            if (terms != null && terms.size() > 0)
                for (Term t : terms) {
                    TreeNode parentNode = getParentHierarchy(t.getAccId(), 0, null);
                    rootNode.getParents().add(parentNode);
                }
        }catch (Exception e){
            System.out.println("TERM ACC WHERE TERM NOT FOUND:"+termAcc);

        }
        return rootNode;
    }

    public void getParentTerms(String termAcc, Map<Integer, List<Term>> parentTerms) throws Exception {
        List<Term> terms = xdao.getParentTerm(termAcc);
        if (terms.size() > 0) {
            int count = 1;
            count = count + parentTerms.size();
            parentTerms.put(count, terms);
            if (terms.size() == 1)
                for (Term term : terms) {
                    PhenominerIndexObject object = new PhenominerIndexObject();
                    object.setRsTerm(term.getTerm());
                    getParentTerms(term.getAccId(), parentTerms);
                }
        }

    }

    // public static void main(String[] args) throws Exception {
    public List<LinkedHashMap<String, String>> getTermHierarchyMaps(String rsTermAcc, String rsRootTermAcc) throws Exception {
        //   OntologyXDAO xdao=new OntologyXDAO();
        //   PhenominerThread t=new PhenominerThread();
        //   String rsTermAcc="RS:0001782";
        //   String rsRootTermAcc=xdao.getRootTerm("RS");
        LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
        Gson gson = new Gson();
        List<LinkedHashMap<String, String>> hmaps = new ArrayList<>();

        TreeNode rootNode = getParentHierarchy(rsTermAcc, 0, null);
        if(rootNode!=null) {
            Queue<TreeNode> queue = new LinkedList<>();
            Stack<TreeNode> stack = new Stack<>();
            queue.offer(rootNode);
            while (!queue.isEmpty()) {
                int len = queue.size();
                for (int i = 0; i < len; i++) { // so that we can reach each level
                    TreeNode node = queue.poll();
                    stack.push(node);
                    System.out.print(node.getTerm().getTerm() + " ");
                    for (TreeNode item : node.getParents()) { // for-Each loop to iterate over all childrens
                        queue.offer(item);
                    }
                }
                System.out.println();
            }
            System.out.println("************STACK**************:" + stack.size());
            while (!stack.empty()) {
                TreeNode node = stack.pop();
                List<String> terms = new ArrayList<>();
                if (map.keySet().contains(node.getTerm().getAccId())) {
                    terms.addAll(map.get(node.getTerm().getAccId()));
                }
                terms.addAll(node.getParents().stream().map(n -> n.getTerm().getAccId()).collect(Collectors.toList()));

                map.put(node.getTerm().getAccId(), terms);
                System.out.print(node.getTerm().getTerm() + "\t");
                System.out.println();
            }

            LinkedHashMap<String, Set<String>> parentChildMap = new LinkedHashMap<>();
            for (Map.Entry e : map.entrySet()) {
                String rootKey = (String) e.getKey();
                Set<String> childAccIds = new HashSet<>();
                for (Map.Entry entry : map.entrySet()) {
                    List<String> values = (List<String>) entry.getValue();
                    if (values.contains(rootKey)) {
                        childAccIds.add((String) entry.getKey());
                    }
                }
                parentChildMap.put(rootKey, childAccIds);
            }

        /*LinkedHashMap<Integer, Set<String>> hierarchyMap=new LinkedHashMap<>();
        hierarchyMap.put(1,new HashSet<>(Arrays.asList(rsRootTermAcc)));
        addChildren(hierarchyMap, parentChildMap);*/
            LinkedHashMap<Integer, Map<String, Set<String>>> hierarchyMap1 = new LinkedHashMap<>();
            Map<String, Set<String>> map1 = new HashMap<>();
            map1.put("", new HashSet<>(Arrays.asList(rsRootTermAcc)));
            hierarchyMap1.put(1, map1);
            addChildren1(hierarchyMap1, rsRootTermAcc, parentChildMap);
            LinkedHashMap<String, String> hMap = new LinkedHashMap<>();
            hMap.put(rsRootTermAcc, "");
            hmaps.add(hMap);
            Set<Integer> keySet = (Set<Integer>) hierarchyMap1.keySet();
            for (int key : keySet) {
                List<LinkedHashMap<String, String>> tmpMap = new ArrayList<>();

                for (Map.Entry e : hierarchyMap1.get(key).entrySet()) {
                    String parent = (String) e.getKey();

                    if (!parent.equals("")) {
                        Set<String> children = (Set<String>) e.getValue();
                        LinkedHashMap<String, String> map2 = new LinkedHashMap<>();
                        for (int i = 0; i < hmaps.size(); i++) {
                            LinkedHashMap<String, String> map3 = hmaps.get(i);
                            if (new ArrayList(map3.keySet()).get(map3.size() - 1).equals(parent) && !map3.keySet().contains(rsTermAcc)) {
                                map2 = map3;
                                tmpMap.add(map3);
                                break;
                            }
                        }
                        if (tmpMap.size() > 0) {
                            removeMapEntries(hmaps, tmpMap);
                        }
                        if (map2.size() > 0)
                            for (String child : children) {
                                LinkedHashMap<String, String> hMap1 = new LinkedHashMap<>(map2);
                                hMap1.put(child, "");
                                hmaps.add(hMap1);

                            }
                    }
                }
            }
        }
        // indexObjects.add(phenominerIndexObject);
        //  System.out.println(gson.toJson(hierarchyMap));
        System.out.println(gson.toJson(hmaps));
        return hmaps;
    }

    public void removeMapEntries(List<LinkedHashMap<String, String>> maps, List<LinkedHashMap<String, String>> tmpMaps) {
        Iterator<LinkedHashMap<String, String>> tmpIterator = tmpMaps.iterator();
        while (tmpIterator.hasNext()) {
            Map<String, String> tmpMap = tmpIterator.next();
            Iterator<LinkedHashMap<String, String>> mapIterator = maps.iterator();

            while (mapIterator.hasNext()) {
                Map<String, String> map = mapIterator.next();
                if (map.equals(tmpMap)) {
                    mapIterator.remove();
                }
            }
        }
    }

    public void addChildren(LinkedHashMap<Integer, Set<String>> hierarchyMap, LinkedHashMap<String, Set<String>> parentChildMap) {

        Set<String> accIds = hierarchyMap.get(hierarchyMap.size());
        Set<String> children = new HashSet<>();
        for (String accId : accIds) {
            if (parentChildMap.get(accId) != null)
                children.addAll(parentChildMap.get(accId));
        }
        if (children.size() > 0) {
            int level = hierarchyMap.size() + 1;
            hierarchyMap.put(level, children);
            addChildren(hierarchyMap, parentChildMap);
        }

    }

    public void addChildren1(LinkedHashMap<Integer, Map<String, Set<String>>> hierarchyMap, String termAcc, LinkedHashMap<String, Set<String>> parentChildMap) {

        Map<String, Set<String>> accIds = hierarchyMap.get(hierarchyMap.size());
        Map<String, Set<String>> children = new HashMap<>();
        for (Map.Entry entry : accIds.entrySet()) {
            Set<String> ids = (Set<String>) entry.getValue();
            for (String accId : ids) {
                children.put(accId, parentChildMap.get(accId));
            }
        }
        if (children.size() > 0) {
            int level = hierarchyMap.size() + 1;
            hierarchyMap.put(level, children);
            addChildren1(hierarchyMap, termAcc, parentChildMap);
        }

    }


    public void indexObjects(List<PhenominerIndexObject> objs, String index, String type) throws ExecutionException, InterruptedException, IOException {
        // BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkRequest bulkRequest = new BulkRequest();
        //  bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        bulkRequest.timeout(TimeValue.timeValueMinutes(2));
        bulkRequest.timeout("2m");
        int docCount = 0;
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
            if (docCount % 100 == 0) {
                ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                bulkRequest = new BulkRequest();
                bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                bulkRequest.timeout(TimeValue.timeValueMinutes(2));
                bulkRequest.timeout("2m");
            } else {
                if (docCount > objs.size() - 100 && docCount == objs.size()) {

                    ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    bulkRequest = new BulkRequest();
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                    bulkRequest.timeout(TimeValue.timeValueMillis(2));
                    // bulkRequest.timeout("2m");
                }
            }
        }
        //   ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);

        //   BulkResponse response=       bulkRequestBuilder.get();

        //  ESClient.getClient().admin().indices().refresh(refreshRequest()).actionGet();
        RefreshRequest refreshRequest = new RefreshRequest();
        ESClient.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
    }
}
