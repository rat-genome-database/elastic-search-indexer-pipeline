package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.dao.spring.IntListQuery;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.variants.SampleManager;
import edu.mcw.rgd.indexer.model.MapInfo;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantDao extends AbstractDAO {
    MapDAO mapDAO = new MapDAO();
    Map<Integer, edu.mcw.rgd.datamodel.Map> rgdMaps=new HashMap<>();
    VariantInfoDAO variantInfoDAO=new VariantInfoDAO();

    public List<Integer> getUniqueVariantsIds( String chr, int mapKey, int speciesTypeKey) throws Exception {
        String sql ="select v.rgd_id from variant v, variant_map_data vmd  " +
                "where v.rgd_id=vmd.rgd_id " +
                " and v.species_type_key=? " +
                " and vmd.chromosome=? " +
                " and vmd.map_key=?";  //Total RECORD COUNT: 1888283; chr:1; map_key:360
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
        return execute(query, mapKey, mapKey,mapKey);

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

        return   execute(query, mapKey, mapKey);

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
    public List<MapInfo> getMapData(VariantIndex vi) throws Exception {
        List<MapInfo> mapList= new ArrayList<>();
        MapInfo map= new MapInfo();
        map.setChromosome(vi.getChromosome());
        map.setStartPos(vi.getStartPos());
        map.setStopPos(vi.getEndPos());
        if(rgdMaps.get(vi.getMapKey())==null){
            edu.mcw.rgd.datamodel.Map m = mapDAO.getMapByKey(vi.getMapKey());
            rgdMaps.put(vi.getMapKey(), m);
        }
        map.setMap(rgdMaps.get(vi.getMapKey()).getDescription());
        map.setRank(rgdMaps.get(vi.getMapKey()).getRank());

        mapList.add(map);

        return mapList;
    }

    public String getClinvarInfo(int variantRgdId) throws Exception {

        VariantInfo info=variantInfoDAO.getVariant(variantRgdId)  ;
        return  info.getClinicalSignificance();
    }
}
