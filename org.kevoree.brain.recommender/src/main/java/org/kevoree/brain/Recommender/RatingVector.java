package org.kevoree.brain.Recommender;

/**
 * Created by assaad on 21/05/15.
 */
public class RatingVector {

    public RatingVector(String uid, String pid, double value){
        this.uid=uid;
        this.pid=pid;
        this.value=value;
    }

    public String uid;
    public String pid;
    public double value;
}
