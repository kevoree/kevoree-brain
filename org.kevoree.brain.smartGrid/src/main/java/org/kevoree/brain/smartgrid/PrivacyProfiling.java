package org.kevoree.brain.smartgrid;

import sun.java2d.cmm.Profile;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by assaad on 19/02/15.
 */
public class PrivacyProfiling {
public static void main(String[] arg){
    String dir="/Users/assaad/work/github/data/consumption/";
    HashMap<String,ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
    int numOfUser=smartmeters.size();
    System.out.println("Loaded measures for "+numOfUser+" users");

    ArrayList<Profiler> profilers = new ArrayList<Profiler>();

    HashMap<String,Profiler> dictionary=new HashMap<String, Profiler>();
    int user=0;
    for(String k: smartmeters.keySet()){
        //ElectricConsumptionProfiler temp=new ElectricConsumptionProfiler();
        MinMaxProfiler temp=new MinMaxProfiler();
      //  GaussianProfiler temp=new GaussianProfiler();
        dictionary.put(k,temp);
        temp.setUserId(k);
        temp.feed(smartmeters.get(k), Math.min(smartmeters.get(k).size(), 3));
        profilers.add(temp);
        user++;

       /* int check=smartmeters.get(k).size();
        for(ElectricMeasure  et: smartmeters.get(k)){
            if(temp.getProba(et)==1){
                check--;
            }
        }
        if(check!=0){
            System.out.println("ERROR!!! at "+user);
        }*/

        System.out.println("Trained user "+k);
    }
    System.out.println("Trained completed");

    try {
        PrintWriter out = new PrintWriter(new File("stat.txt"));
        out.print("id ");
        for(int i=0;i<96;i++){
            out.print("apmin" + i + " ");
        }
        for(int i=0;i<96;i++){
            out.print("apmax"+i+" ");
        }
        for(int i=0;i<96;i++){
            out.print("ammin"+i+" ");
        }
        for(int i=0;i<96;i++){
            out.print("ammax"+i+" ");
        }
        for(int i=0;i<96;i++){
            out.print("rpmin"+i+" ");
        }
        for(int i=0;i<96;i++){
            out.print("rpmax"+i+" ");
        }
        for(int i=0;i<96;i++){
            out.print("rmmin"+i+" ");
        }
        for(int i=0;i<96;i++){
            out.print("rmmax"+i+" ");
        }
        out.println();
        for (Profiler p : profilers) {
            out.println(p.getVector());
        }
    }
    catch (Exception ex){

    }


    try {
        PrintWriter out = new PrintWriter(new File("similarities.txt"));
        out.println("id id sim");
        for(int i=0;i<profilers.size()-1;i++){
            for(int j=i+1;j<profilers.size();j++){
                out.println(profilers.get(i).getUserId() + " " + profilers.get(j).getUserId() + " " + profilers.get(i).similarities(profilers.get(j)));
            }
        }
        out.close();

    }
    catch (Exception ex){
ex.printStackTrace();
    }

    int ccc=0;
    System.out.println("Starting self test");
    for(Profiler p: profilers){
        double test=((MinMaxProfiler)p).getMatchPercent(((MinMaxProfiler)p));
        if(test<0.9){
            System.out.println("Failed at "+ccc);
        }
        ccc++;
    }
    System.out.println("Done with self test");


    dir="/Users/assaad/work/github/data/validation/";

           // dir="/Users/assaad/work/github/data/consumption/";
    HashMap<String,ArrayList<ElectricMeasure>> toguess = ExcelLoader.load(dir);
    System.out.println("Loaded measures for "+numOfUser+" users");




    for(int maxPoss=1;maxPoss<100;maxPoss++) {

        int total=0;
        int guess=0;
        int noguess=0;

        for (String k : toguess.keySet()) {

            double[] scores = new double[profilers.size()];

           /* MinMaxProfiler temp=  new MinMaxProfiler();
            temp.feed(toguess.get(k),5);

            for(int i=0;i<profilers.size();i++){
                scores[i]=((MinMaxProfiler)profilers.get(i)).getMatchPercent(temp);
            }*/

           double[] tempScore=new double[profilers.size()];
            for(ElectricMeasure em: toguess.get(k)){
                double totaltem=0;
                for(int i=0;i<profilers.size();i++){
                    tempScore[i]=profilers.get(i).getProba(em);
                    totaltem+=tempScore[i];
                }
                if(totaltem!=0) {
                    for (int i = 0; i < profilers.size(); i++) {
                        scores[i] += tempScore[i] / totaltem;
                    }
                }
            }






            ArrayList<String> ss = new ArrayList<String>();

            for (int i = 0; i <maxPoss; i++) {
                double max = -1e20;
                int win = 0;
                for (int j = 0; j < scores.length; j++) {
                    if (scores[j] > max) {
                        max = scores[j];
                        win = j;
                    }
                }
                ss.add(profilers.get(win).getUserId());
                scores[win] = -1e20;
            }


            if (ss.contains(k)) {
               // System.out.println("Guessed " + k + " Exactly!");
                guess++;
                total++;
            } else {
                //System.out.println("Wrong guess, Right value is " + k);
                noguess++;
                total++;
            }
        }


        double acc = 100.0 * guess / total;
        System.out.print("Possibilities: "+maxPoss+" Right guesses: " + guess + " wrong guesses: " + noguess + " out of " + total);
        System.out.println(" Accuracy " + acc + " %");
    }

}
}
