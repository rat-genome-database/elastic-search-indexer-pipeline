package edu.mcw.rgd.indexer.objectSearchIndexer;

import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.pheno.Study;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ExpressionStudyDetails;
import edu.mcw.rgd.indexer.objectSearchIndexer.objectDetails.ObjectDetails;

import java.util.List;

public class IndexExpressionStudy implements Runnable{

    private Study study;
    private List<GeneExpression> records;
    GeneExpressionDAO expressionDAO=new GeneExpressionDAO();
    public IndexExpressionStudy(Study study){
        this.study=study;
        setRecords();
    }

    @Override
    public void run() {
        IndexObject object=new IndexObject();
        ObjectDetails<Study> studyObjectDetails=new ExpressionStudyDetails(study,object);
        studyObjectDetails.index();
    }
    public void setRecords(){
//        try {
//            this.records= expressionDAO.getGeneExpressionObjectsByStudyId(study.getId())
//                    .stream().filter(r->
//                         ( r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("high") ||
//                                r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("low"))).collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
