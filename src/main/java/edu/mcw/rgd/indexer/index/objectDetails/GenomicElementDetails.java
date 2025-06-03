package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.datamodel.GenomicElement;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.List;
import java.util.Map;

public class GenomicElementDetails extends  ObjectDetails<GenomicElement> {
   private final  Map<Integer, List<String>> associations;
    public GenomicElementDetails(GenomicElement genomicElement, IndexObject object,     Map<Integer, List<String>> associations) {
        super(genomicElement, object);
        this.associations=associations;
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
