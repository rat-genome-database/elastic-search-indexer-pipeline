package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.datamodel.GenomicElement;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.List;

public class GenomicElementDetails extends  ObjectDetails<GenomicElement> {
    public GenomicElementDetails(GenomicElement genomicElement, IndexObject object) {
        super(genomicElement, object);
    }

    @Override
    public int getRgdId() {
        return t.getRgdId();
    }

    @Override
    public int getSpeciesTypeKey() {
        return t.getSpeciesTypeKey();
    }

    @Override
    public void mapObject() {
        obj.setTerm_acc(String.valueOf(getRgdId()));
        obj.setSymbol(t.getSymbol());
        obj.setName(t.getName());
        obj.setDescription(t.getObjectType());
        obj.setGenomicAlteration(t.getGenomicAlteration());
    }

    @Override
    public void mapAnnotations() {
        try {
            obj.setAnnotationsCount(getAnnotsCount(getRgdId()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mapAssociations() {
        List<String> assocs= associations.get(getRgdId());
        if(assocs!=null && assocs.size()>0)
            obj.setAssociations(assocs);
    }
}
