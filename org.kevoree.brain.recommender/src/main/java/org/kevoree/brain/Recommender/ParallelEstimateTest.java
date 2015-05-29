package org.kevoree.brain.Recommender;

import java.util.ArrayList;

/**
 * Created by assaad on 29/05/15.
 */
public class ParallelEstimateTest implements Runnable {
    private ArrayList<RatingVector> toEstimate;
    private Recommender recommender;

    public double avg = 0;
    public double variance = 0;
    public int  count=0;

    public ParallelEstimateTest(ArrayList<RatingVector> toEstimate, Recommender recommender){
        this.toEstimate=toEstimate;
        this.recommender=recommender;
    }



    @Override
    public void run() {
        double err;
        for(int i=0;i<toEstimate.size();i++){
            RatingVector rv= toEstimate.get(i);
            err=rv.value-recommender.predict(rv.uid,rv.pid);
            avg += Math.abs(err);
            variance += err * err;
            count++;
        }
    }
}

