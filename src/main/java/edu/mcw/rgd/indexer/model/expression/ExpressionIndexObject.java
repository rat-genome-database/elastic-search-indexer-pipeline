package edu.mcw.rgd.indexer.model.expression;

import edu.mcw.rgd.indexer.model.GeneIndexObject;

import java.util.Set;

public class ExpressionIndexObject extends GeneIndexObject {


    private Set<String> geoSampleAcc;
    private Set<String> bioSampleId;
    private Set<String> lifeStage;
    private Set<String> sex;
    private Set<String> strainAccId;
    private Set<String> strainTerms;
    private Set<String>tissueAccId;
    private Set<String>tissueTerms;
    private Set<String> cellTypeAccId;
    private Set<String> cellTypeTerms;
    private Set<String> expressionLevel;
    private Set<String>expressionUnit;
    private Set<Integer>experimentId;
    private Set<String> geoSeriesAcc;
    private Set<Integer> sampleId;
    private Set<String> sample;
    private Set<Integer> mapKey;
    private Set<String> clinicalMeasurementId;
    private Set<String> clinicalMeasurementTerms;

    public Set<String> getGeoSeriesAcc() {
        return geoSeriesAcc;
    }

    public void setGeoSeriesAcc(Set<String> geoSeriesAcc) {
        this.geoSeriesAcc = geoSeriesAcc;
    }

    public Set<Integer> getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Set<Integer> experimentId) {
        this.experimentId = experimentId;
    }

    public Set<Integer> getSampleId() {
        return sampleId;
    }

    public void setSampleId(Set<Integer> sampleId) {
        this.sampleId = sampleId;
    }


    public Set<String> getSample() {
        return sample;
    }

    public void setSample(Set<String> sample) {
        this.sample = sample;
    }

    public Set<String> getCellTypeAccId() {
        return cellTypeAccId;
    }

    public void setCellTypeAccId(Set<String> cellTypeAccId) {
        this.cellTypeAccId = cellTypeAccId;
    }

    public Set<String> getCellTypeTerms() {
        return cellTypeTerms;
    }

    public void setCellTypeTerms(Set<String> cellTypeTerms) {
        this.cellTypeTerms = cellTypeTerms;
    }

    public Set<String> getStrainTerms() {
        return strainTerms;
    }

    public void setStrainTerms(Set<String> strainTerms) {
        this.strainTerms = strainTerms;
    }

    public Set<String> getTissueTerms() {
        return tissueTerms;
    }

    public void setTissueTerms(Set<String> tissueTerms) {
        this.tissueTerms = tissueTerms;
    }

    public Set<String> getClinicalMeasurementTerms() {
        return clinicalMeasurementTerms;
    }

    public void setClinicalMeasurementTerms(Set<String> clinicalMeasurementTerms) {
        this.clinicalMeasurementTerms = clinicalMeasurementTerms;
    }

    public Set<String> getGeoSampleAcc() {
        return geoSampleAcc;
    }

    public void setGeoSampleAcc(Set<String> geoSampleAcc) {
        this.geoSampleAcc = geoSampleAcc;
    }

    public Set<String> getBioSampleId() {
        return bioSampleId;
    }

    public void setBioSampleId(Set<String> bioSampleId) {
        this.bioSampleId = bioSampleId;
    }

    public Set<String> getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(Set<String> lifeStage) {
        this.lifeStage = lifeStage;
    }

    public Set<String> getSex() {
        return sex;
    }

    public void setSex(Set<String> sex) {
        this.sex = sex;
    }

    public Set<String> getStrainAccId() {
        return strainAccId;
    }

    public void setStrainAccId(Set<String> strainAccId) {
        this.strainAccId = strainAccId;
    }

    public Set<String> getTissueAccId() {
        return tissueAccId;
    }

    public void setTissueAccId(Set<String> tissueAccId) {
        this.tissueAccId = tissueAccId;
    }

    public Set<String> getExpressionLevel() {
        return expressionLevel;
    }

    public void setExpressionLevel(Set<String> expressionLevel) {
        this.expressionLevel = expressionLevel;
    }

    public Set<String> getExpressionUnit() {
        return expressionUnit;
    }

    public void setExpressionUnit(Set<String> expressionUnit) {
        this.expressionUnit = expressionUnit;
    }

//    public Set<Integer> getMapKey() {
//        return mapKey;
//    }

    public void setMapKey(Set<Integer> mapKey) {
        this.mapKey = mapKey;
    }

    public Set<String> getClinicalMeasurementId() {
        return clinicalMeasurementId;
    }

    public void setClinicalMeasurementId(Set<String> clinicalMeasurementId) {
        this.clinicalMeasurementId = clinicalMeasurementId;
    }
}
