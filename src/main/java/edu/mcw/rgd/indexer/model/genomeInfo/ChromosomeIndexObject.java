package edu.mcw.rgd.indexer.model.genomeInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by jthota on 11/7/2017.
 */
public class ChromosomeIndexObject {
    //SUMMARY
    private int mapKey;
    private String chromosome;
    private String refseqId;
    private String genbankId;
    private int seqLength;
    private int gapLength;
    private int gapCount;
    private int contigCount;

    //GENE COUNTS
    private int totalGenes;
    private int proteinCoding;
    private int ncrna;
    private int pseudo;
    private int tRna;
    private int snRna;
    private int rRna;
    private int mirnaTargetsConfirmed;
    private int mirnaTargetsPredicted;
    private long transcripts;

    //OTHER COUNTS
    private long utrs3;
    private long utrs5;
    private long exons;
    private long qtls;
    private long sslps;
    private long strains;
    private long variants;

    //ORTHOLOG COUNTS
    private int genesWithOrthologs;
    private int genesWithoutOrthologs;
    private int humanOrthologs;
    private int ratOrthologs;
    private int mouseOrthologs;
    private int bonoboOrthologs;
    private int squirrelOrthologs;
    private int dogOrthologs;
    private int chinchillaOrthologs;

    private String assembly;
    private StringBuffer pieData;


    //DISEASE GENE SETS
    private List<DiseaseGeneObject> diseaseGenes;
    private StringBuffer diseaseGenechartData;

    //REFERENCES
    private List<String> refs;

    //STRAIN VARIANTS
    private String[][] variantsMatrix;

    //PROTEIN COUNT
    private int proteinsCount;

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

    public StringBuffer getDiseaseGenechartData() {
        return diseaseGenechartData;
    }

    public void setDiseaseGenechartData(StringBuffer diseaseGenechartData) {
        this.diseaseGenechartData = diseaseGenechartData;
    }

    public List<DiseaseGeneObject> getDiseaseGenes() {
        return diseaseGenes;
    }

    public void setDiseaseGenes(List<DiseaseGeneObject> diseaseGenes) {
        this.diseaseGenes = diseaseGenes;
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

    public long getVariants() {
        return variants;
    }

    public void setVariants(long variants) {
        this.variants = variants;
    }

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public String getRefseqId() {
        return refseqId;
    }

    public void setRefseqId(String refseqId) {
        this.refseqId = refseqId;
    }

    public String getGenbankId() {
        return genbankId;
    }

    public void setGenbankId(String genbankId) {
        this.genbankId = genbankId;
    }

    public int getSeqLength() {
        return seqLength;
    }

    public void setSeqLength(int seqLength) {
        this.seqLength = seqLength;
    }

    public int getGapLength() {
        return gapLength;
    }

    public void setGapLength(int gapLength) {
        this.gapLength = gapLength;
    }

    public int getGapCount() {
        return gapCount;
    }

    public void setGapCount(int gapCount) {
        this.gapCount = gapCount;
    }

    public int getContigCount() {
        return contigCount;
    }

    public void setContigCount(int contigCount) {
        this.contigCount = contigCount;
    }

    public int getTotalGenes() {
        return totalGenes;
    }

    public void setTotalGenes(int totalGenes) {
        this.totalGenes = totalGenes;
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



    public long getTranscripts() {
        return transcripts;
    }

    public void setTranscripts(long transcripts) {
        this.transcripts = transcripts;
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

    public StringBuffer getPieData() {
        return pieData;
    }

    public void setPieData(StringBuffer pieData) {
        this.pieData = pieData;
    }
}
