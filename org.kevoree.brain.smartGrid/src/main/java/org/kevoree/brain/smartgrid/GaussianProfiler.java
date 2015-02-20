package org.kevoree.brain.smartgrid;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * Created by assaad on 20/02/15.
 */
public class GaussianProfiler extends Profiler {
    private Gaussian[] states =new Gaussian[timeStep];
    @Override
    public void feedMeasure(ElectricMeasure sample) {
        int time=sample.getIntTime(timeStep);
        states[time].train(sample.getArrayFeatures());

    }

    @Override
    public void feedArray(ArrayList<ElectricMeasure> samples) {
        for(int i=0;i<timeStep;i++){
            states[i]=new Gaussian();
        }

        for(int i=0;i<samples.size();i++){
            feedMeasure(samples.get(i));
        }
    }

    @Override
    public double getProba(ElectricMeasure sample) {
        int time=sample.getIntTime(timeStep);
       return states[time].calculateProbability(sample.getArrayFeatures());
        /*if(checkSample(sample)){
            return states[time].calculateProbability(sample.getArrayFeatures());
        }
        else{
            return 0;
        }*/
    }

    @Override
    public double[][] getProbabilities(double[] x, double[] y) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++) {
            int iTime = (int) (x[i] *60*timeStep/1440);
            for (int j = 0; j < y.length; j++) {
                double[] point = {y[j], 0,0,0};
                z[j][i] = states[iTime].calculateProbability(point);
            }
        }
        return z;
    }


}
