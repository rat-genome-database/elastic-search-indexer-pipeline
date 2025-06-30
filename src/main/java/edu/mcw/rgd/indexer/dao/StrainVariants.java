package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;

import edu.mcw.rgd.dao.impl.SampleDAO;
import edu.mcw.rgd.indexer.model.genomeInfo.VariantCounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jthota on 12/6/2017.
 */
public class StrainVariants extends AbstractDAO{

    public String[][] getStrainVariants(int mapKey, String chr) throws Exception {

        SampleDAO sampleDAO= new SampleDAO();
        sampleDAO.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        List<VariantCounts> variantCounts=this.getVariantCounts(chr, mapKey);
        int size=variantCounts.size();
        String matrix[][]= new String[4][size];
        int j=0;
        for(VariantCounts v:variantCounts){

            for(int i=0; i<4&&j<size;i++){
                if(i==0)
               matrix[i][j]=v.getStrain();
                if(i==1)
                    matrix[i][j]=v.getSnv();
                if(i==2)
                    matrix[i][j]=v.getIns();
                if(i==3)
                    matrix[i][j]=v.getDel();
            }

        j++;
        }

        return matrix;
    }
    public List<VariantCounts> getVariantCounts(String chr, int mapKey)  {
        List<VariantCounts> variantCounts= new ArrayList<>();
        String sql="select count(variant_type) tot, variant_type, s.analysis_name, s.sample_id  from variant v " +
                "                inner join variant_sample_detail vsd on vsd.rgd_id=v.rgd_id" +
                " inner join sample s on s.sample_id=vsd.sample_id " +
                "                 inner join variant_map_data vmd on v.rgd_id=vmd.rgd_id " +
                "                 where vmd.map_key=?";

        if(chr!=null){
            sql=sql+"and vmd.chromosome=?";

        }
        sql=sql+ "   group by v.variant_type, s.sample_id, s.analysis_name";
        try(Connection conn= DataSourceFactory.getInstance().getCarpeNovoDataSource().getConnection();
            PreparedStatement ps= conn.prepareStatement(sql);){

            int totalVariants=0;
            ps.setInt(1, mapKey);
                if(chr!=null)
                    ps.setString(2, chr);
                ResultSet rs= ps.executeQuery();
                int snvAndSnps=0;
                while(rs.next()){
                    VariantCounts vc= new VariantCounts();
                    int sampleId=rs.getInt("sample_id");
                    vc.setStrain(rs.getString("analysis_name"));
                    vc.setMapKey(mapKey);
                    vc.setChr(chr);
                    String variantType= rs.getString("variant_type");

                    if(variantType.equalsIgnoreCase("snv") || variantType.equalsIgnoreCase("snp")){
                        snvAndSnps=snvAndSnps+rs.getInt("tot");
                    }

                    if(variantType.equalsIgnoreCase("ins"))
                        vc.setIns(rs.getString("tot"));
                    if(variantType.equalsIgnoreCase("del"))
                        vc.setDel(rs.getString("tot"));

                    totalVariants=totalVariants+rs.getInt("tot");
                    vc.setSnv(String.valueOf(snvAndSnps));
                    variantCounts.add(vc);
                }

                rs.close();

        }catch (Exception e){
            e.printStackTrace();
        }


        return variantCounts;
    }

}
