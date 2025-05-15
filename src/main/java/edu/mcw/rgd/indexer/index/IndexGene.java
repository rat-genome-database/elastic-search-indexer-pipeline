package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.index.objectDetails.GeneDetails;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.model.*;

public class IndexGene implements Runnable {
    private MappedGene mappedGene;
    private Gene gene;

    public IndexGene(MappedGene gene){
        this.gene=gene.getGene();
        this.mappedGene=gene;
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setChromosome(mappedGene.getChromosome());
        object.setStartPos(mappedGene.getStart());
        object.setStopPos(mappedGene.getStop());
        object.setMapKey(mappedGene.getMapKey());
        object.setCategory("Gene");
        ObjectDetails<Gene> details=new GeneDetails(gene, object);
        details.index();

//            indexDAO.setSuggest(obj);
//            indexDAO.indexDocument(obj);


    }
}
