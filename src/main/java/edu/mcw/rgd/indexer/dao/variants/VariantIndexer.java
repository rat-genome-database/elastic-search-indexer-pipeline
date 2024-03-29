package edu.mcw.rgd.indexer.dao.variants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import edu.mcw.rgd.services.ClientInit;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
          //  variantDao.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        } catch (Exception e) {
            e.printStackTrace();
        }


        List<VariantIndex> vrs=variantDao.getVariantResults(sampleId, chromosome, mapKey);
      //  System.out.println("Variants Size:"+vrs.size()+"\tMapKey:"+mapKey+"\tChr:"+chromosome+"\tSampleId:"+sampleId );
       if(vrs.size()>0){
           BulkRequest bulkRequest=new BulkRequest();
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
                        BulkResponse response=      ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bulkRequest= new BulkRequest();


                }else{
                    if(docCount>vrs.size()-100 && docCount==vrs.size()){
                        try {
                            BulkResponse response=      ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bulkRequest= new BulkRequest();


                    }
                }

            }

           RefreshRequest refreshRequest=new RefreshRequest();
           try {
               ClientInit.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
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
