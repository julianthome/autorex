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

package org.snt.autorex;

import dk.brics.automaton.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snt.autorex.util.BufferedString;

import java.util.*;


/**
 * A bridge detector based on the implementation
 * http://stackoverflow.com/questions/28917290/how-can-i-find-bridges-in-an-undirected-graph
 */
public enum BridgeDetector {
    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(BridgeDetector.class);

    private int time = 0;


    private BufferedString combineTrans(FullTransition a, FullTransition b) {
        assert a.isConcrete() && b.isConcrete();
        BufferedString sb = new BufferedString();
        sb.append(a.getCarry());
        sb.append(b.getCarry());
        return sb;
    }

    private void preprocess(AutomatonTrans g) {

        LOGGER.debug("preprocess");

        LinkedList<State> worklist = new LinkedList<>();
        worklist.addAll(g.states);
        while (!worklist.isEmpty()) {

            State s = worklist.pop();

            if (!g.states.contains(s))
                continue;

            if (!g.incoming.containsKey(s))
                continue;

            if (!g.outgoing.containsKey(s))
                continue;

            if (g.incoming.get(s).size() == 1
                    && g.outgoing.get(s).size() == 1) {

                FullTransition in = g.incoming.get(s).iterator().next();
                FullTransition out = g.outgoing.get(s).iterator().next();


                if (in.isConcrete() && out.isConcrete()) {
                    FullTransition nft = new FullTransition(in.getSourceState(),
                            out.getTargetState());


                    nft.setCarry(combineTrans(in, out));

                    g.delTransition(in);
                    g.delTransition(out);
                    g.delState(s);

                    g.addTransition(nft);
                }
            }
        }
    }



    private int cnt = 0;



    private void dfs(AutomatonTrans g,
                     State u,
                     Set<FullTransition> tset,
                     Map<State,Integer> disc,
                     Map<State,Integer> low,
                     Map<State,State> par,
                     Map<State,Boolean> visited) {
        LOGGER.debug("dfs");
        visited.put(u, true);

        int c = ++cnt;
        disc.put(u, c);
        low.put(u, c);

        Set <FullTransition> adj = new HashSet<>();

        if(g.outgoing.get(u) != null)
            adj.addAll(g.outgoing.get(u));

        if(g.incoming.get(u) != null)
            adj.addAll(g.incoming.get(u));

        for (FullTransition t : adj) {

            State v = null;

            if(t.getTargetState().equals(u))
                v = t.getSourceState();
            else
                v = t.getTargetState();

            if (!visited.get(v)) {
                par.put(v,u);
                dfs(g,v,tset,disc,low,par,visited);
                low.put(u, Math.min(low.get(u), low.get(v)));
                if (low.get(v) > disc.get(u)) {
                    LOGGER.debug("{} -> {}", g.getNumberOfState(u),
                            g.getNumberOfState(v));
                    tset.add(t);
                }
            } else if (!v.equals(par.get(u))) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }
    }


    public Set<FullTransition> detectBridges(AutomatonTrans g) {

        cnt = 0;
        Set<FullTransition> tset = new HashSet<>();

        Map<State, Integer> disc = new HashMap<>();
        Map<State, Integer> low = new HashMap<>();
        Map<State, State> par = new HashMap<>();
        Map<State, Boolean> visited = new HashMap<>();


        CycleEliminator.INSTANCE.eliminate(g);
        preprocess(g);
        g.finalize();


        for (State s : g.states) {
            disc.put(s, -1);
            low.put(s, -1);
            par.put(s, null);
            visited.put(s,false);
        }


        for(State s : g.states)
            if(!visited.get(s))
                dfs(g,s,tset,disc,low,par,visited);

        return tset;
    }

    public String getTransString(AutomatonTrans g, FullTransition ft) {
        return g.statenumber.get(ft.getSourceState()) + "->" + g.statenumber
                .get(ft.getTargetState());
    }

}
