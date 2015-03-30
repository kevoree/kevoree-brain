package org.kevoree.brain.smartgrid.Profilers;

import de.tuhh.luethke.okde.model.SampleModel;
import org.ejml.simple.SimpleMatrix;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 19/02/15.
 */
public class ElectricConsumptionProfilerPerTime extends Profiler {

    private static final int COMPONENTS=96*3;
    // disable the forgetting factor
    private static final double forgettingFactor = 1;
    // set the compression threshold
    private static final double compressionThreshold = 0.02;

    private static int timeMax=96/2;
    private SampleModel[] sampleDistribution = new SampleModel[timeMax];

    private static double[][] c= ElectricMeasure.getCovariancePerTime();


    @Override
    public void feedMeasure(ElectricMeasure sample){
        SimpleMatrix pos = sample.convertToMatrixPerTime();
        int iTime=sample.getIntTime(timeMax);
        try {
            sampleDistribution[iTime].updateDistribution(pos, new SimpleMatrix(c),1d);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void feedArray(ArrayList<ElectricMeasure> samples){
        ArrayList<ArrayList<SimpleMatrix>> initSamples = new ArrayList<ArrayList<SimpleMatrix>>();



        for(int i=0;i<timeMax;i++){
            initSamples.add(new ArrayList<SimpleMatrix>());
        }


        for(int i=0;i<samples.size();i++){
            int iTime=samples.get(i).getIntTime(timeMax);
            initSamples.get(iTime).add(samples.get(i).convertToMatrixPerTime());
        }

        for(int i=0;i<timeMax;i++) {

            sampleDistribution[i] = new SampleModel(forgettingFactor, compressionThreshold);
            sampleDistribution[i].setNoOfCompsThreshold(COMPONENTS);

            double[] w =new double[initSamples.get(i).size()];
            SimpleMatrix[] cov = new SimpleMatrix[initSamples.get(i).size()];
            for(int j=0;j< initSamples.get(i).size();j++){
                w[j]=1;
                cov[j]=new SimpleMatrix(c);
            }
            try {
                SimpleMatrix[] means = initSamples.get(i).toArray(new SimpleMatrix[initSamples.get(i).size()]);
                sampleDistribution[i].updateDistribution(means, cov, w);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public double getProba(ElectricMeasure sample) {
        return 0;
    }

    @Override
    public double[][] getProbabilities(double[] x, double[] y) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++) {
            int iTime = (int) (x[i] *60*timeMax/1440);
            for (int j = 0; j < y.length; j++) {
                double[][] point = {{y[j]}, {0},{0},{0}};
                SimpleMatrix pointVector = new SimpleMatrix(point);
                z[j][i] = sampleDistribution[iTime].evaluate(pointVector);
            }
        }
        return z;
    }

}
