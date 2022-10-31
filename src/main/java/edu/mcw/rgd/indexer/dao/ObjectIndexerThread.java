package edu.mcw.rgd.indexer.dao;


import edu.mcw.rgd.indexer.Manager;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.client.IndexAdmin;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.model.RgdIndex;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.client.Requests.refreshRequest;

/**
 * Created by jthota on 6/21/2017.
 */
public class ObjectIndexerThread implements  Runnable {
    private Thread t;
    private String objectType;
    private String index;
    private List<IndexObject> objects;
    IndexDAO indexDAO=new IndexDAO();
    public ObjectIndexerThread(){}
    public ObjectIndexerThread(String object, String indexName,List<IndexObject> objectList, Logger log){
        objectType=object;
        index=indexName;
        objects=objectList;
    }
    @Override
    public void run(){
        Logger log=Logger.getLogger("search");
        System.out.println(Thread.currentThread().getName()  + ": " + objectType+ " started " + new Date() );
        log.info(Thread.currentThread().getName()  + ": " + objectType+ " started " + new Date() );

        try {

            System.out.println(objectType + " size: " + objects.size());
            if(objects.size()>0){
                indexDAO.indexObjects(objects, index, "search");
            }

                System.out.println("Indexed " + objectType + " objects Size: " + objects.size() + " Exiting thread.");
                log.info("Indexed " + objectType + " objects Size: " + objects.size() + " Exiting thread.");


            System.out.println(Thread.currentThread().getName()  +  " " + objectType+ " END " + new Date() );
            log.info(Thread.currentThread().getName()  +  " " + objectType+ " END " + new Date());

        }  catch (Exception e) {
            e.printStackTrace();
            log.info(e);
            throw new RuntimeException();
        }

    }

 /*   public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger("main");

        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));

        Manager manager = (Manager) bf.getBean("manager");
        ESClient es= (ESClient) bf.getBean("client");
        IndexAdmin admin= (IndexAdmin) bf.getBean("admin");
        RgdIndex rgdIndex= (RgdIndex) bf.getBean("rgdIndex");

        List<String> indices= new ArrayList<>();
      //  if (envrionments.contains(args[1])) {
            rgdIndex.setIndex("search"+"_index" + "_" + "test");
            indices.add("search"+"_index" + "_" + "test" + "1");
            indices.add("search"+"_index" + "_" + "test" + "2");
            rgdIndex.setIndices(indices);
    //    }
        admin.createIndex("search_mappings", "search");
        ObjectIndexerThread indexer= new ObjectIndexerThread("Strains", RgdIndex.getNewAlias(), log);

        indexer.run();
        es.destroy();
        System.out.println("DONE!!!");
    }*/
}
