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

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Autorex {

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonTrans.class);

    /**
     * returns the regular expression that represents the semantics
     * of a given automaton a
     * @param a the automaton to convert to a regexp
     * @return the string that represents the regular language accepted by a
     */
    public static String getRegexFromAutomaton(Automaton a) {
        StateEliminator dcom = new StateEliminator(new AutomatonTrans(a));
        return dcom.stateElimination();
    }

    /**
     * converts automaton a to a non case-sensitive automaton
     * @param a an automaton that might be case-sensitie
     * @return the non case-sensitive version of a
     */
    public static Automaton getCamelCaseAutomaton(Automaton a) {
        AutomatonTrans ccas = new AutomatonTrans(a);
        assert(ccas != null);
        ccas.convertToCamelCaseAutomaton();
        return ccas;
    }

    /**
     * returns a non case-sensitive automaton that is based on a
     * @param a an automaton that might be case-sensitive
     * @return the non case-sensitive version of a
     */
    public static Automaton getSubstringAutomaton(Automaton a) {
        AutomatonTrans substr = new AutomatonTrans(a);
        assert(substr != null);
        substr.convertToSubstringAutomaton();
        return substr;
    }

    /**
     * returns an automaton that accepts all the suffixes from a
     * @param a an automaton
     * @return an automaton that accepts all suffix strings from a
     */
    public static Automaton getSuffixAutomaton(Automaton a) {
        AutomatonTrans sfx = new AutomatonTrans(a);
        assert(sfx != null);
        sfx.convertToSuffixAutomaton();
        return sfx;
    }


    public static Set<List<FullTransition>> detectBridges(Automaton a) {

        LOGGER.debug("detect bridges");

        Set<FullTransition> bridges = BridgeDetector.INSTANCE.detect(a);
        Set<FullTransition> grouped = new HashSet<>();

        AutomatonTrans g = new AutomatonTrans(a);

        Set<List<FullTransition>> ret = new HashSet<>();

        for(FullTransition t : bridges) {

            State src = t.getSourceState();
            State dest = t.getTargetState();

            //if(grouped.contains(t))
            //    continue;

            LinkedList<FullTransition> group = new LinkedList<>();
            group.add(t);


            while(g.incoming.containsKey(src) &&
                    g.incoming.get(src).size() == 1) {
                FullTransition trans = g.incoming.get(src).iterator().next();

                LOGGER.debug("next in {}", trans.getLabel());
                grouped.add(trans);
                group.addFirst(g.incoming.get(src).iterator().next());
                src = g.incoming.get(src).iterator().next().getSourceState();
            }

            /**while(g.outgoing.containsKey(dest) &&
                    g.outgoing.get(dest).size() == 1) {
                FullTransition trans = g.outgoing.get(dest).iterator().next();

                LOGGER.debug("trans {}", trans.getLabel());
                if(trans.isEpsilon()) {
                    group.addLast(g.outgoing.get(dest).iterator().next());
                    break;
                }
                LOGGER.debug("next out {}", trans.getLabel());

                grouped.add(trans);
                group.addLast(g.outgoing.get(dest).iterator().next());
                dest = g.outgoing.get(dest).iterator().next().getTargetState();
            }**/

            ret.add(group);
        }

        return ret;
    }




}
