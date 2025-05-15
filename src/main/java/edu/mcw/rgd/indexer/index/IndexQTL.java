package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.index.objectDetails.Details;
import edu.mcw.rgd.indexer.index.objectDetails.GeneDetails;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.index.objectDetails.QTLDetails;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.Annotations;
import edu.mcw.rgd.indexer.model.IndexObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class IndexQTL implements Runnable {
    private final MappedQTL mappedObject;
    private final QTL qtl;
    public IndexQTL(MappedQTL mappedQTL){
        this.mappedObject=mappedQTL;
        this.qtl=mappedQTL.getQTL();
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setChromosome(mappedObject.getChromosome());
        object.setStartPos(mappedObject.getStart());
        object.setStopPos(mappedObject.getStop());
        object.setMapKey(mappedObject.getMapKey());
        object.setCategory("QTL");
        ObjectDetails<QTL> details=new QTLDetails(qtl, object);
        details.index();
    }
}
