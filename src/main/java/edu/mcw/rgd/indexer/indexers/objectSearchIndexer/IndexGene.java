package edu.mcw.rgd.indexer.indexers.objectSearchIndexer;

import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.GeneDetails;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.model.*;

public class IndexGene implements Runnable {
    private final Gene gene;
    public IndexGene(Gene gene){
        this.gene=gene;
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setCategory("Gene");
        ObjectDetails<Gene> details=new GeneDetails(gene, object);
        details.index();


    }
}
