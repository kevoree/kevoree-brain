package org.kevoree.brain.learning.livelearning.Recommender;

import java.util.ArrayList;

/**
 * Created by assaad on 19/01/15.
 */
public class Product {

    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    ArrayList<Rating> ratings= new ArrayList<Rating>();

    public void addRating(Rating rating){
        ratings.add(rating);
    }

    public double[] weights;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
