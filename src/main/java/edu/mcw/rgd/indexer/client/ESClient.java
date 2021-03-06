package edu.mcw.rgd.indexer.client;


import edu.mcw.rgd.indexer.Manager;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;


/**
 * Created by jthota on 2/3/2017.
 */
public class ESClient {
  private static Logger log=Logger.getLogger(Manager.class);
  private static RestHighLevelClient client = null;
    private ESClient(){}
    public void init(){
        System.out.println("Initializing...");
        client=getInstance();
    }
    public void destroy() throws IOException {
        System.out.println("destroying...");
        if(client!=null) {
            try{
                client.close();
                client = null;
            }catch (Exception e){
                log.info(e);
            }

        }
    }

    public static RestHighLevelClient getClient() {
        return getInstance();
    }

    public static void setClient(RestHighLevelClient client) {
        ESClient.client = client;
    }

    public static RestHighLevelClient getInstance() {

        if(client==null){
           // Settings settings=Settings.builder().put("cluster.name", "green").build();
            Properties props= getProperties();

             try {
                  client=new RestHighLevelClient(RestClient.builder(
                        new HttpHost(props.get("HOST1").toString(), 9200, "http"),
                        new HttpHost(props.get("HOST2").toString(), 9200, "http"),
                        new HttpHost(props.get("HOST3").toString(), 9200, "http"),
                        new HttpHost(props.get("HOST4").toString(), 9200, "http"),
                        new HttpHost(props.get("HOST5").toString(), 9200, "http")

                ).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback(){

                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder
                                .setConnectTimeout(5000)
                                .setSocketTimeout(120000);
                    }
                })
                );

            } catch (Exception e) {
                log.info(e);
                e.printStackTrace();
            }

        }

        return client;
    }
     static Properties getProperties(){
        Properties props= new Properties();
        FileInputStream fis=null;


        try{
      //   fis=new FileInputStream("C:/Apps/properties.properties");
          fis=new FileInputStream("/data/pipelines/properties/es_properties.properties");
            props.load(fis);

        }catch (Exception e){
            e.printStackTrace();
        }
         try {
             if (fis != null) {
                 fis.close();
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
         return props;
    }

}
