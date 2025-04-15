package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.Chromosome;
import edu.mcw.rgd.datamodel.Map;
import edu.mcw.rgd.datamodel.RgdIndex;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.ChromosomeThread;
import edu.mcw.rgd.indexer.dao.GenomeDAO;
import edu.mcw.rgd.indexer.dao.StrainVariants;
import edu.mcw.rgd.indexer.model.genomeInfo.DiseaseGeneObject;
import edu.mcw.rgd.indexer.model.genomeInfo.GeneCounts;
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
    MapDAO mapDAO = new MapDAO();

    GenomeDAO genomeDAO=new GenomeDAO();
    StrainVariants variants=new StrainVariants();

    public ChromosomeMapDataThread(int key, Map m) {
        this.m = m;
        this.key = key;
    }

    Logger log = LogManager.getLogger("chromosomeMapDataThread");

    @Override
    public void run() {
        int mapKey = m.getKey();
//     log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || ChromosomeThread MapKey "+key+ " started " + new Date());
        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || Mapkey- "+mapKey+ " STARTED " + new Date());

        ExecutorService executor = new MyThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        //   for (Map m : maps) {

        String assembly = m.getName();
        if (mapKey != 6 && mapKey != 36 && mapKey != 8 && mapKey != 21 && mapKey != 19 && mapKey != 7) {
            if (mapKey != 720 && mapKey != 44 && mapKey != 722 && mapKey != 1313 && mapKey != 1410 && mapKey != 1701 && mapKey != 514) {
                try {
                    List<Chromosome>   chromosomes = mapDAO.getChromosomes(mapKey);

                    for (Chromosome c : chromosomes) {
//                        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " ||  MapKey " + mapKey + " CHR-"+ c.getChromosome()+" started " + new Date());
//
                        GeneCounts geneCounts = null;
                        try {
                            geneCounts = genomeDAO.getGeneCounts(mapKey, key, c.getChromosome());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        java.util.Map<String, Long> objectsCountsMap = null;
                        try {
                            objectsCountsMap = genomeDAO.getObjectCounts(mapKey, c.getChromosome());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        List<DiseaseGeneObject> diseaseGenes = null;
                        try {
                            diseaseGenes = genomeDAO.getDiseaseGenes(mapKey, c.getChromosome(), key);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        String[][] strainVairantMatrix = null;
                        if (key == 3) {
                            try {
                                strainVairantMatrix = variants.getStrainVariants(mapKey, c.getChromosome());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
//                        Runnable workerThread = new ChromosomeThread(c, key, RgdIndex.getNewAlias(), mapKey, assembly);
                        Runnable workerThread = new ChromosomeThread(c, key, RgdIndex.getNewAlias(), mapKey, assembly, geneCounts, objectsCountsMap, diseaseGenes, strainVairantMatrix);
                        executor.execute(workerThread);
                    }

                } catch (Exception e) {}


            }
            // }

        }
        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || Mapkey- "+mapKey+ " END " + new Date());

        executor.shutdown();
        while(!executor.isTerminated()){}
    }
}

