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

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.Collection;
import java.util.Set;

/**
 * A class that represents a graph data structure
 */
public class AbstractGraph implements DirectedGraph<State, Transition> {


    private final DirectedPseudograph<State, Transition> delegate;

    public AbstractGraph() {
        this.delegate = new DirectedPseudograph<>(new EdgeFact());
    }

    public boolean addEdge(State arg0, State arg1, Transition arg2) {
        return delegate.addEdge(arg0, arg1, arg2);
    }

    public Transition addEdge(State arg0, State arg1) {
        return delegate.addEdge(arg0, arg1);
    }

    public boolean addVertex(State arg0) {
        return delegate.addVertex(arg0);
    }

    public boolean containsEdge(Transition arg0) {
        return delegate.containsEdge(arg0);
    }

    public boolean containsEdge(State arg0, State arg1) {
        return delegate.containsEdge(arg0, arg1);
    }

    public boolean containsVertex(State arg0) {
        return delegate.containsVertex(arg0);
    }

    public int degreeOf(State arg0) {
        return delegate.degreeOf(arg0);
    }


    public Set<Transition> edgeSet() {
        return delegate.edgeSet();
    }


    public Set<Transition> edgesOf(State arg0) {
        return delegate.edgesOf(arg0);
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof AbstractGraph)) {
            return false;
        } else {
            AbstractGraph other = (AbstractGraph) obj;
            DirectedPseudograph<State, Transition> otherDelegate = (DirectedPseudograph<State, Transition>) other.delegate;
            return delegate.equals(otherDelegate);
        }
    }


    public Set<Transition> getAllEdges(State arg0, State arg1) {
        return delegate.getAllEdges(arg0, arg1);
    }

    public Transition getEdge(State arg0, State arg1) {
        return delegate.getEdge(arg0, arg1);
    }

    public EdgeFactory<State, Transition> getEdgeFactory() {
        return delegate.getEdgeFactory();
    }

    public State getEdgeSource(Transition arg0) {
        return delegate.getEdgeSource(arg0);
    }

    public State getEdgeTarget(Transition arg0) {
        return delegate.getEdgeTarget(arg0);
    }

    public double getEdgeWeight(Transition arg0) {
        return delegate.getEdgeWeight(arg0);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public int inDegreeOf(State arg0) {
        return delegate.inDegreeOf(arg0);
    }

    public Set<Transition> incomingEdgesOf(State arg0) {
        return delegate.incomingEdgesOf(arg0);
    }


    public int outDegreeOf(State arg0) {
        return delegate.outDegreeOf(arg0);
    }

    public Set<Transition> outgoingEdgesOf(State arg0) {
        return delegate.outgoingEdgesOf(arg0);
    }

    public boolean removeAllEdges(Collection<? extends Transition> arg0) {
        return delegate.removeAllEdges(arg0);
    }

    public Set<Transition> removeAllEdges(State arg0, State arg1) {
        return delegate.removeAllEdges(arg0, arg1);
    }

    public boolean removeAllVertices(Collection<? extends State> arg0) {
        return delegate.removeAllVertices(arg0);
    }

    public boolean removeEdge(Transition arg0) {
        return delegate.removeEdge(arg0);
    }

    public Transition removeEdge(State arg0, State arg1) {
        return delegate.removeEdge(arg0, arg1);
    }

    public boolean removeVertex(State arg0) {
        return delegate.removeVertex(arg0);
    }


    public String toString() {
        return delegate.toString();
    }

    public Set<State> vertexSet() {
        return delegate.vertexSet();
    }

}
