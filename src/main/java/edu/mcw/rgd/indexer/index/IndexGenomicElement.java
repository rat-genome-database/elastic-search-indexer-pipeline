package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.GenomicElement;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.index.objectDetails.GeneDetails;
import edu.mcw.rgd.indexer.index.objectDetails.GenomicElementDetails;
import edu.mcw.rgd.indexer.index.objectDetails.ObjectDetails;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexGenomicElement implements Runnable {
    private GenomicElement ge;
    private String category;
    private Map<Integer, List<String>> associations;
    IndexDAO indexDAO=new IndexDAO();
    public IndexGenomicElement(GenomicElement ge, String category, Map<Integer, List<String>> associations){
        this.ge=ge;this.category=category;
        this.associations=associations;
    }
    @Override
    public void run() {
        IndexObject object=new IndexObject();
        object.setCategory(category);
        ObjectDetails<GenomicElement> details=new GenomicElementDetails(ge, object,associations);
        details.index();
//        try {
//            int speciesTypeKey = ge.getSpeciesTypeKey();
//            boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);
//            String species = SpeciesType.getCommonName(speciesTypeKey);
//            if (isSearchable) {
//
//                IndexObject g = new IndexObject();
//                int rgdId = ge.getRgdId();
//                String symbol = ge.getSymbol();
//
//                g.setSpecies(species);
//                g.setTerm_acc(String.valueOf(rgdId));
//                g.setSymbol(symbol);
//                g.setName(ge.getName());
//                g.setCategory(category);
//                g.setDescription(ge.getObjectType());
//                List<AliasData> aliases = indexDAO.getAliases(rgdId);
//                List<String> synonyms = new ArrayList<>();
//                for (AliasData a : aliases) {
//                    synonyms.add(a.getAlias_name());
//                }
//                g.setSynonyms(synonyms);
//                g.setXdbIdentifiers(indexDAO.getExternalIdentifiers(rgdId));
//
//                g.setAnnotationsCount(indexDAO.getAnnotsCount(rgdId));
//                g.setGenomicAlteration(ge.getGenomicAlteration());
//                List<String> assocs= associations.get(ge.getRgdId());
//                if(assocs!=null && assocs.size()>0)
//                    g.setAssociations(assocs);
//                indexDAO.setSuggest(g);
//            indexDAO.indexDocument(g);
//            }else{
//                if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
//                        || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
//                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tGenomic Element RGD ID: "+ ge.getRgdId()+"\t isSearchable: "+ isSearchable);
//                }
//            }
//        }catch (Exception e){
//            System.out.println(ge.getRgdId()+"\t"+ ge.getSpeciesTypeKey());
//            e.printStackTrace();
//        }
    }
}
