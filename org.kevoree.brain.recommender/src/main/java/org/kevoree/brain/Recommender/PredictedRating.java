package org.kevoree.brain.Recommender;

import java.util.Comparator;

/**
 * Created by assaad on 20/01/15.
 */
public class PredictedRating implements Comparator<PredictedRating> {
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private User user;
    private Product product;
    private double value;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compare(PredictedRating o1, PredictedRating o2) {
        return Double.compare(o2.getValue(),o1.getValue());
    }
}
