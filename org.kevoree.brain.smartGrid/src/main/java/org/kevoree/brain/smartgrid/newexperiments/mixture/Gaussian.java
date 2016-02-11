package org.kevoree.brain.smartgrid.newexperiments.mixture;

/**
 * Created by assaad on 11/02/16.
 */
public class Gaussian extends Component{

    private int features;

    private double[] min;
    private double[] max;
    private double[] sum;
    private double[][] sumsquares;
    private int total;

    //return features between 0 and 1 -> instead of [min,max]
    public double[] normalizeMinMax(double[] f){
        double[] res = new double[features];
        for(int i=0;i<features;i++){
            if(max[i]==min[i]){
                res[i]=0.5;
            }
            else {
                res[i]=(f[i]-min[i])/(max[i]-min[i]);
            }
        }
        return res;
    }

    public Gaussian(){

    }

    public Gaussian(double[] features){
        this.feed(features);
    }

    private void init() {
        min = new double[features];
        max = new double[features];
        sum = new double[features];
        sumsquares = new double[features][features];

        for (int j = 0; j < features; j++) {
            min[j] = Double.NaN;
            max[j] = Double.NaN;
        }
    }

    @Override
    public double[] getAvg(){
        double[] res= new double[features];
        for(int i=0;i<features;i++){
            res[i]=getAvg(i);
        }
        return res;
    }

    public double getAvg(int feature){
        if(total!=0){
            return sum[feature]/total;
        }
        else {
            return Double.NaN;
        }
    }

    public int getTotal(){
        return total;
    }


    @Override
    public boolean checkInside(double[] features, double[] err) {
        //to implement
        return false;
    }

    @Override
    public void feed(double[] feat){
        if(min==null){
            this.features=feat.length;
            init();
        }
        total++;

        for(int i=0;i<features;i++){
            if(Double.isNaN(min[i])||  feat[i]<min[i]){
                min[i]=feat[i];
            }

            if(Double.isNaN(max[i])||  feat[i]>max[i]){
                max[i]=feat[i];
            }

            sum[i]+=feat[i];
        }


        for(int i=0;i<features;i++){
            for(int j=0;j<features;j++){
                sumsquares[i][j]+=feat[i]*feat[j];
            }
        }
    }

    @Override
    public double[][] getCovariance(double[] means){

        if(total>1) {
            double[][] covariances = new double[means.length][means.length];

            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    covariances[i][j] = (sumsquares[i][j]) / (total - 1) - means[i] * means[j] * total / (total - 1);
                    covariances[j][i] = covariances[i][j];
                }
            }
            return covariances;
        }
        else return null;
    }
}
