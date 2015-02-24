package org.kevoree.brain.smartgrid;

/**
 * Created by assaad on 20/02/15.
 */
public class Gaussian {


    public double[] getSumSquares() {
        return sumSquares;
    }

    public void setSumSquares(double[] sumSquares) {
        this.sumSquares = sumSquares;
    }

    private double[] sumSquares=null;
    private double[] sum=null;
    private int nb = 0;

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public double[] getSum() {
        return sum;
    }

    public void setSum(double[] sum) {
        this.sum = sum;
    }


    public double calculateProbability(double[] features){
        if(nb==0){
            return 0;
        }
        int size=sum.length;
        double[] avg= new double[size];
        double[] variances= new double[size];
        double p=1;
        for(int i=0;i<size;i++){
            avg[i]=sum[i] / nb;
            variances[i]=sumSquares[i]/nb-avg[i]*avg[i];
            if(variances[i]==0){
                if(avg[i]==features[i]){
                    continue;
                }
                else{
                    return 0;
                }
            }
            p= p* (1/Math.sqrt(2*Math.PI*variances[i]))*Math.exp(-((features[i]-avg[i])*(features[i]-avg[i]))/(2*variances[i]));
        }
        return p;
    }



    public double[] getAverage(){
        if(nb!=0) {
            int size=sum.length;
            double[] avg= new double[size];
            for(int i=0;i<size;i++){
                avg[i]=sum[i] / nb;
            }
            return avg;
        }
        else {
            return new double[4];

        }

    }

    public void train(double[] features){
        int size=features.length;
        if(nb==0){
            sumSquares=new double[size];
            sum=new double[size];
        }

        for(int i=0;i<size;i++) {
            sum[i] += features[i];
            sumSquares[i] += features[i] * features[i];
        }
        nb++;


    }

    public double[] getVariance(){
        if(nb!=0) {
            int size=sum.length;
            double[] avg= new double[size];
            //sum / nb
            double[] newvar= new double[size];
            for(int i=0;i<size;i++){
                avg[i]=sum[i] / nb;
                newvar[i]=sumSquares[i]/nb-avg[i]*avg[i];
            }
            return newvar;
        }
        else
            return new double[4];
    }

    public void clear(){
        nb=0;
        sum=null;
        sumSquares=null;
    }


    public double calculateProbability2(double[] features) {
        if(nb==0){
            return 0;
        }
        int size=sum.length;
        double[] avg= new double[size];
        double[] variances= new double[size];
        double p=1;
        for(int i=0;i<size;i++){
            avg[i]=sum[i] / nb;
            variances[i]=sumSquares[i]/nb-avg[i]*avg[i];
            if(variances[i]==0){
                if(avg[i]==features[i]){
                    continue;
                }
                else{
                    return 0;
                }
            }
            int res=(int) (Math.abs(features[i]-avg[i])/Math.sqrt(variances[i]));
            if(res<1){
                res=1;
            }

            p= p*(1.025-0.025*res);
        }
        return p;
    }
}
