package edu.mcw.rgd.indexer.model.genomeInfo;

/**
 * Created by jthota on 10/23/2017.
 */
public class AssemblyInfo {

    private String refSeqAssemblyAccession;
    private String ncbiLink;
    private String basePairs;
    private String totalLength;
    private String gapLength;
    private String  gapBetweenScaffolds;
    private String scaffolds;
    private String scaffoldN50;
    private String scaffoldL50;
    private String contigs;
    private String contigN50;
    private String contigL50;
    private String chromosome;

    public String getBasePairs() {
        return basePairs;
    }

    public void setBasePairs(String basePairs) {
        this.basePairs = basePairs;
    }

    public String getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(String totalLength) {
        this.totalLength = totalLength;
    }

    public String getGapLength() {
        return gapLength;
    }

    public void setGapLength(String gapLength) {
        this.gapLength = gapLength;
    }

    public String getGapBetweenScaffolds() {
        return gapBetweenScaffolds;
    }

    public void setGapBetweenScaffolds(String gapBetweenScaffolds) {
        this.gapBetweenScaffolds = gapBetweenScaffolds;
    }

    public String getScaffolds() {
        return scaffolds;
    }

    public void setScaffolds(String scaffolds) {
        this.scaffolds = scaffolds;
    }

    public String getScaffoldN50() {
        return scaffoldN50;
    }

    public void setScaffoldN50(String scaffoldN50) {
        this.scaffoldN50 = scaffoldN50;
    }

    public String getScaffoldL50() {
        return scaffoldL50;
    }

    public void setScaffoldL50(String scaffoldL50) {
        this.scaffoldL50 = scaffoldL50;
    }

    public String getContigs() {
        return contigs;
    }

    public void setContigs(String contigs) {
        this.contigs = contigs;
    }

    public String getContigN50() {
        return contigN50;
    }

    public void setContigN50(String contigN50) {
        this.contigN50 = contigN50;
    }

    public String getContigL50() {
        return contigL50;
    }

    public void setContigL50(String contigL50) {
        this.contigL50 = contigL50;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public String getNcbiLink() {
        return ncbiLink;
    }

    public void setNcbiLink(String ncbiLink) {
        this.ncbiLink = ncbiLink;
    }

    public String getRefSeqAssemblyAccession() {
        return refSeqAssemblyAccession;
    }

    public void setRefSeqAssemblyAccession(String refSeqAssemblyAccession) {
        this.refSeqAssemblyAccession = refSeqAssemblyAccession;
    }
}
