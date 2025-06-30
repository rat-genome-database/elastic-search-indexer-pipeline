package edu.mcw.rgd.indexer.dao;


import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.dao.spring.CountQuery;

import edu.mcw.rgd.datamodel.*;


import edu.mcw.rgd.indexer.model.genomeInfo.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.Map;


/**
 * Created by jthota on 10/23/2017.
 */
public class GenomeDAO extends AbstractDAO{
   public MapDAO mapDAO=new MapDAO();

  public   GeneDAO geneDAO=new GeneDAO();
  public  ProteinDAO pdao=new ProteinDAO();
  public   StrainVariants variants= new StrainVariants();
  public OntologyXDAO ontologyXDAO=new OntologyXDAO();
    public AssemblyStats loadAssemblyStats(String refSeqAccession){
        System.out.println("loading ..."+ refSeqAccession);
        AssemblyStats stats=new AssemblyStats();
        String API_KEY="6994481f9c0f101d5469a91e9b7b6017dc08";
        System.out.println("NCBI API KEY:"+ API_KEY);
        String baseURI="https://www.ncbi.nlm.nih.gov/datasets/docs/v2/reference-docs/rest-api/";
        String fetchUri="https://api.ncbi.nlm.nih.gov/datasets/v2alpha/genome/accession/" ;
        fetchUri+= refSeqAccession.trim();


        fetchUri+="/dataset_report?filters.reference_only=false&filters.assembly_source=all&filters.has_annotation=false&filters.exclude_paired_reports=false&filters.exclude_atypical=false&filters.assembly_version=all_assemblies&filters.is_metagenome_derived=metagenome_derived_exclude&page_size=1000";
       System.out.println("FETCH URI:"+ fetchUri);
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
                System.out.println("JSONOBJECT"+ jsonObject.toString());
                JSONArray array= (JSONArray) jsonObject.get("reports");

                JSONObject object=array.getJSONObject(0);
                JSONObject map= object.getJSONObject("assembly_stats");
                stats.setContigL50(String.valueOf( map.get("contig_l50")));
                stats.setContigN50(String.valueOf(map.get("contig_n50")));
              try {
                  stats.setGapsBetweenScaffolds(String.valueOf(map.get("gaps_between_scaffolds_count")));
              }catch (Exception e){e.printStackTrace();}
                stats.setNumberOfContigs(Integer.parseInt(String.valueOf( map.get("number_of_contigs"))));
                stats.setScaffoldL50(String.valueOf(map.get("scaffold_l50")));
                stats.setScaffoldN50(String.valueOf(map.get("scaffold_n50")));
                stats.setNumberOfScaffolds(Integer.parseInt(String.valueOf(map.get("number_of_scaffolds"))));
                try {
                    stats.setTotalNumberOfChromosome(Integer.parseInt(map.get("total_number_of_chromosomes").toString()));
                }catch (Exception e){e.printStackTrace();}
                stats.setTotalUngappedLength(String.valueOf(map.get("total_ungapped_length")));
                stats.setTotalSequenceLength(String.valueOf(map.get("total_sequence_length")));
            }
        }catch (Exception e){
            System.out.println("ACCESSION:" +refSeqAccession);
            System.out.println("FETCH URL:"+ fetchUri);
            e.printStackTrace();
        }


        return stats;
    }
       public AssemblyInfo getAssemblyInfo(edu.mcw.rgd.datamodel.Map map) throws Exception {
        if(map.getRefSeqAssemblyAcc()==null || Objects.equals(map.getRefSeqAssemblyAcc(), "")){
            return null;
        }
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

    public int getTranscriptsCount(int mapKey, String chr) throws Exception {
        String sql="SELECT count(distinct(ri.rgd_id)) as tot, ro.object_name from rgd_ids ri, rgd_objects ro , maps_data m where ri.object_key = ro.object_key " +
                "AND m.rgd_id=ri.rgd_id and m.map_key=? and ri.object_status = 'ACTIVE' ";
        if(chr!=null){
            sql=sql+" and m.chromosome=?";
        }
        sql=sql+"GROUP BY ro.object_name ORDER BY ro.object_name";
        int count = 0;
      try(Connection conn=this.getConnection();
        PreparedStatement preparedStatement= conn.prepareStatement(sql);) {
          preparedStatement.setInt(1, mapKey);
          if (chr != null) {
              preparedStatement.setString(2, chr);
          }
          ResultSet rs = preparedStatement.executeQuery();

          while (rs.next()) {
              if (rs.getString("object_name").equalsIgnoreCase("transcripts")) {
                  count = rs.getInt("tot");
                  preparedStatement.close();
                  rs.close();
                  conn.close();
                  return count;
              }
          }
          preparedStatement.close();
          rs.close();
          conn.close();
      }
        return count;
    }
    public Map<String, Long> getObjectCounts(int mapKey, String chr) throws Exception {
        Map<String, Long> objectCounts = new HashMap<>();

        String sql="SELECT count(distinct(ri.rgd_id)) as tot, ro.object_name from rgd_ids ri, rgd_objects ro , maps_data m where ri.object_key = ro.object_key " +
                "AND m.rgd_id=ri.rgd_id and m.map_key=? and ri.object_status = 'ACTIVE' ";
       if(chr!=null){
           sql=sql+" and m.chromosome=?";
       }
        sql=sql+"GROUP BY ro.object_name ORDER BY ro.object_name";
       try( Connection conn=this.getConnection();
        PreparedStatement preparedStatement= conn.prepareStatement(sql);) {
           preparedStatement.setInt(1, mapKey);
           if (chr != null) {
               preparedStatement.setString(2, chr);
           }
           ResultSet rs = preparedStatement.executeQuery();

           while (rs.next()) {
               String name = rs.getString("object_name");
               long value = rs.getLong("tot");
               objectCounts.put(name, value);
           }
           preparedStatement.close();
           rs.close();

       }

        return objectCounts;
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

    public String getNCBIAPIKey(){
        Properties props=new Properties();
        FileInputStream fis=null;


        try{

            fis=new FileInputStream("/data/properties/ncbi_api.properties");
            props.load(fis);

        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty("NCBI_API_KEY");
    }

}
