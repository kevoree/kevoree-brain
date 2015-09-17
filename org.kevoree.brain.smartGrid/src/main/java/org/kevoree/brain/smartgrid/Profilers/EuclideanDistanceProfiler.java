package org.kevoree.brain.smartgrid.Profilers;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 17/09/15.
 */
public class EuclideanDistanceProfiler extends Profiler {
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
        return 0;
    }

    @Override
    public double[][] getProbabilities(double[] x, double[] y) {
        return new double[0][];
    }

    public double getDistance(EuclideanDistanceProfiler profiler) {
        double dist=0;
        double var=0;
        for(int i=0;i<timeStep;i++){
            double[] avg1= states[i].getAverage();
            double[] avg2= profiler.states[i].getAverage();
            double[] var1= states[i].getVariance();
            double[] var2= profiler.states[i].getVariance();
            for(int j=0;j<avg1.length;j++){
                dist+= (avg1[j]-avg2[j])*(avg1[j]-avg2[j]);
                var+=(var1[j]-var2[j])*(var1[j]-var2[j]);
            }
        }
        dist=Math.sqrt(dist);
        return dist;
    }
}
