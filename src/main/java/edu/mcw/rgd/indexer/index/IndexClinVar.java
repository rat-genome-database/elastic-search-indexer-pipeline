package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.VariantInfo;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.List;

public class IndexClinVar implements Runnable{
    private VariantInfo obj;
    IndexDAO indexDAO=new IndexDAO();
    OntologyXDAO ontologyXDAO=new OntologyXDAO();
    public IndexClinVar(VariantInfo obj){this.obj=obj;}
    @Override
    public void run() {
        //  VariantInfo obj= vdao.getVariant(8554914);
        int speciesTypeKey = obj.getSpeciesTypeKey();
        boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);
        if (isSearchable) {
            String species = (SpeciesType.getCommonName(speciesTypeKey));
            IndexObject v = new IndexObject();
            int rgdId = obj.getRgdId();
            String symbol = obj.getSymbol();

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
            v.setTrait(obj.getTraitName());
            v.setCategory("Variant");
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
            indexDAO.indexDocument(v);
        }else{
            if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                    || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                try {
                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tVariant RGD ID: "+ obj.getRgdId()+"\t isSearchable: "+ isSearchable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
