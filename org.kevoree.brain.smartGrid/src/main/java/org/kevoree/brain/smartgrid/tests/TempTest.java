package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.ExcelLoader;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 30/03/15.
 */
public class TempTest {
    public static void main(String[] args) {
      //  String dir="/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/";
        // System.out.println("Solved " + ContextSolver.getTemperature(1406844000000l));



        //Calc avg
        String dir="/Users/assaad/work/github/data/consumption/";
        HashMap<String,ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        int numOfUser=smartmeters.size();
        System.out.println("Loaded measures for "+numOfUser+" users");

        HashMap<String, Double> avgs= new HashMap<String, Double>();


        for(String k: smartmeters.keySet()){
            double avg=0;
            int total=0;
            for(ElectricMeasure em: smartmeters.get(k)){
                avg+=em.aplus;
                total++;
            }
            avgs.put(k,avg/total);
        }

        try {
            PrintStream out = new PrintStream(new FileOutputStream("/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/avgs.csv"));

            for( String s: avgs.keySet()){
                out.println(s+","+avgs.get(s));
            }

            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }



    }

}
