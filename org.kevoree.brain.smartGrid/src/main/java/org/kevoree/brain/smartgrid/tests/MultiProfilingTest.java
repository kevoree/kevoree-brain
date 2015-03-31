package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.Profilers.MinMaxProfiler;
import org.kevoree.brain.smartgrid.Profilers.PaperProfiler;
import org.kevoree.brain.smartgrid.Profilers.Profiler;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.ExcelLoader;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by assaad on 31/03/15.
 */
public class MultiProfilingTest {
    public static void main(String[] args) {
        String dir = "/Users/assaad/work/github/data/consumption/";
        HashMap<String, ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        int numOfUser = smartmeters.size();
        System.out.println("Loaded measures for " + numOfUser + " users");
        int total=0;
        for(String k: smartmeters.keySet()){
            total+= smartmeters.get(k).size();
        }
        System.out.println("Totalling "+total+" power measures");


        HashMap<String,PaperProfiler> dictionary=new HashMap<String, PaperProfiler>();

        long starttime= System.nanoTime();
        for(String k: smartmeters.keySet()){
            PaperProfiler temp=new PaperProfiler();
            dictionary.put(k,temp);
            temp.setUserId(k);
            temp.feed(smartmeters.get(k), Math.min(smartmeters.get(k).size(), 3));
        }
        long endtime= System.nanoTime();
        double result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Trained completed in "+result+" s");
        System.out.println("Average process time for of 1 measure is: "+result*1000000/total+" ms");


        dir = "/Users/assaad/work/github/data/validation/";
        smartmeters = ExcelLoader.load(dir);
        numOfUser = smartmeters.size();
        System.out.println("Loaded measures for " + numOfUser + " users for validation");
        total=0;
        for(String k: smartmeters.keySet()){
            total+= smartmeters.get(k).size();
        }
        System.out.println("Totalling "+total+" power measures");

        try {
            PrintStream out = new PrintStream(new FileOutputStream("/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/Varresults.csv"));

            int numOfVar=10;

            double[][] tp= new double[numOfVar+1][5];
            double[][] tn= new double[numOfVar+1][5];
            double[][] fp= new double[numOfVar+1][5];
            double[][] fn= new double[numOfVar+1][5];

            double[][] prec= new double[numOfVar+1][5];
            double[][] rec= new double[numOfVar+1][5];
            double[][] acc= new double[numOfVar+1][5];
            double[][] f1= new double[numOfVar+1][5];

            for (String k : smartmeters.keySet()) {
                PaperProfiler profile = dictionary.get(k);
                for (int variance = 0; variance <= numOfVar; variance ++) {
                    int[] counters = new int[5];
                    for (ElectricMeasure em : smartmeters.get(k)) {
                        boolean[] res = profile.getDifferentDecisions(em, variance * 0.5 + 0.1);
                        for (int i = 0; i < 5; i++) {
                            if (res[i]) {
                                tp[variance][i]++;
                            } else {
                                fn[variance][i]++;
                            }
                        }
                    }

                    Random rand = new Random();
                    for (ElectricMeasure em : smartmeters.get(k)) {
                        ElectricMeasure newEm = new ElectricMeasure();
                        newEm.setTime(em.getTime());
                        for (int kk = 0; kk < 1; kk++) {
                            newEm.aplus = (rand.nextDouble()*0.8 +0.7) * profile.apmax[em.getIntTime(96)];
                            boolean[] res = profile.getDifferentDecisions(newEm, variance * 0.5 + 0.1);
                            for (int i = 0; i < 5; i++) {
                                if (res[i]) {
                                    fp[variance][i]++;
                                } else {
                                    tn[variance][i]++;
                                }
                            }
                        }
                    }
                }
            }

            for (int variance = 0; variance <= numOfVar; variance ++) {
                out.print(variance*0.5+0.1+" , ");
                for (int i = 0; i < 5; i++) {
                    prec[variance][i]= (tp[variance][i])/(tp[variance][i]+fp[variance][i]);
                    rec[variance][i]= (tp[variance][i])/(tp[variance][i]+fn[variance][i]);
                    acc[variance][i]=(tp[variance][i]+tn[variance][i])/(tp[variance][i]+tn[variance][i]+fp[variance][i]+fn[variance][i]);
                    f1[variance][i]= 2*prec[variance][i]*rec[variance][i]/(prec[variance][i]+rec[variance][i]);
                    out.print(f1[variance][i]+" , ");
                }
                out.println();
            }
            out.println();
            out.println();
            for (int variance = 0; variance <= numOfVar; variance ++) {
                out.print(variance*0.5+0.1+" , ");
                for (int i = 0; i < 5; i++) {
                   out.print(acc[variance][i]+" , ");
                }
                out.println();
            }

out.close();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("done!");
    }
}
