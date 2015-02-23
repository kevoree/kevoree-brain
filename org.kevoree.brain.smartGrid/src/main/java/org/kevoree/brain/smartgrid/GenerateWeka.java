package org.kevoree.brain.smartgrid;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 20/02/15.
 */
public class GenerateWeka {
    public static void main(String[] arg) {
        String dir = "/Users/assaad/work/github/data/consumption/";
        HashMap<String, ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        HashMap<String,Integer> dict=new HashMap<String, Integer>();
        int user=0;
        try {
            PrintWriter out = new PrintWriter(new File("Summary.csv"));

            StringBuilder sb= new StringBuilder("id,");
            int timestep=24;
            Profiler.timeStep=timestep;
            for(int i=0;i<timestep;i++){
                //sb.append("apmin"+i+",");
              //  sb.append("apmax"+i+",");
                sb.append("apavg"+i+",");
                //sb.append("apvar"+i+",");
                //sb.append("ammin"+i+",");
               // sb.append("ammax"+i+",");
               // sb.append("amavg"+i+",");
                //sb.append("amvar"+i+",");
               // sb.append("rpmin"+i+",");
              //  sb.append("rpmax"+i+",");
               // sb.append("rpavg"+i+",");
               // sb.append("rpvar"+i+",");
                //sb.append("rmmin"+i+",");
              //  sb.append("rmmax"+i+",");
                //sb.append("rmavg"+i+",");
                //sb.append("rmvar"+i+",");
            }
            out.println(sb.toString());

            System.out.println("users: "+smartmeters.keySet().size());

            for(String k: smartmeters.keySet()){
                dict.put(k,user);
                GaussianProfiler gp=new GaussianProfiler();
                gp.setUserId(k);
                gp.feed(smartmeters.get(k),5);
                out.println(gp.getVector());
                user++;
            }
            System.out.println("users: "+user);
            out.flush();
            out.close();
        }
        catch (Exception ex){

            ex.printStackTrace();
        }
        System.out.println("done!");

    }
}
