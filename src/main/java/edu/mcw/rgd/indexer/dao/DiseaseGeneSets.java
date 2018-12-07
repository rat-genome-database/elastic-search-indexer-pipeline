package edu.mcw.rgd.indexer.dao;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.impl.AnnotationDAO;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.MapDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.MappedGene;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermWithStats;
import edu.mcw.rgd.indexer.model.genomeInfo.DiseaseGeneObject;
import edu.mcw.rgd.process.Utils;


import java.util.*;

/**
 * Created by jthota on 11/9/2017.
 */
public class DiseaseGeneSets extends AbstractDAO {

    GenomeDAO genomeDAO= new GenomeDAO();
    GeneDAO geneDAO= new GeneDAO();
    OntologyXDAO ontologyXDAO= new OntologyXDAO();

    public List<DiseaseGeneObject> getDiseaseGeneSets(int mapKey, String chr, int speciesTypeKey) throws Exception {

        String rootTerm=ontologyXDAO.getRootTerm("RDO");
        List<TermWithStats> topLevelDiseaseTerms=   ontologyXDAO.getActiveChildTerms(rootTerm,speciesTypeKey);
        List<DiseaseGeneObject> diseaseGeneSets= new ArrayList<>();
        Ontology ont= ontologyXDAO.getOntology("RDO");
        String aspect=ont.getAspect();
        for(TermWithStats t:topLevelDiseaseTerms){
            DiseaseGeneObject obj= new DiseaseGeneObject();

           int count= genomeDAO.getGeneCountsByTermAcc(t.getAccId(), mapKey, aspect, chr);
            if(count>0) {
                obj.setOntTermAccId(t.getAccId());
                obj.setOntTerm(t.getTerm());
                obj.setGeneCount(count);
                diseaseGeneSets.add(obj);
            }
        }
         Collections.sort(diseaseGeneSets, new Comparator<DiseaseGeneObject>() {
             @Override
             public int compare(DiseaseGeneObject o1, DiseaseGeneObject o2) {
               return   Utils.stringsCompareToIgnoreCase(o1.getOntTerm(), o2.getOntTerm());

             }
         });
        return diseaseGeneSets;
    }
    public StringBuffer getDiseaseGeneTree(int mapKey, String chr) throws Exception {
        AnnotationDAO adao= new AnnotationDAO();
        List<TermWithStats> level2diseaseTerms=new ArrayList<>();
        List<TermWithStats> topLevelDiseaseTerms=   ontologyXDAO.getActiveChildTerms("DOID:4",3);

        for(TermWithStats dTerm: topLevelDiseaseTerms){
            if(!dTerm.getAccId().equals("DOID:225")) { //DOID:225 is SYNDROME, which we are not considering its child terms instead we are considering the term SYNDROME itself.
                List<TermWithStats> nextLevelTerms = ontologyXDAO.getActiveChildTerms(dTerm.getAccId(), 3);
                level2diseaseTerms.addAll(nextLevelTerms);
            }else{
                level2diseaseTerms.add(dTerm); //if it is DOID:225, add to the List.
            }
        }
        List<MappedGene> genes= geneDAO.getActiveMappedGenes(mapKey);
        List<MappedGene> chromosomeFilteredGenes= new ArrayList<>();
        for(MappedGene g:genes){
           if(g.getChromosome().equals(chr)){
               chromosomeFilteredGenes.add(g);
           }
        }
        Map<Integer, List<Annotation>> diseaseGeneAnnots= new HashMap<>();
        List<MappedGene> diseaseAnnotGenes= new ArrayList<>();
        for(MappedGene mg: chromosomeFilteredGenes){
            Gene g= mg.getGene();
           List<Annotation> annots= adao.getAnnotations(g.getRgdId(), "I"); //Disease Annotations of Gene
            if(annots.size()>0){
                diseaseGeneAnnots.put(g.getRgdId(), annots);
            }
        }

        for(TermWithStats t:level2diseaseTerms){

        }

        return null;
    }
    public static void main(String[] args) throws Exception {
       DiseaseGeneSets ds= new DiseaseGeneSets();
     List<DiseaseGeneObject> diseaseGeneSets=  ds.getDiseaseGeneSets(360, "1", 3);
        for(DiseaseGeneObject d:diseaseGeneSets){
            System.out.println(d.getOntTermAccId() + "  "+ d.getOntTerm() + "   "+ d.getGeneCount());
        }
       System.out.println("DONE");
   }
}
