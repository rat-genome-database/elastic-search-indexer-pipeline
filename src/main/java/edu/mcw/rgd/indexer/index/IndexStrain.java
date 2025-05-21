package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.MappedStrain;

import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;

import edu.mcw.rgd.indexer.index.objectDetails.StrainDetails;
import edu.mcw.rgd.indexer.model.IndexObject;


public class IndexStrain implements Runnable {
    private Strain strain;

    public IndexStrain(Strain strain){
        this.strain=strain;}
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<Strain> details=new StrainDetails(strain, object);
        details.index();
    }
}
