package edu.mcw.rgd.indexer.dao.phenominer.model;

import edu.mcw.rgd.dao.spring.StringMapQuery;

import java.util.HashMap;
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

    private  Map<String, String > hierarchyMap;

    public Map<String, String> getHierarchyMap() {
        return hierarchyMap;
    }

    public void setHierarchyMap(Map<String, String> hierarchyMap) {
        this.hierarchyMap = hierarchyMap;
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
