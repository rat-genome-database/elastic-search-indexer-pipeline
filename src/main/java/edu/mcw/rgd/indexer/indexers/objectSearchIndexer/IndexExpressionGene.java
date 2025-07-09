package edu.mcw.rgd.indexer.indexers.objectSearchIndexer;

import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.datamodel.*;

import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ExpressionDetails;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ObjectDetails;

import java.util.List;
import java.util.stream.Collectors;


public class IndexExpressionGene extends GeneExpressionDAO implements Runnable{
    private Gene gene;
    List<GeneExpression> records;
    public IndexExpressionGene(Gene gene){
        this.gene=gene;
        setRecords();
    }
    @Override
    public void run() {
        if(records.size()>0) {
            IndexObject object = new IndexObject();
            object.setCategory("Expressed Gene");
            ObjectDetails<Gene> details = new ExpressionDetails(gene, object, records);
            details.index();
        }
    }
    public void setRecords(){
        try {
            this.records= getGeneExpressionObjectsByRgdIdUnit(gene.getRgdId(), "TPM")
                    .stream().filter(r->
                            ( r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("high") ||
                                    r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("low"))).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
