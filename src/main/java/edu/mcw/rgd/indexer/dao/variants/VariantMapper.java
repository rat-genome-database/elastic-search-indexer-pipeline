package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VariantMapper extends MappingSqlQuery {
    public VariantMapper(DataSource ds, String sql){
        super(ds, sql);
    }
    @Override
    protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
     /*   VariantIndex vi = new VariantIndex();
        vi.setVariant_id(rs.getLong("rgd_id"));
        vi.setChromosome(rs.getString("chromosome"));
        vi.setPaddingBase(rs.getString("padding_base"));
        vi.setEndPos(rs.getLong("end_pos"));
        vi.setRefNuc(rs.getString("ref_nuc"));
        vi.setStartPos(rs.getLong("start_pos"));
        vi.setVariantType(rs.getString("variant_type"));
        vi.setVarNuc(rs.getString("var_nuc"));
        vi.setGenicStatus(rs.getString("genic_status"));
        if(rs.getString("rs_id")!=null)
        vi.setHGVSNAME(rs.getString("rs_id"));
        else if(rs.getString("clinvar_id")!=null)
            vi.setHGVSNAME(rs.getString("clinvar_id"));

        vi.setAnalysisName(rs.getString("analysis_name"));*/
//******************************************************************************************
        IndexObject v = new IndexObject();
        long rgdId = rs.getLong("rgd_id");
        if(rs.getString("rs_id")!=null) {
            v.setSymbol(rs.getString("rs_id"));
            v.setName(rs.getString("rs_id"));

        }
        else if(rs.getString("clinvar_id")!=null) {
            v.setSymbol(rs.getString("clinvar_id"));
            v.setName(rs.getString("clinvar_id"));

        }
        String species= SpeciesType.getCommonName(rs.getInt("species_type_key"));
        v.setSpecies(species);
        v.setTerm_acc(String.valueOf(rgdId));
        v.setType(rs.getString("variant_type"));
        v.setCategory("Variant");
        return v;
    }
}
