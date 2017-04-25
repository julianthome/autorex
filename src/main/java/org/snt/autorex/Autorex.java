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
     * @param ltrans a label translator
     * @return the string that represents the regular language accepted by a
     */
    public static String getRegexFromAutomaton(Automaton a, LabelTranslator
            ltrans) {
        Gnfa gnfa = Converter.INSTANCE.getGnfaFromAutomaton(a, ltrans);
        return StateEliminator.INSTANCE.eliminate(gnfa);
    }

    public static String getRegexFromAutomaton(Automaton a) {
        return getRegexFromAutomaton(a, new DefaultLabelTranslator());
    }

    /**
     * converts automaton a to a non case-sensitive automaton
     * @param a an automaton that might be case-sensitie
     * @param ltrans a label translator
     * @return the non case-sensitive version of a
     */
    public static Automaton getCamelCaseAutomaton(Automaton a,
                                                  LabelTranslator ltrans) {
        AutomatonTrans ccas = new AutomatonTrans(a, ltrans);
        assert(ccas != null);
        ccas.convertToCamelCaseAutomaton();
        return ccas.auto;
    }

    public static Automaton getCamelCaseAutomaton(Automaton a) {
        return getCamelCaseAutomaton(a, new DefaultLabelTranslator());
    }

    /**
     * returns the length automaton for a given automaton
     * @param a
     * @param ltrans
     * @return
     */
    public static Automaton getLenAutomaton(Automaton a,
                                                  LabelTranslator ltrans) {
        AutomatonTrans ccas = new AutomatonTrans(a, ltrans);
        assert(ccas != null);
        ccas.convertToLenAutomaton();
        return ccas.auto;
    }

    public static Automaton getLenAutomaton(Automaton a) {
        return getLenAutomaton(a, new DefaultLabelTranslator());
    }


    /**
     * returns a non case-sensitive automaton that is based on a
     * @param a an automaton that might be case-sensitive
     * @param ltrans a label translator
     * @return the non case-sensitive version of a
     */
    public static Automaton getSubstringAutomaton(Automaton a,
                                                  LabelTranslator ltrans) {
        AutomatonTrans substr = new AutomatonTrans(a, ltrans);
        assert(substr != null);
        substr.convertToSubstringAutomaton();
        return substr.auto;
    }

    public static Automaton getSubstringAutomaton(Automaton a) {
        return getSubstringAutomaton(a, new DefaultLabelTranslator());
    }

    /**
     * returns an automaton that accepts all the suffixes from a
     * @param a an automaton
     * @return an automaton that accepts all suffix strings from a
     */
    public static Automaton getSuffixAutomaton(Automaton a, LabelTranslator
            ltrans) {
        AutomatonTrans sfx = new AutomatonTrans(a, ltrans);
        assert(sfx != null);
        sfx.convertToSuffixAutomaton();
        return sfx.auto;
    }

    public static Automaton getSuffixAutomaton(Automaton a) {
        return getSuffixAutomaton(a, new DefaultLabelTranslator());
    }

}
