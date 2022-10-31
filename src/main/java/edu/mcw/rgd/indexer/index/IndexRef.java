package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.Contexts;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.model.Suggest;
import org.elasticsearch.action.index.IndexResponse;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndexRef implements Runnable {
    private Reference ref;
    IndexDAO indexDAO=new IndexDAO();
    public IndexRef(Reference ref){this.ref=ref;}
    @Override
    public void run() {
        int speciesTypeKey=ref.getSpeciesTypeKey();
        //    boolean isSearchable=SpeciesType.isSearchable(speciesTypeKey);
        //      if(isSearchable){
        String species= SpeciesType.getCommonName(speciesTypeKey);

        int rgdId= ref.getRgdId();
        IndexObject r= new IndexObject();
        r.setTerm_acc(String.valueOf(rgdId));
        r.setCitation(ref.getCitation());
        r.setTitle(ref.getTitle());
        List<String> authors= null;
        try {
            authors = indexDAO.getAuthors(ref.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        r.setAuthor(authors);

        List<String> input= new ArrayList<>();
        Suggest sugg= new Suggest();
        if(authors!=null)
            input.addAll(authors);
        if(ref.getTitle()!=null)
            input.add(ref.getTitle());
        sugg.setInput(input);

        Contexts contexts= new Contexts();
        contexts.setCategory(new ArrayList<String>(Arrays.asList("reference")));

        sugg.setContexts(contexts);
        r.setSuggest(sugg);
        r.setCategory("Reference");

        r.setSpecies(species);
        List<AliasData> alist= null;
        try {
            alist = indexDAO.getAliases(rgdId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //      r.setAliasDatas(alist);
        r.setRefAbstract(ref.getRefAbstract());
        List<String> synonyms= new ArrayList<>();

        for(AliasData a:alist ){
            synonyms.add(a.getAlias_name());
        }
        r.setSynonyms(synonyms);
        List<String> xids= null;
        try {
            xids = indexDAO.getExternalIdentifiers(rgdId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        r.setXdbIdentifiers(xids);
        if(ref.getPubDate()!=null)
            r.setPub_year(Integer.toString(ref.getPubDate().getYear()+1900));
        indexDAO.indexDocument(r);    }
}
