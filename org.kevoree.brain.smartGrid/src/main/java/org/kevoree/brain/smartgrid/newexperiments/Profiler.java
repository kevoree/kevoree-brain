package org.kevoree.brain.smartgrid.newexperiments;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;

import java.util.ArrayList;

/**
 * Created by assaad on 19/02/15.
 */
public class Profiler {


    private int features;
    private int timeStep;

    private double[][] min;
    private double[][] max;
    private double[][] sum;
    private double[][] variance;
    private int total[];
    private int overallTotal;


    private void init(){
        min= new double[timeStep][features];
        max= new double[timeStep][features];
        sum= new double[timeStep][features];
        variance= new double[timeStep][features*features];
        total=new int[timeStep];


        for(int i=0;i<timeStep;i++){
            for(int j=0;j<features;j++){
                min[i][j]= Double.NaN;
                max[i][j]=Double.NaN;
            }
        }

    }


    public Profiler(int timeStep, int features){
        this.timeStep=timeStep;
        this.features=features;
        init();
    }

    public Profiler(){
        this.timeStep=96;
        this.features=4;
        init();
    }


    public void feed(ElectricMeasure em){
        int time=em.getIntTime(timeStep);
        total[time]++;
        overallTotal++;

        double[] feat= em.getArrayFeatures();

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

    public void feed(ArrayList<ElectricMeasure> samples){
        for(int i=0;i<samples.size();i++){
            feed(samples.get(i));
        }
    }


    public String getHeader(int f) {
        StringBuilder sb= new StringBuilder();
        sb.append("userId,");
        for(int i=0;i<timeStep;i++){
            for(int j=0;j<f;j++){
          //      sb.append("min["+i+"]["+j+"],");
            //    sb.append("max["+i+"]["+j+"],");
                sb.append("avg["+i+"]["+j+"],");
            }
        }
        return sb.toString();
    }

    public double[] getFingerprint(){
        double[] result = new double[timeStep*features];
        int count=0;
        for(int i=0;i<timeStep;i++) {
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
        for(int i=0;i<timeStep;i++){
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
