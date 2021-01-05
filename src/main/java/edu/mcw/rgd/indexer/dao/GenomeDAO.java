package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.dao.spring.CountQuery;

import edu.mcw.rgd.datamodel.*;

import edu.mcw.rgd.indexer.model.genomeInfo.*;




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


    public AssemblyInfo getAssemblyInfo(int speciesTypeKey, int mapKey) throws Exception {

        AssemblyInfo info= new AssemblyInfo();


            switch (speciesTypeKey){
                case 1:
                    if(mapKey==38) {
                      //  info.setBasePairs("3,554,996,726");
                        info.setTotalSeqLength("3,099,706,404");
                        info.setTotalUngappedLength("2,948,583,725");
                        info.setGapBetweenScaffolds("349");
                        info.setScaffolds("472");
                        info.setScaffoldN50("67,794,873");
                        info.setScaffoldL50("16");
                        info.setContigs("998");
                        info.setContigN50("57,879,411");
                        info.setContigL50("18");
                        info.setChromosome("24");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001405.37/");
                        info.setRefSeqAssemblyAccession("GCF_000001405.37");
                    }
                    if(mapKey==17) {
                   //     info.setBasePairs("3,326,743,047");
                        info.setTotalSeqLength("3,101,788,170");
                        info.setTotalUngappedLength("2,867,437,753");
                        info.setGapBetweenScaffolds("271");
                        info.setScaffolds("249");
                        info.setScaffoldN50("46,395,641");
                        info.setScaffoldL50("21");
                        info.setContigs("350");
                        info.setContigN50("38,508,932");
                        info.setContigL50("24");
                        info.setChromosome("24");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001405.25/");
                        info.setRefSeqAssemblyAccession("GCF_000001405.25");
                    }
                    if(mapKey==13) {
                   //     info.setBasePairs("3,253,037,807");
                        info.setTotalSeqLength("3,093,104,542");
                        info.setTotalUngappedLength("2,870,620,296");
                        info.setGapBetweenScaffolds("301");
                        info.setScaffolds("367");
                        info.setScaffoldN50("38,509,590");
                        info.setScaffoldL50("25");
                        info.setContigs("1,006");
                        info.setContigN50("38,509,590");
                        info.setContigL50("25");
                        info.setChromosome("24");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001405.12/");
                        info.setRefSeqAssemblyAccession("GCF_000001405.12");
                    }
                    break;
                case 2:
                    if(mapKey==35) {
                    //    info.setBasePairs("3,482,409,794");
                        info.setTotalSeqLength("2,730,855,475");
                        info.setTotalUngappedLength("2,652,767,259");
                        info.setGapBetweenScaffolds("191");
                        info.setScaffolds("162");
                        info.setScaffoldN50("54,517,951");
                        info.setScaffoldL50("17");
                        info.setContigs("605");
                        info.setContigN50("32,813,180");
                        info.setContigL50("25");
                        info.setChromosome("21");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001635.26/");
                        info.setRefSeqAssemblyAccession("GCF_000001635.26");
                    }

                    if(mapKey==18) {
                    //    info.setBasePairs("3,420,842,930");
                        info.setTotalSeqLength("2,716,949,182");
                        info.setTotalUngappedLength("2,620,329,768");
                        info.setGapBetweenScaffolds("126");
                        info.setScaffolds("315");
                        info.setScaffoldN50("39,293,786");
                        info.setScaffoldL50("20");
                        info.setContigs("1,029");
                        info.setContigN50("25,603,741");
                        info.setContigL50("30");
                        info.setChromosome("21");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001635.18/");
                        info.setRefSeqAssemblyAccession("GCF_000001635.18");
                    }
                    if(mapKey==14) {
                    //    info.setBasePairs("");
                        info.setTotalSeqLength("2,661,188,789");
                        info.setTotalUngappedLength("2,567,267,557");
                        info.setGapBetweenScaffolds("123");
                        info.setScaffolds("200");
                        info.setScaffoldN50("40,319,553");
                        info.setScaffoldL50("19");
                        info.setContigs("1,427");
                        info.setContigN50("17,840,746");
                        info.setContigL50("35");
                        info.setChromosome("21");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001635.15/");
                        info.setRefSeqAssemblyAccession("GCF_000001635.15");
                    }
                    if(mapKey==239) {
                        //    info.setBasePairs("");
                        info.setTotalSeqLength("2,728,222,451");
                        info.setTotalUngappedLength("2,654,621,837");
                        info.setGapBetweenScaffolds("143");
                        info.setScaffolds("102");
                        info.setScaffoldN50("106,145,001");
                        info.setScaffoldL50("11");
                        info.setContigs("306");
                        info.setContigN50("59,462,871");
                        info.setContigL50("15");
                        info.setChromosome("22");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001635.27/");
                        info.setRefSeqAssemblyAccession("GCF_000001635.27");
                    }
                    break;
                case 3:

                        if(mapKey==360) {
                     //       info.setBasePairs("3,042,335,753");
                            info.setTotalSeqLength("2,870,184,193");
                            info.setTotalUngappedLength("2,729,985,504");
                            info.setGapBetweenScaffolds("440");
                            info.setScaffolds("1,395");
                            info.setScaffoldN50("14,986,627");
                            info.setScaffoldL50("65");
                            info.setContigs("75,697");
                            info.setContigN50("100,461");
                            info.setContigL50("7,356");
                            info.setChromosome("23");
                            info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001895.5/");
                            info.setRefSeqAssemblyAccession("GCF_000001895.5");
                      }
                        if(mapKey==60) {
                      //      info.setBasePairs("2,507,066,667");
                            info.setTotalSeqLength("2,826,224,306");
                            info.setTotalUngappedLength("2,567,953,008");
                            info.setGapBetweenScaffolds("270");
                            info.setScaffolds("741");
                            info.setScaffoldN50("18,621,810");
                            info.setScaffoldL50("46");
                            info.setContigs("238,325");
                            info.setContigN50("36,847");
                            info.setContigL50("18,984");
                            info.setChromosome("22");
                            info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001895.3/");
                            info.setRefSeqAssemblyAccession("GCF_000001895.3");

                        }
                        if(mapKey==70) {
                      //      info.setBasePairs("");
                            info.setTotalSeqLength("2,909,698,938");
                            info.setTotalUngappedLength("2,573,099,424");
                            info.setGapBetweenScaffolds("8,109");
                            info.setScaffolds("10,848");
                            info.setScaffoldN50("2,178,346");
                            info.setScaffoldL50("387");
                            info.setContigs("132,131");
                            info.setContigN50("52,491");
                            info.setContigL50("13,663");
                            info.setChromosome("22");
                            info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000001895.4/");
                            info.setRefSeqAssemblyAccession("GCF_000001895.4");

                        }

                    break;
                case 4:
                    if(mapKey==44) {
                    //    info.setBasePairs("");
                        info.setTotalSeqLength("2,390,868,971");
                        info.setTotalUngappedLength("2,284,292,980");
                        info.setGapBetweenScaffolds("0");
                        info.setScaffolds("2,839");
                        info.setScaffoldN50("21,893,125");
                        info.setScaffoldL50("37");
                        info.setContigs("81,656");
                        info.setContigN50("61,105");
                        info.setContigL50("10,854");
                        info.setChromosome("1");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000276665.1/");
                        info.setRefSeqAssemblyAccession("GCF_000276665.1");

                    }
                    break;
                case 5:
                    if(mapKey==511){
                   //     info.setBasePairs("2,725,937,399");
                        info.setTotalSeqLength("3,286,643,938");
                        info.setTotalUngappedLength("2,725,937,204");
                        info.setGapBetweenScaffolds("734");
                        info.setScaffolds("10,984");
                        info.setScaffoldN50("8,197,324");
                        info.setScaffoldL50("94");
                        info.setContigs("121,356");
                        info.setContigN50("66,676");
                        info.setContigL50("11,048");
                        info.setChromosome("25");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000258655.2/");
                        info.setRefSeqAssemblyAccession("GCF_000258655.2");
                    }
                    if(mapKey==513){ //bonobo new assembly
                   //     info.setBasePairs("");
                        info.setTotalSeqLength("3,051,901,337");
                        info.setTotalUngappedLength("3,015,350,297");
                        info.setGapBetweenScaffolds("64");
                        info.setScaffolds("4,357");
                        info.setScaffoldN50("68,246,502");
                        info.setScaffoldL50("16");
                        info.setContigs("4,976");
                        info.setContigN50("16,579,680");
                        info.setContigL50("48");
                        info.setChromosome("25");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_013052645.1/");
                        info.setRefSeqAssemblyAccession("GCF_013052645.1");
                    }
                    break;
                case 6:
                    if(mapKey==631){
                    //    info.setBasePairs("2,392,715,236");
                        info.setTotalSeqLength("2,410,976,875");
                        info.setTotalUngappedLength("2,392,715,236");
                        info.setGapBetweenScaffolds("80");
                        info.setScaffolds("3,310");
                        info.setScaffoldN50("45,876,610");
                        info.setScaffoldL50("20");
                        info.setContigs("27,106");
                        info.setContigN50("267,478");
                        info.setContigL50("2,436");
                        info.setChromosome("40");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000002285.3/");
                        info.setRefSeqAssemblyAccession("GCF_000002285.3");
                    }
                    break;
                case 7:
                    if(mapKey==720){
                   //     info.setBasePairs("2,311,076,758");
                        info.setTotalSeqLength("2,478,393,770");
                        info.setTotalUngappedLength("167,333,470");
                        info.setGapBetweenScaffolds("0");
                        info.setScaffolds("12,483");
                        info.setScaffoldN50("8,192,786");
                        info.setScaffoldL50("80");
                        info.setContigs("153,488");
                        info.setContigN50("44,137");
                        info.setContigL50("14,002");
                        info.setChromosome("0");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000236235.1/");
                        info.setRefSeqAssemblyAccession("GCF_000236235.1");
                    }
                    break;
                case 9:
                    if(mapKey==900){


                    }
                    if(mapKey==910){
                  //      info.setBasePairs("3,024,658,544");
                        info.setTotalSeqLength("2,808,525,991");
                        info.setTotalUngappedLength("2,519,152,092");
                        info.setGapBetweenScaffolds("5,323");
                        info.setScaffolds("9,906");
                        info.setScaffoldN50("576,008");
                        info.setScaffoldL50("1,303");
                        info.setContigs("243,021");
                        info.setContigN50("69,503");
                        info.setContigL50("8,632");
                        info.setChromosome("21");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000003025.5/");
                        info.setRefSeqAssemblyAccession("GCF_000003025.5");
                    }
                    if(mapKey==911){
                   //     info.setBasePairs("2,478,444,698");
                        info.setTotalSeqLength("2,501,912,388");
                        info.setTotalUngappedLength("2,472,047,747");
                        info.setGapBetweenScaffolds("93");
                        info.setScaffolds("706");
                        info.setScaffoldN50("88,231,837");
                        info.setScaffoldL50("9");
                        info.setContigs("1,118");
                        info.setContigN50("48,231,277");
                        info.setContigL50("15");
                        info.setChromosome("21");
                        info.setNcbiLink("https://www.ncbi.nlm.nih.gov/assembly/GCF_000003025.6/");
                        info.setRefSeqAssemblyAccession("GCF_000003025.6");
                    }
                default:
            }


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
    Map<String, Integer> map=genomeDAO.getOrthologCounts(720, 7, null);
        for(Map.Entry e:map.entrySet()){
            System.out.println(e.getKey() + "||"+ e. getValue());
        }


    }
}
