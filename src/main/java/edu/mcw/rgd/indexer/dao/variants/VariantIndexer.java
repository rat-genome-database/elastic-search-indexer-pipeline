package edu.mcw.rgd.indexer.dao.variants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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


        List<VariantIndex> vrs=variantDao.getVariantResults(sampleId, chromosome, mapKey);
       if(vrs.size()>0){
            BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk();
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

                bulkRequestBuilder.add(new IndexRequest(index, "variant").source(json, XContentType.JSON));
                if(docCount%100==0){
                    try {
                        BulkResponse response=       bulkRequestBuilder.execute().get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    bulkRequestBuilder= ESClient.getClient().prepareBulk();
                }else{
                    if(docCount>vrs.size()-100 && docCount==vrs.size()){
                        try {
                            BulkResponse response=       bulkRequestBuilder.execute().get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        bulkRequestBuilder= ESClient.getClient().prepareBulk();
                    }
                }

            }

            ESClient.getClient().admin().indices().refresh(refreshRequest()).actionGet();
            System.out.println("Indexed mapKey " + mapKey + ", chromosome: "+ chromosome+", Variant objects Size: " + vrs.size() + " Exiting thread.");
            System.out.println(Thread.currentThread().getName() + ": VariantThread" + mapKey +"\tSample "+sampleId+ " End " + new Date());
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
