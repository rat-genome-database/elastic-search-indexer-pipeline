package edu.mcw.rgd.indexer.objectSearchIndexer;

import edu.mcw.rgd.dao.impl.AliasDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.RGDManagementDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.Alias;
import edu.mcw.rgd.datamodel.RgdId;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class IndexOntTerm implements Runnable {
    private Term t;
    private String ontName;
    private String ontId;
    private List<TermSynonym> synonyms;
    boolean isAITermMapping;
    IndexDAO indexDAO=new IndexDAO();
    OntologyXDAO ontologyXDAO=new OntologyXDAO();
    RGDManagementDAO rgdManagementDAO=new RGDManagementDAO();
    AliasDAO aliasDAO=new AliasDAO();
    StrainDAO strainDAO=new StrainDAO();
    public IndexOntTerm(Term t,  String ontName, List<TermSynonym> synonyms, boolean isAITermMapping, String ontId) {
        this.t=t;
        this.ontName=ontName;
        this.synonyms=synonyms;
        this.isAITermMapping=isAITermMapping;
        this.ontId=ontId;
    }
    @Override
    public void run() {
        TermWithStats termStats = null;
        try {
            termStats = ontologyXDAO.getTermWithStatsCached(t.getAccId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        IndexObject obj = new IndexObject();
        String acc_id = t.getAccId();
        String term=t.getTerm();

        obj.setTerm_acc(acc_id);
        obj.setTerm(term);
        obj.setTerm_def(t.getDefinition());


        int[][] annotsMatrix = new int[4][7];
        try {
            //         List<Integer> speciesTypeKeys = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
            Collection<Integer> speciesTypeKeys =  SpeciesType.getSpeciesTypeKeys();
            int annotsCount = 0;
            int termOnlyAnnotsCount = 0;
            int childTermAnnotsCount =0;
            for (int species : speciesTypeKeys) {
                if (SpeciesType.isSearchable(species)) {
                    annotsCount = annotsCount + termStats.getAnnotObjectCountForTermAndChildren(species);
                    termOnlyAnnotsCount = termOnlyAnnotsCount + termStats.getAnnotObjectCountForTerm(species);
                }
            }
            if (annotsCount > 0) {
                childTermAnnotsCount=annotsCount - termOnlyAnnotsCount;
                obj.setAnnotationsCount(annotsCount);
                obj.setChildTermsAnnotsCount(childTermAnnotsCount);
                obj.setTermAnnotsCount(termOnlyAnnotsCount);

                annotsMatrix = indexDAO.getAnnotsMatrix(termStats);
                obj.setAnnotationsMatrix(annotsMatrix);

            }
        } catch (Exception e) {
            e.printStackTrace();
           // log.info(e.getMessage());
        }
        String url = null;
        try {
            url = indexDAO.getPathwayUrl(acc_id);
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
                    if(s.getName().contains("RGD ID")){
                        String[] tokens=  s.getName().split(":");
                        int rgdId= Integer.parseInt(tokens[1].trim());
                        RgdId id = rgdManagementDAO.getRgdId(rgdId);
                        if(id.getObjectKey()==5){
                            List<Strain> strain= strainDAO.getStrains(Arrays.asList(rgdId));
                            termSynonyms.add(strain.get(0).getName());
                            for(Alias alias:aliasDAO.getAliases(strain.get(0).getRgdId())){
                                termSynonyms.add(alias.getValue());
                            }

                        }

                    }
                }
            }
            obj.setSynonyms(termSynonyms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        obj.setSubcat(this.ontName);
        obj.setCategory("Ontology");
        indexDAO.setSuggest(obj);
        if(!isAITermMapping){
            IndexDocument.index(obj);}
        else {
            obj.setSubcat(ontId);
            indexDAO.indexAITermMappingDocument(obj);
        }
    }
}
