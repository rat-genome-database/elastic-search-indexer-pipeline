package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.Map;

import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.Associations;
import edu.mcw.rgd.indexer.model.GeneIndexObject;
import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.process.mapping.MapManager;
import org.jsoup.Jsoup;

import java.util.List;
import java.util.stream.Collectors;

public class GeneDetails extends ObjectDetails<Gene> {
    public GeneDetails(Gene gene, GeneIndexObject obj) {
        super(gene, obj);
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
        String symbol = t.getSymbol();
        String name = t.getName();
        String htmlStrippedSymbol = Jsoup.parse(symbol).text();
        String description = null;
        try {
            description = Utils.getGeneDescription(t);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String type = t.getType();

        obj.setTerm_acc(String.valueOf(getRgdId()));
        obj.setSymbol(symbol);
        obj.setHtmlStrippedSymbol(htmlStrippedSymbol);
        obj.setDescription(description);
        obj.setType(type);
        obj.setCategory("Gene");
        obj.setName(name);
    }
    @Override
    public void mapAssembly() {
        try {
          Map map=  MapManager.getInstance().getMap(obj.getMapKey());
          obj.setRank(map.getRank());
          obj.setMap(map.getName());
//            obj.setMapDataList(getMapData(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void mapPromotoers() {
        try {
            obj.setPromoters(getPromotersByGeneRgdId(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapSynonyms() {
        List<AliasData> aliases = null;
        try {
            aliases = getAliases(getRgdId());
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
    }
    @Override
    public void mapAssociations() {
        try{
            Associations<Gene> associations=new Associations<>(t);
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
    }

}
