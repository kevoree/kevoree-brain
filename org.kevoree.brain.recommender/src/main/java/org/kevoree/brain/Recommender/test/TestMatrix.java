package org.kevoree.brain.Recommender.test;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.kevoree.brain.Recommender.*;

import java.util.Random;

/**
 * Created by assaad on 28/05/15.
 */
public class TestMatrix {
    public static void main(String[] args){
        int users=10000;
        int products=200;
        int tastes=15;

        BetaDistribution[] betaProducts = new BetaDistribution[products];


        LearningVector.max=Math.sqrt(2.5/tastes);
        LearningVector.min=-LearningVector.max;

        Recommender oracle = new Recommender();
        oracle.setParameters(0.005,0.005,20,tastes,100,10);

        Random rand = new Random();
        for(int i=0;i<users;i++){
            User u= oracle.addUser(i,""+i);
        }

        for(int i=0;i<products;i++){
            oracle.addProduct(i, "" + i);
            betaProducts[i]=new BetaDistribution(rand.nextInt(10)+1,7);
        }

        double val=0;
        for(int i=0;i<users;i++) {
            BetaDistribution betauser = new BetaDistribution(rand.nextInt(10)+1,3);
            for (int j = 0; j < products; j++) {
                oracle.addRating(i,j,(betauser.inverseCumulativeProbability(rand.nextDouble())+betaProducts[j].inverseCumulativeProbability(rand.nextDouble()))*2.5,0,false);
           }
        }

        System.out.println("Oracle ready!");
        oracle.displayStats();


        Recommender train = new Recommender();
        train.setParameters(0.001,0.003,20,tastes,100,10);
        for(int i=0;i<users;i++){
            train.addUser(i,""+i);
        }

        for(int i=0;i<products;i++){
            train.addProduct(i, "" + i);
        }


        for(int i=0;i<users;i++) {
            for(int j=0;j<products;j++){
                if((i+j)%100<20){
                    train.testVector.add(new RatingVector(i,j,oracle.getUsers().get(i).getRatings().get(j).getValue()));
                }else {
                    train.addRating(i,j,oracle.getUsers().get(i).getRatings().get(j).getValue(),0,false);
                }
            }
        }
        System.out.println("Training and testing ready!");
        train.displayStats();
        System.out.println("Testing set: "+train.testVector.size());
        train.playRound(10000);


    }
}
