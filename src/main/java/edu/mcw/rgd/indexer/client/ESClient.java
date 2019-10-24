package edu.mcw.rgd.indexer.client;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
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

  private static RestHighLevelClient client = null;
    private ESClient(){}
    public void init(){
        System.out.println("Initializing...");
        client=getInstance();
    }
    public void destroy() throws IOException {
        System.out.println("destroying...");
        if(client!=null) {
            client.close();
            client = null;
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
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return client;
    }

}
