package edu.mcw.rgd.indexer.model;

import java.util.List;

public class GeneIndexObject extends IndexObject{
    private List<String> goAnnotations;
    private boolean withSSLPS;
    private boolean withHomologs;

    public List<String> getGoAnnotations() {
        return goAnnotations;
    }

    public void setGoAnnotations(List<String> goAnnotations) {
        this.goAnnotations = goAnnotations;
    }

    public boolean isWithSSLPS() {
        return withSSLPS;
    }

    public void setWithSSLPS(boolean withSSLPS) {
        this.withSSLPS = withSSLPS;
    }

    public boolean isWithHomologs() {
        return withHomologs;
    }

    public void setWithHomologs(boolean withHomologs) {
        this.withHomologs = withHomologs;
    }
}
