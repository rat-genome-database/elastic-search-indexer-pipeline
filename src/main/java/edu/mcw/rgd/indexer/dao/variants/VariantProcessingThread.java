package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class VariantProcessingThread implements Runnable{
    private List<Integer> variantIds;
    private int mapKey;

    VariantDao variantDao=new VariantDao();
    public VariantProcessingThread(int mapKey, List<Integer> variantIds){
        this.variantIds=variantIds;
        this.mapKey=mapKey;
    }
    @Override
    public void run() {

        ExecutorService executor = new MyThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        Runnable workerThread= null;
        List<VariantIndex> indexList = new ArrayList<>();
        if(variantIds.size()>0)
        try {

            indexList = variantDao.getVariantsNewTbaleStructure(mapKey, variantIds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   workerThread = new ProcessPartChromosome(list,mapKey);
        if (indexList.size() > 0) {
            workerThread = new VariantIndexingThread(indexList, mapKey, variantIds);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}
