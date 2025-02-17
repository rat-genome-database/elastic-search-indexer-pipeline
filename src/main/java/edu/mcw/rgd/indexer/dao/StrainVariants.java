package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;

import edu.mcw.rgd.dao.impl.SampleDAO;
import edu.mcw.rgd.datamodel.Sample;
import edu.mcw.rgd.indexer.model.genomeInfo.VariantCounts;
import edu.mcw.rgd.indexer.model.genomeInfo.VariantsCountsList;
import org.elasticsearch.common.recycler.Recycler;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jthota on 12/6/2017.
 */
public class StrainVariants extends AbstractDAO{

    public String[][] getStrainVariants(int mapKey, String chr,  VariantsCountsList variantsCountsList) throws Exception {


        List<VariantCounts> variantCounts=variantsCountsList.getVariantCounts(mapKey,chr );
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
    public static List<VariantCounts> getVariantCounts(int speciesTypeKey) throws Exception {
        List<VariantCounts> variantCounts= new ArrayList<>();

        String sql="select count(*) as tot, variant_type, sample_id, chromosome, map_key from variant v, variant_map_data vmd , variant_sample_detail vsd " +
                "where v.rgd_id=vmd.rgd_id " +
                "and v.rgd_id=vsd.rgd_id " +
                "and v.species_type_key=? " +
                "group by variant_type , sample_id, chromosome, map_key";
        try(Connection conn= DataSourceFactory.getInstance().getCarpeNovoDataSource().getConnection();
            PreparedStatement ps= conn.prepareStatement(sql);){
            ps.setInt(1, speciesTypeKey);
            ResultSet rs= ps.executeQuery();

            while (rs.next()){

                String variantType= rs.getString("variant_type");
                String chromosome=rs.getString("chromosome");
                int mapKey=rs.getInt("map_key");
                int sampleId=rs.getInt("sample_id");
                int count=rs.getInt("tot");
                VariantCounts vc=findInVariantCounts(variantCounts,mapKey,chromosome,sampleId);
                    vc.setChr(chromosome);
                    vc.setSampleId(sampleId);
                    vc.setMapKey(mapKey);
                    if(variantType.equalsIgnoreCase("snp") || variantType.equalsIgnoreCase("snv")){
                        int snv=0;
                        if(vc.getSnv()!=null){
                        snv+= Integer.parseInt(vc.getSnv());
                        }
                       snv+= count;
                        vc.setSnv(String.valueOf(snv));
                    }
                    if(variantType.equalsIgnoreCase("ins") || variantType.equalsIgnoreCase("insertion"))
                        vc.setIns(String.valueOf(count));
                    if(variantType.equalsIgnoreCase("del") || variantType.equalsIgnoreCase("deletion"))
                        vc.setDel(String.valueOf(count));

               variantCounts.add(vc);

            }
            rs.close();
        }
        return variantCounts;

    }
    public static VariantCounts findInVariantCounts(List<VariantCounts> variantCounts, int mapKey, String chromosome, int sampleId){

        for(VariantCounts vc:variantCounts){
            if(vc.getMapKey()==mapKey && vc.getChr().equals(chromosome)&& vc.getSampleId()==sampleId){
                VariantCounts tmpVc=vc;
                variantCounts.remove(vc);
                return tmpVc;
            }
        }
        return new VariantCounts();
    }
    public static List<VariantCounts> getVariants(List<Sample> samples, String chr, int mapKey, int speciesTypeKey)  {
        List<VariantCounts> variantCounts= new ArrayList<>();
        String sql="select count(variant_type) tot, variant_type from variant v " +
                " inner join variant_map_data vmd on vmd.rgd_id=v.rgd_id " +
                " inner join variant_sample_detail vsd on vsd.rgd_id=v.rgd_id " +
                "                  where v.species_type_key=? and " +
                "                 vsd.sample_id=?" ;

        if(chr!=null){
            sql=sql+"and vmd.chromosome=?";
        }
        sql=sql+ "   group by v.variant_type";
        for(Sample s:samples){
        try(Connection conn= DataSourceFactory.getInstance().getCarpeNovoDataSource().getConnection();
            PreparedStatement ps= conn.prepareStatement(sql);){

            int totalVariants=0;

                VariantCounts vc= new VariantCounts();
                int sampleId=s.getId();
                vc.setStrain(s.getAnalysisName());
                vc.setMapKey(mapKey);
                vc.setChr(chr);
                ps.setInt(1, speciesTypeKey);
                ps.setInt(2, sampleId);
                if(chr!=null)
                    ps.setString(3, chr);
                ResultSet rs= ps.executeQuery();
                int snvAndSnps=0;
                while(rs.next()){
                    String variantType= rs.getString("variant_type");

                   if(variantType.equalsIgnoreCase("snv") || variantType.equalsIgnoreCase("snp")){
                        snvAndSnps=snvAndSnps+rs.getInt("tot");
                    }

                    if(variantType.equalsIgnoreCase("ins") || variantType.equalsIgnoreCase("insertion"))
                        vc.setIns(rs.getString("tot"));
                    if(variantType.equalsIgnoreCase("del") || variantType.equalsIgnoreCase("deletion"))
                        vc.setDel(rs.getString("tot"));

                    totalVariants=totalVariants+rs.getInt("tot");
                 //     System.out.println(rs.getString("variant_type")+ " || "+ rs.getString("tot"));
                }
                vc.setSnv(String.valueOf(snvAndSnps));
                variantCounts.add(vc);
                rs.close();


        }catch (Exception e){
            e.printStackTrace();
        }
        }

        return variantCounts;
    }
    public static void main(String[] args) throws Exception {
        System.out.println("HELLO");
        SampleDAO sampleDAO=new SampleDAO();
        sampleDAO.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        List<Sample> samples=sampleDAO.getSamplesByMapKey(372);
        samples.add(sampleDAO.getSample(3000));
        List<VariantCounts> variantCounts= getVariantCounts(3);
        for(VariantCounts vc: variantCounts){
            System.out.println("SAMPLE ID:"+ vc.getSampleId());
            System.out.println("SNPS and SNVS:"+ vc.getSnv());
            System.out.println("DEL:"+ vc.getDel());
            System.out.println("INS:"+ vc.getIns()+"\n############################");
        }
    }

}
