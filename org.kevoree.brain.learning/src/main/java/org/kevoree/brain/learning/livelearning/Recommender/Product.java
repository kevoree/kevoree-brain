package org.kevoree.brain.learning.livelearning.Recommender;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by assaad on 19/01/15.
 */
public class Product {
    private String id;
    private String name;
    private HashMap<String, Rating> ratings;
    private LearningVector lv;

    public Product(String id, String name) {
        this.id = id;
        this.name = name;
        ratings= new HashMap<String, Rating>();
        lv = new LearningVector();
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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void addRating(String userid, Rating rating, boolean update){
        ratings.put(userid, rating);
    }
}
