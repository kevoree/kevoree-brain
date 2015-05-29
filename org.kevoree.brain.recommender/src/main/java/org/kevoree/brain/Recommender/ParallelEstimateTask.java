package org.kevoree.brain.Recommender;

import java.util.ArrayList;

/**
 * Created by assaad on 29/05/15.
 */
public class ParallelEstimateTask implements Runnable {
    private ArrayList<Product> toEstimate;
    private Recommender recommender;

    public double avg = 0;
    public double variance = 0;
    public int  count=0;

    public ParallelEstimateTask(ArrayList<Product> toEstimate, Recommender recommender){
        this.toEstimate=toEstimate;
        this.recommender=recommender;
    }



    @Override
    public void run() {
        double err;

        for (Product prod  : toEstimate) {
            for (Integer user : prod.getRatings().keySet()) {
                Rating rating = prod.getRatings().get(user);
                err = recommender.error(rating);
                avg += Math.abs(err);
                variance += err * err;
                count++;
            }
        }
    }
}

