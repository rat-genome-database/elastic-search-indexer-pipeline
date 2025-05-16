package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.MappedSSLP;
import edu.mcw.rgd.datamodel.QTL;
import edu.mcw.rgd.datamodel.SSLP;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.index.objectDetails.QTLDetails;
import edu.mcw.rgd.indexer.index.objectDetails.SSLPDetails;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.List;

public class IndexSslp implements Runnable {
    private final SSLP sslp;
    private final MappedSSLP mappedObject;
    public IndexSslp(MappedSSLP mappedSSLP){
        this.mappedObject=mappedSSLP;
        this.sslp=mappedSSLP.getSSLP();
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setChromosome(mappedObject.getChromosome());
        object.setStartPos(mappedObject.getStart());
        object.setStopPos(mappedObject.getStop());
        object.setMapKey(mappedObject.getMapKey());
        ObjectDetails<SSLP> details=new SSLPDetails(sslp, object);
        details.index();
    }
}
