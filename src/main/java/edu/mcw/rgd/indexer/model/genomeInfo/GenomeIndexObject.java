package edu.mcw.rgd.indexer.model.genomeInfo;

import java.util.List;

/**
 * Created by jthota on 10/23/2017.
 */
public class GenomeIndexObject {

    private String species;
    private String assembly;
    private int mapKey;
    private long totalGenes;
    private String refSeqAssemblyAccession;
    private String ncbiLink;
    private String basePairs;
    private String totalSeqLength;
    private String totalUngappedLength;
    private String  gapBetweenScaffolds;
    private String scaffolds;
    private String scaffoldN50;
    private String scaffoldL50;
    private String contigs;
    private String contigN50;
    private String contigL50;
    private String chromosomes;
    private int proteinCoding;
    private int ncrna;
    private int pseudo;
    private int tRna;
    private int snRna;
    private int rRna;
    private int mirnaTargetsConfirmed;
    private int mirnaTargetsPredicted;
    private long transcripts;

    private long utrs3;
    private long utrs5;
    private long exons;
    private long qtls;
    private long sslps;
    private long strains;
    private long variants;
    private String primaryAssembly;

    //ORTHOLOGS//
    private int genesWithOrthologs;
    private int genesWithoutOrthologs;
    private int humanOrthologs;
    private int ratOrthologs;
    private int mouseOrthologs;
    private int bonoboOrthologs;
    private int squirrelOrthologs;
    private int dogOrthologs;
    private int chinchillaOrthologs;
    private int pigOrthologs;

    private List<String> refs;


    private String[][] variantsMatrix;
    private int proteinsCount;

    public int getPigOrthologs() {
        return pigOrthologs;
    }

    public void setPigOrthologs(int pigOrthologs) {
        this.pigOrthologs = pigOrthologs;
    }

    public int getProteinsCount() {
        return proteinsCount;
    }

    public void setProteinsCount(int proteinsCount) {
        this.proteinsCount = proteinsCount;
    }

    public String[][] getVariantsMatrix() {
        return variantsMatrix;
    }

    public void setVariantsMatrix(String[][] variantsMatrix) {
        this.variantsMatrix = variantsMatrix;
    }

    public List<String> getRefs() {
        return refs;
    }

    public void setRefs(List<String> refs) {
        this.refs = refs;
    }

    public int getGenesWithOrthologs() {
        return genesWithOrthologs;
    }

    public void setGenesWithOrthologs(int genesWithOrthologs) {
        this.genesWithOrthologs = genesWithOrthologs;
    }

    public int getGenesWithoutOrthologs() {
        return genesWithoutOrthologs;
    }

    public void setGenesWithoutOrthologs(int genesWithoutOrthologs) {
        this.genesWithoutOrthologs = genesWithoutOrthologs;
    }

    public int getHumanOrthologs() {
        return humanOrthologs;
    }

    public void setHumanOrthologs(int humanOrthologs) {
        this.humanOrthologs = humanOrthologs;
    }

    public int getRatOrthologs() {
        return ratOrthologs;
    }

    public void setRatOrthologs(int ratOrthologs) {
        this.ratOrthologs = ratOrthologs;
    }

    public int getMouseOrthologs() {
        return mouseOrthologs;
    }

    public void setMouseOrthologs(int mouseOrthologs) {
        this.mouseOrthologs = mouseOrthologs;
    }

    public int getBonoboOrthologs() {
        return bonoboOrthologs;
    }

    public void setBonoboOrthologs(int bonoboOrthologs) {
        this.bonoboOrthologs = bonoboOrthologs;
    }

    public int getSquirrelOrthologs() {
        return squirrelOrthologs;
    }

    public void setSquirrelOrthologs(int squirrelOrthologs) {
        this.squirrelOrthologs = squirrelOrthologs;
    }

    public int getDogOrthologs() {
        return dogOrthologs;
    }

    public void setDogOrthologs(int dogOrthologs) {
        this.dogOrthologs = dogOrthologs;
    }

    public int getChinchillaOrthologs() {
        return chinchillaOrthologs;
    }

    public void setChinchillaOrthologs(int chinchillaOrthologs) {
        this.chinchillaOrthologs = chinchillaOrthologs;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getPrimaryAssembly() {
        return primaryAssembly;
    }

    public void setPrimaryAssembly(String primaryAssembly) {
        this.primaryAssembly = primaryAssembly;
    }

    public long getVariants() {
        return variants;
    }

    public void setVariants(long variants) {
        this.variants = variants;
    }

    public long getUtrs3() {
        return utrs3;
    }

    public void setUtrs3(long utrs3) {
        this.utrs3 = utrs3;
    }

    public long getUtrs5() {
        return utrs5;
    }

    public void setUtrs5(long utrs5) {
        this.utrs5 = utrs5;
    }

    public long getExons() {
        return exons;
    }

    public void setExons(long exons) {
        this.exons = exons;
    }

    public long getQtls() {
        return qtls;
    }

    public void setQtls(long qtls) {
        this.qtls = qtls;
    }

    public long getSslps() {
        return sslps;
    }

    public void setSslps(long sslps) {
        this.sslps = sslps;
    }

    public long getStrains() {
        return strains;
    }

    public void setStrains(long strains) {
        this.strains = strains;
    }

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public long getTotalGenes() {
        return totalGenes;
    }

    public void setTotalGenes(long totalGenes) {
        this.totalGenes = totalGenes;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getRefSeqAssemblyAccession() {
        return refSeqAssemblyAccession;
    }

    public void setRefSeqAssemblyAccession(String refSeqAssemblyAccession) {
        this.refSeqAssemblyAccession = refSeqAssemblyAccession;
    }

    public String getNcbiLink() {
        return ncbiLink;
    }

    public void setNcbiLink(String ncbiLink) {
        this.ncbiLink = ncbiLink;
    }

    public String getBasePairs() {
        return basePairs;
    }

    public void setBasePairs(String basePairs) {
        this.basePairs = basePairs;
    }

    public String getTotalSeqLength() {
        return totalSeqLength;
    }

    public void setTotalSeqLength(String totalSeqLength) {
        this.totalSeqLength = totalSeqLength;
    }

    public String getTotalUngappedLength() {
        return totalUngappedLength;
    }

    public void setTotalUngappedLength(String totalUngappedLength) {
        this.totalUngappedLength = totalUngappedLength;
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

    public String getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(String chromosomes) {
        this.chromosomes = chromosomes;
    }

    public int getProteinCoding() {
        return proteinCoding;
    }

    public void setProteinCoding(int proteinCoding) {
        this.proteinCoding = proteinCoding;
    }

    public int getNcrna() {
        return ncrna;
    }

    public void setNcrna(int ncrna) {
        this.ncrna = ncrna;
    }

    public int getPseudo() {
        return pseudo;
    }

    public void setPseudo(int pseudo) {
        this.pseudo = pseudo;
    }

    public int gettRna() {
        return tRna;
    }

    public void settRna(int tRna) {
        this.tRna = tRna;
    }

    public int getSnRna() {
        return snRna;
    }

    public void setSnRna(int snRna) {
        this.snRna = snRna;
    }

    public int getrRna() {
        return rRna;
    }

    public void setrRna(int rRna) {
        this.rRna = rRna;
    }

    public int getMirnaTargetsConfirmed() {
        return mirnaTargetsConfirmed;
    }

    public void setMirnaTargetsConfirmed(int mirnaTargetsConfirmed) {
        this.mirnaTargetsConfirmed = mirnaTargetsConfirmed;
    }

    public int getMirnaTargetsPredicted() {
        return mirnaTargetsPredicted;
    }

    public void setMirnaTargetsPredicted(int mirnaTargetsPredicted) {
        this.mirnaTargetsPredicted = mirnaTargetsPredicted;
    }

    public long getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(long transcripts) {
        this.transcripts = transcripts;
    }
}
