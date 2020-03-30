package edu.mcw.rgd.indexer.model.findModels;

import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.datamodel.ontologyx.Term;

import java.util.List;

/**
 * Created by jthota on 3/3/2020.
 */
public class ModelIndexObject {
    private int annotatedObjectRgdId;
    private String annotatedObjectName;
    private String annotatedObjectSymbol;
    private String term;
    private String termAcc;
    private List<Term> parentTerms;
    private String species;
    private String aspect;
    private List<String> qualifiers;
    private List<String> evidenceCodes;
    private List<Integer> refRgdIds;
    private String withInfo;
    private String withInfoTerms;

    public String getWithInfoTerms() {
        return withInfoTerms;
    }

    public void setWithInfoTerms(String withInfoTerms) {
        this.withInfoTerms = withInfoTerms;
    }

    public String getWithInfo() {
        return withInfo;
    }
    public void setWithInfo(String withInfo) {
        this.withInfo = withInfo;
    }



  /*  private List<Integer> references;

    public List<Integer> getReferences() {
        return references;
    }

    public void setReferences(List<Integer> references) {
        this.references = references;
    }*/



    public int getAnnotatedObjectRgdId() {
        return annotatedObjectRgdId;
    }

    public void setAnnotatedObjectRgdId(int annotatedObjectRgdId) {
        this.annotatedObjectRgdId = annotatedObjectRgdId;
    }

    public String getAnnotatedObjectName() {
        return annotatedObjectName;
    }

    public void setAnnotatedObjectName(String annotatedObjectName) {
        this.annotatedObjectName = annotatedObjectName;
    }

    public String getAnnotatedObjectSymbol() {
        return annotatedObjectSymbol;
    }

    public void setAnnotatedObjectSymbol(String annotatedObjectSymbol) {
        this.annotatedObjectSymbol = annotatedObjectSymbol;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTermAcc() {
        return termAcc;
    }

    public void setTermAcc(String termAcc) {
        this.termAcc = termAcc;
    }

    public List<Term> getParentTerms() {
        return parentTerms;
    }

    public void setParentTerms(List<Term> parentTerms) {
        this.parentTerms = parentTerms;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    public List<String> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(List<String> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public List<String> getEvidenceCodes() {
        return evidenceCodes;
    }

    public void setEvidenceCodes(List<String> evidenceCodes) {
        this.evidenceCodes = evidenceCodes;
    }

    public List<Integer> getRefRgdIds() {
        return refRgdIds;
    }

    public void setRefRgdIds(List<Integer> refRgdIds) {
        this.refRgdIds = refRgdIds;
    }
}
