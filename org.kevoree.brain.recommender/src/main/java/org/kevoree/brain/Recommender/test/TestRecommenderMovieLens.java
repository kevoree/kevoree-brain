package org.kevoree.brain.Recommender.test;


import org.kevoree.brain.Recommender.Recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;


/**
 * Created by assaad on 18/05/15.
 */
public class TestRecommenderMovieLens {
    public static void main(String args[]){



        String dir="./Movielens/";

        String csvfile="movies.csv";
        String line = "";
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();

        //alpha,lambda,iterations,numFeatures, loopiter
        recommender.setParameters(0.005,0.001,5,50,100000,1);
        long starttime;
        long endtime;
        double result;



        int total=21063128;
       // int total=1000209;

        csvfile="ratings.csv";
        starttime= System.nanoTime();
        int counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.addRating(vals[0], vals[1], Double.parseDouble(vals[2]), Long.parseLong(vals[3]), false);
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

        recommender.splitRating(0.1);

        recommender.getAverageError();





    }
}
