package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;

import edu.mcw.rgd.dao.impl.SampleDAO;
import edu.mcw.rgd.dao.impl.VariantDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;

import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.client.IndexAdmin;

import edu.mcw.rgd.indexer.dao.ChromosomeThread;
import edu.mcw.rgd.indexer.dao.GenomeInfoThread;
import edu.mcw.rgd.indexer.dao.IndexerDAO;
import edu.mcw.rgd.indexer.dao.ObjectIndexerThread;

import edu.mcw.rgd.indexer.dao.findModels.FullAnnotDao;
import edu.mcw.rgd.indexer.dao.variants.*;
import edu.mcw.rgd.indexer.dao.variants.VariantIndexer;
import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.findModels.ModelIndexObject;
import edu.mcw.rgd.indexer.model.genomeInfo.ChromosomeIndexObject;
import edu.mcw.rgd.process.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

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
    private IndexAdmin admin;
    private static List<String> envrionments;
    private OntologySynonyms ontSynonyms;
    private RgdIndex rgdIndex;
    private boolean reindex;

    private static final Logger log = Logger.getLogger("main");

    public static void main(String[] args) throws Exception {

        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));

       Manager manager = (Manager) bf.getBean("manager");
       ESClient es= (ESClient) bf.getBean("client");
       RgdIndex rgdIndex= (RgdIndex) bf.getBean("rgdIndex");
        log.info("LEVEL:" +log.getLevel());
        log.info(manager.getVersion());

        try {

            List<String> indices= new ArrayList<>();
            if (envrionments.contains(args[1])) {
                rgdIndex.setIndex(args[2]+"_index" + "_" + args[1]);
                indices.add(args[2]+"_index" + "_" + args[1] + "1");
                indices.add(args[2]+"_index" + "_" + args[1] + "2");
                rgdIndex.setIndices(indices);
            }

        manager.run(args);
        } catch (Exception e) {
            if(es!=null)
            es.destroy();
            e.printStackTrace();
          manager.printUsage();
            log.info(e);
        }
        if(es!=null)
        es.destroy();

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

        args= (String[]) ArrayUtils.remove(args, 0);

        ExecutorService executor= new MyThreadPoolExecutor(10,10,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
            boolean searchIndexCreated=false;
            int runningThreadsCount=0;
            for (String arg : args) {
                List<String> indices= new ArrayList<>();
                Runnable workerThread;


                switch(arg){
                    case "Qtls" :
                    case "Strains" :
                    case "Genes" :
                    case "Sslps" :
                    case "GenomicElements" :
                    case "Annotations" :
                    case "Reference" :
                    case "Variants" :

                        if(!searchIndexCreated) {
                            admin.createIndex("search_mappings", "search");
                            searchIndexCreated=true;
                        }
                        if(!arg.equalsIgnoreCase("annotations")) {
                            runningThreadsCount = runningThreadsCount + 1;
                            workerThread = new ObjectIndexerThread(arg, RgdIndex.getNewAlias(), log);

                            executor.execute(workerThread);
                        }else{
                            OntologyXDAO ontologyXDAO = new OntologyXDAO();
                            List<Ontology> ontologies = ontologyXDAO.getPublicOntologies();
                            for (Ontology o : ontologies) {

                                String ont_id = o.getId();
                                List<TermSynonym> termSynonyms = (List<TermSynonym>) ontSynonyms.getClass().getMethod("get" + ont_id).invoke(ontSynonyms);
                                //     if(!ont_id.equalsIgnoreCase("CHEBI")) {

                                workerThread = new IndexerDAO(ont_id, o.getName(), RgdIndex.getNewAlias(), termSynonyms);
                                executor.execute(workerThread);
                            }
                        }

                    break;


                    case "Chromosomes":
                         admin.createIndex("chromosome_mappings", "chromosome");
                        if(arg.equalsIgnoreCase("chromosomes")){
                            MapDAO mapDAO= new MapDAO();
                            System.out.println("INDEXING Chromosomes...");
                            for(int key : SpeciesType.getSpeciesTypeKeys()) {
                                if (SpeciesType.isSearchable(key)) {
                                    //   int key=3;
                                    if (key != 0) {
                                        List<Map> maps = mapDAO.getMaps(key, "bp");
                                        for (Map m : maps) {
                                            int mapKey = m.getKey();
                                            String assembly = m.getName();
                                            if (mapKey != 6 && mapKey != 36 && mapKey != 8 && mapKey != 21 && mapKey != 19 && mapKey != 7) {
                                                workerThread = new ChromosomeThread(key, RgdIndex.getNewAlias(), mapKey, assembly);
                                                executor.execute(workerThread);
                                            }
                                        }

                                    }
                                }
                            }
                        }
                        break;
                    case "GenomeInfo":

                        admin.createIndex("genome_mappings", "genome");
                        System.out.println("INDEXING GENOMEINFO...");
                       for(int key : SpeciesType.getSpeciesTypeKeys()) {
                         //    int key=3;
                           if(SpeciesType.isSearchable(key)) {
                               if (key != 0) {
                                   workerThread = new GenomeInfoThread(key, RgdIndex.getNewAlias(), log);
                                   executor.execute(workerThread);
                               }
                           }
                      }
                        break;
                    case "Models":
                        System.out.println("Indexing models...");
                        admin.createIndex("models", "models");
                        FullAnnotDao dao= new FullAnnotDao();
                        List<String> aspects= new ArrayList<>(Arrays.asList("D", "B", "N"));
                      //  for(String aspect:aspects) {
                            List<ModelIndexObject> models = dao.getAnnotationsBySpeciesNObjectKey(3,5);
                            dao.indexModels(models);
                       // }
                        System.out.println("Indexing models is DONE!!");
                        break;
                    case "Variant":

                        MapDAO mapDAO= new MapDAO();
                        SampleDAO sdao= new SampleDAO();
                        sdao.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
                     //   List<Integer> speciesTypeKeys= new ArrayList<>(Arrays.asList(1,3, 6));
                     //   List<Integer> speciesTypeKeys= new ArrayList<>(Arrays.asList(3));
                        List<Integer> speciesTypeKeys= new ArrayList<>(Arrays.asList(6));
                        for(int species:speciesTypeKeys){
                            switch (species){
                                case 1:
                                    break;
                                case 3:
                                    admin.createIndex("variant_mappings", "variant");
                                    if(SpeciesType.isSearchable(species)) {
                                        List<Map> maps=mapDAO.getMaps(species);
                                        for(Map m:maps) {
                                            int mapKey = m.getKey();
                                            //    int mapKey=360;
                                            List<Sample> samples = sdao.getSamplesByMapKey(mapKey);
                                            if (samples != null && samples.size() > 0){
                                                List<Chromosome> chromosomes = mapDAO.getChromosomes(mapKey);

                                            for (Sample s : samples) {
                                                int sampleId = s.getId();
                                                //   int sampleId=911;
                                                for (Chromosome chr : chromosomes) {
                                                    //    Chromosome chr=mapDAO.getChromosome(360,"10");
                                                    workerThread = new VariantIndexer(sampleId, chr.getChromosome(), mapKey, species, RgdIndex.getNewAlias());
                                                    executor.execute(workerThread);
                                                }
                                            }
                                        }
                                        }
                                    }

                                    break;
                                case 6:
                                    admin.createIndex("variant_mappings", "variant");
                                    List<Map> maps=mapDAO.getMaps(species);
                                    System.out.println("DOG MAPS SIZE: "+ maps.size());
                                for(Map m:maps) {
                                      int mapKey = m.getKey();
                                    //   int mapKey=631;
                                        List<Sample> samples = sdao.getSamplesByMapKey(mapKey);

                                       if (samples.size() > 0){
                                            List<Chromosome> chromosomes = mapDAO.getChromosomes(mapKey);

                                           for (Sample s : samples) {
                                          // Sample s=samples.get(0);
                                                int sampleId = s.getId();
                                                //   int sampleId=911;
                                               for (Chromosome chr : chromosomes) {
                                                    //    Chromosome chr=mapDAO.getChromosome(360,"10");
                                          // Chromosome chr=chromosomes.get(0);
                                                    workerThread = new VariantIndexer(sampleId, chr.getChromosome(), mapKey, species, RgdIndex.getNewAlias());
                                                    executor.execute(workerThread);
                                                }
                                          }
                                        }
                                }
                                    break;
                                default:
                                    break;
                            }
                        }


                        break;
                    default:
                        break;

                }
            }
           executor.shutdown();
            while (!executor.isTerminated()) {}
            System.out.println("Finished all threads: " + new Date());
            log.info("Finished all threads: " + new Date());

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

    public boolean switchAlias() throws Exception {
        System.out.println("NEEW ALIAS: " + RgdIndex.getNewAlias() + " || OLD ALIAS:" + RgdIndex.getOldAlias());
        IndicesAliasesRequest request = new IndicesAliasesRequest();


        if (RgdIndex.getOldAlias() != null) {

            IndicesAliasesRequest.AliasActions removeAliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.REMOVE)
                            .index(RgdIndex.getOldAlias())
                            .alias(rgdIndex.getIndex());
            IndicesAliasesRequest.AliasActions addAliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(RgdIndex.getNewAlias())
                            .alias(rgdIndex.getIndex());
            request.addAliasAction(removeAliasAction);
            request.addAliasAction(addAliasAction);
            log.info("Switched from " + RgdIndex.getOldAlias() + " to  " + RgdIndex.getNewAlias());

        }else{
            IndicesAliasesRequest.AliasActions addAliasAction =
                    new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                            .index(RgdIndex.getNewAlias())
                            .alias(rgdIndex.getIndex());
            request.addAliasAction(addAliasAction);
            log.info(rgdIndex.getIndex() + " pointed to " + RgdIndex.getNewAlias());
        }
        AcknowledgedResponse indicesAliasesResponse =
                ESClient.getClient().indices().updateAliases(request, RequestOptions.DEFAULT);
    /*    if (RgdIndex.getOldAlias() != null) {
            ESClient.getClient().admin().indices().prepareAliases().removeAlias(RgdIndex.getOldAlias(), rgdIndex.getIndex())
                    .addAlias(RgdIndex.getNewAlias(), rgdIndex.getIndex()).execute().actionGet();
            System.out.println("Switched from " + RgdIndex.getOldAlias() + " to  " + RgdIndex.getNewAlias());
           log.info("Switched from " + RgdIndex.getOldAlias() + " to  " + RgdIndex.getNewAlias());

        } else {
           ESClient.getClient().admin().indices().prepareAliases()
                    .addAlias(RgdIndex.getNewAlias(), rgdIndex.getIndex()).execute().actionGet();
            System.out.println(rgdIndex.getIndex() + " pointed to " + RgdIndex.getNewAlias());
            log.info(rgdIndex.getIndex() + " pointed to " + RgdIndex.getNewAlias());
        }*/
        return  true;

    }
    public void printUsage(){
        System.out.println("INCORRECT ARGUMENTS. USAGE..." +
                "\n\t\tREINDEX ENV_OPT OPTION [OPTIONS..]   -   creates new index of specified OPTIONS [Genes Qtls etc] and switch index alias to this new index [RECOMMENDED]" +
                "\n\t\tUPDATE ENV_OPT OPTION [OPTIONS..]    -   updates existing index documents with speciefied OPTIONS[Genes Qtls etc]" +
                "\n\t\tENV_OPT                              -   dev/test/cur only one" +
                "\n\t\tOPTIONS                              -   options must be SPACE seperated and must provide atleast ONE OPTION from below OPTIONS List and case sensitive" +
                "\n\t\tOPTIONS LIST                         -   [Genes Qtls Strains Sslps Variants GenomicElements Reference Annotations]"

        );
        log.info("INCORRECT ARGUMENTS. USAGE..." +
                "\n\t\tREINDEX ENV_OPT OPTION [OPTIONS..] -   creates new index of speciefied OPTIONS [Genes Qtls etc] and switch index alias to this new index [RECOMMENDED]" +
                "\n\t\tUPDATE ENV_OPT OPTION [OPTIONS..] -   updates existing index documents with speciefied OPTIONS[Genes Qtls etc]" +
                "\n\t\tENV_OPT                    -   dev/test/cur only one" +
                "\n\t\tOPTIONS                    -   options must be SPACE seperated and must provide atleast ONE OPTION from below OPTIONS List and case sensitive" +
                "\n\t\tOPTIONS LIST               -   [Genes Qtls Strains Sslps Variants GenomicElements Reference Annotations]"

        );

    }

    public String getClusterHealth(String index) throws Exception {

        ClusterHealthRequest request = new ClusterHealthRequest(index);
        ClusterHealthResponse response = ESClient.getClient().cluster().health(request, RequestOptions.DEFAULT);
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

     public OntologySynonyms getOntSynonyms() {
        return ontSynonyms;
    }

    public void setOntSynonyms(OntologySynonyms ontSynonyms) {
        this.ontSynonyms = ontSynonyms;
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
        this.envrionments = envrionments;
    }

    public boolean isReindex() {
        return reindex;
    }

    public void setReindex(boolean reindex) {
        this.reindex = reindex;
    }
}
