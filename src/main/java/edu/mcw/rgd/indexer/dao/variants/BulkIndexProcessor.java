package edu.mcw.rgd.indexer.dao.variants;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import edu.mcw.rgd.services.ClientInit;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BulkIndexProcessor {
    public static BulkIngester<Void> bulkProcessor = null;
    private static BulkIndexProcessor bulkIndexProcessor = null;

    private BulkIndexProcessor() {}

    public static synchronized BulkIndexProcessor getInstance() {
        if (bulkIndexProcessor == null) {
            bulkIndexProcessor = new BulkIndexProcessor();
            bulkProcessor = create();
        }
        return bulkIndexProcessor;
    }

    public static synchronized void init() {
        getInstance();
    }

    private static BulkIngester<Void> create() {
        System.out.println("CREATING NEW BULK PROCESSOR....");
        BulkListener<Void> listener = new BulkListener<>() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request, List<Void> contexts) {}

            @Override
            public void afterBulk(long executionId, BulkRequest request, List<Void> contexts, BulkResponse response) {}

            @Override
            public void afterBulk(long executionId, BulkRequest request, List<Void> contexts, Throwable failure) {}
        };
        ElasticsearchClient esClient;
        try {
            esClient = ClientInit.getClient();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return BulkIngester.of(b -> b
                .client(esClient)
                .maxOperations(10000)
                .maxSize(5L * 1024 * 1024)
                .flushInterval(5, TimeUnit.SECONDS)
                .maxConcurrentRequests(10)
                .listener(listener));
    }

    public static synchronized void destroy() {
        if (bulkProcessor != null) {
            try {
                bulkProcessor.close();
            } finally {
                bulkProcessor = null;
                bulkIndexProcessor = null;
            }
        }
    }
}
