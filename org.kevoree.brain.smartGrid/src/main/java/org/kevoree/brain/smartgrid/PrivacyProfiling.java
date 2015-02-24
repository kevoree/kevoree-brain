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
       // GaussianProfiler temp=new GaussianProfiler();
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




    dir="/Users/assaad/work/github/data/validation/";

           // dir="/Users/assaad/work/github/data/consumption/";
    HashMap<String,ArrayList<ElectricMeasure>> toguess = ExcelLoader.load(dir);
 //   HashMap<String,ArrayList<ElectricMeasure>> toguess = smartmeters;
    System.out.println("Loaded measures for "+numOfUser+" users");


            int total=0;
            int rank=0;
    int[] guesses = new int[40];

            for (String k : toguess.keySet()) {

                double[] scores = new double[numOfUser];
                for(int i=0;i<numOfUser;i++){
                    scores[i]=0;
                }

                double[] tempScore=new double[profilers.size()];
                for(ElectricMeasure em: toguess.get(k)){
                   // double totaltem=0;
                    for(int i=0;i<profilers.size();i++){
                        tempScore[i]=profilers.get(i).getProba(em);
                        // totaltem+=tempScore[i];
                        scores[i] +=tempScore[i];

                    }
                 /*   if(totaltem!=0) {
                        for (int i = 0; i < profilers.size(); i++) {
                            scores[i] += tempScore[i] / totaltem;
                        }
                    }*/
                }


                ArrayList<Solution> ss = new ArrayList<Solution>();

                for(int i=0;i<numOfUser;i++){
                    Solution sol = new Solution();
                    sol.id=profilers.get(i).getUserId();
                    sol.score=scores[i];
                    ss.add(sol);
                }

                for(int l=0;l<40;l++){
                    if(Solution.contain(ss,k,l)){
                        guesses[l]++;
                    }
                }

                rank+=Solution.rank(ss,k);
                total++;
            }


            double acc = ((double) rank) / total;
    System.out.println("Can guess in average by sending a list of "+acc+" users");
    for(int l=1;l<35;l++) {
        System.out.println("Among "+l+" users, accuracy: "+(100*((double) guesses[l]) / total)+" %");
    }

}
}
