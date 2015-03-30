package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.util.ContextSolver;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.ExcelLoader;
import org.kevoree.brain.smartgrid.Profilers.Profiler;
import org.kevoree.brain.smartgrid.Profilers.GaussianProfiler;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
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

        try {
            PrintStream out = new PrintStream(new FileOutputStream("/Users/assaad/work/github/data/"+index+" result.csv"));
            PrintStream out2 = new PrintStream(new FileOutputStream("/Users/assaad/work/github/data/"+index+" result weekends.csv"));
            ArrayList<ElectricMeasure> best = smartmeters.get(index);

            for(ElectricMeasure em: best){
                if(ContextSolver.getWeekday(em.getTime())){
                    int t=em.getIntTime(96);
                    tot[t]+=em.aplus;
                    sum[t]++;
                    out.println(t+ "," + ((double)em.getIntTime(96))/4 +","  + em.aplus);
                }
                else{
                    out2.println(em.getIntTime(96)+ ","+ ((double)em.getIntTime(96))/4 +","  + em.aplus);
                }
            }

            PrintStream out3 = new PrintStream(new FileOutputStream("/Users/assaad/work/github/data/"+index+" avg.csv"));
            for(int i=0; i<96; i++){
                out3.println(i+" , "+tot[i]/sum[i]);
            }

            out3.close();
            out2.close();
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

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
