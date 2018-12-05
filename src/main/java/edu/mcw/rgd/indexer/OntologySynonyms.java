package edu.mcw.rgd.indexer;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.datamodel.ontologyx.Ontology;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jthota on 7/20/2017.
 */
public class OntologySynonyms {
  private List<TermSynonym> pw;
    private List<TermSynonym> rdo;
  //  private List<TermSynonym> disease;
    private List<TermSynonym> bp;
    private List<TermSynonym> mf;
    private List<TermSynonym> cc;
    private List<TermSynonym> chebi;
    private List<TermSynonym> cl;
    private List<TermSynonym> cmo;
    private List<TermSynonym> cs;
    private List<TermSynonym> efo;
    private List<TermSynonym> hp;
    private List<TermSynonym> ma;
    private List<TermSynonym> mi;
    private List<TermSynonym> mmo;
    private List<TermSynonym> mp;
    private List<TermSynonym> nbo;
    private List<TermSynonym> rs;
    private List<TermSynonym> so;
    private List<TermSynonym> uberon;
    private List<TermSynonym> vt;
    private List<TermSynonym> xco;
    private List<TermSynonym> zfa;
    public OntologySynonyms() throws Exception {
        OntologyXDAO ontologyXDAO= new OntologyXDAO();
        List<Ontology> ontologies = ontologyXDAO.getPublicOntologies();
        for(Ontology ont:ontologies){
            List<TermSynonym> synonyms = ontologyXDAO.getActiveSynonyms(ont.getId());
            if(ont.getId().equalsIgnoreCase("pw")) this.setPw(synonyms);
            if(ont.getId().equalsIgnoreCase("rdo")) this.setRdo(synonyms);
          //  if(ont.getId().equalsIgnoreCase("do")) this.setDisease(synonyms);
            if(ont.getId().equalsIgnoreCase("bp")) this.setBp(synonyms);
            if(ont.getId().equalsIgnoreCase("mf")) this.setMf(synonyms);
            if(ont.getId().equalsIgnoreCase("cc")) this.setCc(synonyms);
            if(ont.getId().equalsIgnoreCase("chebi")) this.setChebi(synonyms);
            if(ont.getId().equalsIgnoreCase("cl")) this.setCl(synonyms);
            if(ont.getId().equalsIgnoreCase("cmo")) this.setCmo(synonyms);
            if(ont.getId().equalsIgnoreCase("cs")) this.setCs(synonyms);
            if(ont.getId().equalsIgnoreCase("efo")) this.setEfo(synonyms);
            if(ont.getId().equalsIgnoreCase("hp")) this.setHp(synonyms);
            if(ont.getId().equalsIgnoreCase("ma")) this.setMa(synonyms);
            if(ont.getId().equalsIgnoreCase("mi")) this.setMi(synonyms);
            if(ont.getId().equalsIgnoreCase("mmo")) this.setMmo(synonyms);
            if(ont.getId().equalsIgnoreCase("mp")) this.setMp(synonyms);
            if(ont.getId().equalsIgnoreCase("nbo")) this.setNbo(synonyms);
            if(ont.getId().equalsIgnoreCase("rs")) this.setRs(synonyms);
            if(ont.getId().equalsIgnoreCase("so")) this.setSo(synonyms);
            if(ont.getId().equalsIgnoreCase("uberon")) this.setUberon(synonyms);
            if(ont.getId().equalsIgnoreCase("vt")) this.setVt(synonyms);
            if(ont.getId().equalsIgnoreCase("xco")) this.setXco(synonyms);
            if(ont.getId().equalsIgnoreCase("zfa")) this.setZfa(synonyms);

        }
    }
    public List<TermSynonym> getPW() {
        return pw;
    }

    public void setPw(List<TermSynonym> pw) {
        this.pw = pw;
    }

    public List<TermSynonym> getRDO() {
        return rdo;
    }

    public void setRdo(List<TermSynonym> rdo) {
        this.rdo = rdo;
    }

   /* public List<TermSynonym> getDO() {
        return disease;
    }

    public void setDisease(List<TermSynonym> disease) {
        this.disease = disease;
    }
*/
  public List<TermSynonym> getBP() {
        return bp;
    }

    public void setBp(List<TermSynonym> bp) {
        this.bp = bp;
    }

    public List<TermSynonym> getMF() {
        return mf;
    }

    public void setMf(List<TermSynonym> mf) {
        this.mf = mf;
    }

    public List<TermSynonym> getCC() {
        return cc;
    }

    public void setCc(List<TermSynonym> cc) {
        this.cc = cc;
    }

    public List<TermSynonym> getCHEBI() {
        return chebi;
    }

    public void setChebi(List<TermSynonym> chebi) {
        this.chebi = chebi;
    }

    public List<TermSynonym> getCL() {
        return cl;
    }

    public void setCl(List<TermSynonym> cl) {
        this.cl = cl;
    }

    public List<TermSynonym> getCMO() {
        return cmo;
    }

    public void setCmo(List<TermSynonym> cmo) {
        this.cmo = cmo;
    }

    public List<TermSynonym> getCS() {
        return cs;
    }

    public void setCs(List<TermSynonym> cs) {
        this.cs = cs;
    }

    public List<TermSynonym> getEFO() {
        return efo;
    }

    public void setEfo(List<TermSynonym> efo) {
        this.efo = efo;
    }

    public List<TermSynonym> getHP() {
        return hp;
    }

    public void setHp(List<TermSynonym> hp) {
        this.hp = hp;
    }

    public List<TermSynonym> getMA() {
        return ma;
    }

    public void setMa(List<TermSynonym> ma) {
        this.ma = ma;
    }

    public List<TermSynonym> getMI() {
        return mi;
    }

    public void setMi(List<TermSynonym> mi) {
        this.mi = mi;
    }

    public List<TermSynonym> getMMO() {
        return mmo;
    }

    public void setMmo(List<TermSynonym> mmo) {
        this.mmo = mmo;
    }

    public List<TermSynonym> getMP() {
        return mp;
    }

    public void setMp(List<TermSynonym> mp) {
        this.mp = mp;
    }

    public List<TermSynonym> getNBO() {
        return nbo;
    }

    public void setNbo(List<TermSynonym> nbo) {
        this.nbo = nbo;
    }

    public List<TermSynonym> getRS() {
        return rs;
    }

    public void setRs(List<TermSynonym> rs) {
        this.rs = rs;
    }

    public List<TermSynonym> getSO() {
        return so;
    }

    public void setSo(List<TermSynonym> so) {
        this.so = so;
    }

    public List<TermSynonym> getUBERON() {
        return uberon;
    }

    public void setUberon(List<TermSynonym> uberon) {
        this.uberon = uberon;
    }

    public List<TermSynonym> getVT() {
        return vt;
    }

    public void setVt(List<TermSynonym> vt) {
        this.vt = vt;
    }

    public List<TermSynonym> getXCO() {
        return xco;
    }

    public void setXco(List<TermSynonym> xco) {
        this.xco = xco;
    }

    public List<TermSynonym> getZFA() {
        return zfa;
    }

    public void setZfa(List<TermSynonym> zfa) {
        this.zfa = zfa;
    }
}
