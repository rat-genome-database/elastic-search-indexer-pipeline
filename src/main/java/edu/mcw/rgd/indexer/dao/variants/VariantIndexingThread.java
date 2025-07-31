package edu.mcw.rgd.indexer.dao.variants;


import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;


import java.util.*;

public class VariantIndexingThread extends VariantDao implements Runnable {
    private final List<VariantIndex> indexList;
    private final int mapKey;
    public final long variantId;

    public VariantIndexingThread(List<VariantIndex> indexList, int mapKey, long variantId){
        this.indexList=indexList;
        this.mapKey=mapKey;

        this.variantId = variantId;
    }
    @Override
    public void run() {
        try {
            sortVariants();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void sortVariants() throws Exception {
        VariantIndex indexDoc= indexList.stream().filter(v -> v.getVariant_id() == variantId).toList().get(0);
        indexDoc.setMapDataList(this.getMapData(indexDoc));
        if (mapKey == 38 || mapKey == 17) {
            mapClinicalSignificance(indexDoc);
        }
        for (VariantIndex variant : indexList) {
                if(variantId==variant.getVariant_id()){
                    List<Long> transcriptIds = new ArrayList<>();
                    boolean exists = false;
                    if (indexDoc.getTranscriptRgdId() != null) {
                        transcriptIds.addAll(indexDoc.getTranscriptRgdId());
                    }
                    if (variant.getTranscriptRgdId() != null) {
                        for (long transcript : variant.getTranscriptRgdId()) {
                            for (long t : transcriptIds) {
                                if (transcript == t) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                transcriptIds.add(transcript);
                                indexDoc.setTranscriptRgdId(transcriptIds);
                            }

                        }
                    }
                    if (variant.getAnalysisName() != null) {
                        List<String> sampleNames=new ArrayList<>();
                        if(indexDoc.getAnalysisName()!=null)
                            sampleNames .addAll(indexDoc.getAnalysisName());
                        for (String name : variant.getAnalysisName()) {
                            for (String str : sampleNames) {
                                if (name.equals(str)) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                sampleNames.add(name);
                                indexDoc.setAnalysisName(sampleNames);
                            }
                        }
                    }
                  //  indexDoc.setSampleId(0);
              //  }


            }

            }
            IndexDocument.index(indexDoc);
      //  }

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
