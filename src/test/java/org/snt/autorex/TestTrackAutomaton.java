/**
 * autoMate - yet another automaton library for Java
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Julian Thome <julian.thome.de@gmail.com>
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


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.trackauto.TrackAutomaton;

public class TestTrackAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestTrackAutomaton.class);


    @Test
    public void getAutomatonCheck() {
        TrackAutomaton a = new TrackAutomaton("a");
        TrackAutomaton c = new TrackAutomaton("c");


        TrackAutomaton x = new TrackAutomaton("x", "x");
        TrackAutomaton minus = new TrackAutomaton("-", "\\-");
        TrackAutomaton b = new TrackAutomaton("b", "b");

        TrackAutomaton seven = new TrackAutomaton("seven", ".{7,}");


        TrackAutomaton lencplus7 = c.concatenate(seven);

        TrackAutomaton anlena = lencplus7.intersect(a);




        TrackAutomaton concatmb = minus.concatenate(b);
        TrackAutomaton concatcx = c.concatenate(x);
        TrackAutomaton concatamb = anlena.concatenate(concatmb);


        TrackAutomaton f = concatamb.intersect(concatcx);

        LOGGER.debug(f.toDot());




    }





}


