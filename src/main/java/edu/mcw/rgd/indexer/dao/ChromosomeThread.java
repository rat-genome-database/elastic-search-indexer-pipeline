package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.genomeInfo.*;

import edu.mcw.rgd.services.ClientInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.index.IndexRequest;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by jthota on 11/7/2017.
 */
public class ChromosomeThread  {
 //   private Thread t;
    private int key;
    private int mapKey;
    private String assembly;
    private String index;
    private GenomeDAO genomeDAO= new GenomeDAO();
    private Chromosome c;
    private GeneCounts geneCounts;
    private java.util.Map<String, Long> objectsCountsMap ;
    private List<DiseaseGeneObject> diseaseGenes ;
    private String[][] strainVairantMatrix ;


    ObjectMapper mapper = new ObjectMapper();
    public ChromosomeThread(Chromosome c, int speciestypeKey, String index, int mapKey, String assembly, GeneCounts geneCounts, Map<String, Long> objectsCountsMap,List<DiseaseGeneObject> diseaseGenes, String[][] strainVairantMatrix){
//          public ChromosomeThread(Chromosome c, int speciestypeKey, String index, int mapKey, String assembly){

            this.key=speciestypeKey;
        this.index= index;
        this.mapKey=mapKey;
        this.assembly=assembly;
        this.c=c;
        this.geneCounts=geneCounts;
        this.objectsCountsMap=objectsCountsMap;
        this.diseaseGenes=diseaseGenes;
        this.strainVairantMatrix=strainVairantMatrix;
    }


    public void run()  {
//        Logger log = LogManager.getLogger("chromosome");
//        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || ChromosomeThread MapKey "+mapKey+ " started " + new Date());
        try {
//            log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || " + mapKey + " -CHR-"+ c.getChromosome()+" started " + new Date());

//            GeneCounts geneCounts = null;
//            try {
//                geneCounts = genomeDAO.getGeneCounts(mapKey, key, c.getChromosome());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            java.util.Map<String, Long> objectsCountsMap = null;
//            try {
//                objectsCountsMap = genomeDAO.getObjectCounts(mapKey, c.getChromosome());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            List<DiseaseGeneObject> diseaseGenes = null;
//            try {
//                diseaseGenes = genomeDAO.getDiseaseGenes(mapKey, c.getChromosome(), key);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            String[][] strainVairantMatrix = null;
//            if (key == 3) {
//                try {
//                    strainVairantMatrix = variants.getStrainVariants(mapKey, c.getChromosome());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }

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
                    if(key==3 && (mapKey==372 || mapKey==70 || mapKey==60)) {
                        obj.setVariantsMatrix(strainVairantMatrix);
                    }
//                    indexObject(obj);
//                    BulkIndexProcessor.bulkProcessor.add(new IndexRequest(index, "chromosome").source(obj, XContentType.JSON));

            // }
//              log.info("Indexed mapKey " + mapKey + ",  chromosome objects Size: " + objects.size() + " Exiting thread.");
//              log.info(Thread.currentThread().getName() + ":" + mapKey +"-CHR-"+c.getChromosome() + " End " + new Date());
//            }
          //  }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }


    }
    public void indexObject(ChromosomeIndexObject o) throws ExecutionException, InterruptedException, IOException {

        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


            byte[] json = new byte[0];
            try {
                json = mapper.writeValueAsBytes(o);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
//            BulkIndexProcessor.bulkProcessor.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));

        IndexRequest indexRequest=new IndexRequest(index).source(json, XContentType.JSON);
        ClientInit.getClient().index(indexRequest, RequestOptions.DEFAULT);
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
