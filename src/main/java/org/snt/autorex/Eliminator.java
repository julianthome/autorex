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


        while (a.vertexSet().size() > 2) {

            final State qrip = getQrip(a);

            Set<State> in = a.getConnectedInStates(qrip).stream()
                    .filter(v -> v.getKind() != State.Kind.ACCEPT)
                    .filter(v -> !v.equals(qrip)).collect
                            (Collectors.toSet());

            Set<State> out = a.getConnectedOutStates(qrip).stream()
                    .filter(v -> v.getKind() != State.Kind.START)
                    .filter(v -> !v.equals(qrip)).collect
                            (Collectors.toSet());


            Set<Transition> trans = new HashSet<>();

            for(State qi : in) {
                for (State qj : out) {

                    //LOGGER.debug("qi:{}; gj:{}; qrip:{}", qi.getDotLabel(), qj
                    //        .getDotLabel(), qrip.getDotLabel());

                    StringBuilder lbl = new StringBuilder();

                    if (a.containsEdge(qi, qrip) && a.getEdge(qi, qrip).getLabel().length() > 0) {
                        lbl.append("(");
                        lbl.append(a.getEdge(qi, qrip).getLabel());
                        lbl.append(")");
                    }

                    // make one loop
                    if (a.containsEdge(qrip, qrip) && a.getEdge(qrip, qrip)
                            .getLabel().length() > 0) {
                        lbl.append("(");
                        lbl.append(a.getEdge(qrip, qrip).getLabel());
                        lbl.append(")*");
                    }

                    if (a.containsEdge(qrip, qj) && a.getEdge(qrip, qj)
                            .getLabel().length() > 0) {
                        lbl.append("(");
                        lbl.append(a.getEdge(qrip, qj).getLabel());
                        lbl.append(")");
                    }

                    if (a.containsEdge(qi, qj) && a.getEdge(qi,qj).getLabel()
                            .length() > 0) {
                        if (lbl.length() > 0)
                            lbl.append("|");
                        lbl.append(a.getEdge(qi,qj).getLabel());
                    }


                    //LOGGER.debug("LBL {}", lbl);

                    if (lbl.length() > 0) {
                        trans.add(new Transition(qi, qj, Transition.Kind.MATCH, lbl));
                    }
                }
            }


            a.removeVertex(qrip);

            LOGGER.debug("#states:{}", a.vertexSet().size());

            trans.forEach(t -> {
                if (a.containsEdge(t.getSource(), t.getTarget())) {
                    a.getEdge(t.getSource(), t.getTarget()).setLabel(t.getLabel());
                } else {
                    a.addEdge(t);
                }
            });

            //LOGGER.debug("remove {}", qrip.getDotLabel());
            //LOGGER.debug(a.toDot());
        }


        assert a.edgeSet().size() == 1;


        //LOGGER.debug(a.toDot());

        String ret = a.edgeSet().iterator().next().getLabel().toString();

        //LOGGER.debug("RETURN {}", ret);

        return ret;

    }


}
