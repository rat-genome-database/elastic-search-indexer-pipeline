package edu.mcw.rgd.indexer.model.genomeInfo;

/**
 * Created by jthota on 12/6/2017.
 */
public class VariantCounts {
    private int mapKey;
    private String strain;
    private int sampleId;
    private String totalVariants;
    private String snp;
    private String del;
    private String ins;
    private String snv;
    private String chr;

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public String getSnv() {
        return snv;
    }

    public void setSnv(String snv) {
        this.snv = snv;
    }

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public String getStrain() {
        return strain;
    }

    public void setStrain(String strain) {
        this.strain = strain;
    }

    public String getTotalVariants() {
        return totalVariants;
    }

    public void setTotalVariants(String totalVariants) {
        this.totalVariants = totalVariants;
    }

    public String getSnp() {
        return snp;
    }

    public void setSnp(String snp) {
        this.snp = snp;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public String getIns() {
        return ins;
    }

    public void setIns(String ins) {
        this.ins = ins;
    }
}
