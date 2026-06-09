package edu.mcw.rgd.indexer.model;

import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;

public class IndexDocument {
    public static <T> void index(T document) {
        try {
            BulkOperation op = BulkOperation.of(b -> b
                    .index(i -> i
                            .index(RgdIndex.getNewAlias())
                            .document(document)));
            BulkIndexProcessor.bulkProcessor.add(op);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
