package org.snt.autorex.trackauto;

import dk.brics.automaton.Automaton;

public class MemAutomatonNode implements Comparable<MemAutomatonNode> {

    public enum Kind {
        UNION,
        INTERSECTION,
        MINUS,
        CONCAT,
        PLUS,
        STAR,
        COMPLEMENT,
        LEAF
    }

    private Automaton a = null;
    private String name = "";
    private Kind kind = Kind.LEAF;
    private int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MemAutomatonNode(Kind kind,
                            Automaton a,
                            int id,
                            String name) {
        this.a = a;
        this.kind = kind;
        this.id = id;
        this.name = name;
    }

    public MemAutomatonNode(MemAutomatonNode n) {
        this.a = n.a.clone();
        this.kind = n.kind;
        this.id = n.id;
        this.name = n.name;
    }

    public Automaton getAutomaton() {
        return a;
    }

    public void setAutomaton(Automaton a) {
        this.a = a;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind k) {
        this.kind = k;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof MemAutomatonNode))
            return false;

        MemAutomatonNode o = (MemAutomatonNode)other;

        return id == o.id;
    }

    @Override
    public int compareTo(MemAutomatonNode n) {
        return id - n.getId();
    }
}
