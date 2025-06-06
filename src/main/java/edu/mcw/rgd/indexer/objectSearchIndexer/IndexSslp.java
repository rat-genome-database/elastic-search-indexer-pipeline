package edu.mcw.rgd.indexer.objectSearchIndexer;

import edu.mcw.rgd.datamodel.SSLP;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.SSLPDetails;
import edu.mcw.rgd.indexer.model.IndexObject;

public class IndexSslp implements Runnable {
    private final SSLP sslp;
    public IndexSslp(SSLP sslp){
        this.sslp=sslp;
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<SSLP> details=new SSLPDetails(sslp, object);
        details.index();
    }
}
