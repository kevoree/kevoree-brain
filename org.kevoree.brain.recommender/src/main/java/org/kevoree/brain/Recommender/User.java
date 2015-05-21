package org.kevoree.brain.Recommender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 19/01/15.
 */
public class User {
    private static int _id=0;
    public static double sum;
    public static int count;
    public static double getOverAllAvg(){
        return sum/count;
    }

    public int incrementalId;
    private Integer id;
    private String name;
    private HashMap<Integer, Rating> ratings;
    private LearningVector lv;

    public HashMap<Integer, Rating> getRatings() {
        return ratings;
    }



    public LearningVector getLv() {
        return lv;
    }
    public void setLv(LearningVector lv) {
        this.lv = lv;
    }


    public User(Integer id, String name, int numOfFeatures) {
        this.incrementalId=_id;
        _id++;
        this.id = id;
        this.name = name;
        ratings= new HashMap<Integer, Rating>();
        lv = new LearningVector(numOfFeatures);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void addRating(Integer productid, Rating rating){
        ratings.put(productid, rating);
    }

    public double getAverage(){
        return lv.getAverage();
    }
}
