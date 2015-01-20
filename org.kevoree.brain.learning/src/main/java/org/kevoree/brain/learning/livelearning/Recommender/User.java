package org.kevoree.brain.learning.livelearning.Recommender;

import java.util.ArrayList;

/**
 * Created by assaad on 19/01/15.
 */
public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int id;
    String name;
    ArrayList<Rating> ratings= new ArrayList<Rating>();

    public void addRating(Rating rating){
        ratings.add(rating);
    }

    public double[] weights;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
