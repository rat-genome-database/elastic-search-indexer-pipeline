package edu.mcw.rgd.indexer.dao.phenominer.model;

import edu.mcw.rgd.dao.spring.StringMapQuery;
import edu.mcw.rgd.datamodel.pheno.IndividualRecord;

import java.util.*;

public class PhenominerIndexObject {
    private int recordId;
    private String rsTermAcc;
    private String rsTerm;

    private String cmoTermAcc;
    private String cmoTerm;
    private String cmoTermWithUnits;

    private String mmoTermAcc;
    private String mmoTerm;
    private String vtTermAcc;
    private String vtTerm;
    private String vtTerm3Acc;
    private String vtTerm3;
    private String vtTerm2Acc;
    private String vtTerm2;
    private List<String> xcoTermAcc;
    private Set<String> xcoTerm;

    private String xcoConditionDescription;
    private String rsRootTerm;
    private String rsRootTermAcc;

    private String sex;
    private int ageLowBound;
    private int ageHighBound;
    private String units;

    private String rsTopLevelTerm;
    private String rsTopLevelTermAcc;

    private String rsParentTerm;
    private String rsParentTermAcc;
    private List<String> rsTerms; // terms and synonyms
    private List<String> cmoTerms;// terms and synonyms
    private List<String> mmoTerms;// terms and synonyms
    private List<String> xcoTerms;// terms and synonyms

    public String getVtTerm3Acc() {
        return vtTerm3Acc;
    }

    public void setVtTerm3Acc(String vtTerm3Acc) {
        this.vtTerm3Acc = vtTerm3Acc;
    }

    public String getVtTerm3() {
        return vtTerm3;
    }

    public void setVtTerm3(String vtTerm3) {
        this.vtTerm3 = vtTerm3;
    }

    public String getVtTerm2Acc() {
        return vtTerm2Acc;
    }

    public void setVtTerm2Acc(String vtTerm2Acc) {
        this.vtTerm2Acc = vtTerm2Acc;
    }

    public String getVtTerm2() {
        return vtTerm2;
    }

    public void setVtTerm2(String vtTerm2) {
        this.vtTerm2 = vtTerm2;
    }

    public List<String> getVtTerms() {
        return vtTerms;
    }

    public void setVtTerms(List<String> vtTerms) {
        this.vtTerms = vtTerms;
    }

    private List<String> vtTerms;

    private LinkedHashMap<String, String > rsHierarchyMap;
    private LinkedHashMap<String, String > cmoHierarchyMap;
    private LinkedHashMap<String, String > mmoHierarchyMap;
    private LinkedHashMap<String, String > xcoHierarchyMap;

    private String sem;
    private String sd;
    private String value;
    private int studyId;
    private String study;
    private String experimentName;
    private String experimentNotes;
    private int numberOfAnimals;
    private String sampleNotes;
    private String formula;
    private String clinicalMeasurementNotes;
    private String averageType;
    private String methodSite;
    private String methodDuration;
    private String methodNotes;
    private String postInsultType;
    private int postInsultTimeValue;
    private String postInsultTimeUnit;
    private int refRgdId;
    private List<IndividualRecord> individualRecords;

    private int speciesTypeKey;
    private String species;

    public int getSpeciesTypeKey() {
        return speciesTypeKey;
    }

    public void setSpeciesTypeKey(int speciesTypeKey) {
        this.speciesTypeKey = speciesTypeKey;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public List<IndividualRecord> getIndividualRecords() {
        return individualRecords;
    }

    public void setIndividualRecords(List<IndividualRecord> individualRecords) {
        this.individualRecords = individualRecords;
    }

    public int getRefRgdId() {
        return refRgdId;
    }

    public void setRefRgdId(int refRgdId) {
        this.refRgdId = refRgdId;
    }

    public String getCmoTermWithUnits() {
        return cmoTermWithUnits;
    }

    public void setCmoTermWithUnits(String cmoTermWithUnits) {
        this.cmoTermWithUnits = cmoTermWithUnits;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStudyId() {
        return studyId;
    }

    public void setStudyId(int studyId) {
        this.studyId = studyId;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getExperimentNotes() {
        return experimentNotes;
    }

    public void setExperimentNotes(String experimentNotes) {
        this.experimentNotes = experimentNotes;
    }

    public int getNumberOfAnimals() {
        return numberOfAnimals;
    }

    public void setNumberOfAnimals(int numberOfAnimals) {
        this.numberOfAnimals = numberOfAnimals;
    }

    public String getSampleNotes() {
        return sampleNotes;
    }

    public void setSampleNotes(String sampleNotes) {
        this.sampleNotes = sampleNotes;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getClinicalMeasurementNotes() {
        return clinicalMeasurementNotes;
    }

    public void setClinicalMeasurementNotes(String clinicalMeasurementNotes) {
        this.clinicalMeasurementNotes = clinicalMeasurementNotes;
    }

    public String getAverageType() {
        return averageType;
    }

    public void setAverageType(String averageType) {
        this.averageType = averageType;
    }

    public String getMethodSite() {
        return methodSite;
    }

    public void setMethodSite(String methodSite) {
        this.methodSite = methodSite;
    }

    public String getMethodDuration() {
        return methodDuration;
    }

    public void setMethodDuration(String methodDuration) {
        this.methodDuration = methodDuration;
    }

    public String getMethodNotes() {
        return methodNotes;
    }

    public void setMethodNotes(String methodNotes) {
        this.methodNotes = methodNotes;
    }

    public String getPostInsultType() {
        return postInsultType;
    }

    public void setPostInsultType(String postInsultType) {
        this.postInsultType = postInsultType;
    }

    public int getPostInsultTimeValue() {
        return postInsultTimeValue;
    }

    public void setPostInsultTimeValue(int postInsultTimeValue) {
        this.postInsultTimeValue = postInsultTimeValue;
    }

    public String getPostInsultTimeUnit() {
        return postInsultTimeUnit;
    }

    public void setPostInsultTimeUnit(String postInsultTimeUnit) {
        this.postInsultTimeUnit = postInsultTimeUnit;
    }

    public String getCmoTermAcc() {
        return cmoTermAcc;
    }

    public void setCmoTermAcc(String cmoTermAcc) {
        this.cmoTermAcc = cmoTermAcc;
    }

    public String getCmoTerm() {
        return cmoTerm;
    }

    public void setCmoTerm(String cmoTerm) {
        this.cmoTerm = cmoTerm;
    }

    public String getMmoTermAcc() {
        return mmoTermAcc;
    }

    public void setMmoTermAcc(String mmoTermAcc) {
        this.mmoTermAcc = mmoTermAcc;
    }

    public String getMmoTerm() {
        return mmoTerm;
    }

    public void setMmoTerm(String mmoTerm) {
        this.mmoTerm = mmoTerm;
    }

    public List<String> getXcoTermAcc() {
        return xcoTermAcc;
    }

    public void setXcoTermAcc(List<String> xcoTermAcc) {
        this.xcoTermAcc = xcoTermAcc;
    }


    public Set<String> getXcoTerm() {
        return xcoTerm;
    }

    public void setXcoTerm(Set<String> xcoTerm) {
        this.xcoTerm = xcoTerm;
    }

    public String getXcoConditionDescription() {
        return xcoConditionDescription;
    }

    public void setXcoConditionDescription(String xcoConditionDescription) {
        this.xcoConditionDescription = xcoConditionDescription;
    }

    public List<String> getRsTerms() {
        return rsTerms;
    }

    public void setRsTerms(List<String> rsTerms) {
        this.rsTerms = rsTerms;
    }

    public List<String> getCmoTerms() {
        return cmoTerms;
    }

    public void setCmoTerms(List<String> cmoTerms) {
        this.cmoTerms = cmoTerms;
    }

    public List<String> getMmoTerms() {
        return mmoTerms;
    }

    public void setMmoTerms(List<String> mmoTerms) {
        this.mmoTerms = mmoTerms;
    }

    public List<String> getXcoTerms() {
        return xcoTerms;
    }

    public void setXcoTerms(List<String> xcoTerms) {
        this.xcoTerms = xcoTerms;
    }

    public LinkedHashMap<String, String> getCmoHierarchyMap() {
        return cmoHierarchyMap;
    }

    public void setCmoHierarchyMap(LinkedHashMap<String, String> cmoHierarchyMap) {
        this.cmoHierarchyMap = cmoHierarchyMap;
    }

    public LinkedHashMap<String, String> getMmoHierarchyMap() {
        return mmoHierarchyMap;
    }

    public void setMmoHierarchyMap(LinkedHashMap<String, String> mmoHierarchyMap) {
        this.mmoHierarchyMap = mmoHierarchyMap;
    }

    public LinkedHashMap<String, String> getXcoHierarchyMap() {
        return xcoHierarchyMap;
    }

    public void setXcoHierarchyMap(LinkedHashMap<String, String> xcoHierarchyMap) {
        this.xcoHierarchyMap = xcoHierarchyMap;
    }

    public LinkedHashMap<String, String> getRsHierarchyMap() {
        return rsHierarchyMap;
    }

    public void setRsHierarchyMap(LinkedHashMap<String, String> rsHierarchyMap) {
        this.rsHierarchyMap = rsHierarchyMap;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getRsTermAcc() {
        return rsTermAcc;
    }

    public void setRsTermAcc(String rsTermAcc) {
        this.rsTermAcc = rsTermAcc;
    }

    public String getRsTerm() {
        return rsTerm;
    }

    public void setRsTerm(String rsTerm) {
        this.rsTerm = rsTerm;
    }



    public String getRsRootTerm() {
        return rsRootTerm;
    }

    public void setRsRootTerm(String rsRootTerm) {
        this.rsRootTerm = rsRootTerm;
    }

    public String getRsRootTermAcc() {
        return rsRootTermAcc;
    }

    public void setRsRootTermAcc(String rsRootTermAcc) {
        this.rsRootTermAcc = rsRootTermAcc;
    }

    public String getRsTopLevelTerm() {
        return rsTopLevelTerm;
    }

    public void setRsTopLevelTerm(String rsTopLevelTerm) {
        this.rsTopLevelTerm = rsTopLevelTerm;
    }

    public String getRsTopLevelTermAcc() {
        return rsTopLevelTermAcc;
    }

    public void setRsTopLevelTermAcc(String rsTopLevelTermAcc) {
        this.rsTopLevelTermAcc = rsTopLevelTermAcc;
    }

    public String getRsParentTerm() {
        return rsParentTerm;
    }

    public void setRsParentTerm(String rsParentTerm) {
        this.rsParentTerm = rsParentTerm;
    }

    public String getRsParentTermAcc() {
        return rsParentTermAcc;
    }

    public void setRsParentTermAcc(String rsParentTermAcc) {
        this.rsParentTermAcc = rsParentTermAcc;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAgeLowBound() {
        return ageLowBound;
    }

    public void setAgeLowBound(int ageLowBound) {
        this.ageLowBound = ageLowBound;
    }

    public int getAgeHighBound() {
        return ageHighBound;
    }

    public void setAgeHighBound(int ageHighBound) {
        this.ageHighBound = ageHighBound;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getVtTermAcc() {
        return vtTermAcc;
    }

    public void setVtTermAcc(String vtTermAcc) {
        this.vtTermAcc = vtTermAcc;
    }

    public String getVtTerm() {
        return vtTerm;
    }

    public void setVtTerm(String vtTerm) {
        this.vtTerm = vtTerm;
    }
}
