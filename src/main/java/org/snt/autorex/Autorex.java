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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;

public class Autorex {

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonTrans.class);

    /**
     * returns the regular expression that represents the semantics
     * of a given automaton a
     * @param a the automaton to convert to a regexp
     * @return the string that represents the regular language accepted by a
     */
    public static String getRegexFromAutomaton(Automaton a) {
        Gnfa gnfa = Converter.INSTANCE.getGnfaFromAutomaton(a);
        return StateEliminator.INSTANCE.eliminate(gnfa);
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


}
