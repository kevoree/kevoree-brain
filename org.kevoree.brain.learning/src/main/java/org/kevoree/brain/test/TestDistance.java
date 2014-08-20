package org.kevoree.brain.test;

import org.kevoree.brain.util.LevenshteinDistance;

/**
 * Created by assaa_000 on 8/14/2014.
 */
public class TestDistance {
    public static void main (String[] args){
        String s1="Assaad";
        String s2="Assaad 2";
        System.out.println("Distance "+ LevenshteinDistance.levenshteinDistance(s1, s2));
    }
}
