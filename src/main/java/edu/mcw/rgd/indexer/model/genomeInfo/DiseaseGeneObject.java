package edu.mcw.rgd.indexer.model.genomeInfo;

import java.util.List;

/**
 * Created by jthota on 11/15/2017.
 */
public class DiseaseGeneObject {
    private String ontTermAccId;
    private String  ontTerm;
    private int  geneCount;

    public String getOntTermAccId() {
        return ontTermAccId;
    }

    public void setOntTermAccId(String ontTermAccId) {
        this.ontTermAccId = ontTermAccId;
    }

    public String getOntTerm() {
        return ontTerm;
    }

    public void setOntTerm(String ontTerm) {
        this.ontTerm = ontTerm;
    }

    public int getGeneCount() {
        return geneCount;
    }

    public void setGeneCount(int geneCount) {
        this.geneCount = geneCount;
    }
}
