/*
* autorex - fsm state eliminator
*
* Copyright 2016, Julian Thomé <julian.thome@uni.lu>
*
* Licensed under the EUPL, Version 1.1 or – as soon they will be approved by
* the European Commission - subsequent versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the Licence. You may
* obtain a copy of the Licence at:
*
* https://joinup.ec.europa.eu/sites/default/files/eupl1.1.-licence-en_0.pdf
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/

package org.snt.autorex;

import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import org.snt.autorex.util.BufferedString;

import java.util.HashSet;
import java.util.Set;

public class FullTransition {

    private State src;
    private State dest;
    private Set<Transition> trans;
    private Transition recentlyAdded;
    private BufferedString label = new BufferedString();
    private BufferedString carry = new BufferedString();

    public BufferedString getCarry() {
        if(!carry.isEmpty())
            return carry;
        else
            return new BufferedString(getLastTran().getMax());
    }

    public void setCarry(BufferedString carry) {
        this.carry = carry;
    }

    private int tid;
    private boolean isEpsilon = false;
    private Kind kind;

    public boolean isConcrete() {
        return isEpsilon || !carry.isEmpty() || getLastTran().getMin() ==
                getLastTran()
                .getMax();
    }

    public enum Kind {

        MATCH(1, "m"),
        SUBST(2, "s"),
        DEL(3, "d"),
        INS(4, "i"),
        EPSILON(5, "e");

        private final String sval;
        private final int ival;

        Kind(int ival, String sval) {
            this.sval = sval;
            this.ival = ival;
        }

        public int getId() {
            return this.ival;
        }

        public String toString() {
            return this.sval;
        }
    }

    public static int id = 0;

    public FullTransition(State src, Transition trans, State dest) {
        this.src = src;
        this.trans = new HashSet<>();

        if(trans != null) {
            this.trans.add(trans);
            this.label = getTransitionLabel();
        }

        this.dest = dest;
        this.tid = id ++;
        this.isEpsilon = false;
        this.kind = Kind.MATCH;
        this.recentlyAdded = trans;
    }

    public FullTransition(State src, Transition trans, State dest, Kind kind) {
        this(src,trans,dest);
        this.kind = kind;
    }


    public void setIsEpsilon(boolean eps) {
        if(eps) {
            this.kind = Kind.EPSILON;
        }
    }

    public boolean isEpsilon() {
        return this.kind == Kind.EPSILON;

    }
    public FullTransition(State src, State dest) {
        this(src, null, dest);
    }

    public State getSourceState() {
        return this.src;
    }

    public State getTargetState() {
        return this.dest;
    }


    public void setSourceState(State src) {
        this.src = src;
    }

    public void setTargetState(State target) {
        this.dest = target;
    }

    public Set<Transition> getTrans() {
        return this.trans;
    }

    public Transition getLastTran() {
        return this.recentlyAdded;
    }


    public void addTransition (Transition t) {
        this.trans.add(t);
        this.label = getTransitionLabel();
    }

    private BufferedString getTransitionString(Transition t) {

        BufferedString sb = new BufferedString();

        if (t.getMax() == t.getMin()) {
            sb.append(t.getMin());
        } else {
            sb.append("\u0000[" + t.getMin() + "\u0000-" + t.getMax() + "\u0000]");
        }

        return sb;
    }

    public BufferedString getTransitionLabel() {

        if(this.trans.size() == 1)
            return getTransitionString(this.trans.iterator().next());

        BufferedString sb = new BufferedString();

        for(Transition t : this.trans) {
            if(!sb.isEmpty())
                sb.append("\u0000|");
            sb.append(getTransitionString(t));
        }

        sb.prepend("\u0000(");
        sb.append("\u0000)");

        return sb;
    }

    public BufferedString getLabel() {
        return this.label;
    }

    public void setLabel(BufferedString l) {
        this.label = l;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================\n");
        sb.append("Source:"+ this.src.toString() + "\n");
        sb.append("Transition:" + this.trans.toString() + "\n");
        sb.append("Dest:" + this.dest.toString() +"\n");
        sb.append("==================================\n");
        return sb.toString();
    }


}