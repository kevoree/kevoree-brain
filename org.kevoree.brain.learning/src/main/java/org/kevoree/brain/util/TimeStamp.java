package org.kevoree.brain.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by assaad on 06/02/15.
 */
public class TimeStamp {
    public static Long getTimeStamp(int year, int month, int day, int hour, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, min, 0);
        Date date = cal.getTime(); // get back a Date object
        Long timestamp = date.getTime() - date.getTime() % 1000;
        return timestamp;
    }

    public static String getDate(Long timeStamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        return cal.getTime().toString();
    }



    public static long getTimeStamp(String s){
        //2014.11.28 16:31
        SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat(
                "yyyy.MM.dd hh:mm");
        Date lFromDate1 = null;
        try {
            lFromDate1 = datetimeFormatter1.parse(s);
            return lFromDate1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
