package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.TranscriptDAO;
import edu.mcw.rgd.dao.impl.VariantDAO;
import edu.mcw.rgd.dao.impl.VariantInfoDAO;
import edu.mcw.rgd.dao.spring.IntListQuery;
import edu.mcw.rgd.dao.spring.VariantMapper;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.springframework.jdbc.core.SqlParameter;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantDao extends AbstractDAO {
    GeneDAO geneDAO=new GeneDAO();

    VariantInfoDAO variantInfoDAO=new VariantInfoDAO();
    public List<Variant> getVariants(int sampleId, String chr) throws Exception {
        String sql = "SELECT * FROM Variant where sample_id=? and chromosome=?" ;
        VariantMapper q = new VariantMapper(this.getDataSource(), sql);
        q.declareParameter(new SqlParameter(4));
        q.declareParameter(new SqlParameter(12));
        return q.execute(new Object[]{sampleId, chr});
    }
    public List<VariantIndex> getVariantResults(int sampleId, String chr, int mapKey) {


        String sql="select v.*, vt.*,t.*,  cs.*,dbs.* , dbs.snp_name as MCW_DBS_SNP_NAME, md.*, gl.gene_symbols as region_name, g.gene_symbol as symbol, g.gene_symbol_lc as symbol_lc , s.analysis_name from variant_dog v " +
                "left outer join gene_loci gl on (gl.map_key=? and gl.chromosome=v.chromosome and gl.pos=v.start_pos) " +
                "left outer join variant_transcript vt on v.variant_id=vt.variant_id " +
                "left outer join transcripts t on vt.transcript_rgd_id=t.transcript_rgd_id " +

                "left outer JOIN sample s on (v.sample_id=s.sample_id and s.map_key=?)" +
                "left outer JOIN  db_snp dbs  ON  " +
                "( v.START_POS = dbs.POSITION     AND v.CHROMOSOME = dbs.CHROMOSOME      AND v.VAR_NUC = dbs.ALLELE      AND dbs.MAP_KEY = s.MAP_KEY AND dbs.source=s.dbsnp_source) " +
                "left outer join CONSERVATION_SCORE cs on (cs.chr=v.chromosome and cs.position=v.start_pos) " +
                "left outer join maps_data md on (md.chromosome=v.chromosome and md.rgd_id=t.gene_rgd_id and md.map_key=?) " +
                "left outer join genes g on (g.rgd_id=t.gene_rgd_id) " +
                "left outer join rgd_ids r on (r.rgd_id=md.rgd_id and r.object_status='ACTIVE') " +
                "where v.chromosome=? " +
                "and v.total_depth>8 " +
                "and v.sample_id=?";
        List<VariantIndex> vrList = new ArrayList<>();
        java.util.Map<Long, VariantIndex> variants= new HashMap<>();
        Set<Long> variantIds= new HashSet<>();
        ResultSet rs= null;
        Connection connection= null;
        PreparedStatement stmt=null;
        try{
            connection=this.getDataSource().getConnection();
            stmt=connection.prepareStatement(sql);
            stmt.setInt(1, mapKey);
            stmt.setInt(2, mapKey);
            stmt.setInt(3, mapKey);
            stmt.setString(4, chr);
            stmt.setInt(5, sampleId);
            rs=  stmt.executeQuery();

        //   System.out.println("RESULT SET SIZE: "+ rs.getFetchSize());
            while(rs.next()) {
                try {
                    VariantIndex vi = new VariantIndex();
                    long variant_id = rs.getLong("variant_id");
                    if (!variantIds.contains(variant_id)) {
                        variantIds.add(variant_id);
                        vi.setVariant_id(variant_id);
                        vi.setChromosome(rs.getString("chromosome"));
                        vi.setEndPos(rs.getLong("end_pos"));
                        vi.setSampleId(rs.getInt("sample_id"));
                        vi.setStartPos(rs.getLong("start_pos"));
                        vi.setVariantType(rs.getString("variant_type"));
                        vi.setZygosityStatus(rs.getString("zygosity_status"));
                        vi.setGenicStatus(rs.getString("genic_status"));
                        vi.setHGVSNAME(rs.getString("hgvs_name"));
                  //      vi.setAnalysisName(rs.getString("analysis_name"));

                        /***************Variant Transcript****************************/
                        //   vi.setVariantTranscriptId(rs.getInt("variant_transcript_id"));
                        //  vi.setTranscriptRgdId(rs.getInt("transcript_rgd_id"));
                        List<Long> vtIds=new ArrayList<>();
                        vtIds.add(rs.getLong("variant_transcript_id"));

                        List<Long> tIds= new ArrayList<>();
                        tIds.add(rs.getLong("transcript_rgd_id"));
               /*         vi.setPolyphenStatus(rs.getString("polyphen_status"));
                        vi.setSynStatus(rs.getString("syn_status"));
                        vi.setLocationName(rs.getString("location_name"));
                        vi.setUniprotId(rs.getString("uniprot_id"));

                        /**************************dbs_snp****************************/
                        vi.setDbsSnpName(rs.getString("MCW_DBS_SNP_NAME"));
                        /******************region_name*******************/
                        String regionName=rs.getString("region_name");
                        vi.setRegionName(regionName);
                        vi.setRegionNameLc(regionName.toLowerCase());
                        List<BigDecimal> conScores = new ArrayList<>();
                        conScores.add(rs.getBigDecimal("score"));

                        int geneRgdId = rs.getInt("gene_rgd_id");
                        if(geneRgdId!=0) {
                            List<Integer> gIds = new ArrayList<>();
                            gIds.add(geneRgdId);
                            vi.setGeneRgdIds(gIds);
                            List<String> gSymbols = new ArrayList<>();
                            String geneSymbol=rs.getString("symbol");
                            String geneSymbolLc= rs.getString("symbol_lc");
                            if(geneSymbol!=null){
                                gSymbols.add(geneSymbol);
                            }
                            if(geneSymbolLc!=null){
                                gSymbols.add(geneSymbolLc);
                             }
                         vi.setGeneSymbols(gSymbols);
                        }
                        variants.put(variant_id, vi);

                    } else {
                        VariantIndex obj = variants.get(variant_id);
                        Long vtId=rs.getLong("variant_transcript_id");
                        Long tId=rs.getLong("transcript_rgd_id");


                        int geneRgdId = rs.getInt("gene_rgd_id");
                         if(geneRgdId!=0) {
                             List<Integer> gRgdIds=new ArrayList<>();
                             if(obj.getGeneRgdIds()!=null){
                                 gRgdIds.addAll(obj.getGeneRgdIds());
                             }
                            if(!gRgdIds.contains(geneRgdId)) {
                                gRgdIds.add(geneRgdId);

                            }
                             obj.setGeneRgdIds(gRgdIds);
                            try{
                                String geneSymbol=rs.getString("symbol");
                                String geneSymbolLc= rs.getString("symbol_lc");
                                List<String> gSymbols=new ArrayList<>();
                                if(obj.getGeneSymbols()!=null){
                                    gSymbols.addAll(obj.getGeneSymbols());
                                }
                                if(geneSymbol!=null){
                                  if(!gSymbols.contains(geneSymbol) ) {
                                      gSymbols.add(geneSymbol);

                                  }
                                }
                                if(geneSymbolLc!=null){
                                    if(!gSymbols.contains(geneSymbolLc) ) {
                                        gSymbols.add(geneSymbolLc);

                                    }

                                }
                                obj.setGeneSymbols(gSymbols);
                            }catch (Exception e){
                                System.err.print("GENE RGD ID: "+ geneRgdId);
                                throw new Exception("GENE RGD _ID" + geneRgdId);

                            }

                        }
                        variants.put(variant_id, obj);
                    }
                }catch (Exception e){

                    e.printStackTrace();
                }
            }

                rs.close();
                stmt.close();
               connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if(rs!=null)
                    rs.close();
                if(stmt!=null)
                    stmt.close();
                if(connection!=null)
                    connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }finally {
             try {
                    if(rs!=null)
                    rs.close();
                    if(stmt!=null)
                    stmt.close();
                    if(connection!=null)
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }
        for(Map.Entry e:variants.entrySet()){
            vrList.add((VariantIndex) e.getValue());
        }
        return vrList;
    }

    public List<MappedGene> getMappedGenes(String chr,long startPos, long endPos, int mapKey)  {
        GeneDAO gdao= new GeneDAO();
        List<MappedGene> mappedGenes = null;
        try {
            mappedGenes = gdao.getActiveMappedGenes(chr, startPos, endPos, mapKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappedGenes;
    }


    public List<Integer> getUniqueVariantsIds( String chr, int mapKey, int speciesTypeKey) throws Exception {
        String sql ="select v.rgd_id from variant v, variant_map_data vmd  " +
                "where v.rgd_id=vmd.rgd_id " +
                " and v.species_type_key=? " +
                " and vmd.chromosome=? " +
                " and vmd.map_key=?";  //Total RECORD COUNT: 1888283; chr:1; map_key:360
        //  VariantMapQuery q=new VariantMapQuery(DataSourceFactory.getInstance().getDataSource("Variant"), sql);
        IntListQuery q=new IntListQuery(DataSourceFactory.getInstance().getCarpeNovoDataSource(), sql);
        return execute(q,speciesTypeKey,chr,mapKey);
    }
    public List<VariantIndex> getVariantsNewTbaleStructure(  int mapKey, List<Integer> variantIdsList) throws Exception {

        String csTable=getConScoreTable(mapKey,null);
        String sql="select v.*,vmd.*, vsd.*,vt.*, p.prediction ,gl.gene_symbols as region_name, g.rgd_id as gene_rgd_id," +
                " g.gene_symbol_lc as gene_symbol_lc, md.strand as strand " ;

        if(!csTable.equals("")){
            sql+=" , cs.score ";
        }
        sql+=   " from variant v " +
                " left outer join variant_map_data vmd on (vmd.rgd_id=v.rgd_id) " +
                " left outer join variant_sample_detail vsd on (vsd.rgd_id=v.rgd_id) " +
                " left outer join variant_transcript vt on ( v.rgd_id=vt.variant_rgd_id )  " +
                " left outer join transcripts t on (t.transcript_rgd_id=vt.transcript_rgd_id) " +
                " left outer join genes g on (g.rgd_id=t.gene_rgd_id) " +
                " left outer join maps_data md on ( md.rgd_id=g.rgd_id and md.map_key=vmd.map_key) " +
                " left outer join polyphen  p on (vt.variant_rgd_id =p.variant_rgd_id and vt.transcript_rgd_id=p.transcript_rgd_id)   " ;
        if(!csTable.equals("")) {
            sql+=  " left outer join" + csTable + "cs on (cs.position=vmd.start_pos and cs.chr=vmd.chromosome)     ";
        }
        sql+=   " left outer join gene_loci gl on (gl.map_key=vmd.map_key and gl.chromosome=vmd.chromosome and gl.pos=vmd.start_pos)         " +
                " where  " +
                " v.rgd_id in (" ;
        //   "63409322)";
        String ids=  variantIdsList.stream().map(Object::toString).collect(Collectors.joining(","));
        sql=sql+ids;
        sql=sql+") ";

        sql=sql+ " and vsd.sample_id in (select sample_id from sample where map_key=?) "+
                " and vt.map_key=? " +
                " and vmd.map_key=?";

        VariantIndexQuery query=new VariantIndexQuery(DataSourceFactory.getInstance().getCarpeNovoDataSource(), sql);
        List<VariantIndex> variants=  execute(query, mapKey, mapKey,mapKey);
        List<VariantIndex> vrList=new ArrayList<>();
        Set<String> variantIds=new HashSet<>();
        java.util.Map<String, VariantIndex>  sortedVariants=new HashMap<>();
        Set<Long> variantIdsWithTrancripts=new HashSet<>();
        for(VariantIndex variant:variants){
            variantIdsWithTrancripts.add(variant.getVariant_id());
            String key=variant.getVariant_id()+"-"+variant.getMapKey();
            if(mapKey==38 || mapKey==17){
                try {
                    String clinvarSignificance = getClinvarInfo((int) variant.getVariant_id());
                    if (clinvarSignificance != null && !clinvarSignificance.equals(""))
                        variant.setClinicalSignificance(clinvarSignificance);
                }catch (Exception e){
                    System.out.println("NO CLINICAL SIGNIFICACE SAMPLE_ID:"+ variant.getSampleId() +" RGD_ID:"+variant.getVariant_id());
                }
            }
            if(!variantIds.contains(key)){
                variantIds.add(key);
                sortedVariants.put(key,variant);
            }else{
                VariantIndex obj = sortedVariants.get(key);
                List<Long> transcriptIds=new ArrayList<>();
                boolean exists = false;
                if(obj.getTranscriptRgdId()!=null){
                    transcriptIds.addAll(obj.getTranscriptRgdId());
                }
                if(variant.getTranscriptRgdId()!=null) {
                    for (long transcript : variant.getTranscriptRgdId()) {
                            for (long t : transcriptIds) {
                                if (transcript == t) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                transcriptIds.add(transcript);
                                obj.setTranscriptRgdId(transcriptIds);
                            }

                    }
                }
             /*   if(variant.getAnalysisName()!=null){
                    List<String> sampleNames=new ArrayList<>();
                    for(String name:variant.getAnalysisName()) {
                        if (obj != null) {

                            sampleNames = obj.getAnalysisName();
                            for (String str : sampleNames) {
                                if (name.equals(str)) {
                                    exists = true;
                                }
                            }
                            if (!exists) {
                                sampleNames.add(name);
                                obj.setAnalysisName(sampleNames);
                            }
                        }   }
                    }*/
                sortedVariants.put(key, obj);
            }
        }


        for(Map.Entry e:sortedVariants.entrySet()){
            vrList.add((VariantIndex) e.getValue());
        }
        System.out.println("varaiants size: "+ vrList.size());

        Set<Long> variantIdsWithoutTranscripts=new HashSet<>();
        if(variantIdsList.size()>variantIdsWithTrancripts.size()){
            for(int id:variantIdsList){
                if(!variantIdsWithTrancripts.contains((long)id)){
                    variantIdsWithoutTranscripts.add((long) id);
                }
            }
            System.out.println("Queried IDS:"+variantIdsList.size()+"\nIds without transcripts:"+ variantIdsWithoutTranscripts.size());
            if(variantIdsWithoutTranscripts.size()>0) {
                List<VariantIndex> variantsWithoutTranscripts = getVariantsWithoutTranscripts(mapKey, variantIdsWithoutTranscripts);
                vrList.addAll(variantsWithoutTranscripts);
            }
        }
        System.out.println("varaiants size include no transcript variants: "+ vrList.size());
        return vrList;
    }

    public List<VariantIndex> getVariantsWithoutTranscripts(int mapKey,Set<Long> variantIdsWithoutTranscripts) throws Exception {
        String csTable=getConScoreTable(mapKey,null);
        String sql="select v.*,vmd.*, vsd.* ,gl.gene_symbols as region_name " ;
        if(!csTable.equals("")){
            sql+=" , cs.score ";
        }
        sql+=   " from variant v " +
                " left outer join variant_map_data vmd on (vmd.rgd_id=v.rgd_id) " +
                " left outer join variant_sample_detail vsd on (vsd.rgd_id=v.rgd_id) " ;

        if(!csTable.equals("")) {
            sql+=  " left outer join" + csTable + "cs on (cs.position=vmd.start_pos and cs.chr=vmd.chromosome) ";
        }
        sql+=   " left outer join gene_loci gl on (gl.map_key=vmd.map_key and gl.chromosome=vmd.chromosome and gl.pos=vmd.start_pos) " +
                " where  " +
                " v.rgd_id in (" ;
        //   "63409322)";
        String ids=  variantIdsWithoutTranscripts.stream().map(Object::toString).collect(Collectors.joining(","));
        sql=sql+ids;
        sql=sql+") ";
        sql=sql+ " and vsd.sample_id in (select sample_id from sample where map_key=?) "+
                " and vmd.map_key=?";
        //   System.out.println("SQL:"+sql);
        VariantIndexQuery query=new VariantIndexQuery(DataSourceFactory.getInstance().getCarpeNovoDataSource(), sql);

        List<VariantIndex> variants=  execute(query, mapKey, mapKey);
        List<VariantIndex> vrList=new ArrayList<>();
        Set<String> variantIds=new HashSet<>();
        java.util.Map<String, VariantIndex>  sortedVariants=new HashMap<>();

        for(VariantIndex variant:variants){
            String key=variant.getVariant_id()+"-"+variant.getMapKey();

            if(!variantIds.contains(key)){
                variantIds.add(key);
                sortedVariants.put(key,variant);
            }else{
            /*    VariantIndex obj = sortedVariants.get(key);
                boolean exists = false;

                if(variant.getAnalysisName()!=null){
                    List<String> sampleNames=new ArrayList<>();
                    for(String name:variant.getAnalysisName()) {
                        if (obj != null) {

                            sampleNames = obj.getAnalysisName();
                            for (String str : sampleNames) {
                                if (name.equals(str)) {
                                    exists = true;
                                }
                            }
                            if (!exists) {
                                sampleNames.add(name);
                                obj.setAnalysisName(sampleNames);
                            }
                        }   }
                }
                sortedVariants.put(key, obj);*/
            }
        }


        for(Map.Entry e:sortedVariants.entrySet()){
            vrList.add((VariantIndex) e.getValue());
        }

           return vrList;

    }
    public String getConScoreTable(int mapKey, String genicStatus ) {
        switch(mapKey) {
            case 17:
                return " B37_CONSCORE_PART_IOT ";
            case 38:
                return " CONSERVATION_SCORE_HG38 ";
            case 60:
            /*    if (genicStatus.equalsIgnoreCase("GENIC")) {
                    return " CONSERVATION_SCORE_GENIC ";
                }
*/
                return " CONSERVATION_SCORE ";
            case 70:
                return " CONSERVATION_SCORE_5 ";
            case 360:
                return " CONSERVATION_SCORE_6 ";
            default:
                return "";
        }
    }
    public String getClinvarInfo(int variantRgdId) throws Exception {

        VariantInfo info=variantInfoDAO.getVariant(variantRgdId)  ;
        return  info.getClinicalSignificance();
    }
}
