package edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectDetails<T> extends IndexDAO implements Details<T> {
    T t;
     IndexObject obj;

    ObjectDetails(T t, IndexObject object){this.t=t; this.obj=object;}
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
    public void mapAssembly() {
        try {
            obj.setMapDataList(getMapData(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void mapSynonyms() {
        List<AliasData> aliases = new ArrayList<>();
        try {
            aliases = getAliases(getRgdId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> synonyms = new ArrayList<>();
        for (AliasData a : aliases) {
            synonyms.add(a.getAlias_name());
        }
        obj.setSynonyms(synonyms);
    }

    @Override
    public void mapPromoters() {

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
