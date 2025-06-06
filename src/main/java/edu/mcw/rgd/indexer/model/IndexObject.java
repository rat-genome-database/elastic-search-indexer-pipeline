package edu.mcw.rgd.indexer.model;

import edu.mcw.rgd.datamodel.Association;
import edu.mcw.rgd.indexer.model.expression.ExpressionIndexObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jthota on 3/23/2017.
 */
public class IndexObject extends ExpressionIndexObject {
    private String term_acc;
    private String status;
    private String symbol;
    private List<String> oldSymbols;

    private String name;
    private List<String> oldNames;
    private String htmlStrippedSymbol;
    private String description;
    private List<String> xdbIdentifiers;
    private List<String> xdbNames;
    private String species;
    private String type;
    private String category;
    private String source;
    private String origin;
    private List<String> transcriptIds;
    private List<String> protein_acc_ids;
    private List<String> synonyms;
    private String trait;
    private String subTrait;

    private List<MapInfo> mapDataList;
    private String chromosome;
    private String map;
    private long startPos;
    private long stopPos;
    private int rank;
    private int mapKey;
    private List<String> goAnnotations;
    private boolean withSSLPS;
    private boolean withHomologs;
    private String regionName;
    private String regionNameLc;
    private List<String>  analysisName;
    private String varNuc;
    private String refNuc;
    private Set<String> lifeStage;
    private Set<String> sex;
    private Set<String> expressionLevel;
    private Set<String>expressionUnit;

    public Set<String> getLifeStage() {
        return lifeStage;
    }

    public void setLifeStage(Set<String> lifeStage) {
        this.lifeStage = lifeStage;
    }

    public Set<String> getSex() {
        return sex;
    }

    public void setSex(Set<String> sex) {
        this.sex = sex;
    }

    public Set<String> getExpressionLevel() {
        return expressionLevel;
    }

    public void setExpressionLevel(Set<String> expressionLevel) {
        this.expressionLevel = expressionLevel;
    }

    public Set<String> getExpressionUnit() {
        return expressionUnit;
    }

    public void setExpressionUnit(Set<String> expressionUnit) {
        this.expressionUnit = expressionUnit;
    }

    public String getVarNuc() {
        return varNuc;
    }

    public void setVarNuc(String varNuc) {
        this.varNuc = varNuc;
    }

    public String getRefNuc() {
        return refNuc;
    }

    public void setRefNuc(String refNuc) {
        this.refNuc = refNuc;
    }

    public List<String> getAnalysisName() {
        return analysisName;
    }

    public void setAnalysisName(List<String> analysisName) {
        this.analysisName = analysisName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionNameLc() {
        return regionNameLc;
    }

    public void setRegionNameLc(String regionNameLc) {
        this.regionNameLc = regionNameLc;
    }

    public List<String> getGoAnnotations() {
        return goAnnotations;
    }

    public void setGoAnnotations(List<String> goAnnotations) {
        this.goAnnotations = goAnnotations;
    }

    public boolean isWithSSLPS() {
        return withSSLPS;
    }

    public void setWithSSLPS(boolean withSSLPS) {
        this.withSSLPS = withSSLPS;
    }

    public boolean isWithHomologs() {
        return withHomologs;
    }

    public void setWithHomologs(boolean withHomologs) {
        this.withHomologs = withHomologs;
    }

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getStopPos() {
        return stopPos;
    }

    public void setStopPos(long stopPos) {
        this.stopPos = stopPos;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    private List<String> promoters;
    private String subcat;
    private String variantCategory;
    private List<String> associations;


    private List<String> xdata;
    private List<String> annotated_objects;
    private List<String> annotation_synonyms;
    private String term;
    private String term_def;
    private int sampleExists;
/******************************************************************************************/

    private int annotationsCount;
    private String pathwayDiagUrl;
    private int experimentRecordCount;
    /**************************************************************06/14*********************/
    private int childTermsAnnotsCount;
    private int termAnnotsCount;
    private int[][] annotationsMatrix;

    /*********************************ref data*********************************************/
    private List<String> author;
    private String pub_year;
    private String citation;
    private String title;
    private String refAbstract;

    private String genomicAlteration;

    private Map<String, Set<String>> suggest;
    private List<String> strainsCrossed ;

    public List<String> getStrainsCrossed() {
        return strainsCrossed;
    }

    public void setStrainsCrossed(List<String> strainsCrossed) {
        this.strainsCrossed = strainsCrossed;
    }

    public String getGenomicAlteration() {
        return genomicAlteration;
    }

    public void setGenomicAlteration(String genomicAlteration) {
        this.genomicAlteration = genomicAlteration;
    }

    public List<String> getAssociations() {
        return associations;
    }

    public void setAssociations(List<String> associations) {
        this.associations = associations;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public String getPub_year() {
        return pub_year;
    }

    public void setPub_year(String pub_year) {
        this.pub_year = pub_year;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRefAbstract() {
        return refAbstract;
    }

    public void setRefAbstract(String refAbstract) {
        this.refAbstract = refAbstract;
    }

    public Map<String, Set<String>> getSuggest() {
        return suggest;
    }

    public void setSuggest(Map<String, Set<String>> suggest) {
        this.suggest = suggest;
    }

    public int getTermAnnotsCount() {
        return termAnnotsCount;
    }

    public void setTermAnnotsCount(int termAnnotsCount) {
        this.termAnnotsCount = termAnnotsCount;
    }

    public int getChildTermsAnnotsCount() {
        return childTermsAnnotsCount;
    }

    public void setChildTermsAnnotsCount(int childTermsAnnotsCount) {
        this.childTermsAnnotsCount = childTermsAnnotsCount;
    }

    public int[][] getAnnotationsMatrix() {
        return annotationsMatrix;
    }

    public void setAnnotationsMatrix(int[][] annotationsMatrix) {
        this.annotationsMatrix = annotationsMatrix;
    }
/********************************************************************************************************/
    public int getSampleExists() {
        return sampleExists;
    }

    public void setSampleExists(int sampleExists) {
        this.sampleExists = sampleExists;
    }

    public int getExperimentRecordCount() {
        return experimentRecordCount;
    }

    public void setExperimentRecordCount(int experimentRecordCount) {
        this.experimentRecordCount = experimentRecordCount;
    }

    public String getSubTrait() {
        return subTrait;
    }

    public void setSubTrait(String subTrait) {
        this.subTrait = subTrait;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public String getVariantCategory() {
        return variantCategory;
    }

    public void setVariantCategory(String variantCategory) {
        this.variantCategory = variantCategory;
    }

    public int getAnnotationsCount() {
        return annotationsCount;
    }

    public void setAnnotationsCount(int annotationsCount) {
        this.annotationsCount = annotationsCount;
    }

    public String getPathwayDiagUrl() {
        return pathwayDiagUrl;
    }

    public void setPathwayDiagUrl(String pathwayDiagUrl) {
        this.pathwayDiagUrl = pathwayDiagUrl;
    }

    public String getTerm_def() {
        return term_def;
    }

    public void setTerm_def(String term_def) {
        this.term_def = term_def;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String def) {
        this.term = def;
    }

    public List<String> getXdata() {
        return xdata;
    }

    public void setXdata(List<String> xdata) {
        this.xdata = xdata;
    }

    public List<String> getAnnotation_synonyms() {
        return annotation_synonyms;
    }

    public void setAnnotation_synonyms(List<String> annotation_synonyms) {
        this.annotation_synonyms = annotation_synonyms;
    }

    public String getTerm_acc() {
        return term_acc;
    }

    public void setTerm_acc(String term_acc) {
        this.term_acc = term_acc;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getXdbIdentifiers() {
        return xdbIdentifiers;
    }

    public void setXdbIdentifiers(List<String> xdbIdentifiers) {
        this.xdbIdentifiers = xdbIdentifiers;
    }

    public List<String> getXdbNames() {
        return xdbNames;
    }

    public void setXdbNames(List<String> xdbNames) {
        this.xdbNames = xdbNames;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List<String> getTranscriptIds() {
        return transcriptIds;
    }

    public void setTranscriptIds(List<String> transcriptIds) {
        this.transcriptIds = transcriptIds;
    }

    public List<String> getProtein_acc_ids() {
        return protein_acc_ids;
    }

    public void setProtein_acc_ids(List<String> protein_acc_ids) {
        this.protein_acc_ids = protein_acc_ids;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public List<MapInfo> getMapDataList() {
        return mapDataList;
    }

    public void setMapDataList(List<MapInfo> mapDataList) {
        this.mapDataList = mapDataList;
    }

    public List<String> getPromoters() {
        return promoters;
    }

    public void setPromoters(List<String> promoters) {
        this.promoters = promoters;
    }

    public String getHtmlStrippedSymbol() {
        return htmlStrippedSymbol;
    }

    public void setHtmlStrippedSymbol(String htmlStrippedSymbol) {
        this.htmlStrippedSymbol = htmlStrippedSymbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*   public List<String> getDisease() {
            return disease;
        }

        public void setDisease(List<String> disease) {
            this.disease = disease;
        }

        public List<String> getBiological_process() {
            return biological_process;
        }

        public void setBiological_process(List<String> biological_process) {
            this.biological_process = biological_process;
        }

        public List<String> getCellular_component() {
            return cellular_component;
        }

        public void setCellular_component(List<String> cellular_component) {
            this.cellular_component = cellular_component;
        }

        public List<String> getMolecular_function() {
            return molecular_function;
        }

        public void setMolecular_function(List<String> molecular_function) {
            this.molecular_function = molecular_function;
        }

        public List<String> getPathway() {
            return pathway;
        }

        public void setPathway(List<String> pathway) {
            this.pathway = pathway;
        }

        public List<String> getPhenotype() {
            return phenotype;
        }

        public void setPhenotype(List<String> phenotype) {
            this.phenotype = phenotype;
        }

        public List<String> getGene_chem_interaction() {
            return gene_chem_interaction;
        }

        public void setGene_chem_interaction(List<String> gene_chem_interaction) {
            this.gene_chem_interaction = gene_chem_interaction;
        }

        public List<String> getNeuro_behavioural() {
            return neuro_behavioural;
        }

        public void setNeuro_behavioural(List<String> neuro_behavioural) {
            this.neuro_behavioural = neuro_behavioural;
        }

        public List<String> getCross_species_anatomy() {
            return cross_species_anatomy;
        }

        public void setCross_species_anatomy(List<String> cross_species_anatomy) {
            this.cross_species_anatomy = cross_species_anatomy;
        }
    */

    public List<String> getOldSymbols() {
        return oldSymbols;
    }

    public void setOldSymbols(List<String> oldSymbols) {
        this.oldSymbols = oldSymbols;
    }

    public List<String> getOldNames() {
        return oldNames;
    }

    public void setOldNames(List<String> oldNames) {
        this.oldNames = oldNames;
    }

    public List<String> getAnnotated_objects() {
        return annotated_objects;
    }

    public void setAnnotated_objects(List<String> annotated_objects) {
        this.annotated_objects = annotated_objects;
    }
}
