package edu.mcw.rgd.indexer.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpressionDataIndexObject {
   private String geneSymbol;
   private int geneRgdId;
    private String geneSymbolWithRgdId;
   private String sampleId;
   private String strainAcc;
   private String strainTerm;
   private String tissueAcc;
   private String tissueTerm;
   private double expressionValue;
   private double valueMean;
   private double logValue;
   private String studyId;
   private String geoSeriesAcc;
   private String traitOntId;
   private String traitTerm;
   private String expressionUnit;
   private String expressionLevel;
   private String species;
   private String condition;
    private String conditionAcc;
    private List<MapInfo> mapDataList;
    private String lifeStage;
    private String computedSex;
    private String sex;
    private String unit;
    private String bioSampleId;
    private String geoSampleAcc;
   private Map<String, String> metaData;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(String lifeStage) {
        this.lifeStage = lifeStage;
    }

    public String getComputedSex() {
        return computedSex;
    }

    public void setComputedSex(String computedSex) {
        this.computedSex = computedSex;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBioSampleId() {
        return bioSampleId;
    }

    public void setBioSampleId(String bioSampleId) {
        this.bioSampleId = bioSampleId;
    }

    public String getGeoSampleAcc() {
        return geoSampleAcc;
    }

    public void setGeoSampleAcc(String geoSampleAcc) {
        this.geoSampleAcc = geoSampleAcc;
    }

    public String getConditionAcc() {
        return conditionAcc;
    }

    public void setConditionAcc(String conditionAcc) {
        this.conditionAcc = conditionAcc;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getGeoSeriesAcc() {
        return geoSeriesAcc;
    }

    public void setGeoSeriesAcc(String geoSeriesAcc) {
        this.geoSeriesAcc = geoSeriesAcc;
    }

    public String getTraitOntId() {
        return traitOntId;
    }

    public void setTraitOntId(String traitOntId) {
        this.traitOntId = traitOntId;
    }

    public String getTraitTerm() {
        return traitTerm;
    }

    public void setTraitTerm(String traitTerm) {
        this.traitTerm = traitTerm;
    }

    public double getLogValue() {
        return logValue;
    }

    public void setLogValue(double logValue) {
        this.logValue = logValue;
    }
    public String getGeneSymbolWithRgdId() {
        return geneSymbolWithRgdId;
    }

    public void setGeneSymbolWithRgdId(String geneSymbolWithRgdId) {
        this.geneSymbolWithRgdId = geneSymbolWithRgdId;
    }

    public double getValueMean() {
        return valueMean;
    }

    public void setValueMean(double valueMean) {
        this.valueMean = valueMean;
    }

    public List<MapInfo> getMapDataList() {
        return mapDataList;
    }

    public void setMapDataList(List<MapInfo> mapDataList) {
        this.mapDataList = mapDataList;
    }



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

    public int getGeneRgdId() {
        return geneRgdId;
    }

    public void setGeneRgdId(int geneRgdId) {
        this.geneRgdId = geneRgdId;
    }

    public double getExpressionValue() {
        return expressionValue;
    }

    public void setExpressionValue(double expressionValue) {
        this.expressionValue = expressionValue;
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
