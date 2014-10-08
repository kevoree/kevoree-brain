package org.kevoree.brain.learning;


import org.kevoree.brain.api.classifier.Classifier;
import org.kevoree.brain.util.StatLibrary;

import java.util.ArrayList;

/**
 * Created by assaa_000 on 8/12/2014.
 */
public class GaussianKernelLearning implements Classifier {

    private String[] featuresNames;
    private ArrayList<Double[]> trainingX = new ArrayList<Double[]>();
    private ArrayList<Integer> trainingY = new ArrayList<Integer>();


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
    public Byte[] getState() {
        //To do
        // serialize means, variance, eps
        return new Byte[0];
    }

    @Override
    public void setState(Byte[] state) {
        //To do
        //Load mean, variance, eps
    }



    @Override
    public void train() throws Exception {
        if(trainingX.size()==0)
            throw new Exception("Training set size is not enough");
        int dim= trainingX.get(0).length;

        int size= trainingX.size();
        int m;
        int mVal;

        if(size<=2) {
            throw new Exception("Training set size is not enough");
        }
        else if(size<10){
            m=size-2;
            mVal=2;
        }
        else
        {
            m=(int)(0.8*size);
            mVal=size-m;
        }

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
        ArrayList<Integer> crossValY = new ArrayList<Integer>(mVal);
        for(int i=m; i<size;i++)
        {
            p[i-m] = gaussianEstimate(trainingX.get(i));
            crossValY.add(trainingY.get(i));
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
            ArrayList<Integer> estimations = new ArrayList<Integer>(mVal);
            for(int j=0; j<mVal;j++){
                estimations.add(new Integer(eval(p[j],epsilon)));
            }

            f1= StatLibrary.calculateF1(estimations,crossValY);
            if(f1>bestF1){
                bestF1=f1;
                bestEpsilon=epsilon;
            }
        }
        eps=bestEpsilon;
      //  System.out.println("Best epsilon: "+eps);
       // System.out.println("Best F1: "+bestF1);
    }

    @Override
    public void printState() {
        if(means==null)
            return;
        System.out.println("Means vector: ");
        for(int i=0; i<means.length;i++){
            System.out.print(means[i]+" ");
        }
        System.out.println();
        System.out.println("Variance vector: ");
        for(int i=0; i<variances.length;i++){
            System.out.print(variances[i]+" ");
        }
        System.out.println();
        System.out.println("Best epsilon: "+eps);
    }



    public double gaussianEstimate(Object[] features){
        int dim= features.length;
        double p=1;
        double x=0;

        for(int i=0; i<dim; i++){
            x=((Double) features[i]).doubleValue();
            p= p* (1/Math.sqrt(2*Math.PI*variances[i]))*Math.exp(-((x-means[i])*(x-means[i]))/(2*variances[i]));
        }
        return p;

    }

    private int eval(double p, double epsilon){
        if(p<epsilon)
            return 1;
        else
            return 0;

    }

    @Override
    public int evaluate(Object[] features){
        double p= gaussianEstimate(features);
        if(p<eps)
            return 1;
        else
            return 0;
    }
}
