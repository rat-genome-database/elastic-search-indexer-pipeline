package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.Map;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.client.ESClient;

import edu.mcw.rgd.indexer.model.genomeInfo.AssemblyInfo;
import edu.mcw.rgd.indexer.model.genomeInfo.GeneCounts;
import edu.mcw.rgd.indexer.model.genomeInfo.GenomeIndexObject;

import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;

import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.client.Requests.refreshRequest;


/**
 * Created by jthota on 10/24/2017.
 */
public class GenomeInfoThread implements Runnable {

    private Thread t;
    private int key;
    private int mapKey;
    private String index;
    private Logger log;

    public GenomeInfoThread(int speciestypeKey, String index, Logger log){

     this.key=speciestypeKey;
       this.index= index;
        this.log=log;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " started " + new Date());
        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " started " + new Date());

      MapDAO mapDAO= new MapDAO();
        GenomeDAO genomeDAO= new GenomeDAO();
        StrainVariants variants= new StrainVariants();
        List<GenomeIndexObject> objects= new ArrayList<>();
     try {


         String species = SpeciesType.getCommonName(key);
            List<Map> maps = mapDAO.getMaps(key,"bp");
        for (edu.mcw.rgd.datamodel.Map m : maps) {
           //   Map m= mapDAO.getMap(360);

                int mapKey=m.getKey();
                if(mapKey!=6 && mapKey!=36 && mapKey!=8 && mapKey!=21 && mapKey!=19 && mapKey!=7 && mapKey!=900) {
              //  System.out.println(key + " || " + mapKey);
                GenomeIndexObject obj = new GenomeIndexObject();
                obj.setSpecies(species);
                AssemblyInfo info = new AssemblyInfo();
                info = genomeDAO.getAssemblyInfo(key, mapKey);
                //    info=genomeDAO.getAssemblyInfo(3, 70);
                obj.setBasePairs(info.getBasePairs());
                obj.setTotalLength(info.getTotalLength());
                obj.setGapLength(info.getGapLength());
                obj.setGapBetweenScaffolds(info.getGapBetweenScaffolds());
                obj.setScaffolds(info.getScaffolds());
                obj.setScaffoldN50(info.getScaffoldN50());
                obj.setScaffoldL50(info.getScaffoldL50());
                obj.setContigs(info.getContigs());
                obj.setContigN50(info.getContigN50());
                obj.setContigL50(info.getContigL50());
                obj.setChromosomes(info.getChromosome());
                obj.setNcbiLink(info.getNcbiLink());
                obj.setRefSeqAssemblyAccession(info.getRefSeqAssemblyAccession());
                obj.setMapKey(mapKey);
                if(m.isPrimaryRefAssembly())
                obj.setPrimaryAssembly("Y");
                else
                obj.setPrimaryAssembly("N");
                //      obj.setMapKey(70);
                obj.setAssembly(m.getName());
                GeneCounts geneCounts = new GeneCounts();
                geneCounts = genomeDAO.getGeneCounts(mapKey, key, null);
                // geneCounts= genomeDAO.getGeneCounts(70);
             //   obj.setTotalGenes(geneCounts.getTotalGenes());
                obj.setProteinCoding(geneCounts.getProteinCoding());
                obj.setNcrna(geneCounts.getNcrna());
                obj.settRna(geneCounts.gettRna());
                obj.setSnRna(geneCounts.getSnRna());
                obj.setrRna(geneCounts.getrRna());
                obj.setPseudo(geneCounts.getPseudo());
              //  obj.setTranscripts(geneCounts.getTranscripts());
                obj.setHumanOrthologs(geneCounts.getHumanOrthologs());
                obj.setMouseOrthologs(geneCounts.getMouseOrthologs());
                obj.setRatOrthologs(geneCounts.getRatOrthologs());
                obj.setChinchillaOrthologs(geneCounts.getChinchillaOrthologs());
                obj.setDogOrthologs(geneCounts.getDogOrthologs());
                obj.setSquirrelOrthologs(geneCounts.getSquirrelOrthologs());
                obj.setBonoboOrthologs(geneCounts.getBonoboOrthologs());
                obj.setGenesWithoutOrthologs(geneCounts.getGenesWithoutOrthologs());
                obj.setGenesWithOrthologs(geneCounts.getGenesWithOrthologs());

                obj.setProteinsCount(genomeDAO.getProteinCounts(mapKey, null));

                java.util.Map<String, Integer> mirnaTargets = geneCounts.getMirnaTargets();
                if(mirnaTargets!=null) {
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
                java.util.Map<String, Long> countsMap = genomeDAO.getObjectCounts(mapKey, null);
                for(java.util.Map.Entry e: countsMap.entrySet()){
                    String key= (String) e.getKey();
                    Long value= (Long) e.getValue();
                    if(key.equalsIgnoreCase("exons")){
                        obj.setExons(value);
                    }
                    if(key.equalsIgnoreCase("qtls")){
                        obj.setQtls(value);
                    }
                    if(key.equalsIgnoreCase("transcripts")){
                        obj.setTranscripts(value);
                    }
                    if(key.equalsIgnoreCase("sslps")){
                        obj.setSslps(value);
                    }
                    if(key.equalsIgnoreCase("strains")){
                        obj.setStrains(value);
                    }
                    if(key.equalsIgnoreCase("qtls")){
                        obj.setQtls(value);
                    }
                    if(key.equalsIgnoreCase("3utrs")){
                        obj.setUtrs3(value);
                    }
                    if(key.equalsIgnoreCase("5utrs")){
                        obj.setUtrs5(value);
                    }
                    if(key.equalsIgnoreCase("genes")){
                        obj.setTotalGenes(value);
                    }
                    if(key.equalsIgnoreCase("variants")){
                        obj.setVariants(value);
                    }
                }
                if (key == 3) {
                    if (mapKey == 360 || mapKey == 70 || mapKey == 60) {
                        String strainVairantMatrix[][] = variants.getStrainVariants(mapKey, null);
                        obj.setVariantsMatrix(strainVairantMatrix);
                    }
                }

                objects.add(obj);
          }
       }
            System.out.println("Objects List Size of " + species + " : " + objects.size());
           log.info("Objects List Size of " + species + " : " + objects.size());
         BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
         int docCount=0;
            for (GenomeIndexObject o : objects) {
                docCount++;
                ObjectMapper mapper = new ObjectMapper();
                byte[] json = new byte[0];
                try {
                    json = mapper.writeValueAsBytes(o);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                bulkRequestBuilder.add(new IndexRequest(index, "genome").source(json, XContentType.JSON));

                if(docCount%100==0){
                    BulkResponse response=       bulkRequestBuilder.execute().get();
                    bulkRequestBuilder= ESClient.getClient().prepareBulk();
                }else{
                    if(docCount>objects.size()-100 && docCount==objects.size()){
                        BulkResponse response=       bulkRequestBuilder.execute().get();
                        bulkRequestBuilder= ESClient.getClient().prepareBulk();
                    }
                }

            }
          //   BulkResponse response=       bulkRequestBuilder.get();
           ESClient.getClient().admin().indices().refresh(refreshRequest()).actionGet();
            System.out.println("Indexed " + species + "  genome objects Size: " + objects.size() + " Exiting thread.");
            System.out.println(Thread.currentThread().getName() + ": " + species + " End " + new Date());
            log.info("Indexed " + species + "  genome objects Size: " + objects.size() + " Exiting thread.");
           log.info(Thread.currentThread().getName() + ": " + species + " End " + new Date());
       }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }


    }

}
