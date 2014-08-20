package org.kevoree.brain.util.adapters;

import org.kevoree.brain.api.classifier.Adapter;
import org.kevoree.brain.api.classifier.LiveLearning;

/**
 * Created by assaa_000 on 8/20/2014.
 */
public class PolynomialAdapter  implements Adapter {

    private int degree;
    private LiveLearning liveLearning;

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;


        reset();
    }


    @Override
    public void feed(double[] values) {
        double[] powers= new double[degree];
        powers[degree-1]=values[0];
        for(int i=degree-2; i>=0;i--)
            powers[i]=powers[i+1]*values[0];
        liveLearning.feed(powers,values[1]);

    }

    @Override
    public void setLiveLearning(LiveLearning c) {
        this.liveLearning=c;

    }

    @Override
    public void reset() {

    }

    @Override
    public LiveLearning getLiveLearning() {
        return liveLearning;
    }
}
