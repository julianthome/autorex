/*
* prex - approximate regular expression matching
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

package org.snt.autorex.autograph;


public class Transition implements Cloneable {

    public enum Kind {
        EPSILON,
        MATCH
    }

    private Kind kind = Kind.MATCH;
    private StringBuilder m = new StringBuilder();
    private State src = null;
    private State dst = null;

    public Transition(State src, State dst, Kind k, StringBuilder m) {
        this(src,dst,k);
        this.m = m;
    }

    public Transition(State src, State dst, Kind k) {
        this.src = src;
        this.dst = dst;
        this.kind = k;
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

        return this.src.equals(t.src) && this.dst.equals(t.dst) && this.m
                .equals(t.m);
    }


}
