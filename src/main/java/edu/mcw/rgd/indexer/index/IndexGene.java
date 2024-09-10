package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.QTL;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.*;
import edu.mcw.rgd.process.Utils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

            GeneIndexObject obj = new GeneIndexObject();
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
            List<String> synonyms = aliases!=null && aliases.size()>0?aliases.stream().map(a->a.getAlias_name()).collect(Collectors.toList()) : null;
            List<String> oldSymbols=aliases!=null && aliases.size()>0? aliases.stream()
                    .filter(a -> a.getAlias_type().equalsIgnoreCase("old_gene_symbol"))
                    .map(AliasData::getAlias_name).toList() : null;
            List<String> oldNames=aliases!=null && aliases.size()>0? aliases.stream()
                    .filter(a -> a.getAlias_type().equalsIgnoreCase("old_gene_name"))
                    .map(AliasData::getAlias_name).toList() : null;
            obj.setSynonyms(synonyms);
            obj.setOldSymbols(oldSymbols);
            obj.setOldNames(oldNames);
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
            try {
                Annotations<Gene> annotations=new Annotations<>(gene);
                obj.setGoAnnotations(annotations.getGoAnnotations());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try{
                Associations<Gene> associations=new Associations<>(gene);
                try {
                    List<String> sslpAssociations = associations.getAssociatedSSLPs();
                    if (sslpAssociations != null && sslpAssociations.size() > 0) {
                        obj.setWithSSLPS(true);
                    }
                }catch (Exception e){e.printStackTrace();}
                try {
                    List<String> homologs = associations.getHomologs();
                    if (homologs != null && homologs.size() > 0) {
                        obj.setWithHomologs(true);
                    }
                }catch (Exception e){e.printStackTrace();}
            }catch (Exception e){
                e.printStackTrace();
            }

            indexDAO.setSuggest(obj);
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
