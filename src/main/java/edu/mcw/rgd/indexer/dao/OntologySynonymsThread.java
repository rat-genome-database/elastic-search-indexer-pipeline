package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class OntologySynonymsThread implements Runnable{
    
    private String ontId;
    private Map<String, List<TermSynonym>> ontSynonyms;
    OntologyXDAO ontologyXDAO=new OntologyXDAO();
    public OntologySynonymsThread(String ontId,Map<String, List<TermSynonym>> ontSynonyms){
        this.ontId=ontId;
        this.ontSynonyms=ontSynonyms;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": " + ontId + "Synonyms START " + new Date());
        try {
            List<TermSynonym> synonyms = ontologyXDAO.getActiveSynonyms(ontId);
            ontSynonyms.put(ontId, synonyms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + ": " + ontId + "Synonyms END " + new Date());
    }
}
