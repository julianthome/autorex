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
import dk.brics.automaton.StatePair;
import dk.brics.automaton.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StateEliminator {


    final static Logger LOGGER = LoggerFactory.getLogger(StateEliminator.class);

    private static Character [] sarray = new Character[] {'+', '{', '}', '(', ')', '[', ']', '&', '^', '-', '?', '*','\"','$', '<', '>', '.', '|' };
    private static Set<Character> special = new HashSet<Character>(Arrays.asList(sarray));

    // Book keeping data structures
    HashMap<State, HashSet<FullTransition>> incoming = null;
    HashMap<State, HashSet<FullTransition>> outgoing = null;
    HashMap<StatePair, FullTransition> transitions = null;
    HashSet<State> states = null;

    final static Logger logger = LoggerFactory.getLogger(StateEliminator.class);

    private AutomatonTrans a = null;
    private FullTransition init = null;
    private State finish = null;
    private State start = null;
    private Set<FullTransition> end = null;

    public StateEliminator(AutomatonTrans a) {
        this.a = a;
        this.a.finalize();
        this.finish = new State();
        this.start = new State();
        this.incoming = new HashMap<>();
        this.outgoing = new HashMap<>();
        this.states = new HashSet<State>();
        this.init = new FullTransition(this.start, new Transition(' ', this
                .a.init), this.a.init);
        this.init.setIsEpsilon(true);
        this.transitions = new HashMap<>();
        this.end = new HashSet<FullTransition>();
    }


    /**
     * Construct the GNFA
     */
    private void prepare() {

        // init state has no incomings
        incoming.put(this.init.getSourceState(), new HashSet<FullTransition>());
        outgoing.put(this.init.getSourceState(), new HashSet<FullTransition>());
        states.add(this.init.getSourceState());
        transitions.put(new StatePair(this.init.getSourceState(), this.init.getTargetState()), this.init);

        // get all transitions
            for (State s : this.a.auto.getStates()) {

            states.add(s);
            for (Transition t : s.getTransitions()) {

                // merge transition with same source and target states
                if (this.transitions.containsKey(new StatePair(s, t.getDest()))) {
                    FullTransition ft = this.transitions.get(new StatePair(s, t.getDest()));
                    ft.addTransition(t);
                } else {
                    FullTransition ft = new FullTransition(s, t, t.getDest());
                    this.transitions.put(new StatePair(s, t.getDest()), ft);
                    addToIncoming(ft);
                    addToOutgoing(ft);
                }
            }

            assert (this.a.auto.getInitialState() != null);
        }
        addToIncoming(this.init);
        addToOutgoing(this.init);

        for (State accept : this.a.auto.getAcceptStates()) {
            FullTransition newFinalTransition =
                    new FullTransition(accept, new Transition(' ', accept), this.finish);
            newFinalTransition.setIsEpsilon(true);
            newFinalTransition.setLabel("\u0000.\u0000{0\u0000}");
            this.end.add(newFinalTransition);
            addToIncoming(newFinalTransition);
            addToOutgoing(newFinalTransition);
            this.transitions.put(new StatePair(accept, finish), newFinalTransition);
        }
        this.states.add(this.finish);
    }

    /**private void debug () {
        LOGGER.info("");
        LOGGER.info("transitions");
        for(Map.Entry<StatePair, FullTransition> e : this.transitions.entrySet()) {
            StatePair sp = e.getKey();

            LOGGER.info(getStringForState(sp.getFirstState()) + " " + getStringForState(sp.getSecondState()));

            FullTransition ft = e.getValue();
            LOGGER.info("\t" +  getStringForTransition(ft));
        }
        LOGGER.info("");
        LOGGER.info("outgoing");
        for(Map.Entry<State, HashSet<FullTransition>> e : this.outgoing.entrySet()) {
            LOGGER.info("\t" + getStringForState(e.getKey()));
            for(FullTransition t : e.getValue()) {
                LOGGER.info("\t\t" + getStringForTransition(t));
            }
        }
        LOGGER.info("");
        LOGGER.info("incoming");
        for(Map.Entry<State, HashSet<FullTransition>> e : this.incoming.entrySet()) {
            LOGGER.info("\t" + getStringForState(e.getKey()));
            for(FullTransition t : e.getValue()) {
                LOGGER.info("\t\t" + getStringForTransition(t));
            }
        }
        LOGGER.info("");
        LOGGER.info("states");
        for(State s : this.states) {
            LOGGER.info(getStringForState(s));
        }
    }**/

    /**private Set<StatePair> getAllRelatedStatePairs(State s) {

        Set<StatePair> related = new HashSet<StatePair>();
        if(this.incoming.containsKey(s)) {
            for (FullTransition f : this.incoming.get(s)) {
                related.add(new StatePair(f.getSourceState(),s));
            }
        }
        if(this.outgoing.containsKey(s)) {
            for (FullTransition f : this.outgoing.get(s)) {
                related.add(new StatePair(s,f.getTargetState()));
            }
        }
        return related;
    }**/


    private boolean eliminate(State src, State s, State dest, Set<FullTransition> toDel, Set<FullTransition> toAdd) {



        //LOGGER.info("eliminate " + getStringForState(src) + " " + getStringForState(s) + " " + getStringForState(dest));

        StatePair loop = new StatePair(s,s);

        StatePair src2slink = new StatePair(src,s);
        StatePair s2destlink = new StatePair(s,dest);
        StatePair s2srclink = new StatePair(s,src);
        StatePair dest2slink = new StatePair(dest,s);
        StatePair src2destlink = new StatePair(src,dest);
        StatePair dest2srclink = new StatePair(dest,src);


        FullTransition s2src = null;
        FullTransition src2s = null;
        FullTransition dest2s = null;
        FullTransition s2dest = null;
        FullTransition sloop = null;
        FullTransition src2dest = null;
        FullTransition dest2src = null;

        String loopLabel = "";
        String s2srcLabel = "";
        String dest2destLabel = "";
        String src2srcLabel = "";
        String src2destLabel = "";
        String dest2srcLabel = "";
        String src2sLabel = "";
        String dest2sLabel = "";
        String s2destLabel = "";


        if(this.transitions.containsKey(loop)) {
            // there is a self loop involved
            sloop = this.transitions.get(loop);
            //if(!sloop.isEpsilon())
            loopLabel = "\u0000(" + sloop.getLabel() + "\u0000)\u0000*";
            toDel.add(sloop);
            //clearTransition(sloop);
        }

        if(this.transitions.containsKey(src2slink)) {
            src2s = this.transitions.get(src2slink);
            //if(!src2s.isEpsilon())
            src2sLabel = src2s.getLabel();
            if(src2s.isEpsilon() && src2s.getLabel().trim().length() == 0)
                src2sLabel = "";
            toDel.add(src2s);
            //clearTransition(src2s);
        }

        if(this.transitions.containsKey(s2destlink)) {
            s2dest = this.transitions.get(s2destlink);
            //if(!s2dest.isEpsilon())
            s2destLabel = s2dest.getLabel();
            //if(s2dest.isEpsilon() && s2dest.getLabel().trim().length() == 0)
            //   s2destLabel = "";

            toDel.add(s2dest);
            //clearTransition(s2dest);
        }

        if(this.transitions.containsKey(dest2slink)) {
            dest2s = this.transitions.get(dest2slink);
            //if(!dest2s.isEpsilon())
            dest2sLabel = dest2s.getLabel();

            //if(dest2s.isEpsilon() && dest2s.getLabel().trim().length() == 0)
            //    dest2sLabel = "";

            toDel.add(dest2s);
            //clearTransition(dest2s);
        }

        // Backlin from s to src
        if(this.transitions.containsKey(s2srclink)) {
            s2src = this.transitions.get(s2srclink);
            //if(!s2src.isEpsilon())
            s2srcLabel = s2src.getLabel();

            //if(s2src.isEpsilon() && s2src.getLabel().trim().length() == 0)
            //    s2srcLabel = "";

            toDel.add(s2src);
           // clearTransition(s2src);
        }

        if(this.transitions.containsKey(src2destlink)) {
            src2dest = this.transitions.get(src2destlink);
            //if(!s2src.isEpsilon())
            src2destLabel =  "\u0000|" + src2dest.getLabel();
            //LOGGER.info("WHOOO " + src2destLabel);
        }

        if(this.transitions.containsKey(dest2srclink)) {
            dest2src = this.transitions.get(dest2srclink);
            //if(!s2src.isEpsilon())
            dest2srcLabel = "\u0000|" + dest2src.getLabel();
            //ogger.info("WHOO " + dest2srcLabel);
        }


        src2destLabel = "\u0000(" + src2sLabel + loopLabel + s2destLabel + src2destLabel +"\u0000)";
        dest2srcLabel = "\u0000(" + dest2sLabel + loopLabel + s2srcLabel + dest2srcLabel +"\u0000)";

        //src2destLabel = src2sLabel + loopLabel + s2destLabel + src2destLabel;
        //dest2srcLabel = dest2sLabel + loopLabel + s2srcLabel + dest2srcLabel;

        // just required if loops s <-> dest or s <-> src
        src2srcLabel = src2sLabel + loopLabel + s2srcLabel;
        dest2destLabel = dest2sLabel + loopLabel + s2destLabel;

        //LOGGER.info("src2dest " + src2destLabel);
        //LOGGER.info("dest2src " + dest2srcLabel);
        //LOGGER.info("src2sr " + src2srcLabel);
        //LOGGER.info("dest2dest " + dest2destLabel);

        if(src2s != null && s2dest != null) {
            FullTransition newSrc2Dest = new FullTransition(src, dest);
            newSrc2Dest.setLabel(src2destLabel);
            toAdd.add(newSrc2Dest);
        }


        if(dest2s != null && s2src != null) {
            FullTransition newDest2Src = new FullTransition(dest, src);
            newDest2Src.setLabel(dest2srcLabel);
            toAdd.add(newDest2Src);
        }

        if(s2dest != null && dest2s != null) {
            FullTransition newDest2Dest = new FullTransition(dest, dest);
            newDest2Dest.setLabel(dest2destLabel);
            toAdd.add(newDest2Dest);
        }

        if(src2s != null && s2src != null) {
            FullTransition newSrc2Src = new FullTransition(src, src);
            newSrc2Src.setLabel(src2srcLabel);
            toAdd.add(newSrc2Src);
        }

        return true;
    }

    private void addToIncoming(FullTransition ft) {
        if (!incoming.containsKey(ft.getTargetState())) {
            incoming.put(ft.getTargetState(), new HashSet<FullTransition>());
        }
        incoming.get(ft.getTargetState()).add(ft);
    }

    private void addToOutgoing(FullTransition ft) {
        if (!outgoing.containsKey(ft.getSourceState())) {
            outgoing.put(ft.getSourceState(), new HashSet<FullTransition>());
        }
        outgoing.get(ft.getSourceState()).add(ft);
    }

    private void addToTransition(FullTransition ft) {
        //LOGGER.info("ADD " + getStringForTransition(ft));

        FullTransition param = ft;

        StatePair key = new StatePair(ft.getSourceState(), ft.getTargetState());

        // instantly merge rules to ensure that two states can only be connected with one transition
        // note that this is an input parameter !
        if(this.transitions.containsKey(key)) {
            param = this.transitions.remove(key);
            //LOGGER.info("ALREADY CONTAINED EPSILON " + param.isEpsilon() + " " + param.getLabel());
            String par = "";
            if(!param.isEpsilon())
                par =  "\u0000|" + param.getLabel();


            ft.setLabel("\u0000(" + ft.getLabel() + par + "\u0000)");
        }
        this.transitions.put(key, ft);
    }


    private boolean handleSpecialCases(State s) {
        StatePair loop = new StatePair(s,s);


        if(!this.incoming.containsKey(s) || !this.outgoing.containsKey(s)) {
            //LOGGER.info("cannot handle " + getStringForState(s));
            return false;
        }

        //LOGGER.info("SPECIAL  " + getStringForState(s));

        HashSet<FullTransition> incoming = new HashSet<FullTransition>();
        HashSet<FullTransition> outgoing = new HashSet<FullTransition>();
        HashSet<FullTransition> toAdd = new HashSet<FullTransition>();
        HashSet<FullTransition> toDel = new HashSet<FullTransition>();


        incoming.addAll(this.incoming.get(s));
        outgoing.addAll(this.outgoing.get(s));

        for(FullTransition in : incoming) {
            State src = in.getSourceState();
            for(FullTransition out : outgoing) {
                State dest = out.getTargetState();

                // ignore loops
                if(src.equals(s) || s.equals(dest))
                    continue;

                //LOGGER.info("out for " + getStringForState(out.getTargetState()));
                eliminate(src,s,dest,toDel,toAdd);
            }

        }


        for(FullTransition del : toDel){
            clearTransition(del);
        }
        for(FullTransition add : toAdd) {
            addLink(add);
        }
        this.states.remove(s);
        this.transitions.remove(s);
        this.outgoing.remove(s);
        this.incoming.remove(s);

        return true;
    }

    private void addLink( FullTransition ft ){
        //LOGGER.info("addAll link " + getStringForTransition(ft));
        addToOutgoing(ft);
        addToIncoming(ft);
        addToTransition(ft);
    }

    private void clearTransition(FullTransition ft) {
       clearTransition(ft.getSourceState(), ft.getTargetState());
    }

    private void clearTransition(State src, State dest) {

        //LOGGER.info("clear transition " + getStringForState(src) + " " + getStringForState(dest));
        assert(src != null);
        assert(dest != null);

        assert(this.outgoing.containsKey(src));
        assert(this.incoming.containsKey(dest));
        assert(this.transitions.containsKey(new StatePair(src,dest)));


        FullTransition trans = this.transitions.remove(new StatePair(src, dest));

        assert(trans != null);

        if(trans != null) {
            //LOGGER.info("RM TRANS " + getStringForTransition(trans));
            this.incoming.get(dest).remove(trans);
            this.outgoing.get(src).remove(trans);
        }

        if(this.incoming.get(dest).isEmpty()) {
            this.incoming.remove(dest);
        }
        if(this.outgoing.get(src).isEmpty()) {
            this.outgoing.remove(src);
        }

        if(!this.outgoing.containsKey(src) && !this.incoming.containsKey(src)) {
            this.states.remove(src);
        }
        if(!this.outgoing.containsKey(dest) && !this.incoming.containsKey(dest)) {
            this.states.remove(dest);
        }

        if(!this.states.contains(src) && !this.states.contains(dest)) {
            this.transitions.remove(new StatePair(src,dest));
        }


    }


    public String stateElimination() {


        if(this.a.auto.isTotal()) {
            return ".*";
        }

        prepare();

        //LOGGER.info(this.toDot());


        LinkedList<State> worklist = new LinkedList<State>();
        worklist.addAll(this.states);

        int t= 0;
        int k = 0;
        //while(this.states.size() > 1) {
            //t++;
            //if (t ==100)
            //    break;
            //k %= this.states.size();

            //State s = states.get(k++);

        while(!worklist.isEmpty()) {

            State s = worklist.pop();

            /**if (!this.states.contains(s)) {
                LOGGER.info(this.a.getNumberOfState(s) + " not there anymore");
                continue;
            }**/
            if (!s.equals(this.finish) && !s.equals(this.start)) {
                /**if (!this.incoming.containsKey(s)) {
                    continue;
                }
                if (!this.outgoing.containsKey(s)) {
                    continue;
                }**/

                //debug();
                //LOGGER.info(this.toDot());
                //LOGGER.info("");

                //if(!handleStraightConnection(s)) {
                handleSpecialCases(s);
                //}
                //debug();
                //LOGGER.info(this.toDot());

                LOGGER.debug("#States: {}", states.size());
            }

        }


        //LOGGER.info(this.toDot());

        return escapeSpecialCharacters(getRexpString());
    }


    public String getRexpString() {

        StringBuffer sb = new StringBuffer();
        for (FullTransition t : this.transitions.values()) {
            sb.append(t.getLabel());
        }

        return sb.toString().trim();
    }


    /**public String toDot() {

        StringBuilder sb = new StringBuilder();

        sb.append("digraph g {\n");

        for (State s : this.states) {

            if (s.equals(this.init.getSourceState())) {
                continue;
            }

            if (s.isAccept()) {
                sb.append("\tn" + getStringForState(s) + "[shape=\"doublecircle\"];\n");
            }

            if (s.equals(this.finish)) {
                sb.append("\tn" + getStringForState(s) + "[shape=\"doublecircle\",label=\"END\"];\n");
            }

        }

        for (FullTransition t : this.transitions.values()) {


            State src = t.getSourceState();
            State dest = t.getTargetState();

            sb.append("\tn" + getStringForState(src) + " -> n" +
                        getStringForState(dest) + " [label=\"" + t.getLabel() + "\"];\n");

        }
        sb.append("}");

        return sb.toString();

    }**/


    /**private String getStringForState(State s) {
        if(this.a.getNumberOfState(s) >= 0) {
            return this.a.getNumberOfState(s) + "";
        } else {
            if(s.equals(this.start)) {
                return "start";
            } else if (s.equals(this.finish)) {
                return "finish";
            } else {
                return "x";
            }
        }
    }**/


    /**private String getStringForTransition(FullTransition t) {
        return getStringForState(t.getSourceState()) + " -" + t.getLabel() + "-> " + getStringForState(t.getTargetState());
    }**/


    private String escapeSpecialCharacters(String s) {
        StringBuffer out = new StringBuffer();
        char pred = ' ';
        for(char c : s.toCharArray()) {
            if(out.length() > 0) {
                if (pred != '\u0000' && special.contains(c)) {
                    out.append("\\" + c);
                } else if (pred == '\u0000' && special.contains(c)) {
                    out.deleteCharAt(out.length() - 1); // delete NULL
                    out.append(c);
                } else {
                    out.append(c);
                }
            } else {
                out.append(c);
            }
            pred = c;
        }
        return out.toString();
    }



}
