package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.*;

import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.model.Contexts;
import edu.mcw.rgd.indexer.model.IndexObject;


import edu.mcw.rgd.indexer.model.Suggest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.client.Requests.refreshRequest;

/**
 * Created by jthota on 6/20/2017.
 */
public class IndexerDAO extends IndexDAO implements Runnable {


    private AnnotationDAO annotationDAO = new AnnotationDAO();
    private OntologyXDAO ontologyXDAO = new OntologyXDAO();
    private IndexDAO indexDAO= new IndexDAO();
    private Thread t;
    private String ont_id;
    private String ont_name;
    private String index;


    private List<TermSynonym> synonyms;

    public IndexerDAO() {

    }

    public IndexerDAO(String ont_id, String ont_name, String indexName, List<TermSynonym> synonyms) {
        this.ont_id = ont_id;
        this.ont_name = ont_name;
        this.synonyms = synonyms;
        this.index = indexName;


    }
    public void run() {
        try {

            System.out.println(Thread.currentThread().getName() + ": " + ont_id + " started " + new Date());
            log.info(Thread.currentThread().getName() + ": " + ont_id + " started " + new Date());
            String ont_id = this.ont_id;
            List<IndexObject> objs = new ArrayList<>();


            List<Term> terms = null;
            try {
                terms = ontologyXDAO.getActiveTerms(ont_id);
                System.out.println(ont_id + " TERMS SIZE:" + terms.size());
                log.info(ont_id + " TERMS SIZE:" + terms.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (terms != null) {
                for (Term t : terms) {

                    TermWithStats termStats = ontologyXDAO.getTermWithStatsCached(t.getAccId());
                    IndexObject obj = new IndexObject();
                    String acc_id = t.getAccId();
                    String term=t.getTerm();

                    obj.setTerm_acc(acc_id);
                    obj.setTerm(term);
                    obj.setTerm_def(t.getDefinition());
                    obj.setSuggest(indexDAO.getSuggest(term, null, "ontology"));


                    int[][] annotsMatrix = new int[4][7];
                    try {
                        List<Integer> speciesTypeKeys = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
                        int annotsCount = 0;
                        int termOnlyAnnotsCount = 0;
                        int childTermAnnotsCount =0;
                        for (int species : speciesTypeKeys) {
                            annotsCount=annotsCount+ termStats.getAnnotObjectCountForTermAndChildren(species);
                            termOnlyAnnotsCount=termOnlyAnnotsCount+termStats.getAnnotObjectCountForTerm(species);
                        }

                        if (annotsCount > 0) {
                            childTermAnnotsCount=annotsCount - termOnlyAnnotsCount;
                            obj.setAnnotationsCount(annotsCount);
                            obj.setChildTermsAnnotsCount(childTermAnnotsCount);
                            obj.setTermAnnotsCount(termOnlyAnnotsCount);

                            annotsMatrix = this.getAnnotsMatrix(termStats);
                            obj.setAnnotationsMatrix(annotsMatrix);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info(e.getMessage());
                    }
                    String url = null;
                    try {
                        url = this.getPathwayUrl(acc_id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (url != null)
                        obj.setPathwayDiagUrl(url);
                    List<String> termSynonyms = new ArrayList<>();
                    try {

                        for (TermSynonym s : this.synonyms) {
                            if (s.getTermAcc().equalsIgnoreCase(t.getAccId())) {
                                termSynonyms.add(s.getName());
                            }
                        }
                        obj.setSynonyms(termSynonyms);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    obj.setSubcat(this.ont_name);
                    obj.setCategory("Ontology");

                    objs.add(obj);
                }
            }
            System.out.println("Objects List Size of " + ont_id + " : " + objs.size());
            log.info("Objects List Size of " + ont_id + " : " + objs.size());
            if(objs.size()>0){
                indexDAO.indexObjects(objs, index, "search");
            }

            System.out.println("Indexed " + ont_id + " objects Size: " + objs.size() + " Exiting thread.");
            System.out.println(Thread.currentThread().getName() + ": " + ont_id + " End " + new Date());
            log.info("Indexed " + ont_id + " objects Size: " + objs.size() + " Exiting thread.");
            log.info(Thread.currentThread().getName() + ": " + ont_id + " End " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}