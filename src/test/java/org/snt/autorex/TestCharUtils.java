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

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCharUtils {
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
