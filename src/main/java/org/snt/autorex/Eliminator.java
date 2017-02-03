package org.snt.autorex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;
import org.snt.autorex.autograph.State;
import org.snt.autorex.autograph.Transition;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public enum Eliminator {

    INSTANCE;


    final static Logger LOGGER = LoggerFactory.getLogger(Eliminator.class);

    private State getQrip(Gnfa a) {
        try {
            return a.vertexSet().stream().filter(s -> s.getKind() != State.Kind
                    .START && s.getKind() != State.Kind.ACCEPT).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public String eliminate(Gnfa a) {

        LOGGER.debug(a.toDot());

        while (a.vertexSet().size() > 2) {

            final State qrip = getQrip(a);

            LOGGER.debug("QRIP {}", qrip.getDotLabel());

            Set<State> i = a.vertexSet().stream().filter(q ->
                    !q.equals(qrip) && q.getKind() != State.Kind.ACCEPT).collect
                    (Collectors.toSet());

            Set<State> j = a.vertexSet().stream().filter(q ->
                    !q.equals(qrip) && q.getKind() != State.Kind.START).collect
                    (Collectors.toSet());


            Set<Transition> trans = new HashSet<>();

            for (State qi : i) {
                for (State qj : j) {
                    LOGGER.debug("LOOKING at {} {}", qi.getDotLabel(), qj.getDotLabel());

                    StringBuilder lbl = new StringBuilder();

                    if (a.containsEdge(qi, qrip) && a.getEdge(qi, qrip).getLabel().length() > 0) {
                        lbl.append("(");
                        lbl.append(a.getEdge(qi, qrip).getLabel());
                        lbl.append(")");
                    }

                    // make one loop
                    if (a.containsEdge(qrip, qrip) && a.getEdge(qrip,qrip)
                            .getLabel().length() > 0) {
                        lbl.append("(");
                        lbl.append(a.getEdge(qrip,qrip).getLabel());
                        lbl.append(")*");
                    }

                    if (a.containsEdge(qrip, qj) && a.getEdge(qrip, qj)
                            .getLabel().length() > 0) {
                        lbl.append("(");
                        lbl.append(a.getEdge(qrip, qj).getLabel());
                        lbl.append(")");
                    }

                    if (a.containsEdge(qi, qj) && a.getEdge(qi, qj).getLabel()
                            .length() > 0) {
                        if(lbl.length() > 0)
                            lbl.append("|");
                        lbl.append(a.getEdge(qi, qj).getLabel());
                    }


                    LOGGER.debug("LBL {}", lbl);

                    if (lbl.length() > 0) {
                        trans.add(new Transition(qi, qj, Transition.Kind.MATCH, lbl));
                    }
                }
            }

            a.removeVertex(qrip);

            trans.forEach(t -> {
                if (a.containsEdge(t.getSource(), t.getTarget())) {
                    a.getEdge(t.getSource(), t.getTarget()).setLabel(t.getLabel());
                } else {
                    a.addEdge(t);
                }
            });

            LOGGER.debug("remove {}", qrip.getDotLabel());
            LOGGER.debug(a.toDot());
        }


        assert a.edgeSet().size() == 1;


        LOGGER.debug(a.toDot());

        String ret = a.edgeSet().iterator().next().getLabel().toString();

        LOGGER.debug("RETURN {}", ret);

        return ret;

    }


}