package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.dao.impl.SampleDAO;
import edu.mcw.rgd.datamodel.Chromosome;
import edu.mcw.rgd.datamodel.Sample;
import edu.mcw.rgd.indexer.model.genomeInfo.VariantCounts;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jthota on 12/6/2017.
 */
public class StrainVariants extends AbstractDAO{

    public String[][] getStrainVariants(int mapKey, String chr) throws Exception {

        SampleDAO sampleDAO= new SampleDAO();
        sampleDAO.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        List<edu.mcw.rgd.datamodel.Sample> samples=sampleDAO.getSamplesByMapKey(mapKey);
        List<VariantCounts> variantCounts=this.getVariants(samples, chr, mapKey);
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
    public List<VariantCounts> getVariants(List<Sample> samples, String chr, int mapKey)  {
        List<VariantCounts> variantCounts= new ArrayList<>();
        String sql="select count(variant_type) tot, variant_type from variant v " +
                " inner join variant_sample_detail vsd on vsd.rgd_id=v.rgd_id " +
                " inner join variant_map_data vmd on v.rgd_id=vmd.rgd_id " +
                "                  where " +
                "                 vsd.sample_id=? " ;

        if(chr!=null){
            sql=sql+"and vmd.chromosome=?";
        }
        sql=sql+ "   group by v.variant_type";
        try(Connection conn= DataSourceFactory.getInstance().getCarpeNovoDataSource().getConnection();
            PreparedStatement ps= conn.prepareStatement(sql);){

            int totalVariants=0;
            for(Sample s:samples){
                VariantCounts vc= new VariantCounts();
                int sampleId=s.getId();
                vc.setStrain(s.getAnalysisName());
                vc.setMapKey(mapKey);
                vc.setChr(chr);
                ps.setInt(1, sampleId);
                if(chr!=null)
                    ps.setString(2, chr);
                ResultSet rs= ps.executeQuery();
                int snvAndSnps=0;
                while(rs.next()){
                    String variantType= rs.getString("variant_type");

                   if(variantType.equalsIgnoreCase("snv") || variantType.equalsIgnoreCase("snp")){
                        snvAndSnps=snvAndSnps+rs.getInt("tot");
                    }

                    if(variantType.equalsIgnoreCase("ins"))
                        vc.setIns(rs.getString("tot"));
                    if(variantType.equalsIgnoreCase("del"))
                        vc.setDel(rs.getString("tot"));

                    totalVariants=totalVariants+rs.getInt("tot");
                 //     System.out.println(rs.getString("variant_type")+ " || "+ rs.getString("tot"));
                }
                vc.setSnv(String.valueOf(snvAndSnps));
                variantCounts.add(vc);
                rs.close();

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return variantCounts;
    }
public static void main(String[] args) throws Exception {
    StrainVariants s= new StrainVariants();
    s.getStrainVariants(360, null);
    System.out.println("DONE");
}
}
