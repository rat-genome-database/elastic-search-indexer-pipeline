package edu.mcw.rgd.indexer.model;

import java.util.Date;
import java.util.List;

/**
 * Created by jthota on 2/3/2017.
 */
public class RefObject {
    private String term_acc;
    private String citation;
    private String title;
    private List<String> author;
    private String pub_year;
    private String category;
    private String species;
    private List<String> xdbIdentifiers;
    private List<AliasData> aliasDatas;
    private List<String> synonyms;
    private String refAbstract;
    private Suggest suggest;

    public Suggest getSuggest() {
        return suggest;
    }

    public void setSuggest(Suggest suggest) {
        this.suggest = suggest;
    }
    public String getRefAbstract() {
        return refAbstract;
    }

    public void setRefAbstract(String refAbstract) {
        this.refAbstract = refAbstract;
    }

    public String getTerm_acc() {
        return term_acc;
    }

    public void setTerm_acc(String term_acc) {
        this.term_acc = term_acc;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public String getPub_year() {
        return pub_year;
    }

    public void setPub_year(String pub_year) {
        this.pub_year = pub_year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public List<String> getXdbIdentifiers() {
        return xdbIdentifiers;
    }

    public void setXdbIdentifiers(List<String> xdbIdentifiers) {
        this.xdbIdentifiers = xdbIdentifiers;
    }

    public List<AliasData> getAliasDatas() {
        return aliasDatas;
    }

    public void setAliasDatas(List<AliasData> aliasDatas) {
        this.aliasDatas = aliasDatas;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
