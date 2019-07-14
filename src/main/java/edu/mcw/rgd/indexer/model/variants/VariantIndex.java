package edu.mcw.rgd.indexer.model.variants;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantIndex {
    private long variant_id;
    private String chromosome;
    private String  paddingBase;
    private long endPos;
    private String refNuc;
    private int sampleId;
    private long startPos;
    private int totalDepth;
    private int varFreq;
    private String analystFlag;
    private String variantType;
    private String varNuc;
    private String zygosityStatus;
    private String genicStatus;
    private double zygosityPercentRead;
    private String zygosityPossError;
    private String zygosityRefAllele;
    private int zygosityNumAllele;
    private String zygosityInPseudo;
    private int qualityScore;
    private String HGVSNAME;
    private int rgdId;
    private String regionName;
    /*****************Sample******************/

   private String  analysisName;
   private Date analysisTime;
   private String description;
   private int  patientId;
   private String  sequencer;
   private String remoteDataLoadDir;
   private String  gender;
   private String grantNumber;
   private String  sequencedBy;
   private String whereBred;
    private String secondaryAnalysisSoftware;
   private int mapKey;
   private String  dbsnpSource;
   private int strainRgdId;
   private String publications;
   private int refRgdId;

/*****************Variant_Transcript******************/

    private long variantTranscriptId;
    private long   transcriptRgdId;
    private String refAA;
    private String varAA;
    private String geneSpliceStatus;
    private String polyphenStatus;
    private String synStatus;
    private String locationName;
    private String nearSpliceSite;
    private String fullRefNuc;
    private long   fullRefNucPos;
    private String  fullRefAA;
    private long     fullRefAAPos;
    private String  uniprotId;
    private String  proteinId;
    private String tripletError;
    private String  frameShift;

/*****************Transcripts******************/

    private List<Integer> geneRgdIds;
    private List<String> geneSymbols;
    private String accId;
    private String isNonCodingInd;
    private String refSeqStat;
    private String peptideLabel;
    private String proteinAccId;
    private String bioType;
/**************************************************/
    private List<BigDecimal> conScores;

    /*******************ployphen*********************/
    private String polyphenPrediction;

    public String getPolyphenPrediction() {
        return polyphenPrediction;
    }

    public void setPolyphenPrediction(String polyphenPrediction) {
        this.polyphenPrediction = polyphenPrediction;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public long getVariant_id() {
        return variant_id;
    }

    public void setVariant_id(long variant_id) {
        this.variant_id = variant_id;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public String getPaddingBase() {
        return paddingBase;
    }

    public void setPaddingBase(String paddingBase) {
        this.paddingBase = paddingBase;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public String getRefNuc() {
        return refNuc;
    }

    public void setRefNuc(String refNuc) {
        this.refNuc = refNuc;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public int getTotalDepth() {
        return totalDepth;
    }

    public void setTotalDepth(int totalDepth) {
        this.totalDepth = totalDepth;
    }

    public int getVarFreq() {
        return varFreq;
    }

    public void setVarFreq(int varFreq) {
        this.varFreq = varFreq;
    }

    public String getAnalystFlag() {
        return analystFlag;
    }

    public void setAnalystFlag(String analystFlag) {
        this.analystFlag = analystFlag;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public String getVarNuc() {
        return varNuc;
    }

    public void setVarNuc(String varNuc) {
        this.varNuc = varNuc;
    }

    public String getZygosityStatus() {
        return zygosityStatus;
    }

    public void setZygosityStatus(String zygosityStatus) {
        this.zygosityStatus = zygosityStatus;
    }

    public String getGenicStatus() {
        return genicStatus;
    }

    public void setGenicStatus(String genicStatus) {
        this.genicStatus = genicStatus;
    }

    public double getZygosityPercentRead() {
        return zygosityPercentRead;
    }

    public void setZygosityPercentRead(double zygosityPercentRead) {
        this.zygosityPercentRead = zygosityPercentRead;
    }

    public String getZygosityPossError() {
        return zygosityPossError;
    }

    public void setZygosityPossError(String zygosityPossError) {
        this.zygosityPossError = zygosityPossError;
    }

    public String getZygosityRefAllele() {
        return zygosityRefAllele;
    }

    public void setZygosityRefAllele(String zygosityRefAllele) {
        this.zygosityRefAllele = zygosityRefAllele;
    }

    public int getZygosityNumAllele() {
        return zygosityNumAllele;
    }

    public void setZygosityNumAllele(int zygosityNumAllele) {
        this.zygosityNumAllele = zygosityNumAllele;
    }

    public String getZygosityInPseudo() {
        return zygosityInPseudo;
    }

    public void setZygosityInPseudo(String zygosityInPseudo) {
        this.zygosityInPseudo = zygosityInPseudo;
    }

    public int getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getHGVSNAME() {
        return HGVSNAME;
    }

    public void setHGVSNAME(String HGVSNAME) {
        this.HGVSNAME = HGVSNAME;
    }

    public int getRgdId() {
        return rgdId;
    }

    public void setRgdId(int rgdId) {
        this.rgdId = rgdId;
    }

    public String getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    public Date getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(Date analysisTime) {
        this.analysisTime = analysisTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getSequencer() {
        return sequencer;
    }

    public void setSequencer(String sequencer) {
        this.sequencer = sequencer;
    }

    public String getRemoteDataLoadDir() {
        return remoteDataLoadDir;
    }

    public void setRemoteDataLoadDir(String remoteDataLoadDir) {
        this.remoteDataLoadDir = remoteDataLoadDir;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    public String getSequencedBy() {
        return sequencedBy;
    }

    public void setSequencedBy(String sequencedBy) {
        this.sequencedBy = sequencedBy;
    }

    public String getWhereBred() {
        return whereBred;
    }

    public void setWhereBred(String whereBred) {
        this.whereBred = whereBred;
    }

    public String getSecondaryAnalysisSoftware() {
        return secondaryAnalysisSoftware;
    }

    public void setSecondaryAnalysisSoftware(String secondaryAnalysisSoftware) {
        this.secondaryAnalysisSoftware = secondaryAnalysisSoftware;
    }

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public String getDbsnpSource() {
        return dbsnpSource;
    }

    public void setDbsnpSource(String dbsnpSource) {
        this.dbsnpSource = dbsnpSource;
    }

    public int getStrainRgdId() {
        return strainRgdId;
    }

    public void setStrainRgdId(int strainRgdId) {
        this.strainRgdId = strainRgdId;
    }

    public String getPublications() {
        return publications;
    }

    public void setPublications(String publications) {
        this.publications = publications;
    }

    public int getRefRgdId() {
        return refRgdId;
    }

    public void setRefRgdId(int refRgdId) {
        this.refRgdId = refRgdId;
    }

    public long getVariantTranscriptId() {
        return variantTranscriptId;
    }

    public void setVariantTranscriptId(long variantTranscriptId) {
        this.variantTranscriptId = variantTranscriptId;
    }

    public long getTranscriptRgdId() {
        return transcriptRgdId;
    }

    public void setTranscriptRgdId(long transcriptRgdId) {
        this.transcriptRgdId = transcriptRgdId;
    }

    public String getRefAA() {
        return refAA;
    }

    public void setRefAA(String refAA) {
        this.refAA = refAA;
    }

    public String getVarAA() {
        return varAA;
    }

    public void setVarAA(String varAA) {
        this.varAA = varAA;
    }

    public String getGeneSpliceStatus() {
        return geneSpliceStatus;
    }

    public void setGeneSpliceStatus(String geneSpliceStatus) {
        this.geneSpliceStatus = geneSpliceStatus;
    }

    public String getPolyphenStatus() {
        return polyphenStatus;
    }

    public void setPolyphenStatus(String polyphenStatus) {
        this.polyphenStatus = polyphenStatus;
    }

    public String getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(String synStatus) {
        this.synStatus = synStatus;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getNearSpliceSite() {
        return nearSpliceSite;
    }

    public void setNearSpliceSite(String nearSpliceSite) {
        this.nearSpliceSite = nearSpliceSite;
    }

    public String getFullRefNuc() {
        return fullRefNuc;
    }

    public void setFullRefNuc(String fullRefNuc) {
        this.fullRefNuc = fullRefNuc;
    }

    public long getFullRefNucPos() {
        return fullRefNucPos;
    }

    public void setFullRefNucPos(long fullRefNucPos) {
        this.fullRefNucPos = fullRefNucPos;
    }

    public String getFullRefAA() {
        return fullRefAA;
    }

    public void setFullRefAA(String fullRefAA) {
        this.fullRefAA = fullRefAA;
    }

    public long getFullRefAAPos() {
        return fullRefAAPos;
    }

    public void setFullRefAAPos(long fullRefAAPos) {
        this.fullRefAAPos = fullRefAAPos;
    }

    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    public String getProteinId() {
        return proteinId;
    }

    public void setProteinId(String proteinId) {
        this.proteinId = proteinId;
    }

    public String getTripletError() {
        return tripletError;
    }

    public void setTripletError(String tripletError) {
        this.tripletError = tripletError;
    }

    public String getFrameShift() {
        return frameShift;
    }

    public void setFrameShift(String frameShift) {
        this.frameShift = frameShift;
    }


    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getIsNonCodingInd() {
        return isNonCodingInd;
    }

    public void setIsNonCodingInd(String isNonCodingInd) {
        this.isNonCodingInd = isNonCodingInd;
    }

    public String getRefSeqStat() {
        return refSeqStat;
    }

    public void setRefSeqStat(String refSeqStat) {
        this.refSeqStat = refSeqStat;
    }

    public String getPeptideLabel() {
        return peptideLabel;
    }

    public void setPeptideLabel(String peptideLabel) {
        this.peptideLabel = peptideLabel;
    }

    public String getProteinAccId() {
        return proteinAccId;
    }

    public void setProteinAccId(String proteinAccId) {
        this.proteinAccId = proteinAccId;
    }

    public String getBioType() {
        return bioType;
    }

    public void setBioType(String bioType) {
        this.bioType = bioType;
    }

    public List<Integer> getGeneRgdIds() {
        return geneRgdIds;
    }

    public void setGeneRgdIds(List<Integer> geneRgdIds) {
        this.geneRgdIds = geneRgdIds;
    }

    public List<String> getGeneSymbols() {
        return geneSymbols;
    }

    public void setGeneSymbols(List<String> geneSymbols) {
        this.geneSymbols = geneSymbols;
    }

    public List<BigDecimal> getConScores() {
        return conScores;
    }

    public void setConScores(List<BigDecimal> conScores) {
        this.conScores = conScores;
    }
}
