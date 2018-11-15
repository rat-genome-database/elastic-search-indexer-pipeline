package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.model.genomeInfo.*;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.*;
import java.util.Map;

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
        System.out.println(Thread.currentThread().getName() + ": " + SpeciesType.getCommonName(key) + " || ChromosomeThread MapKey "+mapKey+ " started " + new Date());
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


                    obj.setHumanOrthologs(geneCounts.getHumanOrthologs());
                    obj.setMouseOrthologs(geneCounts.getMouseOrthologs());
                    obj.setRatOrthologs(geneCounts.getRatOrthologs());
                    obj.setChinchillaOrthologs(geneCounts.getChinchillaOrthologs());
                    obj.setDogOrthologs(geneCounts.getDogOrthologs());
                    obj.setSquirrelOrthologs(geneCounts.getSquirrelOrthologs());
                    obj.setBonoboOrthologs(geneCounts.getBonoboOrthologs());
                    obj.setGenesWithoutOrthologs(geneCounts.getGenesWithoutOrthologs());
                    obj.setGenesWithOrthologs(geneCounts.getGenesWithOrthologs());
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
                        String strainVairantMatrix[][] = variants.getStrainVariants(mapKey, c.getChromosome());
                        obj.setVariantsMatrix(strainVairantMatrix);
                    }
                    objects.add(obj);

             }
                if(objects.size()>0){

                    BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk();
                for (ChromosomeIndexObject o : objects) {
                    ObjectMapper mapper = new ObjectMapper();
                    byte[] json = new byte[0];
                    try {
                        json = mapper.writeValueAsBytes(o);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    bulkRequestBuilder.add(new IndexRequest(index, "chromosome").source(json, XContentType.JSON));

                }
                    BulkResponse response=       bulkRequestBuilder.get();
                System.out.println("Indexed mapKey " + mapKey + ",  chromosome objects Size: " + objects.size() + " Exiting thread.");
                System.out.println(Thread.currentThread().getName() + ": chromosomeThread" + mapKey + " End " + new Date());
            }
            }
        }catch (Exception e){
            e.printStackTrace();
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
