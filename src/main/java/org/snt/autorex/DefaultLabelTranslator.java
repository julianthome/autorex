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

import dk.brics.automaton.Transition;
import org.snt.autorex.utils.EscapeUtils;

/**
 * Created by julian on 12/04/2017.
 */
public class DefaultLabelTranslator implements LabelTranslator {

    @Override
    public String getTransitionString(Transition t) {
        StringBuilder sb = new StringBuilder();

        if (t.getMax() == t.getMin()) {
            sb.append(EscapeUtils.escapeSpecialCharacters(String.valueOf(t.getMin
                    ())));
        } else {
            sb.append("[" + t.getMin() + "-" + t.getMax() + "]");
        }
        return sb.toString();
    }
}
