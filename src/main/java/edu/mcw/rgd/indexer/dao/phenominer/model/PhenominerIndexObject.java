package edu.mcw.rgd.indexer.dao.phenominer.model;

import edu.mcw.rgd.dao.spring.StringMapQuery;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PhenominerIndexObject {
    private int recordId;
    private String rsTermAcc;
    private String rsTerm;
    private String rsRootTerm;
    private String rsRootTermAcc;

    private String rsTopLevelTerm;
    private String rsTopLevelTermAcc;

    private String rsParentTerm;
    private String rsParentTermAcc;
    private List<String> rsTerms;
    private List<String> cmoTerms;
    private List<String> mmoTerms;
    private List<String> xcoTerms;

    private LinkedHashMap<String, String > rsHierarchyMap;
    private LinkedHashMap<String, String > cmoHierarchyMap;
    private LinkedHashMap<String, String > mmoHierarchyMap;
    private LinkedHashMap<String, String > xcoHierarchyMap;

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
}
