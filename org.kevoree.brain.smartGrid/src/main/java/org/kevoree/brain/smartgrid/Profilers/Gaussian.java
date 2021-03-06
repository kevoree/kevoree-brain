package org.kevoree.brain.smartgrid.profilers;

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
        double q=1;
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
            q=q*(1/Math.sqrt(2*Math.PI*variances[i]));
        }
        return p;
    }

    public double calculateProbabilityAplus(double aplus){
        if(nb==0){
            return 0;
        }
        double avg;
        double variance;


            avg=sum[0] / nb;
            variance=sumSquares[0]/nb-avg*avg;
            if(variance==0){
                if(avg==aplus){
                    return 1;
                }
                else{
                    return 0;
                }
            }
            return (1/Math.sqrt(2*Math.PI*variance))*Math.exp(-((aplus-avg)*(aplus-avg))/(2*variance));
    }


    public boolean calculateBooleanDecision(double aplus, double numOfVar){
        if(nb==0){
            return false;
        }
        double avg;
        double variance;


        avg=sum[0] / nb;
        variance=sumSquares[0]/nb-avg*avg;
        if(variance==0){
            if(avg==aplus){
                return true;
            }
            else{
                return false;
            }
        }
        return (Math.abs(aplus-avg)<=numOfVar*Math.sqrt(variance));
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

}
