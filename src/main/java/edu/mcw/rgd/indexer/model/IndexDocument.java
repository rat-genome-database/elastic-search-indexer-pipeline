package edu.mcw.rgd.indexer.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;

public class IndexDocument {
    public static<T> void index(T document) {
        byte[] json = new byte[0];
        try {
            json = JacksonConfiguration.MAPPER.writeValueAsBytes(document);
            BulkIndexProcessor.bulkProcessor.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
}
