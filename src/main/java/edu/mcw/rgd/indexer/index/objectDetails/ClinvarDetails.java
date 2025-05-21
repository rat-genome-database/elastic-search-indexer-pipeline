package edu.mcw.rgd.indexer.index.objectDetails;

import edu.mcw.rgd.datamodel.RgdVariant;
import edu.mcw.rgd.datamodel.VariantInfo;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.indexer.model.IndexObject;

public class ClinvarDetails extends ObjectDetails<VariantInfo>{
    public ClinvarDetails(VariantInfo variantInfo, IndexObject object) {
        super(variantInfo, object);
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
        String symbol = obj.getSymbol();

        Term term = null;
        try {
            term = ontologyXDAO.getTermByAccId(t.getSoAccId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        obj.setSymbol(symbol);
        obj.setTerm_acc(String.valueOf(getRgdId()));
        if (term != null)
            obj.setType(term.getTerm());
        obj.setName(t.getName());
        obj.setTrait(t.getTraitName());
        obj.setCategory("Variant");
    }

    @Override
    public void mapAnnotations() {

    }

    @Override
    public void mapAssociations() {

    }
}
