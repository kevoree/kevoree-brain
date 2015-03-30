package org.kevoree.brain.smartgrid.Profilers;

/**
 * Created by assaad on 20/02/15.
 */
public class BaysianGaussianSubState {

    public double getSumSquares() {
        return sumSquares;
    }

    public void setSumSquares(double sumSquares) {
        this.sumSquares = sumSquares;
    }

    private double sumSquares=0;
    private double sum=0;
    private int nb = 0;

    public int getNb() {
        return nb;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }


    public double calculateProbability(double fet){
        double avg= sum/ nb;
        double variances= sumSquares/nb-avg*avg;
        if(variances!=0){
            double ress= (1/Math.sqrt(2*Math.PI*variances))*Math.exp(-((fet-avg)*(fet-avg))/(2*variances));
            if(ress>0.00001){
                return 1;
            }
            else {
                return 0;
            }

        }
        else{
            if(fet==avg){
                return 1;
            }
        }
        return 0;
    }

    public Double getAverage(){
        if(nb!=0) {
            Double avg= sum / nb;
            return avg;
        }
        else
            return null;
    }

      public void train(Object feature){
        Double fet=(Double) feature;
        sum += fet;
        sumSquares += fet * fet;
        nb++;
    }

    public Double getVariance(){
        if(nb!=0) {
            double avg= sum / nb;
            //sum / nb
            Double newvar=sumSquares/nb-avg*avg;
            return newvar;
        }
        else
            return null;
    }

    public void clear(){
        nb=0;
        sum=0;
        sumSquares=0;
    }




}
