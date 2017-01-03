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

import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * A bridge detector based on the implementation
 * http://stackoverflow.com/questions/28917290/how-can-i-find-bridges-in-an-undirected-graph
 */
public enum BridgeDetector {
        INSTANCE;

    final static Logger LOGGER = LoggerFactory.getLogger(AutomatonTrans.class);

    private int cnt = 0;
    private Map<State, Integer> pre = new HashMap<>();
    private Map<State, Integer> low = new HashMap<>();


    public Set<FullTransition> detect(Automaton t) {
        cnt = 0;
        Set<FullTransition> tset = new HashSet<>();

        AutomatonTrans g = new AutomatonTrans(t);

        State fs = new State();
        fs.setAccept(true);
        g.states.add(fs);

        Set<FullTransition> epsilons = new HashSet<>();

        for(State s : g.states){
            pre.put(s,-1);
            low.put(s,-1);
            if(s.isAccept()) {
                s.setAccept(false);
                FullTransition ft = new FullTransition(s, fs);
                ft.setIsEpsilon(true);
                epsilons.add(ft);
            }
        }

        g.addTransitions(epsilons);
        g.finalize();
        dfs(g, g.getInitialState(), g.getInitialState(), tset);
        return tset;
    }

    private List<State> preorder(AutomatonTrans g) {
        LinkedList<State> ret = new LinkedList<>();
        doPreorder(g, g.getInitialState(), ret);
        return ret;
    }

    private void doPreorder(AutomatonTrans g, State s, List<State> ret) {
        if(ret.contains(s))
            return;
        else
            ret.add(s);

        Set<FullTransition> trans = g.outgoing.get(s);

        if(trans != null) {
            for (FullTransition t : trans) {
                doPreorder(g, t.getTargetState(), ret);
            }
        }
    }

    private void dfs(AutomatonTrans g, State u, State v, Set<FullTransition> tset) {
        LOGGER.debug("dfs");
        pre.put(v, cnt++);
        low.put(v, pre.get(v));


        for(FullTransition t : g.outgoing.get(v)) {
            State w = t.getTargetState();
            if(pre.get(w).equals(-1)) {
                dfs(g, v, w, tset);
                low.put(v,Math.min(low.get(v), low.get(w)));
                if (low.get(w) == pre.get(w)) {
                    LOGGER.debug("{} is a bridge", t.getLabel());
                    tset.add(t);
                }
            } else if (!w.equals(u)) {
                low.put(v, Math.min(low.get(v),pre.get(w)));
            }
        }
    }
}
