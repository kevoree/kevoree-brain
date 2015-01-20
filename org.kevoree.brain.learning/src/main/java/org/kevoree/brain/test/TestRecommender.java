package org.kevoree.brain.test;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLUInt8;
import org.kevoree.brain.learning.livelearning.Recommender.Rating;
import org.kevoree.brain.learning.livelearning.Recommender.Recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by assaad on 19/01/15.
 */
public class TestRecommender {
    public static void main(String[] args) throws IOException {
        String basedir = "/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.learning/src/main/java/org/kevoree/brain/test/data/";

        MatFileReader mfr = new MatFileReader(basedir+"ex8_movies.mat");
        // get a reference to out matlab 'squares' double matrix
        MLDouble mlreader= (MLDouble) mfr.getMLArray("Y");
        double[][] movieRatings=null;
        if (mlreader != null)
        {
            // now get the double values
            movieRatings= mlreader.getArray();

        }
        System.out.println("movie ratings loaded: "+movieRatings.length+" x "+ movieRatings[0].length);

        byte[][] Rexists=null;
        MLUInt8 binaries = (MLUInt8) mfr.getMLArray("R");
        if (binaries != null)
        {
            // now get the double values
            Rexists= binaries.getArray();
        }
        System.out.println("R matrix "+Rexists.length+" x "+Rexists[0].length);


        int count=0;




        FileReader filenames = new FileReader(basedir+"movie_ids.txt");

        BufferedReader br = new BufferedReader(filenames);
        String line;
        count=1;

        Recommender rec = new Recommender();


        rec.setAlpha(0.055);
        rec.setLambda(0.012);
        rec.setIterations(100);
        rec.setNumOfFeatures(100);

        while ((line = br.readLine()) != null) {
            if(count<10){
                line = line.substring(2);
            }
            else if(count<100){
                line = line.substring(3);
            }
            else if(count<1000){
                line = line.substring(4);
            }
            else if(count<10000){
                line = line.substring(5);
            }
            rec.addProduct(line,count-1);
            count++;
        }
        br.close();

        for(int i=0; i<movieRatings[0].length;i++){
            rec.addUser("user"+(i+1),i);
        }



        count=0;
        for(int i=0;i<movieRatings.length;i++){
            for(int j=0;j<movieRatings[0].length;j++){
                if(Rexists[i][j]!=0){
                        rec.addRating(j, i, movieRatings[i][j]);
                        count++;
                }
            }
        }
        System.out.println("number of ratings: " + count);
        System.out.println("Average rating for movie 1 (Toy Story): "+rec.calculateAverageRatingProduct(0));


        double error=0;
        count=0;
        double temp=0;

        rec.setIterations(1);

        for(int i=0;i<movieRatings.length;i++) {
            for (int j = 0; j < movieRatings[0].length; j++) {
                if (Rexists[i][j] != 0) {
                    temp=Math.abs(rec.predict(j,i)-movieRatings[i][j]);
                    error=error+temp;
                    count++;
                    //System.out.println("Predicted "+rec.predict(j,i)+" Given: "+ movieRatings[i][j]);
                }
            }
        }
        error=error/count;
        System.out.println("Average error "+error);
        rec.setLambda(0.0001);
        rec.pass(500);
        System.out.println("Average error after iterating "+rec.getAverageError());
        rec.save();

    }
}
