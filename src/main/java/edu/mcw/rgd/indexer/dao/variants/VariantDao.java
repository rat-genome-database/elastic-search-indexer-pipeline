package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.TranscriptDAO;
import edu.mcw.rgd.dao.impl.VariantDAO;
import edu.mcw.rgd.dao.spring.VariantMapper;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.springframework.jdbc.core.SqlParameter;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Map;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantDao extends VariantDAO {
    GeneDAO geneDAO=new GeneDAO();

    public List<Variant> getVariants(int sampleId, String chr) {
        String sql = "SELECT * FROM Variant where sample_id=? and chromosome=?" ;
        VariantMapper q = new VariantMapper(this.getDataSource(), sql);
        q.declareParameter(new SqlParameter(4));
        q.declareParameter(new SqlParameter(12));
        return q.execute(new Object[]{sampleId, chr});
    }
    public List<VariantIndex> getVariantResults(int sampleId, String chr, int mapKey) {


        String sql="select v.*, vt.*,t.*, p.*, cs.*,dbs.* , dbs.snp_name as MCW_DBS_SNP_NAME, md.*, gl.gene_symbols as region_name, g.gene_symbol as symbol, g.gene_symbol_lc as symbol_lc from variant v " +
                "left outer join gene_loci gl on (gl.map_key=? and gl.chromosome=v.chromosome and gl.pos=v.start_pos) " +
                "left outer join variant_transcript vt on v.variant_id=vt.variant_id " +
                "left outer join transcripts t on vt.transcript_rgd_id=t.transcript_rgd_id " +
                "left outer join polyphen p on (v.variant_id=p.variant_id and p.protein_status='100 PERC MATCH') " +
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
                        vi.setPaddingBase(rs.getString("padding_base"));
                        vi.setEndPos(rs.getLong("end_pos"));
                        vi.setRefNuc(rs.getString("ref_nuc"));
                        vi.setSampleId(rs.getInt("sample_id"));
                        vi.setStartPos(rs.getLong("start_pos"));
                        vi.setTotalDepth(rs.getInt("total_depth"));
                        vi.setVarFreq(rs.getInt("var_freq"));
                        vi.setVariantType(rs.getString("variant_type"));
                        vi.setVarNuc(rs.getString("var_nuc"));
                        vi.setZygosityStatus(rs.getString("zygosity_status"));
                        vi.setGenicStatus(rs.getString("genic_status"));
                        vi.setZygosityPercentRead(rs.getDouble("zygosity_percent_read"));
                        vi.setZygosityPossError(rs.getString("zygosity_poss_error"));
                        vi.setZygosityRefAllele(rs.getString("zygosity_ref_allele"));
                        vi.setZygosityNumAllele(rs.getInt("zygosity_num_allele"));
                        vi.setZygosityInPseudo(rs.getString("zygosity_in_pseudo"));
                        vi.setQualityScore(rs.getInt("quality_score"));
                        vi.setHGVSNAME(rs.getString("hgvs_name"));


                        /***************Variant Transcript****************************/
                        //   vi.setVariantTranscriptId(rs.getInt("variant_transcript_id"));
                        //  vi.setTranscriptRgdId(rs.getInt("transcript_rgd_id"));
                        List<Long> vtIds=new ArrayList<>();
                        vtIds.add(rs.getLong("variant_transcript_id"));
                        vi.setVariantTranscriptIds(vtIds);
                        List<Long> tIds= new ArrayList<>();
                        tIds.add(rs.getLong("transcript_rgd_id"));
                        vi.setTranscriptRgdIds(tIds);
                        vi.setRefAA(rs.getString("ref_aa"));
                        vi.setVarAA(rs.getString("var_aa"));
                        vi.setGeneSpliceStatus(rs.getString("genesplice_status"));
                        vi.setPolyphenStatus(rs.getString("polyphen_status"));
                        vi.setSynStatus(rs.getString("syn_status"));
                        vi.setLocationName(rs.getString("location_name"));
                        vi.setNearSpliceSite(rs.getString("near_splice_site"));
                        //    vi.setFullRefNuc(rs.getString("full_ref_nuc"));
                        vi.setFullRefNucPos(rs.getInt("full_ref_nuc_pos"));
                        //   vi.setFullRefAA(rs.getString("full_ref_aa"));
                        vi.setFullRefAAPos(rs.getInt("full_ref_aa_pos"));
                        vi.setUniprotId(rs.getString("uniprot_id"));
                        vi.setTripletError(rs.getString("triplet_error"));
                        vi.setFrameShift(rs.getString("frameshift"));

                        /*****************polyphen******************/

                        vi.setPolyphenPrediction(rs.getString("prediction"));
                        /******************region_name*******************/
                        vi.setRegionName(rs.getString("region_name"));
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
                        vi.setConScores(conScores);
                        variants.put(variant_id, vi);

                    } else {
                        VariantIndex obj = variants.get(variant_id);
                        Long vtId=rs.getLong("variant_transcript_id");
                        Long tId=rs.getLong("transcript_rgd_id");
                        if(vtId!=0 && !obj.getVariantTranscriptIds().contains(vtId))
                            obj.getVariantTranscriptIds().add(vtId);
                        if(tId!=0 && !obj.getTranscriptRgdIds().contains(tId))
                            obj.getTranscriptRgdIds().add(tId);

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


        } catch (Exception e) {
            e.printStackTrace();
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

    public static void main(String[] args) throws Exception {
        VariantDao dao= new VariantDao();
        GeneDAO gdao=new GeneDAO();
        TranscriptDAO tdao = new TranscriptDAO();
        dao.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        List<VariantIndex> variantResults= dao.getVariantResults(911,"10", 360);
        List<SNPlotyper> snps=new ArrayList<>();
        for (VariantIndex v: variantResults) {
         List<MappedGene> mappedGenes = gdao.getActiveMappedGenes(v.getChromosome(), v.getStartPos(), v.getEndPos(), 360);
     //    List<MapData> mapData = tdao.getFeaturesByGenomicPos(60,v.getVariant().getChromosome(), (int) (long) v.getVariant().getStartPos(), (int) (long) v.getVariant().getEndPos(),15);

            System.out.print(v.getVariant_id()+"\t"+v.getSampleId()+"\t"+v.getChromosome()+"\t"+v.getStartPos()+
                    "\t"+ v.getEndPos()+"\t"+ v.getConScores().toString()+"\t"+ v.getRegionName());
         /*   for(ConservationScore s:v.getVariant().getConservationScore()){
                System.out.print(s.getScore() +",");
            }
            System.out.print("\t"+v.getVariant().getReferenceNucleotide()+"\t"+v.getVariant().getVariantNucleotide()+"\t"+v.getVariant().getVariantType()+
                    "\t"+ v.getVariant().getDepth()+"\t"+v.getVariant().getZygosityPossibleError()+"\t"+ v.getVariant().getGenicStatus()+"\t");

            for(MappedGene g:mappedGenes){
                System.out.print(g.getGene().getSymbol()+"|"+g.getGene().getRgdId()+"|");
            }*/
            System.out.print("\n");
        }



    }

}
