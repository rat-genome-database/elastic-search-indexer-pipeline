package edu.mcw.rgd.indexer.dao.variants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.client.Requests.refreshRequest;

/**
 * Created by jthota on 6/26/2019.
 */
public class VariantIndexer  implements  Runnable{
    private int mapKey;
    private int speciesTypeKey;
    private String chromosome;
    private int sampleId;
    private String index;
    public VariantIndexer(int sampleId, String chromosome, int mapKey, int speciesTypeKey, String index){
        this.mapKey=mapKey;
        this.speciesTypeKey=speciesTypeKey;
        this.chromosome=chromosome;
        this.sampleId=sampleId;
        this.index=index;
    }

    @Override
    public void run() {
        VariantDao variantDao= new VariantDao();
        try {
            variantDao.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<VariantIndex> vrs=variantDao.getVariantResults(sampleId, chromosome, mapKey, SpeciesType.getCommonName(speciesTypeKey));
        System.out.println("Variants Size:"+vrs.size()+"\tMapKey:"+mapKey+"\tChr:"+chromosome+"\tSampleId:"+sampleId );
       if(vrs.size()>0){

           BulkProcessor.Listener listener = new BulkProcessor.Listener() {
               @Override
               public void beforeBulk(long executionId, BulkRequest request) {
                   //        System.out.println("ACTIONS: "+request.numberOfActions());
               }

               @Override
               public void afterBulk(long executionId, BulkRequest request,
                                     BulkResponse response) {
                   //     System.out.println("in process...");
               }

               @Override
               public void afterBulk(long executionId, BulkRequest request,
                                     Throwable failure) {

               }
           };

           BulkProcessor bulkProcessor = BulkProcessor.builder(
                   (request, bulkListener) ->
                           ESClient.getClient().bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                   listener)
                   .setBulkActions(10000)
                   .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                   .setFlushInterval(TimeValue.timeValueSeconds(5))
                   .setConcurrentRequests(1)
                   .setBackoffPolicy(
                           BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                   .build();
            for (VariantIndex o : vrs) {

               ObjectMapper mapper = new ObjectMapper();
               byte[] json = new byte[0];
               try {
                   json = mapper.writeValueAsBytes(o);
               } catch (JsonProcessingException e) {
                   e.printStackTrace();
               }
               bulkProcessor.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));
             }


           try {
               bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }finally {
               bulkProcessor.close();
           }
     /*      BulkRequest bulkRequest=new BulkRequest();
           bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

           int docCount=1;

            for (VariantIndex o : vrs) {
                docCount++;
                ObjectMapper mapper = new ObjectMapper();
                byte[] json = new byte[0];
                try {
                    json = mapper.writeValueAsBytes(o);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                bulkRequest.add(new IndexRequest(index).source(json, XContentType.JSON));
                if(docCount%100==0){

                    try {
                        BulkResponse response=      ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bulkRequest= new BulkRequest();


                }else{
                    if(docCount>vrs.size()-100 && docCount==vrs.size()){
                        try {
                            BulkResponse response=      ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bulkRequest= new BulkRequest();


                    }
                }

            }
*/
           RefreshRequest refreshRequest=new RefreshRequest();
           try {
               ESClient.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
           } catch (IOException e) {
               e.printStackTrace();
           }
           System.out.println("Indexed mapKey " + mapKey + ", chromosome: "+ chromosome+", Variant objects Size: " + vrs.size() + " Exiting thread.");
            System.out.println(Thread.currentThread().getName() + ": VariantThread" + mapKey +"\tSample: "+sampleId+"\tChromosome: "+chromosome+ " End " + new Date());
        }

    }
    public List<MappedGene> getMappedGenes(Variant v, int mapKey)  {
        GeneDAO gdao= new GeneDAO();
        List<MappedGene> mappedGenes = null;
        try {
            mappedGenes = gdao.getActiveMappedGenes(v.getChromosome(), v.getStartPos(), v.getEndPos(), mapKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappedGenes;
    }
}
