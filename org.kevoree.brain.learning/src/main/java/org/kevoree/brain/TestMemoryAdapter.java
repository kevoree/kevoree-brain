package org.kevoree.brain;

import org.kevoree.brain.learning.livelearning.LinearRegressionLive;
import org.kevoree.brain.util.adapters.MemoryAdapter;

import java.util.Random;

/**
 * Created by assaa_000 on 8/20/2014.
 */
public class TestMemoryAdapter {

    private static double[] initialweights={0.1,0.401,0.2,-0.61,0.91};
    private static double[] prev;
    private static int counter=0;


    public static void main(String[] args) {
        int size=5;
        int max = 10000;

        MemoryAdapter mA= new MemoryAdapter();
        LinearRegressionLive lrl = new LinearRegressionLive();
        lrl.setSize(size);
        mA.setSize(size);
        mA.setLiveLearning(lrl);

        for(int i=0; i<max;i++){
            double[] res= generateNew(size);
            System.out.println(res[0]);
            mA.feed(res);
        }

        System.out.println("Original weights");
        for(int i=0;i<size;i++){
            System.out.println(initialweights[i]);
        }

        System.out.println("Learned weights");
        lrl.print();
        System.out.println("last error %"+lrl.getLasterror());


    }

    private static Random random=new Random();

    public static double[] generateNew(int size){
            if(counter==0) {
                prev = new double[size];
            }
        if(counter<size){
                prev[size-1-counter]=random.nextDouble()*4 ;
                double[] temp=new double[1];
                temp[0]=1;
                counter++;
                return temp;
            }
        double[] temp=new double[1];
        temp[0]=0;
        for(int i=0;i<size;i++){
            temp[0]+=initialweights[i]*prev[i];
        }
        for(int i=0;i<size-1;i++){
            prev[i]=prev[i+1];
        }
        prev[size-1]=temp[0];
        return temp;


    }
}
