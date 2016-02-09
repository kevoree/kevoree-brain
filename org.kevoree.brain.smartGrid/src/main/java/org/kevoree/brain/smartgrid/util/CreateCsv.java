package org.kevoree.brain.smartgrid.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by assaad on 09/02/16.
 */
public class CreateCsv {
    public static void main(String[] arg){
        //Load all electric consumptions
        String electricdir = "/Users/assaad/work/github/data/consumption/";
        HashMap<String, ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(electricdir);

        String csvdir= "/Users/assaad/work/github/data/consumption/csv/";

        long starttime=System.nanoTime();
        int x=0;
        try {
            for (String k : smartmeters.keySet()) {
                PrintWriter out = new PrintWriter(new File(csvdir + k + ".csv"));
                ArrayList<ElectricMeasure> em=smartmeters.get(k);

                TreeMap<Long,ElectricMeasure> temptree = new TreeMap<Long, ElectricMeasure>();
                for(ElectricMeasure ee: em){
                  temptree.put(ee.getTime(),ee);
                }

                for (Map.Entry<Long, ElectricMeasure> entry : temptree.entrySet())
                {
                    ElectricMeasure ee= entry.getValue();
                    out.println(ee.getTime()+","+ee.aplus+","+ee.aminus+","+ee.rplus+","+ee.rminus);
                }

                x++;
                out.flush();
                out.close();

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        long endtime=System.nanoTime();
        double restime = (endtime-starttime)/1000000;
        System.out.println("Created "+x+" csv files in "+ restime+" ms!");
    }
}
