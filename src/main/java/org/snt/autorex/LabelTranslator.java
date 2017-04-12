package org.snt.autorex;


import dk.brics.automaton.Transition;

public interface LabelTranslator {
    String getTransitionString(Transition t);
}
