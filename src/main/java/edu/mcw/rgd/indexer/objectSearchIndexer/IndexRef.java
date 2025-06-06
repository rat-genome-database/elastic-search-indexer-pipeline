package edu.mcw.rgd.indexer.objectSearchIndexer;

import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ReferenceDetails;
import edu.mcw.rgd.indexer.model.IndexObject;

public class IndexRef implements Runnable {
    private final Reference ref;
    public IndexRef(Reference ref){this.ref=ref;}
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<Reference> details=new ReferenceDetails(ref, object);
        details.index();
        }
}
