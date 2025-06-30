package edu.mcw.rgd.indexer;


import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.Map;
import edu.mcw.rgd.datamodel.SpeciesType;

import edu.mcw.rgd.indexer.dao.GenomeDAO;
import edu.mcw.rgd.indexer.dao.StrainVariants;
import edu.mcw.rgd.indexer.model.GenomeDataCounts;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.genomeInfo.AssemblyInfo;
import edu.mcw.rgd.indexer.model.genomeInfo.GeneCounts;
import edu.mcw.rgd.indexer.model.genomeInfo.GenomeIndexObject;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Date;
import java.util.List;


/**
 * Created by jthota on 10/24/2017.
 */
public class GenomeInfoThread implements Runnable {

    private int key;
    private String index;
    private final Logger log= LogManager.getLogger("genome");
    MapDAO mapDAO= new MapDAO();
    public GenomeInfoThread(int speciestypeKey, String index, Logger log){

     this.key=speciestypeKey;
       this.index= index;

    }

    @Override
    public void run() {

        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " started " + new Date());
        try {
            String species = SpeciesType.getCommonName(key);
            List<Map> maps = mapDAO.getMaps(key,"bp");
            for (edu.mcw.rgd.datamodel.Map m : maps) {
      //        Map m= mapDAO.getMap(360);
                int mapKey=m.getKey();
                if(mapKey!=6 && mapKey!=36 && mapKey!=8 && mapKey!=21 && mapKey!=19 && mapKey!=7 && mapKey!=900) {
                    GenomeDataCounts counts=new GenomeDataCounts(m,key,null);
                    counts.index();

                }
       }
            log.info(Thread.currentThread().getName() + ": " + species + " End " + new Date());
       }catch (Exception e){
            e.printStackTrace();
            log.info(e);
            throw new RuntimeException();
        }


    }

}
