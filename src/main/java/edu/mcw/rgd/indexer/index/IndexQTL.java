package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.datamodel.QTL;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.Annotations;
import edu.mcw.rgd.indexer.model.IndexObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class IndexQTL implements Runnable {
    private QTL qtl;
    public IndexQTL(QTL qtl){
        this.qtl=qtl;
    }
    @Override
    public void run() {
        int speciesTypeKey = qtl.getSpeciesTypeKey();
        IndexDAO indexDAO=new IndexDAO();
        AssociationDAO associationDAO=new AssociationDAO();
        boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);

        if (isSearchable) {
            String species = SpeciesType.getCommonName(speciesTypeKey);
            IndexObject q = new IndexObject();
            String symbol = qtl.getSymbol();
            String name = qtl.getName();
            int rgdId = qtl.getRgdId();

            String htmlStrippedSymbol = Jsoup.parse(symbol).text();

            q.setTerm_acc(String.valueOf(rgdId));
            q.setSymbol(symbol);
            try {
                q.setTrait(indexDAO.getTraitSubTrait(qtl.getRgdId(), "V"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                q.setSubTrait(indexDAO.getTraitSubTrait(qtl.getRgdId(), "L"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            q.setSymbol(htmlStrippedSymbol);
            q.setName(name);
            q.setSpecies(species);
            q.setSuggest(indexDAO.getSuggest(symbol, null, "qtl"));
            try {
                Annotations<QTL> annotations=new Annotations<>(qtl);
                List<String> annots=annotations.getExperimentalAnnotations();
             //   q.setXdata(indexDAO.getAnnotations(annotMap, "xdata", "qtl"));
                q.setXdata(annots);
                q.setStatus(annotations.getObjectStatus());

            } catch (Exception e) {
                e.printStackTrace();
            }

            //  q.setSynonyms(getAliasesByRgdId(aliases, rgdId));
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
            q.setSynonyms(synonyms);
            try {
                q.setXdbIdentifiers(indexDAO.getExternalIdentifiers(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            q.setCategory("QTL");
            try {
                q.setMapDataList(indexDAO.getMapData(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                q.setAnnotationsCount(indexDAO.getAnnotsCount(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }


            List<Strain> sts = null;
            try {
                sts = associationDAO.getStrainAssociationsForQTL(qtl.getRgdId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<String> strainsCrossed =new ArrayList<>();
            if(qtl.getSpeciesTypeKey() == SpeciesType.RAT){
                for (Strain strain: sts) {
                    strainsCrossed.add(strain.getSymbol());
                }
            }

            q.setStrainsCrossed(strainsCrossed);
            indexDAO.indexDocument(q);

        }else{
            if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                    || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                try {
                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tQTL RGD ID: "+ qtl.getRgdId()+"\t isSearchable: "+ isSearchable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
