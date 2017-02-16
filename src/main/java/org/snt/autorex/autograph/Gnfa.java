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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class Gnfa extends AbstractGraph implements Cloneable {

    final static Logger LOGGER = LoggerFactory.getLogger(Gnfa.class);

    private State start;
    private State end;


    public Set<Transition> getIncomingEdgesOfKind(State n, Transition.Kind k) {
        return super.incomingEdgesOf(n).stream().filter(e -> e.getKind() ==
                k).collect(Collectors.toSet());
    }

    public Set<Transition> getOutgoingEdgesOfKind(State n, Transition.Kind k) {
        return super.outgoingEdgesOf(n).stream().filter(e -> e.getKind() ==
                k).collect(Collectors.toSet());
    }

    public Set<State> getConnectedInStates(State s) {
        return incomingEdgesOf(s).stream().map(Transition::getSource).collect
                (Collectors.toSet());
    }

    public Set<State> getConnectedOutStates(State s) {
        return outgoingEdgesOf(s).stream().map(Transition::getTarget).collect
                (Collectors.toSet());
    }


    public void addEdge(Transition e) {


        State src = e.getSource();
        State dst = e.getTarget();

        super.addVertex(src);
        super.addVertex(dst);
        super.addEdge(src,dst, e);
    }

    public boolean addVertex(State s) {
       if (s.getKind() == State.Kind.ACCEPT) {
           this.end = s;
       }
       if(s.getKind() == State.Kind.START) {
           this.start = s;
       }
       return super.addVertex(s);
    }

    public State getStart() {
        return start;
    }


    public State getEnd() {
        return end;
    }


    public String toDot() {

        StringBuilder sb = new StringBuilder();
        sb.append("digraph {\n" +
                "\trankdir=TB;\n");

        sb.append("\tnode [fontname=Helvetica,fontsize=11];\n");
        sb.append("\tedge [fontname=Helvetica,fontsize=10];\n");


        for (State n : this.vertexSet()) {
            String shape = "";
            String color = "";

            if (n.getKind() == State.Kind.START) {
                color = "green";
            }

            if (n.getKind() == State.Kind.ACCEPT) {
                shape = "doublecircle";
            }

            sb.append("\t" + n.getDotLabel() + " [label=\"" + n.getDotLabel() + "\"," +
                    "shape=\"" + shape + "\", color=\"" + color + "\"];\n");
        }


        for (Transition e : this.edgeSet())  {

            State src = e.getSource();
            State dst = e.getTarget();

            String label = "";
            String color = "black";

            switch(e.getKind()){
                case MATCH:
                    break;
                case EPSILON:
                    color = "red";
                    break;
            }

            switch(e.getProp()) {
                case NORMAL:
                    color = "brown";
                    break;
                case BACK:
                    color = "pink";
                    break;
                case FWD:
                    color = "blue";
                    break;
                case CROSS:
                    color = "yellow";
                    break;
                case TREE:
                    color = "green";
                    break;
            }

            label = "[label=\"" + e.getLabel() + "\",color=" + color + "];\n";

            sb.append("\t" + src.getDotLabel() + " -> " + dst.getDotLabel() + label);
        }


        sb.append("}\n");

        return sb.toString();


    }


}
