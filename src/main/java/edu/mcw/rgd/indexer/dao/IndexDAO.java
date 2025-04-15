package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.dao.spring.ExperimentQuery;
import edu.mcw.rgd.dao.spring.GeneQuery;
import edu.mcw.rgd.dao.spring.StringMapQuery;

import edu.mcw.rgd.datamodel.*;

import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.indexer.AnnotationFormatter;
import edu.mcw.rgd.indexer.MyThreadPoolExecutor;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import edu.mcw.rgd.indexer.dao.variants.VariantIndexerThread;
import edu.mcw.rgd.indexer.index.*;
import edu.mcw.rgd.indexer.model.*;

import edu.mcw.rgd.indexer.model.genomeInfo.AssemblyInfo;
import edu.mcw.rgd.indexer.model.genomeInfo.GeneCounts;
import edu.mcw.rgd.indexer.model.genomeInfo.GenomeIndexObject;
import edu.mcw.rgd.indexer.spring.XdbObjectQuery;
import edu.mcw.rgd.process.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.Index;
import org.elasticsearch.xcontent.XContentType;


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.Statement;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.client.Requests.refreshRequest;

/**
 * Created by jthota on 3/23/2017.
 */
public class IndexDAO extends AbstractDAO {

    private GeneDAO geneDAO = new GeneDAO();
    private StrainDAO strainDAO = new StrainDAO();
    private QTLDAO qtlDAO = new QTLDAO();
    private SSLPDAO sslpdao = new SSLPDAO();
    private AnnotationDAO annotationDAO = new AnnotationDAO();
    private XdbIdDAO xdbDAO = new XdbIdDAO();
    private PhenominerDAO phenominerDAO = new PhenominerDAO();
    private AliasDAO aliasDAO = new AliasDAO();
    private MapDAO mapDAO = new MapDAO();
    private TranscriptDAO transcriptDAO = new TranscriptDAO();
    private AssociationDAO adao = new AssociationDAO();
    private VariantInfoDAO vdao= new VariantInfoDAO();
    private VariantDAO variantDAO= new VariantDAO();
    private OntologyXDAO ontologyXDAO= new OntologyXDAO();
    private ReferenceDAO referenceDAO=new ReferenceDAO();
    private GenomeDAO genomeDAO=new GenomeDAO();
    private GenomicElementDAO gdao= new GenomicElementDAO();
    private AssociationDAO associationDAO=new AssociationDAO();
    private GenomicElementDAO gedao= new GenomicElementDAO();
    private RgdVariantDAO rgdVariantDAO =new RgdVariantDAO();
    Logger log= LogManager.getLogger("main");

    Map<Integer, edu.mcw.rgd.datamodel.Map> rgdMaps=new HashMap<>();
    public List<GenomeIndexObject> getGenomeInfo() throws Exception {
        List<GenomeIndexObject> objects= new ArrayList<>();
        for(int speciesTypeKey : SpeciesType.getSpeciesTypeKeys()) {
            //  int key=3;
            boolean isSearchable=SpeciesType.isSearchable(speciesTypeKey);
            if (speciesTypeKey != 0 && isSearchable) {
             //   System.out.println("SPECIES TYPE KEY: " + key);
                String species = SpeciesType.getCommonName(speciesTypeKey);
                List<edu.mcw.rgd.datamodel.Map> maps = mapDAO.getMaps(speciesTypeKey);
                for (edu.mcw.rgd.datamodel.Map m : maps) {

                    GenomeIndexObject obj = new GenomeIndexObject();
                    obj.setSpecies(species);
                    AssemblyInfo info = new AssemblyInfo();
                    info = genomeDAO.getAssemblyInfo(m);
                    //    info=genomeDAO.getAssemblyInfo(3, 70);
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
                    obj.setMapKey(m.getKey());
                    //      obj.setMapKey(70);

                    GeneCounts geneCounts = new GeneCounts();
                    geneCounts = genomeDAO.getGeneCounts(m.getKey(), speciesTypeKey, null);
                    // geneCounts= genomeDAO.getGeneCounts(70);
                    obj.setTotalGenes(geneCounts.getTotalGenes());
                    obj.setProteinCoding(geneCounts.getProteinCoding());
                    obj.setNcrna(geneCounts.getNcrna());
                    obj.settRna(geneCounts.gettRna());
                    obj.setSnRna(geneCounts.getSnRna());
                    obj.setrRna(geneCounts.getrRna());
                    obj.setPseudo(geneCounts.getPseudo());
                    obj.setTranscripts(geneCounts.getTranscripts());

                    Map<String, Integer> mirnaTargets = geneCounts.getMirnaTargets();
                    for (Map.Entry entry : mirnaTargets.entrySet()) {
                        String targetType = (String) entry.getKey();
                        int value = (int) entry.getValue();
                        if (targetType.equalsIgnoreCase("confirmed")) {
                            obj.setMirnaTargetsConfirmed(value);
                        }
                        if (targetType.equalsIgnoreCase("predicted")) {
                            obj.setMirnaTargetsPredicted(value);
                        }
                    }
                    objects.add(obj);
                }

            }else{
                if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                        || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tin getGenomeInfo Method"+"\t isSearchable: "+ isSearchable);
                }
            }
        }
     //   System.out.println("GENOMEINFO OBJECTS SIZE: "+ objects.size());

        return objects;
    }


    public void getGenes() throws Exception {
        List<Gene> genes= geneDAO.getAllActiveGenes();
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        for(Gene gene: genes) {
      // Gene gene= geneDAO.getGene(2493);
            Runnable workerThread= new IndexGene(gene);
            executor.execute(workerThread);
   }
        executor.shutdown();
        while (!executor.isTerminated()) {}

    }
    public void getExpression() throws Exception {
        List<Gene> genes= geneDAO.getAllActiveGenes();
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        for(Gene gene: genes) {
//            Gene gene= geneDAO.getGene(3001);
            Runnable workerThread= new IndexExpression(gene);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}

    }
    public int getAnnotsCount(int rgdId) throws Exception {
       List<Annotation> annots= annotationDAO.getAnnotations(rgdId);
        return annots.size();
    }
    public boolean containsId(List<Annotation> annots, String term_acc){
        for(Annotation a:annots){
            if(a.getTermAcc().equals(term_acc)){
                return true;
            }
        }
        return false;
    }

    public List<String> getTranscriptIds(int rgdId) throws Exception {
        List<Transcript> transcripts = transcriptDAO.getTranscriptsForGene(rgdId);
        List<String> tlist= new ArrayList<>();
        for(Transcript tr: transcripts){
           tlist.add(tr.getAccId());

        }
        return tlist;
    }
    public List<String> getTranscriptProteinIds(int rgdId) throws Exception {
        List<Transcript> transcripts = transcriptDAO.getTranscriptsForGene(rgdId);
        List<String> tlist= new ArrayList<>();
        for(Transcript tr: transcripts){
            tlist.add(tr.getProteinAccId());

        }
        return tlist;
    }
    public List<TranscriptData> getTranscripts(int rgdId) throws Exception {
        List<Transcript> transcripts = transcriptDAO.getTranscriptsForGene(rgdId);
        List<TranscriptData> tlist= new ArrayList<>();
        for(Transcript tr: transcripts){
            TranscriptData t= new TranscriptData();
            t.setTranscript_id(tr.getRgdId());
            t.setProtein_acc_id(tr.getProteinAccId());
            t.setTr_acc_id(tr.getAccId());
            tlist.add(t);
        }

        return tlist;
    }
    public List<String> getAnnotations(java.util.Map<String, List<Annotation>> annotMap, String annotType, String object) throws Exception {
        List<String> annotations= new ArrayList<>();
        switch(annotType.toLowerCase()){
            case "disease":

                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    List<Annotation> annots= (List<Annotation>) e.getValue();

                    if(key.equalsIgnoreCase("ClinVar")||key.equalsIgnoreCase("CTD")||key.equalsIgnoreCase("OMIM")
                            ||key.equalsIgnoreCase("GAD")||key.equalsIgnoreCase("ManualDisease")){

                        annotations.addAll(this.getAnnotatedObjects(annots, "Disease",object));
                    }

                }

                break;

            case "gene_chem":

                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    if(key.equalsIgnoreCase("gene_chem")){
                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Gene Chem", object));
                    }
                }
                break;
            case "bp":
                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    if(key.equalsIgnoreCase("bp")){
                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Biological Process", object));
                    }
                }
                break;
            case "cc":
                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    if(key.equalsIgnoreCase("cc")){
                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Cellular Component",object));
                    }
                }
                break;
            case "mf":
                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    if(key.equalsIgnoreCase("mf")){
                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Molecular Function", object));
                    }
                }
                break;
            case "pw":
                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    if(key.equalsIgnoreCase("rgd")||key.equalsIgnoreCase("smpdb")||key.equalsIgnoreCase("kegg")||key.equalsIgnoreCase("pid")||key.equalsIgnoreCase("otherPW")){
                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Pathway",object));
                    }
                }
                break;
            case "phenotype":
                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();
                    if(key.equalsIgnoreCase("mammalian phenotype")||key.equalsIgnoreCase("hp")){
                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Phenotype",object));
                    }
                }
                break;
            case "xdata":

                for(java.util.Map.Entry e:  annotMap.entrySet()){
                    String key= (String) e.getKey();

                    if(key.equalsIgnoreCase("co")||key.equalsIgnoreCase("cmo")||key.equalsIgnoreCase("xco")||key.equalsIgnoreCase("mmo")||key.equalsIgnoreCase("ma")||key.equalsIgnoreCase("rs")||key.equalsIgnoreCase("vt")){

                        List<Annotation> annots= (List<Annotation>) e.getValue();
                        annotations.addAll(this.getAnnotatedObjects(annots, "Experiemntal Data", object));
                    }

                }
                break;

        }

        return annotations;
    }
    public List<String> getAnnotatedObjects(List<Annotation> annots, String annotType, String object) throws Exception {
        List<String> annotations= new ArrayList<>();
        for(Annotation a:annots){
               annotations.add(a.getTerm());

            List<TermSynonym> term_synms=ontologyXDAO.getTermSynonyms(a.getTermAcc());

            for(TermSynonym s:term_synms){
                annotations.add(s.getName());
            }

    } return annotations;}
   public void getStrains() throws Exception{
       ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        //  Strain strain=strainDAO.getStrain(7248453);
  //      List<Alias> aliases=aliasDAO.getActiveAliases(RgdId.OBJECT_KEY_STRAINS);
        List<Strain> strains= strainDAO.getActiveStrains();
        Map<Integer, Gene> genes=getStrainAssociations(strains);
        for(Strain strain: strains) {
            Runnable workerThread= new IndexStrain(strain, genes);
            executor.execute(workerThread);
        }
       executor.shutdown();
       while (!executor.isTerminated()) {}
    }
    public Map<Integer, Gene> getStrainAssociations(List<Strain> strains) throws Exception {
        Map<Integer, Gene> genes= new HashMap<>();
        List<Integer> rgdIds=new ArrayList<>();

        for(Strain s:strains){
           rgdIds.add(s.getRgdId());
        }
        Collection[] colletions = this.split(rgdIds, 1000);
        Connection conn=null;
        Statement stmt= null;
        ResultSet rs=null;
        for(int i=0; i<colletions.length;i++){
            List c= (List) colletions[i];
            String sql="select s.rgd_id as strain_rgd_id, g.* from genes g, " +
                    "genes_variations gv, " +
                    "genes a, rgd_ids r, " +
                    "strains s," +
                    "rgd_strains_rgd rs " +
                    "where gv.gene_key=g.gene_key " +
                    "AND gv.variation_key=a.gene_key " +
                    "and a.rgd_id=rs.rgd_id " +
                    "and rs.strain_key=s.strain_key " +
                    "and r.rgd_id=g.rgd_id " +
                    "and r.object_status='ACTIVE' " +
                    "and s.rgd_id in ("+Utils.concatenate(c,",")+")";

           conn=this.getDataSource().getConnection();
           stmt= conn.createStatement();
          rs=stmt.executeQuery(sql);
            while(rs.next()){
                Gene g= new Gene();
                g.setSymbol(rs.getString("gene_symbol").toLowerCase());
                g.setName(rs.getString("full_name_lc"));

                genes.put(rs.getInt("strain_rgd_id"),g );
            }

            rs.close();
            stmt.close();
            if(!conn.isClosed()){
                conn.close();
            }
        }

        return genes;
    }

    public List<String> getAliasesByRgdId(List<Alias> aliases,int rgdid) throws Exception {
        List<String> synonyms = new ArrayList<>();
        for(Alias a: aliases){
            if(a.getRgdId()==rgdid) {
                String aliasType = a.getTypeName() == null ? "alias" : a.getTypeName();
                synonyms.add(a.getValue());
            }
        }
        return synonyms;
    }


    public Set<String> setSuggest(IndexObject object){
        Set<String> input= new HashSet<>();
        try{
            if(object.getName()!=null)
            input.add(object.getName());
        }catch (Exception e){}
        try{
            if(object.getSymbol()!=null)
            input.add(object.getSymbol());
        }catch (Exception e){}
        try{
            if(object.getTerm()!=null)
            input.add(object.getTerm());
        }catch (Exception e){}
        try{
            if(object.getTitle()!=null)
            input.add(object.getTitle());
        }catch (Exception e){}
        try{
            if(object.getAuthor()!=null)
            input.addAll(object.getAuthor());
        }catch (Exception e){}
        try{
            if(object.getSynonyms()!=null)
            input.addAll(object.getSynonyms());
        }catch (Exception e){}
//        try{
//            if(object.getHtmlStrippedSymbol()!=null)
//            input.add(object.getHtmlStrippedSymbol());
//        }catch (Exception e){}
        try{
            if(object.getOrigin()!=null)
            input.add(object.getOrigin());
        }catch (Exception e){}
        try{
            if(object.getType()!=null)
            input.add(object.getType());
        }catch (Exception e){}
        try{
            if(object.getTrait()!=null)
            input.add(object.getTrait());
        }catch (Exception e){}




        Map<String, Set<String>> suggestions=new HashMap<>();
        if(input.size()>0) {
            suggestions.put("input", input);
            object.setSuggest(suggestions);
        }
    return input;
    }

    public List<Annotation> getFilteredAnnotations(int rgdid) throws Exception {
        java.util.Map<String, List<Annotation>> annotMap= this.getAnnotations(rgdid);
        List<Annotation> distinctAnnots= new ArrayList<>();
        for(Map.Entry e: annotMap.entrySet()){
            List<Annotation> annots= (List<Annotation>) e.getValue();
            for(Annotation a:annots){
                if(!containsId(distinctAnnots, a.getTermAcc())){
                    distinctAnnots.add(a);
                }
            }

        }
        return distinctAnnots;
    }
    public int sampleExists(int strainRgdid, int patient_id) throws Exception {
        SampleDAO sampleDAO= new SampleDAO();
        sampleDAO.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
      Sample s=  sampleDAO.getSampleByStrainRgdId(strainRgdid, patient_id);
        if(s!=null){

            return 1;
        }else{
            return 0;
        }
    }
    public int getExperimentRecordCount(int rgdid, String aspect) throws Exception {
        AnnotationDAO annotationDAO=new AnnotationDAO();
        PhenominerDAO phenominerDAO=new PhenominerDAO();
        List<Annotation> annotations=annotationDAO.getAnnotationsByAspect(rgdid, aspect);
        int recordCount=0;
        for(Annotation a:annotations){
            String term_acc= a.getTermAcc();
            recordCount=recordCount+phenominerDAO.getRecordCountForTerm(term_acc);
        }
        return recordCount;
    }

    public void getQtls() throws Exception{
        List<IndexObject> objList= new ArrayList<>();
     //   List<Alias> aliases=aliasDAO.getActiveAliases(RgdId.OBJECT_KEY_QTLS);
        List<QTL> qtls=qtlDAO.getActiveQTLs();
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        for(QTL qtl: qtls) {
            // QTL qtl= qtlDAO.getQTL(61368);
            Runnable workerThread= new IndexQTL(qtl);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}

    }
    public String getTraitSubTrait(int rgdid, String aspect) throws Exception {
        NotesDAO notesDAO = new NotesDAO();
        String notesString=new String();
        if(aspect.equalsIgnoreCase("v")){
            notesString="qtl_trait";
        }
        if(aspect.equalsIgnoreCase("l")){
            notesString="qtl_subtrait";
        }
        String traitTerm = null;
        for( StringMapQuery.MapPair pair: annotationDAO.getAnnotationTermAccIds(rgdid, aspect) ) {
            traitTerm = pair.stringValue+" ("+pair.keyValue+")";
        }

        if( traitTerm==null ) {
            List<Note> notes = notesDAO.getNotes(rgdid, notesString);
            if( !notes.isEmpty() ) {
                traitTerm = notes.get(0).getNotes();
            }
        }


        return traitTerm;
    }
    public void getSslps() throws Exception{
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
     //   List<Alias> aliases=aliasDAO.getActiveAliases(RgdId.OBJECT_KEY_SSLPS);
        for(SSLP sslp: sslpdao.getActiveSSLPs()) {
            //  SSLP sslp= sslpdao.getSSLP(37320);
            Runnable workerThread=new IndexSslp(sslp);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()){}

    }

    public void getGenomicElements() throws Exception{
        this.getGenomicElements(RgdId.OBJECT_KEY_CELL_LINES);
       this.getGenomicElements(RgdId.OBJECT_KEY_PROMOTERS);
    }
    public void getGenomicElements(int objectKey) throws Exception {
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        String category=new String();
        if(objectKey==11){
            category="Cell line";
        }
        if(objectKey==16){
            category="Promoter";
        }

        Map<Integer, List<String>> associations=this.getAssociationsByObjectKey(objectKey);
        for(GenomicElement ge: gedao.getActiveElements(objectKey)) {
       //    GenomicElement ge= gedao.getElement(8655626);
            Runnable workerThread=new IndexGenomicElement(ge,category, associations);
            executor.execute(workerThread);
      }
        executor.shutdown();
        while (!executor.isTerminated()){}
    }
    public Map<Integer, List<String>> getAssociationsByObjectKey(int objectKey) throws Exception {
        AssociationDAO associationDAO=new AssociationDAO();
        RGDManagementDAO managementDAO=new RGDManagementDAO();
       Map<Integer, List<String>> associationsMap=new HashMap<>();
       for(Association a : associationDAO.getAssociationsByObjectKey(objectKey)){
           Object o=managementDAO.getObject(a.getDetailRgdId());
           List<String> associations=new ArrayList<>();
           associations= associationsMap.get(a.getMasterRgdId());
           String symbol=new String();
           if(o instanceof Gene) {
              symbol=((Gene) o).getSymbol();
           }
           if(o instanceof Strain) {
               symbol=((Strain) o).getSymbol();
           }
           if(!symbol.equals("")) {
               if (associations != null && !associations.contains(symbol))
                   associations.add(symbol);
               else {
                   associations = new ArrayList<>();
                   associations.add(symbol);
               }
           }
           if(associations!=null && associations.size()>0)
           associationsMap.put(a.getMasterRgdId(), associations);
           }
       return associationsMap;
    }
    public void getVariants() throws Exception{
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        List<VariantInfo> variants=vdao.getVariantsBySource("CLINVAR");
        for(VariantInfo obj: variants) {
            Runnable workerThread=new IndexClinVar(obj);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()){}
    }
    public void getAlleleVariants() throws Exception{
        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        List<RgdVariant> variants= rgdVariantDAO.getVariantsForSpecies(3); //species type key 3 for RAT
        for(RgdVariant obj: variants) {
            Runnable workerThread=new IndexAlleleVariant(obj);
            executor.execute(workerThread);
        }
        executor.shutdown();
        while (!executor.isTerminated()){}
    }
    public void indexVariantsFromCarpenovoNewTableStructure() throws Exception{
        ExecutorService executor2 = new MyThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        Runnable variantIndexerThread = null;

        for(int speciesTypeKey:Arrays.asList(2, 3, 6, 9, 13)) {
            String species = SpeciesType.getCommonName(speciesTypeKey);
            System.out.println("Processing " + species + " variants...");

           for( edu.mcw.rgd.datamodel.Map map : mapDAO.getMaps(speciesTypeKey)) {
               for (Chromosome chr : mapDAO.getChromosomes(map.getKey())) {
                     variantIndexerThread = new VariantIndexerThread(chr.getChromosome(), map.getKey(), speciesTypeKey);
                     executor2.execute(variantIndexerThread);
                   }
           }

        }
        executor2.shutdown();
        while (!executor2.isTerminated()) {}
    }
    public void getReference() throws Exception{

        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    //    List<Alias> aliases=aliasDAO.getActiveAliases(RgdId.OBJECT_KEY_REFERENCES);
        for(Reference ref: referenceDAO.getActiveReferences()){
            // Reference ref=referenceDAO.getReference(1004);
            Runnable workerThread=new IndexRef(ref);
            executor.execute(workerThread);
        }
    //}
        executor.shutdown();
        while (!executor.isTerminated()){}

    }

    public List<String> getAuthors(int refkey) throws Exception {
        List<String> authors= new ArrayList<>();
        for(Author author:referenceDAO.getAuthors(refkey)){
            authors.add(author.getLastName() + " " + author.getFirstName());
        }

        return authors;
    }

     public int[][] getAnnotsMatrix(TermWithStats termWithStats) throws Exception {


        int[][] annotsMatrix =new int[4][8];

        List<SpeciesObject> speciesObjects=this.getSpeicesObjects(termWithStats);
        int k=0;
        for(SpeciesObject s:speciesObjects){

            String species=s.getName().toLowerCase();

            if(species.equals("rat"))k=0;
            if(species.equals("human"))k=1;
            if(species.equals("mouse"))k=2;
            if(species.equals("chinchilla"))k=3;
            if(species.equals("dog"))k=4;
            if(species.equals("bonobo"))k=5;
            if(species.equals("squirrel"))k=6;
            if(species.equals("pig"))k=7;

            annotsMatrix[0][k]=s.getGeneCount();
            annotsMatrix[1][k]=s.getStrainCount();
            annotsMatrix[2][k]=s.getQtlCount();
            annotsMatrix[3][k]=s.getVariantCount();
        }

        return annotsMatrix;
    }
    /***************************************************************************************************/

    public List<SpeciesObject> getSpeicesObjects(TermWithStats termWithStats) throws Exception {
        List<SpeciesObject> sList= new ArrayList<>();
   //     List<Integer> speciesTypeKeys=new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7));
        Collection<Integer> speciesTypeKeys=  SpeciesType.getSpeciesTypeKeys();
        List<Integer> objectKeys= new ArrayList<>(Arrays.asList(1,5,6,7));
        for(int speciesTypeKey:speciesTypeKeys){
         boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);
            String species=SpeciesType.getCommonName(speciesTypeKey);
            if (isSearchable) {


            SpeciesObject s= new SpeciesObject();

            s.setName(species);
            int count=0;
            for(int okey:objectKeys){

                 count=termWithStats.getStat("annotated_object_count",speciesTypeKey, okey, 1,null );

                if(okey==1) s.setGeneCount(count);
                if(okey==5) s.setStrainCount(count);
                if(okey==6) s.setQtlCount(count);
                if(okey==7) s.setVariantCount(count);

            }
            sList.add(s);
        }else{
                if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                        || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                    throw new Exception("Species Type Key: " +speciesTypeKey +"Method: "+ "getSpeicesObjects()"+"\t isSearchable: "+ isSearchable);
                }
            }}
        return sList;
    }
    /*************************************************************************************************/

    public Collection[] split(List objs, int size) throws Exception{
        int numOfBatches=(objs.size()/size)+1;
        Collection[] batches= new Collection[numOfBatches];
        for(int index=0; index<numOfBatches; index++){
            int count=index+1;
            int fromIndex=Math.max(((count-1)*size),0);
            int toIndex=Math.min((count*size), objs.size());
            batches[index]= objs.subList(fromIndex, toIndex);
        }
        return batches;
    }


    public List<Annotation> getAnnotations(String term_acc) throws Exception {
        AnnotationDAO annotationDAO= new AnnotationDAO();
        List<Annotation> annots= annotationDAO.getAnnotations(term_acc);
        List<Annotation> filteredAnnots=new ArrayList<>();
        for(Annotation a: annots){
            if(!containsAnnotatedRgdId(filteredAnnots, a.getAnnotatedObjectRgdId())){
                filteredAnnots.add(a);
            }
        }

        return filteredAnnots;
    }
    public boolean containsAnnotatedRgdId(List<Annotation> filteredAnnots, int annotated_object_rgd_id){
        for(Annotation a:filteredAnnots){
            if(a.getAnnotatedObjectRgdId()==annotated_object_rgd_id){
                return true;
            }
        }
        return false;
    }

    public List<String> getTermSynonyms(List<TermSynonym> synonyms, String acc_id) throws Exception{
       List<String> synonymList= new ArrayList<>();

        List<TermSynonym> termSynonyms=ontologyXDAO.getTermSynonyms(acc_id);
        for(TermSynonym s:termSynonyms){
           synonymList.add(s.getName());
        }
        return synonymList;
    }

    public List<String> getTermSynonyms(String acc_id) throws Exception{
        List<String> synonymList= new ArrayList<>();

        List<TermSynonym> termSynonyms=ontologyXDAO.getTermSynonyms(acc_id);
        for(TermSynonym s:termSynonyms){
            synonymList.add(s.getName());
        }
        return synonymList;
    }

    public List<AliasData> getAliases(int rgdid) throws Exception {
        List<Alias> aliases=new ArrayList<>();
        aliases=aliasDAO.getAliases(rgdid);
        List<AliasData> aList= new ArrayList<>();
        if (aliases.size() > 0) {
            for(Alias a: aliases){
                AliasData ad= new AliasData();
                ad.setAlias_name(a.getValue());;
                ad.setAlias_type(a.getTypeName());
                aList.add(ad);
            }
        }
        return aList;
    }

    public List<MapInfo> getMapData(int rgdId) throws Exception {
        List<MapData> mapData = mapDAO.getMapDataByRank(rgdId);
        List<MapInfo> mapList= new ArrayList<>();
        for(MapData m: mapData){
            MapInfo map= new MapInfo();
            map.setChromosome(m.getChromosome());
            map.setStartPos(m.getStartPos());
            map.setStopPos(m.getStopPos());
            map.setMap(this.getMapofMapkey(m.getMapKey()));
            map.setRank(this.getMapRank(m.getMapKey()));
            mapList.add(map);
        }
        return mapList;
    }
    public String getMapofMapkey(int mapkey) throws Exception {

     /*   MapDAO mapDAO = new MapDAO();
        edu.mcw.rgd.datamodel.Map m = mapDAO.getMapByKey(mapkey);
        return m.getDescription();*/
     if(rgdMaps.get(mapkey)!=null){
         return rgdMaps.get(mapkey).getDescription();
     }else{
         edu.mcw.rgd.datamodel.Map m = mapDAO.getMapByKey(mapkey);
         rgdMaps.put(mapkey,m);
         return m.getDescription();
     }

    }
    public int getMapRank(int mapkey) throws Exception {
        if(rgdMaps.get(mapkey)!=null){
            return rgdMaps.get(mapkey).getRank();
        }else{
            edu.mcw.rgd.datamodel.Map m = mapDAO.getMapByKey(mapkey);
            rgdMaps.put(mapkey,m);
            return m.getRank();
        }

    }
    public java.util.Map<String, List<Annotation>> getAnnotations(int rgdId) throws Exception {

        java.util.Map<String, List<Annotation>> annotMap = new HashMap<>();
        List<Annotation> annotList = annotationDAO.getAnnotations(rgdId);

        int isReferenceRgd = 0;

        if (annotList.isEmpty()) {
            annotList = annotationDAO.getAnnotationsByReference(rgdId);
            if (annotList.size() > 0) {
                isReferenceRgd = 1;
            }
        }

        boolean hasPhenoMinerAnn = (annotationDAO.getPhenoAnnotationsCountByReference(rgdId) > 0);
        AnnotationFormatter af = new AnnotationFormatter();
        List<Annotation> filteredList = af.filterList(annotList, "D");
        if (filteredList.size() > 0) {
            // split annotations into 5 buckets
            List<Annotation> listClinVar = new ArrayList<>(filteredList.size());
            List<Annotation> listCTD = new ArrayList<>(filteredList.size());
            List<Annotation> listOmim = new ArrayList<>(filteredList.size());
            List<Annotation> listGAD = new ArrayList<>(filteredList.size());
            List<Annotation> listManual = new ArrayList<>(filteredList.size());

            for (Annotation ax : filteredList) {
                switch (ax.getDataSrc()) {
                    case "ClinVar":
                        listClinVar.add(ax);

                        break;
                    case "CTD":
                        listCTD.add(ax);

                        break;
                    case "OMIM":
                        listOmim.add(ax);

                        break;
                    case "GAD":
                        listGAD.add(ax);

                        break;
                    default:
                        listManual.add(ax);

                        break;
                }
            }
            annotMap.put("ClinVar", listClinVar);
            annotMap.put("CTD", listCTD);
            annotMap.put("OMIM", listOmim);
            annotMap.put("GAD", listGAD);
            annotMap.put("ManualDisease", listManual);


        }
        filteredList = af.filterList(annotList, "E");
        if (filteredList.size() > 0) {
            annotMap.put("Gene_chem", filteredList);
        }
        List<Annotation> bpList = af.filterList(annotList, "P");
        List<Annotation> ccList = af.filterList(annotList, "C");
        List<Annotation> mfList = af.filterList(annotList, "F");
        if ((bpList.size() + ccList.size() + mfList.size()) > 0) {
            if (bpList.size() > 0) {
                annotMap.put("BP", bpList);

            }
            if (ccList.size() > 0) {
                annotMap.put("CC", ccList);

            }
            if (mfList.size() > 0) {
                annotMap.put("MF", mfList);

            }
        }
        List<XdbId> xdbKeggPathways = xdbDAO.getXdbIdsByRgdId(XdbId.XDB_KEY_KEGGPATHWAY, rgdId);
        filteredList = af.filterList(annotList, "W");
        if (!filteredList.isEmpty() || xdbKeggPathways.size() > 0) {
            // split annotations into buckets
            List<Annotation> listManual = new ArrayList<Annotation>(filteredList.size());
            List<Annotation> listImportedPID = new ArrayList<Annotation>(filteredList.size());
            List<Annotation> listImportedKEGG = new ArrayList<Annotation>(filteredList.size());
            List<Annotation> listImportedSMPDB = new ArrayList<Annotation>(filteredList.size());
            List<Annotation> listImported = new ArrayList<Annotation>(filteredList.size());

            for (Annotation ax : filteredList) {
                if (Utils.stringsAreEqual(ax.getDataSrc(), "RGD")) {
                    listManual.add(ax);
                } else if (Utils.stringsAreEqual(ax.getDataSrc(), "SMPDB")) {
                    listImportedSMPDB.add(ax);
                } else if (Utils.stringsAreEqual(ax.getDataSrc(), "KEGG")) {
                    listImportedKEGG.add(ax);
                } else if (Utils.stringsAreEqual(ax.getDataSrc(), "PID")) {
                    listImportedPID.add(ax);
                } else {
                    listImported.add(ax);
                }
            }
            annotMap.put("RGD", listManual);
            annotMap.put("SMPDB", listImportedSMPDB);
            annotMap.put("KEGG", listImportedKEGG);
            annotMap.put("PID", listImportedPID);
            annotMap.put("otherPW", listImported);



        }
        List<Annotation> mpList = af.filterList(annotList, "N");
        List<Annotation> hpList = af.filterList(annotList, "H");
        if (mpList.size() + hpList.size() > 0) {
            if (mpList.size() > 0) {
                annotMap.put("Mammalian Phenotype", mpList);

            }
            if (hpList.size() > 0) {
                annotMap.put("HP", hpList);

            }
        }
        List<Annotation> maList = af.filterList(annotList, "A");
        List<Annotation> clList = af.filterList(annotList, "O");
        List<Annotation> vtList = af.filterList(annotList, "V");
        List<Annotation> rsList = af.filterList(annotList, "S");
        List<Annotation> cmoList = af.filterList(annotList, "L");
        List<Annotation> mmoList = af.filterList(annotList, "M");
        List<Annotation> xcoList = af.filterList(annotList, "X");

        int rgdid = phenominerDAO.getNumOfRecords(rgdId);

        if (((clList.size() + vtList.size() + cmoList.size() + mmoList.size() + xcoList.size() > 0) && (isReferenceRgd == 0)) ||
                ((isReferenceRgd == 1) && (rgdid > 0)) || hasPhenoMinerAnn) {
            annotMap.put("CO", clList);
            annotMap.put("CMO", cmoList);
            annotMap.put("XCO", xcoList);
            annotMap.put("MMO", mmoList);
            annotMap.put("MA", maList);
            annotMap.put("RS", rsList);
            annotMap.put("VT", vtList);

        }
        return annotMap;
    }

    public List<String> getPromotersByGeneRgdId(int rgdId) throws Exception {
        GenomicElementDAO gdao = new GenomicElementDAO();
        List<String> symbols = new ArrayList<>();
        List<Association> associations = adao.getAssociationsForDetailRgdId(rgdId, "promoter_to_gene");
        for (Association a : associations) {
            int mRgdId = a.getMasterRgdId();
            GenomicElement g = gdao.getElement(mRgdId);
            symbols.add(g.getSymbol());
        }
        return symbols;
    }
    public List<String> getPromoersByRgdId(int rgdId, List<Association> associations, List<GenomicElement> gElements) throws Exception {

        List<String> symbols = new ArrayList<>();
        for (Association a : associations) {
            if(a.getDetailRgdId()==rgdId) {
                int mRgdId = a.getMasterRgdId();
                GenomicElement g= getGenomicElement(mRgdId, gElements);
            //   GenomicElement g = gdao.getElement(mRgdId);
                if(g!=null)
                symbols.add(g.getSymbol());
            }
        }
        return symbols;
    }
    public GenomicElement getGenomicElement(int rgdId, List<GenomicElement> genomicElements){
        for(GenomicElement g: genomicElements){
            if(g.getRgdId()==rgdId){
                return g;
            }
        }
        return null;
    }

   public List<XdbObject> getXdbIdsByObjectKey(int objectKey) throws Exception {
       String sql = "SELECT  x.rgd_id, x.acc_id FROM rgd_acc_xdb x, rgd_ids i, rgd_objects o, rgd_xdb d WHERE x.rgd_id = i.rgd_id AND i.object_key = o.object_key AND x.xdb_key = d.xdb_key  AND i.object_status=\'ACTIVE\' AND i.species_type_key<>8" +
               " and o.object_key=?";
       XdbObjectQuery query= new XdbObjectQuery(this.getDataSource(), sql);
       List<XdbObject> xdbs= execute(query, new Object[]{objectKey});
       System.out.println("XDBS SIZE:"+xdbs.size());
       return xdbs;
   }
    public List<String> getXdbIds(List<XdbObject> objects,int rgdId) throws Exception {

        List<String> xdbIds= new ArrayList<>();
        for(XdbObject o:objects){
            if(o.getRgdId()==732446){
                xdbIds.add(o.getAccId());
            }
        }
        return xdbIds;
    }

    public  List<String> getExternalIdentifiers(int rgdId) throws Exception {


        List<String> xIds=new ArrayList<>();
        ResultSet rs = xdbDAO.getExternalIdsResultSet(rgdId);
        ExternalIdentifierXRef xref;
        while( (xref=xdbDAO.getNextExternalIdentifierXRef(rs))!=null ) {

            // limit processed xdb ids only to genes, sslps, qtls, strains and references
            if( !xref.getObjectType().equals("GENES") &&
                    !xref.getObjectType().equals("SSLPS") &&
                    !xref.getObjectType().equals("QTLS") &&
                    !xref.getObjectType().equals("STRAINS") &&
                    !xref.getObjectType().equals("VARIANTS") &&
                    !xref.getObjectType().equals("REFERENCES") ) {
                continue;
            }


            String id=xref.getExId();

            xIds.add(id);

        }
        return xIds;
    }
    public List<String> getChildTermAccIds(String parentTermAccId) throws Exception{
        List<String> childTermAccIds= new ArrayList<>();
        List<Term> childTerms= ontologyXDAO.getAllActiveTermDescendants(parentTermAccId);
        for(Term term:childTerms){
            if(!childTermAccIds.contains(term.getAccId()))
            childTermAccIds.add(term.getAccId());
        }
        return childTermAccIds;
    }
    public List<Annotation> getChildTermAnnotaions(String parentTermAccId) throws Exception {
        List<String> childTermsIds=  this.getChildTermAccIds(parentTermAccId);
        List<Annotation> allChildTermsAnnotations= new ArrayList<>();
         for(String id:childTermsIds){

           List<Annotation> childtermAnnots= annotationDAO.getAnnotations(id); //List of annotations included repeated annotated_object_rgd_id from mutiple sources
           allChildTermsAnnotations.addAll(childtermAnnots);

        }
        return allChildTermsAnnotations;
    }
    public String getPathwayUrl(String term_acc_id) throws Exception {
        PathwayDAO  pdao= new PathwayDAO();
        String url=null;
        Pathway pathway=pdao.getPathway(term_acc_id);
        if(pathway!=null){
             url="/rgdweb/pathway/pathwayRecord.html?acc_id="+ term_acc_id;
         }
        return url;
    }

    public void indexObjects(List<IndexObject> objs, String index, String type) throws ExecutionException, InterruptedException, IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        for (IndexObject o : objs) {
            byte[] json = new byte[0];
            try {
                json = mapper.writeValueAsBytes(o);
                BulkIndexProcessor.bulkProcessor.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }
    public void indexAITermMappingDocument(IndexObject document) {
      List<String> termSynonyms=document.getSynonyms();
      IndexObject termObject=new IndexObject();
      termObject.setTerm_acc(document.getTerm_acc());
      termObject.setTerm(document.getTerm());
      termObject.setCategory(document.getCategory());
      termObject.setSubcat(document.getSubcat());
      termObject.setType("term");
      indexDocument(termObject);
      if(termSynonyms!=null && termSynonyms.size()>0){
          for(String synonym:termSynonyms){
              IndexObject object=new IndexObject();
              object.setTerm_acc(document.getTerm_acc());
              object.setTerm(synonym);
          //    object.setSynonyms(Arrays.asList(synonym));
              object.setCategory(document.getCategory());
              object.setSubcat(document.getSubcat());
              object.setType("synonym");
              indexDocument(object);
          }
      }

    }
    public void indexDocument(IndexObject document) {
        byte[] json = new byte[0];
            try {
                json = JacksonConfiguration.MAPPER.writeValueAsBytes(document);
                BulkIndexProcessor.bulkProcessor.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
       

    }

    public static void main(String[] args) throws Exception {
          IndexDAO dao= new IndexDAO();
       Map<Integer,  List<String>> associations=   dao.getAssociationsByObjectKey(11);
      for(Map.Entry e:associations.entrySet()){
          System.out.println(e.getKey() +"\t"+ e.getValue().toString());
      }
        System.out.println("DONE!!");
    }


}
