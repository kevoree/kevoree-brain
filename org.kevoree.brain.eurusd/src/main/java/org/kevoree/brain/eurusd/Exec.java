package org.kevoree.brain.eurusd;

import org.kevoree.brain.util.TimeStamp;

/**
 * Created by assaad on 06/02/15.
 */
public class Exec {
    public static void main(String[] arf){
        System.out.println(TimeStamp.getDate(1237382220000L));
        System.out.println(TimeStamp.getDate(TimeStamp.getTimeStamp(2000,1,1,1,1)));
    }
}
