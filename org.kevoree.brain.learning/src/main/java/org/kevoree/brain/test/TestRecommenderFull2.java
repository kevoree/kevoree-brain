package org.kevoree.brain.test;

import org.kevoree.brain.learning.livelearning.Recommender.Product;
import org.kevoree.brain.learning.livelearning.Recommender.Recommender;
import org.kevoree.brain.learning.livelearning.Recommender.User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaad on 21/01/15.
 */
public class TestRecommenderFull2 {
    public static void main(String[] args){
        Recommender generator = new Recommender();

        Random random=new Random();

        int numFeat=20;
        int max=20;
        generator.setNumOfFeatures(numFeat);


        for(int i=0;i<max;i++){
            User test = generator.addUser("user"+i,i);
            for(int j=0; j<numFeat; j++){
                test.weights[j]=random.nextDouble()*Math.sqrt(2);
            }

        }
        for(int i=0;i<max;i++){
            Product test =generator.addProduct("product" + i, i);
            for(int j=0; j<numFeat; j++){
                test.weights[j]=random.nextDouble()*Math.sqrt(2);
            }
        }



        Recommender learner =new Recommender();
        learner.setNumOfFeatures(numFeat);
        for(int i=0;i<max;i++) {
            learner.addUser("user" + i, i);
            learner.addProduct("product" + i, i);
        }

        double alpha=0.046;
        double lamda=0.003;

        try {
            PrintWriter out = new PrintWriter(new FileWriter("learning.txt"));
            PrintWriter out2 = new PrintWriter(new FileWriter("crossval.txt"));
            out.print(" ");
            out2.print(" ");
            for(lamda=0.001; lamda<0.05; lamda+=0.001){
                out.print(lamda+" ");
                out2.print(lamda+" ");
            }
            out.println();
            out2.println();

            for(alpha=0.001; alpha<0.05; alpha+=0.001){
                out.print(alpha+" ");
                out2.print(alpha+" ");
                for(lamda=0.001; lamda<0.05; lamda+=0.001){
                    learner.setAlpha(alpha);
                    learner.setLambda(lamda);
                    learner.reset();

                    for(int i=0;i<max;i++){
                        for(int j=0;j<max;j++){
                            if(i==j){
                                continue;
                            }
                            learner.addRating(i,j,generator.predict(i,j));
                        }
                    }
                    learner.pass(100);

                    double learningerr = learner.getAverageError();

                    double error=0;
                    int count=0;
                    for(int i=0;i<max;i++){
                            error = error+Math.abs(learner.predict(i,i)-generator.predict(i,i));
                            count++;
                    }
                    error = error/count;
                    double crossVal= error;

                    System.out.println(alpha + " , " + lamda + " , " + learningerr + " , " + crossVal);
                    out.print(learningerr+" ");
                    out2.print(crossVal + " ");
                }
                out.println();
                out2.println();
            }
            out.close();
            out2.close();
       } catch (IOException e) {
            e.printStackTrace();
        }











    }
}
