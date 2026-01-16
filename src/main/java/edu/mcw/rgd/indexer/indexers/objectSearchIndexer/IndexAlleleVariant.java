package edu.mcw.rgd.indexer.indexers.objectSearchIndexer;


import edu.mcw.rgd.datamodel.*;

import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.AlleleVariantDetails;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ObjectDetails;

import edu.mcw.rgd.indexer.model.IndexObject;

public class IndexAlleleVariant implements Runnable{

    private RgdVariant alleleVariant;

    public IndexAlleleVariant(RgdVariant obj){this.alleleVariant=obj;}
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<RgdVariant> details=new AlleleVariantDetails(alleleVariant, object);
        details.index();
    }
}
