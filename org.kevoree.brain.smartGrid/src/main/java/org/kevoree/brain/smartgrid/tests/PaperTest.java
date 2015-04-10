package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.ExcelLoader;
import org.kevoree.brain.smartgrid.Profilers.Profiler;
import org.kevoree.brain.smartgrid.Profilers.GaussianProfiler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 13/03/15.
 */
public class PaperTest {
    public static void main(String[] args){
        String dir="/Users/assaad/work/github/data/consumption/";
        HashMap<String,ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        int numOfUser=smartmeters.size();
        System.out.println("Loaded measures for "+numOfUser+" users");

        int total=0;

        int max=0;
        String index="ZIV0036303916";
        for(String k: smartmeters.keySet()){
            total+= smartmeters.get(k).size();


        }

        double[] tot = new double[96];
        int[] sum = new int[96];

        System.out.println("Totalling "+total+" power measures");

        ArrayList<Profiler> profilers = new ArrayList<Profiler>();

        HashMap<String,Profiler> dictionary=new HashMap<String, Profiler>();

        long starttime= System.nanoTime();
        for(String k: smartmeters.keySet()){
            GaussianProfiler temp=new GaussianProfiler();
            dictionary.put(k,temp);
            temp.setUserId(k);
            temp.feed(smartmeters.get(k), Math.min(smartmeters.get(k).size(), 3));
            profilers.add(temp);
        }
        long endtime= System.nanoTime();
        double result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Trained completed in "+result+" s");
        System.out.println("Average time taken "+(result*1000)/total+" ms per measurement");
    }
}
