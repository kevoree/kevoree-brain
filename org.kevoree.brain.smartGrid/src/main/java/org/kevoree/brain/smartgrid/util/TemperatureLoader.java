package org.kevoree.brain.smartgrid.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by assaad on 03/04/15.
 */
public class TemperatureLoader {
    public static TreeMap<Long,Double> load(String filename){
        long starttime = System.nanoTime();
        TreeMap<Long,Double> result=new TreeMap<Long, Double>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        int duplicate=0;
        int linenum=0;

        try {


            br = new BufferedReader(new FileReader(filename));
            int count=0;
            while ((line = br.readLine()) != null) {
                linenum++;
                if (count < 6) {
                    count++;
                    continue;
                }
                String[] data = line.split(cvsSplitBy);


                //ELLX,2014-01-01 00:20,4.00
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                java.util.Date parsedDate = dateFormat.parse(data[1]);
                Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
                if(result.keySet().contains(ts.getTime())){
                    duplicate++;
                //    System.out.println("Duplicate ["+linenum+"]"+data[1]);
                }
                result.put(ts.getTime(),Double.parseDouble(data[2]));
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        long endtime=System.nanoTime();
        double restime = (endtime-starttime)/1000000;
     //   System.out.println("Loaded " + result.size() + " values, duplicated: "+duplicate);
        System.out.println("Loaded " + result.size() + " temperature records in "+restime+" ms!");
        return result;
    }
}
