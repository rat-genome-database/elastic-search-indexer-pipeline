package edu.mcw.rgd.indexer.dao.variants;


import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;

import java.util.*;
import java.util.stream.Collectors;

public class VariantIndexingThread extends VariantDao implements Runnable {
    private final List<Integer> batch;
    private final int mapKey;

    VariantDao variantDao=new VariantDao();

    public VariantIndexingThread(List<Integer> batch, int mapKey){
        this.batch=batch;
        this.mapKey=mapKey;

    }
    @Override
    public void run() {
        try {
            List<VariantIndex> indexList = variantDao.getVariantsNewTbaleStructure(mapKey, batch);
            sortVariants(indexList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void sortVariants(List<VariantIndex> indexList ) throws Exception {
        Map<Integer, List<VariantIndex>> rowsByVariantId = indexList.stream()
                .collect(Collectors.groupingBy(v -> (int) v.getVariant_id()));
        Set<Integer> indexed = new HashSet<>();
        for(int variantId:batch) {
            if (!indexed.add(variantId)) continue;
            List<VariantIndex> rows = rowsByVariantId.get(variantId);
            if (rows == null || rows.isEmpty()) continue;

            VariantIndex indexDoc = rows.get(0);
            indexDoc.setMapDataList(this.getMapData(indexDoc));
            if (mapKey == 38 || mapKey == 17) {
                mapClinicalSignificance(indexDoc);
            }

            Set<Long> transcriptIds = new LinkedHashSet<>();
            Set<String> analysisNames = new LinkedHashSet<>();
            for (VariantIndex variant : rows) {
                if (variant.getTranscriptRgdId() != null) {
                    transcriptIds.addAll(variant.getTranscriptRgdId());
                }
                if (variant.getAnalysisName() != null) {
                    analysisNames.addAll(variant.getAnalysisName());
                }
            }

            indexDoc.setTranscriptRgdId(new ArrayList<>(transcriptIds));
            indexDoc.setAnalysisName(new ArrayList<>(analysisNames));
            IndexDocument.index(indexDoc);

//        Set<Long> variantIdsWithoutTranscripts = new HashSet<>();
//        if (uniqueVariantIds.size() > variantIdsWithTrancripts.size()) {
//            for (int id : uniqueVariantIds) {
//                if (!variantIdsWithTrancripts.contains((long) id)) {
//                    variantIdsWithoutTranscripts.add((long) id);
//                }
//            }
//            if (variantIdsWithoutTranscripts.size() > 0) {
//                List<VariantIndex> variantsWithoutTranscripts = getVariantsWithoutTranscripts(mapKey, variantIdsWithoutTranscripts);
//               for(VariantIndex indexDoc:variantsWithoutTranscripts){
//                   IndexDocument.index(indexDoc);
//               }
//            }
//        }
        }
    }
    public void mapClinicalSignificance(VariantIndex variant){
        try {
            String clinvarSignificance = getClinvarInfo((int) variant.getVariant_id());
            if (clinvarSignificance != null && !clinvarSignificance.equals(""))
                variant.setClinicalSignificance(clinvarSignificance);
        } catch (Exception e) {
            System.out.println("NO CLINICAL SIGNIFICACE SAMPLE_ID:" + variant.getSampleId() + " RGD_ID:" + variant.getVariant_id());
        }
    }
}
