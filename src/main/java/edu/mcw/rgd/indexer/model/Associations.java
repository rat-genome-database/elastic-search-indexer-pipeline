package edu.mcw.rgd.indexer.model;

import edu.mcw.rgd.dao.impl.AssociationDAO;
import edu.mcw.rgd.dao.impl.GeneDAO;
import edu.mcw.rgd.dao.impl.SSLPDAO;
import edu.mcw.rgd.datamodel.Gene;
import edu.mcw.rgd.datamodel.SSLP;

import java.util.List;
import java.util.stream.Collectors;

public class Associations<T> extends RGDObject<T>{
    public Associations(T obj) throws Exception {
        super(obj);
    }
    public List<String> getAssociatedSSLPs() throws Exception {
        SSLPDAO sslpdao=new SSLPDAO();
        List<SSLP> sslps=sslpdao.getSSLPsForGene(key);
        return sslps!=null && sslps.size()>0? sslps.stream().map(SSLP::getName).collect(Collectors.toList()):null;
    }
    public List<String> getHomologs() throws Exception {
        GeneDAO geneDAO=new GeneDAO();
        List<Gene> homologs = geneDAO.getHomologs(rgdId);
        return homologs!=null && homologs.size()>0? homologs.stream().map(Gene::getSymbol).collect(Collectors.toList()):null;

    }

}
