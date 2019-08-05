package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.impl.StatisticsDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jthota on 10/23/2017.
 */
public class StatsDAO extends StatisticsDAO {
    public Map<String, Integer> getMirnaTargetCountsMap(int mapKey, String chr)  {
        Map<String, Integer> targetsMap= new HashMap<>();
        Connection conn=null;
        PreparedStatement stmt=null;
        ResultSet rs=null;
        String sql="SELECT COUNT(gene_rgd_id) AS tot, target_type AS object_name \n" +
                "FROM rgd_ids r,  (SELECT DISTINCT gene_rgd_id,target_type FROM mirna_targets) t , maps_data m\n" +
                "WHERE r.rgd_id=gene_rgd_id \n" +
                "AND gene_rgd_id=m.rgd_id\n" +
                "AND r.object_status='ACTIVE'\n" +
                "AND m.map_key=?" ;
        try{
            if(chr!=null){
                sql=sql+" and m.chromosome=?";
            }
            sql=sql+ " GROUP BY target_type";
            conn= this.getConnection();
            stmt=conn.prepareStatement(sql);
            stmt.setInt(1,mapKey);
            if(chr!=null){
                stmt.setString(2, chr);
            }
            rs= stmt.executeQuery();
            while(rs.next()){
                targetsMap.put(rs.getString("object_name"), rs.getInt("tot"));
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

                try {
                    if(rs!=null)
                        rs.close();
                    if(stmt!=null)
                    stmt.close();

                    if(conn!=null)
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }


        return targetsMap;
    }
}
