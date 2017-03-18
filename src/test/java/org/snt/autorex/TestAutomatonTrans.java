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
import org.snt.autorex.AutomatonTrans.Kind;

import static org.snt.autorex.AutomatonTrans.Kind.*;


public class TestAutomatonTrans {


    final static Logger LOGGER = LoggerFactory.getLogger(TestAutomatonTrans.class);


    @Test
    public void testKind() {

        Kind k = CAMEL;
        Assert.assertTrue(k.isCamel());
        Assert.assertFalse(k.isNormal());
        Assert.assertFalse(k.isSubstring());
        Assert.assertFalse(k.isSuffix());
        Assert.assertTrue(k.toString().equals("camel"));
        Assert.assertTrue(KindFromString("camel").equals(k));

        k = NORMAL;
        Assert.assertFalse(k.isCamel());
        Assert.assertTrue(k.isNormal());
        Assert.assertFalse(k.isSubstring());
        Assert.assertFalse(k.isSuffix());
        Assert.assertTrue(k.toString().equals("normal"));
        Assert.assertTrue(KindFromString("normal").equals(k));

        k = SUBSTRING;
        Assert.assertFalse(k.isCamel());
        Assert.assertFalse(k.isNormal());
        Assert.assertTrue(k.isSubstring());
        Assert.assertFalse(k.isSuffix());
        Assert.assertTrue(k.toString().equals("substring"));
        Assert.assertTrue(KindFromString("substring").equals(k));

        k = SUFFIX;
        Assert.assertFalse(k.isCamel());
        Assert.assertFalse(k.isNormal());
        Assert.assertFalse(k.isSubstring());
        Assert.assertTrue(k.isSuffix());
        Assert.assertTrue(k.toString().equals("suffix"));
        Assert.assertTrue(KindFromString("suffix").equals(k));

    }


    @Test
    public void testConversion() {
        String s = "hello my name is Alice";

        Automaton a = new RegExp(s).toAutomaton();
        AutomatonTrans substr = new AutomatonTrans(a);
        AutomatonTrans ccas = new AutomatonTrans(a);
        AutomatonTrans sfx = new AutomatonTrans(a);

        LOGGER.debug(sfx.toDot());

        Assert.assertNotNull(substr);
        Assert.assertNotNull(ccas);
        Assert.assertNotNull(sfx);

        substr.convertToSubstringAutomaton();
        ccas.convertToCamelCaseAutomaton();
        sfx.convertToSuffixAutomaton();

        Assert.assertNotNull(substr);
        Assert.assertNotNull(ccas);
        Assert.assertNotNull(sfx);

        Assert.assertNotNull(substr.toDot());
        Assert.assertNotNull(ccas.toDot());
        Assert.assertNotNull(sfx.toDot());

        for( int idx = 0 ; idx < s.length() ; idx++ ) {
            for( int nidx = 1 ; nidx <= s.length() - idx ; nidx++ ) {
                String sub = s.substring(idx, idx+nidx);
                Assert.assertTrue(substr.auto.run(sub));

                if(!s.endsWith(sub)) {
                    LOGGER.debug("sub {}", sub);
                    Assert.assertFalse(sfx.auto.run(sub));
                } else {
                    Assert.assertTrue(sfx.auto.run(sub));
                }
            }
        }

        for( int idx = 0 ; idx < s.length() ; idx++ ) {
            String suf = s.substring(idx);
            Assert.assertTrue(sfx.auto.run(suf));
        }

        Assert.assertTrue(ccas.auto.run(s.toUpperCase()));

        AutomatonTrans csubstr = substr.clone();
        assert(csubstr.equals(csubstr));
    }

}
