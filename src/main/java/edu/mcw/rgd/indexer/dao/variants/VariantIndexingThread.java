package edu.mcw.rgd.indexer.dao.variants;


import edu.mcw.rgd.datamodel.variants.SampleManager;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;


import java.util.*;

public class VariantIndexingThread extends VariantDao implements Runnable {
    private final List<VariantIndex> indexList;
    private final int mapKey;
    private final List<Integer> uniqueVariantIds;
    public VariantIndexingThread(List<VariantIndex> indexList, int mapKey, List<Integer> uniqueVariantIds){
        this.indexList=indexList;
        this.mapKey=mapKey;
        this.uniqueVariantIds=uniqueVariantIds;
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
        Set<Long> variantIdsWithTrancripts = new HashSet<>();
        for (int variantId : uniqueVariantIds) {
            boolean first=true;
            VariantIndex indexDoc=null;
            for (VariantIndex variant : indexList) {
                if(variantId==variant.getVariant_id()){
                variantIdsWithTrancripts.add(variant.getVariant_id());
                if (mapKey == 38 || mapKey == 17) {
                    try {
                        String clinvarSignificance = getClinvarInfo((int) variant.getVariant_id());
                        if (clinvarSignificance != null && !clinvarSignificance.equals(""))
                            variant.setClinicalSignificance(clinvarSignificance);
                    } catch (Exception e) {
                        System.out.println("NO CLINICAL SIGNIFICACE SAMPLE_ID:" + variant.getSampleId() + " RGD_ID:" + variant.getVariant_id());
                    }
                }
                if (first) {
                    first=false;
                    indexDoc=variant;
                    indexDoc.setAnalysisName(Arrays.asList(SampleManager.getInstance().getSampleName(variant.getSampleId()).getAnalysisName()));
                    try {
                        indexDoc.setMapDataList(this.getMapData(indexDoc));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
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
                }


            }

            }
            if(indexDoc!=null)
            IndexDocument.index(indexDoc);
        }

        Set<Long> variantIdsWithoutTranscripts = new HashSet<>();
        if (uniqueVariantIds.size() > variantIdsWithTrancripts.size()) {
            for (int id : uniqueVariantIds) {
                if (!variantIdsWithTrancripts.contains((long) id)) {
                    variantIdsWithoutTranscripts.add((long) id);
                }
            }
            if (variantIdsWithoutTranscripts.size() > 0) {
                List<VariantIndex> variantsWithoutTranscripts = getVariantsWithoutTranscripts(mapKey, variantIdsWithoutTranscripts);
               for(VariantIndex indexDoc:variantsWithoutTranscripts){
                   IndexDocument.index(indexDoc);
               }
            }
        }
    }
}
