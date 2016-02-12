package org.kevoree.brain.learning;

/**
 * Created by assaad on 27/08/15.
 */
public class MultivariateGaussian {
    double[] sum;
    double[][] sumsquares;
    int counter=0;

    public void train (double[] features){
        if(sum==null){
            sum=new double[features.length];
            sumsquares=new double[features.length][features.length];
        }

        for(int i=0;i<features.length;i++){
            sum[i]+=features[i];
            for(int j=0;j<features.length;j++){
                sumsquares[i][j]+=features[i]*features[j];
            }
        }
        counter++;
    }

    public double[] getMeans(){
        double[] means = new double[sum.length];
        for(int i=0;i<sum.length;i++){
            means[i]=sum[i]/counter;
        }
        return means;
    }

    public double[][] getCovariance(double[] means){
        double[][] covariances=new double[means.length][means.length];

        for(int i=0; i<means.length;i++){
            for(int j=i;j<means.length;j++){
                covariances[i][j]=(sumsquares[i][j])/(counter-1)-means[i]*means[j]*counter/(counter-1);
                covariances[j][i]=covariances[i][j];
            }
        }
        return covariances;
    }


}
