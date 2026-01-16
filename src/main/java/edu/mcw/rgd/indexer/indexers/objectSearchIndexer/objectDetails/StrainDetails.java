package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.indexer.model.IndexObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;


public class StrainDetails extends ObjectDetails<Strain>{

    public StrainDetails(Strain strain, IndexObject object) {
        super(strain, object);
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
        obj.setTerm_acc(String.valueOf(getRgdId()));
        obj.setSymbol(t.getSymbol());
        obj.setSource(t.getSource());
        obj.setOrigin(t.getOrigination());
        obj.setDescription(t.getDescription());
        obj.setType(t.getStrainTypeName());
        obj.setName(t.getName());
        obj.setCategory("Strain");
        String htmlStrippedSymbol = Jsoup.parse(t.getSymbol()).text();
        obj.setHtmlStrippedSymbol(htmlStrippedSymbol);
        try {
            obj.setSampleExists(sampleExists(getRgdId(), 600));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void mapAnnotations() {
        try {
            obj.setExperimentRecordCount(getExperimentRecordCount(getRgdId(), "S"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            obj.setAnnotationsCount(getAnnotsCount(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapAssociations() {
        try {
            Gene g = getStrainAssociations(getRgdId());
            if (g != null) {
                List<String> associations = new ArrayList<>();
                associations.add(g.getSymbol());
                associations.add(g.getName());
                obj.setAssociations(associations);
            }
        }catch (Exception e){e.printStackTrace();}
    }

}
