package edu.mcw.rgd.indexer.client;


import edu.mcw.rgd.indexer.Manager;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.log4j.Logger;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.security.user.privileges.ManageApplicationPrivilege;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
        return client;
    }

    public static void setClient(RestHighLevelClient client) {
        ESClient.client = client;
    }

    public static RestHighLevelClient getInstance() {

        if(client==null){
            Settings settings=Settings.builder().put("cluster.name", "green").build();
            try {
              /*  client= new PreBuiltTransportClient(settings)
                        .addTransportAddress(new TransportAddress(InetAddress.getByName("green.rgd.mcw.edu"), 9300));*/
                client=new RestHighLevelClient(RestClient.builder(
                        new HttpHost("green.rgd.mcw.edu", 9200, "http")

                ).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback(){

                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder
                                .setConnectTimeout(5000)
                                .setSocketTimeout(60000);
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

}
