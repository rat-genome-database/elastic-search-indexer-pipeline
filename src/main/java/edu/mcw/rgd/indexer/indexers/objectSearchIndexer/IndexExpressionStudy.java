package edu.mcw.rgd.indexer.indexers.objectSearchIndexer;

import edu.mcw.rgd.dao.impl.GeneExpressionDAO;
import edu.mcw.rgd.datamodel.GeneExpression;
import edu.mcw.rgd.datamodel.pheno.Study;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ExpressionStudyDetails;
import edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails.ObjectDetails;

import java.util.Date;
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
        System.out.println(Thread.currentThread().getName() +  " STARTED " + new Date() + "records size:"+ records.size());
        if(records.size()>0) {
            IndexObject object = new IndexObject();
            object.setCategory("Expression Study");
            ObjectDetails<Study> studyObjectDetails = new ExpressionStudyDetails(study, object, records);
            studyObjectDetails.index();
        }
    }
    public void setRecords(){
        System.out.println("Getting  study records ...");
        try {
            this.records= expressionDAO.getGeneExpressionByStudyId(study.getId(), "TPM");
            System.out.println("RECORDS SIZE:"+records.size());
//                    .stream().filter(r->
//                         ( r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("high") ||
//                                r.getGeneExpressionRecordValue().getExpressionLevel().equalsIgnoreCase("low"))).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
