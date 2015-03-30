package org.kevoree.brain.smartgrid.Profilers;

import org.kevoree.brain.smartgrid.ElectricMeasure;
import org.kevoree.brain.smartgrid.EnumSubstate;

import java.util.ArrayList;

/**
 * Created by assaad on 20/02/15.
 */
public class BayesianClassifier {
    private BaysianGaussianSubState[][][] states; //BayesianSubstate[numOfClasses+1][timeStep][numOfFeatures];
    private int timeStep=96;
    private EnumSubstate[] classStats=new EnumSubstate[timeStep];

    private int numOfFeatures;
    private int numOfClasses;

    private  static String stateSep="/";
    private static String interStateSep="|";

    public void initialize (int numOfFeatures,int numOfClasses) {
        //TODO write cases

        this.numOfFeatures = numOfFeatures;
        this.numOfClasses =numOfClasses; //TODO fill the number of possible classes

        states = new BaysianGaussianSubState[numOfClasses + 1][timeStep][numOfFeatures];



        for (int i = 0; i < numOfClasses + 1; i++) {
            for (int j = 0; j < timeStep; j++) {
                classStats[j]=new EnumSubstate();
                classStats[j].initialize(numOfClasses);
                for (int k = 0; k < numOfFeatures; k++) {
                    states[i][j][k] = new BaysianGaussianSubState();
                }
            }
        }

    }

    public double[] predict(ElectricMeasure em){

        int time=em.getIntTime(timeStep);
        double[] result=new double[numOfClasses];
        double[] features=em.getArrayFeatures();

        double probaX=1;
        for(int j=0;j<numOfFeatures;j++) {
            probaX=probaX*states[numOfClasses][time][j].calculateProbability(features[j]);
        }
        if(probaX==0){
            //System.out.println("never happened");
           // return result;
            probaX=1;
        }

        for(int i=0;i<numOfClasses;i++){
            result[i]=classStats[time].calculateProbability(i);
            for(int j=0;j<numOfFeatures;j++) {
                result[i]=  result[i]*states[i][time][j].calculateProbability(features[j]);
            }
            result[i]=result[i]/probaX;
        }
        return result;
    }

    public void train(ElectricMeasure em, int classNum){
        int time=em.getIntTime(timeStep);
        double[] features=em.getArrayFeatures();

        for(int i=0;i<numOfFeatures;i++){
            states[classNum][time][i].train(features[i]);     //Train the class
            states[numOfClasses][time][i].train(features[i]); //Train the total
        }
        classStats[time].train(classNum);

    }


    public void trainArray(ArrayList<ElectricMeasure> electricMeasures, int user) {
        for(ElectricMeasure em: electricMeasures){
            train(em,user);
        }
    }
}
