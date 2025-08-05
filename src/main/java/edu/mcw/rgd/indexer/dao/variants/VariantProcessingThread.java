package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VariantProcessingThread implements Runnable{
    private final List<VariantIndex> indexList;
    private final int mapKey;


    public VariantProcessingThread(int mapKey,  List<VariantIndex> indexList){
        this.indexList = indexList;
        this.mapKey=mapKey;
    }
    @Override
    public void run() {
        if (indexList == null || indexList.isEmpty()) return;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


            Set<Long> uniqueVariantIds=indexList.stream().map(VariantIndex::getVariant_id).collect(Collectors.toSet());
            for(long id:uniqueVariantIds) {
                executor.execute( new VariantIndexingThread( indexList, mapKey, id));

            }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
                System.err.println("Executor timeout in VariantProcessingThread for mapKey " + mapKey);
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Executor interrupted for mapKey " + mapKey);
        }
    }
}
