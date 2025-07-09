package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExpressionDetails extends GeneDetails{

    private final List<GeneExpression> records;
    public ExpressionDetails(Gene gene, IndexObject obj,  List<GeneExpression> records) {
        super(gene, obj);
        this.records=records;
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
            setExpressionLevel();
            setExpressionUnit();
            setSex();
            setLifeStage();
            IndexDocument.index(obj);
        }
    }

    void setLifeStage(){
        obj.setLifeStage( records.stream().map(r->r.getSample().getLifeStage()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
    void setSex(){
        obj.setSex( records.stream().map(r->r.getSample().getSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    void setExpressionLevel(){
        obj.setExpressionLevel( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionLevel()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }
    void setExpressionUnit(){
        obj.setExpressionUnit( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionUnit()).filter(Objects::nonNull).collect(Collectors.toSet()));

    }


}
