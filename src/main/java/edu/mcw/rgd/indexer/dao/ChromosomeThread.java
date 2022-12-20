package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.model.genomeInfo.*;

import edu.mcw.rgd.services.ClientInit;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;

import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.*;

/**
 * Created by jthota on 11/7/2017.
 */
public class ChromosomeThread implements  Runnable {
 //   private Thread t;
    private int key;
    private int mapKey;
    private String assembly;
    private String index;
    private GenomeDAO genomeDAO= new GenomeDAO();

    public ChromosomeThread(int speciestypeKey, String index, int mapKey, String assembly){
        this.key=speciestypeKey;
        this.index= index;
        this.mapKey=mapKey;
        this.assembly=assembly;
    }

    @Override
    public void run()  {
        Logger log = Logger.getLogger("chromosome");
        log.info(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || ChromosomeThread MapKey "+mapKey+ " started " + new Date());
        MapDAO mapDAO= new MapDAO();
        GenomeDAO genomeDAO= new GenomeDAO();
        StrainVariants variants= new StrainVariants();
        List<ChromosomeIndexObject> objects= new ArrayList<>();
        try {
            if(mapKey!=720 && mapKey!=44) {
                List<Chromosome> chromosomes = mapDAO.getChromosomes(mapKey);

              for (Chromosome c : chromosomes) {
                 //   Chromosome c=   mapDAO.getChromosome(360, "1");
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

                    GeneCounts geneCounts = genomeDAO.getGeneCounts(mapKey, key, c.getChromosome());
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
                  java.util.Map<String, Integer> orthoCounts=geneCounts.getOrthologCountsMap();
                  if(orthoCounts.get("1")!=null){obj.setHumanOrthologs(orthoCounts.get("1")); }
                  if(orthoCounts.get("2")!=null){obj.setMouseOrthologs(orthoCounts.get("2"));}
                  if(orthoCounts.get("3")!=null) {obj.setRatOrthologs(orthoCounts.get("3"));}
                  if(orthoCounts.get("4")!=null) {obj.setChinchillaOrthologs(orthoCounts.get("4"));}
                  if(orthoCounts.get("6")!=null){    obj.setDogOrthologs(orthoCounts.get("6"));}
                  if(orthoCounts.get("7")!=null){    obj.setSquirrelOrthologs(orthoCounts.get("7"));}
                  if(orthoCounts.get("5")!=null){    obj.setBonoboOrthologs(orthoCounts.get("5"));}
                  if(orthoCounts.get("9")!=null){    obj.setPigOrthologs(orthoCounts.get("9"));}

                  obj.setGenesWithoutOrthologs(orthoCounts.get("WithOutOrthologs"));
                  obj.setGenesWithOrthologs(orthoCounts.get("withOrthologs"));

                    StringBuffer pieData = this.getPieData(geneCounts);
                    obj.setPieData(pieData);


                    java.util.Map<String, Long> objectsCountsMap = genomeDAO.getObjectCounts(mapKey, c.getChromosome());
                    obj.setExons(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "exons"));
                    obj.setQtls(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "qtls"));
                    obj.setTotalGenes((int) genomeDAO.getOtherObjectsCounts(objectsCountsMap, "genes"));
                    obj.setSslps(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "sslps"));
                    obj.setUtrs3(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "3utrs"));
                    obj.setUtrs5(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "5utrs"));
                    obj.setStrains(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "strains"));
                    obj.setTranscripts(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "transcripts"));
                    obj.setVariants(genomeDAO.getOtherObjectsCounts(objectsCountsMap, "variants"));

                   obj.setProteinsCount(genomeDAO.getProteinCounts(mapKey, c.getChromosome()));

                    List<DiseaseGeneObject> diseaseGenes = genomeDAO.getDiseaseGenes(mapKey, c.getChromosome(), key);
                    obj.setDiseaseGenes(diseaseGenes);
                    obj.setDiseaseGenechartData(this.getDiseaseGeneChartData(diseaseGenes));

                    //ADD STRAIN VARIANTS IF SPECIES_TYPE_KEY=3 (RAT SPECIES)
                    if(key==3) {
                        String[][] strainVairantMatrix = variants.getStrainVariants(mapKey, c.getChromosome());
                        obj.setVariantsMatrix(strainVairantMatrix);
                    }
                    objects.add(obj);

             }
                if(objects.size()>0){


                    BulkRequest bulkRequest=new BulkRequest();
                    bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

                    int docCount=1;

                for (ChromosomeIndexObject o : objects) {
                        docCount++;
                        ObjectMapper mapper = new ObjectMapper();
                        byte[] json = new byte[0];
                        try {
                            json = mapper.writeValueAsBytes(o);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                   //   bulkRequestBuilder.add(new IndexRequest(index, "chromosome").source(json, XContentType.JSON));
                    bulkRequest.add(new IndexRequest(index).source(json, XContentType.JSON));
                        if(docCount%100==0){
                          /*  BulkResponse response=       bulkRequestBuilder.execute().get();
                            bulkRequestBuilder= ESClient.getClient().prepareBulk();*/
                            BulkResponse response=      ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                            bulkRequest= new BulkRequest();
                        }else{
                            if(docCount>objects.size()-100 && docCount==objects.size()){
                                /*BulkResponse response=       bulkRequestBuilder.execute().get();
                                bulkRequestBuilder= ESClient.getClient().prepareBulk();*/
                                BulkResponse response=      ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                                bulkRequest= new BulkRequest();
                            }
                        }

                }
                    RefreshRequest refreshRequest=new RefreshRequest();
                    ClientInit.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
                log.info("Indexed mapKey " + mapKey + ",  chromosome objects Size: " + objects.size() + " Exiting thread.");
                log.info(Thread.currentThread().getName() + ": chromosomeThread" + mapKey + " End " + new Date());
            }
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info(e);
            throw new RuntimeException();
        }

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
