package org.kevoree.brain.smartgrid.util;

import org.ejml.simple.SimpleMatrix;

import java.time.LocalDateTime;

/**
 * Created by assaad on 19/02/15.
 */
public class ElectricMeasure {

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    private Long time;
    public double aplus;
    public double aminus;
    public double rplus;
    public double rminus;


    private double convertTime(Long timestamp){
        java.sql.Timestamp tiempoint= new java.sql.Timestamp(timestamp);
        LocalDateTime ldt= tiempoint.toLocalDateTime();
        double res= ((double)ldt.getHour())/24+((double)ldt.getMinute())/(24*60)+((double)ldt.getSecond())/(24*60*60);
        return res;
    }

    //get time in 15 minutes chunks
    public int getIntTime(int max){
        java.sql.Timestamp tiempoint= new java.sql.Timestamp(time);
        LocalDateTime ldt= tiempoint.toLocalDateTime();
        int res= (ldt.getHour()*60+ldt.getMinute())*max/1440;
        return res;
    }
    public double getDoubleTime(){
        return convertTime(time)*24;
    }


    public double[] getArrayFeatures(){
        double[] features = new double[4];
        features[0]=aplus;
        features[1]=aminus;
        features[2]=rplus;
        features[3]=rminus;
        return features;
    }



    public SimpleMatrix convertToMatrix(){
        SimpleMatrix res=new SimpleMatrix(5,1);
        res.set(0,getDoubleTime());
        res.set(1,aplus);
        res.set(2,aminus);
        res.set(3,rplus);
        res.set(4,rminus);
        return res;
    }

    public SimpleMatrix convertToMatrixPerTime(){
        SimpleMatrix res=new SimpleMatrix(4,1);
        res.set(0,aplus);
        res.set(1,aminus);
        res.set(2,rplus);
        res.set(3,rminus);
        return res;
    }

    public static double[][] getCovariance(){
        double[][] c = new double[5][5];

        return c;
    }

    public static double[][] getCovariancePerTime(){
        double[][] c = new double[4][4];

        return c;
    }
    public static double[][] getCovariancePerEnergy() {
        double[][] c = new double[2][2];
        return c;
    }

    public SimpleMatrix convertToEnergyMatrix(int ienergy) {
        SimpleMatrix res=new SimpleMatrix(2,1);
        res.set(0,getDoubleTime());
        if(ienergy==0) {
            res.set(1, aplus);
        }
        else if(ienergy==1){
            res.set(1, aminus);
        }
        else if(ienergy==2){
            res.set(1, rplus);
        }
        else if(ienergy==3){
            res.set(1, rminus);
        }
        return res;
    }
}
