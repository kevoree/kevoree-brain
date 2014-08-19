package org.kevoree.brain.api.classifier;

/**
 * Created by Ace Shooting on 8/19/2014.
 */
public interface LiveLearning {
    public void initialize(Object[] params);
    public void feed(double[] features, double result);
    public double calculate(double[] feature);
    public void reset();
    public void print();

}
