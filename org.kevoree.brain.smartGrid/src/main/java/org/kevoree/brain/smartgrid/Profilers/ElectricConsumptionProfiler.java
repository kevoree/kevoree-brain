package org.kevoree.brain.smartgrid.profilers;

import de.tuhh.luethke.okde.model.SampleModel;
import org.ejml.simple.SimpleMatrix;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 19/02/15.
 */
public class ElectricConsumptionProfiler extends Profiler {

    private static final int COMPONENTS=50;
    // disable the forgetting factor
    private static final double forgettingFactor = 1;
    // set the compression threshold
    private static final double compressionThreshold = 0.02;

    private SampleModel sampleDistribution;

    private static double[][] c= ElectricMeasure.getCovariance();


    @Override
    public void feedMeasure(ElectricMeasure sample){
        SimpleMatrix pos = sample.convertToMatrix();
        try {
        sampleDistribution.updateDistribution(pos, new SimpleMatrix(c),1d);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void feedArray(ArrayList<ElectricMeasure> samples){
        ArrayList<SimpleMatrix> initSamples = new ArrayList<SimpleMatrix>();
        double[] w =new double[samples.size()];
        SimpleMatrix[] cov = new SimpleMatrix[samples.size()];

        for(int i=0;i<samples.size();i++){
            initSamples.add(samples.get(i).convertToMatrix());
            w[i]=1;
            cov[i]=new SimpleMatrix(c);
        }
        sampleDistribution = new SampleModel(forgettingFactor, compressionThreshold);
        sampleDistribution.setNoOfCompsThreshold(COMPONENTS);

        try {
            SimpleMatrix[] means=initSamples.toArray(new SimpleMatrix[samples.size()]);
            sampleDistribution.updateDistribution(means, cov, w);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public double getProba(ElectricMeasure sample) {
        if(checkSample(sample)){
            double proba=sampleDistribution.evaluate(sample.convertToMatrix());
            return proba;
           /* double proba=sampleDistribution.evaluate(sample.convertToMatrix());
            int time=sample.getIntTime(96);

            double apstep=Math.max(1,apmax[time]/maxSteps);
            double amstep=Math.max(1,ammax[time]/maxSteps);
            double rpstep=Math.max(1,rpmax[time]/maxSteps);
            double rmstep=Math.max(1,rmmax[time]/maxSteps);

            double ap;
            double am;
            double rp;
            double rm;

            double sumOfProba=0;

            SimpleMatrix res=new SimpleMatrix(5,1);
            res.set(0,sample.getDoubleTime());

            for(ap=0;ap<=apmax[time]*maxmultiplier;ap+=apstep) {
                res.set(1, ap);
                for (am = 0; am <= ammax[time] * maxmultiplier; am += amstep) {
                    res.set(2, am);
                    for (rp = 0; rp <= rpmax[time] * maxmultiplier; rp += rpstep) {
                        res.set(3, rp);
                        for (rm = 0; rm <= rmmax[time] * maxmultiplier; rm += rmstep) {
                            res.set(4, rm);
                            sumOfProba += sampleDistribution.evaluate(res);
                        }
                    }
                }
            }

            return proba/sumOfProba;*/
        }
        else {
            return 0;
        }
    }


    @Override
    public double[][] getProbabilities(double[] x, double[] y) {
        double[][] z = new double[y.length][x.length];
        for (int i = 0; i < x.length; i++)
            for (int j = 0; j < y.length; j++) {
                double[][] point = { { x[i] }, {y[j]}, {0},{0},{0}};
                SimpleMatrix pointVector = new SimpleMatrix(point);
                z[j][i] = sampleDistribution.evaluate(pointVector);
            }
        return z;
    }



}
