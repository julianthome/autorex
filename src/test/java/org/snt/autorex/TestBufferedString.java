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

import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.util.BufferedString;


public class TestBufferedString {

    final static Logger LOGGER = LoggerFactory.getLogger(TestBufferedString
            .class);


    @Test
    public void testString() {

        BufferedString bs = new BufferedString();

        bs.append("hello");
        bs.append("world");
        bs.append("2");

        Assert.assertEquals(bs.len(),11);


        BufferedString bs2 = new BufferedString();

        bs2.append("abcd 0000");

        bs2.prepend(bs);

        Assert.assertEquals(bs2.len(),20);


        LOGGER.debug(bs2.toString());

    }

}
