package com.inso.modules.passport.user.model;

public class UserRelation {

    private long ancestor;
    private long descendant;
    private long depth;


    public static String getColumnPrefix(){
        return "relation";
    }


    public long getAncestor() {
        return ancestor;
    }

    public void setAncestor(long ancestor) {
        this.ancestor = ancestor;
    }

    public long getDescendant() {
        return descendant;
    }

    public void setDescendant(long descendant) {
        this.descendant = descendant;
    }

    public long getDepth() {
        return depth;
    }

    public void setDepth(long depth) {
        this.depth = depth;
    }
}
