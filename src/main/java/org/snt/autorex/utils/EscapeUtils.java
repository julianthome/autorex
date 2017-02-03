package org.snt.autorex.utils;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * escaping helper class
 */
public final class EscapeUtils {

    private static final Set<Character> SPECIAL = Stream.of('+', '{', '}', '(', ')', '[', ']', '&', '^',
            '-', '?', '*', '\"', '$', '<', '>', '.', '|', '#').collect(toSet());

    private EscapeUtils() {
    }

    /**
     * escape special character in a string with a backslash
     *
     * @param s string to be escaped
     * @return escaped string
     */
    public static String escapeSpecialCharacters(String s) {
        if (s == null)
            return "";

        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (SPECIAL.contains(c)) {
                out.append("\\").append(c);
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * unescape special character in a string
     *
     * @param s string to be unescaped
     * @return unescaped string
     */
    public static String unescapeSpecialCharacters(String s) {
        if (s == null)
            return "";

        StringBuilder out = new StringBuilder();
        char pred = ' ';
        for (char c : s.toCharArray()) {
            if (pred == '\\' && SPECIAL.contains(c)) {
                out.deleteCharAt(out.length() - 1);
                out.append(c);
            } else {
                out.append(c);
            }
            pred = c;
        }
        return out.toString();
    }
}