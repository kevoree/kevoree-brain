package org.kevoree.brain.learning.livelearning.Recommender;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.util.*;

/**
 * Created by assaad on 19/01/15.
 */
public class Recommender {

    private double alpha=0.01; //Learning rate
    private double lambda = 0.01; // regularization factor
    private int iterations =200; //number of iterations
    private int numOfFeatures=100; //number of features
    private static String separator="\t";
    private Random rand = new Random();

    public Recommender(double alpha, double lambda, int iterations, int numOfFeatures) {
        this.alpha = alpha;
        this.lambda = lambda;
        this.iterations = iterations;
        this.numOfFeatures = numOfFeatures;
    }

    public Recommender() {
    }


    public HashMap<String, User> getUsers() {
        return users;
    }

    public HashMap<String, Product> getProducts() {
        return products;
    }

    HashMap<String, User> users = new HashMap<String, User>();
    HashMap<String, Product> products = new HashMap<String, Product>();

    //private ArrayList<User> users=new ArrayList<User>();
   // private ArrayList<Product> products=new ArrayList<Product>();


    public User addUser (String id, String username){
        User user = new User();
        user.setName(username);
        user.setId(id);
        user.weights=new double[numOfFeatures];

        for(int i=0; i<numOfFeatures;i++){
            user.weights[i]=getNewWeight();
        }
        users.put(id,user);
        return user;
    }

    public Product addProduct(String id, String productname){
        Product product = new Product();
        product.setName(productname);
        product.setId(id);

        product.weights=new double[numOfFeatures];

        for(int i=0; i<numOfFeatures;i++){
            product.weights[i]=getNewWeight();
        }
        products.put(id, product);
        return product;
    }

    public void addRating(String userId, String productId, double value, long timestamp){
        User user = users.get(userId);
        if(user==null){
            user=addUser(userId,"");
        }
        Product product = products.get(productId);
        if(product==null){
            product=addProduct(productId,"");
        }
        Rating rating = new Rating(user, product,value,timestamp);
        updateWeights(user, product, value);
    }


    public void addRatingNoUpdate(String userId, String productId, double value, long timestamp){
        User user = users.get(userId);
        Product product = products.get(productId);
        Rating rating = new Rating(user, product,value,timestamp);
    }


    public double getNewWeight(){
        return rand.nextDouble();
    }


    public void reset(){
        for(String k: users.keySet()){
            User user = users.get(k);
            for (int i = 0; i < numOfFeatures; i++) {
                user.weights[i] = getNewWeight();
                user.ratings.clear();
            }
        }

        for(String k: products.keySet()){
            Product product = products.get(k);
            for (int i = 0; i < numOfFeatures; i++) {
                product.weights[i] = getNewWeight();
                product.ratings.clear();
            }
        }

    }

    public double error(Rating rating){
        return Math.abs(rating.getValue()-predict(rating.getUser(),rating.getProduct()));
    }


    public double getAverageError(){
        double avg=0;
        int count=0;
        for(String k: users.keySet()){
            User user = users.get(k);
            for(Rating rating:user.ratings){
                avg+=error(rating);
                count++;
            }
        }
        if(count!=0){
            avg=avg/count;
        }
        //System.out.println(count);
        return avg;
    }


    public void pass(int n){
        for(int i=0;i<n;i++) {
            for(String k: users.keySet()){
                User user = users.get(k);
                for (Rating rating : user.ratings) {
                    updateWeights(rating.getUser(),rating.getProduct(),rating.getValue());
                }
            }
        }
    }



    public ArrayList<PredictedRating> recommend(String userId){
        User user=users.get(userId);
        ArrayList<PredictedRating> predictions = new ArrayList<PredictedRating>();
        ArrayList<Integer> p = new ArrayList<Integer>();
        for(int i=0;i<products.size();i++){
            p.add(i);
        }
        for(Rating r: user.ratings){
            p.remove(r.getProduct().getId());
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
        double val=0;
        for(int i=0;i<numOfFeatures;i++){
            val=val+user.weights[i]*product.weights[i];
        }

        /*if(val>5){
            val=5;
        }
        if(val<0){
            val=0;
        }*/
        return val;
    }

    public double predict(String userId, String productId){
        User user = users.get(userId);
        Product product = products.get(productId);
        return predict(user,product);
    }

    private void updateWeights(User user, Product product, double value) {
        for(int iter=0;iter<iterations;iter++) {
            double[] newProdWeights = new double[numOfFeatures];
            double[] newuserWeights = new double[numOfFeatures];
            double diff = predict(user, product) - value;
            int d=0;
            for (int i = 0; i < numOfFeatures; i++) {
                newProdWeights[i] = product.weights[i] - alpha * (diff * user.weights[i] + lambda * product.weights[i]);
                newuserWeights[i] = user.weights[i] - alpha * (diff * product.weights[i] + lambda * user.weights[i]);
            }
            for (int i = 0; i < numOfFeatures; i++) {
                product.weights[i] = newProdWeights[i];
                user.weights[i] = newuserWeights[i];
            }
        }
    }

    public double calculateAverageRatingUser(String userId){
        User user = users.get(userId);
        double var=0;
        if(user.ratings.size()==0){
            return 0;
        }
        else {
            for (Rating r : user.ratings) {
                var=var+r.getValue();
            }
        }
        var=var/user.ratings.size();
        return var;

    }
    public double calculateAverageRatingProduct(String productId){
        Product product = products.get(productId);
        double var=0;
        if(product.ratings.size()==0){
            return 0;
        }
        else {
            for (Rating r : product.ratings) {
                var=var+r.getValue();
            }
        }
        var=var/product.ratings.size();
        return var;
    }


    public static Recommender load(String file){

        try {
            Recommender recommender = new Recommender();
            BufferedReader br = new BufferedReader(new FileReader(file));
            recommender.setAlpha(Double.parseDouble(br.readLine()));
            recommender.setLambda(Double.parseDouble(br.readLine()));
            recommender.setIterations(Integer.parseInt(br.readLine()));
            recommender.setNumOfFeatures(Integer.parseInt(br.readLine()));
            int usersize=Integer.parseInt(br.readLine());
            for(int i=0;i<usersize;i++){
                String u = br.readLine();
                String[] us=u.split(separator);
                User user = recommender.addUser(us[0],us[1]);
                for(int j=0; j<recommender.getNumOfFeatures();j++){
                    user.weights[j]=Double.parseDouble(us[j+2]);
                }

            }
            int productsize=Integer.parseInt(br.readLine());
            for(int i=0;i<productsize;i++){
                String u = br.readLine();
                String[] us=u.split(separator);
                Product product = recommender.addProduct(us[0], us[1]);
                for(int j=0; j<recommender.getNumOfFeatures();j++){
                    product.weights[j]=Double.parseDouble(us[j+2]);
                }
            }
            String line="";
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(separator);
                recommender.addRatingNoUpdate(splitLine[0],splitLine[1],Double.parseDouble(splitLine[2]),Long.parseLong(splitLine[3]));
            }

            br.close();
            return recommender;

        }
        catch (Exception ex){
            ex.printStackTrace();

        }

        return null;
    }

    public void save(){

        try {
            //create an print writer for writing to a file
            PrintWriter out = new PrintWriter(new FileWriter("Rec.txt"));
            out.println(alpha);
            out.println(lambda);
            out.println(iterations);
            out.println(numOfFeatures);
            out.println(users.size());
            for(int i=0;i<users.size();i++){
                out.print(users.get(i).getId()+separator+users.get(i).getName()+separator);
                for(int j=0;j<numOfFeatures;j++){
                    out.print(users.get(i).weights[j]+separator);
                }
                out.println();
            }
            out.println(products.size());
            for(int i=0;i<products.size();i++){
                out.print(products.get(i).getId()+separator+products.get(i).getName()+separator);
                for(int j=0;j<numOfFeatures;j++){
                    out.print(products.get(i).weights[j]+separator);
                }
                out.println();
            }
            for(int i=0;i<users.size();i++){
                for(Rating r: users.get(i).ratings){
                    out.println(r.getUser().getId() + separator + r.getProduct().getId() + separator + r.getValue() + separator + r.getTimestamp());
                }
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public int getNumOfFeatures() {
        return numOfFeatures;
    }

    public void setNumOfFeatures(int numOfFeatures) {
        this.numOfFeatures = numOfFeatures;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }


    public void displayPredictions() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("Results.txt"));
            for(String k: users.keySet()){
                User user = users.get(k);                for(Rating rating:user.ratings){
                    out.println(user.getName() + separator + rating.getProduct().getName() + separator + rating.getValue()+separator+predict(user,rating.getProduct()));
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
