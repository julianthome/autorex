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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
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
        return ccas.auto;
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
        return substr.auto;
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
        return sfx.auto;
    }

    /**
     * returns all the bridges, i.e. all the concrete strings that are contained
     * within an automaton
     * @param a the automaton to check for bridges
     * @return set of bridges (as strings)
     */
    public static Set<String> detectBridges(Automaton a) {
        AutomatonTrans br = new AutomatonTrans(a);
        Set<FullTransition> bridges = BridgeDetector.INSTANCE.detectBridges(br);

        Set<String> ret = new HashSet<>();

        for(FullTransition t : bridges) {
            ret.add(t.getCarry().toString());
        }

        return ret;
    }




}
