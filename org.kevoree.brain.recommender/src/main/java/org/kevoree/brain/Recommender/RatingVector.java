package org.kevoree.brain.Recommender;

/**
 * Created by assaad on 21/05/15.
 */
public class RatingVector {

    public RatingVector(Integer uid, Integer pid, double value){
        this.uid=uid;
        this.pid=pid;
        this.value=value;
    }

    public Integer uid;
    public Integer pid;
    public double value;
}
