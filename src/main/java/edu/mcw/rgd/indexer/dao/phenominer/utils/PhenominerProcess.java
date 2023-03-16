package edu.mcw.rgd.indexer.dao.phenominer.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.datamodel.pheno.Condition;
import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;
import edu.mcw.rgd.indexer.dao.phenominer.model.TreeNode;
import edu.mcw.rgd.services.ClientInit;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.xcontent.XContentType;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PhenominerProcess {
    PhenominerDAO phenominerDAO = new PhenominerDAO();
    OntologyXDAO xdao = new OntologyXDAO();
    StrainDAO strainDAO = new StrainDAO();
    AssociationDAO associationDAO = new AssociationDAO();
    IndexDAO indexDAO = new IndexDAO();
    public Set<String> mapSynonyms(Record record, Term rootTerm) throws Exception {
        List<String> accIds=new ArrayList<>();

        switch (rootTerm.getOntologyId()){
            case "RS":
                accIds.add (record.getSample().getStrainAccId());
                break;
            case "CMO":
                accIds.add ( record.getClinicalMeasurement().getAccId());
                break;
            case "MMO":
                accIds.add(record.getMeasurementMethod().getAccId());
                break;
            case "XCO":
                for(Condition condition:record.getConditions()) {
                    accIds.add(condition.getOntologyId());
                }
                break;
            default:

        }
        Set<String> synonyms=new HashSet<>();
        List<String> synTypes=new ArrayList<>(Arrays.asList("alt_id",
                "axiom_lost",
                "broad_synonym",
                "consider",
                "cyclic_relationship",
                "disease_has_feature",
                "disease_shares_features_of",
                "display_synonym",
                "exact_synonym",
                "external_ontology",
                "narrow_synonym",
                "never_in_taxon",
                "only_in_taxon",
                "pimary_id",
                "present_in_taxon",
                "primary_id",
                "related_synonym",
                "replaced_by",
                "see_also",
                "seeAlso",
                "subset",
                "Synonym",
                "synonym",
                "xref",
                "xref_mesh"));
        for(String termAcc:accIds) {
            List<TermSynonym> termSynonyms= xdao.getSynonymsForTermAndAscendants(termAcc, synTypes);
            for(TermSynonym s:termSynonyms){
                synonyms.add(s.getName());
            }

        }

        return synonyms;
    }
    public List<LinkedHashMap<String, String>> mapOntology(Record record, Term rootTerm) throws Exception {
        List<String> accIds=new ArrayList<>();

        List<LinkedHashMap<String, String>> mapList=new ArrayList<>();
        List<LinkedHashMap<String, String>> termUpdatedMapList=new ArrayList<>();
        switch (rootTerm.getOntologyId()){
            case "RS":
                accIds.add (record.getSample().getStrainAccId());
                break;
            case "CMO":
                accIds.add ( record.getClinicalMeasurement().getAccId());
                break;
            case "MMO":
                accIds.add(record.getMeasurementMethod().getAccId());
                break;
            case "XCO":
                for(Condition condition:record.getConditions()) {
                    accIds.add(condition.getOntologyId());
                }
                break;
            default:

        }
        for(String termAcc:accIds) {
            if (termAcc != null && !termAcc.equals("")) {
                Map<Integer, List<Term>> parentTerms = new HashMap<>();
                getParentTerms(termAcc, parentTerms);
                mapList = getTermHierarchyMaps(termAcc, rootTerm.getAccId());
                for (LinkedHashMap<String, String> map : mapList) {
                    LinkedHashMap<String, String> mapUpdated = new LinkedHashMap<>();
                    for (String key : map.keySet()) {
                        Term term = xdao.getTerm(key);
                        mapUpdated.put(term.getTerm(), term.getAccId());
                    }
                    termUpdatedMapList.add(mapUpdated);
                }
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
                            if(children!=null && children.size()>0)
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
            if(ids!=null && ids.size()>0)
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
        System.out.println("CREATED BULK REQUEST and INDEXING STARTED....");
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
                ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                bulkRequest = new BulkRequest();
                bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                bulkRequest.timeout(TimeValue.timeValueMinutes(2));
                bulkRequest.timeout("2m");
            } else {
                if (docCount > objs.size() - 100 && docCount == objs.size()) {

                    ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    bulkRequest = new BulkRequest();
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
                    bulkRequest.timeout(TimeValue.timeValueMillis(2));
                    // bulkRequest.timeout("2m");
                }
            }
        }
        RefreshRequest refreshRequest = new RefreshRequest();
        ClientInit.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
    }
}
