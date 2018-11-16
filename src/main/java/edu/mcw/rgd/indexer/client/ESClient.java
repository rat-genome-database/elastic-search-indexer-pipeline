package edu.mcw.rgd.indexer.client;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jthota on 2/3/2017.
 */
public class ESClient {

  private static TransportClient client = null;
    private ESClient(){

    }
    public void init(){
        System.out.println("Initializing...");
        getInstance();
    }
    public void destroy(){
        System.out.println("destroying...");
        client.close();
        client=null;
    }

    public static TransportClient getClient() {
        return client;
    }

    public static void setClient(TransportClient client) {
        ESClient.client = client;
    }

    public static TransportClient getInstance() {

        if(client==null){
            Settings settings=Settings.builder().put("cluster.name", "erika").build();
            try {
                client= new PreBuiltTransportClient(settings)
                        .addTransportAddress(new TransportAddress(InetAddress.getByName("erika01.rgd.mcw.edu"), 9300));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }

        return client;
    }

}
