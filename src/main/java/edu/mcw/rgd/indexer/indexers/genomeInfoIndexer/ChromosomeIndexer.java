package edu.mcw.rgd.indexer.indexers.genomeInfoIndexer;


import edu.mcw.rgd.datamodel.Chromosome;
import edu.mcw.rgd.datamodel.Map;
import edu.mcw.rgd.datamodel.SpeciesType;

import edu.mcw.rgd.indexer.model.GenomeDataCounts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ChromosomeIndexer implements Runnable {
    private final int speciesTypeKey;
    private final Chromosome chromosome;
    private final Map map;

    public ChromosomeIndexer(int speciestypeKey, Chromosome chromosome, Map map){
        this.speciesTypeKey=speciestypeKey;
        this.chromosome=chromosome;
        this.map=map;

    }
    @Override
    public void run()  {
        Logger log = LogManager.getLogger("chromosome");
        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(speciesTypeKey) + " || ChromosomeThread MapKey "+map.getKey()+ " started " + new Date());
        GenomeDataCounts counts=new GenomeDataCounts(map, map.getSpeciesTypeKey(), chromosome);
        try {
            counts.index();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
