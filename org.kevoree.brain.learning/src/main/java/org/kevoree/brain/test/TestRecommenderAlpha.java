package org.kevoree.brain.test;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLUInt8;
import org.kevoree.brain.learning.livelearning.Recommender.Recommender;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by assaad on 19/01/15.
 */
public class TestRecommenderAlpha {
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
        }
        br.close();

        for(int i=0; i<movieRatings[0].length;i++){
            rec.addUser("user"+(i+1),i);
        }


        double min=10;
        double alphamin=0;
        double lambdamin=0;

        double amin=0.055;
        double amax=0.075;
        double asteps=20;

        double lmin=0.10;
        double lmax=0.20;
        double lsteps=5;


        double prev;


        double lambda=0.4;

        for(double alpha=amin;alpha<amax;alpha+=(amax-amin)/asteps){
            prev=-1;


            for(lambda=lmin;lambda<lmax;lambda+=(lmax-lmin)/lsteps){
                rec.reset();
                rec.setAlpha(alpha);
                rec.setLambda(lambda);
                for(int i=0;i<movieRatings.length;i++){
                    for(int j=0;j<movieRatings[0].length;j++){
                        if(Rexists[i][j]!=0){
                            rec.addRating(j, i, movieRatings[i][j]);
                        }
                    }
                }
                double error=0;
                count=0;
                double temp=0;
                for(int i=0;i<movieRatings.length;i++) {
                    for (int j = 0; j < movieRatings[0].length; j++) {
                        if (Rexists[i][j] != 0) {
                            temp=Math.abs(rec.predict(j,i)-movieRatings[i][j]);
                            error=error+temp;
                            count++;
                        }
                    }
                }
                error=error/count;
                if(error<min){
                    min=error;
                    alphamin=alpha;
                    lambdamin=lambda;
                }

                System.out.println("Alpha: "+alpha+" , lambda: "+lambda+" Average error: "+error);
                if(prev!=-1){
                    if(prev<error){
                        break;
                    }
                }
                prev=error;
            }
        }
        System.out.println("Alpha: "+alphamin+" , lambda: "+lambdamin+" Average error: "+min);





    }
}
