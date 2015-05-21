package org.kevoree.brain.Recommender.test;

import org.kevoree.brain.Recommender.Recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

/**
 * Created by assaad on 21/05/15.
 */
public class ConvertNetflixDataset {

    public static  String dir="./Movielens/netflix/training/";
    public static void main(String[] args){

        String csvfile;
        String line;
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();

        //alpha,lambda,iterations,numFeatures, loopiter
        recommender.setParameters(0.005,0.001,5,50,100000,1);
        long starttime;
        long endtime;
        double result;



        int total;

        csvfile="ratings.csv";
        starttime= System.nanoTime();
        int counter=0;
        try {
            String s;

            File directory = new File(dir);
            File[] directoryListing = directory.listFiles();
            total=directoryListing.length;
            System.out.println("Found "+total+" files!");

            //  System.out.println("Found " + directoryListing.length + " files");
            if (directoryListing != null) {
                for (File file : directoryListing) {

                    s= file.getName();


                    if (file.getName().equals(".DS_Store")) {
                        continue;
                    }

                    BufferedReader br = new BufferedReader(new FileReader(dir + s));
                    int pid= Integer.parseInt(br.readLine().split(":")[0]);
                    while ((line = br.readLine()) != null) {
                        String[] vals = line.split(cvsSplitBy);
                        recommender.addRating(Integer.parseInt(vals[0]), pid, Double.parseDouble(vals[1]), 0, false);

                    }
                    counter++;
                    if(counter%(total/100)==0) {
                        System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / total) + "%");
                    }

                }
            }
        }
            catch (Exception ex){
            ex.printStackTrace();
        }


        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        recommender.displayStats();
        System.out.println("Model created in "+result+" s");
        System.out.println("Saving ratings...");
        recommender.exportRating();
        System.out.println("Splitting ratings...");
        recommender.splitRating(0.1);

    }

}
