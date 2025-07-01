package edu.mcw.rgd.indexer.dao.phenominer.model;

import edu.mcw.rgd.datamodel.ontologyx.Term;

import java.util.LinkedList;
import java.util.List;

public class TreeNode {
    private Term term;
    List<TreeNode> parents = new LinkedList<>();

    public TreeNode(Term term){this.term=term;}
    public TreeNode(Term term, List<TreeNode> parents){
        this.term=term;
        this.parents=parents;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public List<TreeNode> getParents() {
        return parents;
    }

    public void setParents(List<TreeNode> parents) {
        this.parents = parents;
    }
}
