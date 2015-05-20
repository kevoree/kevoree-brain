package org.kevoree.brain.learning.livelearning.Recommender;

/**
 * Created by assaad on 19/01/15.
 */
public class Rating {

    public static double sum;
    public static int count;
    public static double getOverAllAvg(){
        return sum/count;
    }

    private long timestamp;
    private User user;
    private Product product;
    private double value;


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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



    public Rating (User user, Product product, double value, long timestamp){
        this.user = user;
        this.product=product;
        this.value=value;
        this.timestamp=timestamp;
        user.addRating(product.getId(), this);
        product.addRating(user.getId(), this);
        LearningVector.updateAvgRating(user,product,value);
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
