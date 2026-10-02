package edu.mcw.rgd.indexer.dao.variants;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkIngester;
import co.elastic.clients.elasticsearch._helpers.bulk.BulkListener;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.util.BinaryData;
import com.google.gson.Gson;
import edu.mcw.rgd.services.ClientInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class BulkIndexProcessor {
    public static BulkIngester<Void> bulkProcessor = null;
    private static BulkIndexProcessor bulkIndexProcessor = null;

    private static final Logger log = LogManager.getLogger("main");
    private static final Gson gson = new Gson();
    private static final int MAX_DOC_CHARS = 2000;
    private static final AtomicLong rejectedDocs = new AtomicLong();
    private static final AtomicLong submittedDocs = new AtomicLong();

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
        rejectedDocs.set(0);
        submittedDocs.set(0);
        BulkListener<Void> listener = new BulkListener<>() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request, List<Void> contexts) {
                int ops = opCount(request);
                submittedDocs.addAndGet(ops);
                log.debug("BULK SUBMIT [executionId=" + executionId + "] operations=" + ops);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, List<Void> contexts, BulkResponse response) {
                int ops = opCount(request);
                if (!response.errors()) {
                    log.debug("BULK OK [executionId=" + executionId + "] operations=" + ops + " took=" + response.took() + "ms");
                    return;
                }
                List<BulkResponseItem> items = response.items();
                List<BulkOperation> operations = request.operations();
                long failedInBatch = 0;
                for (int i = 0; i < items.size(); i++) {
                    BulkResponseItem item = items.get(i);
                    ErrorCause error = item.error();
                    if (error == null) {
                        continue;
                    }
                    failedInBatch++;
                    rejectedDocs.incrementAndGet();
                    BulkOperation operation = (operations != null && i < operations.size()) ? operations.get(i) : null;
                    log.error("BULK ITEM REJECTED [executionId=" + executionId + "]"
                            + " index=" + item.index()
                            + " id=" + item.id()
                            + " status=" + item.status()
                            + " type=" + error.type()
                            + " reason=" + error.reason()
                            + " document=" + describe(operation));
                    ErrorCause cause = error.causedBy();
                    while (cause != null) {
                        log.error("    causedBy: " + cause.type() + " - " + cause.reason());
                        cause = cause.causedBy();
                    }
                }
                log.error("BULK BATCH HAD FAILURES [executionId=" + executionId + "] rejected=" + failedInBatch
                        + " of " + ops + " operations");
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, List<Void> contexts, Throwable failure) {
                int ops = opCount(request);
                rejectedDocs.addAndGet(ops);
                log.error("BULK REQUEST FAILED [executionId=" + executionId + "] " + ops
                        + " document(s) NOT indexed: " + failure, failure);
            }
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

    private static int opCount(BulkRequest request) {
        return (request == null || request.operations() == null) ? 0 : request.operations().size();
    }

    /** the source document of a failed operation, so the rejected record can be identified in the logs */
    private static String describe(BulkOperation operation) {
        if (operation == null) {
            return "<unavailable>";
        }
        try {
            Object document = null;
            if (operation.isIndex()) {
                document = operation.index().document();
            } else if (operation.isCreate()) {
                document = operation.create().document();
            }
            if (document == null) {
                return "<" + operation._kind() + ">";
            }
            // the client serializes the document when the operation is built, so what we hold is usually
            // the exact json elasticsearch rejected - decode it rather than dumping the raw byte array
            if (document instanceof BinaryData) {
                return truncate(new String(((BinaryData) document).asByteBuffer().array(), StandardCharsets.UTF_8));
            }
            return truncate(gson.toJson(document));
        } catch (Exception e) {
            return "<unserializable: " + e + ">";
        }
    }

    private static String truncate(String json) {
        return json.length() > MAX_DOC_CHARS ? json.substring(0, MAX_DOC_CHARS) + "...[truncated]" : json;
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
        long rejected = rejectedDocs.get();
        if (rejected > 0) {
            log.error("BULK INDEXING SUMMARY: " + rejected + " of " + submittedDocs.get()
                    + " document(s) were REJECTED by Elasticsearch - see errors above");
        } else {
            log.info("BULK INDEXING SUMMARY: " + submittedDocs.get()
                    + " document(s) submitted, no failures reported");
        }
    }
}
