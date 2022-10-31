package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;
import edu.mcw.rgd.process.Utils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class IndexGene implements Runnable {
    private Gene gene;
    IndexDAO indexDAO=new IndexDAO();
    public IndexGene(Gene gene){
        this.gene=gene;
    }
    @Override
    public void run() {
        int speciesTypeKey = gene.getSpeciesTypeKey();
        String species = SpeciesType.getCommonName(speciesTypeKey);
        boolean isSearchable=SpeciesType.isSearchable(speciesTypeKey);
        if (isSearchable) {

            IndexObject obj = new IndexObject();
            int rgdId = gene.getRgdId();
            String symbol = gene.getSymbol();
            String name = gene.getName();
            String htmlStrippedSymbol = Jsoup.parse(symbol).text();
            String description = null;
            try {
                description = Utils.getGeneDescription(gene);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String type = gene.getType();

            obj.setTerm_acc(String.valueOf(rgdId));
            obj.setSymbol(symbol);
            obj.setHtmlStrippedSymbol(htmlStrippedSymbol);
            obj.setDescription(description);
            obj.setSpecies(species);
            obj.setType(type);
            obj.setCategory("Gene");
            obj.setName(name);

            List<AliasData> aliases = null;
            try {
                aliases = indexDAO.getAliases(gene.getRgdId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<String> synonyms = new ArrayList<>();
            for (AliasData a : aliases) {
                synonyms.add(a.getAlias_name());
            }
            obj.setSynonyms(synonyms);
            //    obj.setSynonyms(getAliasesByRgdId(aliases, rgdId));
            try {
                obj.setXdbIdentifiers(indexDAO.getExternalIdentifiers(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //   obj.setXdbIdentifiers(getXdbIds(objects, rgdId));
            try {
                obj.setPromoters(indexDAO.getPromotersByGeneRgdId(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //  obj.setPromoters(getPromoersByRgdId(rgdId, associations, genomicElements));
            try {
                obj.setMapDataList(indexDAO.getMapData(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                obj.setTranscriptIds(indexDAO.getTranscriptIds(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                obj.setProtein_acc_ids(indexDAO.getTranscriptProteinIds(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                obj.setAnnotationsCount(indexDAO.getAnnotsCount(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            obj.setSuggest(indexDAO.getSuggest(symbol, null, "gene"));
            indexDAO.indexDocument(obj);

        }else{
            if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                    || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                try {
                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tGene RGD ID: "+ gene.getRgdId()+"\t isSearchable: "+ isSearchable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
