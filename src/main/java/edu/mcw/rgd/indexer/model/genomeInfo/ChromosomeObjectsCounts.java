package edu.mcw.rgd.indexer.model.genomeInfo;

import edu.mcw.rgd.datamodel.Chromosome;

/**
 * Created by jthota on 11/7/2017.
 */
public class ChromosomeObjectsCounts {
    private String chromosomeNumber;
    private String assembly;
    private GeneCounts geneCounts;
    private Chromosome chromosome;
    private StringBuffer pieData;

    public String getChromosomeNumber() {
        return chromosomeNumber;
    }

    public void setChromosomeNumber(String chromosomeNumber) {
        this.chromosomeNumber = chromosomeNumber;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public GeneCounts getGeneCounts() {
        return geneCounts;
    }

    public void setGeneCounts(GeneCounts geneCounts) {
        this.geneCounts = geneCounts;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }

    public StringBuffer getPieData() {
        return pieData;
    }

    public void setPieData(StringBuffer pieData) {
        this.pieData = pieData;
    }
}
