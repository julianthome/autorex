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

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.Collection;
import java.util.Set;

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
