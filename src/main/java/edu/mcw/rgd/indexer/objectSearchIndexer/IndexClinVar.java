package edu.mcw.rgd.indexer.objectSearchIndexer;


import edu.mcw.rgd.datamodel.VariantInfo;

import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ClinvarDetails;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ObjectDetails;

import edu.mcw.rgd.indexer.model.IndexObject;


public class IndexClinVar implements Runnable{
    private final VariantInfo variantInfo;

    public IndexClinVar(VariantInfo obj){this.variantInfo=obj;}
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<VariantInfo> details=new ClinvarDetails(variantInfo, object);
        details.index();
    }
}
