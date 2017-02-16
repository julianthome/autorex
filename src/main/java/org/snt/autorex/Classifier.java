package org.snt.autorex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.autograph.Gnfa;
import org.snt.autorex.autograph.State;
import org.snt.autorex.autograph.Transition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public enum Classifier {

    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(Classifier.class);

    public enum Color {
        WHITE,
        GRAY,
        BLACK
    }

    public void classify(Gnfa g) {

        LOGGER.debug("classify");

        Map<State, Color> cmap = new HashMap<>();
        Map<State, Integer> dmap = new HashMap<>();
        Map<State, Integer> fmap = new HashMap<>();

        g.vertexSet().forEach(x -> {
            cmap.put(x, Color.WHITE);
            dmap.put(x, 0);
            fmap.put(x, 0);
        });


        Stack<State> visited = new Stack<>();

        int time = 0;

        visited.add(g.getStart());

        while (!visited.isEmpty()) {

            State s = visited.pop();
            time++;
            dmap.put(s, time);

            Set<Transition> trans = g.outgoingEdgesOf(s);

            for (Transition t : trans) {

                State nxt = t.getTarget();

                Color nxtColor = cmap.get(nxt);

                if (nxtColor == Color.WHITE) {
                    cmap.put(nxt, Color.GRAY);
                    time++;
                    dmap.put(nxt, time);
                    visited.push(nxt);
                    t.setProp(Transition.Property.TREE);
                } else if (nxtColor == Color.GRAY) {
                    t.setProp(Transition.Property.BACK);
                } else if (nxtColor == Color.BLACK) {
                    t.setProp(Transition.Property.TREE);
                    if (dmap.get(s) < dmap.get(nxt)) {
                        t.setProp(Transition.Property.FWD);
                    } else {
                        t.setProp(Transition.Property.CROSS);
                    }
                }
            }
            cmap.put(s, Color.BLACK);
            time++;
            fmap.put(s, time);
        }
    }


}


