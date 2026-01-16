package edu.mcw.rgd.indexer.indexers.genomeInfoIndexer;

import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.Chromosome;
import edu.mcw.rgd.datamodel.Map;
import edu.mcw.rgd.datamodel.SpeciesType;

import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.model.GenomeDataCounts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ChromosomeMapDataThread implements Runnable {

    private Map m;
    private int key;
    MapDAO mapDAO=new MapDAO();

    public ChromosomeMapDataThread(int key, Map m) {
        this.m = m;
        this.key = key;
    }

    private final Logger log = LogManager.getLogger("chromosomeMapDataThread");

    @Override
    public void run() {
        int mapKey = m.getKey();
        if (mapKey != 6 && mapKey != 36 && mapKey != 8 && mapKey != 21 && mapKey != 19 && mapKey != 7
                && mapKey != 720 && mapKey != 44 && mapKey != 722 && mapKey != 1313 && mapKey != 1410  && mapKey != 514) {
//            ExecutorService executor = new MyThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            try {
                List<Chromosome> chromosomes = mapDAO.getChromosomes(mapKey);

                for (Chromosome c : chromosomes) {
//                Chromosome c=mapDAO.getChromosome(m.getKey(), "12");
                    log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " ||  MapKey " + mapKey + " CHR-" + c.getChromosome() + " started " + new Date());
//                    Runnable workerThread = new ChromosomeIndexer(key, c, m);
//                    executor.execute(workerThread);
                    GenomeDataCounts counts=new GenomeDataCounts(m, m.getSpeciesTypeKey(), c);
                    try {
                        counts.index();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

//                executor.shutdown();
//                while (!executor.isTerminated()) {}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

