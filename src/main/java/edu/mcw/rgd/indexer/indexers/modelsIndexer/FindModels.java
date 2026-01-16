package edu.mcw.rgd.indexer.indexers.modelsIndexer;

import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.annotation.Evidence;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermDagEdge;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.dao.findModels.FullAnnotDao;
import edu.mcw.rgd.indexer.model.IndexDocument;
import edu.mcw.rgd.indexer.model.findModels.ModelIndexObject;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

public class FindModels extends FullAnnotDao {

    public void getModelIndexObjects(int speciesTypeKey, int objectKey) throws Exception {

        List<Annotation> models = adao.getDiseaseAndStrainAnnotationsBySpecies(speciesTypeKey, objectKey);
        Map<Integer, List<Annotation>> rgdIdNAnnotationsMap=models.stream().collect(Collectors.groupingBy(Annotation::getAnnotatedObjectRgdId));
        for(Map.Entry e:rgdIdNAnnotationsMap.entrySet()){
            int annotatedRgdId= (int) e.getKey();
            List<Annotation> groupedAnnotationsOfRgdId= (List<Annotation>) e.getValue();
            Map<String, List<Annotation>> termAnnotationsMap= groupedAnnotationsOfRgdId.stream().collect(Collectors.groupingBy(Annotation::getTermAcc));
            for(Map.Entry entry:termAnnotationsMap.entrySet()){
                List<Annotation> termAnnotations= (List<Annotation>) entry.getValue();

                    if(duplicates(termAnnotations)){
                        ModelIndexObject indexObject= createIndexObject(termAnnotations.get(0));;
                        for(Annotation annotation:termAnnotations){
                            addEvidence(annotation, indexObject);
                            addReference(annotation,indexObject);

                        }
                        IndexDocument.index(indexObject);
                    }else{
                        for(Annotation annotation:termAnnotations){
                            ModelIndexObject indexObject=createIndexObject(annotation);
                            IndexDocument.index(indexObject);
                        }
                    }

            }

        }
    }
    public ModelIndexObject createIndexObject(Annotation annotation) throws Exception {
        ModelIndexObject modelIndexObject=new ModelIndexObject();
        mapObject(modelIndexObject, annotation);
        addSuggestTerms(modelIndexObject);
        return modelIndexObject;
    }
    public boolean duplicates(List<Annotation> annotations){
        Set<String> withInfoField=annotations.stream().filter(annotation -> annotation.getWithInfo()!=null).map(annotation -> annotation.getWithInfo().toLowerCase().trim()).collect(Collectors.toSet());
        Set<String> qualifier=annotations.stream().filter(annotation -> annotation.getQualifier()!=null).map(annotation -> annotation.getQualifier().toLowerCase().trim()).collect(Collectors.toSet());
        return withInfoField.size() == 1 || qualifier.size() == 1;
    }

    public void addEvidence(Annotation annotation,ModelIndexObject modelIndexObject) throws Exception {
       List<Evidence> evidences =new ArrayList<>();
        boolean flag = false;
        if ( modelIndexObject.getEvidences()!=null &&  modelIndexObject.getEvidences().size() > 0) {
            for (Evidence e :  modelIndexObject.getEvidences()) {
                if (e.getEvidence().equalsIgnoreCase(annotation.getEvidence())) {
                    flag = true;
                }
            }
        }
            if (!flag) {
                Evidence evidence = new Evidence();
                evidence.setEvidence(annotation.getEvidence());
                evidence.setName(EvidenceCode.getName(annotation.getEvidence()));
                evidences.add(evidence);
                modelIndexObject.setEvidences(evidences);
            }


    }
    public void addReference(Annotation annotation,ModelIndexObject modelIndexObject){

      List<Integer>  refRgdIds = modelIndexObject.getRefRgdIds();
        if (refRgdIds!=null && !refRgdIds.contains(annotation.getRefRgdId())) {
            refRgdIds.add(annotation.getRefRgdId());

        }
        modelIndexObject.setRefRgdIds(refRgdIds);
    }

    public void mapObject(  ModelIndexObject object, Annotation m ) throws Exception {

        object.setAnnotatedObjectRgdId(m.getAnnotatedObjectRgdId());
        object.setAnnotatedObjectName(m.getObjectName());
        object.setAnnotatedObjectSymbol(m.getObjectSymbol());
        object.setAspect(m.getAspect());
        object.setTerm(m.getTerm());
        object.setTermAcc(m.getTermAcc());
        if (m.getQualifier() != null)
            object.setQualifiers(m.getQualifier().trim());

        mapAnnotatedObjectType(m, object);
        mapAliases(m, object);
        mapAssociations(m,object);
        mapSpecies(m, object);

        addEvidence(m,object);
        addReference(m,object);
        addSynonyms(m, object);
        mapInfoFields(m, object);
       mapParentTerms(m, object);



    }
    public void mapParentTerms(Annotation annotation, ModelIndexObject object) throws Exception {
        List<Term> parentTerms = new ArrayList<>();
        for (TermDagEdge e : getALLParentTerms(annotation.getTermAcc())) {
            Term pt = new Term();
            pt.setTerm(e.getParentTermName());
            pt.setAccId(e.getParentTermAcc());
            if (!isRepeated(parentTerms, e.getParentTermAcc()))
                parentTerms.add(pt);
        }
        object.setParentTerms(parentTerms);
    }
    public void mapAliases(Annotation annotation, ModelIndexObject indexObject) throws Exception {
        List<Alias> aliases = aliasDAO.getAliases(annotation.getAnnotatedObjectRgdId());
        List<String> aValues = new ArrayList<>();
        for (Alias a : aliases) {
            aValues.add(a.getValue().toLowerCase().trim());
        }

        indexObject.setAliases(aValues);
    }
    public void mapInfoFields(Annotation annotation, ModelIndexObject object){
        StringBuffer sb = new StringBuffer();
        //     System.out.println(m.getAnnotatedObjectRgdId()+"\t"+ m.getWithInfo());
        if (annotation.getWithInfo() != null) {

            String str1 = annotation.getWithInfo();

            if (annotation.getWithInfo().contains("|")) {
                str1 = annotation.getWithInfo().replace("|", ",");
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
                System.out.println("Annotated Object RGD ID:" + annotation.getAnnotatedObjectRgdId() + "\tWITH INFO:" + annotation.getWithInfo());
                // e.printStackTrace();
            }
            object.setInfoTerms(infoTerms);
            object.setWithInfo(annotation.getWithInfo());
            object.setWithInfoTerms(sb.toString());
        }
    }
    public void mapAnnotatedObjectType(Annotation m, ModelIndexObject object) throws Exception {
        String strainType = strainDAO.getStrain(m.getAnnotatedObjectRgdId()).getStrainTypeName();
        object.setAnnotatedObjectType(strainType);
    }

    public void addSuggestTerms(ModelIndexObject object){
        Set<String> suggestTerms=new HashSet<>();
        try {
            if(object.getAnnotatedObjectName()!=null)
                suggestTerms.add(object.getAnnotatedObjectName());
        }catch (Exception e){
            System.out.println("OBJECT ID:"+ object.getTermAcc());
            e.printStackTrace();

        }
        try {
            if(object.getAnnotatedObjectSymbol()!=null)
                suggestTerms.add(object.getAnnotatedObjectSymbol());
        }catch (Exception e){
            System.out.println("OBJECT ID:"+ object.getTermAcc());
            e.printStackTrace();
        }

        try {
            if(object.getTerm()!=null)
                suggestTerms.add(object.getTerm());
        }catch (Exception e){
            System.out.println("OBJECT ID:"+ object.getTermAcc());
            e.printStackTrace();
        }
        try {
            if(object.getSpecies()!=null)
                suggestTerms.add(object.getSpecies());
        }catch (Exception e){    System.out.println("OBJECT ID:"+ object.getTermAcc());
            e.printStackTrace(); }

        try {
            for (Term term : object.getParentTerms()) {
                try {
                    if(term.getTerm()!=null)
                        suggestTerms.add(term.getTerm());
                } catch (Exception e) {    System.out.println("OBJECT ID:"+ object.getTermAcc());
                    e.printStackTrace();
                }
            }
        }catch (Exception e){    System.out.println("OBJECT ID:"+ object.getTermAcc());
            e.printStackTrace(); }
        //  suggestTerms.addAll(object.getAssociations());
        Map<String, Set<String>> suggestions=new HashMap<>();
        if(suggestTerms.size()>0) {
            suggestions.put("input", suggestTerms);
            object.setSuggest(suggestions);
        }
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

    public void mapSpecies(Annotation annotation, ModelIndexObject object) throws Exception {
        RGDManagementDAO mdao= new RGDManagementDAO();
        Object obj= mdao.getObject(annotation.getAnnotatedObjectRgdId());
        if(obj instanceof Strain){
            int speciesTypeKey=((Strain) obj).getSpeciesTypeKey();
            object.setSpecies(SpeciesType.getCommonName(speciesTypeKey));
        }
        object.setSpecies( "All");
    }

    public void mapAssociations(Annotation annotation, ModelIndexObject object) throws Exception {
        List<String> assocs= new ArrayList<>();
        List associations=associationDAO.getStrainAssociations(annotation.getAnnotatedObjectRgdId());
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
        object.setAliases(assocs);
    }
    public void addSynonyms(Annotation annotation, ModelIndexObject object) throws Exception {

        List<TermSynonym> termSynonyms= xdao.getTermSynonyms(annotation.getTermAcc());
        List<String> synonyms=new ArrayList<>();
        //   System.out.println("TERM AC:"+ termAcc+"\tSynonyms SIZE:"+ termSynonyms.size());
        for(TermSynonym s: termSynonyms){
            synonyms.add(s.getName());
            //  System.out.println(s.getName());
        }
        object.setTermSynonyms(synonyms);
    }
}
