package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.dao.spring.CountQuery;

import edu.mcw.rgd.datamodel.*;

import edu.mcw.rgd.indexer.model.genomeInfo.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by jthota on 10/23/2017.
 */
public class GenomeDAO extends AbstractDAO{
    MapDAO mapDAO=new MapDAO();
    public AssemblyStats loadAssemblyStats(String refSeqAccession){
        AssemblyStats stats=new AssemblyStats();
        String API_KEY="119bb78b74c59987962bbf4d55fb4c55de09";
        String baseURI="https://www.ncbi.nlm.nih.gov/datasets/docs/v2/reference-docs/rest-api/";
        String fetchUri="https://api.ncbi.nlm.nih.gov/datasets/v2alpha/genome/accession/" ;
        fetchUri+= refSeqAccession;
        fetchUri+= "/dataset_report?filters.reference_only=true&filters.assembly_source=refseq&filters.has_annotation=true" +
                "&filters.exclude_paired_reports=false&filters.exclude_atypical=false&filters.assembly_version=current" +
                "&filters.assembly_level=chromosome&filters.assembly_level=complete_genome" +
                "&filters.is_metagenome_derived=metagenome_derived_exclude" +
                "&table_fields=assminfo-accession&table_fields=assminfo-name" +
                "&api_key="+API_KEY;
        RestClient restClient=RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(baseURI)
                .build();
        try{
            String responseStr=restClient.get()
                    .uri(fetchUri)
                    .retrieve()
                    .body(String.class);
            if(responseStr!=null){
                JSONObject jsonObject = new JSONObject(responseStr);
                JSONArray array= (JSONArray) jsonObject.get("reports");

                JSONObject object=array.getJSONObject(0);
                JSONObject map= object.getJSONObject("assembly_stats");
                System.out.println("ASSEMBLY STATS:"+ object.get("assembly_stats"));
                stats.setContigL50(String.valueOf( map.get("contig_l50")));
                stats.setContigN50(String.valueOf(map.get("contig_n50")));
              try {
                  stats.setGapsBetweenScaffolds(String.valueOf(map.get("gaps_between_scaffolds")));
              }catch (Exception e){e.printStackTrace();}
                stats.setNumberOfContigs(Integer.parseInt(String.valueOf( map.get("number_of_contigs"))));
                stats.setScaffoldL50(String.valueOf(map.get("scaffold_l50")));
                stats.setScaffoldN50(String.valueOf(map.get("scaffold_n50")));
                stats.setNumberOfScaffolds(Integer.parseInt(String.valueOf(map.get("number_of_scaffolds"))));
                stats.setTotalNumberOfChromosome(Integer.parseInt(map.get("total_number_of_chromosomes").toString()));
                stats.setTotalUngappedLength(String.valueOf(map.get("total_ungapped_length")));
                stats.setTotalSequenceLength(String.valueOf(map.get("total_sequence_length")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return stats;
    }
    public AssemblyInfo getAssemblyInfo(edu.mcw.rgd.datamodel.Map map) throws Exception {

        AssemblyInfo info= new AssemblyInfo();
        int mapKey=map.getKey();
        AssemblyStats stats= mapDAO.getAssemblyStats(mapKey);
        if(stats == null || stats.getTotalSequenceLength()==null){
            if(stats!=null){
                mapDAO.deleteAssemblyStats(mapKey);
            }
            stats=loadAssemblyStats(map.getRefSeqAssemblyAcc());
            stats.setMapKey(mapKey);
            if(stats.getTotalSequenceLength()!=null)
            mapDAO.insertAssemblyStats(stats);
        }
        info.setTotalSeqLength(stats.getTotalSequenceLength());
        info.setTotalUngappedLength(stats.getTotalUngappedLength());
        info.setGapBetweenScaffolds(stats.getGapsBetweenScaffolds());
        info.setScaffolds(String.valueOf(stats.getNumberOfScaffolds()));
        info.setScaffoldN50(stats.getScaffoldN50());
        info.setScaffoldL50(stats.getScaffoldL50());
        info.setContigs(String.valueOf(stats.getNumberOfContigs()));
        info.setContigN50(stats.getContigN50());
        info.setContigL50(stats.getContigL50());
        info.setChromosome(String.valueOf(stats.getTotalNumberOfChromosome()));
        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/"+map.getRefSeqAssemblyAcc()+"/");
        info.setRefSeqAssemblyAccession(map.getRefSeqAssemblyAcc());

        return info;
    }
    public void getPrimaryRef(int speciesTypeKey) throws Exception {

    }
  /*  public GeneCounts  getGeneCounts(int mapKey, int speciesTypeKey, String chr) throws Exception {

        GeneDAO geneDAO= new GeneDAO();
        MapDAO mapDAO= new MapDAO();

        int proteninCoding=0;
        int ncrna=0;
        int trna=0;
        int snrna=0;
        int rrna=0;
        int pseudo=0;

        GeneCounts geneCounts = new GeneCounts();
        List<MappedGene> filteredGenes= new ArrayList<>();
        List<MappedGene> mGenes= geneDAO.getActiveMappedGenes(mapKey);
        System.out.println("MapKey: "+ mapKey + "\t"+ mGenes.size());
        if(chr!=null){
            for(MappedGene m: mGenes){
               if(m.getChromosome().equals(chr)){
                   filteredGenes.add(m);
               }
            }
        }else{
            filteredGenes=mGenes;
        }
        if(filteredGenes.size()>0) {
            for (MappedGene g : filteredGenes) {

                Gene gene = g.getGene();
                String type = gene.getType();

                if (type.equalsIgnoreCase("protein-coding")) {
                    proteninCoding++;
                }
                if (type.equalsIgnoreCase("ncrna")) {
                    ncrna++;
                }
                if (type.equalsIgnoreCase("trna")) {
                    trna++;
                }
                if (type.equalsIgnoreCase("snrna")) {
                    snrna++;
                }
                if (type.equalsIgnoreCase("rrna")) {
                    rrna++;
                }
                if (type.equalsIgnoreCase("pseudo")) {
                    pseudo++;
                }
            }

            geneCounts.setTotalGenes(filteredGenes.size());
            geneCounts.setProteinCoding(proteninCoding);
            geneCounts.setNcrna(ncrna);
            geneCounts.setPseudo(pseudo);
            geneCounts.setrRna(rrna);
            geneCounts.setSnRna(snrna);
            geneCounts.settRna(trna);
            Map<String, Integer> mirTargetCount=  this.getMirnaTargetCount(mapKey, chr);
            System.out.println("MIRTARGE COUNT: "+ mirTargetCount);
            geneCounts.setMirnaTargets(mirTargetCount);


        }
        Map<String, Integer> orthCounts=new HashMap<>();
        orthCounts = this.getOrthologCounts(mapKey, speciesTypeKey, chr);
        System.out.println("ORTHOCOUNTS:"+ orthCounts);
        for(Map.Entry e: orthCounts.entrySet()){
            String key= (String) e.getKey();
            int value= (int) e.getValue();

            if(key.equals("1")){
                geneCounts.setHumanOrthologs(value);
            }
            if(key.equals("2")){
                geneCounts.setMouseOrthologs(value);
            }
            if(key.equals("3")){
                geneCounts.setRatOrthologs(value);
            }
            if(key.equals("4")){
                geneCounts.setChinchillaOrthologs(value);
            }
            if(key.equals("5")){
                geneCounts.setBonoboOrthologs(value);
            }
            if(key.equals("6")){
                geneCounts.setDogOrthologs(value);
            }
            if(key.equals("7")){
                geneCounts.setSquirrelOrthologs(value);
            }
            if(key.equals("9")){
                geneCounts.setSquirrelOrthologs(value);
            }
            if(key.equalsIgnoreCase("withOrthologs")){
                geneCounts.setGenesWithOrthologs(value);
            }
            if(key.equalsIgnoreCase("withOutOrthologs")){
                geneCounts.setGenesWithoutOrthologs(value);
            }
        }
        return geneCounts;

    }*/
    public GeneCounts  getGeneCounts(int mapKey, int speciesTypeKey, String chr) throws Exception {

        GeneDAO geneDAO= new GeneDAO();
        MapDAO mapDAO= new MapDAO();

        int proteninCoding=0;
        int ncrna=0;
        int trna=0;
        int snrna=0;
        int rrna=0;
        int pseudo=0;

        GeneCounts geneCounts = new GeneCounts();
        List<MappedGene> filteredGenes= new ArrayList<>();
        List<MappedGene> mGenes= geneDAO.getActiveMappedGenes(mapKey);
 //       System.out.println("MapKey: "+ mapKey + "\t"+ mGenes.size());
        if(chr!=null){
            for(MappedGene m: mGenes){
                if(m.getChromosome().equals(chr)){
                    filteredGenes.add(m);
                }
            }
        }else{
            filteredGenes=mGenes;
        }
        if(filteredGenes.size()>0) {
            for (MappedGene g : filteredGenes) {

                Gene gene = g.getGene();
                String type = gene.getType();

                if (type.equalsIgnoreCase("protein-coding")) {
                    proteninCoding++;
                }
                if (type.equalsIgnoreCase("ncrna")) {
                    ncrna++;
                }
                if (type.equalsIgnoreCase("trna")) {
                    trna++;
                }
                if (type.equalsIgnoreCase("snrna")) {
                    snrna++;
                }
                if (type.equalsIgnoreCase("rrna")) {
                    rrna++;
                }
                if (type.equalsIgnoreCase("pseudo")) {
                    pseudo++;
                }
            }

            geneCounts.setTotalGenes(filteredGenes.size());
            geneCounts.setProteinCoding(proteninCoding);
            geneCounts.setNcrna(ncrna);
            geneCounts.setPseudo(pseudo);
            geneCounts.setrRna(rrna);
            geneCounts.setSnRna(snrna);
            geneCounts.settRna(trna);
            Map<String, Integer> mirTargetCount=  this.getMirnaTargetCount(mapKey, chr);
      //      System.out.println("MIRTARGE COUNT: "+ mirTargetCount);
            geneCounts.setMirnaTargets(mirTargetCount);


        }
        Map<String, Integer> orthCounts=new HashMap<>();
        orthCounts = this.getOrthologCounts(mapKey, speciesTypeKey, chr);
        geneCounts.setOrthologCountsMap(orthCounts);
     //   System.out.println("ORTHOCOUNTS:"+ orthCounts);

        return geneCounts;

    }
    public Map<String, Integer> getOrthologCounts(int mapKey, int speciesTypeKey, String chr) throws Exception {

        OrthologDAO orthologDAO= new OrthologDAO();
        Map<String, Integer> counts=  orthologDAO.getOrthologCounts(mapKey, speciesTypeKey, chr);
        int genesWithOrthologs= orthologDAO.getGeneCountWithOrthologs(mapKey, speciesTypeKey, chr);
        int genesWithoutOrthologs= orthologDAO.getGeneCountWithOutOrthologs(mapKey, speciesTypeKey, chr);
        counts.put("withOrthologs", genesWithOrthologs);
        counts.put("WithOutOrthologs",genesWithoutOrthologs);
        return counts;

    }

    public Map<String, Integer> getMirnaTargetCount(int mapKey, String chr) throws Exception {
        Map<String, Integer> targetMap=new HashMap<>();
        StatsDAO statsDAO= new StatsDAO();
        targetMap= statsDAO.getMirnaTargetCountsMap(mapKey, chr);

        return targetMap;
    }
    public int getTranscriptsCount(int mapKey, String chr) throws Exception {
        String sql="SELECT count(distinct(ri.rgd_id)) as tot, ro.object_name from rgd_ids ri, rgd_objects ro , maps_data m where ri.object_key = ro.object_key " +
                "AND m.rgd_id=ri.rgd_id and m.map_key=? and ri.object_status = 'ACTIVE' ";
        if(chr!=null){
            sql=sql+" and m.chromosome=?";
        }
        sql=sql+"GROUP BY ro.object_name ORDER BY ro.object_name";
      Connection conn=this.getConnection();
        PreparedStatement preparedStatement= conn.prepareStatement(sql);
        preparedStatement.setInt(1,mapKey);
        if(chr!=null){
            preparedStatement.setString(2, chr);
        }
        ResultSet rs= preparedStatement.executeQuery();
        int count=0;
      while(rs.next()){
          if(rs.getString("object_name").equalsIgnoreCase("transcripts")){
              count=rs.getInt("tot");
              preparedStatement.close();
              rs.close();
              conn.close();
            return count;
          }
      }
        preparedStatement.close();
        rs.close();
        conn.close();
        return count;
    }
    public Map<String, Long> getObjectCounts(int mapKey, String chr) throws Exception {

        String sql="SELECT count(distinct(ri.rgd_id)) as tot, ro.object_name from rgd_ids ri, rgd_objects ro , maps_data m where ri.object_key = ro.object_key " +
                "AND m.rgd_id=ri.rgd_id and m.map_key=? and ri.object_status = 'ACTIVE' ";
       if(chr!=null){
           sql=sql+" and m.chromosome=?";
       }
        sql=sql+"GROUP BY ro.object_name ORDER BY ro.object_name";
        Connection conn=this.getConnection();
        PreparedStatement preparedStatement= conn.prepareStatement(sql);
        preparedStatement.setInt(1,mapKey);
        if(chr!=null){
            preparedStatement.setString(2, chr);
        }
        ResultSet rs= preparedStatement.executeQuery();

        Map<String, Long> objectCounts= new HashMap<>();
        while(rs.next()){
                String name=rs.getString("object_name");
                long value= rs.getLong("tot");
              objectCounts.put(name, value);
        }
        preparedStatement.close();
        rs.close();
        conn.close();

        return objectCounts;
    }
    public Object getOtherObjectsCounts(Object obj, int mapKey, String chr, String objectType) throws Exception {
        GenomeDAO genomeDAO= new GenomeDAO();
        java.util.Map<String, Long> countsMap=   genomeDAO.getObjectCounts(mapKey, chr);
        if(objectType.equalsIgnoreCase("chromosome")){
        ChromosomeIndexObject  chrObject=(ChromosomeIndexObject) obj;
           chrObject.setExons(this.getOtherObjectsCounts(countsMap, "exons"));
            chrObject.setQtls(this.getOtherObjectsCounts(countsMap, "qtls"));
            chrObject.setExons(this.getOtherObjectsCounts(countsMap, "genes"));
            chrObject.setQtls(this.getOtherObjectsCounts(countsMap, "qtls"));
            chrObject.setExons(this.getOtherObjectsCounts(countsMap, "exons"));
            chrObject.setQtls(this.getOtherObjectsCounts(countsMap, "qtls"));
            chrObject.setExons(this.getOtherObjectsCounts(countsMap, "exons"));
            chrObject.setQtls(this.getOtherObjectsCounts(countsMap, "qtls"));

        }
        if(objectType.equalsIgnoreCase("genome")){
         GenomeIndexObject  geneomeObject=(GenomeIndexObject)obj;

    }
        return null;
}
    public long getOtherObjectsCounts(Map<String, Long> countsMap, String type){
        for(java.util.Map.Entry e: countsMap.entrySet()){
            String key= (String) e.getKey();
            Long value= (Long) e.getValue();
            if(key.equalsIgnoreCase(type)){
               return  value;
            }

        }
        return 0;
    }
 public int   getGeneCountsByTermAcc(String acc_id, int mapKey, String aspect, String chr) throws Exception {
        String sql="SELECT COUNT(DISTINCT (f.ANNOTATED_OBJECT_RGD_ID))\n" +
                "  FROM FULL_ANNOT  f,\n" +
                "       GENES       g,\n" +
                "       MAPS_DATA   m,\n" +
                "       RGD_IDS     r,\n" +
                "       full_annot_index i  \n" +
                "        WHERE   f.ASPECT = ? \n" +
                "        AND i.full_annot_key=f.full_annot_key \n" +
                "        AND i.term_acc=? \n" +
                "          AND f.ANNOTATED_OBJECT_RGD_ID = g.RGD_ID \n" +
                "          AND g.RGD_ID = m.RGD_ID \n" +
                "          AND g.RGD_iD = r.RGD_ID \n" +
                "          AND m.MAP_KEY = ? \n" +
                "          AND m.CHROMOSOME = ? \n" +
                "          AND r.OBJECT_STATUS = 'ACTIVE' \n" +
                "          ";
     CountQuery q = new CountQuery(this.getDataSource(), sql);
     List results = this.execute(q, new Object[]{aspect, acc_id, mapKey, chr});
     return ((Integer)results.get(0)).intValue();

    }
    public List<DiseaseGeneObject> getDiseaseGenes(int mapKey, String chr, int speciesTypeKey) throws Exception {
        DiseaseGeneSets d= new DiseaseGeneSets();
        return d.getDiseaseGeneSets(mapKey, chr, speciesTypeKey);
    }

    public int getProteinCounts(int mapKey, String chr) throws Exception {
        ProteinDAO pdao=new ProteinDAO();
       return pdao.getProteinsCount(mapKey, chr);
    }
    public static void main(String[] args) throws Exception {
        GenomeDAO genomeDAO= new GenomeDAO();
        MapDAO dao=new MapDAO();
        edu.mcw.rgd.datamodel.Map  map=dao.getMap(380);
        genomeDAO.getAssemblyInfo(map);

    }
}
