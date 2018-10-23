package edu.mcw.rgd.indexer.model;

import java.util.List;

/**
 * Created by jthota on 10/16/2017.
 */
public class Suggest {
    private List<String> input;

    private Contexts contexts;

    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    public Contexts getContexts() {
        return contexts;
    }

    public void setContexts(Contexts contexts) {
        this.contexts = contexts;
    }
}
