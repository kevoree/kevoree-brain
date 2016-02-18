package org.kevoree.brain.smartgrid.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaad on 16/02/16.
 */
public class GenerateSynth {
    public static void main(String[] arg){
        long t=1406844000000l;
        int generate=20000;
        String dir= "/Users/assaad/work/github/data/test/synthetic.csv";
        String dir2= "/Users/assaad/work/github/data/test/check.csv";

        Random random=new Random();
        try{
            PrintWriter out = new PrintWriter(new File(dir));
            PrintWriter out2 = new PrintWriter(new File(dir2));
            for(int i=0;i<generate;i++){
                ElectricMeasure em = new ElectricMeasure();
                em.setTime(t);
                double d=em.getDoubleTime();

                double p;

                if(d<12){
                    p=-100*d+1400;
                }
                else {
                    p=100*(d-12)+200;
                }

                p+=(random.nextDouble()*20-10);
                out2.println(d+","+p);
                out.println(t+","+p+",0,0,0");
                t+=900000;
            }
            out.close();
            out2.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


    }
}
