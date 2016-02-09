package org.kevoree.brain.smartgrid.profilers;

import org.kevoree.brain.smartgrid.util.ContextSolver;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 31/03/15.
 */
public class PaperProfiler extends  Profiler {


    private Gaussian[] uniqueState =new Gaussian[timeStep];

    private Gaussian[][] workingdays=new Gaussian[2][timeStep];
    private Gaussian[][] temperature=new Gaussian[2][timeStep];
    private Gaussian[][] Residentialtype=new Gaussian[2][timeStep];

    private Gaussian[][] contextStates =new Gaussian[8][timeStep];


    @Override
    public void feedMeasure(ElectricMeasure sample) {
        int time=sample.getIntTime(timeStep);
        double[] feats= sample.getArrayFeatures();

        uniqueState[time].train(feats);

        workingdays[ContextSolver.getWeekdayClassification(sample.getTime())][time].train(feats);
        temperature[ContextSolver.getTemperatureClassification(sample.getTime())][time].train(feats);
        Residentialtype[ContextSolver.getConsumerClassification(getUserId())][time].train(feats);
        contextStates[ContextSolver.getProfileNum(sample.getTime(),getUserId())][time].train(feats);

    }

    @Override
    public void feedArray(ArrayList<ElectricMeasure> samples) {
        for(int i=0;i<timeStep;i++){

            uniqueState[i]=new Gaussian();

            workingdays[0][i]=new Gaussian();
            temperature[0][i]=new Gaussian();
            Residentialtype[0][i]=new Gaussian();

            workingdays[1][i]=new Gaussian();
            temperature[1][i]=new Gaussian();
            Residentialtype[1][i]=new Gaussian();

            contextStates[0][i]=new Gaussian();
            contextStates[1][i]=new Gaussian();
            contextStates[2][i]=new Gaussian();
            contextStates[3][i]=new Gaussian();
            contextStates[4][i]=new Gaussian();
            contextStates[5][i]=new Gaussian();
            contextStates[6][i]=new Gaussian();
            contextStates[7][i]=new Gaussian();
        }

        for(int i=0;i<samples.size();i++){
            feedMeasure(samples.get(i));
        }
    }


    public double[] getDifferentProba(ElectricMeasure sample) {
        double[] results = new double[5];
        int time=sample.getIntTime(timeStep);
        double[] feats= sample.getArrayFeatures();

        results[0]=uniqueState[time].calculateProbabilityAplus(sample.aplus);
        results[1]=workingdays[ContextSolver.getWeekdayClassification(sample.getTime())][time].calculateProbabilityAplus(sample.aplus);
        results[2]=temperature[ContextSolver.getTemperatureClassification(sample.getTime())][time].calculateProbabilityAplus(sample.aplus);
        results[3]=Residentialtype[ContextSolver.getConsumerClassification(getUserId())][time].calculateProbabilityAplus(sample.aplus);
        results[4]= contextStates[ContextSolver.getProfileNum(sample.getTime(),getUserId())][time].calculateProbabilityAplus(sample.aplus);
        return results;
    }

    public boolean[] getDifferentDecisions(ElectricMeasure sample, double numOfVar) {
        boolean[] results = new boolean[5];

        int time=sample.getIntTime(timeStep);
        double[] feats= sample.getArrayFeatures();



        results[0]=uniqueState[time].calculateBooleanDecision(sample.aplus,numOfVar);
        results[1]=workingdays[ContextSolver.getWeekdayClassification(sample.getTime())][time].calculateBooleanDecision(sample.aplus,numOfVar);
        results[2]=temperature[ContextSolver.getTemperatureClassification(sample.getTime())][time].calculateBooleanDecision(sample.aplus,numOfVar);
        results[3]=Residentialtype[ContextSolver.getConsumerClassification(getUserId())][time].calculateBooleanDecision(sample.aplus,numOfVar);
        results[4]= contextStates[ContextSolver.getProfileNum(sample.getTime(), getUserId())][time].calculateBooleanDecision(sample.aplus,numOfVar);
        return results;
    }

    @Override
    public double getProba(ElectricMeasure sample) {
        return 0;
    }

    @Override
    public double[][] getProbabilities(double[] x, double[] y) {
        return new double[0][];
    }
}
