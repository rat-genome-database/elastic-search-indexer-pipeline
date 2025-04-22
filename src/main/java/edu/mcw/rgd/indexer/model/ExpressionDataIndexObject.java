package edu.mcw.rgd.indexer.model;

import java.util.Map;

public class ExpressionDataIndexObject {
   private String geneSymbol;
   private String geneRgdId;
   private String sampleId;
   private String strainAcc;
   private String strainTerm;
   private String tissueAcc;
   private String tissueTerm;
   private double expressionValue;
   private String expressionUnit;
   private String expressionLevel;
   private String species;
   private String condition;
   private Map<String, String> metaData;

    public String getExpressionLevel() {
        return expressionLevel;
    }

    public void setExpressionLevel(String expressionLevel) {
        this.expressionLevel = expressionLevel;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getGeneRgdId() {
        return geneRgdId;
    }

    public void setGeneRgdId(String geneRgdId) {
        this.geneRgdId = geneRgdId;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getStrainAcc() {
        return strainAcc;
    }

    public void setStrainAcc(String strainAcc) {
        this.strainAcc = strainAcc;
    }

    public String getStrainTerm() {
        return strainTerm;
    }

    public void setStrainTerm(String strainTerm) {
        this.strainTerm = strainTerm;
    }

    public String getTissueAcc() {
        return tissueAcc;
    }

    public void setTissueAcc(String tissueAcc) {
        this.tissueAcc = tissueAcc;
    }

    public String getTissueTerm() {
        return tissueTerm;
    }

    public void setTissueTerm(String tissueTerm) {
        this.tissueTerm = tissueTerm;
    }

    public double getExpressionValue() {
        return expressionValue;
    }

    public void setExpressionValue(double expressionValue) {
        this.expressionValue = expressionValue;
    }

    public String getExpressionUnit() {
        return expressionUnit;
    }

    public void setExpressionUnit(String expressionUnit) {
        this.expressionUnit = expressionUnit;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }
}
