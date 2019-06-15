/**
 * autorex - fsm state eliminator
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Julian Thome <julian.thome.de@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package org.snt.autorex;

import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for dk.brics transitions
 */
public class FullTransition {

    private State src;
    private State dest;
    private Set<Transition> trans;
    private Transition recentlyAdded;
    private String label = "";

    private int tid;
    private boolean isEpsilon = false;
    private Kind kind;
    private LabelTranslator ltrans = null;

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

    public FullTransition(State src, Transition trans, State dest,
                          LabelTranslator ltrans) {
        this.src = src;
        this.trans = new HashSet<>();
        this.ltrans = ltrans;

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

    public void setIsEpsilon(boolean eps) {
        if(eps) {
            this.kind = Kind.EPSILON;
        }
    }

    public boolean isEpsilon() {
        return this.kind == Kind.EPSILON;

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

    public String getTransitionLabel() {

        if(this.trans.size() == 1)
            return ltrans.getTransitionString(this.trans.iterator().next());

        StringBuilder sb = new StringBuilder();

        for(Transition t : this.trans) {
            if(sb.length() > 0)
                sb.append("|");
            sb.append(ltrans.getTransitionString(t));
        }

        sb.insert(0,"(");
        sb.append(")");

        return sb.toString();
    }

    public String getLabel() {
        return this.label;
    }

    public Kind getKind() {
        return this.kind;
    }

    @Override
    public String toString() {
        java.lang.StringBuilder sb = new java.lang.StringBuilder();
        sb.append("==================================\n");
        sb.append("Source:"+ this.src.toString() + "\n");
        sb.append("Transition:" + this.trans.toString() + "\n");
        sb.append("Dest:" + this.dest.toString() +"\n");
        sb.append("==================================\n");
        return sb.toString();
    }
}