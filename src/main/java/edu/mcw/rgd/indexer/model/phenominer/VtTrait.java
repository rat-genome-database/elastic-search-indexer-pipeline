package edu.mcw.rgd.indexer.model.phenominer;

/**
 * One vertebrate trait on a phenominer record: its ontology accession and its term name.
 *
 * <p>A record can carry up to three traits, which the curation database models as three columns
 * (trait_id, trait2_id, trait3_id) and {@link PhenominerIndexObject} has always mirrored as three
 * pairs of scalar fields - vtTermAcc/vtTerm, vtTerm2Acc/vtTerm2, vtTerm3Acc/vtTerm3. That shape
 * forces every consumer to know how many slots there are: the record table printed only the
 * first, and the facet aggregation still counts only the first, so a record annotated to both
 * "tactile sensory behavior trait" and "mechanical nociception trait" appears under one of them.
 *
 * <p>The traits are additionally indexed as a list of these objects, under vtTraits, so a consumer
 * can read them without counting slots: one terms aggregation over vtTraits.term.keyword facets
 * every trait on a record, and one terms query over vtTraits.termAcc.keyword replaces the three
 * should-clauses PhenominerService builds today. Accession and name are kept together in one
 * object rather than in two parallel arrays so they cannot drift out of step.
 *
 * <p>The scalar slots are still populated, so nothing that reads them has to change at the same
 * time as this field appears.
 */
public class VtTrait {

    private String termAcc;
    private String term;

    public VtTrait() {
    }

    public VtTrait(String termAcc, String term) {
        this.termAcc = termAcc;
        this.term = term;
    }

    public String getTermAcc() {
        return termAcc;
    }

    public void setTermAcc(String termAcc) {
        this.termAcc = termAcc;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
