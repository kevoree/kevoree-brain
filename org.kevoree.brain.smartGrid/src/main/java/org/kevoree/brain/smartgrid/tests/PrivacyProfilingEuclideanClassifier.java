package org.kevoree.brain.smartgrid.tests;

import org.kevoree.brain.smartgrid.classifier.BayesianClassifier;
import org.kevoree.brain.smartgrid.classifier.EuclideanDistanceClassifier;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.ExcelLoader;
import org.kevoree.brain.smartgrid.util.SolutionComparator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 20/02/15.
 */
public class PrivacyProfilingEuclideanClassifier {
    public static void main(String[] arg){
        String dir="/Users/assaad/work/github/data/consumption/";
        HashMap<String,ArrayList<ElectricMeasure>> smartmeters = ExcelLoader.load(dir);
        int numOfUser=smartmeters.size();
        System.out.println("Loaded measures for "+numOfUser+" users");
        HashMap<Integer,String> dictionary=new HashMap<Integer,String>();
        EuclideanDistanceClassifier ec=new EuclideanDistanceClassifier();
        ec.initialize(numOfUser);

        int user=0;
        for(String k: smartmeters.keySet()) {
            dictionary.put(user, k);
            ec.trainArray(smartmeters.get(k), user);

            user++;
        }
        System.out.println("Trained completed");






        dir="/Users/assaad/work/github/data/validation/";
        HashMap<String,ArrayList<ElectricMeasure>> toguess = ExcelLoader.load(dir);
        //HashMap<String,ArrayList<ElectricMeasure>> toguess =smartmeters;
        System.out.println("Loaded measures for "+numOfUser+" users");




        for(int maxPoss=1;maxPoss<10;maxPoss++) {

       // int maxPoss=1;
            int total=0;
            int guess=0;
            int noguess=0;

            for (String k : toguess.keySet()) {


                ArrayList<SolutionComparator> ss = ec.classifyArray(toguess.get(k),dictionary);

                if (SolutionComparator.contain(ss, k, maxPoss)) {
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
