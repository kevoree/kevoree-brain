package org.kevoree.brain.smartgrid;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by assaad on 20/02/15.
 */
public class PrivacyProfilingClassifier {
    public static void main(String[] arg){
        String dir="/Users/assaad/work/github/data/consumption/";
        HashMap<String,ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        int numOfUser=smartmeters.size();
        System.out.println("Loaded measures for "+numOfUser+" users");
        HashMap<Integer,String> dictionary=new HashMap<Integer,String>();
        BayesianClassifier bc = new BayesianClassifier();
        bc.initialize(4,numOfUser);

        int user=0;
        for(String k: smartmeters.keySet()) {
            dictionary.put(user, k);
            bc.trainArray(smartmeters.get(k), user);

            user++;
        }
        System.out.println("Trained completed");






        //dir="/Users/assaad/work/github/data/validation/";
        //HashMap<String,ArrayList<ElectricMeasure>> toguess = ExcelLoader.load(dir);
        HashMap<String,ArrayList<ElectricMeasure>> toguess =smartmeters;
        System.out.println("Loaded measures for "+numOfUser+" users");




     //   for(int maxPoss=5;maxPoss<100;maxPoss++) {

        int maxPoss=1;
            int total=0;
            int guess=0;
            int noguess=0;

            for (String k : toguess.keySet()) {

                double[] scores = new double[numOfUser];
                for(int i=0;i<numOfUser;i++){
                    scores[i]=0;
                }

                for(ElectricMeasure em: toguess.get(k)){
                    double[] tempScore =bc.predict(em);
                    for(int i=0;i<numOfUser;i++){
                        scores[i]=scores[i]+tempScore[i];
                    }
                }


                ArrayList<Solution> ss = new ArrayList<Solution>();

                for(int i=0;i<numOfUser;i++){
                    Solution sol = new Solution();
                    sol.id=dictionary.get(i);
                    sol.score=scores[i];
                    ss.add(sol);
                }


                if (Solution.contain(ss,k,maxPoss)) {
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
        //}

    }
}
