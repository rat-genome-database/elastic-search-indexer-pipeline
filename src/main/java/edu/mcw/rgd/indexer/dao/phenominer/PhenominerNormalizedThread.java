package edu.mcw.rgd.indexer.dao.phenominer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        for (Record record : records) {
                Runnable workerThread=new RecordProcessingThread(record,mapper);
                executor.execute(workerThread);

    }
        executor.shutdown();
        while (!executor.isTerminated()){}
//        if (indexObjects.size() > 0) {
//            try {
//                process.indexObject(indexObjects, index);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//      }
    }
}
