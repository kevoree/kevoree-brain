package org.kevoree.brain.learning.livelearning.Recommender;

import java.util.Random;

/**
 * Created by assaad on 19/05/15.
 */
public class LearningVector {


    private static double alpha=0.001; //Learning rate
    private static double lambda = 0.001; // regularization factor
    private static int iterations =200; //number of iterations
    private static int numOfFeatures=100; //number of features
    private static String separator="\t";
    private static Random rand = new Random();

    public static void setParameters(double alpha, double lambda, int iterations, int numOfFeatures) {
        LearningVector.alpha = alpha;
        LearningVector.lambda = lambda;
        LearningVector.iterations = iterations;
        LearningVector.numOfFeatures = numOfFeatures;
    }

    public double sum;
    public int counter;
    public double[] taste;


    public LearningVector() {
        taste=new double[numOfFeatures];
        for(int i=0; i<numOfFeatures;i++){
            taste[i]=rand.nextDouble();
        }
    }

    public double getAverage(){
        if (counter!=0){
            return sum/counter;
        }
        else return 0;
    }


    public static void update2(LearningVector user, LearningVector product, double value){
        user.sum+=value;
        user.counter++;

        product.sum+=value;
        product.counter++;

        for (int i = 0; i < numOfFeatures; i++) {
            for(int iter=0; iter<iterations;iter++){
                double diff = multiply(user, product) - value;
                product.taste[i]= product.taste[i] - alpha * (diff * user.taste[i] + lambda * product.taste[i]);
                user.taste[i] = user.taste[i] - alpha * (diff * product.taste[i] + lambda * user.taste[i]);
            }
        }
    }


    public static void update(LearningVector user, LearningVector product, double value){
        user.sum+=value;
        user.counter++;

        product.sum+=value;
        product.counter++;

        for(int iter=0; iter<iterations;iter++){
            double[] newProdWeights = new double[numOfFeatures];
            double[] newuserWeights = new double[numOfFeatures];
            double diff = multiply(user, product) - value;
            int d=0;
            for (int i = 0; i < numOfFeatures; i++) {
                newProdWeights[i] = product.taste[i] - alpha * (diff * user.taste[i] + lambda * product.taste[i]);
                newuserWeights[i] = user.taste[i] - alpha * (diff * product.taste[i] + lambda * user.taste[i]);
            }
            for (int i = 0; i < numOfFeatures; i++) {
                product.taste[i] = newProdWeights[i];
                user.taste[i] = newuserWeights[i];
            }
        }
    }

    public static void updateOnce(LearningVector user, LearningVector product, double value) {
        user.sum += value;
        user.counter++;

        product.sum += value;
        product.counter++;

        double[] newProdWeights = new double[numOfFeatures];
        double[] newuserWeights = new double[numOfFeatures];
        double diff = multiply(user, product) - value;
        int d = 0;
        for (int i = 0; i < numOfFeatures; i++) {
            newProdWeights[i] = product.taste[i] - alpha * (diff * user.taste[i] + lambda * product.taste[i]);
            newuserWeights[i] = user.taste[i] - alpha * (diff * product.taste[i] + lambda * user.taste[i]);
        }
        for (int i = 0; i < numOfFeatures; i++) {
            product.taste[i] = newProdWeights[i];
            user.taste[i] = newuserWeights[i];
        }

    }

    public static double multiply(LearningVector lv1, LearningVector lv2){
        double val=0;
        for(int i=0;i<lv1.taste.length;i++){
            val=val+lv1.taste[i]*lv2.taste[i];
        }
        return val;
    }
}
