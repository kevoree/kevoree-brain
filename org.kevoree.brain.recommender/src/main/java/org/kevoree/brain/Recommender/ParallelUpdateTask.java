package org.kevoree.brain.Recommender;

import java.util.ArrayList;

/**
 * Created by assaad on 29/05/15.
 */
public class ParallelUpdateTask implements Runnable {
    private ArrayList<Product> toUpdate;
    private Recommender recommender;

    public ParallelUpdateTask(ArrayList<Product> toUpdate, Recommender recommender){
        this.toUpdate=toUpdate;
        this.recommender=recommender;
    }



    @Override
    public void run() {
        for (Product prod  : toUpdate) {
            for (Integer user : prod.getRatings().keySet()) {
                Rating r = prod.getRatings().get(user);
                recommender.updateOnce(r.getUser(), prod, r.getValue());
            }
        }
    }
}
