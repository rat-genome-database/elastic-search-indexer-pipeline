package edu.mcw.rgd.indexer.indexers.objectSearchIndexer.objectDetails;

import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.datamodel.QTL;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.Strain;
import edu.mcw.rgd.indexer.model.Annotations;
import edu.mcw.rgd.indexer.model.IndexObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class QTLDetails extends  ObjectDetails<QTL> {
    AssociationDAO associationDAO=new AssociationDAO();
    public QTLDetails(QTL qtl, IndexObject object) {
        super(qtl, object);
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

        obj.setTerm_acc(String.valueOf(getRgdId()));
        obj.setSymbol(symbol);
        try {
            obj.setTrait(getTraitSubTrait(t.getRgdId(), "V"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            obj.setSubTrait(getTraitSubTrait(t.getRgdId(), "L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        obj.setSymbol(htmlStrippedSymbol);
        obj.setName(name);
        obj.setCategory("QTL");
    }

    @Override
    public void mapAnnotations() {
        try {
            obj.setAnnotationsCount(getAnnotsCount(getRgdId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Annotations<QTL> annotations=new Annotations<>(t);
            List<String> annots=annotations.getExperimentalAnnotations();
            //   q.setXdata(indexDAO.getAnnotations(annotMap, "xdata", "qtl"));
            obj.setXdata(annots);
            obj.setStatus(annotations.getObjectStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void mapAssociations() {
        List<Strain> sts = null;
        try {
            sts = associationDAO.getStrainAssociationsForQTL(getRgdId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> strainsCrossed =new ArrayList<>();
        if(t.getSpeciesTypeKey() == SpeciesType.RAT){
            for (Strain strain: sts) {
                strainsCrossed.add(strain.getSymbol());
            }
        }

        obj.setStrainsCrossed(strainsCrossed);
    }
}
