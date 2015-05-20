package org.kevoree.brain.learning.livelearning.Recommender;

import java.util.Random;

/**
 * Created by assaad on 19/05/15.
 */
public class LearningVector {



    private static Random rand = new Random();



    public double sum;
    public int counter;
    public double[] taste;


    public LearningVector(int numOfFeatures) {
        taste=new double[numOfFeatures];
        for(int i=0; i<numOfFeatures;i++){
            taste[i]=rand.nextDouble(); //*Math.sqrt(5/numOfFeatures);
        }
    }

    public double getAverage(){
        if (counter!=0){
            return sum/counter;
        }
        else return 0;
    }



    public static void updateAvgRating(User user, Product product, double value){
        double prevusravg;
        double prevProdavg;

        if(user.getLv().counter==0){
            prevusravg=0;
            User.count++;
        }
        else{
            prevusravg=user.getLv().getAverage();
        }

        if(product.getLv().counter==0){
            prevProdavg=0;
            Product.count++;
        }
        else{
            prevProdavg=product.getLv().getAverage();
        }

        user.getLv().sum += value;
        user.getLv().counter++;

        product.getLv().sum += value;
        product.getLv().counter++;

        Rating.sum+=value;
        Rating.count++;

        User.sum += user.getLv().getAverage()-prevusravg;
        Product.sum += product.getLv().getAverage()-prevProdavg;
    }



    public static double multiply(LearningVector lv1, LearningVector lv2){
        double val=0;
        for(int i=0;i<lv1.taste.length;i++){
            val=val+lv1.taste[i]*lv2.taste[i];
        }
        return val;
    }
}
