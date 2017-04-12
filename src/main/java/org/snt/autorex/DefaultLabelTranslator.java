package org.snt.autorex;

import dk.brics.automaton.Transition;
import org.snt.autorex.utils.EscapeUtils;

/**
 * Created by julian on 12/04/2017.
 */
public class DefaultLabelTranslator implements LabelTranslator {

    @Override
    public String getTransitionString(Transition t) {
        StringBuilder sb = new StringBuilder();

        if (t.getMax() == t.getMin()) {
            sb.append(EscapeUtils.escapeSpecialCharacters(String.valueOf(t.getMin
                    ())));
        } else {
            sb.append("[" + t.getMin() + "-" + t.getMax() + "]");
        }

        return sb.toString();
    }
}
