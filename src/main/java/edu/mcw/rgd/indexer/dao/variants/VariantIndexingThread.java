package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;

import java.util.List;

public class VariantIndexingThread implements Runnable {
    private List<VariantIndex> indexList;
    public VariantIndexingThread(List<VariantIndex> indexList){
        this.indexList=indexList;
    }
    @Override
    public void run() {

        for(VariantIndex vi:indexList) {
            IndexDocument.index(vi);
        }


    }
}
