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
import org.snt.autorex.autograph.Gnfa;


public class TestStateElimination {

    final static Logger LOGGER = LoggerFactory.getLogger(TestStateElimination.class);


    private enum Op {
        CONCAT,
        UNION,
        ISECT
    };

    private boolean compareRexp(String rexp) {
        LOGGER.debug("TEST " + rexp);
        RegExp r0 = new RegExp(rexp);
        Automaton a0 = r0.toAutomaton();
        return compareRexpAutomaton(a0);
    }

    private boolean compareRexpAutomaton(Automaton a0) {
        LOGGER.debug("old Automaton:" + a0.toDot());
        LOGGER.debug("is det" + a0.isDeterministic());
        String s0 = Autorex.getRegexFromAutomaton(a0);
        LOGGER.debug("Regexp 1 " + s0);
        RegExp r0new = new RegExp(s0);
        Automaton a0new = r0new.toAutomaton();
        LOGGER.debug("new Automaton:" + a0new.toDot());
        return a0new.equals(a0);
    }

    private boolean modAutomata(String rexp1, String rexp2, Op op) {
        Automaton a = new RegExp(rexp1).toAutomaton();
        Automaton b = new RegExp(rexp2).toAutomaton();
        Automaton c = null;
        switch(op) {
            case CONCAT:
                c = a.concatenate(b);
                break;
            case UNION:
                c = a.union(b);
                break;
            case ISECT:
                c = a.intersection(b);
                break;
        }
        Assert.assertNotNull(c);
        return compareRexpAutomaton(c);
    }

    @Test
    public void testGetRegexpFromAutomaton() {
        Assert.assertTrue(compareRexp("aa+"));
        /**Assert.assertTrue(compareRexp("[4-7]+"));
        Assert.assertTrue(compareRexp("[0-9]&[4-7]+"));
        Assert.assertTrue(compareRexp("(gnt)*") == true);
        Assert.assertTrue(compareRexp("(ab){0,10}c+d") == true);
        Assert.assertTrue(compareRexp("(ab){0,10}c*d") == true);
        Assert.assertTrue(compareRexp("[a-b]{0,2}c+d{0,1}") == true);
        Assert.assertTrue(compareRexp("((ta)+)*d") == true);**/
        //Assert.assertTrue(compareRexp("((ab)c)+d*") == true);
        //Assert.assertTrue(compareRexp("abc(abc)*e") == true);
        //Assert.assertTrue(compareRexp("[a-z0-9]+d") == true);
        //Assert.assertTrue(compareRexp("([a-z0-9]+de)*") == true);
        //Assert.assertTrue(compareRexp("(ab*(bac)*)d+(ay)*") == true);
        //Assert.assertTrue(compareRexp(".*") == true);
        //Assert.assertTrue(compareRexp("[13d]d*") == true);
        //Assert.assertTrue(compareRexp("[a-z]{1,3}test[0-9]+") == true);
    }

    @Test
    public void testgetRegexpForModAutomaton() {
        Assert.assertTrue(modAutomata("aa+", "aa+", Op.CONCAT));
        Assert.assertTrue(modAutomata("aa+", "aa+", Op.UNION));
        Assert.assertTrue(modAutomata("aa+", "aa+", Op.ISECT));
        Assert.assertTrue(modAutomata("[a-z]{1,3}test[0-9]+", "abcabtest", Op.CONCAT));
        Assert.assertTrue(modAutomata("[a-z]{1,3}test[0-9]+", "abcabtest", Op.UNION));
        Assert.assertTrue(modAutomata("[a-z]{1,3}test[0-9]+", "abctest[0-6]{2,3}", Op.ISECT));
    }

    @Test
    public void testSimple() {
        Automaton a = new RegExp("[a-z]{1,3}test[0-9]+").toAutomaton();
        String regex = Autorex.getRegexFromAutomaton(a);
        //LOGGER.debug(regex.toString());
        Assert.assertTrue(new RegExp(regex).toAutomaton().equals(a));
    }


    @Test
    public void testCheck() {

        Automaton a = new RegExp("aa+").toAutomaton();
        Gnfa ag = Converter.INSTANCE.getGnfaFromAutomaton(a);

        Eliminator.INSTANCE.eliminate(ag);

        LOGGER.debug("{}", ag.toDot());
    }



}
