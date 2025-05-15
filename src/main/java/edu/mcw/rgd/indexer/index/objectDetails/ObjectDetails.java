package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.Annotations;
import edu.mcw.rgd.indexer.model.GeneIndexObject;

public abstract class ObjectDetails<T> extends IndexDAO implements Details<T> {
    T t;
     GeneIndexObject obj;

    ObjectDetails(T t, GeneIndexObject object){this.t=t; this.obj=object;}
    @Override
    public void mapSpecies() {
        String species = SpeciesType.getCommonName(getSpeciesTypeKey());
        obj.setSpecies(species);
    }
    @Override
    public boolean isSearchable() {
        return SpeciesType.isSearchable(getSpeciesTypeKey());
    }
    @Override
    public void mapExternalDataBaseIdentifiers() {
        try {
            obj.setXdbIdentifiers(getExternalIdentifiers(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void mapTranscripts() {
        try {
            obj.setTranscriptIds(getTranscriptIds(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            obj.setProtein_acc_ids(getTranscriptProteinIds(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void mapAnnotations() {
        try {
            obj.setAnnotationsCount(getAnnotsCount(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Annotations<T> annotations=new Annotations<>(t);
            obj.setGoAnnotations(annotations.getGoAnnotations());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void index() {
        if(isSearchable()) {
            mapObject();
            mapSpecies();
            mapAssembly();
            mapAnnotations();
            mapAssociations();
            mapExternalDataBaseIdentifiers();
            mapTranscripts();
            mapSynonyms();
            indexDocument(obj);
        }
    }
}
