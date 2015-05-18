package org.kevoree.brain.test;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TimeZone;

/**
 * Created by assaad on 23/04/15.
 */
public class TestD {
    public static void main(String[] arg){
        int population=1000;
        int plays=100000;

        ArrayList<Integer> mates = new ArrayList<Integer>();

        for(int i=0;i<population;i++){
            mates.add(i+1);
        }

        int[] max=new int[population];
        double[] avg=new double[population];

        int bestmax=0;
        int mmax=0;
        double bestAvg=0;
        int mAvg=0;

        try {

            PrintStream out = new PrintStream(new FileOutputStream("/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.smartGrid/resource/date.csv"));
            for (int decision = 0; decision < population; decision++) {
                for (int play = 0; play < plays; play++) {
                    Collections.shuffle(mates);
                    //  System.out.println(mates.get(0));
                    if (decision == 0) {
                        avg[decision] += mates.get(0);
                        if (mates.get(0) == population) {
                            max[decision]++;
                        }
                    } else if (decision == population - 1) {
                        avg[decision] += mates.get(population - 1);
                        if (mates.get(population - 1) == population) {
                            max[decision]++;
                        }
                    } else {
                        int tempMax = 0;
                        for (int i = 0; i < decision; i++) {
                            if (mates.get(i) > tempMax) {
                                tempMax = mates.get(i);
                            }
                        }
                        int selection = 0;
                        for (int i = decision; i < population; i++) {
                            selection = mates.get(i);
                            if (selection > tempMax) {
                                break;
                            }
                        }
                        avg[decision] += selection;
                        if (selection == population) {
                            max[decision]++;
                        }
                    }
                }
                avg[decision] = avg[decision] / plays;
                if(avg[decision]>bestAvg){
                    bestAvg=avg[decision];
                    mAvg=decision;
                }
                if(max[decision]>bestmax){
                    bestmax=max[decision];
                    mmax=decision;
                }
                System.out.println(decision + " , " + avg[decision] + " , " + max[decision] + " , " + mAvg + " bst avg " + bestAvg + " , " + mmax + " best max " + bestmax);
                out.println(decision + "," + avg[decision] + "," + max[decision]);
                out.flush();
            }
            out.println(mAvg+" bst avg "+bestAvg+" , "+ mmax+" best max "+ bestmax);
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }






    }
}
