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

import dk.brics.automaton.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutomatonTrans extends Automaton {

    final static Logger logger = LoggerFactory.getLogger(AutomatonTrans.class);

    public static enum Kind {

        SUFFIX(1,"suffix"),
        NORMAL(3,"normal"),
        CAMEL(5, "camel"),
        SUBSTRING(7, "substring");

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
        public boolean isSubstring() {return this == SUBSTRING;}


    }

    private Kind kind;
    private Map<State, Integer> statenumber = null;
    private Map<Integer, State> numberstate = null;
    private Map<Integer, HashSet<Integer>> adjacency = null;

    HashMap<State, HashSet<FullTransition>> incoming = null;
    HashMap<State, HashSet<FullTransition>> outgoing = null;
    HashSet<FullTransition> transitions = null;

    private int stateId;

    private static Automaton any = new RegExp(".*").toAutomaton();

    public AutomatonTrans() {
        super();
        this.statenumber = new HashMap<State,Integer>();
        this.numberstate = new HashMap<Integer,State>();
        this.adjacency = new HashMap<Integer, HashSet<Integer>>();
        this.stateId = 0;
        this.incoming = new HashMap<>();
        this.outgoing = new HashMap<>();
        this.kind = Kind.NORMAL;
        this.transitions = new HashSet<FullTransition>();
    }

    public AutomatonTrans(Automaton a) {
        this();
        this.setInitialState(a.clone().getInitialState());
        finalize();
        prepare();
    }

    public AutomatonTrans(String rexp) {
        this(new RegExp(rexp).toAutomaton());
    }

    private void set() {
        for (State s : this.getStates()) {
            s.setAccept(true);
        }
    }

    private void setAccepting() {
        for (State s : this.getStates()) {
            s.setAccept(true);
        }
    }


    private void prepare() {

        // init state has no incomings

        // get all transitions
        for (State s : this.getStates()) {
            for (Transition t : s.getTransitions()) {
                FullTransition ft = new FullTransition(s, t, t.getDest());
                addToIncoming(ft);
                addToOutgoing(ft);
                this.transitions.add(ft);
                addToAdjacency(getNumberOfState(ft.getSourceState()), getNumberOfState(ft.getTargetState()));
            }
        }
    }


    private void addToAdjacency(int src, int dest) {
        if(!this.adjacency.containsKey(src)) {
            this.adjacency.put(src,new HashSet<Integer>());
        }
        this.adjacency.get(src).add(dest);
    }

    private void addToIncoming(FullTransition ft) {
        if (!incoming.containsKey(ft.getTargetState())) {
            incoming.put(ft.getTargetState(), new HashSet<FullTransition>());
        }
        incoming.get(ft.getTargetState()).add(ft);
    }

    private void addToOutgoing(FullTransition ft) {
        if (!outgoing.containsKey(ft.getSourceState())) {
            outgoing.put(ft.getSourceState(), new HashSet<FullTransition>());
        }
        outgoing.get(ft.getSourceState()).add(ft);
    }

    private void setEpsilon() {

        boolean init = this.getInitialState().isAccept();

        Set<StatePair> spairs = new HashSet<StatePair>();
        for (State s : this.getStates()) {
            if (!s.equals(this.getInitialState())) {
                spairs.add(new StatePair(this.getInitialState(), s));
            }
        }
        this.addEpsilons(spairs);
        this.getInitialState().setAccept(init);
    }

    protected void convertToCamelCaseAutomaton() {

        Set<Transition> handled = new HashSet<Transition>();

        for (State s : this.getStates()) {

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

        this.removeDeadTransitions();
        this.determinize();
        this.reduce();
        this.kind = Kind.CAMEL;
        this.prepare();

    }

    protected void convertToSubstringAutomaton() {
        setAccepting();
        setEpsilon();
        this.kind = Kind.SUBSTRING;
        this.finalize();
        this.prepare();
    }

    protected void convertToSuffixAutomaton() {
        setEpsilon();
        this.kind = Kind.SUFFIX;
        this.finalize();
        this.prepare();
    }

    public void finalize() {
        dfsNumering(this.getInitialState());
    }

    private void dfsNumering(State s) {

        if (this.statenumber.containsKey(s))
            return;

        this.stateId++;
        this.statenumber.put(s, this.stateId);
        this.numberstate.put(this.stateId,s);
        for (Transition t : s.getTransitions()) {
            dfsNumering(t.getDest());
        }
    }

    static void appendCharString(char var0, StringBuilder var1) {
        if (var0 >= 33 && var0 <= 126 && var0 != 92 && var0 != 34) {
            var1.append(var0);
        } else {
            var1.append("\\u");
            String var2 = Integer.toHexString(var0);
            if (var0 < 16) {
                var1.append("000").append(var2);
            } else if (var0 < 256) {
                var1.append("00").append(var2);
            } else if (var0 < 4096) {
                var1.append("0").append(var2);
            } else {
                var1.append(var2);
            }
        }

    }

    void appendDot(StringBuilder sbuilder, Transition t) {
        sbuilder.append(" -> ").append(this.statenumber.get(t.getDest())).append(" [label=\"");
        appendCharString(t.getMin(), sbuilder);
        if (t.getMin() != t.getMax()) {
            sbuilder.append("-");
            appendCharString(t.getMax(), sbuilder);
        }

        sbuilder.append("\"]\n");
    }


    @Override
    public AutomatonTrans clone() {

        AutomatonTrans a = new AutomatonTrans();

        HashMap<State, State> m = new HashMap<State, State>();
        Set<State> states = this.getStates();

        for (State s : states)
            m.put(s, new State());

        for (State s : states) {
            State p = m.get(s);

            assert (p != null);
            p.setAccept(s.isAccept());

            if (s.equals(this.getInitialState())) {
                a.setInitialState(p);
                assert (a.getInitialState() != null);
                //logger.info("INITIAL STATE");
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


    @Override
    public String toDot() {

        StringBuilder sbuilder = new StringBuilder("digraph Automaton {\n");
        sbuilder.append("  rankdir = LR;\n");
        Set states = this.getStates();

        Iterator stateIter = states.iterator();

        while (stateIter.hasNext()) {
            State state = (State) stateIter.next();
            sbuilder.append("  ").append(this.statenumber.get(state));
            if (state.isAccept()) {
                sbuilder.append(" [shape=doublecircle,label=\"" + this.statenumber.get(state) + "\"];\n");
            } else {
                sbuilder.append(" [shape=circle,label=\"" + this.statenumber.get(state) + "\"];\n");
            }

            if (state == this.getInitialState()) {
                sbuilder.append("  initial [shape=plaintext,label=\"" + this.statenumber.get(state) + "\"];\n");
                sbuilder.append("  initial -> ").append(this.statenumber.get(state)).append("\n");
            }

            Iterator transIter = state.getTransitions().iterator();

            while (transIter.hasNext()) {
                Transition trans = (Transition) transIter.next();
                sbuilder.append("  ").append(this.statenumber.get(state));
                appendDot(sbuilder, trans);
            }
        }

        return sbuilder.append("}\n").toString();
    }


    public int getNumberOfState(State s) {
        if (this.statenumber.containsKey(s))
            return this.statenumber.get(s);
        else
            return -1;
    }
}
