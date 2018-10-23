package edu.mcw.rgd.indexer.model;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.QTL;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.VariantInfo;

import java.util.List;

/**
 * Created by jthota on 6/14/2017.
 */
public class SpeciesObject {
    private String name;
    private int geneCount;
    private int qtlCount;
    private int strainCount;
    private int variantCount;

    public int getGeneCount() {
        return geneCount;
    }

    public void setGeneCount(int geneCount) {
        this.geneCount = geneCount;
    }

    public int getQtlCount() {
        return qtlCount;
    }

    public void setQtlCount(int qtlCount) {
        this.qtlCount = qtlCount;
    }

    public int getStrainCount() {
        return strainCount;
    }

    public void setStrainCount(int strainCount) {
        this.strainCount = strainCount;
    }

    public int getVariantCount() {
        return variantCount;
    }

    public void setVariantCount(int variantCount) {
        this.variantCount = variantCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
