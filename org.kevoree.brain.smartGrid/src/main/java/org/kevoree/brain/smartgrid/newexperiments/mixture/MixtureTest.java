package org.kevoree.brain.smartgrid.newexperiments.mixture;

import java.util.Random;

/**
 * Created by assaad on 12/02/16.
 */
public class MixtureTest {
    public static void main(String[] arg){

        int size=10000;
        int feat=4;

        long starttime,endtime;
        double res;


        double[] err=new double[feat];
        for(int i=0;i<feat;i++){
            err[i]=0.1;
        }
        Configration configration=new Configration();
        configration.capacity=10;
        configration.compression=20;
        configration.kmeanIterations=20;
        configration.err=err;
        configration.maxLevels=2;

        MixtureModel mm=new MixtureModel(configration);

        Random rand=new Random();

        starttime=System.nanoTime();
        double[] test= new double[4];
        for(int j=0;j<size;j++){
            for(int i=0;i<feat;i++){
                test[i]=rand.nextDouble()*4;
            }
            mm.feed(test);
        }
        endtime=System.nanoTime();
        res=(endtime-starttime)/1000000000;
        System.out.println("Done in "+res+" s!");

        System.out.println("Total components "+mm.totalComponents());
        int x=0;


    }
}
