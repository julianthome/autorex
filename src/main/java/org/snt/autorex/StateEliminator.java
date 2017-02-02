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
import dk.brics.automaton.StatePair;
import dk.brics.automaton.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.util.BufferedString;

import java.util.*;

public class StateEliminator {


    final static Logger LOGGER = LoggerFactory.getLogger(StateEliminator.class);


    private static Character [] sarray = new Character[] {'+', '{', '}', '(', ')', '[', ']', '&', '^', '-', '?', '*','\"','$', '<', '>', '.', '|' };
    private static Set<Character> special = new HashSet<>(Arrays.asList(sarray));

    // Book keeping data structures
    HashMap<State, HashSet<FullTransition>> incoming = null;
    HashMap<State, HashSet<FullTransition>> outgoing = null;
    HashMap<StatePair, FullTransition> transitions = null;
    HashSet<State> states = null;



    private AutomatonTrans a = null;
    private FullTransition init = null;
    private State finish = null;
    private State start = null;
    private Set<FullTransition> end = null;

    public StateEliminator(AutomatonTrans a) {
        this.a = a;
        this.a.finalize();
        this.finish = new State();
        this.start = new State();
        this.incoming = new HashMap<>();
        this.outgoing = new HashMap<>();
        this.states = new HashSet<State>();
        this.init = new FullTransition(this.start, new Transition(' ', this
                .a.init), this.a.init);
        this.init.setIsEpsilon(true);
        this.transitions = new HashMap<>();
        this.end = new HashSet<FullTransition>();
    }


    /**
     * Construct the GNFA
     */
    private void prepare() {

        // init state has no incomings
        incoming.put(this.init.getSourceState(), new HashSet<>());
        outgoing.put(this.init.getSourceState(), new HashSet<>());
        states.add(this.init.getSourceState());
        transitions.put(new StatePair(this.init.getSourceState(), this.init.getTargetState()), this.init);

        // get all transitions
        for (State s : this.a.auto.getStates()) {

            states.add(s);
            for (Transition t : s.getTransitions()) {

                // merge transition with same source and target states
                if (this.transitions.containsKey(new StatePair(s, t.getDest()))) {
                    FullTransition ft = this.transitions.get(new StatePair(s, t.getDest()));
                    ft.addTransition(t);
                } else {
                    FullTransition ft = new FullTransition(s, t, t.getDest());
                    this.transitions.put(new StatePair(s, t.getDest()), ft);
                    addToIncoming(ft);
                    addToOutgoing(ft);
                }
            }

            assert (this.a.auto.getInitialState() != null);
        }
        addToIncoming(this.init);
        addToOutgoing(this.init);

        for (State accept : this.a.auto.getAcceptStates()) {
            FullTransition newFinalTransition =
                    new FullTransition(accept, new Transition(' ', accept), this.finish);
            newFinalTransition.setIsEpsilon(true);
            newFinalTransition.setLabel(new BufferedString(
                    ("\u0000.\u0000{0\u0000}")));
            this.end.add(newFinalTransition);
            addToIncoming(newFinalTransition);
            addToOutgoing(newFinalTransition);
            this.transitions.put(new StatePair(accept, finish), newFinalTransition);
        }
        this.states.add(this.finish);
    }


    private boolean eliminate(State src, State s, State dest, Set<FullTransition> toDel, Set<FullTransition> toAdd) {



        //LOGGER.info("eliminate " + getBufferedStringForState(src) + " " + getBufferedStringForState(s) + " " + getBufferedStringForState(dest));

        StatePair loop = new StatePair(s,s);

        StatePair src2slink = new StatePair(src,s);
        StatePair s2destlink = new StatePair(s,dest);
        StatePair s2srclink = new StatePair(s,src);
        StatePair dest2slink = new StatePair(dest,s);
        StatePair src2destlink = new StatePair(src,dest);
        StatePair dest2srclink = new StatePair(dest,src);


        FullTransition s2src = null;
        FullTransition src2s = null;
        FullTransition dest2s = null;
        FullTransition s2dest = null;
        FullTransition sloop = null;
        FullTransition src2dest = null;
        FullTransition dest2src = null;

        BufferedString loopLabel = new BufferedString();
        BufferedString s2srcLabel = new BufferedString();
        BufferedString dest2destLabel = new BufferedString();
        BufferedString src2srcLabel = new BufferedString();
        BufferedString src2destLabel = new BufferedString();
        BufferedString dest2srcLabel = new BufferedString();
        BufferedString src2sLabel = new BufferedString();
        BufferedString dest2sLabel = new BufferedString();
        BufferedString s2destLabel = new BufferedString();


        if(this.transitions.containsKey(loop)) {
            sloop = this.transitions.get(loop);
            loopLabel.append("\u0000(");
            loopLabel.append(sloop.getLabel());
            loopLabel.append("\u0000)\u0000*");
            toDel.add(sloop);
        }

        if(this.transitions.containsKey(src2slink)) {
            src2s = this.transitions.get(src2slink);
            src2sLabel = src2s.getLabel();
            if(src2s.isEpsilon() && src2s.getLabel().toString().trim().length
                    () == 0) {
                src2sLabel.clear();
            }
            toDel.add(src2s);
        }

        if(this.transitions.containsKey(s2destlink)) {
            s2dest = this.transitions.get(s2destlink);
            s2destLabel.append(s2dest.getLabel());
            toDel.add(s2dest);
        }

        if(this.transitions.containsKey(dest2slink)) {
            dest2s = this.transitions.get(dest2slink);
            dest2sLabel.append(dest2s.getLabel());
            toDel.add(dest2s);
        }

        // Backlin from s to src
        if(this.transitions.containsKey(s2srclink)) {
            s2src = this.transitions.get(s2srclink);
            s2srcLabel.append(s2src.getLabel());
            toDel.add(s2src);
        }

        if(this.transitions.containsKey(src2destlink)) {
            src2dest = this.transitions.get(src2destlink);
            src2destLabel.append("\u0000|");
            src2destLabel.append(src2dest.getLabel());
        }

        if(this.transitions.containsKey(dest2srclink)) {
            dest2src = this.transitions.get(dest2srclink);
            dest2srcLabel.append("\u0000|");
            dest2srcLabel.append(dest2src.getLabel());
        }


        BufferedString pfx = new BufferedString();

        //pfx.append("\u0000(");
        pfx.append(src2sLabel);
        pfx.append(loopLabel);
        pfx.append(s2destLabel);
        src2destLabel.prepend(pfx);
        //src2destLabel.append("\u0000)");

        pfx.clear();
        //pfx.append("\u0000(");
        pfx.append(dest2sLabel);
        pfx.append(loopLabel);
        pfx.append(s2srcLabel);
        dest2srcLabel.prepend(pfx);
        //dest2srcLabel.append("\u0000)");

        // just required if loops s <-> dest or s <-> src
        src2srcLabel.append(src2sLabel);
        src2srcLabel.append(loopLabel);
        src2srcLabel.append(s2srcLabel);

        dest2destLabel.append(dest2sLabel);
        dest2destLabel.append(loopLabel);
        dest2destLabel.append(s2destLabel);

        if(src2s != null && s2dest != null) {
            FullTransition newSrc2Dest = new FullTransition(src, dest);
            newSrc2Dest.setLabel(src2destLabel);
            toAdd.add(newSrc2Dest);
        }


        if(dest2s != null && s2src != null) {
            FullTransition newDest2Src = new FullTransition(dest, src);
            newDest2Src.setLabel(dest2srcLabel);
            toAdd.add(newDest2Src);
        }

        if(s2dest != null && dest2s != null) {
            FullTransition newDest2Dest = new FullTransition(dest, dest);
            newDest2Dest.setLabel(dest2destLabel);
            toAdd.add(newDest2Dest);
        }

        if(src2s != null && s2src != null) {
            FullTransition newSrc2Src = new FullTransition(src, src);
            newSrc2Src.setLabel(src2srcLabel);
            toAdd.add(newSrc2Src);
        }

        return true;
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

    private void addToTransition(FullTransition ft) {
        //LOGGER.info("ADD " + getBufferedStringForTransition(ft));

        FullTransition param = ft;

        StatePair key = new StatePair(ft.getSourceState(), ft.getTargetState());

        // instantly merge rules to ensure that two states can only be connected with one transition
        // note that this is an input parameter !
        if(this.transitions.containsKey(key)) {
            param = this.transitions.remove(key);
            //LOGGER.info("ALREADY CONTAINED EPSILON " + param.isEpsilon() + " " + param.getLabel());
            BufferedString par = new BufferedString();
            if(!param.isEpsilon()) {
                par.append("\u0000|");
                par.append(param.getLabel());
            }

            ft.getLabel().prepend("\u0000(");
            ft.getLabel().append(par);
            ft.getLabel().append("\u0000)");
        }
        this.transitions.put(key, ft);
    }


    private boolean eliminate(State s) {

        if(!this.incoming.containsKey(s) || !this.outgoing.containsKey(s)) {
            //LOGGER.info("cannot handle " + getBufferedStringForState(s));
            return false;
        }

        //LOGGER.info("SPECIAL  " + getBufferedStringForState(s));

        Set<FullTransition> incoming = new HashSet<>();
        Set<FullTransition> outgoing = new HashSet<>();
        Set<FullTransition> toAdd = new HashSet<>();
        Set<FullTransition> toDel = new HashSet<>();


        incoming.addAll(this.incoming.get(s));
        outgoing.addAll(this.outgoing.get(s));

        for(FullTransition in : incoming) {
            State src = in.getSourceState();
            for(FullTransition out : outgoing) {
                State dest = out.getTargetState();

                // ignore loops
                if(src.equals(s) || s.equals(dest))
                    continue;

                eliminate(src,s,dest,toDel,toAdd);
            }
        }

        toDel.forEach(x -> clearTransition(x));
        toAdd.forEach(x -> addLink(x));

        this.states.remove(s);
        this.transitions.remove(s);
        this.outgoing.remove(s);
        this.incoming.remove(s);

        return true;
    }

    private void addLink( FullTransition ft ){
        //LOGGER.info("addAll link " + getBufferedStringForTransition(ft));
        addToOutgoing(ft);
        addToIncoming(ft);
        addToTransition(ft);
    }

    private void clearTransition(FullTransition ft) {
        clearTransition(ft.getSourceState(), ft.getTargetState());
    }

    private void clearTransition(State src, State dest) {

        //LOGGER.info("clear transition " + getBufferedStringForState(src) + " " + getBufferedStringForState(dest));
        assert(src != null);
        assert(dest != null);

        assert(this.outgoing.containsKey(src));
        assert(this.incoming.containsKey(dest));
        assert(this.transitions.containsKey(new StatePair(src,dest)));


        FullTransition trans = this.transitions.remove(new StatePair(src, dest));

        assert(trans != null);

        if(trans != null) {
            //LOGGER.info("RM TRANS " + getBufferedStringForTransition(trans));
            this.incoming.get(dest).remove(trans);
            this.outgoing.get(src).remove(trans);
        }

        if(this.incoming.get(dest).isEmpty()) {
            this.incoming.remove(dest);
        }
        if(this.outgoing.get(src).isEmpty()) {
            this.outgoing.remove(src);
        }

        if(!this.outgoing.containsKey(src) && !this.incoming.containsKey(src)) {
            this.states.remove(src);
        }
        if(!this.outgoing.containsKey(dest) && !this.incoming.containsKey(dest)) {
            this.states.remove(dest);
        }

        if(!this.states.contains(src) && !this.states.contains(dest)) {
            this.transitions.remove(new StatePair(src,dest));
        }


    }


    public String stateElimination() {


        if(this.a.auto.isTotal()) {
            return ".*";
        }

        prepare();

        LinkedList<State> worklist = new LinkedList<State>();
        worklist.addAll(this.states);
        while(!worklist.isEmpty()) {

            State s = worklist.pop();

            if (!s.equals(this.finish) && !s.equals(this.start)) {
                eliminate(s);
            }

            LOGGER.debug("#states: {}", states.size());

        }

        return escapeSpecialCharacters(getRexpBufferedString()).toString();
    }


    public BufferedString getRexpBufferedString() {

        BufferedString sb = new BufferedString();
        for (FullTransition t : this.transitions.values()) {
            sb.append(t.getLabel());
        }

        return sb;
    }

    private BufferedString escapeSpecialCharacters(BufferedString s) {
        StringBuilder out = new StringBuilder();
        char pred = ' ';
        for(char c : s.toString().toCharArray()) {
            if(out.length() > 0) {
                if (pred != '\u0000' && special.contains(c)) {
                    out.append("\\" + c);
                } else if (pred == '\u0000' && special.contains(c)) {
                    out.deleteCharAt(out.length() - 1); // delete NULL
                    out.append(c);
                } else {
                    out.append(c);
                }
            } else {
                out.append(c);
            }
            pred = c;
        }
        return new BufferedString(out);
    }



}