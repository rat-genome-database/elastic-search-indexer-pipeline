package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.dao.OntologySynonymsThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by jthota on 7/20/2017.
 */
public class OntologySynonyms {
    public  static Map<String, List<TermSynonym>> ontSynonyms;
    private static String indexCategory;

    public void init(){
        if(!indexCategory.toLowerCase().contains("variant")) {
            OntologyXDAO ontologyXDAO = new OntologyXDAO();

            Map<String, List<TermSynonym>> synonyms = new HashMap<>();

            ExecutorService executor = new MyThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            List<Ontology> ontologies = null;
            try {
                ontologies = ontologyXDAO.getPublicOntologies();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (Ontology ont : ontologies) {
                Runnable workerThread = new OntologySynonymsThread(ont.getId(), synonyms);
                executor.execute(workerThread);
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            ontSynonyms = synonyms;
            System.out.println("DONE Ontology Synonyms " + ontSynonyms.keySet().toString());
        }
    }

    public void setIndexCategory(String indexCategory) {
       this.indexCategory=indexCategory;
    }
}
