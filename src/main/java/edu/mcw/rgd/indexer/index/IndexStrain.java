package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.MappedStrain;

import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;

import edu.mcw.rgd.indexer.index.objectDetails.StrainDetails;
import edu.mcw.rgd.indexer.model.IndexObject;


public class IndexStrain implements Runnable {
    private MappedStrain mappedObject;
    private Strain strain;

    public IndexStrain(MappedStrain mappedObject){
        this.mappedObject=mappedObject;
        this.strain=mappedObject.getStrain();}
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setChromosome(mappedObject.getChromosome());
        object.setStartPos(mappedObject.getStart());
        object.setStopPos(mappedObject.getStop());
        object.setMapKey(mappedObject.getMapKey());
        ObjectDetails<Strain> details=new StrainDetails(strain, object);
        details.index();
    }
}
