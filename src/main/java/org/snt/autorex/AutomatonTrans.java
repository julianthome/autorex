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

import dk.brics.automaton.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutomatonTrans {

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonTrans.class);

    public enum Kind {

        SUFFIX(1,"suffix"),
        NORMAL(3,"normal"),
        CAMEL(5, "camel"),
        SUBSTRING(7, "substring"),
        LEN(9, "len"),
        EPSILON(11, "epsilon");

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

        public static Kind KindFromString(String kind) {
            switch(kind) {
                case "suffix": return SUFFIX;
                case "normal": return NORMAL;
                case "camel" : return CAMEL;
                case "substring": return SUBSTRING;
                case "len": return LEN;
                case "epsilon": return EPSILON;

            }
            // should never ever happen
            assert(false);
            return null;
        }

        public boolean isSuffix() {
            return this == SUFFIX;
        }
        public boolean isNormal() {
            return this == NORMAL;
        }
        public boolean isCamel() {
            return this == CAMEL;
        }
        public boolean isLen() {
            return this == LEN;
        }
        public boolean isSubstring() {return this == SUBSTRING;}

    }

    private Kind kind = Kind.NORMAL;

    protected Automaton auto = null;
    protected Set<State>  states = new HashSet<>();
    protected Map<State, Integer> statenumber = new HashMap<>();
    protected State init = null;
    protected LabelTranslator ltrans = null;

    HashMap<State, HashSet<FullTransition>> incoming = new HashMap<>();
    HashMap<State, HashSet<FullTransition>> outgoing = new HashMap<>();
    HashSet<FullTransition> transitions = new HashSet<>();

    private int stateId;


    public AutomatonTrans() {
        super();
        stateId = 0;
    }

    public AutomatonTrans(Automaton a, LabelTranslator ltrans) {
        this();
        this.ltrans = ltrans;
        this.auto = a.clone();
        this.init = this.auto.getInitialState();
        prepare();
        finalize();
    }


    public AutomatonTrans(Automaton a) {
        this(a, new DefaultLabelTranslator());
    }

    public AutomatonTrans(String rexp) {
        this(new RegExp(rexp).toAutomaton());
    }

    public AutomatonTrans(String rexp, LabelTranslator ltrans) {
        this(new RegExp(rexp).toAutomaton(),ltrans);
    }


    private void reset() {
        incoming.clear();
        outgoing.clear();
        transitions.clear();
    }
    private void set() {
        for (State s : auto.getStates()) {
            s.setAccept(true);
        }
    }

    private void setAccepting() {
        for (State s : auto.getStates()) {
            s.setAccept(true);
        }
    }



    private void prepare() {
        // get all transitions
        reset();
        for (State s : auto.getStates()) {
            for (Transition t : s.getTransitions()) {
                FullTransition ft = new FullTransition(s, t, t.getDest(), ltrans);
                addTransition(ft);
            }
        }
    }


    public void addTransitions(Collection<FullTransition> ft) {

        for(FullTransition t : ft) {
            addTransition(t);
        }
        Set<State> visited = new HashSet<>();
        dfsNumering(init, visited);
    }

    public void addTransition(FullTransition ft) {
        states.add(ft.getSourceState());
        states.add(ft.getTargetState());
        addToIncoming(ft);
        addToOutgoing(ft);
        transitions.add(ft);
    }


    private void addToIncoming(FullTransition ft) {
        if (!incoming.containsKey(ft.getTargetState())) {
            incoming.put(ft.getTargetState(), new HashSet<>());
        }
        incoming.get(ft.getTargetState()).add(ft);
    }

    private void addToOutgoing(FullTransition ft) {
        if (!outgoing.containsKey(ft.getSourceState())) {
            outgoing.put(ft.getSourceState(), new HashSet<>());
        }
        outgoing.get(ft.getSourceState()).add(ft);
    }

    private void setEpsilon() {

        boolean binit = init.isAccept();

        Set<StatePair> spairs = new HashSet<StatePair>();
        for (State s : auto.getStates()) {
            if (!s.equals(auto.getInitialState())) {
                spairs.add(new StatePair(auto.getInitialState(), s));
            }
        }
        auto.addEpsilons(spairs);
        init.setAccept(binit);
    }


    protected void convertToCamelCaseAutomaton() {

        Set<Transition> handled = new HashSet<Transition>();

        for (State s : auto.getStates()) {

            Set<Transition> transitions = new HashSet<Transition>();

            transitions.addAll(s.getTransitions());

            for (Transition t : transitions) {

                if (handled.contains(t))
                    continue;

                char min = t.getMin();
                char max = t.getMax();

                if (CharUtils.isLowerCase(min)) {
                    min = Character.toUpperCase(min);
                } else if (CharUtils.isUpperCase(min)) {
                    min = Character.toLowerCase(min);
                }

                if (CharUtils.isLowerCase(max)) {
                    max = Character.toUpperCase(min);
                } else if (CharUtils.isUpperCase(max)) {
                    max = Character.toLowerCase(min);
                }

                Transition tnew = new Transition(min, max, t.getDest());
                s.addTransition(tnew);
                handled.add(tnew);
                handled.add(t);
            }
        }

        auto.removeDeadTransitions();
        auto.determinize();
        this.kind = Kind.CAMEL;
        this.prepare();
    }

    protected void convertToLenAutomaton() {

        Map<Transition, State> transitions = new HashMap<>();

        for (State s : auto.getStates()) {
            for(Transition t : s.getTransitions()){
                transitions.put(t, s);
            }
        }

        for (Transition t : transitions.keySet()) {

            if(t.getMin() == Character.MIN_VALUE && t.getMax() == Character
                    .MAX_VALUE)
                continue;

            Transition tnew = new Transition(Character.MIN_VALUE,
                    Character.MAX_VALUE, t.getDest());

            State s = transitions.get(t);

            LOGGER.debug("remove {}:{}", t.getMax(), t.getMax());


            s.getTransitions().remove(t);
            s.getTransitions().add(tnew);
        }

        auto.removeDeadTransitions();
        auto.determinize();
        this.kind = Kind.LEN;
        this.prepare();
        this.finalize();
    }


    protected void convertToSubstringAutomaton() {
        setAccepting();
        setEpsilon();
        this.kind = Kind.SUBSTRING;
        this.prepare();
        this.finalize();
    }

    protected void convertToSuffixAutomaton() {
        setEpsilon();
        this.kind = Kind.SUFFIX;
        this.prepare();
        this.finalize();
    }


    public void finalize() {
        stateId = 0;
        statenumber.clear();
        Set<State> visited = new HashSet<State>();
        dfsNumering(init, visited);
    }

    private void dfsNumering(State s, Set<State> visited) {

        if(!visited.contains(s))
            visited.add(s);
        else
            return;

        this.stateId++;
        this.statenumber.put(s, this.stateId);

        if(!outgoing.containsKey(s))
            return;

        for (FullTransition t : outgoing.get(s)) {
            dfsNumering(t.getTargetState(), visited);
        }
    }

    void appendDot(StringBuilder sbuilder, FullTransition ft) {
        sbuilder.append(" -> ").append(
                "n" + statenumber.get(ft.getTargetState())).append(" [label=\"");


        sbuilder.append(ft.getTransitionLabel());

        sbuilder.append("\"");

        if(ft.isEpsilon()){
            sbuilder.append(",color=red");
        }
        sbuilder.append("];\n");
    }

    @Override
    public AutomatonTrans clone() {

        AutomatonTrans a = new AutomatonTrans();

        HashMap<State, State> m = new HashMap<State, State>();
        Set<State> states = auto.getStates();

        for (State s : states)
            m.put(s, new State());

        for (State s : states) {
            State p = m.get(s);

            assert (p != null);
            p.setAccept(s.isAccept());

            if (s.equals(auto.getInitialState())) {
                auto.setInitialState(p);
                assert auto.getInitialState() != null;
                //LOGGER.info("INITIAL STATE");
            }

            for (Transition t : s.getTransitions()) {
                p.getTransitions().add(new Transition(t.getMin(), t.getMax(), m.get(t.getDest())));
            }

            if (this.statenumber.containsKey(s)) {
                a.statenumber.put(p, this.statenumber.get(s));
            }
        }

        return a;
    }

    public String toDot() {

        StringBuilder sbuilder = new StringBuilder("digraph Automaton {\n");
        sbuilder.append("  rankdir = LR;\n");

        for(State state : states){
            sbuilder.append("  ").append("n" + this.statenumber.get(state));
            if (state.isAccept()) {
                sbuilder.append(" [shape=doublecircle,label=\"" + this.statenumber.get(state) + "\"];\n");
            } else {
                sbuilder.append(" [shape=circle,label=\"" + this.statenumber.get(state) + "\"];\n");
            }

        }

        for(FullTransition ft : transitions){
            sbuilder.append("  n" + this.statenumber.get(ft.getSourceState
                    ()));
            appendDot(sbuilder, ft);
        }

        return sbuilder.append("}\n").toString();
    }

}
