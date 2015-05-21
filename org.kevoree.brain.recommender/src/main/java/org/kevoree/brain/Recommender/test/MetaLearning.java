package org.kevoree.brain.Recommender.test;

import org.kevoree.brain.Recommender.RatingVector;
import org.kevoree.brain.Recommender.Recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

/**
 * Created by assaad on 21/05/15.
 */
public class MetaLearning {
    public static void main(String[] args){
        String dir="./netflix/";

        String csvfile;
        String line;
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();

        //alpha,lambda,iterations,numFeatures, loopiter
        recommender.setParameters(0.005,0.05,5,15,100000,1);
        long starttime;
        long endtime;
        double result;



       // int total=18983578;
        long total=90482505l;

        csvfile="train.csv";
        starttime= System.nanoTime();
        long counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.addRating(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2]), 0, false);
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
        System.out.println("train set of "+recommender.getRatingCounter()+" ratings loaded in "+result+" s");
        recommender.displayStats();



        csvfile="test.csv";
       // total=2079550;
        total=9998004l;
        starttime= System.nanoTime();
        counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.testVector.add(new RatingVector(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2])));
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
        System.out.println("test set of "+recommender.testVector.size()+" ratings loaded in "+result+" s");
        recommender.playRound(10000);


    }
}
