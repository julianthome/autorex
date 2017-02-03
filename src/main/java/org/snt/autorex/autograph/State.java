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