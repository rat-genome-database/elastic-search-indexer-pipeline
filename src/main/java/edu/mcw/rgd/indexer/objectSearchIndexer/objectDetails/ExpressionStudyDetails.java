package edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails;

import com.google.gson.Gson;
import edu.mcw.rgd.datamodel.pheno.Study;
import edu.mcw.rgd.indexer.model.IndexObject;

public class ExpressionStudyDetails extends ObjectDetails<Study> {
   public ExpressionStudyDetails(Study study, IndexObject object) {
        super(study, object);
    }

    @Override
    public int getRgdId() {
        return 0;
    }

    @Override
    public int getSpeciesTypeKey() {
        return 0;
    }

    @Override
    public void mapObject() {

    }

    @Override
    public void mapAnnotations() {

    }

    @Override
    public void mapAssociations() {

    }

//        public void mapGene(){
//        object.setTerm_acc(String.valueOf(gene.getRgdId()));
//        object.setSymbol(gene.getSymbol());
//        object.setType(gene.getType());
//        object.setName(gene.getName());
//        try {
//            object.setMapDataList(indexDAO.getMapData(gene.getRgdId()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        List<AliasData> aliases = null;
//        try {
//            aliases = indexDAO.getAliases(gene.getRgdId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        List<String> synonyms = aliases!=null && aliases.size()>0?aliases.stream().map(a->a.getAlias_name()).collect(Collectors.toList()) : null;
//        List<String> oldSymbols=aliases!=null && aliases.size()>0? aliases.stream()
//                .filter(a -> a.getAlias_type().equalsIgnoreCase("old_gene_symbol"))
//                .map(AliasData::getAlias_name).toList() : null;
//        List<String> oldNames=aliases!=null && aliases.size()>0? aliases.stream()
//                .filter(a -> a.getAlias_type().equalsIgnoreCase("old_gene_name"))
//                .map(AliasData::getAlias_name).toList() : null;
//        object.setSynonyms(synonyms);
//        object.setOldSymbols(oldSymbols);
//        object.setOldNames(oldNames);
//        //    obj.setSynonyms(getAliasesByRgdId(aliases, rgdId));
//        try {
//            object.setXdbIdentifiers(indexDAO.getExternalIdentifiers(gene.getRgdId()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    void setExpressionRecords()  {
//        try {
//            this.records= expressionDAO.getGeneExpressionObjectsByRgdIdUnit(gene.getRgdId(), "TPM")
//                    .stream().filter(r->
//                         ( r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("high") ||
//                                r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("low"))).collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void index()  {
//        if(isSearchable()) {
//            setMap();
//            // setBioSampleId();
////        setGeoSampleAcc();
//            setExpressionUnit();
//            setExpressionLevel();
//            setSex();
//            setLifeStage();
////        setClinicalMeasurement();
////        setTissueAcc();
////        setTissueTerms();
////        setStrainAcc();
////        setStrainTerms();
////
////        setCellTypeAcc();
////        setCellTypeTerms();
//            setExperimentId();
////        setGEOSeriesAcc();
//            //     setSampleId();
//            /**** TODO
//             setExperimentId();
//             setSampleId();
//             setVTAcc();
//             setStudySource();
//
//             setRGDReference();
//             ****/
//            setClinicalMeasurementTerms();
//        }
//    }
//    void setGeoSampleAcc(){
//       object.setGeoSampleAcc( records.stream().map(r->r.getSample().getGeoSampleAcc()).filter(Objects::nonNull).collect(Collectors.toSet()));
//    }
//    void setBioSampleId(){
//        object.setBioSampleId( records.stream().map(r->r.getSample().getBioSampleId()).filter(Objects::nonNull).collect(Collectors.toSet()));
//    }
//    void setLifeStage(){
//        object.setLifeStage( records.stream().map(r->r.getSample().getLifeStage()).filter(Objects::nonNull).collect(Collectors.toSet()));
//    }
//    void setSex(){
//        object.setSex( records.stream().map(r->r.getSample().getSex()).filter(Objects::nonNull).collect(Collectors.toSet()));
//    }
//    void setStrainAcc(){
//        object.setStrainAccId( records.stream().map(r->r.getSample().getStrainAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));
//
//    }
//    void setStrainTerms() throws Exception {
//        if(object.getStrainAccId().size()>0) {
//            String[] arrayIds = object.getStrainAccId().toArray(new String[0]);
//            List<Term> terms = xdao.getTermByAccId(arrayIds);
//            if (terms!=null && terms.size() > 0)
//                object.setStrainTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
//        }
//    }
//    void setCellTypeAcc(){
//        object.setCellTypeAccId( records.stream().map(r->r.getSample().getCellTypeAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));
//
//    }
//    void setCellTypeTerms() throws Exception {
//        if(object.getCellTypeAccId().size()>0) {
//            String[] arrayIds = object.getCellTypeAccId().toArray(new String[0]);
//            List<Term> terms = xdao.getTermByAccId(arrayIds);
//            if (terms!=null && terms.size() > 0)
//                object.setCellTypeTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
//        }
//    }
//    void setTissueAcc(){
//        object.setTissueAccId( records.stream().map(r->r.getSample().getTissueAccId()).filter(Objects::nonNull).collect(Collectors.toSet()));
//
//    }
//    void setTissueTerms() throws Exception {
//        if(object.getTissueAccId().size()>0) {
//            String[] arrayIds = object.getTissueAccId().toArray(new String[0]);
//            List<Term> terms = xdao.getTermByAccId(arrayIds);
//            if (terms!=null && terms.size() > 0)
//                object.setTissueTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
//        }
//    }
//    void setExpressionLevel(){
//        object.setExpressionLevel( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionLevel()).filter(Objects::nonNull).collect(Collectors.toSet()));
//
//    }
//    void setExpressionUnit(){
//        object.setExpressionUnit( records.stream().map(r->r.getGeneExpressionRecordValue().getExpressionUnit()).filter(Objects::nonNull).collect(Collectors.toSet()));
//
//    }
//    void setExperimentId(){
//        object.setExperimentId( records.stream().map(r->r.getGeneExpressionRecord().getExperimentId()).collect(Collectors.toSet()));
//
//    }
////    void setGEOSeriesAcc(){
////        object.setGeoSeriesAcc( records.stream().map(r->r.getGeoSeriesAcc()).collect(Collectors.toSet()));
////
////    }
////    void setSampleId(){
////        object.setSampleId( records.stream().map(r->r.getGeneExpressionRecord().getSampleId()).collect(Collectors.toSet()));
////
////    }
////    void setSample(){
////        Set<Integer> sampleIds=object.getSampleId();
////        if(sampleIds!=null && sampleIds.size()>0){
////          List<Sample> samples=  sampleDAO.getSampleBySampleId(new ArrayList<>(sampleIds));
////          if(samples!=null && samples.size()>0){
////              object.setSample( samples.stream().map(s->s.get));
////          }
////        }
////
////
////    }
//    void setMap(){
//        object.setMapKey( records.stream().map(r->r.getGeneExpressionRecordValue().getMapKey()).filter(obj -> true).collect(Collectors.toSet()));
//
//    }
//    void setClinicalMeasurement(){
////        System.out.println("CMO IDS:"+records.stream().map(r->r.getGeneExpressionRecord().getClinicalMeasurementId()).collect(Collectors.toSet()));
//        object.setClinicalMeasurementId( records.stream().filter(r->r.getGeneExpressionRecord().getClinicalMeasurementId()>0).map(r->String.valueOf(r.getGeneExpressionRecord().getClinicalMeasurementId())).collect(Collectors.toSet()));
//
//    }
//    void setClinicalMeasurementTerms() throws Exception {
////        if(object.getClinicalMeasurementId().size()>0) {
////            String[] arrayIds = object.getClinicalMeasurementId().toArray(new String[0]);
////            List<Term> terms = xdao.getTermByAccId(arrayIds);
////            if (terms.size() > 0)
////                object.setClinicalMeasurementTerms(terms.stream().map(Term::getTerm).collect(Collectors.toSet()));
////        }
//    }
//    void print(){
//        Gson gson=new Gson();
////        for(GeneExpression ge:records){
////            System.out.println(gson.toJson(ge));
////        }
//        System.out.println(gson.toJson(object));
//    }

}
