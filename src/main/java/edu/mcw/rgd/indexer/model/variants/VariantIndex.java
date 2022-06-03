package edu.mcw.rgd.indexer.model.variants;

import edu.mcw.rgd.indexer.model.IndexObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantIndex extends IndexObject {

    private long variant_id;
    private String category;
    private String chromosome;
    private long endPos;
    private int sampleId;

    private long startPos;

    private String variantType;
    private String zygosityStatus;
    private String genicStatus;
    private String HGVSNAME;
    private String rsId;
    private String regionName;
    private String regionNameLc;
    /*****************Sample******************/

   private List<String>  analysisName;
   private int mapKey;
   private List<Integer> strainRgdId;

/*****************Variant_Transcript******************/
    private List<Long>  transcriptRgdId;
    private List<String> polyphenStatus;
    private String synStatus;
    private List<String> locationName;
    private String  uniprotId;
    private String  proteinId;


/*****************Transcripts******************/

    private List<Integer> geneRgdIds;
    private List<String> geneSymbols;
    private String accId;
    private String peptideLabel;
    private String proteinAccId;
    private String bioType;
    /********************dbs_snp*******************************/
    private String dbsSnpName;

    private String clinicalSignificance;


    public String getRsId() {
        return rsId;
    }

    public void setRsId(String rsId) {
        this.rsId = rsId;
    }

    public String getClinicalSignificance() {
        return clinicalSignificance;
    }

    public void setClinicalSignificance(String clinicalSignificance) {
        this.clinicalSignificance = clinicalSignificance;
    }

    public String getRegionNameLc() {
        return regionNameLc;

    }

    public String getDbsSnpName() {
        return dbsSnpName;
    }

    public void setDbsSnpName(String dbsSnpName) {
        this.dbsSnpName = dbsSnpName;
    }

    public void setRegionNameLc(String regionNameLc) {
        this.regionNameLc = regionNameLc;
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



    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
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



    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
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


    public String getHGVSNAME() {
        return HGVSNAME;
    }

    public void setHGVSNAME(String HGVSNAME) {
        this.HGVSNAME = HGVSNAME;
    }





    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }



    public String getSynStatus() {
        return synStatus;
    }

    public void setSynStatus(String synStatus) {
        this.synStatus = synStatus;
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



    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(List<String> analysisName) {
        this.analysisName = analysisName;
    }

    public List<Integer> getStrainRgdId() {
        return strainRgdId;
    }

    public void setStrainRgdId(List<Integer> strainRgdId) {
        this.strainRgdId = strainRgdId;
    }

    public List<Long> getTranscriptRgdId() {
        return transcriptRgdId;
    }

    public void setTranscriptRgdId(List<Long> transcriptRgdId) {
        this.transcriptRgdId = transcriptRgdId;
    }

    public List<String> getPolyphenStatus() {
        return polyphenStatus;
    }

    public void setPolyphenStatus(List<String> polyphenStatus) {
        this.polyphenStatus = polyphenStatus;
    }

    public List<String> getLocationName() {
        return locationName;
    }

    public void setLocationName(List<String> locationName) {
        this.locationName = locationName;
    }
}
