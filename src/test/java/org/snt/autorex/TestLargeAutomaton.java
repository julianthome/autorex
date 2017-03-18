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
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestLargeAutomaton {

    final static Logger LOGGER = LoggerFactory.getLogger(TestLargeAutomaton.class);

    private static Map<String, State> smap = new HashMap();
    private static boolean SKIP = true;

    @Test
    public void testLargeAutomaton() {

        if(SKIP)
            return;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testcase.syntax")
                .getFile());

        FileReader fileReader = null;
        String line = "";

        Pattern p = Pattern.compile("^(s[0-9]+)\\:a([0-9]+)\\>(" +
                "s[0-9]+)$");

        Automaton a = new Automaton();

        try {
            fileReader =
                    new FileReader(file);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            System.exit(-1);
        }


        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader =
                new BufferedReader(fileReader);

        try {
            while((line = bufferedReader.readLine()) != null) {
                if(line.matches("^s[0-9]+$")) {

                    if(line.equals("s0"))
                        smap.put(line, a.getInitialState());
                    else
                        smap.put(line, new State());
                    LOGGER.debug(line);
                }

                Matcher m = p.matcher(line);
                if(m.matches()) {
                    String src = m.group(1);
                    String lbl = m.group(2);
                    String trt = m.group(3);

                    assert smap.containsKey(src);
                    assert smap.containsKey(trt);

                    State ssrc = smap.get(src);

                    ssrc.addTransition(new Transition((char)Integer.parseInt
                            (lbl),smap.get(trt)));

                    LOGGER.debug("{}:{}:{}", src,lbl,trt);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            System.exit(-1);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        LOGGER.debug("S" + a.getStates().size());
        LOGGER.debug("S" + a.getNumberOfTransitions());
        //String result = Autorex.getRegexFromAutomaton(a);

        //LOGGER.debug(result);


    }


}
