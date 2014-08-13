package org.kevoree.brain.learning;

import org.kevoree.brain.api.classifier.Classifier;
import org.kevoree.brain.statistic.StatLibrary;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by assaa_000 on 8/13/2014.
 */
public class KmeansLearning implements Classifier {
    private String[] featuresNames;

    private ArrayList<Double[]> trainingX = new ArrayList<Double[]>();
    private ArrayList<Integer> trainingY = new ArrayList<Integer>();
    private int numberOfClusters=2;
    private int maxIter=1000;
    private static Random rand = new Random();

    private ArrayList<double[]> centers;


    public KmeansLearning(int numberOfClusters, int maxIter){
        this.numberOfClusters=numberOfClusters;
        this.maxIter=maxIter;
    }

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
        // serialize centers
        return new Byte[0];
    }

    @Override
    public void setState(Byte[] state) {
        //To do
        //Load centers
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
            m=(int)(0.4*size);
            mVal=size-m;
        }



        double maxF1=0;
        ArrayList<double[]> bestCenters;


        ArrayList<Integer> crossValY = new ArrayList<Integer>(mVal);
        ArrayList<Object[]> crossValX = new ArrayList<Object[]>(mVal);
        for(int i=m; i<size;i++)
        {
            crossValY.add(trainingY.get(i));
            crossValX.add(trainingX.get(i));
        }

        //Iterate over maximum iterations
        for(int iter=0; iter<maxIter;iter++){

            centers= new ArrayList<double[]>();
            for(int clusterId=0; clusterId<numberOfClusters;clusterId++){
                double[] measures= new double[dim];
                int init= rand.nextInt(m);
                for(int j=0;j<dim;j++){
                    measures[j]=trainingX.get(init)[j];
                }
                centers.add(measures);
            }

            int[] result = new int[m];

            int counter=0;
            ArrayList<double[]> previous;
            do {
                counter++;
                previous = backupCenters();
                //First evaluate using current centers
                for (int j = 0; j < m; j++) {
                    result[j] = evaluate(trainingX.get(j));
                }

                //Update centers to empty
                centers = new ArrayList<double[]>();
                for (int clusterId = 0; clusterId < numberOfClusters; clusterId++) {
                    double[] measures = new double[dim];
                    centers.add(measures);
                }
                int[] totals = new int[numberOfClusters];

                //Calculate new centers
                for (int j = 0; j < m; j++) {
                    for (int i = 0; i < dim; i++) {
                        centers.get(result[j])[i] += trainingX.get(j)[i];
                        totals[result[j]]++;
                    }
                }

                for (int clusterId = 0; clusterId < numberOfClusters; clusterId++) {
                    for (int i = 0; i < dim; i++) {
                        if (totals[clusterId] == 0)
                            continue;
                        centers.get(clusterId)[i] = centers.get(clusterId)[i] / totals[clusterId];
                    }
                }
            } while(counter<100 && notSameCenters(previous)); //or centers are the same to be implemented
            //System.out.println(counter);
            double f1= StatLibrary.calculateF1(crossValX,crossValY,this);
            if(f1>maxF1){
                maxF1=f1;
                bestCenters=backupCenters();
            }

        }
        centers=backupCenters();
        System.out.println("Best F1 "+ maxF1);
    }

    private boolean notSameCenters(ArrayList<double[]> previous ){
        for(int i=0; i<previous.size();i++){
            for(int j=0; j<previous.get(i).length; j++){
                if(previous.get(i)[j]!=centers.get(i)[j])
                    return true;
            }
        }
       // System.out.println("same center");
        return false;

    }

    private ArrayList<double[]> backupCenters(){
        ArrayList<double[]> backup = new ArrayList<double[]>(centers.size());

        for(double[] dd: centers){
            double[] tempd = new double[dd.length];
            for(int i=0; i<dd.length;i++)
                tempd[i]=dd[i];
            backup.add(tempd);
        }

        return backup;
    }

    @Override
    public void printState() {
        int dim= centers.get(0).length;
        for(int clusterId=0; clusterId<numberOfClusters;clusterId++){
            System.out.print("Cluster "+clusterId+": ");
            for(int j=0 ; j<dim;j++){
                System.out.print(centers.get(clusterId)[j]+" , ");
            }
            System.out.println();
        }

    }

    @Override
    public int evaluate(Object[] features) {

        int dim= trainingX.get(0).length;
        double maxdistance=-1;
        int currentCluster=0;
        for(int clusterId=0; clusterId<numberOfClusters;clusterId++){
            double distance =0;
            double temp=0;
            for(int j=0 ; j<dim;j++){
                temp= centers.get(clusterId)[j]- ((Double)features[j]).doubleValue();
                distance += temp*temp;
            }
            if(maxdistance==-1 || distance<maxdistance){
                maxdistance=distance;
                currentCluster=clusterId;
            }
        }
        return currentCluster;
    }
}
