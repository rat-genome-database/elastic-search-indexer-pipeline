package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.Chromosome;
import edu.mcw.rgd.datamodel.Map;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;

import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.client.IndexAdmin;

import edu.mcw.rgd.indexer.dao.ChromosomeThread;
import edu.mcw.rgd.indexer.dao.GenomeInfoThread;
import edu.mcw.rgd.indexer.dao.IndexerDAO;
import edu.mcw.rgd.indexer.dao.ObjectIndexerThread;

import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.process.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

    private static final Logger log = Logger.getLogger("main");

    public static void main(String[] args) {
         Logger esLog= Logger.getLogger("test");
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));
        Manager manager = (Manager) bf.getBean("manager");
        ESClient es= (ESClient) bf.getBean("client");
        RgdIndex rgdIndex= (RgdIndex) bf.getBean("rgdIndex");
        List<String> indices= new ArrayList<>();

        System.out.println(manager.getVersion());
        Logger log= Manager.log;
        esLog.info(manager.getVersion());
        System.out.println("LEVEL:" +esLog.getLevel());
        log.info(manager.getVersion());
        try {
            if(envrionments.contains(args[1])){
                rgdIndex.setIndex("rgd_index_"+ args[1]);
                indices.add("rgd_index_"+args[1]+"1");
                indices.add("rgd_index_"+args[1]+"2");
                rgdIndex.setIndices(indices);

            }else{
                throw new Exception("Incorrect Arguments. Please see USAGE.");
            }
          manager.run(args);
        } catch (Exception e) {
            es.destroy();
            e.printStackTrace();
          manager.printUsage();
            log.info(e);
        }

    }

    private void run(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        boolean reindex=false;
        if (args.length <=2 || (!args[0].equalsIgnoreCase("update") && !args[0].equalsIgnoreCase("reindex"))) {
            printUsage();
            throw new Exception("INCORRECT ARGUMENTS. Please see the USAGE");

        }
       if(args[0].equalsIgnoreCase("reindex")) {
           reindex=true;
            admin.createIndex(log);
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
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        int runningThreadsCount=0;
        for (String arg : args) {
            if (!arg.equalsIgnoreCase("annotations") && !arg.equalsIgnoreCase("genomeinfo") && !arg.equalsIgnoreCase("chromosomes")) {
                runningThreadsCount=runningThreadsCount+1;
                Runnable workerThread = new ObjectIndexerThread(arg, RgdIndex.getNewAlias(), log);
                executor.execute(workerThread);
            } else {
                if (arg.equalsIgnoreCase("annotations")){
                OntologyXDAO ontologyXDAO = new OntologyXDAO();
                List<Ontology> ontologies = ontologyXDAO.getPublicOntologies();
                for (Ontology o : ontologies) {

                    String ont_id = o.getId();
                    List<TermSynonym> termSynonyms = (List<TermSynonym>) ontSynonyms.getClass().getMethod("get" + ont_id).invoke(ontSynonyms);
                    //     if(!ont_id.equalsIgnoreCase("CHEBI")) {

                    Runnable workerThread = new IndexerDAO(ont_id, o.getName(), RgdIndex.getNewAlias(), termSynonyms);
                    executor.execute(workerThread);
              //         }
                }
            }else{

                    if(arg.equalsIgnoreCase("genomeinfo")){
                        System.out.println("INDEXING GENOMEINFO...");
                      for(int key : SpeciesType.getSpeciesTypeKeys()) {
                            // int key=3;
                            if (key != 0) {
                                Runnable workerThread= new GenomeInfoThread(key, RgdIndex.getNewAlias(), log);
                                executor.execute(workerThread);
            }
                        }
                    }else {

                        if(arg.equalsIgnoreCase("chromosomes")){
                            MapDAO mapDAO= new MapDAO();
                            System.out.println("INDEXING Chromosomes...");
                         for(int key : SpeciesType.getSpeciesTypeKeys()) {
                              //   int key=3;
                                if (key != 0) {
                                List<Map> maps=    mapDAO.getMaps(key,"bp");
                                    for(Map m: maps){
                                        int mapKey= m.getKey();
                                        String assembly= m.getName();
                                        if(mapKey!=6 && mapKey!=36 && mapKey!=8 && mapKey!=21 && mapKey!=19 && mapKey!=7) {
                                            Runnable workerThread = new ChromosomeThread(key, RgdIndex.getNewAlias(), mapKey, assembly);
                                            executor.execute(workerThread);
                    }
                }

            }
                            }
                       }
                    }
                }

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

        if (RgdIndex.getOldAlias() != null) {
            ESClient.getClient().admin().indices().prepareAliases().removeAlias(RgdIndex.getOldAlias(), rgdIndex.getIndex())
                    .addAlias(RgdIndex.getNewAlias(), rgdIndex.getIndex()).execute().actionGet();
            System.out.println("Switched from " + RgdIndex.getOldAlias() + " to  " + RgdIndex.getNewAlias());
           log.info("Switched from " + RgdIndex.getOldAlias() + " to  " + RgdIndex.getNewAlias());

        } else {
           ESClient.getClient().admin().indices().prepareAliases()
                    .addAlias(RgdIndex.getNewAlias(), rgdIndex.getIndex()).execute().actionGet();
            System.out.println(rgdIndex.getIndex() + " pointed to " + RgdIndex.getNewAlias());
            log.info(rgdIndex.getIndex() + " pointed to " + RgdIndex.getNewAlias());
        }
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
        ClusterHealthResponse response = ESClient.getClient().admin().cluster().prepareHealth(index).execute().actionGet();
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
}
