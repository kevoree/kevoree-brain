package org.kevoree.brain.smartgrid.profilers;

import de.tuhh.luethke.okde.model.SampleModel;
import org.ejml.simple.SimpleMatrix;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 19/02/15.
 */
public class ElectricConsumptionProfilerPerEnergy extends Profiler {
    private static final int COMPONENTS=96*3;
    // disable the forgetting factor
    private static final double forgettingFactor = 1;
    // set the compression threshold
    private static final double compressionThreshold = 0.02;

    private static int energyMax=4;
    private SampleModel[] sampleDistribution = new SampleModel[energyMax];
    private static double[][] c= ElectricMeasure.getCovariancePerEnergy();


    @Override
    public void feedMeasure(ElectricMeasure sample){

        for(int ienergy=0;ienergy<energyMax;ienergy++) {
            try {
                SimpleMatrix pos = sample.convertToEnergyMatrix(ienergy);
                sampleDistribution[ienergy].updateDistribution(pos, new SimpleMatrix(c), 1d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void feedArray(ArrayList<ElectricMeasure> samples){
        ArrayList<ArrayList<SimpleMatrix>> initSamples = new ArrayList<ArrayList<SimpleMatrix>>();



        for(int i=0;i<energyMax;i++){
            initSamples.add(new ArrayList<SimpleMatrix>());
        }


        for(int i=0;i<samples.size();i++){
            for(int j=0;j<energyMax;j++){
                initSamples.get(j).add(samples.get(i).convertToEnergyMatrix(j));
            }
        }

        for(int i=0;i<energyMax;i++) {

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
            for (int j = 0; j < y.length; j++) {
                double[][] point = { { x[i] }, {y[j]}};
                SimpleMatrix pointVector = new SimpleMatrix(point);
                z[j][i] = sampleDistribution[0].evaluate(pointVector);
            }
        }
        return z;
    }


}
