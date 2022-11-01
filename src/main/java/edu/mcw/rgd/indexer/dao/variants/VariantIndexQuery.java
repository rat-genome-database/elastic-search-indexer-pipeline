package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.datamodel.MapData;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.model.MapInfo;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VariantIndexQuery extends MappingSqlQuery {
    public VariantIndexQuery(DataSource ds, String query){
        super(ds, query);
    }
    @Override
    protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        VariantIndex vi = new VariantIndex();
        vi.setCategory("Variant");
        vi.setName(rs.getString("rs_id"));
        vi.setRsId(rs.getString("rs_id"));
     //   vi.setSymbol(String.valueOf(rs.getLong("rgd_id")));
        vi.setTerm_acc(String.valueOf(rs.getLong("rgd_id")));
        vi.setSpecies(SpeciesType.getCommonName(rs.getInt("species_type_key")));
        vi.setType(rs.getString("variant_type"));
        /*********************/
        vi.setVariant_id(rs.getLong("rgd_id"));
        vi.setChromosome(rs.getString("chromosome"));
        vi.setEndPos(rs.getLong("end_pos"));
        vi.setSampleId(rs.getInt("sample_id"));
        vi.setStartPos(rs.getLong("start_pos"));

        vi.setVariantType(rs.getString("variant_type"));
        vi.setZygosityStatus(rs.getString("zygosity_status"));
        vi.setGenicStatus(rs.getString("genic_status"));
        vi.setHGVSNAME(rs.getString("rs_id"));
    //     vi.setAnalysisName(Arrays.asList(rs.getString("analysis_name")));

        vi.setMapKey(rs.getInt("map_key"));

        /***************Variant Transcript****************************/
        try{
            if(rs.getInt("transcript_rgd_id")!=0) {
                vi.setTranscriptRgdId(Arrays.asList(rs.getLong("transcript_rgd_id")));
                vi.setPolyphenStatus(Arrays.asList(rs.getString("prediction")));
                vi.setSynStatus(rs.getString("syn_status"));
                vi.setLocationName(Arrays.asList(  rs.getString("location_name")));
            }}catch (Exception e){}
        /******************region_name*******************/
        try {
            String regionName = rs.getString("region_name");
            vi.setRegionName(regionName);
            vi.setRegionNameLc(regionName.toLowerCase());;
        }catch (Exception e){}
        try {
            if(rs.getInt("gene_rgd_id")>0) {
                vi.setGeneRgdIds(Arrays.asList(rs.getInt("gene_rgd_id")));

                vi.setGeneSymbols(Arrays.asList( rs.getString("gene_symbol_lc")));
            }
        }catch (Exception e){}

        try{
            vi.setVarNuc(rs.getString("var_nuc"));
            vi.setRefNuc(rs.getString("ref_nuc"));

        }catch (Exception e){
            e.printStackTrace();
        }
        return vi;
    }

}
