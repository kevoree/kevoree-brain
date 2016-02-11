package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 19/02/15.
 */
public class Profiler {


    private int features;
    private int timeSteps;

    private double[][] min;
    private double[][] max;
    private double[][] sum;
    private double[][] variance;
    private int total[];
    private int overallTotal;


    private void init(){
        min= new double[timeSteps][features];
        max= new double[timeSteps][features];
        sum= new double[timeSteps][features];
        variance= new double[timeSteps][features*features];
        total=new int[timeSteps];


        for(int i=0;i<timeSteps;i++){
            for(int j=0;j<features;j++){
                min[i][j]= Double.NaN;
                max[i][j]=Double.NaN;
            }
        }

    }

    public ElectricMeasure getAvgElectricMeasure(int timeSlot){
        ElectricMeasure em = new ElectricMeasure();
        for(int i=0;i<features;i++){
            em.setFeature(i,getAvg(timeSlot,i));
        }
        return em;
    }

    public double getAvg(int timeslot, int feature){
        if(total[timeslot]!=0){
            return sum[timeslot][feature]/total[timeslot];
        }
        else {
            return 0;
        }
    }

    public int getTotal(int timeslot){
        return total[timeslot];
    }

    public Profiler(int timeSteps, int features){
        this.timeSteps=timeSteps;
        this.features=features;
        init();
    }


    public void feed(int time, double[] feat){
        total[time]++;
        overallTotal++;

        for(int i=0;i<features;i++){
            if(Double.isNaN(min[time][i])||  feat[i]<min[time][i]){
                min[time][i]=feat[i];
            }

            if(Double.isNaN(max[time][i])||  feat[i]>max[time][i]){
                max[time][i]=feat[i];
            }

            sum[time][i]+=feat[i];
        }


        for(int i=0;i<features;i++){
            for(int j=0;j<features;j++){
                variance[i][j]+=feat[i]*feat[j];
            }
        }
    }



    public String getHeader(int f) {
        StringBuilder sb= new StringBuilder();
        sb.append("userId,");
        for(int i=0;i<timeSteps;i++){
            for(int j=0;j<f;j++){
          //      sb.append("min["+i+"]["+j+"],");
            //    sb.append("max["+i+"]["+j+"],");
                sb.append("avg["+i+"]["+j+"],");
            }
        }
        return sb.toString();
    }

    public double[] getFingerprint(){
        double[] result = new double[timeSteps*features];
        int count=0;
        for(int i=0;i<timeSteps;i++) {
            for (int j = 0; j < features; j++) {
                double avg=sum[i][j];
                if(total[i]!=0){
                    avg=avg/total[i];
                }
                else {
                    avg=0;
                }

                result[count]=avg;
                count++;
            }
        }
        return result;
    }

    public String export(int f) {
        StringBuilder sb= new StringBuilder();
        for(int i=0;i<timeSteps;i++){
            for(int j=0;j<f;j++){
             //   sb.append(min[i][j]+",");
            //    sb.append(max[i][j]+",");
                double avg=sum[i][j];
                if(total[i]!=0){
                    avg=avg/total[i];
                }
                else {
                    avg=0;
                }
                sb.append(avg+",");

            }
        }
        return sb.toString();
    }
}
