package org.kevoree.brain.smartgrid.newexperiments.mixture;


import org.kevoree.brain.smartgrid.util.MultivariateNormalDistribution;

/**
 * Created by assaad on 11/02/16.
 */
public class Gaussian extends Component{

    private int features;

    private double[] min;
    private double[] max;
    private double[] sum;
    private double[] sumsquares;
    private int total;

    public Gaussian() {
    }

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


    public int getFeatures(){
        return features;
    }

    public Gaussian(double[] features){
        this.feed(features);
    }


    @Override
    public double[] getAvg(){
        double[] res= new double[features];
        if(total==0){
            System.out.println("Impossible avg[]");
            return null;
        }
        else if(total==1){
            System.arraycopy(min, 0, res, 0, features);
            return res;
        }
        else {
            for (int i = 0; i < features; i++) {
                res[i] = sum[i]/total;
            }
            return res;
        }
    }

    public double getAvg(int feature){
        if(total==1){
            return min[feature];
        }
        if(total>1){
            return sum[feature]/total;
        }
        else {
            System.out.println("Impossible avg");
            return Double.NaN;
        }
    }

    public int getTotal(){
        return total;
    }


    @Override
    public boolean checkInside(double[] feat, double[] err) {

        //for dirac
        if(total==1){
            for(int i=0;i<features;i++){
                if(Math.abs(feat[i]-min[i])>err[i]){
                    return false;
                }
            }
            return true;
        }

        //for gaussian
        try {
            double[] avg = getAvg();
            double[][] cov = this.getCovariance(avg);
            double pAvg = getProbability(avg, avg, cov);
            double pX = getProbability(feat, avg, cov);
            pX=pX/pAvg;
            return pX > 0.5;
        }
        catch (Exception ex){
            System.out.println("Error inside gaussian");
            return false;
        }
    }

    @Override
    public void feed(double[] feat){

        //dirac case
        if(min==null){
            //In case of dirac, create a vector in min and that's it
            this.features=feat.length;
            min = new double[features];
            System.arraycopy(feat, 0, min, 0, feat.length);
            total++;
            this.incWeight();
            return;
        }

        if(total==1){
            //Create a real gaussian from the previously stored vector in min
            max = new double[features];
            sum = new double[features];
            System.arraycopy(min, 0, max, 0, this.features);
            System.arraycopy(min, 0, sum, 0, this.features);
            sumsquares = new double[features*(features+1)/2];
            int count=0;
            for(int i=0;i<features;i++){
                for(int j=i;j<features;j++){
                    sumsquares[count]=min[i]*min[j];
                    count++;
                }
            }
        }

        //Update the values
        for(int i=0;i<features;i++){
            if(feat[i]<min[i]){
                min[i]=feat[i];
            }

            if(feat[i]>max[i]){
                max[i]=feat[i];
            }
            sum[i]+=feat[i];
        }

        int count=0;
        for(int i=0;i<features;i++){
            for(int j=i;j<features;j++){
                sumsquares[count]+=feat[i]*feat[j];
                count++;
            }
        }
        total++;
        this.incWeight();
    }

    public void addComponent(Gaussian component) {
        if(min==null){
            this.features=component.features;
            min = new double[features];
            if(component.total==1){
                System.arraycopy(component.min, 0, min, 0, features);
            }
            else {
                max= new double[features];
                sum= new double[features];
                sumsquares= new double[features*(features+1)/2];
                System.arraycopy(component.max, 0, max, 0, features);
                System.arraycopy(component.sum, 0, sum, 0, features);
                System.arraycopy(component.sumsquares, 0, sumsquares, 0, features*(features+1)/2);
            }
        }
        else {
            if(total==1){
                //Create a real gaussian from the previously stored vector in min
                max = new double[features];
                sum = new double[features];
                System.arraycopy(min, 0, max, 0, features);
                System.arraycopy(min, 0, sum, 0, features);
                sumsquares = new double[features*(features+1)/2];
                int count=0;
                for(int i=0;i<features;i++){
                    for(int j=i;j<features;j++){
                        sumsquares[count]=min[i]*min[j];
                        count++;
                    }
                }
            }

            if(component.total>1) {
                for (int i = 0; i < features; i++) {
                    min[i] = Math.min(min[i], component.min[i]);
                    max[i] = Math.max(max[i], component.max[i]);
                    sum[i] = sum[i] + component.sum[i];
                }
                for (int j = 0; j < features * (features + 1) / 2; j++) {
                    sumsquares[j] = sumsquares[j] + component.sumsquares[j];
                }
            }
            else {
                for (int i = 0; i < features; i++) {
                    min[i] = Math.min(min[i], component.min[i]);
                    max[i] = Math.max(max[i], component.min[i]);
                    sum[i] = sum[i] + component.min[i];
                }
                int count=0;
                for(int i=0;i<features;i++){
                    for(int j=i;j<features;j++){
                        sumsquares[count]= sumsquares[count]+component.min[i]*component.min[j];
                        count++;
                    }
                }
            }
        }

        total+=component.total;
        this.weight += component.weight;
    }

    public double getProbability(double[] features, double[] avg, double[][] cov){

        MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(avg,cov);
        return mnd.density(features);
    }

    @Override
    public double[][] getCovariance(double[] means){
        if(total>1) {
            double[][] covariances = new double[features][features];


            double correction=total;
            correction=correction/(total-1);

            int count=0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    covariances[i][j] = (sumsquares[count] / total - means[i] * means[j]) * correction;
                    covariances[j][i]=covariances[i][j];
                    count++;
                }
            }
            return covariances;
        }
        else return null;
    }
}
