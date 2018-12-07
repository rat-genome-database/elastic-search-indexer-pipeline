package edu.mcw.rgd.indexer.spring;

import edu.mcw.rgd.indexer.model.XdbObject;
import org.springframework.jdbc.object.MappingSqlQuery;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jthota on 12/6/2018.
 */
public class XdbObjectQuery extends MappingSqlQuery {
    public XdbObjectQuery(DataSource ds, String query){
        super(ds, query);
    }
    @Override
    protected Object mapRow(ResultSet rs, int i) throws SQLException {
        XdbObject object= new XdbObject();
        object.setRgdId(rs.getInt("rgd_id"));
        object.setAccId(rs.getString("acc_id"));
        return object;
    }
}
