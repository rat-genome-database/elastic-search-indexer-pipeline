package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.datamodel.SSLP;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.index.objectDetails.ReferenceDetails;
import edu.mcw.rgd.indexer.index.objectDetails.SSLPDetails;
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
    private final Reference ref;
    public IndexRef(Reference ref){this.ref=ref;}
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<Reference> details=new ReferenceDetails(ref, object);
        details.index();
        }
}
