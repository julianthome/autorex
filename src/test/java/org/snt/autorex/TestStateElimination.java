/**
 * autorex - fsm state eliminator
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Julian Thome <julian.thome.de@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/

package org.snt.autorex;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.Transition;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestStateElimination {

    final static Logger LOGGER = LoggerFactory.getLogger(TestStateElimination.class);

    // a custom label translator
    private static class Trans extends DefaultLabelTranslator {
        @Override
        public String getTransitionString(Transition t) {
            if (t.getMin() == Character.MIN_VALUE && t.getMax() == Character.MAX_VALUE){
                return ".";
            }
            return super.getTransitionString(t);
        }
    }

    private enum Op {
        CONCAT,
        UNION,
        ISECT
    }

    private boolean compareRexp(String rexp) {
        RegExp r0 = new RegExp(rexp);
        Automaton a0 = r0.toAutomaton();
        return compareRexpAutomaton(a0);
    }

    private boolean compareRexpAutomaton(Automaton a0) {
        String s0 = Autorex.getRegexFromAutomaton(a0);
        RegExp r0new = new RegExp(s0);
        Automaton a0new = r0new.toAutomaton();
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
        Assert.assertTrue(compareRexp("[4-7]+"));
        Assert.assertTrue(compareRexp("[0-9]&[4-7]+"));
        Assert.assertTrue(compareRexp("(gnt)*") == true);
        Assert.assertTrue(compareRexp("(ab){0,10}c+d") == true);
        Assert.assertTrue(compareRexp("(ab){0,10}c*d") == true);
        Assert.assertTrue(compareRexp("d{0,1}") == true);
        Assert.assertTrue(compareRexp("[a-b]{0,2}c+d{0,1}") == true);
        Assert.assertTrue(compareRexp("((ta)+)*d") == true);
        Assert.assertTrue(compareRexp("((ab)c)+d*") == true);
        Assert.assertTrue(compareRexp("abc(abc)*e") == true);
        Assert.assertTrue(compareRexp("[a-z0-9]+d") == true);
        Assert.assertTrue(compareRexp("([a-z0-9]+de)*") == true);
        Assert.assertTrue(compareRexp("(ab*(bac)*)d+(ay)*") == true);
        Assert.assertTrue(compareRexp(".*") == true);
        Assert.assertTrue(compareRexp("[13d]d*") == true);
        Assert.assertTrue(compareRexp("[a-z]{1,3}test[0-9]+") == true);
        Assert.assertTrue(compareRexp("\\(test\\)") == true);
    }

    @Test
    public void testLabelTranslator() {
        Automaton a = new RegExp("ab.*").toAutomaton();
        String s0 = Autorex.getRegexFromAutomaton(a, new Trans());
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
    public void testTransformation() {
        Automaton a = new RegExp("(abc)+[0-9]{1,3}[dg]*").toAutomaton();
        Automaton b = new RegExp("12345678").toAutomaton();
        Automaton c = new RegExp(".{0,5}").toAutomaton();

        Automaton d = a.union(b).intersection(c);
        String s0 = Autorex.getRegexFromAutomaton(d);
        RegExp r0new = new RegExp(s0);
        Automaton a0new = r0new.toAutomaton();
        Assert.assertTrue(a0new.equals(d));
    }
}
