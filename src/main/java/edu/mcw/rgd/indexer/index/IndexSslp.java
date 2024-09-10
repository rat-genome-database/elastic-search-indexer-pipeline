package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.SSLP;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.List;

public class IndexSslp implements Runnable {
    private SSLP sslp;
    IndexDAO indexDAO=new IndexDAO();
    public IndexSslp(SSLP sslp){
        this.sslp=sslp;
    }
    @Override
    public void run() {
        int speciesTypeKey = sslp.getSpeciesTypeKey();
        boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);

        if (isSearchable) {
            String species = SpeciesType.getCommonName(speciesTypeKey);
            IndexObject slp = new IndexObject();
            int rgdId = sslp.getRgdId();
            String name = sslp.getName();
            slp.setTerm_acc(String.valueOf(rgdId));
            slp.setSymbol(sslp.getName());
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
            slp.setSynonyms(synonyms);
            //     slp.setSynonyms(getAliasesByRgdId(aliases,rgdId));
            try {
                slp.setXdbIdentifiers(indexDAO.getExternalIdentifiers(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            slp.setCategory("SSLP");

            slp.setSpecies(species);
            try {
                slp.setMapDataList(indexDAO.getMapData(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                slp.setAnnotationsCount(indexDAO.getAnnotsCount(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            indexDAO.setSuggest(slp);
            indexDAO.indexDocument(slp);

        }else{
            if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                    || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                try {
                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tSSLP RGD ID: "+ sslp.getRgdId()+"\t isSearchable: "+ isSearchable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
