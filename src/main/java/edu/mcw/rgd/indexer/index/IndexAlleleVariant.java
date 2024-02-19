package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.indexer.model.variants.VariantIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IndexAlleleVariant implements Runnable{

    private RgdVariant obj;
    IndexDAO indexDAO=new IndexDAO();
    OntologyXDAO ontologyXDAO=new OntologyXDAO();
    AssociationDAO associationDAO=new AssociationDAO();
    GeneDAO geneDAO=new GeneDAO();
    StrainDAO strainDAO=new StrainDAO();
    public IndexAlleleVariant(RgdVariant obj){this.obj=obj;}
    @Override
    public void run() {
        int speciesTypeKey = obj.getSpeciesTypeKey();
        boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);
        if (isSearchable) {
            String species = (SpeciesType.getCommonName(speciesTypeKey));
            VariantIndex v = new VariantIndex();
            int rgdId = obj.getRgdId();
            String symbol = obj.getName();

            Term term = null;
            try {
                term = ontologyXDAO.getTermByAccId(obj.getSoAccId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            v.setSpecies(species);
            v.setSymbol(symbol);
            v.setTerm_acc(String.valueOf(rgdId));
            if (term != null)
                v.setType(term.getTerm());
            v.setName(obj.getName());

            v.setCategory("Variant");
            v.setVariantCategory("Phenotypic Variant");
            v.setSuggest(indexDAO.getSuggest(symbol, null, "variant"));
            //   v.setSynonyms(getAliasesByRgdId(aliases, rgdId));
            List<AliasData> aliases = null;
            try {
                aliases = indexDAO.getAliases(rgdId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<String> synonyms = new ArrayList<>();
            for (AliasData a : aliases) {
                synonyms.add(a.getAlias_name());
            }
            v.setSynonyms(synonyms);
            try {
                v.setXdbIdentifiers(indexDAO.getExternalIdentifiers(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                v.setMapDataList(indexDAO.getMapData(obj.getRgdId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                v.setAnnotationsCount(indexDAO.getAnnotsCount(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                List<Association> associations=associationDAO.getAssociationsForMasterRgdId(obj.getRgdId(),"variant_to_gene");
                StringBuilder sb=new StringBuilder();
                boolean first=true;
                for(Association association:associations){
                    Gene gene=geneDAO.getGene(association.getDetailRgdId());
                    if(gene!=null){
                        if(first) {
                            sb.append(gene.getSymbol());
                            first=false;
                        }else{
                            sb.append(" | ").append(gene.getSymbol());
                        }
                    }
                }
                v.setRegionName(sb.toString());
                v.setRegionNameLc(sb.toString().toLowerCase());
                List<Strain> strainList = strainDAO.getAssociatedStrains(obj.getRgdId());
                List<String> strains=strainList.stream().map(Strain::getName).collect(Collectors.toList());
                v.setAnalysisName(strains);
                if(obj.getRefNuc()!=null && obj.getRefNuc().length()>10)
                    v.setRefNuc(obj.getRefNuc().substring(0,6)+"...");
                else
                    v.setRefNuc(obj.getRefNuc());
                if(obj.getVarNuc()!=null && obj.getVarNuc().length()>10)
                    v.setVarNuc(obj.getVarNuc().substring(0,6)+"...");
                else
                    v.setVarNuc(obj.getVarNuc());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            indexDAO.indexDocument(v);
        }
    }
}
