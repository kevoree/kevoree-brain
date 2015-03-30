package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.ElectricMeasure;
import org.kevoree.brain.smartgrid.ExcelLoader;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 10/03/15.
 */
public class GenerateAllLoads {
    public static void main(String[] arg) {
        String dir = "/Users/assaad/work/github/data/consumption/";
        HashMap<String, ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        HashMap<String,Integer> dict=new HashMap<String, Integer>();
        int user=0;
        try {
            PrintWriter out = new PrintWriter(new File("ds5.csv"));

            int max=0;
            String best="";
            for(String k: smartmeters.keySet()){
                if(smartmeters.get(k).size()>max){
                    max=smartmeters.get(k).size();
                    best=k;
                }
            }

            ArrayList<ElectricMeasure> em= smartmeters.get(best);
            Long time=em.get(0).getTime();

            for(int i=0; i<3506304;i++){
                Long t= time+i*15*60000;
                double val=em.get(i%em.size()).aplus;
                out.println(t+","+val);
            }
            out.close();



        }
        catch (Exception ex){

            ex.printStackTrace();
        }
        System.out.println("done!");

    }
}
