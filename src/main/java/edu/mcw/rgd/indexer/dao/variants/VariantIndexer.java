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
        List<VariantIndex> variantsList= new ArrayList<>();

        List<VariantResult> vrs=variantDao.getVariantResults(sampleId, chromosome, mapKey);
        if(vrs.size()>0){
            for(VariantResult vr:vrs){
                VariantIndex vi= new VariantIndex();

                vi.setVariant_id(vr.getVariant().getId());
                vi.setChromosome(vr.getVariant().getChromosome());
                vi.setEndPos(vr.getVariant().getEndPos());
                vi.setStartPos(vr.getVariant().getStartPos());
                vi.setRefNuc(vr.getVariant().getReferenceNucleotide());
                vi.setSampleId(vr.getVariant().getSampleId());
                vi.setTotalDepth(vr.getVariant().getDepth());
                vi.setVarFreq(vr.getVariant().getVariantFrequency());
                vi.setVariantType(vr.getVariant().getVariantType());
                vi.setVarNuc(vr.getVariant().getVariantNucleotide());
                vi.setZygosityStatus(vr.getVariant().getZygosityStatus());
                vi.setZygosityPossError(vr.getVariant().getZygosityPossibleError());
                vi.setZygosityPercentRead(vr.getVariant().getZygosityPercentRead());
                vi.setZygosityInPseudo(vr.getVariant().getZygosityInPseudo());
                vi.setGenicStatus(vr.getVariant().getGenicStatus());
                vi.setMapKey(mapKey);
                List<BigDecimal> conScores=new ArrayList<>();
                for(ConservationScore s:vr.getVariant().getConservationScore()){
                    conScores.add(s.getScore());
                }
                List<Integer> geneRgdIds=new ArrayList<>();
                List<String> geneSymbols= new ArrayList<>();
                for(MappedGene g: getMappedGenes(vr.getVariant(), mapKey)){
                    geneRgdIds.add(g.getGene().getRgdId());
                    geneSymbols.add(g.getGene().getSymbol());
                }
                vi.setGeneRgdIds(geneRgdIds);
                vi.setGeneSymbols(geneSymbols);
                vi.setConScores(conScores);
                variantsList.add(vi);
            }

        }
        if(variantsList.size()>0){
            BulkRequestBuilder bulkRequestBuilder= ESClient.getClient().prepareBulk();
            int docCount=1;

            for (VariantIndex o : variantsList) {
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
                    if(docCount>variantsList.size()-100 && docCount==variantsList.size()){
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
            System.out.println("Indexed mapKey " + mapKey + ",  Variant objects Size: " + variantsList.size() + " Exiting thread.");
            System.out.println(Thread.currentThread().getName() + ": VariantThread" + mapKey + " End " + new Date());
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
