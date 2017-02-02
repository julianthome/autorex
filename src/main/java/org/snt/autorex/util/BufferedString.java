package org.snt.autorex.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BufferedString {

    final static Logger LOGGER = LoggerFactory.getLogger(BufferedString
            .class);

    private StringBuilder sb = new StringBuilder();



    public BufferedString(char c) {
        append(c);
    }

    public BufferedString(String s) {
        append(s);
    }

    public BufferedString(StringBuilder s) {
        append(s.toString());
    }


    public BufferedString() {

    }

    public void append(BufferedString other) {
       sb.append(other.sb);
    }

    public void prepend(BufferedString other) {
       sb.insert(0, other.sb);
    }

    public void append(char c) {
        append(String.valueOf(c));
    }

    public void append(String s) {
        sb.append(s);
    }

    public void prepend(char c) {
        prepend(String.valueOf(c));
    }

    public void clear() {
        sb.setLength(0);
    }

    public void prepend(String s) {
        sb.insert(0,s);
    }


    public int len() {
        return sb.length();
    }

    public boolean isEmpty(){
        return sb.length() == 0;
    }


    public StringBuilder getStringBuffer() {
       return sb;
    }


    @Override
    public String toString() {
        return sb.toString();
    }



}
