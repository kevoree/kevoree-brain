package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.util.TemperatureLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeMap;

/**
 * Created by assaad on 03/04/15.
 */
public class TemperatureService {
    public static void main(String[] args){
        //String dir="/Users/assaad/work/github/data/Temperature/OLBA.txt";
        String dir="/Users/assaad/work/github/data/Temperature/ELLX.txt";
       // String dir="/Users/assaad/work/github/data/Temperature/LFMN.txt";
       // String dir="/Users/assaad/work/github/data/Temperature/BIRK.txt";
        TreeMap<Long,Double> temperature = TemperatureLoader.load(dir);

        double totalTemp=0;
        double totalTempSq=0;
        double min=100;
        double max=-100;
        int count=0;

        int HoursInYear=365*24;

        int mm=-10;
        int mmax=40;
        int interval=10;
        int[] counters = new int[(mmax-mm)/interval+1];

        for(Long l: temperature.keySet()){

            Double var=temperature.get(l);
            totalTemp+=var;
            totalTempSq+=var*var;
            count++;
            if(var<min){
                min=var;
            }
            if(var>max){
                max=var;
            }
            int i= ((int)(var-mm))/interval;
            counters[i]++;
        }
        double avg= totalTemp/count;
        double variance = totalTempSq/count-avg*avg;

       // System.out.println("total: "+count/2+" h or "+count/48+" days");
        System.out.println("min: "+String.format( "%.2f",min)+", max: "+String.format( "%.2f",max)+" , avg: "+String.format( "%.2f",avg) + " , std: "+String.format("%.2f", Math.sqrt(variance)) );

        int i=0;
        for(int x=mm;x<mmax;x+=interval){
            System.out.println("["+x+" , "+(x+interval)+"[: "+counters[i]*HoursInYear/count+" h or "+String.format( "%.2f",(((double)counters[i])*HoursInYear/count)/24)+" days, "+String.format( "%.2f",(((double)counters[i])*100/count))+" %");
            i++;
        }


      /*  i=0;
        for(int x=mm;x<mmax;x+=interval){
            System.out.println("["+x+" , "+(x+interval)+"[: "+counters[i]*HoursInYear/count+" , "+String.format( "%.2f",(((double)counters[i])*HoursInYear/count)/24)+" , "+String.format( "%.2f",(((double)counters[i])*100/count)));
            i++;
        }*/







    }
}
