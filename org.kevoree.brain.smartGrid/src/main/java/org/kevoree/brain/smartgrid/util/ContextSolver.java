package org.kevoree.brain.smartgrid.util;




import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;


/**
 * Created by assaad on 30/03/15.
 */
public class ContextSolver {

    private static String dir= "/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/";

    private static TreeMap<Long, Double> temp = null;

    private static HashMap<String, Double> consumerType=null;

    public static int getWeekdayClassification(Long timestamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        DayOfWeek dow= DayOfWeek.of(cal.get(Calendar.DAY_OF_WEEK));
        if(dow==DayOfWeek.SATURDAY||dow==DayOfWeek.SUNDAY){
            return 1;
        }
        else {
            return 0;
        }
    }

    public static int getConsumerType(String Id){
        if (consumerType==null){
            consumerType=new HashMap<String, Double>();

            String csvFile = dir+"avgs.csv";
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            try {

                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] data = line.split(cvsSplitBy);
                    consumerType.put(data[0],Double.parseDouble(data[1]));

                }

            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        double av= consumerType.get(Id);
        if(av<101){
            return 0;
        }
        else{
            return 1;
        }


    }


    public static int getTemperatureClassification(Long timestamp){
        double t=getTemperature(timestamp);
        if(t<19){
            return 0;
        }
        else
            return 1;
    }


    public static double getTemperature(Long timestamp){
        if(temp==null){
            temp=new TreeMap<Long, Double>();

            String csvFile = dir+"Temperature.csv";
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            try {

                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] data = line.split(cvsSplitBy);

                    //2014-07-01 02:50
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    java.util.Date parsedDate =  dateFormat.parse(data[1]);

                    Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
                    temp.put(ts.getTime(),Double.parseDouble(data[2]));
                }

            }
            catch (Exception ex){
                ex.printStackTrace();
            }

        }

        return temp.get(temp.lowerKey(timestamp));
    }

}
