package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.Association;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.RgdVariant;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.List;
import java.util.stream.Collectors;

public class AlleleVariantDetails extends ObjectDetails<RgdVariant> {
    public AlleleVariantDetails(RgdVariant rgdVariant, IndexObject object) {
        super(rgdVariant, object);
    }

    @Override
    public int getRgdId() {
        return t.getRgdId();
    }

    @Override
    public int getSpeciesTypeKey() {
        return t.getSpeciesTypeKey();
    }

    @Override
    public void mapObject() {
        String symbol = obj.getName();

        Term term = null;
        try {
            term = ontologyXDAO.getTermByAccId(t.getSoAccId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        obj.setSymbol(symbol);
        obj.setTerm_acc(String.valueOf(getRgdId()));
        if (term != null)
            obj.setType(term.getTerm());
        obj.setName(obj.getName());

        obj.setCategory("Variant");
        obj.setVariantCategory("Phenotypic Variant");
        if(t.getRefNuc()!=null && t.getRefNuc().length()>10)
            obj.setRefNuc(t.getRefNuc().substring(0,6)+"...");
        else
            obj.setRefNuc(t.getRefNuc());
        if(t.getVarNuc()!=null && t.getVarNuc().length()>10)
            obj.setVarNuc(t.getVarNuc().substring(0,6)+"...");
        else
            obj.setVarNuc(t.getVarNuc());

    }

    @Override
    public void mapAnnotations() {
        try {
            obj.setAnnotationsCount(getAnnotsCount(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapAssociations() {
        try {
            List<Association> associations = associationDAO.getAssociationsForMasterRgdId(getRgdId(), "variant_to_gene");
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Association association : associations) {
                Gene gene = geneDAO.getGene(association.getDetailRgdId());
                if (gene != null) {
                    if (first) {
                        sb.append(gene.getSymbol());
                        first = false;
                    } else {
                        sb.append(" | ").append(gene.getSymbol());
                    }
                }
            }
            obj.setRegionName(sb.toString());
            obj.setRegionNameLc(sb.toString().toLowerCase());
            List<Strain> strainList = strainDAO.getAssociatedStrains(getRgdId());
            List<String> strains=strainList.stream().map(Strain::getName).collect(Collectors.toList());
            obj.setAnalysisName(strains);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}