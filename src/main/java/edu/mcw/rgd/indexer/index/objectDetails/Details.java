package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.indexer.model.IndexObject;

public interface Details<T> {
    int getRgdId();
    int getSpeciesTypeKey();
    boolean isSearchable();
    void mapObject();
    void mapSpecies();
    void mapAssembly();
    void mapPromoters();

    void mapSynonyms();
    void mapTranscripts();
    void mapExternalDataBaseIdentifiers();
    void mapAnnotations();
    void mapAssociations();
    void index();
}
