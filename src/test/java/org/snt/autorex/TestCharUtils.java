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


public class TestCharUtils {

    final static Logger logger = LoggerFactory.getLogger(TestCharUtils.class);


    @Test
    public void testDigit() {

        for (char c = '0'; c <= '9'; c++) {
            Assert.assertFalse(CharUtils.isLowerCase(c));
            Assert.assertFalse(CharUtils.isUpperCase(c));
            Assert.assertTrue(CharUtils.isDigit(c));
        }

        for (char c = 'a'; c <= 'z'; c++) {
            Assert.assertTrue(CharUtils.isLowerCase(c));
            Assert.assertFalse(CharUtils.isUpperCase(c));
            Assert.assertFalse(CharUtils.isDigit(c));
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            Assert.assertFalse(CharUtils.isLowerCase(c));
            Assert.assertTrue(CharUtils.isUpperCase(c));
            Assert.assertFalse(CharUtils.isDigit(c));
        }
    }

}
