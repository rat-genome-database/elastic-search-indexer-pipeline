package edu.mcw.rgd.indexer.indexers.expressionIndexer;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.spring.MeasurementMethodQuery;
import edu.mcw.rgd.datamodel.expression.ExpressionCondition;
import edu.mcw.rgd.datamodel.pheno.MeasurementMethod;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Conditions and measurement methods hang off a gene expression record, not off an expression value,
 * so they are loaded once for the whole run and shared by every gene thread. The two tables together hold
 * about 27,000 rows, while a single gene can have more than 20,000 expression values to index, so querying
 * them per value costs two round trips per document indexed.
 */
public class ExpressionRecordCache extends AbstractDAO {

    private static ExpressionRecordCache instance;

    private final Map<Integer, List<ExpressionCondition>> conditionsByRecordId;
    private final Map<Integer, List<MeasurementMethod>> measurementMethodsByRecordId;

    private ExpressionRecordCache() throws Exception {
        this.conditionsByRecordId = loadConditions();
        this.measurementMethodsByRecordId = loadMeasurementMethods();
        System.out.println("Expression record cache: conditions for " + conditionsByRecordId.size()
                + " records, measurement methods for " + measurementMethodsByRecordId.size() + " records");
    }

    public static synchronized ExpressionRecordCache getInstance() throws Exception {
        if (instance == null) {
            instance = new ExpressionRecordCache();
        }
        return instance;
    }

    public List<ExpressionCondition> getConditions(int geneExpressionRecordId) {
        List<ExpressionCondition> conditions = conditionsByRecordId.get(geneExpressionRecordId);
        return conditions != null ? conditions : Collections.emptyList();
    }

    public List<MeasurementMethod> getMeasurementMethods(int geneExpressionRecordId) {
        List<MeasurementMethod> methods = measurementMethodsByRecordId.get(geneExpressionRecordId);
        return methods != null ? methods : Collections.emptyList();
    }

    private Map<Integer, List<ExpressionCondition>> loadConditions() throws Exception {
        // the term is joined in, so no ontology lookup is needed while documents are being built; ordering
        // matches GeneExpressionDAO.getConditions() so the indexed lists keep curator ordinality
        String query = "SELECT c.gene_expression_exp_record_id, c.exp_cond_ont_id, c.exp_cond_ordinality," +
                " t.term, t.is_obsolete FROM experiment_condition c" +
                " LEFT JOIN ont_terms t ON t.term_acc = c.exp_cond_ont_id" +
                " WHERE c.gene_expression_exp_record_id IS NOT NULL" +
                " ORDER BY c.exp_cond_ordinality, c.experiment_condition_id";
        Map<Integer, List<ExpressionCondition>> map = new HashMap<>();
        for (Object row : this.execute(new ExpressionConditionQuery(this.getDataSource(), query))) {
            ConditionRow conditionRow = (ConditionRow) row;
            map.computeIfAbsent(conditionRow.recordId, k -> new ArrayList<>()).add(conditionRow.condition);
        }
        return freeze(map);
    }

    private Map<Integer, List<MeasurementMethod>> loadMeasurementMethods() throws Exception {
        String query = "SELECT * FROM measurement_method WHERE gene_expression_exp_record_id IS NOT NULL";
        Map<Integer, List<MeasurementMethod>> map = new HashMap<>();
        for (Object row : this.execute(new MeasurementMethodQuery(this.getDataSource(), query))) {
            MeasurementMethod method = (MeasurementMethod) row;
            map.computeIfAbsent(method.getGeneExpressionRecordId(), k -> new ArrayList<>()).add(method);
        }
        return freeze(map);
    }

    /** the record id is only needed to group the conditions, so it is kept out of the indexed object */
    private static class ConditionRow {
        int recordId;
        ExpressionCondition condition;
    }

    private static class ExpressionConditionQuery extends MappingSqlQuery<ConditionRow> {

        ExpressionConditionQuery(DataSource ds, String query) {
            super(ds, query);
        }

        @Override
        protected ConditionRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExpressionCondition condition = new ExpressionCondition();
            condition.setAccId(rs.getString("exp_cond_ont_id"));
            condition.setTerm(rs.getString("term"));
            condition.setObsolete(rs.getInt("is_obsolete"));
            condition.setOrdinality(rs.getInt("exp_cond_ordinality"));
            if (rs.wasNull()) {
                condition.setOrdinality(null);
            }

            ConditionRow row = new ConditionRow();
            row.recordId = rs.getInt("gene_expression_exp_record_id");
            row.condition = condition;
            return row;
        }
    }

    /** the lists are handed to every thread that indexes a record, so nothing may modify them afterwards */
    private <T> Map<Integer, List<T>> freeze(Map<Integer, List<T>> map) {
        for (Map.Entry<Integer, List<T>> entry : map.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(map);
    }
}
