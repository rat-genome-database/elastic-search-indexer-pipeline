package edu.mcw.rgd.indexer.indexers.objectSearchIndexer;

import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.QTLDetails;
import edu.mcw.rgd.indexer.model.IndexObject;

public class IndexQTL implements Runnable {
    private final QTL qtl;
    public IndexQTL(QTL qtl){
        this.qtl=qtl;
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setCategory("QTL");
        ObjectDetails<QTL> details=new QTLDetails(qtl, object);
        details.index();
    }
}
