package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.dao.phenominer.OntologySynonymsThread;

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
    public static Map<String, List<TermSynonym>> ontSynonyms;

    public void init() throws Exception {
        if(ontSynonyms==null){
            load();
        }
    }
    public void load() throws Exception {
        OntologyXDAO ontologyXDAO= new OntologyXDAO();
        if(ontSynonyms==null){
            ontSynonyms=new HashMap<>();
        }
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        List<Ontology> ontologies = ontologyXDAO.getPublicOntologies();
        for(Ontology ont:ontologies){
            Runnable   workerThread = new OntologySynonymsThread(ont.getId(), ontSynonyms);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()){}
        System.out.println("DONE Ontology Synonyms"+ ontSynonyms.keySet().toString());
    }

}
