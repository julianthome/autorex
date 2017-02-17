package org.snt.autorex;

import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;
import org.snt.autorex.autograph.State;
import org.snt.autorex.autograph.Transition;

import java.util.HashMap;
import java.util.Map;

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

    public void classify(Gnfa g) {

        LOGGER.debug("classify");
        ClassificationIterator iter = new ClassificationIterator(g);
        while(iter.hasNext())
            iter.next();

    }


}


