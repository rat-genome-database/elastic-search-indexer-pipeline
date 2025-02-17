package edu.mcw.rgd.indexer.model.genomeInfo;

import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.SampleDAO;
import edu.mcw.rgd.datamodel.Sample;

import java.util.*;

public class VariantsCountsList implements List<VariantCounts> {
    List<VariantCounts> variantCounts=new ArrayList<>();
    public VariantsCountsList(List<VariantCounts> variantCounts){
        this.variantCounts=variantCounts;
    }
    public List<VariantCounts> getVariantCounts(int mapKey, String chromosome, int sampleId){
        List<VariantCounts> vcCounts=new ArrayList<>();
        while (iterator().hasNext()){
            VariantCounts vc=iterator().next();
            if(vc.getMapKey()==mapKey && vc.getChr().equals(chromosome) && vc.getSampleId()==sampleId){
                vcCounts.add(vc);
            }
        }
        return vcCounts;
    }
    public List<VariantCounts> getVariantCounts(int mapKey, String  chromosome) throws Exception {
        Map<Integer, String> sampleIdNameMap=getSampleIdNameMap(mapKey);
        List<VariantCounts> vcCounts=new ArrayList<>();
        while (iterator().hasNext()){
            VariantCounts vc=iterator().next();
            vc.setStrain(sampleIdNameMap.get(vc.getSampleId()));
            if(chromosome==null ){
                if(mapKey==vc.getMapKey()){
                    vcCounts.add(vc);
                }

            }else {
                if (vc.getMapKey() == mapKey && vc.getChr() == chromosome) {

                    vcCounts.add(vc);
                }
            }
        }
        return vcCounts;
    }
    public Map<Integer, String> getSampleIdNameMap(int mapKey) throws Exception {
        SampleDAO sampleDAO= new SampleDAO();
        sampleDAO.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        List<Sample> samples=null;
        java.util.Map<Integer, String> sampleIdName=new HashMap<>();
        samples=sampleDAO.getSamplesByMapKey(mapKey);
            for(Sample sample:samples){
                sampleIdName.put(sample.getId(), sample.getAnalysisName());
            }
       return sampleIdName;
    }
    @Override
    public int size() {
        return variantCounts.size();
    }

    @Override
    public boolean isEmpty() {
        return variantCounts.size()==0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<VariantCounts> iterator() {
        return variantCounts.iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(VariantCounts variantCounts) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends VariantCounts> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends VariantCounts> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public VariantCounts get(int index) {
        return null;
    }

    @Override
    public VariantCounts set(int index, VariantCounts element) {
        return null;
    }

    @Override
    public void add(int index, VariantCounts element) {

    }

    @Override
    public VariantCounts remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<VariantCounts> listIterator() {
        return null;
    }

    @Override
    public ListIterator<VariantCounts> listIterator(int index) {
        return null;
    }

    @Override
    public List<VariantCounts> subList(int fromIndex, int toIndex) {
        return null;
    }
}
