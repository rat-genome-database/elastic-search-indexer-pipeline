package edu.mcw.rgd.indexer.model;

import edu.mcw.rgd.dao.impl.OrthologDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;

import edu.mcw.rgd.indexer.dao.GenomeDAO;
import edu.mcw.rgd.indexer.dao.StatsDAO;
import edu.mcw.rgd.indexer.model.genomeInfo.AssemblyInfo;
import edu.mcw.rgd.indexer.model.genomeInfo.DiseaseGeneObject;

import edu.mcw.rgd.indexer.model.genomeInfo.GenomeIndexObject;
import edu.mcw.rgd.process.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GenomeDataCounts extends GenomeDAO {
    private final Map map;
    private final int speciesTypeKey;
    private List<MappedGene> mappedGenes;
    private final GenomeIndexObject obj;
    private final Chromosome chromosome;
    public GenomeDataCounts(Map map, int speciesTypeKey, Chromosome chr){
        this.map=map;
        this.speciesTypeKey=speciesTypeKey;
        this.chromosome=chr;
        setMappedGenes();
        this.obj=new GenomeIndexObject();
    }
    public void setMappedGenes() {
        try {
            this.mappedGenes=   geneDAO.getActiveMappedGenes(map.getKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String getChromosome(){
        if(chromosome!=null){
            return chromosome.getChromosome();
        }else
            return null;
    }
    public List<MappedGene> getMappedGenes(){
        if(chromosome!=null){
            List<MappedGene> genes=new ArrayList<>();
            for(MappedGene m: mappedGenes){
                if(m.getChromosome().equals(chromosome.getChromosome())){
                    genes.add(m);
                }
            }
            return genes;
        }else{
           return mappedGenes;
        }
    }
    public void mapAssembly() throws Exception {
        if(chromosome==null) {
            AssemblyInfo info = getAssemblyInfo(map);
            obj.setBasePairs(info.getBasePairs());
            obj.setTotalSeqLength(info.getTotalSeqLength());
            obj.setTotalUngappedLength(info.getTotalUngappedLength());
            obj.setGapBetweenScaffolds(info.getGapBetweenScaffolds());
            obj.setScaffolds(info.getScaffolds());
            obj.setScaffoldN50(info.getScaffoldN50());
            obj.setScaffoldL50(info.getScaffoldL50());
            obj.setContigs(info.getContigs());
            obj.setContigN50(info.getContigN50());
            obj.setContigL50(info.getContigL50());
            obj.setChromosomes(info.getChromosome());
            obj.setNcbiLink(info.getNcbiLink());
            obj.setRefSeqAssemblyAccession(info.getRefSeqAssemblyAccession());
            obj.setMapKey(map.getKey());
            if (map.isPrimaryRefAssembly())
                obj.setPrimaryAssembly("Y");
            else
                obj.setPrimaryAssembly("N");
            obj.setAssembly(map.getName());
        }else{
            obj.setMapKey(map.getKey());
            obj.setChromosome(chromosome.getChromosome());
            obj.setAssembly(map.getName());
            obj.setRefseqId(chromosome.getRefseqId());
            obj.setGenbankId(chromosome.getGenbankId());
            obj.setSeqLength(chromosome.getSeqLength());
            obj.setGapLength(chromosome.getGapLength());
            obj.setGapCount(chromosome.getGapCount());
            obj.setContigCount(chromosome.getContigCount());
        }
    }
    public void mapGeneTypeCounts(){
        int proteninCoding=0;
        int ncrna=0;
        int trna=0;
        int snrna=0;
        int rrna=0;
        int pseudo=0;
        for (MappedGene g : getMappedGenes()) {
            Gene gene = g.getGene();
            String type = gene.getType();

            if (type.equalsIgnoreCase("protein-coding")) {
                proteninCoding++;
            }
            if (type.equalsIgnoreCase("ncrna")) {
                ncrna++;
            }
            if (type.equalsIgnoreCase("trna")) {
                trna++;
            }
            if (type.equalsIgnoreCase("snrna")) {
                snrna++;
            }
            if (type.equalsIgnoreCase("rrna")) {
                rrna++;
            }
            if (type.equalsIgnoreCase("pseudo")) {
                pseudo++;
            }
        }

        obj.setTotalGenes(getMappedGenes().size());
        obj.setProteinCoding(proteninCoding);
        obj.setNcrna(ncrna);
        obj.setPseudo(pseudo);
        obj.setrRna(rrna);
        obj.setSnRna(snrna);
        obj.settRna(trna);
    }
    public void mapMirnaTargetCount() throws Exception {
        StatsDAO statsDAO= new StatsDAO();
        java.util.Map<String, Integer>  mirnaTargets= statsDAO.getMirnaTargetCountsMap(map.getKey(), getChromosome());
        if(mirnaTargets!=null) {
            for (java.util.Map.Entry entry : mirnaTargets.entrySet()) {
                String targetType = (String) entry.getKey();
                int value = (int) entry.getValue();
                if (targetType.equalsIgnoreCase("confirmed")) {
                    obj.setMirnaTargetsConfirmed(value);
                }
                if (targetType.equalsIgnoreCase("predicted")) {
                    obj.setMirnaTargetsPredicted(value);
                }
            }
        }

    }
    public void mapOrthologCounts() throws Exception {
        OrthologDAO orthologDAO= new OrthologDAO();
        java.util.Map<String, Integer> orthologCounts=  orthologDAO.getOrthologCounts(map.getKey(), speciesTypeKey, getChromosome());
        int genesWithOrthologs= orthologDAO.getGeneCountWithOrthologs(map.getKey(), speciesTypeKey,getChromosome());
        int genesWithoutOrthologs= orthologDAO.getGeneCountWithOutOrthologs(map.getKey(), speciesTypeKey, getChromosome());
        orthologCounts.put("withOrthologs", genesWithOrthologs);
        orthologCounts.put("WithOutOrthologs",genesWithoutOrthologs);
        if(orthologCounts.get("1")!=null){obj.setHumanOrthologs(orthologCounts.get("1")); }
        if(orthologCounts.get("2")!=null){obj.setMouseOrthologs(orthologCounts.get("2"));}
        if(orthologCounts.get("3")!=null) {obj.setRatOrthologs(orthologCounts.get("3"));}
        if(orthologCounts.get("4")!=null) {obj.setChinchillaOrthologs(orthologCounts.get("4"));}
        if(orthologCounts.get("6")!=null){    obj.setDogOrthologs(orthologCounts.get("6"));}
        if(orthologCounts.get("7")!=null){    obj.setSquirrelOrthologs(orthologCounts.get("7"));}
        if(orthologCounts.get("5")!=null){    obj.setBonoboOrthologs(orthologCounts.get("5"));}
        if(orthologCounts.get("9")!=null){    obj.setPigOrthologs(orthologCounts.get("9"));}
        if(orthologCounts.get("13")!=null){    obj.setGreenMonkeyOrthologs(orthologCounts.get("13"));}
        if(orthologCounts.get("14")!=null){    obj.setNakedMoleRatOrthologs(orthologCounts.get("14"));}

        obj.setGenesWithoutOrthologs(orthologCounts.get("WithOutOrthologs"));
        obj.setGenesWithOrthologs(orthologCounts.get("withOrthologs"));

    }
    public void mapProteinCounts() throws Exception {
        obj.setProteinsCount(pdao.getProteinsCount(map.getKey(), getChromosome()));
    }
    public void mapObjectCounts(){
        java.util.Map<String, Long> countsMap = null;
        try {
            countsMap = getObjectCounts(map.getKey(), getChromosome());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for(java.util.Map.Entry e: countsMap.entrySet()){
            String key= (String) e.getKey();
            Long value= (Long) e.getValue();
            if(key.equalsIgnoreCase("exons")){
                obj.setExons(value);
            }
            if(key.equalsIgnoreCase("qtls")){
                obj.setQtls(value);
            }
            if(key.equalsIgnoreCase("transcripts")){
                obj.setTranscripts(value);
            }
            if(key.equalsIgnoreCase("sslps")){
                obj.setSslps(value);
            }
            if(key.equalsIgnoreCase("strains")){
                obj.setStrains(value);
            }
            if(key.equalsIgnoreCase("qtls")){
                obj.setQtls(value);
            }
            if(key.equalsIgnoreCase("3utrs")){
                obj.setUtrs3(value);
            }
            if(key.equalsIgnoreCase("5utrs")){
                obj.setUtrs5(value);
            }
            if(key.equalsIgnoreCase("genes")){
                obj.setTotalGenes(value);
            }
            if(key.equalsIgnoreCase("variants")){
                obj.setVariants(value);
            }
        }
    }
    public void mapVariants(){
        if (map.getKey() == 360 || map.getKey() == 70 || map.getKey() == 60 || map.getKey() == 372 || map.getKey()==38 || map.getKey()==380) {
          //  String[][] strainVairantMatrix = new String[0][];
            try {
                String[][]   strainVariantMatrix = variants.getStrainVariants(map.getKey(), getChromosome());
                obj.setVariantsMatrix(strainVariantMatrix);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            }

    }
    public void mapSpecies(){
        obj.setSpecies(SpeciesType.getCommonName(speciesTypeKey));
    }
    public void mapDiseaseGenes() throws Exception {
        String rootTerm=ontologyXDAO.getRootTerm("RDO");
        List<TermWithStats> topLevelDiseaseTerms=   ontologyXDAO.getActiveChildTerms(rootTerm,speciesTypeKey);
        List<DiseaseGeneObject> diseaseGeneSets= new ArrayList<>();
        Ontology ont= ontologyXDAO.getOntology("RDO");
        String aspect=ont.getAspect();
        for(TermWithStats t:topLevelDiseaseTerms){
            DiseaseGeneObject obj= new DiseaseGeneObject();

            int count= getGeneCountsByTermAcc(t.getAccId(), map.getKey(), aspect, getChromosome());
            if(count>0) {
                obj.setOntTermAccId(t.getAccId());
                obj.setOntTerm(t.getTerm());
                obj.setGeneCount(count);
                diseaseGeneSets.add(obj);
            }
        }
        Collections.sort(diseaseGeneSets, new Comparator<DiseaseGeneObject>() {
            @Override
            public int compare(DiseaseGeneObject o1, DiseaseGeneObject o2) {
                return   Utils.stringsCompareToIgnoreCase(o1.getOntTerm(), o2.getOntTerm());

            }
        });
        obj.setDiseaseGenes(diseaseGeneSets);
    }
    public void addDiseaseGeneChartData(){
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (DiseaseGeneObject d : obj.getDiseaseGenes()) {
            if (d.getGeneCount() > 0)
                sb.append("{\"disease\":\"").append(d.getOntTerm()).append("\", \"geneCount\":").append(d.getGeneCount()).append("},");
        }
        sb.append("]");

       obj.setDiseaseGenechartData(sb);
    }
    public void addPieData(){
        StringBuffer pieData= new StringBuffer();
        pieData.append("[{\"label\":\"protein-coding\", \"value\":"+obj.getProteinCoding()+"}," );
        if(obj.getNcrna()!=0)
            pieData.append("{\"label\":\"ncrna\", \"value\":" + obj.getNcrna()+"}," );
        if(obj.gettRna()!=0)
            pieData.append("{\"label\":\"tRna\", \"value\":" +obj.gettRna()+"}," );
        if(obj.getrRna()!=0)
            pieData.append("{\"label\":\"rrna\", \"value\":" + obj.getrRna()+"}," );
        if(obj.getPseudo()!=0)
            pieData.append("{\"label\":\"pseudo\", \"value\":" +obj.getPseudo()+"}," );
        if(obj.getSnRna()!=0)
            pieData.append("{\"label\":\"ncrna\", \"value\":" + obj.getSnRna()+"}");
        pieData.append("]");
       obj.setPieData( pieData);
    }
    public void index() throws Exception {
        mapAssembly();
        mapSpecies();
        mapGeneTypeCounts();
        mapMirnaTargetCount();
        mapProteinCounts();
        mapOrthologCounts();
        mapObjectCounts();
        mapVariants();
        if(chromosome!=null){
            mapDiseaseGenes();
            addDiseaseGeneChartData();
            addPieData();
        }
        IndexDocument.index(obj);
    }
}
