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
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class TestAutorex {

    final static Logger LOGGER = LoggerFactory.getLogger(TestAutorex.class);


    @Test
    public void testConversion() {
        String s = "hello my name is Alice";

        Automaton a = new RegExp(s).toAutomaton();

        Automaton substr = Autorex.getSubstringAutomaton(a);
        Automaton sfx = Autorex.getSuffixAutomaton(a);
        Automaton ccas = Autorex.getCamelCaseAutomaton(a);

        Assert.assertNotNull(substr);
        Assert.assertNotNull(ccas);
        Assert.assertNotNull(sfx);

        Assert.assertNotNull(substr.toDot());
        Assert.assertNotNull(ccas.toDot());
        Assert.assertNotNull(sfx.toDot());

        for( int idx = 0 ; idx < s.length() ; idx++ ) {
            for( int nidx = 1 ; nidx <= s.length() - idx ; nidx++ ) {
                String sub = s.substring(idx, idx+nidx);
                Assert.assertTrue(substr.run(sub));

                if(!s.endsWith(sub)) {
                    Assert.assertFalse(sfx.run(sub));
                } else {
                    Assert.assertTrue(sfx.run(sub));
                }
            }
        }

        for( int idx = 0 ; idx < s.length() ; idx++ ) {
            String suf = s.substring(idx);
            Assert.assertTrue(sfx.run(suf));
        }

        Assert.assertTrue(ccas.run(s.toUpperCase()));

    }

    @Test
    public void testConcreteSubstring() {
        String s = "a+ hello .* s+";

        AutomatonTrans at = new AutomatonTrans(s);

        at.convertToSubstringAutomaton();
        LOGGER.info(at.toDot());

    }

    @Test
    public void testBridgeDetector() {
        String s = "a .* hello .*k";

        Automaton a = new RegExp(s).toAutomaton();
        Set<List<FullTransition>> ret = Autorex.detectBridges(a);

        for(List<FullTransition> l : ret) {
            String st = "";
            for(FullTransition t : l) {
                st += t.getLabel();
            }
            LOGGER.debug("STR {}", st);
        }
    }

}
