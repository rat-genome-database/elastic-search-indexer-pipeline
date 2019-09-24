package edu.mcw.rgd.indexer.model.genomeInfo;

import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.TranscriptDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.MappedGene;
import edu.mcw.rgd.datamodel.Transcript;
import edu.mcw.rgd.indexer.dao.StatsDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jthota on 10/23/2017.
 */
public class GeneCounts {

    private int totalGenes;
    private int proteinCoding;
    private int ncrna;
    private int pseudo;
    private int tRna;
    private int snRna;
    private int rRna;
    private Map<String, Integer> mirnaTargets;
    private long transcripts;

    private int genesWithOrthologs;
    private int genesWithoutOrthologs;

    private  Map<String, Integer> orthologCountsMap;
    public GeneCounts(){}

    public Map<String, Integer> getOrthologCountsMap() {
        return orthologCountsMap;
    }

    public void setOrthologCountsMap(Map<String, Integer> orthologCountsMap) {
        this.orthologCountsMap = orthologCountsMap;
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



    public int getTotalGenes() {
        return totalGenes;
    }

    public void setTotalGenes(int totalGenes) {
        this.totalGenes = totalGenes;
    }

    public Map<String, Integer> getMirnaTargets() {
        return mirnaTargets;
    }

    public void setMirnaTargets(Map<String, Integer> mirnaTargets) {
        this.mirnaTargets = mirnaTargets;
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


}
