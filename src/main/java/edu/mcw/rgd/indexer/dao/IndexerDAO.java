package edu.mcw.rgd.indexer.dao;


import edu.mcw.rgd.dao.impl.*;

import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.IndexOntTerm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by jthota on 6/20/2017.
 */
public class IndexerDAO extends IndexDAO implements Runnable {
    private OntologyXDAO ontologyXDAO = new OntologyXDAO();
    private IndexDAO indexDAO= new IndexDAO();


    private String ontId;
    private String ontName;
    private String index;
    private List<TermSynonym> synonyms;
    private boolean isAITermMapping;

    public IndexerDAO(String ont_id, String ont_name, String indexName, List<TermSynonym> synonyms, boolean isAITermMapping) {
        this.ontId = ont_id;
        this.ontName = ont_name;
        this.synonyms = synonyms;
        this.index = indexName;

        this.isAITermMapping=isAITermMapping;

    }
    public void run() {
        Logger log= LogManager.getLogger("ontology");

        try {

            System.out.println(Thread.currentThread().getName() + ": " + ontId + " started " + new Date());
            log.info(Thread.currentThread().getName() + ": " + ontId + " started " + new Date());
            String ont_id = this.ontId;
            List<Term> terms = null;
            try {
                terms = ontologyXDAO.getActiveTerms(ont_id);
                System.out.println(ont_id + " TERMS SIZE:" + terms.size());
                log.info(ont_id + " TERMS SIZE:" + terms.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (terms != null) {
                ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

                for (Term t : terms) {
                    Runnable workerThread=new IndexOntTerm(t,ontName, synonyms,isAITermMapping, ontId);
                    executor.execute(workerThread);
                }
                executor.shutdown();
                while(!executor.isTerminated()){}
            }
            System.out.println(Thread.currentThread().getName() + ": " + ont_id + " End " + new Date());
            log.info(Thread.currentThread().getName() + ": " + ont_id + " End " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e);
            throw new RuntimeException();
        }
    }

}