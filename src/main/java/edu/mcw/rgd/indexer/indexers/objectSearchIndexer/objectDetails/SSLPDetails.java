package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.SSLP;
import edu.mcw.rgd.indexer.model.IndexObject;

public class SSLPDetails extends  ObjectDetails<SSLP> {
   public SSLPDetails(SSLP sslp, IndexObject object) {
        super(sslp, object);
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
     obj.setSymbol(t.getName());
     obj.setCategory("SSLP");
    }

    @Override
    public void mapAnnotations() {
     try {
      obj.setAnnotationsCount(getAnnotsCount(getRgdId()));
     } catch (Exception e) {
      e.printStackTrace();
     }
    }

    @Override
    public void mapAssociations() {

    }
}
