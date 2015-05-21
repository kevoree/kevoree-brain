package org.kevoree.brain.Recommender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 19/01/15.
 */
public class Product {

    public static double sum;
    public static int count;
    public static double getOverAllAvg(){
    return sum/count;
    }



    private Integer id;
    private String name;
    private static int _id=0;
    public int incrementalId;
    public HashMap<Integer, Rating> getRatings() {
        return ratings;
    }

    private HashMap<Integer, Rating> ratings;
    private LearningVector lv;

    public Product(Integer id, String name, int numOfFeatures) {
        this.incrementalId=_id;
        _id++;
        this.id = id;
        this.name = name;
        ratings= new HashMap<Integer, Rating>();
        lv = new LearningVector(numOfFeatures);
    }

    public LearningVector getLv() {
        return lv;
    }
    public void setLv(LearningVector lv) {
        this.lv = lv;
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

    public void addRating(Integer userid, Rating rating){
        ratings.put(userid, rating);
    }
    public double getAverage(){
        return lv.getAverage();
    }
}
