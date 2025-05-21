package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.indexer.model.IndexObject;

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

    }

    @Override
    public void mapAnnotations() {

    }

    @Override
    public void mapAssociations() {

    }
}
