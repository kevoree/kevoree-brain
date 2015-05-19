package org.kevoree.brain.learning.livelearning.Recommender;

import org.kevoree.brain.util.Histogram;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.util.*;

/**
 * Created by assaad on 19/01/15.
 */
public class Recommender {
    private static String separator="\t";
    HashMap<String, User> users = new HashMap<String, User>();
    HashMap<String, Product> products = new HashMap<String, Product>();

   // HashMap<Integer, Rating> ratings = new HashMap<Integer, Rating>();
    int ratingCounter=0;


    public Recommender() {
    }


    public HashMap<String, User> getUsers() {
        return users;
    }
    public HashMap<String, Product> getProducts() {
        return products;
    }




    public User addUser (String id, String username){
        User user = new User(id,username);
        users.put(id,user);
        return user;
    }

    public Product addProduct(String id, String productname){
        Product product = new Product(id,productname);
        products.put(id, product);
        return product;
    }

    public void addRating(String userId, String productId, double value, long timestamp, boolean updateweights){
        User user = users.get(userId);
        if(user==null){
            user=addUser(userId,"");
        }
        Product product = products.get(productId);
        if(product==null){
            product=addProduct(productId,"");
        }
        Rating rating = new Rating(user, product,value,timestamp,updateweights);


        ratingCounter++;

        if(ratingCounter%100000==0){
            loopRatings();
        }
    }

    public void loopRatings(){
        for(String k: users.keySet()) {
            User user = users.get(k);
            for (String prod : user.getRatings().keySet()) {
                Rating r = user.getRatings().get(prod);
                LearningVector.updateOnce(user.getLv(), r.getProduct().getLv(), r.getValue());
            }
        }
    }



    public double error(Rating rating){
        return rating.getValue()-predict(rating.getUser(),rating.getProduct());
    }


    public double getAverageError(){
        double avg=0;
        double variance=0;
        int count=0;
        double err;
        ArrayList<Double> errors = new ArrayList<Double>();

        for(String k: users.keySet()){
            User user = users.get(k);
            for(String prod: user.getRatings().keySet()){
                Rating rating= user.getRatings().get(prod);
                err=error(rating);
                errors.add(err);
                avg+=Math.abs(err);
                variance+=err*err;
                count++;
            }
        }
        if(count!=0){
            avg=avg/count;
            variance=Math.sqrt(variance/count-avg*avg);
        }
        //System.out.println(count);
        Histogram.calcHistogramArray(errors,1000);

        System.out.println("Average error: "+avg);
        System.out.println("STD: "+variance);
        return avg;
    }



    public ArrayList<PredictedRating> recommend(String userId){
        User user=users.get(userId);
        ArrayList<PredictedRating> predictions = new ArrayList<PredictedRating>();
        ArrayList<Integer> p = new ArrayList<Integer>();
        for(int i=0;i<products.size();i++){
            p.add(i);
        }
        for(String prod: user.getRatings().keySet()){
            p.remove(prod);
        }

        for(Integer i: p){
            PredictedRating pr= new PredictedRating();
            pr.setUser(user);
            pr.setProduct(products.get(i));
            pr.setValue(predict(user,products.get(i)));
            predictions.add(pr);
        }
        predictions.sort(new PredictedRating());
        return predictions;
    }


    private double predict(User user, Product product){
       return LearningVector.multiply(user.getLv(),product.getLv());
    }

    public double predict(String userId, String productId){
        User user = users.get(userId);
        Product product = products.get(productId);
        return predict(user,product);
    }

    public void displayStats(){
        System.out.println("Num of products: "+products.size());
        System.out.println("Num of users: "+users.size());
        int value=0;
        for(String k: users.keySet()) {
            User user = users.get(k);
            value+=user.getRatings().size();
        }
        System.out.println("Num of ratings: "+value);
    }


    public void displayPredictions() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("Results.txt"));
            for(String k: users.keySet()){
                User user = users.get(k);
                for(String prod: user.getRatings().keySet()){
                    Rating rating= user.getRatings().get(prod);
                    out.println(user.getName() + separator + rating.getProduct().getName() + separator + rating.getValue()+separator+predict(user,rating.getProduct()));
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
