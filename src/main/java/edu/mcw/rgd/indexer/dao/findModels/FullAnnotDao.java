package edu.mcw.rgd.indexer.dao.findModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.annotation.Evidence;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermDagEdge;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.model.RgdIndex;
import edu.mcw.rgd.indexer.model.findModels.ModelIndexObject;

import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.services.ClientInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.*;
import java.util.Map;

/**
 * Created by jthota on 3/3/2020.
 */
public class FullAnnotDao {
    private final Logger log = LogManager.getLogger("main");
    AnnotationDAO adao= new AnnotationDAO();
    OntologyXDAO xdao= new OntologyXDAO();
    StrainDAO strainDAO=new StrainDAO();
    AliasDAO aliasDAO=new AliasDAO();
    AssociationDAO associationDAO=new AssociationDAO();

    //EvidenceCode evidenceCode=new EvidenceCode();

    public List<ModelIndexObject> getAnnotationsBySpeciesNObjectKey(int speciesTypeKey, int objectKey) throws Exception {
        List<Annotation>  models= adao.getAnnotationsBySpecies(speciesTypeKey, objectKey);
        //    List<Annotation>  models= adao.getAnnotations(734760);
        System.out.println("MODELS SIZE: "+ models.size());
        List<ModelIndexObject> objects= new ArrayList<>();

        Map<Integer, List<ModelIndexObject>> indexedMap=new HashMap<>();
        for(Annotation m:models) {

            if (m.getAspect().equalsIgnoreCase("D") || m.getAspect().equalsIgnoreCase("N")) {
                List<Integer> refRgdIds = new ArrayList<>();
                List<Evidence> evidences = new ArrayList<>();
                List<ModelIndexObject> indexObjects = new ArrayList<>();
                ModelIndexObject object = new ModelIndexObject();

                ModelIndexObject loadedObject = this.getLoadedObject(m.getAnnotatedObjectRgdId(), m.getTermAcc(), m.getWithInfo(), m.getQualifier(), indexedMap);

                if (loadedObject != null) {
                    indexObjects = indexedMap.get(m.getAnnotatedObjectRgdId());
                    for (ModelIndexObject o : indexObjects) {
                        if (o.getAnnotatedObjectRgdId() == loadedObject.getAnnotatedObjectRgdId()
                                && o.getTermAcc().equalsIgnoreCase(loadedObject.getTermAcc())
                        ) {
                            if ((o.getWithInfo() != null && loadedObject.getWithInfo() != null && o.getWithInfo().equalsIgnoreCase(loadedObject.getWithInfo())
                                    || (o.getWithInfo() == null && loadedObject.getWithInfo() == null))
                            ) {
                                if ((o.getQualifiers() != null && loadedObject.getQualifiers() != null && o.getQualifiers().toString().equalsIgnoreCase(loadedObject.getQualifiers().toString()))
                                        || (o.getQualifiers() == null && loadedObject.getQualifiers() == null)) {


                                    refRgdIds = loadedObject.getRefRgdIds();
                                    if (!refRgdIds.contains(m.getRefRgdId())) {
                                        refRgdIds.add(m.getRefRgdId());

                                    }
                                    o.setRefRgdIds(refRgdIds);
                           /*  evidenceCodes = loadedObject.getEvidenceCodes();
                             if (!evidenceCodes.contains(m.getEvidence())) {
                                 evidenceCodes.add(m.getEvidence());
                             }
                             o.setEvidenceCodes(evidenceCodes);*/
                                    evidences = loadedObject.getEvidences();
                                    if (evidences.size() > 0) {
                                        boolean flag = false;
                                        for (Evidence e : evidences) {
                                            if (e.getEvidence().equalsIgnoreCase(m.getEvidence())) {
                                                flag = true;
                                            }
                                        }
                                        if (!flag) {
                                            Evidence evidence = new Evidence();
                                            evidence.setEvidence(m.getEvidence());
                                            evidence.setName(EvidenceCode.getName(m.getEvidence()));
                                            evidences.add(evidence);
                                            object.setEvidences(evidences);
                                        }
                                    }


                               /*  qualifiers = loadedObject.getQualifiers();
                                 if (!qualifiers.contains(m.getQualifier())) {
                                     if (m.getQualifier() != null)
                                         qualifiers.add(m.getQualifier().trim());
                                 }
                                 o.setQualifiers(qualifiers);*/
                                    indexedMap.put(m.getAnnotatedObjectRgdId(), indexObjects);
                                }
                            }
                        }
                    }

                } else {

                    indexObjects = indexedMap.get(m.getAnnotatedObjectRgdId());
                    object = new ModelIndexObject();
                    object.setAnnotatedObjectRgdId(m.getAnnotatedObjectRgdId());
                    object.setAnnotatedObjectName(m.getObjectName());
                    object.setAnnotatedObjectSymbol(m.getObjectSymbol());
                    String strainType = strainDAO.getStrain(m.getAnnotatedObjectRgdId()).getStrainTypeName();
                    object.setAnnotatedObjectType(strainType);
                    /************aliases*******************************/
                    List<Alias> aliases = aliasDAO.getAliases(m.getAnnotatedObjectRgdId());
                    List<String> aValues = new ArrayList<>();
                    for (Alias a : aliases) {
                        aValues.add(a.getValue().toLowerCase().trim());
                    }

                    object.setAliases(aValues);
                    /*****************associations************************/
                    object.setAssociations(this.getAssociations(m.getAnnotatedObjectRgdId()));

                    /*************************************************************/
                    object.setSpecies(this.getSpecies(m.getAnnotatedObjectRgdId()));
                    object.setAspect(m.getAspect());

                    if (m.getQualifier() != null)
                        object.setQualifiers(m.getQualifier().trim());
                    //    evidenceCodes.add(m.getEvidence());
                    //    object.setEvidenceCodes(evidenceCodes);

                    Evidence evidence = new Evidence();
                    evidence.setEvidence(m.getEvidence());
                    evidence.setName(EvidenceCode.getName(m.getEvidence()));
                    evidences.add(evidence);
                    object.setEvidences(evidences);

                    refRgdIds.add(m.getRefRgdId());
                    object.setRefRgdIds(refRgdIds);
                    object.setTerm(m.getTerm());
                    object.setTermAcc(m.getTermAcc());
                    object.setTermSynonyms(this.getSynonyms(m.getTermAcc()));
                    StringBuffer sb = new StringBuffer();
                    //     System.out.println(m.getAnnotatedObjectRgdId()+"\t"+ m.getWithInfo());
                    if (m.getWithInfo() != null) {

                        String str1 = m.getWithInfo();

                        if (m.getWithInfo().contains("|")) {
                            str1 = m.getWithInfo().replace("|", ",");
                        }
                        if (str1.contains(";")) {
                            str1 = str1.replace(";", ",");
                        }
                        List<Term> infoTerms = new ArrayList<>();
                        String[] tokens = str1.trim().split(",");
                        boolean first = true;
                        try {
                            for (String token : tokens) {
                                //   System.out.println(token);
                                if (!token.equals("")) {
                                    Term info = new Term();
                                    if (first) {
                                        sb.append(xdao.getTermByAccId(token.trim()).getTerm());
                                        first = false;
                                    } else {
                                        sb.append(" || ");
                                        sb.append(xdao.getTermByAccId(token.trim()).getTerm());
                                    }
                                    info.setAccId(token.trim());
                                    info.setTerm(xdao.getTermByAccId(token.trim()).getTerm());
                                    infoTerms.add(info);

                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Annotated Object RGD ID:" + m.getAnnotatedObjectRgdId() + "\tWITH INFO:" + m.getWithInfo());
                            // e.printStackTrace();
                        }
                        object.setInfoTerms(infoTerms);
                        object.setWithInfo(m.getWithInfo());
                        object.setWithInfoTerms(sb.toString());
                    }

                    List<Term> parentTerms = new ArrayList<>();
                    for (TermDagEdge e : getALLParentTerms(m.getTermAcc())) {
                        Term pt = new Term();
                        pt.setTerm(e.getParentTermName());
                        pt.setAccId(e.getParentTermAcc());
                        if (!isRepeated(parentTerms, e.getParentTermAcc()))
                            parentTerms.add(pt);
                    }
                    object.setParentTerms(parentTerms);
                    if (indexObjects == null)
                        indexObjects = new ArrayList<>();

                    indexObjects.add(object);
                    indexedMap.put(m.getAnnotatedObjectRgdId(), indexObjects);

                }

                //    }
            }
        }
        for (Map.Entry e : indexedMap.entrySet()) {
            List<ModelIndexObject> modelIndexObjects = (List<ModelIndexObject>) e.getValue();
            for(ModelIndexObject object:modelIndexObjects){
               addSuggestTerms(object);
            }
            objects.addAll(modelIndexObjects);
        }
        System.out.println("OBJECTS SIZE:"+ objects.size());
        return objects;


    }
    public void addSuggestTerms(ModelIndexObject object){
        Set<String> suggestTerms=new HashSet<>();
        try {
            suggestTerms.add(object.getAnnotatedObjectName());
        }catch (Exception e){}
        try {
        suggestTerms.add(object.getAnnotatedObjectSymbol());
        }catch (Exception e){}
            try {
        suggestTerms.add(object.getAnnotatedObjectType());
            }catch (Exception e){}
                try {
        suggestTerms.add(object.getTerm());
                }catch (Exception e){}
                    try {
        suggestTerms.add(object.getSpecies());
                    }catch (Exception e){}
                        try {
        suggestTerms.addAll(object.getAliases());
                        }catch (Exception e){}
                            try {
        suggestTerms.addAll(object.getTermSynonyms());
                            }catch (Exception e){}
                                try {
        suggestTerms.add(object.getQualifiers());
                                }catch (Exception e){}

                                try {
                                    for (Term term : object.getParentTerms()) {
                                        try {
                                            suggestTerms.add(term.getTerm());
                                        } catch (Exception e) {
                                        }
                                    }
                                }catch (Exception exception){}
      //  suggestTerms.addAll(object.getAssociations());
        Map<String, Set<String>> suggestions=new HashMap<>();
        if(suggestTerms.size()>0) {
            suggestions.put("input", suggestTerms);
            object.setSuggest(suggestions);
        }
    }
    public void indexModels(List<ModelIndexObject> objects) throws IOException {
//        BulkRequest bulkRequest=new BulkRequest();
//        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        int docCount=0;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);


        for (ModelIndexObject o : objects) {
            docCount++;


            byte[] json = new byte[0];
            try {
                json = mapper.writeValueAsBytes(o);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            IndexRequest request=new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON);
              ClientInit.getClient().index(request, RequestOptions.DEFAULT);

//            bulkRequest.add(new IndexRequest(RgdIndex.getNewAlias()).source(json, XContentType.JSON));
//
//            if(docCount%100==0){
//                BulkResponse response=      ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
//                bulkRequest= new BulkRequest();
//            }else{
//                if(docCount>objects.size()-100 && docCount==objects.size()){
//
//                    BulkResponse response=      ClientInit.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
//                    bulkRequest= new BulkRequest();
//                }
//            }

        }

        RefreshRequest refreshRequest=new RefreshRequest();
        ClientInit.getClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
        System.out.println("Indexed Model objects Size: " + objects.size() + " Exiting thread.");
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
    public ModelIndexObject getLoadedObject(int annotatedObjectRgdId,String termAcc, String withInfo, String qualifier, Map<Integer, List<ModelIndexObject>> indexedMap  ){
        for(Map.Entry e: indexedMap.entrySet()){
            int key= (int) e.getKey();
            if(key==annotatedObjectRgdId) {
                List<ModelIndexObject> annots = (List<ModelIndexObject>) e.getValue();
                for (ModelIndexObject a : annots) {
                    if (a.getAnnotatedObjectRgdId() == annotatedObjectRgdId
                            && a.getTermAcc().equalsIgnoreCase(termAcc)
                    ) {
                        if (((a.getWithInfo() != null && withInfo!=null && a.getWithInfo().equalsIgnoreCase(withInfo))
                                || (a.getWithInfo() == null && withInfo == null))){
                            if((a.getQualifiers()!=null && qualifier!=null && a.getQualifiers().toString().equalsIgnoreCase(qualifier.trim()))
                                    || (a.getQualifiers()==null && qualifier==null)){
                                return a;
                            }
                        }


                    }
                }
            }
        }
        return null;
    }
    public List<String> getAssociations(int annotatedObjectRgdId) throws Exception {
        List<String> assocs= new ArrayList<>();
        List associations=associationDAO.getStrainAssociations(annotatedObjectRgdId);
        for(Object a:associations){
            if(a instanceof Gene){
                assocs.add(((Gene) a).getSymbol());
            } else
            if(a instanceof Strain){

                assocs.add(((Strain) a).getSymbol());
            }else
            if(a instanceof SSLP){

                assocs.add(((SSLP) a).getName());
            }else{

                assocs.add(a.toString());
            }
        }
        return assocs;
    }
    public List<String> getSynonyms(String termAcc) throws Exception {

        List<TermSynonym> termSynonyms= xdao.getTermSynonyms(termAcc);
        List<String> synonyms=new ArrayList<>();
        //   System.out.println("TERM AC:"+ termAcc+"\tSynonyms SIZE:"+ termSynonyms.size());
        for(TermSynonym s: termSynonyms){
            synonyms.add(s.getName());
            //  System.out.println(s.getName());
        }
        return synonyms;
    }
}