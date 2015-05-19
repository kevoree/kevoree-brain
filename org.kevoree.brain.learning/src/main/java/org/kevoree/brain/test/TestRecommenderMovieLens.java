package org.kevoree.brain.test;

import org.kevoree.brain.learning.livelearning.Recommender.LearningVector;
import org.kevoree.brain.learning.livelearning.Recommender.Recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by assaad on 18/05/15.
 */
public class TestRecommenderMovieLens {
    public static void main(String args[]){

        //alpha,lambda,iterations,numFeatures
        LearningVector.setParameters(0.005,0.001,10,50);

        String dir="/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.learning/src/main/resources/Movielens/";

        String csvfile="movies.csv";
        String line = "";
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();
        long starttime;
        long endtime;
        double result;



        starttime= System.nanoTime();
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {

                String[] vals = line.split(cvsSplitBy);
                recommender.addProduct(vals[0],vals[1]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Loaded: "+recommender.getProducts().size()+" movies in "+result+" s");



        int total=21063128;
        //int total=1000209;

        csvfile="ratings.csv";
        starttime= System.nanoTime();
        int counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.addRating(vals[0], vals[1], Double.parseDouble(vals[2]), Long.parseLong(vals[3]), true);
                counter++;
                if(counter%(total/20)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / total) + "%");
                }
            }
            //recommender.loopRatings(100);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        recommender.displayStats();
        System.out.println("Model created in "+result+" s");
        recommender.getAverageError();





    }
}
