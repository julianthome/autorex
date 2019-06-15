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

package org.snt.autorex.autograph;

import java.math.BigInteger;

public class State implements Cloneable {
    public enum Kind {
        NORMAL,
        START,
        ACCEPT
    }

    private Kind kind = Kind.NORMAL;

    private static BigInteger cnt = new BigInteger("0");

    private BigInteger id = new BigInteger("0");

    public State(State s) {
        this.kind = s.kind;
        this.id = s.id;
    }

    public State(Kind kind) {
        cnt = cnt.add(new BigInteger("1"));
        id = cnt;
        this.kind = kind;
    }

    private State() {
        this(Kind.NORMAL);
    }

    public State clone() {
        return new State(this);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof State))
            return false;

        State s = (State)o;

        return s.kind == this.kind && s.id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public BigInteger getId() {
        return this.id;
    }

    public String getDotLabel() {
        return "n" + id;
    }

    public Kind getKind() {
        return kind;
    }

}