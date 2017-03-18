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

import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;
import org.snt.autorex.autograph.State;
import org.snt.autorex.autograph.Transition;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for classifying automaton edges
 * based on Thomas H. Cormen, Introduction to Algorithms, 2011,
 * page
 */
public enum Classifier {

    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(Classifier.class);

    public enum Color {
        WHITE,
        GRAY,
        BLACK
    }


    static class ClassificationIterator extends DepthFirstIterator<State,
            Transition> {

        private int timer = 0;

        private Map<State, Integer> dmap = new HashMap();
        private Map<State, Color> cmap = new HashMap();

        public ClassificationIterator(Gnfa g) {
            super(g, g.getStart());
        }

        @Override
        protected void encounterVertex(State vertex, Transition edge) {
            if(edge != null)
                edge.setProp(Transition.Property.TREE);

            LOGGER.debug("e {}", vertex.getDotLabel());
            dmap.put(vertex, timer++);
            cmap.put(vertex, Color.GRAY);
            super.encounterVertex(vertex,edge);
        }

        @Override
        protected void encounterVertexAgain(State vertex, Transition edge) {
            if(cmap.get(vertex) == Color.BLACK) {
                if(dmap.get(edge.getSource()) < dmap.get(edge.getTarget())) {
                    edge.setProp(Transition.Property.FWD);
                } else if (dmap.get(edge.getSource()) > dmap.get(edge
                        .getTarget())) {
                    edge.setProp(Transition.Property.CROSS);
                } else {
                    edge.setProp(Transition.Property.FWD);
                }
            } else if (cmap.get(vertex) == Color.GRAY) {
                edge.setProp(Transition.Property.BACK);
            }
            super.encounterVertexAgain(vertex,edge);
        }

        protected void finishVertex(State vertex) {
            cmap.put(vertex, Color.BLACK);
        }

    }

    /**
     * Start edge classification
     * @param g the Gnfa to analyze
     */
    public void classify(Gnfa g) {
        LOGGER.debug("classify");
        ClassificationIterator iter = new ClassificationIterator(g);
        while(iter.hasNext())
            iter.next();

    }


}


