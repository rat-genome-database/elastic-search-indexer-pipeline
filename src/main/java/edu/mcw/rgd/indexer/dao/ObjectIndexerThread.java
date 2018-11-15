package edu.mcw.rgd.indexer.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.model.IndexObject;


import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;

import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentType;


import java.util.Date;
import java.util.List;

/**
 * Created by jthota on 6/21/2017.
 */
public class ObjectIndexerThread implements  Runnable {
    private Thread t;
    private String objectType;
    private String index;
    private Logger log;

    public ObjectIndexerThread(){}
    public ObjectIndexerThread(String object, String indexName, Logger log){
        objectType=object;
        index=indexName;
        this.log=log;

    }
    @Override
    public void run(){
        System.out.println(Thread.currentThread().getName()  + ": " + objectType+ " started " + new Date() );
       log.info(Thread.currentThread().getName()  + ": " + objectType+ " started " + new Date() );

        try {
            IndexDAO dao=new IndexDAO();
        //    if(!objectType.equalsIgnoreCase("reference")) {
                List<IndexObject> objs = (List<IndexObject>) dao.getClass().getMethod("get" + objectType).invoke(dao);
                System.out.println(objectType + " size: " + objs.size());
            BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk();
                for (IndexObject obj : objs) {

                    ObjectMapper mapper = new ObjectMapper();
                    byte[] json = mapper.writeValueAsBytes(obj);
                 /*        IndexResponse response =ESClient.getClient().prepareIndex(index, "rgd_objects", obj.getTerm_acc())
                             .setSource(json).get();*/

                    bulkRequestBuilder.add(new IndexRequest(index, "search").source(json, XContentType.JSON));

            }
            BulkResponse response=       bulkRequestBuilder.get();

                System.out.println("Indexed " + objectType + " objects Size: " + objs.size() + " Exiting thread.");
                log.info("Indexed " + objectType + " objects Size: " + objs.size() + " Exiting thread.");
        //    }
          /*  if(objectType.equalsIgnoreCase("reference")) {
                List<RefObject> objs = (List<RefObject>) dao.getClass().getMethod("get" + objectType).invoke(dao);
                System.out.println(objectType + " size: " + objs.size());
                for (RefObject obj : objs) {
                    ObjectMapper mapper = new ObjectMapper();
                    byte[] json = mapper.writeValueAsBytes(obj);

                 /*  IndexResponse response = ESClient.getClient().prepareIndex(index, objectType.toLowerCase(), obj.getTerm_acc())
                         .setSource(json).get();*/

            /*        BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk();
                    bulkRequestBuilder.add(new IndexRequest(index, objectType.toLowerCase()).source(json, XContentType.JSON));
                    BulkResponse response=       bulkRequestBuilder.get();
                }
                System.out.println("Indexed " + objectType + " objects Size: " + objs.size() + " Exiting thread.");
                log.info("Indexed " + objectType + " objects Size: " + objs.size() + " Exiting thread.");
                }*/

            System.out.println(Thread.currentThread().getName()  +  " " + objectType+ " END " + new Date() );
            log.info(Thread.currentThread().getName()  +  " " + objectType+ " END " + new Date());

        }  catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

}
