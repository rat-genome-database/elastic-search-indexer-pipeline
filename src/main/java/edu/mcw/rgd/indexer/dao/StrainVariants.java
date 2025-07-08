package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;

import edu.mcw.rgd.dao.impl.SampleDAO;
import edu.mcw.rgd.indexer.model.genomeInfo.VariantCounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jthota on 12/6/2017.
 */
public class StrainVariants extends AbstractDAO{
    public static void main(String[] args) throws Exception {
        StrainVariants variants=new StrainVariants();
        variants.getStrainVariants(372, "12");
    }


    public String[][] getStrainVariants(int mapKey, String chr) throws Exception {

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

        System.out.println("MATRIX:"+ Arrays.deepToString(matrix));
        return matrix;
    }
    public List<VariantCounts> getVariantCounts(String chr, int mapKey)  {
        List<VariantCounts> variantCounts= new ArrayList<>();
        LinkedHashMap<Integer, VariantCounts> countsMap=new LinkedHashMap<>();
        String sql="select count(variant_type) tot, variant_type, s.analysis_name, s.sample_id  from variant v " +
                "                inner join variant_sample_detail vsd on vsd.rgd_id=v.rgd_id" +
                " inner join sample s on s.sample_id=vsd.sample_id " +
                "                 inner join variant_map_data vmd on v.rgd_id=vmd.rgd_id " +
                "                 where vmd.map_key=?";

        if(chr!=null){
            sql=sql+"   and vmd.chromosome=?";

        }
        sql=sql+ "   group by v.variant_type, s.sample_id, s.analysis_name order by s.analysis_name";
        try(Connection conn= DataSourceFactory.getInstance().getCarpeNovoDataSource().getConnection();
            PreparedStatement ps= conn.prepareStatement(sql);){


            ps.setInt(1, mapKey);
                if(chr!=null)
                    ps.setString(2, chr);
                ResultSet rs= ps.executeQuery();
            int totalVariants=0;
                while(rs.next()) {
                    int sampleId = rs.getInt("sample_id");
                    VariantCounts vc=countsMap.get(sampleId);
                    if(vc==null){
                        vc=new VariantCounts();
                        vc.setStrain(rs.getString("analysis_name"));
                        vc.setMapKey(mapKey);
                        vc.setChr(chr);

                    }

                        String variantType = rs.getString("variant_type");

                        if (variantType.equalsIgnoreCase("snv") || variantType.equalsIgnoreCase("snp")) {
                            int snvOrSnpCount = 0;
                            if (vc.getSnv() != null)
                                snvOrSnpCount += Integer.parseInt(vc.getSnv());
                            if (vc.getSnp() != null)
                                snvOrSnpCount += Integer.parseInt(vc.getSnp());
                            snvOrSnpCount += rs.getInt("tot");
                            vc.setSnv(String.valueOf(snvOrSnpCount));
                        }

                        if (variantType.equalsIgnoreCase("insertion"))
                            vc.setIns(rs.getString("tot"));
                        if (variantType.equalsIgnoreCase("deletion"))
                            vc.setDel(rs.getString("tot"));

                        totalVariants = totalVariants + rs.getInt("tot");
                        vc.setTotalVariants(String.valueOf(totalVariants));
                        countsMap.put(sampleId, vc);
                       // variantCounts.add(vc);
                    }


                rs.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        for(Map.Entry entry:countsMap.entrySet()){
            variantCounts.add((VariantCounts) entry.getValue());
        }

        return variantCounts;
    }

}
