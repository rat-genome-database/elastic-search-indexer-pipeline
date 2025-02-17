package edu.mcw.rgd.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.indexer.dao.GenomeDAO;
import edu.mcw.rgd.indexer.dao.StrainVariants;
import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.genomeInfo.ChromosomeIndexObject;
import edu.mcw.rgd.indexer.model.genomeInfo.DiseaseGeneObject;
import edu.mcw.rgd.indexer.model.genomeInfo.GeneCounts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;

import java.util.Date;
import java.util.List;


public class ChromosomeMapDataThread implements Runnable {

    private Map m;
    private int speciesTypeKey;
    private List<MappedGene> mappedGenes;
    private Chromosome c;
    private BulkRequest bulkRequest;
    private ObjectMapper mapper;

//    List<Chromosome>   chromosomes ;
   private List<TermWithStats> topLevelDiseaseTerms;

   private String[][] strainVairantMatrix;
    GenomeDAO genomeDAO=new GenomeDAO();

    public ChromosomeMapDataThread(int speciesTypeKey, Map m,List<MappedGene> mappedGenes,  List<TermWithStats> topLevelDiseaseTerms, Chromosome c,BulkRequest bulkRequest, ObjectMapper mapper ,String[][] strainVairantMatrix) {
//        this.chromosomes=chromosomes;
        this.m = m;
        this.speciesTypeKey = speciesTypeKey;
        this.mappedGenes=mappedGenes;
        this.topLevelDiseaseTerms=topLevelDiseaseTerms;
        this.c=c;
        this.bulkRequest=bulkRequest;
        this.mapper=mapper;
        this.strainVairantMatrix=strainVairantMatrix;
    }

    Logger log = LogManager.getLogger("chromosomeMapDataThread");

    @Override
    public void run() {
        int mapKey = m.getKey();

//        ExecutorService executor = new MyThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        //   for (Map m : maps) {

        String assembly = m.getName();
//        if (mapKey != 6 && mapKey != 36 && mapKey != 8 && mapKey != 21 && mapKey != 19 && mapKey != 7 &&
//                mapKey != 720 && mapKey != 44 && mapKey != 722 && mapKey != 1313 && mapKey != 1410 && mapKey != 1701 && mapKey != 514) {
                try {


              //      for (Chromosome c : chromosomes) {
//                        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " ||  MapKey " + mapKey + " CHR-"+ c.getChromosome()+" started " + new Date());
//
                        GeneCounts geneCounts = null;
                        try {
                            geneCounts = genomeDAO.getGeneCounts( mapKey, speciesTypeKey,c.getChromosome(), mappedGenes);
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
                            diseaseGenes = genomeDAO.getDiseaseGenes(mapKey, c.getChromosome(), speciesTypeKey,topLevelDiseaseTerms);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

//                        Runnable workerThread = new ChromosomeThread(c, key, RgdIndex.getNewAlias(), mapKey, assembly);
//                       ChromosomeThread chromosomeThread = new ChromosomeThread(c, key, RgdIndex.getNewAlias(), mapKey, assembly, geneCounts, objectsCountsMap, diseaseGenes, strainVairantMatrix);
//                        chromosomeThread.run();


                    ChromosomeIndexObject indexObject=  buildIndexObject(c, speciesTypeKey, RgdIndex.getNewAlias(), mapKey, assembly, geneCounts, objectsCountsMap, diseaseGenes, strainVairantMatrix);




                    try {
                        String   json = mapper.writeValueAsString(indexObject);
                        IndexRequest indexRequest=new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON);
                        bulkRequest.add(indexRequest);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }



                    //   }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


          //  }
            // }


        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(speciesTypeKey) + " || Mapkey-"+mapKey+"-CHR-"+c.getChromosome()+ " END " + new Date());

//        executor.shutdown();
//        while(!executor.isTerminated()){}
    }
    public ChromosomeIndexObject buildIndexObject(Chromosome c, int speciestypeKey, String index, int mapKey, String assembly, GeneCounts geneCounts, java.util.Map<String, Long> objectsCountsMap, List<DiseaseGeneObject> diseaseGenes, String[][] strainVairantMatrix) throws Exception {
        ChromosomeIndexObject obj = new ChromosomeIndexObject();
        obj.setMapKey(mapKey);
        obj.setChromosome(c.getChromosome());
        obj.setAssembly(assembly);
        obj.setRefseqId(c.getRefseqId());
        obj.setGenbankId(c.getGenbankId());
        obj.setSeqLength(c.getSeqLength());
        obj.setGapLength(c.getGapLength());
        obj.setGapCount(c.getGapCount());
        obj.setContigCount(c.getContigCount());

        obj.setProteinCoding(geneCounts.getProteinCoding());
        obj.setNcrna(geneCounts.getNcrna());
        obj.settRna(geneCounts.gettRna());
        obj.setSnRna(geneCounts.getSnRna());
        obj.setrRna(geneCounts.getrRna());
        obj.setPseudo(geneCounts.getPseudo());

        java.util.Map<String, Integer> mirnaTargets = geneCounts.getMirnaTargets();
        if (mirnaTargets != null) {
            for (java.util.Map.Entry entry : mirnaTargets.entrySet()) {
                String targetType = (String) entry.getKey();
                int value = (int) entry.getValue();
                if (targetType.equalsIgnoreCase("confirmed")) {
                    obj.setMirnaTargetsConfirmed(value);
                }
                if (targetType.equalsIgnoreCase("predicted")) {
                    obj.setMirnaTargetsPredicted(value);
                }
            }
        }
        if(geneCounts!=null) {
            java.util.Map<String, Integer> orthoCounts = geneCounts.getOrthologCountsMap();
            if (orthoCounts.get("1") != null) {
                obj.setHumanOrthologs(orthoCounts.get("1"));
            }
            if (orthoCounts.get("2") != null) {
                obj.setMouseOrthologs(orthoCounts.get("2"));
            }
            if (orthoCounts.get("3") != null) {
                obj.setRatOrthologs(orthoCounts.get("3"));
            }
            if (orthoCounts.get("4") != null) {
                obj.setChinchillaOrthologs(orthoCounts.get("4"));
            }
            if (orthoCounts.get("6") != null) {
                obj.setDogOrthologs(orthoCounts.get("6"));
            }
            if (orthoCounts.get("7") != null) {
                obj.setSquirrelOrthologs(orthoCounts.get("7"));
            }
            if (orthoCounts.get("5") != null) {
                obj.setBonoboOrthologs(orthoCounts.get("5"));
            }
            if (orthoCounts.get("9") != null) {
                obj.setPigOrthologs(orthoCounts.get("9"));
            }

            obj.setGenesWithoutOrthologs(orthoCounts.get("WithOutOrthologs"));
            obj.setGenesWithOrthologs(orthoCounts.get("withOrthologs"));

            StringBuffer pieData = this.getPieData(geneCounts);
            obj.setPieData(pieData);
        }
        if(objectsCountsMap!=null) {
            obj.setExons(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "exons"));
            obj.setQtls(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "qtls"));
            obj.setTotalGenes((int) genomeDAO.getOtherObjectsCounts(objectsCountsMap, "genes"));
            obj.setSslps(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "sslps"));
            obj.setUtrs3(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "3utrs"));
            obj.setUtrs5(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "5utrs"));
            obj.setStrains(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "strains"));
            obj.setTranscripts(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "transcripts"));
            obj.setVariants(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "variants"));
        }

        obj.setProteinsCount(genomeDAO.getProteinCounts(mapKey, c.getChromosome()));
        if(diseaseGenes!=null) {
            obj.setDiseaseGenes(diseaseGenes);
            obj.setDiseaseGenechartData(this.getDiseaseGeneChartData(diseaseGenes));
        }
        //ADD STRAIN VARIANTS IF SPECIES_TYPE_KEY=3 (RAT SPECIES)
        if(speciesTypeKey==3 && (mapKey==372 || mapKey==360 || mapKey==70 || mapKey==60)) {
            obj.setVariantsMatrix(strainVairantMatrix);
        }
//                    indexObject(obj);
//                    BulkIndexProcessor.bulkProcessor.add(new IndexRequest(index, "chromosome").source(obj, XContentType.JSON));

        // }
//              log.info("Indexed mapKey " + mapKey + ",  chromosome objects Size: " + objects.size() + " Exiting thread.");
//              log.info(Thread.currentThread().getName() + ":" + mapKey +"-CHR-"+c.getChromosome() + " End " + new Date());
//            }
        //  }
        return obj;
    }


    public StringBuffer getDiseaseGeneChartData(List<DiseaseGeneObject> diseaseGenes){
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (DiseaseGeneObject d : diseaseGenes) {
            if (d.getGeneCount() > 0)
                sb.append("{\"disease\":\"" + d.getOntTerm() + "\", \"geneCount\":" + d.getGeneCount() + "},");
        }
        sb.append("]");

        return sb;
    }
    public StringBuffer getPieData(GeneCounts geneCounts){
        StringBuffer pieData= new StringBuffer();
        pieData.append("[{\"label\":\"protein-coding\", \"value\":"+geneCounts.getProteinCoding()+"}," );
        if(geneCounts.getNcrna()!=0)
            pieData.append("{\"label\":\"ncrna\", \"value\":" + geneCounts.getNcrna()+"}," );
        if(geneCounts.gettRna()!=0)
            pieData.append("{\"label\":\"tRna\", \"value\":" +geneCounts.gettRna()+"}," );
        if(geneCounts.getrRna()!=0)
            pieData.append("{\"label\":\"rrna\", \"value\":" + geneCounts.getrRna()+"}," );
        if(geneCounts.getPseudo()!=0)
            pieData.append("{\"label\":\"pseudo\", \"value\":" +geneCounts.getPseudo()+"}," );
        if(geneCounts.getSnRna()!=0)
            pieData.append("{\"label\":\"ncrna\", \"value\":" + geneCounts.getSnRna()+"}");
        pieData.append("]");
        return pieData;
    }
}

