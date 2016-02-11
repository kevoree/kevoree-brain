package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.Random;
import java.util.TreeMap;

/**
 * Created by assaad on 09/02/16.
 */
public class Forecaster {
    private static Random rand=new Random();
    private static int iterations=20;
    private static double alpha=0.01;

    private int features;
    private int timeSteps;

    private double[][][] coeficients;
    private double[][] remainders;
    private int total[];
    private int overallTotal;

    public Forecaster(int timeSteps, int features){
        this.timeSteps=timeSteps;
        this.features=features;
        init();
    }

    private void init(){
        total=new int[timeSteps];
        coeficients=new double[timeSteps][features][features+1];
        remainders=new double[timeSteps][features];
        for(int i=0;i<timeSteps;i++){
            for(int j=0;j<features;j++){
                remainders[i][j]= rand.nextDouble() * 0.01;
                for(int k=0;k<features+1;k++){
                    if(j==k){
                        coeficients[i][j][k]=1+rand.nextDouble()*0.01;
                    }
                    else {
                        coeficients[i][j][k] = rand.nextDouble() * 0.01;
                    }
                }
            }
        }
    }


    public void feed(int timeSlot, Profiler electricProfiler, Profiler temperatureProfiler, ElectricMeasure prevCons, ElectricMeasure currentCons, double prevTemp, double currentTemp) {
        int prevSlot=(timeSlot-1);
        if(prevSlot<0){
            prevSlot+=timeSteps;
        }
        if(electricProfiler.getTotal(timeSlot)==0|| electricProfiler.getTotal(prevSlot)==0){
            return;
        }

        //train the matrix

        double[] deltaConsum = new double[features];
        double[] deltaConsumAvg=new double[features+1];
        double[] cp=currentCons.getArrayFeatures();
        double[] pp=prevCons.getArrayFeatures();


        for(int i=0;i<features;i++){
            deltaConsum[i]=cp[i]-pp[i];
            deltaConsumAvg[i]=electricProfiler.getAvg(timeSlot,i)-electricProfiler.getAvg(prevSlot,i);
        }
        deltaConsumAvg[features]=(currentTemp-temperatureProfiler.getAvg(timeSlot,0))-(prevTemp-temperatureProfiler.getAvg(prevSlot,0));



        for(int j=0; j<iterations;j++) {
            double[] h = calculate(timeSlot,deltaConsumAvg);


            for(int k=0;k<features;k++){
                double err= -alpha * (h[k] - deltaConsum[k]);

                for (int i = 0; i < features+1; i++) {
                    coeficients[timeSlot][k][i] = coeficients[timeSlot][k][i] + err * deltaConsumAvg[i];
                }
                remainders[timeSlot][k] =  remainders[timeSlot][k] + err;
            }
        }
        total[timeSlot]++;
        overallTotal++;
    }

    public ElectricMeasure predict(int timeSlot,  Profiler electricProfiler, Profiler temperatureProfiler, ElectricMeasure prevCons, double prevTemp, double currentTemp){
        int prevSlot=(timeSlot-1);
        if(prevSlot<0){
            prevSlot+=timeSteps;
        }
        if(electricProfiler.getTotal(timeSlot)==0|| electricProfiler.getTotal(prevSlot)==0){
            return null;
        }

        //calculate the result

        double[] deltaConsumAvg=new double[features+1];
        double[] pp=prevCons.getArrayFeatures();


        for(int i=0;i<features;i++){
            deltaConsumAvg[i]=electricProfiler.getAvg(timeSlot,i)-electricProfiler.getAvg(prevSlot,i);
        }
        deltaConsumAvg[features]=(currentTemp-temperatureProfiler.getAvg(timeSlot,0))-(prevTemp-temperatureProfiler.getAvg(prevSlot,0));

        double[] h = calculate(timeSlot,deltaConsumAvg);

        ElectricMeasure em = new ElectricMeasure();
        for(int i=0;i<features;i++) {
            em.setFeature(i,h[i] + pp[i]);
        }
        return em;
    }

    private double[] calculate(int timeSlot, double[] vals){
        double[] res=new double[features];
        for(int i=0;i<features;i++){
            for(int j=0;j<features+1;j++){
                res[i]+=coeficients[timeSlot][i][j]*vals[j];
            }
            res[i]+=remainders[timeSlot][i];
        }
        return res;

    }
}
