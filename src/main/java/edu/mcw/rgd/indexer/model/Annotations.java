package edu.mcw.rgd.indexer.model;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.dao.IndexDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Annotations<T>  extends RGDObject<T> {
    List<String> diseaseAnnotations;
    List<String> goAnnotations;
    List<String> geneChemAnnotations;
    List<String> pathwayAnnotations;
    List<String> phenotypeAnnotations;
    List<String> experimentalAnnotations;
    IndexDAO indexDAO=new IndexDAO();
   public Annotations(T obj) throws Exception {
       super(obj);
       sortAnnotationsByType();
   }
    public T getObject(){return obj;}


    public List<String> getDiseaseAnnotations() {
        return diseaseAnnotations;
    }

    public void setDiseaseAnnotations(List<String> diseaseAnnotations) {
        this.diseaseAnnotations = diseaseAnnotations;
    }

    public List<String> getGoAnnotations() {
        return goAnnotations;
    }

    public void setGoAnnotations(List<String> goAnnotations) {
        this.goAnnotations = goAnnotations;
    }

    public List<String> getGeneChemAnnotations() {

        return geneChemAnnotations;
    }

    public void setGeneChemAnnotations(List<String> geneChemAnnotations) {
        this.geneChemAnnotations = geneChemAnnotations;
    }

    public List<String> getPathwayAnnotations() {
        return pathwayAnnotations;
    }

    public void setPathwayAnnotations(List<String> pathwayAnnotations) {
        this.pathwayAnnotations = pathwayAnnotations;
    }

    public List<String> getPhenotypeAnnotations() {
        return phenotypeAnnotations;
    }

    public void setPhenotypeAnnotations(List<String> phenotypeAnnotations) {
        this.phenotypeAnnotations = phenotypeAnnotations;
    }

    public List<String> getExperimentalAnnotations() {
        return experimentalAnnotations;
    }

    public void setExperimentalAnnotations(List<String> experimentalAnnotations) {
        this.experimentalAnnotations = experimentalAnnotations;
    }


    public void sortAnnotationsByType() throws Exception {
        Map<String, List<edu.mcw.rgd.datamodel.ontology.Annotation>> annotMap = indexDAO.getAnnotations(rgdId);
        for(String type: Arrays.asList("disease", "geneChemical","go","phenotype","xdata","pw")){
            List<String> annotations= new ArrayList<>();

            switch(type.toLowerCase()) {
            case "disease":

                for (java.util.Map.Entry e : annotMap.entrySet()) {
                    String key = (String) e.getKey();
                    List<Annotation> annots = (List<Annotation>) e.getValue();

                    if (key.equalsIgnoreCase("ClinVar") || key.equalsIgnoreCase("CTD") || key.equalsIgnoreCase("OMIM")
                            || key.equalsIgnoreCase("GAD") || key.equalsIgnoreCase("ManualDisease")) {

                        annotations.addAll(this.getAnnotationsWithSynonyms(annots));
                    }

                }
                this.diseaseAnnotations = annotations;
                break;

            case "geneChemical":

                for (java.util.Map.Entry e : annotMap.entrySet()) {
                    String key = (String) e.getKey();
                    if (key.equalsIgnoreCase("gene_chem")) {
                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }
                }
                this.geneChemAnnotations=annotations;
                break;
            case "go":
                for (java.util.Map.Entry e : annotMap.entrySet()) {
                    String key = (String) e.getKey();
                    if (key.equalsIgnoreCase("bp")) {
                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }

                    if (key.equalsIgnoreCase("cc")) {
                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }

                    if (key.equalsIgnoreCase("mf")) {
                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }
                }
                this.setGoAnnotations(annotations);
                break;
            case "pw":
                for (java.util.Map.Entry e : annotMap.entrySet()) {
                    String key = (String) e.getKey();
                    if (key.equalsIgnoreCase("rgd") || key.equalsIgnoreCase("smpdb") || key.equalsIgnoreCase("kegg") || key.equalsIgnoreCase("pid") || key.equalsIgnoreCase("otherPW")) {
                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }
                }
                this.setPathwayAnnotations(annotations);
                break;
            case "phenotype":
                for (java.util.Map.Entry e : annotMap.entrySet()) {
                    String key = (String) e.getKey();
                    if (key.equalsIgnoreCase("mammalian phenotype") || key.equalsIgnoreCase("hp")) {
                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }
                }
                this.setPhenotypeAnnotations(annotations);
                break;
            case "xdata":

                for (java.util.Map.Entry e : annotMap.entrySet()) {
                    String key = (String) e.getKey();

                    if (key.equalsIgnoreCase("co") || key.equalsIgnoreCase("cmo") || key.equalsIgnoreCase("xco") || key.equalsIgnoreCase("mmo") || key.equalsIgnoreCase("ma") || key.equalsIgnoreCase("rs") || key.equalsIgnoreCase("vt")) {

                        List<Annotation> annots = (List<Annotation>) e.getValue();
                        annotations.addAll(getAnnotationsWithSynonyms(annots));
                    }

                }
                this.experimentalAnnotations=(annotations);
                break;
        }
        }


    }
    public List<String> getAnnotationsWithSynonyms(List<Annotation> annots) throws Exception {
        OntologyXDAO ontologyXDAO=new OntologyXDAO();
        List<String> annotations= new ArrayList<>();
        for(Annotation a:annots){
            annotations.add(a.getTerm());

            List<TermSynonym> term_synms=ontologyXDAO.getTermSynonyms(a.getTermAcc());

            for(TermSynonym s:term_synms){
                annotations.add(s.getName());
            }

        } return annotations;}
}
