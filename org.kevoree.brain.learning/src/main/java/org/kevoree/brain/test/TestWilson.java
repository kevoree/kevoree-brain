package org.kevoree.brain.test;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.kevoree.brain.learning.livelearning.Recommender.Util;

/**
 * Created by assaa_000 on 03/05/2015.
 */
public class TestWilson {
    public static void main(String[] args){
        double x=0.95;
        double y=1-x/2;
        System.out.println(Util.InverseNormalCDF(y));
        NormalDistribution nd = new NormalDistribution(0,1);
        System.out.println(nd.inverseCumulativeProbability(x));
        System.out.println("wilson " + Util.wilsonRank(900,100,0.95));
        Util.wilsonApache(900, 100,0.95);
    }
}
