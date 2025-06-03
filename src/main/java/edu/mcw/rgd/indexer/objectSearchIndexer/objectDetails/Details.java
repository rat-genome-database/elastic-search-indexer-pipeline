package edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails;

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
