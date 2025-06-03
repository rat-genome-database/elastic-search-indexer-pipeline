package edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.List;

public class ReferenceDetails extends ObjectDetails<Reference>{

   public ReferenceDetails(Reference reference, IndexObject object) {
        super(reference, object);
    }

    @Override
    public int getRgdId() {
        return t.getRgdId();
    }

    @Override
    public int getSpeciesTypeKey() {
        return t.getSpeciesTypeKey();
    }

    @Override
    public void mapObject() {
        obj.setTerm_acc(String.valueOf(getRgdId()));
        obj.setCitation(t.getCitation());
        obj.setTitle(t.getTitle());
        List<String> authors= null;
        try {
            authors = getAuthors(t.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        obj.setAuthor(authors);

        List<String> input= new ArrayList<>();
        if(authors!=null)
            input.addAll(authors);
        if(t.getTitle()!=null)
            input.add(t.getTitle());

        obj.setRefAbstract(t.getRefAbstract());
        if(t.getPubDate()!=null)
            obj.setPub_year(Integer.toString(t.getPubDate().getYear()+1900));
        obj.setCategory("Reference");

    }

    @Override
    public void mapAnnotations() {

    }

    @Override
    public void mapAssociations() {

    }
}
