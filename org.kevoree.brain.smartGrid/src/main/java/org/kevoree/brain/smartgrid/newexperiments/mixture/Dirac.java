package org.kevoree.brain.smartgrid.newexperiments.mixture;


/**
 * Created by assaad on 11/02/16.
 */
public class Dirac extends Component {

    double[] features;
    public Dirac(double[] features){
        this.weight=1;
        this.features=features;
    }

    @Override
    public boolean checkInside(double[] f, double[] err) {
        for(int i=0;i<features.length;i++){
            if(Math.abs(f[i]-features[i])>err[i]){
                return false;
            }
        }
        return true;
    }

    @Override
    public void feed(double[] features) {
        this.features=features;
    }

    @Override
    public double[] getAvg() {
        return features;
    }

    @Override
    public double[][] getCovariance(double[] means) {
        return null;
    }
}
