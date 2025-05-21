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
