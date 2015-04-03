package org.kevoree.brain.smartgrid.tests;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by assaad on 03/04/15.
 */
public class ReformatTemperature {
    public static void main(String[] arg){

        String dir= "/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/";
        String csvFile = dir+"temperatureFindel.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            PrintStream out = new PrintStream(new FileOutputStream("/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/tempAll.csv"));

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                //LUX0000001 , 01.01.2014 02:00 , 3.7
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                dateFormat.setTimeZone(TimeZone.getDefault());
                java.util.Date parsedDate =  dateFormat.parse(data[1]);

                Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());

                out.println(ts.getTime()+" , "+ data[2]);


            }
            out.close();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
