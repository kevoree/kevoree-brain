package org.kevoree.brain.learning;


import org.kevoree.brain.api.classifier.Classifier;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class GaussianKernelLearning implements Classifier {

    private String[] featuresNames;
    private ArrayList<Double[]> trainingX = new ArrayList<Double[]>();
    private ArrayList<Integer> trainingY = new ArrayList<Integer>();

    private ArrayList<Double[]> crossValX = new ArrayList<Double[]>();
    private ArrayList<Integer> crossValY = new ArrayList<Integer>();

    private double[] means;
    private double[] variances;
    private double eps=0;


    @Override
    public void setFeatureNames(String[] features) {
        this.featuresNames=features;
    }

    @Override
    public void addTrainingSet(Object[] features, int supervisedClass) {
        Double[] fd = new Double[features.length];
        for(int i=0; i<features.length; i++){
            fd[i]=(Double) features[i];
        }
        trainingX.add(fd);
        trainingY.add(supervisedClass);
    }

    @Override
    public void addCrossValSet(Object[] features, int supervisedClass) {
        Double[] fd = new Double[features.length];
        for(int i=0; i<features.length; i++){
            fd[i]=(Double) features[i];
        }
        crossValX.add(fd);
        crossValY.add(supervisedClass);
    }

    public void addTrainingSet(Double[] features, int supervisedClass) {
        trainingX.add(features);
        trainingY.add(supervisedClass);
    }

    public void addCrossValSet(Double[] features, int supervisedClass) {
        crossValX.add(features);
        crossValY.add(supervisedClass);
    }

    @Override
    public void update() {
        if(trainingX.size()==0)
            return;

        int dim= trainingX.get(0).length;
        int m= trainingX.size();
        int mVal= crossValX.size();

        means = new double[dim];
        variances =new double[dim];

        //Calculate means over features
        for(int j=0; j<m;j++){
            for(int i=0;i<dim;i++){
                means[i]+= trainingX.get(j)[i];
            }
        }

      //  System.out.println("Means vector: ");

        for(int i=0;i<dim;i++){
            means[i] = means[i]/m;
            //System.out.print(means[i]+" ");
        }
        //System.out.println();

       // System.out.println("Variance vector: ");
        //Calculate variances over features
        for(int j=0; j<m;j++){
            for(int i=0;i<dim;i++){
                variances[i]+=(trainingX.get(j)[i]-means[i])*(trainingX.get(j)[i]-means[i]);
            }
        }
        for(int i=0;i<dim;i++){
            variances[i] = variances[i]/m;
            //System.out.print(variances[i]+" ");
        }
      //  System.out.println();

        //Calculate the density of the multivariate normal at each data point (row) of X
        double[] p = new double[mVal];
        for(int i=0; i<mVal;i++)
        {
            p[i] = gaussianEstimate(crossValX.get(i),means,variances);
        }

        // Search p min
        double pmin = p[0];
        for(int i=0; i<p.length;i++){
            if(p[i]<pmin)
                pmin=p[i];
        }
        // Search p max
        double pmax = p[0];
        for(int i=0; i<p.length;i++){
            if(p[i]>pmax)
                pmax=p[i];
        }
        //Search for best epsilon
        double stepsize = (pmax-pmin)/1000;
        double epsilon=0;
        double bestEpsilon=0;
        double bestF1=0;
        double f1=0;

        for(epsilon=pmin; epsilon<pmax; epsilon+=stepsize){
            f1=calculateF1(epsilon, p, crossValY);
            if(f1>bestF1){
                bestF1=f1;
                bestEpsilon=epsilon;
            }
        }
        eps=bestEpsilon;
      //  System.out.println("Best epsilon: "+eps);
       // System.out.println("Best F1: "+bestF1);
    }

    private double calculateF1(double epsilon, double[] p, ArrayList<Integer> data){
        int tp=0; // true positives  - the ground truth label says it's an anomaly and our algorithm correctly classied it as an anomaly
        int fp=0; // false positives - the ground truth label says it's not an anomaly, but our algorithm incorrectly classied it as an anomaly.
        int fn=0; // false negatives - the ground truth label says it's an anomaly, but our algorithm incorrectly classied it as not being anomalous.


        for(int i=0; i<p.length; i++){
            boolean prediction = (p[i]<epsilon);
            if(prediction== true && data.get(i)==0)
                fp++;
            if(prediction== false && data.get(i)==1)
                fn++;
            if(prediction== true && data.get(i)==1)
                tp++;
        }


        double prec= ((double) tp)/(tp+fp);
        double rec= ((double) tp)/(tp+fn);

        double f1=2*prec*rec/(prec+rec);
        return f1;

    }

    private double gaussianEstimate(Object[] features, double[] means, double[] variances){
        int dim= features.length;
        double p=1;
        double x=0;

        for(int i=0; i<dim; i++){
            x=((Double) features[i]).doubleValue();
            p= p* (1/Math.sqrt(2*Math.PI*variances[i]))*Math.exp(-((x-means[i])*(x-means[i]))/(2*variances[i]));
        }
        return p;

    }

    @Override
    public int eval(Object[] features) {
        return 0;
    }
}
