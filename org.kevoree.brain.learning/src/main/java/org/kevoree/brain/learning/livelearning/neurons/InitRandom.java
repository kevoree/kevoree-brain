package org.kevoree.brain.learning.livelearning.neurons;

import java.util.Random;

/**
 * Created by assaad on 21/09/16.
 */
public class InitRandom {
    private static int i=-1;
    private static double[] vals = new double[]{0.15, 0.2, 0.35, 0.25, 0.3, 0.35, 0.4, 0.45, 0.6, 0.5, 0.55, 0.6};
    private static Random random=new Random(0);

    public static double next(){
        i++;
        if(i<vals.length) {
            return vals[i];
        }
        else{
            return random.nextDouble();
        }

    }
}
