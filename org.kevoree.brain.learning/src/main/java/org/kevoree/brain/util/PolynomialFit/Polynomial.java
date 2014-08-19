package org.kevoree.brain.util.PolynomialFit;

/**
 * Created by assaa_000 on 8/19/2014.
 */
public interface Polynomial {
    public double[] getCoef();
    public void fit( double samplePoints[] , double[] observations );
    public double getError(double samplePoints[] , double[] observations);
    public double calculate (double t);
    public int getDegree();
    public void print();
}
