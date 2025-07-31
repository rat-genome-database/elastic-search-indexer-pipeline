package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VariantProcessingThread implements Runnable{
    private final List<VariantIndex> indexList;
    private int mapKey;


    public VariantProcessingThread(int mapKey,  List<VariantIndex> indexList){
        this.indexList = indexList;
        this.mapKey=mapKey;
    }
    @Override
    public void run() {

        ExecutorService executor = new MyThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        Runnable workerThread= null;
        List<VariantIndex> indexList = new ArrayList<>();

        if (indexList.size() > 0) {
            Set<Long> uniqueVariantIds=indexList.stream().map(VariantIndex::getVariant_id).collect(Collectors.toSet());
            for(long id:uniqueVariantIds) {
                workerThread = new VariantIndexingThread( indexList, mapKey, id);
                executor.execute(workerThread);
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}
