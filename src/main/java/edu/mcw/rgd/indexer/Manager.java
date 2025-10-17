package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;


import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;

import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;


import edu.mcw.rgd.indexer.dao.*;

import edu.mcw.rgd.indexer.dao.findModels.FullAnnotDao;
import edu.mcw.rgd.indexer.indexers.phenominerIndexer.PhenominerNormalizedThread;


import edu.mcw.rgd.indexer.indexers.expressionIndexer.ExpressionDataIndexer;
import edu.mcw.rgd.indexer.indexers.genomeInfoIndexer.ChromosomeMapDataThread;
import edu.mcw.rgd.indexer.indexers.genomeInfoIndexer.GenomeInfoThread;
import edu.mcw.rgd.indexer.indexers.modelsIndexer.FindModels;

import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.indexer.dao.variants.BulkIndexProcessor;
import edu.mcw.rgd.services.ClientInit;
import edu.mcw.rgd.services.IndexAdmin;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * Created by jthota on 2/9/2017.
 */
public class Manager {
    private String version;
    private int threadCount;

    private static List<String> envrionments;
    private IndexAdmin admin;
    private RgdIndex rgdIndex;
    private OntologySynonyms ontologySynonyms;
    private boolean reindex;

    private BulkIndexProcessor bulkIndexProcessor;

   private ClientInit clientInit;
    IndexDAO indexDAO=new IndexDAO();
    private final Logger log = LogManager.getLogger(Manager.class);

    public static void main(String[] args) throws Exception {

        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));

        Manager manager = (Manager) bf.getBean("manager");
        manager.log.info(manager.getVersion());
        RgdIndex rgdIndex= (RgdIndex) bf.getBean("rgdIndex");
        OntologySynonyms synonyms= (OntologySynonyms) bf.getBean("ontologySynonyms");

        manager.bulkIndexProcessor=  bf.getBean("bulkIndexProcessor", BulkIndexProcessor.class);
        manager.clientInit= bf.getBean("clientInit", ClientInit.class);

        try {
            List<String> indices= new ArrayList<>();
            if (envrionments.contains(args[1])) {
                rgdIndex.setIndex(args[2]+"_index" + "_" + args[1]);
                indices.add(args[2]+"_index" + "_" + args[1] + "1");
                indices.add(args[2]+"_index" + "_" + args[1] + "2");
                rgdIndex.setIndices(indices);
            }
            synonyms.setIndexCategory(RgdIndex.getIndex());
            synonyms.init();
     manager.run(args);
        } catch (Exception e) {
            BulkIndexProcessor.destroy();
            ClientInit.destroy();
            manager.printUsage();

            Utils.printStackTrace(e, manager.log);
        }
        BulkIndexProcessor.destroy();
        ClientInit.destroy();


    }
    private void run(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        if (args.length <=2 || (!args[0].equalsIgnoreCase("update") && !args[0].equalsIgnoreCase("reindex"))) {
            printUsage();
            throw new Exception("INCORRECT ARGUMENTS. Please see the USAGE");

        }
        if(args[0].equalsIgnoreCase("reindex")) {
           reindex=true;
            args= (String[]) ArrayUtils.remove(args, 0);
        }
        if(args[0].equalsIgnoreCase("update")) {
            int update=admin.updateIndex();
            if(update==0){
                return;
            }
            args= (String[]) ArrayUtils.remove(args, 0);
        }
        ExecutorService executor=null;
        args= (String[]) ArrayUtils.remove(args, 0);

            boolean searchIndexCreated=false;
            boolean expressionSearchIndexCreated=false;
            for (String arg : args) {
                Runnable workerThread;
                switch (arg) {
                    case "Qtls",
                        "Strains" ,
                        "Genes",
                        "Sslps",
                        "GenomicElements",
                        "Annotations" , // all public ontologies
                        "Reference",
                        "Variants",// these are only ClinVar variants
                           "AlleleVariants" // these are RGD allele variants
                            -> {
                        System.out.println("Running Object Search Indexer ....");
                        if (!searchIndexCreated) {
                            admin.createIndex("search_mappings", "search");
                            searchIndexCreated = true;
                        }

                            System.out.println("Indexing ..."+ arg);
                            indexDAO.getClass().getMethod("get" + arg).invoke(indexDAO);
                            System.out.println("Indexing ..."+ arg +" DONE");
                    }
                    case "Chromosomes" -> {
                         executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

                        System.out.println("Running Chromosome Indexer ....");
                        admin.createIndex("chromosome_mappings", "chromosome");
                        MapDAO mapDAO = new MapDAO();
                        log.info("INDEXING Chromosomes...");
                        for (int key : SpeciesType.getSpeciesTypeKeys()) {
                            if (key != 0 && SpeciesType.isSearchable(key)) {
                                List<Map> maps = null;
                                    try {
                                        maps = mapDAO.getMaps(key, "bp");
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    for (Map m : maps) {
                                        workerThread = new ChromosomeMapDataThread(key, m);
                                        executor.execute(workerThread);
                                    }

                            }
                        }
                    }
                    case "GenomeInfo" -> {
                         executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

                        System.out.println("Running Genome Info Indexer ....");
                        admin.createIndex("genome_mappings", "genome");
                        System.out.println("INDEXING GENOMEINFO...");
                        for (int key : SpeciesType.getSpeciesTypeKeys()) {
//                        int key=3;
                            if (SpeciesType.isSearchable(key)) {
                                if (key != 0) {
                                    workerThread = new GenomeInfoThread(key, RgdIndex.getNewAlias());
                                    executor.execute(workerThread);
                                }
                            }
                        }
                    }
                    case "Phenominer" -> {
                        System.out.println("Running Phenominer Indexer ....");
                        admin.createIndex("phenominer_mappings", "genome");
                        System.out.println("INDEXING phenominer records...");
                        PhenominerNormalizedThread thread = new PhenominerNormalizedThread(RgdIndex.getNewAlias());
                        thread.run();
                    }
                    case "Models" -> {
                        System.out.println("Indexing models...");
                        admin.createIndex("models", "models");
                        FullAnnotDao dao = new FullAnnotDao();
                        List<String> aspects = new ArrayList<>(Arrays.asList("D", "B", "N"));
//                        List<ModelIndexObject> models = dao.getAnnotationsBySpeciesNObjectKey(3, 5);
//                        dao.indexModels(models);
                        FindModels models=new FindModels();
                        models.getModelIndexObjects(3, 5);
                        System.out.println("Indexing models is DONE!!");
                    }
                    case "Variant" -> { // all species variants
                        System.out.println("Running Variant General Search Indexer ....");
                        admin.createIndex("variant_mappings", "variant");
                        indexDAO.indexVariantsFromCarpenovoNewTableStructure();
                    }
                    case "AITermMappings" -> { // all species variants
                         executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

                        System.out.println("Running AI Term - Ontology Term Mapper Indexer ....");
                        admin.createIndex("ai_mappings", "ai_mappings");
                        OntologyXDAO ontologyXDAO = new OntologyXDAO();
                        List<Ontology> ontologies = ontologyXDAO.getPublicOntologies();
                        for (Ontology o : ontologies) {
                            String ont_id = o.getId();
                            List<TermSynonym> termSynonyms = (List<TermSynonym>) OntologySynonyms.ontSynonyms.get(ont_id);
                            workerThread = new IndexerDAO(ont_id, o.getName(), RgdIndex.getNewAlias(), termSynonyms, true);
                            executor.execute(workerThread);

                        }
                    }
                    case "ExpressionGene",
                            "ExpressionStudy"-> { // for general search
                        System.out.println("Running "+arg+" Indexer ....");
                        if (!expressionSearchIndexCreated) {
                            admin.createIndex("search_mappings", "expression");
                            expressionSearchIndexCreated = true;
                        }

                        System.out.println("Indexing ..."+ arg);
                        indexDAO.getClass().getMethod("get" + arg).invoke(indexDAO);

                    }
                    case "ExpressionData"-> { // for plotting the data in standalone expression UI tool
                         executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

                        System.out.println("Running Expression General Search Indexer ....");
                        admin.createIndex(null, null);
                        GeneDAO geneDAO = new GeneDAO();
                        List<Gene> genes = geneDAO.getActiveGenes(3);
                        for (Gene gene : genes) {
                            workerThread = new ExpressionDataIndexer(gene);
                            executor.execute(workerThread);
                        }
                        executor.shutdown();
                        while (!executor.isTerminated()) {
                        }
                    }
                    default -> {
                    }
                }
            }
            if(executor!=null) {
                executor.shutdown();
                while (!executor.isTerminated()) {
                }
                System.out.println("Finished all threads: " + new Date());
                log.info("Finished all threads: " + new Date());
            }


            String clusterStatus = this.getClusterHealth(RgdIndex.getNewAlias());
            if (!clusterStatus.equalsIgnoreCase("ok")) {
                System.out.println(clusterStatus + ", refusing to continue with operations");
                log.info(clusterStatus + ", refusing to continue with operations");
            } else {
                if(reindex) {
                    System.out.println("CLUSTER STATUR:"+ clusterStatus+". Switching Alias...");
                    log.info("CLUSTER STATUR:"+ clusterStatus+". Switching Alias...");
                    switchAlias();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println(" - " + Utils.formatElapsedTime(start, end));
            log.info(" - " + Utils.formatElapsedTime(start, end));
            System.out.println("CLIENT IS CLOSED");
        }

    public void switchAlias() throws Exception {
        System.out.println("NEEW ALIAS: " + RgdIndex.getNewAlias() + " || OLD ALIAS:" + RgdIndex.getOldAlias());
        IndicesAliasesRequest request = new IndicesAliasesRequest();


        if (RgdIndex.getOldAlias() != null) {

            IndicesAliasesRequest.AliasActions removeAliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE)
                            .index(RgdIndex.getOldAlias())
                            .alias(RgdIndex.getIndex());
            IndicesAliasesRequest.AliasActions addAliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(RgdIndex.getNewAlias())
                            .alias(RgdIndex.getIndex());
            request.addAliasAction(removeAliasAction);
            request.addAliasAction(addAliasAction);
            log.info("Switched from " + RgdIndex.getOldAlias() + " to  " + RgdIndex.getNewAlias());

        }else{
            IndicesAliasesRequest.AliasActions addAliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(RgdIndex.getNewAlias())
                            .alias(RgdIndex.getIndex());
            request.addAliasAction(addAliasAction);
            log.info(RgdIndex.getIndex() + " pointed to " + RgdIndex.getNewAlias());
        }
        AcknowledgedResponse indicesAliasesResponse =
                ClientInit.getClient().indices().updateAliases(request, RequestOptions.DEFAULT);

    }
    public void printUsage(){
        log.info(usageInfo());
    }
    public String usageInfo(){
        return "INCORRECT ARGUMENTS. USAGE..." +
                  "\n\t\tREINDEX ENV_OPT INDEX_NAME OPTION [OPTIONS..]   -   creates new index of specified OPTIONS [Genes Qtls etc] and switch index alias to this new index [RECOMMENDED]" +
                  "\n\t\tUPDATE ENV_OPT INDEX_NAME OPTION [OPTIONS..]    -   updates existing index documents with speciefied OPTIONS[Genes Qtls etc]" +
                  "\n\t\tINDEX_NAME                           -    for example: search or chromosome etc" +
                  "\n\t\tENV_OPT                              -   dev/test/cur only one" +
                  "\n\t\tOPTIONS                              -   options must be SPACE seperated and must provide atleast ONE OPTION from below OPTIONS List and case sensitive" +
                  "\n\t\tOPTIONS LIST                         -   [ObjectSearch, Chromosomes, Models, Variants, GenomeInfo, Phenominer]";
    }
    public String getClusterHealth(String index) throws Exception {

        ClusterHealthRequest request = new ClusterHealthRequest(index);
        ClusterHealthResponse response = ClientInit.getClient().cluster().health(request, RequestOptions.DEFAULT);
      /*  ClusterHealthResponse response = ESClient.getClient().admin().cluster().prepareHealth(index).execute().actionGet();*/
        System.out.println(response.getStatus().name());
        log.info("CLUSTER STATE: " + response.getStatus().name());
        if (response.isTimedOut()) {
            return   "cluster state is " + response.getStatus().name();
        }

        return "OK";
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public IndexAdmin getAdmin() {
        return admin;
    }

    public void setAdmin(IndexAdmin admin) {
        this.admin = admin;
    }

    public void setRgdIndex(RgdIndex rgdIndex) {
        this.rgdIndex = rgdIndex;
    }

    public RgdIndex getRgdIndex() {
        return rgdIndex;
    }

    public List<String> getEnvrionments() {
        return envrionments;
}

    public void setEnvrionments(List<String> envrionments) {
        Manager.envrionments = envrionments;
    }

    public boolean isReindex() {
        return reindex;
    }

    public void setReindex(boolean reindex) {
        this.reindex = reindex;
    }

    public OntologySynonyms getOntologySynonyms() {
        return ontologySynonyms;
    }

    public void setOntologySynonyms(OntologySynonyms ontologySynonyms) {
        this.ontologySynonyms = ontologySynonyms;
    }

    public BulkIndexProcessor getBulkIndexProcessor() {
        return bulkIndexProcessor;
    }

    public void setBulkIndexProcessor(BulkIndexProcessor bulkIndexProcessor) {
        this.bulkIndexProcessor = bulkIndexProcessor;
    }

    public ClientInit getClientInit() {
        return clientInit;
    }

    public void setClientInit(ClientInit clientInit) {
        this.clientInit = clientInit;
    }
}
