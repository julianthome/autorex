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

package org.snt.autorex;

import dk.brics.automaton.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public enum CycleEliminator {
    INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(CycleEliminator.class);

    private int timer;

    public void eliminate(AutomatonTrans a) {
        timer = 1;
        Set<FullTransition> back = doDFS(a);
        a.delTransitions(back);
    }

    private Set<FullTransition> doDFS(AutomatonTrans auto) {

        Map<State, String> cmap = new HashMap<>();
        Map<State, Integer> dmap = new HashMap<>();
        Map<FullTransition, String> tmap = new HashMap<>();

        for(State s : auto.states){
            cmap.put(s, "white");
            dmap.put(s, 0);
        }

        dfs(auto,auto.auto.getInitialState(),cmap,dmap,tmap);

        for(Map.Entry<FullTransition, String> e : tmap.entrySet()) {
            LOGGER.debug(e.getKey().getLabel() + " : " + e.getValue());
        }

        return tmap.entrySet().stream().filter(e -> e.getValue().equals("bw"))
                .map(e -> e.getKey()).collect(Collectors.toSet());

    }

    private void dfs(AutomatonTrans g, State srcstate,
                    Map<State, String> cmap, Map<State,Integer> dmap,
                    Map<FullTransition, String> tmap) {
        this.timer++;


        Set<FullTransition> trans = g.outgoing.get(srcstate);

        if(trans == null)
            return;

        dmap.put(srcstate, timer);
        cmap.put(srcstate, "gray");

        for (FullTransition t : trans) {

            State desttate = t.getTargetState();
            String destcolor = cmap.get(desttate);

            //LOGGER.debug("DSTOLOR {}", destcolor);
            int srcd = dmap.get(srcstate);
            int dstd = dmap.get(desttate);

            if (destcolor.equals("white")) {
                tmap.put(t, "tree");
                dfs(g,desttate, cmap, dmap, tmap);
            } else if (destcolor.equals("gray")) {
                LOGGER.debug("Found back edge {}", t.getLabel());
                tmap.put(t, "bw");
            } else if (destcolor.equals("black")) {
                tmap.put(t, "tree");
                if (srcd < dstd) {
                    tmap.put(t, "fwd");
                } else if (srcd  > dstd) {
                    tmap.put(t, "cross");
                }
            }

        }

        cmap.put(srcstate, "black");
        this.timer++;
    }



}
