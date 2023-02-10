package edu.mcw.rgd.indexer.dao.variants;

import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.xcontent.XContentType;

import java.util.List;

public class VariantIndexingThread implements Runnable {
    private List<VariantIndex> indexList;
    public VariantIndexingThread(List<VariantIndex> indexList){
        this.indexList=indexList;
    }
    @Override
    public void run() {

      //  System.out.println("INDEX LSIT SIZE:"+indexList.size());

        for(VariantIndex vi:indexList) {
            try {
                String json = JsonMapper.serializer().mapper().writeValueAsString(vi);
                BulkIndexProcessor.bulkProcessor.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
