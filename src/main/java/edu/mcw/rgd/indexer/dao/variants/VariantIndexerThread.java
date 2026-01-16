package edu.mcw.rgd.indexer.dao.variants;




import java.util.Collection;
import java.util.List;


public class VariantIndexerThread implements Runnable {
    private final String chr;
    private final int mapKey;
    private final int speciesTypeKey;
    VariantDao variantDao=new VariantDao();
    public VariantIndexerThread(String chr, int mapKey, int speciesTypeKey){
        this.chr=chr;
        this.mapKey=mapKey;
        this.speciesTypeKey=speciesTypeKey;
    }
    @Override
    public void run() {
//        Logger log = LogManager.getLogger("variant");
//        ExecutorService executor2 = new MyThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
//        Runnable variantsNewTableThread= null;
//        List<Integer> variantIds = null;
//        try {
////            variantIds = variantDao.getUniqueVariantsIds(chr, mapKey, speciesTypeKey);
////            System.out.println("VariantIds Size:"+ variantIds.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(mapKey) + " ||  MapKey: "+mapKey+ " || CHROMOSOME: "+chr+ " started " + new Date());
//
//        if(variantIds!=null) {
//            Collection[] collections = new Collection[0];
//            try {
//                collections = split(variantIds, 1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            for (int i = 0; i < collections.length; i++) {
//                variantsNewTableThread = new VariantProcessingThread(mapKey, (List<Integer>) collections[i]);
//                executor2.execute(variantsNewTableThread);
//            }
//        }
//        executor2.shutdown();
//        while (!executor2.isTerminated()) {}
    }
    public Collection[] split(List<Integer> rgdids, int size) throws Exception {
        int numOfBatches = rgdids.size() / size + 1;
        Collection[] batches = new Collection[numOfBatches];

        for(int index = 0; index < numOfBatches; ++index) {
            int count = index + 1;
            int fromIndex = Math.max((count - 1) * size, 0);
            int toIndex = Math.min(count * size, rgdids.size());
            batches[index] = rgdids.subList(fromIndex, toIndex);
        }

        return batches;
    }
}
