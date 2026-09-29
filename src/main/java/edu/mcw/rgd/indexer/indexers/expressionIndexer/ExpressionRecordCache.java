package edu.mcw.rgd.indexer.indexers.expressionIndexer;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.spring.ConditionQuery;
import edu.mcw.rgd.dao.spring.MeasurementMethodQuery;
import edu.mcw.rgd.datamodel.pheno.Condition;
import edu.mcw.rgd.datamodel.pheno.MeasurementMethod;

import java.util.*;

/**
 * Conditions and measurement methods hang off a gene expression record, not off an expression value,
 * so they are loaded once for the whole run and shared by every gene thread. The two tables together hold
 * about 27,000 rows, while a single gene can have more than 20,000 expression values to index, so querying
 * them per value costs two round trips per document indexed.
 */
public class ExpressionRecordCache extends AbstractDAO {

    private static ExpressionRecordCache instance;

    private final Map<Integer, List<Condition>> conditionsByRecordId;
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

    public List<Condition> getConditions(int geneExpressionRecordId) {
        List<Condition> conditions = conditionsByRecordId.get(geneExpressionRecordId);
        return conditions != null ? conditions : Collections.emptyList();
    }

    public List<MeasurementMethod> getMeasurementMethods(int geneExpressionRecordId) {
        List<MeasurementMethod> methods = measurementMethodsByRecordId.get(geneExpressionRecordId);
        return methods != null ? methods : Collections.emptyList();
    }

    private Map<Integer, List<Condition>> loadConditions() throws Exception {
        // same ordering as GeneExpressionDAO.getConditions(), so the indexed lists keep curator ordinality
        String query = "SELECT * FROM experiment_condition WHERE gene_expression_exp_record_id IS NOT NULL " +
                "ORDER BY exp_cond_ordinality, experiment_condition_id";
        Map<Integer, List<Condition>> map = new HashMap<>();
        List<Condition> conditions = new ArrayList<>();
        for (Object row : this.execute(new ConditionQuery(this.getDataSource(), query))) {
            Condition condition = (Condition) row;
            conditions.add(condition);
            map.computeIfAbsent(condition.getGeneExpressionRecordId(), k -> new ArrayList<>()).add(condition);
        }
        generateDescriptions(conditions);
        return freeze(map);
    }

    /**
     * Condition.getConditionDescription2() fills in conditionDescription the first time it is called, with an
     * ont_terms lookup, so leaving it to serialization would race the gene threads against each other: whichever
     * document reaches the bulk processor first goes out without the field. The descriptions are generated here
     * instead, before any thread sees the conditions, and only once per distinct description.
     */
    private void generateDescriptions(List<Condition> conditions) {
        Map<String, String> descriptionsByKey = new HashMap<>();
        for (Condition condition : conditions) {
            // the values the description is built from: ontology term, value with its units, and duration
            String key = condition.getOntologyId() + "|" + condition.getValue() + "|" + condition.getUnits()
                    + "|" + condition.getDurationLowerBound() + "|" + condition.getDurationUpperBound();
            String description = descriptionsByKey.get(key);
            try {
                if (description == null) {
                    descriptionsByKey.put(key, condition.getConditionDescription2());
                } else {
                    condition.setConditionDescription(description);
                }
            } catch (Exception e) {
                // an ontology id that no longer resolves; the condition is still indexed, just without a description
                System.out.println("Cannot describe condition " + condition.getId() + ": " + e.getMessage());
            }
        }
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

    /** the lists are handed to every thread that indexes a record, so nothing may modify them afterwards */
    private <T> Map<Integer, List<T>> freeze(Map<Integer, List<T>> map) {
        for (Map.Entry<Integer, List<T>> entry : map.entrySet()) {
            entry.setValue(Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(map);
    }
}
