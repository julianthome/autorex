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
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

        AutomatonTrans at = new AutomatonTrans(s, new DefaultLabelTranslator());
        at.convertToSubstringAutomaton();
        LOGGER.info(at.toDot());
    }

}
