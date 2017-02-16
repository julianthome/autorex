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
import dk.brics.automaton.RegExp;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;
import org.snt.autorex.autograph.State;
import org.snt.autorex.autograph.Transition;


public class TestClassification {

    final static Logger LOGGER = LoggerFactory.getLogger(TestClassification.class);

    @Test
    public void testClassfication() {
        Automaton a = new RegExp("[a-z]{1,3}test[0-9]+").toAutomaton();

        Gnfa g = Converter.INSTANCE.getGnfaFromAutomaton(a);

        StateEliminator.INSTANCE.handleTrivialCases(g);


        TopologicalOrderIterator<State, Transition> topOrder =
                new TopologicalOrderIterator(g);

        while (topOrder.hasNext()) {
            State next = topOrder.next();
            LOGGER.debug(next.getDotLabel());
        }

        Classifier.INSTANCE.classify(g);
        LOGGER.debug(g.toDot());
    }

}
