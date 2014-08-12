package org.kevoree.brain.learning;


import org.kevoree.brain.api.InputTrainingSet;
import org.kevoree.brain.api.InputVector;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class GaussianKernelLearning extends AbstractLearning {
    @Override
    public void learn(InputTrainingSet its) {

        int dim = its.getFeaturesDimension();
        int m= its.getTrainingSetSize();

        double[] means = new double[dim];
        double[] variances = new double[dim];


        //Calculate means over features
        for(InputVector xi: its.getTraining()){
            for(int i=0;i<dim;i++){
                means[i]+=xi.getFeatures()[i];
            }
        }
        for(int i=0;i<dim;i++){
            means[i] = means[i]/m;
        }

        //Calculate variances over features
        for(InputVector xi: its.getTraining()){
            for(int i=0;i<dim;i++){
                variances[i]+=(xi.getFeatures()[i]-means[i])*(xi.getFeatures()[i]-means[i]);
            }
        }
        for(int i=0;i<dim;i++){
            variances[i] = variances[i]/m;
        }

        //Calculate the density of the multivariate normal at each data point (row) of X
        double[] p = new double[m];
        for(int i=0; i<m;i++)
        {
            p[i] = GaussianEstimate(its.getTraining().get(i).getFeatures(),means,variances);
        }

    }


    private double GaussianEstimate(double[] features, double[] means, double[] variances){

    }


    @Override
    public int predict(InputVector iv) {
        return 0;
    }
}
