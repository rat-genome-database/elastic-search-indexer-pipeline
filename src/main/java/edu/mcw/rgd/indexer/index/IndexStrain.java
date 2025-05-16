package edu.mcw.rgd.indexer.index;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.MappedStrain;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.indexer.dao.IndexDAO;
import edu.mcw.rgd.indexer.model.AliasData;
import edu.mcw.rgd.indexer.model.IndexObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexStrain implements Runnable {
    private MappedStrain mappedObject;
    private Strain strain;
    private Map<Integer, Gene> genes;
    IndexDAO indexDAO=new IndexDAO();
    public IndexStrain(MappedStrain mappedObject){
        this.mappedObject=mappedObject;
        this.strain=mappedObject.getStrain();}
    @Override
    public void run() {
        int speciesTypeKey = strain.getSpeciesTypeKey();
        boolean isSearchable= SpeciesType.isSearchable(speciesTypeKey);
        //  System.out.println("isSearchable: "+isSearchable +"\tSpeciesTypeKey: "+ speciesTypeKey+"\tStrain RGDID: "+strain.getRgdId());
        if (isSearchable) {
            String species = SpeciesType.getCommonName(speciesTypeKey);

            IndexObject s = new IndexObject();
            String symbol = strain.getSymbol();
            String source = strain.getSource();
            String origin = strain.getOrigination();
            String description=strain.getDescription();
            String strainTypeName = strain.getStrainTypeName();
            String name = strain.getName();

            int rgdId = strain.getRgdId();
            s.setSpecies(species);
            s.setTerm_acc(String.valueOf(rgdId));
            s.setSymbol(symbol);
            s.setSource(source);
            s.setOrigin(origin);
            s.setDescription(description);
            s.setType(strainTypeName);
            s.setName(name);

            String htmlStrippedSymbol = Jsoup.parse(symbol).text();
            s.setHtmlStrippedSymbol(htmlStrippedSymbol);
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
            s.setSynonyms(synonyms);
            //     s.setSynonyms(getAliasesByRgdId(aliases,  rgdId));
            try {
                s.setXdbIdentifiers(indexDAO.getExternalIdentifiers(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.setCategory("Strain");
            try {
                s.setExperimentRecordCount(indexDAO.getExperimentRecordCount(rgdId, "S"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                s.setSampleExists(indexDAO.sampleExists(rgdId, 600));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                s.setMapDataList(indexDAO.getMapData(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                s.setAnnotationsCount(indexDAO.getAnnotsCount(rgdId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Gene g=genes.get(rgdId);
            if(g!=null){
                List<String> associations= new ArrayList<>();
                associations.add(g.getSymbol());
                associations.add(g.getName());
                s.setAssociations(associations);
            }
            indexDAO.setSuggest(s);
            indexDAO.indexDocument(s);

        }else{
            if(speciesTypeKey==3 || speciesTypeKey==2 || speciesTypeKey==1 || speciesTypeKey==4 || speciesTypeKey==5
                    || speciesTypeKey==7 || speciesTypeKey==6 || speciesTypeKey==9){
                try {
                    throw new Exception("Species Type Key: " +speciesTypeKey +"\tStrain RGD ID: "+ strain.getRgdId()+"\t isSearchable: "+ isSearchable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
