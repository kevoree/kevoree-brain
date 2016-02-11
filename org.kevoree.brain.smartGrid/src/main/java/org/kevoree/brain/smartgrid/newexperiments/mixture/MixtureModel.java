package org.kevoree.brain.smartgrid.newexperiments.mixture;

import java.util.ArrayList;

/**
 * Created by assaad on 11/02/16.
 */
public class MixtureModel extends Component{

    private int level;
    private Configration config;





    private Component overallProfile;
    private ArrayList<Component> dataset;


    //root mixture
    public MixtureModel(double[] err, int level, Configration config){
        this.level=level;
        this.config=config;
        overallProfile=new Gaussian();
        if(level<config.maxLevels){
            dataset= new ArrayList<Component>(config.compression);
        }
    }



    public void feed(double[] feat){
        overallProfile.feed(feat);

        if(!checkInside(feat,config.err)) {
            if (dataset.size() < config.compression) {
                dataset.add(new Dirac(feat));

            } else {
                //need to compress code here
            }
        }
    }


    @Override
    public boolean checkInside(double[] features, double[] err) {
        if(this.level<config.maxLevels){
            for(Component c: dataset){
                if(checkInside(features, err)){
                    c.incWeight();
                    return true;
                }
            }
            return false;
        }
        else {
            if(overallProfile.checkInside(features,err)){
                overallProfile.incWeight();
                return true;
            }
            else {
                return false;
            }
        }
    }

    @Override
    public double[] getAvg() {
        return overallProfile.getAvg();
    }

    @Override
    public double[][] getCovariance(double[] means) {
        return overallProfile.getCovariance(means);
    }



}
