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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A generalized non-deterministic finite automaton
 */
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


    /**
     * get a subgraph
     * @param vertices set of vertices
     * @return the subgraph that connects all vertices in the given set
     */
    private Gnfa subgraph(Collection<State> vertices) {
        Gnfa g = new Gnfa();

        for (State n : vertices) {
            g.addVertex(n);
        }

        for (State n : vertices) {
            for (Transition e : outgoingEdgesOf(n)) {
                if (vertices.contains(e.getTarget())) {
                    g.addEdge(e);
                }
            }
        }
        return g;
    }

}
