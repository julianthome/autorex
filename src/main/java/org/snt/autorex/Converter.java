package org.snt.autorex;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;
import org.snt.autorex.autograph.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Converter {

    INSTANCE;
    final static Logger LOGGER = LoggerFactory.getLogger(Converter.class);

    public Gnfa getGnfaFromAutomaton(Automaton auto) {

        LOGGER.debug("ORIG ==== ===== ==== ===");
        LOGGER.debug(auto.toDot());
        LOGGER.debug("ORIG ==== ===== ==== ===");
        Gnfa ag = new Gnfa();
        AutomatonTrans at = new AutomatonTrans(auto);

        Map<State,org.snt.autorex.autograph.State> smap = new HashMap<>();
        Set<org.snt.autorex.autograph.State> accepting = new HashSet<>();
        Set<org.snt.autorex.autograph.State> start = new HashSet<>();


        org.snt.autorex.autograph.State end =  new org.snt.autorex.autograph
                .State(org.snt.autorex
                .autograph.State.Kind.ACCEPT);

        org.snt.autorex.autograph.State init =  new org.snt.autorex.autograph
                .State(org.snt.autorex
                .autograph.State.Kind.START);

        ag.addVertex(end);
        ag.addVertex(init);


        for(FullTransition ft : at.transitions){
            State src = ft.getSourceState();
            State dst = ft.getTargetState();

            org.snt.autorex.autograph.State arxsrc = getStateFrom(start,
                    accepting, smap, at, src);
            org.snt.autorex.autograph.State arxdst = getStateFrom(start,
                    accepting, smap, at, dst);

            Transition t = null;
            if(ag.containsEdge(arxsrc, arxdst)) {
                t = ag.getEdge(arxsrc, arxdst);
                t.getLabel().append("|");
                t.getLabel().append(ft
                        .getTransitionLabel());
            } else {
                t = new Transition(arxsrc, arxdst, Transition.Kind
                        .MATCH,ft
                        .getTransitionLabel());
            }

            ag.addVertex(arxdst);
            ag.addVertex(arxsrc);
            ag.addEdge(t);
        }

        accepting.forEach(
            a -> {
                Transition t = new Transition(a,end,Transition.Kind
                        .EPSILON);
                ag.addEdge(t);
            }
        );

        start.forEach(
                a -> {
                    Transition t = new Transition(init,a, Transition.Kind
                            .EPSILON);
                    ag.addEdge(t);
                }
        );

        return ag;
    }


    private org.snt.autorex.autograph.State getStateFrom
            (Set<org.snt.autorex.autograph.State> start,
             Set<org.snt.autorex.autograph.State> accepting,
             Map<State,org.snt.autorex.autograph.State> smap,
             AutomatonTrans a, State s) {

        if(smap.containsKey(s))
            return smap.get(s);

        org.snt.autorex.autograph.State ret = new org.snt.autorex.autograph
                .State(org.snt.autorex
                .autograph.State.Kind.NORMAL);


        if(s.isAccept()) {
            accepting.add(ret);
        }

        if(s.equals(a.init)) {
            start.add(ret);
        }

        smap.put(s,ret);

        return ret;
    }

}
