package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.TranscriptDAO;
import edu.mcw.rgd.dao.impl.VariantDAO;
import edu.mcw.rgd.dao.spring.VariantMapper;
import edu.mcw.rgd.datamodel.*;
import org.springframework.jdbc.core.SqlParameter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantDao extends VariantDAO {
    public List<Variant> getVariants(int sampleId, String chr) {
        String sql = "SELECT * FROM Variant where sample_id=? and chromosome=?" ;
        VariantMapper q = new VariantMapper(this.getDataSource(), sql);
        q.declareParameter(new SqlParameter(4));
        q.declareParameter(new SqlParameter(12));
        return q.execute(new Object[]{sampleId, chr});
    }
    public List<VariantResult> getVariantResults(int sampleId, String chr)  {
     String sql="select v.*, vt.*, p.*, cs.* from variant v\n" +
             "inner join gene_loci gl on (gl.map_key=60 and gl.chromosome=v.chromosome and gl.pos=v.start_pos) " +
             "inner join variant_transcript vt on v.variant_id=vt.variant_id " +
             "inner join transcripts t on vt.transcript_rgd_id=t.transcript_rgd_id " +
             "inner join polyphen p on (v.variant_id=p.variant_id and p.protein_status='100 PERC MATCH') " +
             "left outer JOIN sample s on (v.sample_id=s.sample_id and s.map_key=60) " +
             "inner join CONSERVATION_SCORE cs on (cs.chr=v.chromosome and cs.position=v.start_pos) " +
             "where v.sample_id=? " +
             "and v.chromosome=?";
        List<VariantResult> vrList = new ArrayList<>();
        try(Connection connection= DataSourceFactory.getInstance().getCarpeNovoDataSource().getConnection()){
            PreparedStatement stmt=connection.prepareStatement(sql);
            stmt.setInt(1, sampleId);
            stmt.setString(2, chr);
            ResultSet rs=  stmt.executeQuery();
            long lastVariant = 0L;
            VariantResultBuilder vrb = new VariantResultBuilder();
            boolean found = false;

            while(rs.next()) {
                found = true;
                long variantId = rs.getLong("variant_id");
                if(variantId != lastVariant) {
                    if(lastVariant != 0L) {
                        vrList.add(vrb.getVariantResult());
                    }

                    lastVariant = variantId;
                    vrb = new VariantResultBuilder();
                    vrb.mapVariant(rs);
                    vrb.mapConservation(rs);

                }
            }
            if(found) {
                vrList.add(vrb.getVariantResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lastQuery = sql;
      //  this.logger.debug("found vrList size " + vrList.size());
        return vrList;
    }
    public List<MappedGene> getMappedGenes(Variant v, int mapKey)  {
        GeneDAO gdao= new GeneDAO();
        List<MappedGene> mappedGenes = null;
        try {
            mappedGenes = gdao.getActiveMappedGenes(v.getChromosome(), v.getStartPos(), v.getEndPos(), mapKey);
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
      /*  List<Variant> variants= dao.getVariants(516, "1");
        System.out.println("VARIANTS: "+ variants.size());*/
        List<VariantResult> variantResults= dao.getVariantResults(516,"10");
        List<SNPlotyper> snps=new ArrayList<>();
        for (VariantResult v: variantResults) {
         List<MappedGene> mappedGenes = gdao.getActiveMappedGenes(v.getVariant().getChromosome(), v.getVariant().getStartPos(), v.getVariant().getEndPos(), 60);
      //     List<MapData> mapData = tdao.getFeaturesByGenomicPos(60,v.getVariant().getChromosome(), (int) (long) v.getVariant().getStartPos(), (int) (long) v.getVariant().getEndPos(),15);

            System.out.print(v.getVariant().getId()+"\t"+v.getVariant().getSampleId()+"\t"+v.getVariant().getChromosome()+"\t"+v.getVariant().getStartPos()+
                    "\t"+ v.getVariant().getEndPos()+"\t");
            for(ConservationScore s:v.getVariant().getConservationScore()){
                System.out.print(s.getScore() +",");
            }
            System.out.print("\t"+v.getVariant().getReferenceNucleotide()+"\t"+v.getVariant().getVariantNucleotide()+"\t"+v.getVariant().getVariantType()+
                    "\t"+ v.getVariant().getDepth()+"\t"+v.getVariant().getZygosityPossibleError()+"\t"+ v.getVariant().getGenicStatus()+"\t");

            for(MappedGene g:mappedGenes){
                System.out.print(g.getGene().getSymbol()+"|"+g.getGene().getRgdId()+"|");
            }
            System.out.print("\n");
        }


    /*    for(VariantResult v:variantResults){
            System.out.print(v.getVariant().getId()+"\t"+v.getVariant().getSampleId()+"\t"+v.getVariant().getChromosome()+"\t"+v.getVariant().getStartPos()+
            "\t"+ v.getVariant().getEndPos()+"\t");
            for(ConservationScore s:v.getVariant().getConservationScore()){
                System.out.print(s.getScore() +",");
            }
           System.out.print("\t"+v.getVariant().getReferenceNucleotide()+"\t"+v.getVariant().getVariantNucleotide()+"\t"+v.getVariant().getVariantType()+
            "\t"+ v.getVariant().getDepth()+"\t"+v.getVariant().getZygosityPossibleError()+"\t"+ v.getVariant().getGenicStatus());
            System.out.print("\t"+v.getTranscriptResults().size());
            for(TranscriptResult t:v.getTranscriptResults()){
                System.out.print(t.getTranscriptId()+"|"+ t.getLocation()+"|"+t.getAminoAcidVariant()+"|"+t.getPolyPhenPrediction()+"|");
            }
            System.out.print("\n");
        }*/
    }

}
