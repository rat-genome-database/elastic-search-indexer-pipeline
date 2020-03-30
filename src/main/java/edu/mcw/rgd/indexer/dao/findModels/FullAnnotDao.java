package edu.mcw.rgd.indexer.dao.findModels;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.AnnotationDAO;
import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.RGDManagementDAO;
import edu.mcw.rgd.datamodel.Reference;
import edu.mcw.rgd.datamodel.RgdId;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermDagEdge;
import edu.mcw.rgd.indexer.client.ESClient;
import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.findModels.ModelIndexObject;

import edu.mcw.rgd.process.Utils;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.*;

/**
 * Created by jthota on 3/3/2020.
 */
public class FullAnnotDao {
    AnnotationDAO adao= new AnnotationDAO();
    OntologyXDAO xdao= new OntologyXDAO();
    public List<ModelIndexObject> getAnnotationsBySpeciesNObjectKey(int speciesTypeKey, int objectKey) throws Exception {
     List<Annotation>  models= adao.getAnnotationsBySpecies(speciesTypeKey, objectKey);
   //List<Annotation>  models= adao.getAnnotations(60997);
        System.out.println("MODELS SIZE: "+ models.size());
        List<ModelIndexObject> objects= new ArrayList<>();


        Map<Integer, List<ModelIndexObject>> indexedMap=new HashMap<>();
      for(Annotation m:models){

            if(m.getAspect().equalsIgnoreCase("D") || m.getAspect().equalsIgnoreCase("N")) {
                List<Integer> refRgdIds=new ArrayList<>();
                List<String> evidenceCodes=new ArrayList<>();
                List<String> qualifiers= new ArrayList<>();
                List<ModelIndexObject> indexObjects=new ArrayList<>();
                ModelIndexObject object = new ModelIndexObject();
               ModelIndexObject loadedObject=this.getLoadedObject(m.getAnnotatedObjectRgdId(), m.getTermAcc(),m.getWithInfo(), indexedMap);

             if(loadedObject!=null) {
                 indexObjects= indexedMap.get(m.getAnnotatedObjectRgdId());
                 for(ModelIndexObject o:indexObjects){
                     if(o.getAnnotatedObjectRgdId()==loadedObject.getAnnotatedObjectRgdId()
                             && o.getTermAcc().equalsIgnoreCase(loadedObject.getTermAcc())
                             ){
                        if((o.getWithInfo() !=null && loadedObject.getWithInfo() !=null && o.getWithInfo().equalsIgnoreCase(loadedObject.getWithInfo())
                          || (o.getWithInfo() ==null && loadedObject.getWithInfo() ==null)
                        || ( o.getQualifiers().toString() .equalsIgnoreCase(loadedObject.getQualifiers().toString())))) {
                             refRgdIds = loadedObject.getRefRgdIds();
                             if (!refRgdIds.contains(m.getRefRgdId())) {
                                 refRgdIds.add(m.getRefRgdId());

                             }
                             o.setRefRgdIds(refRgdIds);
                             evidenceCodes = loadedObject.getEvidenceCodes();
                             if (!evidenceCodes.contains(m.getEvidence())) {
                                 evidenceCodes.add(m.getEvidence());
                             }
                             o.setEvidenceCodes(evidenceCodes);
                             qualifiers = loadedObject.getQualifiers();
                             if (!qualifiers.contains(m.getQualifier())) {
                                 qualifiers.add(m.getQualifier());
                             }
                             o.setQualifiers(qualifiers);
                             indexedMap.put(m.getAnnotatedObjectRgdId(), indexObjects);
                         }
                     }
                 }


             }
              else{

                indexObjects= indexedMap.get(m.getAnnotatedObjectRgdId());
                 object=new ModelIndexObject();
                    object.setAnnotatedObjectRgdId(m.getAnnotatedObjectRgdId());
                    object.setAnnotatedObjectName(m.getObjectName());
                    object.setAnnotatedObjectSymbol(m.getObjectSymbol());
                    object.setSpecies(this.getSpecies(m.getAnnotatedObjectRgdId()));
                    object.setAspect(m.getAspect());
                    qualifiers.add(m.getQualifier());
                    object.setQualifiers(qualifiers);
                    evidenceCodes.add(m.getEvidence());
                    object.setEvidenceCodes(evidenceCodes);
                    refRgdIds.add(m.getRefRgdId());
                    object.setRefRgdIds(refRgdIds);
                    object.setTerm(m.getTerm());
                    object.setTermAcc(m.getTermAcc());

                 StringBuffer sb= new StringBuffer();
            //     System.out.println(m.getAnnotatedObjectRgdId()+"\t"+ m.getWithInfo());
                 if(m.getWithInfo()!=null){

                     String str1=m.getWithInfo();

                     if(m.getWithInfo().contains("|")) {
                         str1 = m.getWithInfo().replace("|", ",");
                     }
                     if(str1.contains(";")){
                        str1= str1.replace(";", ",");
                     }

                     String[] tokens= str1.trim().split(",");
                     try {
                         for (String token : tokens) {
                             //   System.out.println(token);
                             if (!token.equals("")) {
                                 sb.append(xdao.getTermByAccId(token.trim()).getTerm());
                                 sb.append(" || ");
                             }
                         }
                     }catch (Exception e){
                         System.out.println("Annotated Object RGD ID:"+m.getAnnotatedObjectRgdId() +"\tWITH INFO:"+ m.getWithInfo());
                        // e.printStackTrace();
                     }
                 }
                 object.setWithInfo(m.getWithInfo());
                 object.setWithInfoTerms(sb.toString());
                    List<Term> parentTerms = new ArrayList<>();
                    for (TermDagEdge e : getALLParentTerms(m.getTermAcc())) {
                        Term pt = new Term();
                        pt.setTerm(e.getParentTermName());
                        pt.setAccId(e.getParentTermAcc());
                        if (!isRepeated(parentTerms, e.getParentTermAcc()))
                            parentTerms.add(pt);
                    }
                    object.setParentTerms(parentTerms);
                 if(indexObjects==null)
                        indexObjects=new ArrayList<>();

                    indexObjects.add(object);
                    indexedMap.put(m.getAnnotatedObjectRgdId(),indexObjects );

                }

            }
      }
        for(Map.Entry e: indexedMap.entrySet()){
            List<ModelIndexObject> modelIndexObjects= (List<ModelIndexObject>) e.getValue();
            objects.addAll(modelIndexObjects);
        }
        return objects;


    }
    public void indexModels(List<ModelIndexObject> objects) throws IOException {
        BulkRequest bulkRequest=new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        int docCount=0;
        for (ModelIndexObject o : objects) {
            docCount++;
            ObjectMapper mapper = new ObjectMapper();
            byte[] json = new byte[0];
            try {
                json = mapper.writeValueAsBytes(o);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            bulkRequest.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));

            if(docCount%100==0){
                //  BulkResponse response=       bulkRequestBuilder.execute().get();
                BulkResponse response=      ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                bulkRequest= new BulkRequest();
            }else{
                if(docCount>objects.size()-100 && docCount==objects.size()){

                    BulkResponse response=      ESClient.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    bulkRequest= new BulkRequest();
                }
            }

        }
        //   BulkResponse response=       bulkRequestBuilder.get();
        //  ESClient.getClient().admin().indices().refresh(refreshRequest()).actionGet();
        RefreshRequest refreshRequest=new RefreshRequest();
        ESClient.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
        System.out.println("Indexed " +  "  Model objects Size: " + objects.size() + " Exiting thread.");
        System.out.println(Thread.currentThread().getName() + ": " + " End " + new Date());


    }
    public boolean isRepeated(List<Term> terms, String termAcc){
        for(Term t:terms){
            if(t.getAccId().equalsIgnoreCase(termAcc)){
                return true;
            }
        }
        return false;
    }
    public List<TermDagEdge> getALLParentTerms(String childTerm) throws Exception {
        return xdao.getAllParentEdges(childTerm);
    }
    public String getSpecies(int rgdId) throws Exception {
        RGDManagementDAO mdao= new RGDManagementDAO();
        Object obj= mdao.getObject(rgdId);
        if(obj instanceof Strain ){

            int speciesTypeKey=((Strain) obj).getSpeciesTypeKey();
            return  SpeciesType.getCommonName(speciesTypeKey);
        }
        return "All";
    }
    public List<Integer> getReferences(int rgdId, int objectKey) throws Exception {
        AssociationDAO associationDAO = new AssociationDAO();

        List<Reference> refs = associationDAO.getReferenceAssociations(rgdId);
        if (refs.size() > 0 ) {
            // sort references by citation
            Collections.sort(refs, new Comparator<Reference>() {
                public int compare(Reference o1, Reference o2) {
                    return Utils.stringsCompareToIgnoreCase(o1.getCitation(), o2.getCitation());
                }
            });
    }
        List<Integer> refRgdIds= new ArrayList<>();
        for(Reference r:refs){
            refRgdIds.add(r.getRgdId());
        }
        return refRgdIds;
    }
    public boolean alreadyLoaded(int annotatedObjectRgdId,String termAcc, String withInfo, Map<Integer, List<ModelIndexObject>> indexedMap  ){
        for(Map.Entry e: indexedMap.entrySet()){
            int key= (int) e.getKey();
            if(key==annotatedObjectRgdId) {
                List<ModelIndexObject> annots = (List<ModelIndexObject>) e.getValue();
                for (ModelIndexObject a : annots) {
                    if (a.getAnnotatedObjectRgdId() == annotatedObjectRgdId
                            && a.getTermAcc().equalsIgnoreCase(termAcc)
                            ) {
                        if ((a.getWithInfo() != null && withInfo != null && a.getWithInfo().equalsIgnoreCase(withInfo))
                                || (a.getWithInfo() == null && withInfo == null))
                            return true;

                    }
                }
            }
        }
        return false;
    }
    public ModelIndexObject getLoadedObject(int annotatedObjectRgdId,String termAcc, String withInfo, Map<Integer, List<ModelIndexObject>> indexedMap  ){
        for(Map.Entry e: indexedMap.entrySet()){
            int key= (int) e.getKey();
            if(key==annotatedObjectRgdId) {
                List<ModelIndexObject> annots = (List<ModelIndexObject>) e.getValue();
                for (ModelIndexObject a : annots) {
                    if (a.getAnnotatedObjectRgdId() == annotatedObjectRgdId
                            && a.getTermAcc().equalsIgnoreCase(termAcc)
                            ) {
                        if ((a.getWithInfo() != null && withInfo != null && a.getWithInfo().equalsIgnoreCase(withInfo))
                                || (a.getWithInfo() == null && withInfo == null))
                            return a;

                    }
                }
            }
        }
        return null;
    }
}
