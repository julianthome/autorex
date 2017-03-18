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


public class Transition implements Cloneable {


    public enum Kind {
        EPSILON,
        MATCH
    }

    /**
     * Properties used when doing the DFS
     * edge classification
     */
    public enum Property {
        NORMAL,
        BACK,
        FWD,
        CROSS,
        TREE;
    }

    private Kind kind = Kind.MATCH;
    private Property prop = Property.NORMAL;

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Property getProp() {
        return prop;
    }

    public void setProp(Property prop) {
        this.prop = prop;
    }

    private StringBuilder m = new StringBuilder();
    private State src = null;
    private State dst = null;


    public Transition(State src, State dst, Kind k, StringBuilder m) {
        this(src,dst,k);
        this.m = m;
    }


    public Transition(State src, State dst, Kind k, String s) {
        this(src,dst,k);
        this.m.append(s);
    }

    public Transition(State src, State dst, Kind k) {
        this.src = src;
        this.dst = dst;
        this.kind = k;

        if(k == Kind.EPSILON)
            this.m = new StringBuilder(".{0}");
    }

    public Kind getKind() {
        return kind;
    }

    public State getSource() {
        return this.src;
    }

    public State getTarget() {
        return this.dst;
    }

    public void setSource(State src) {
        this.src = src;
    }

    public void setTarget(State dst) {
        this.dst = dst;
    }

    public boolean contains(State s) {
        return this.getSource().equals(s) || this.getTarget().equals(s);
    }

    @Override
    public Transition clone() {
        return new Transition(src,dst,kind,m);
    }

    public StringBuilder getLabel() {
        return m;
    }

    @Override
    public String toString() {
        return src.getDotLabel() + " -(" + m.toString() + ")>" + dst.getDotLabel();
    }

    public void setLabel(StringBuilder lbl) {
        this.m = lbl;
    }


    @Override
    public int hashCode() {
        return String.valueOf(src.getDotLabel() + dst.getDotLabel()).hashCode();

    }

    @Override
    public boolean equals(Object o) {

        if(!(o instanceof Transition))
            return false;

        Transition t = (Transition)o;

        return this.src.equals(t.src) && this.dst.equals(t.dst);
    }


}
