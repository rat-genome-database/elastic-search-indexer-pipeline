package edu.mcw.rgd.indexer.indexers.gviewerIndexer;

import edu.mcw.rgd.datamodel.annotation.GViewerIndex;
import edu.mcw.rgd.indexer.model.IndexDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class GViewerIndexer implements Runnable {

    private static final String SQL = """
            SELECT m.chromosome, m.start_pos, m.stop_pos, z.rgd_id, z.object_symbol, z.object_type, z.term, z.term_acc
            FROM maps_data m, (
              SELECT annotated_object_rgd_id rgd_id,
                     NVL(object_symbol, object_name) object_symbol,
                     DECODE(rgd_object_key, 1, 'gene', 6, 'qtl', 'strain') object_type,
                     t.term, t.term_acc
              FROM full_annot a, ont_terms t
              WHERE rgd_object_key IN (1, 5, 6)
                AND a.term_acc = t.term_acc AND t.is_obsolete = 0
                AND EXISTS (SELECT 1 FROM ont_term_stats2 s
                            WHERE s.term_acc = t.term_acc
                              AND stat_name = 'annotated_object_count'
                              AND with_children > 0)
                AND a.term_acc IN (
                  SELECT child_term_acc FROM ont_dag
                  START WITH child_term_acc IN (
                    SELECT term_acc FROM ont_terms t WHERE t.ont_id = ?
                  ) CONNECT BY PRIOR child_term_acc = parent_term_acc UNION
                                                                      SELECT ? FROM dual
                )
            ) z
            WHERE z.rgd_id = m.rgd_id AND m.map_key = ?
            """;

    private final String ontId;
    private final int mapKey;
    private final int speciesTypeKey;
    private final DataSource dataSource;
    private final Logger log = LogManager.getLogger("gviewer");

    public GViewerIndexer(String ontId, int mapKey, int speciesTypeKey,  DataSource dataSource) {
        this.ontId = ontId;
        this.mapKey = mapKey;
        this.speciesTypeKey = speciesTypeKey;
        this.dataSource = dataSource;
    }

    @Override
    public void run() {
        String thread = Thread.currentThread().getName();
        log.info(thread + ": gviewer ontId=" + ontId + " mapKey=" + mapKey + " started " + new Date());
        long count = 0;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL,
                     ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            ps.setFetchSize(1000);
            ps.setString(1, ontId);
            ps.setString(2, ontId);
            ps.setInt(3, mapKey);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GViewerIndex idx = new GViewerIndex();
                    idx.setMapKey(mapKey);
                    idx.setSpeciesTypeKey(speciesTypeKey);
                    idx.setChromosome(rs.getString("chromosome"));
                    idx.setAnnotatedObjectRgdId(rs.getInt("rgd_id"));
                    idx.setStartPos(rs.getInt("start_pos"));
                    idx.setStopPos(rs.getInt("stop_pos"));
                    idx.setObjectSymbol(rs.getString("object_symbol"));
                    idx.setObjectType(rs.getString("object_type"));
                    idx.setTerm(rs.getString("term"));
                    idx.setTermAcc(rs.getString("term_acc"));
                    IndexDocument.index(idx);
                    count++;
                }
            }
        } catch (SQLException e) {
            log.error(thread + ": gviewer ontId=" + ontId + " failed", e);
            throw new RuntimeException(e);
        }
        log.info(thread + ": gviewer ontId=" + ontId + " end " + new Date() + " (" + count + " records)");
    }
}
