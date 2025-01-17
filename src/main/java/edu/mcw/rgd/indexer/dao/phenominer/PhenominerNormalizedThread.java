package edu.mcw.rgd.indexer.dao.phenominer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.pheno.Condition;
import edu.mcw.rgd.datamodel.pheno.IndividualRecord;
import edu.mcw.rgd.datamodel.pheno.PhenominerUnitTable;
import edu.mcw.rgd.datamodel.pheno.Record;
import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.dao.phenominer.model.PhenominerIndexObject;
import edu.mcw.rgd.indexer.dao.phenominer.utils.PhenominerProcess;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import edu.mcw.rgd.process.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PhenominerNormalizedThread implements Runnable {

    private final Logger log = LogManager.getLogger("phenominer");

    private String index;
//    private BulkIndexProcessor bulkIndexProcessor;
    PhenominerDAO phenominerDAO = new PhenominerDAO();
//    PhenominerProcess process=new PhenominerProcess();
//
//    public PhenominerNormalizedThread() {}

    public PhenominerNormalizedThread(String index) {
        this.index = index;

    }
    @Override
    public void run() {
        List<Record> records = new ArrayList<>();
        try {
            records.addAll(phenominerDAO.getFullRecords());
            records.addAll(phenominerDAO.getChincillaFullRecords());
            log.info("RECORDS SIZE:"+ records.size());
        } catch (Exception e) {
            Utils.printStackTrace(e, log);
        }

        List<PhenominerIndexObject> indexObjects = new ArrayList<>();
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ObjectMapper mapper = JsonMapper.builder().
                enable( JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                //.enable(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION)
                .build();;
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        for (Record record : records) {
                Runnable workerThread=new RecordProcessingThread(record,mapper);
                executor.execute(workerThread);

    }
        executor.shutdown();
        while (!executor.isTerminated()){}
    }
}
