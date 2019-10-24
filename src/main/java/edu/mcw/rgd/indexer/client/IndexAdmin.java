package edu.mcw.rgd.indexer.client;




import edu.mcw.rgd.indexer.model.RgdIndex;
import org.apache.log4j.Logger;

import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by jthota on 7/10/2017.
 */
public class IndexAdmin {


    private Logger log;
    private RgdIndex rgdIndex;

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public void createIndex(Logger log, String mappings, String type) throws Exception {

        this.setLog(log);
  //    IndicesAdminClient indicesAdminClient= ESClient.getClient().admin().indices();
    // IndicesExistsResponse existsResponse= indicesAdminClient.exists(new IndicesExistsRequest(rgdIndex.getIndex())).actionGet();


        GetIndexRequest request=new GetIndexRequest(rgdIndex.getIndex());
        request.local(false);
        request.humanReadable(true);
        boolean indicesExists=ESClient.getClient().indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(rgdIndex.getIndex() + " :" + indicesExists);
        log.info(rgdIndex.getIndex() +" :"+indicesExists);
        if(indicesExists) {  /* CHECK IF INDEX NAME PROVIDED EXISTS*/
     //     GetAliasesResponse response = indicesAdminClient.getAliases(new GetAliasesRequest().indices(rgdIndex.getIndex())).actionGet();
            GetAliasesRequest aliasesRequest=new GetAliasesRequest(rgdIndex.getIndex());
            boolean existsAlias=ESClient.getClient().indices().existsAlias(aliasesRequest, RequestOptions.DEFAULT);

            if (existsAlias) {
            for (String index : rgdIndex.getIndices()) {
            /* INDEX IS NOT ALIAS AND EXISTS, then DELETE INDEX AND CREATE NEW INDEX WITH SAME NAME.*/
             //   if (!response.getAliases().containsKey(index)) {
                    if (!Arrays.asList(aliasesRequest.aliases()).contains(index)) {// if index is not  alias to current index(rgd_index_dev)
                        GetIndexRequest request1=new GetIndexRequest(index);
                        //         IndicesExistsRequest request = new IndicesExistsRequest(index); // check if index exists
                        boolean indexExists=ESClient.getClient().indices().exists(request1, RequestOptions.DEFAULT);
           //         boolean indicesExists = indicesAdminClient.exists(request).actionGet().isExists();
                   if (indexExists) {   /**** delete index if exists ****/
                  //    DeleteIndexResponse deleteResponse = indicesAdminClient.prepareDelete(index).get();
                       DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
                       org.elasticsearch.action.support.master.AcknowledgedResponse deleteIndexResponse = ESClient.getClient().indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);

                       System.out.println(index + " deleted");
                        log.info(index + " deleted");
                    }else
                      createNewIndex(index, mappings, type);
              }
            /*INDEX IS ALIAS TO CURRENT INDEX, SET IT AS OLD INDEX TO SWITCH ALIAS FROM OLD INDEX TO NEW INDEX CREATED ABOVE */
                else {
                    RgdIndex.setOldAlias(index);
                }
            }
        }else{
               if (aliasesRequest.aliases().length==0) {
                    createNewIndex(rgdIndex.getIndex()+"1",mappings, type);
                }
            }
        }else{ // IF INDEX NAME PROVIDED DOES NOT EXISTS THEN CREATE NEW INDEX
            System.out.println(rgdIndex.getIndex() + " does not exists.");
            this.createNewIndex(rgdIndex.getIndex()+"1", mappings, type);
        }

    }
    public void createNewIndex(String index, String _mappings, String type) throws Exception {

        String path="data/"+_mappings+".json";
        System.out.println("PATH: " +path+"\n"+ type);
        System.out.println("CREATING NEW INDEX..." + index);
        log.info("CREATING NEW INDEX..." + index);

        String mappings=new String(Files.readAllBytes(Paths.get(path)));
        String analyzers=new String(Files.readAllBytes(Paths.get("data/analyzers.json")));

        /********* create index, put mappings and analyzers ****/
        CreateIndexRequest request=new CreateIndexRequest(index);
        request.settings(Settings.builder()
                .put("index.number_of_shards",1)
                .put("index.number_of_replicas", 0)
        .loadFromSource(analyzers,XContentType.JSON));
        request.mapping(mappings, XContentType.JSON);
        org.elasticsearch.client.indices.CreateIndexResponse createIndexResponse = ESClient.getClient().indices().create(request, RequestOptions.DEFAULT);

        System.out.println(index + " created on  " + new Date());
        log.info(index + " created on  " + new Date());

        RgdIndex.setNewAlias(index);
    }
    public int updateIndex() throws Exception {
        if(rgdIndex.getIndex()!=null) {
            System.out.println("Updating " + rgdIndex.getIndex() + "...");
            GetIndexRequest request=new GetIndexRequest(rgdIndex.getIndex());
            boolean indicesExists=ESClient.getClient().indices().exists(request, RequestOptions.DEFAULT);
            if (indicesExists) {  /* CHECK IF INDEX NAME PROVIDED EXISTS*/

                RgdIndex.setNewAlias(rgdIndex.getIndex());

                return 1;
            } else {
                System.out.println("Cannot Update. " + rgdIndex.getIndex() + " does not exists. Use REINDEX option to create index");
                return 0;
            }
        }else {
            System.out.println("INDEX cannot be null");
            return 0;
        }
    }


    public void setRgdIndex(RgdIndex rgdIndex) {
        this.rgdIndex = rgdIndex;
    }

    public RgdIndex getRgdIndex() {
        return rgdIndex;
    }


    public static void main(String[] args) throws IOException {
        IndexAdmin admin= new IndexAdmin();

        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new FileSystemResource("properties/AppConfigure.xml"));

        ESClient es= (ESClient) bf.getBean("client");
        admin.rgdIndex= (RgdIndex) bf.getBean("rgdIndex");
        List<String> indices= new ArrayList<>();
        admin.rgdIndex.setIndex("rgd_index_"+ "dev");
        indices.add("rgd_index_"+"dev"+"1");
        indices.add("rgd_index_"+"dev"+"2");
        admin.rgdIndex.setIndices(indices);


        Logger log= Logger.getLogger(IndexAdmin.class);
        try {
            admin.createIndex(log, "","");
        } catch (Exception e) {

            e.printStackTrace();
        }

        es.destroy();
    }
}
