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


    @Test
    public void testLargeAutomaton() {

        if(true)
            return;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testcase.syntax")
                .getFile());

        FileReader fileReader = null;
        String line = "";

        Pattern p = Pattern.compile("^(s[0-9]+)\\:a([0-9]+)\\>(" +
                "s[0-9]+)$");

        Automaton a = new Automaton();

        char x;

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


        String result = Autorex.getRegexFromAutomaton(a);

        LOGGER.debug(result);


    }


}
