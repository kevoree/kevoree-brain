package org.kevoree.brain.Recommender;


import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by assaad on 19/01/15.
 */
public class Recommender {
    private double alpha=0.001; //Learning rate
    private double lambda = 0.001; // regularization factor
    private int iterations =200; //number of iterations
    private int loopIter=10000;
    private int loopTimes=1;
    private int numOfFeatures=100; //number of features
    private static String separator="\t";
    HashMap<Integer, User> users = new HashMap<Integer, User>();
    HashMap<Integer, Product> products = new HashMap<Integer, Product>();


    // HashMap<Integer, Rating> ratings = new HashMap<Integer, Rating>();



    public Recommender() {
    }

    public double usersSum=0;
    public long usersCount=0;
    public double productsSum=0;
    public long productsCount=0;
    public double ratingsSum=0;
    public long ratingsCount=0;


    public ArrayList<RatingVector> testVector=new ArrayList<RatingVector>();

    public HashMap<Integer, User> getUsers() {
        return users;
    }
    public HashMap<Integer, Product> getProducts() {
        return products;
    }



    public void splitRating(double proba){

        boolean[] usersTrain=new boolean[users.size()] ;
        boolean[] productsTrain=new  boolean[products.size()] ;


        ArrayList<RatingVector> train=new ArrayList<RatingVector>();
        ArrayList<RatingVector> test=new ArrayList<RatingVector>();
        Random random=new Random();


        int counter=0;

        for (Integer k : users.keySet()) {
            User user = users.get(k);
            counter++;
            if(counter%(users.size()/20)==0){
                System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / users.size()) + "%");
            }
            for (Integer prod : user.getRatings().keySet()) {
                Product product = user.getRatings().get(prod).getProduct();

                RatingVector rv = new RatingVector(k,prod,user.getRatings().get(prod).getValue());

                if(usersTrain[user.incrementalId]){
                    if(productsTrain[product.incrementalId]){
                        //play proba here
                        if(random.nextDouble()<proba){
                            test.add(rv);
                        }
                        else {
                            train.add(rv);
                        }
                    }
                    else{
                        productsTrain[product.incrementalId]=true;
                        train.add(rv);
                    }

                }
                else{
                    usersTrain[user.incrementalId]=true;
                    train.add(rv);
                    if(productsTrain[product.incrementalId]==false){
                        productsTrain[product.incrementalId]=true;
                    }
                }
            }
        }

        System.out.println("Train size: "+train.size());
        System.out.println("Test size: "+test.size());
        System.out.println("Percent: "+((double)test.size()*100)/(test.size()+train.size()));

        try {
            PrintStream out = new PrintStream(new FileOutputStream("train.csv"));
            for (int i = 0; i <train.size(); i++) {
                out.println(train.get(i).uid+","+train.get(i).pid+","+train.get(i).value);
            }
            out.close();

            out = new PrintStream(new FileOutputStream("test.csv"));
            for (int i = 0; i <test.size(); i++) {
                out.println(test.get(i).uid+","+test.get(i).pid+","+test.get(i).value);
            }
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }



    public User addUser (Integer id, String username){
        User user = new User(id,username,numOfFeatures);
        users.put(id,user);
        return user;
    }

    public Product addProduct(Integer id, String productname){
        Product product = new Product(id,productname,numOfFeatures);
        products.put(id, product);
        return product;
    }

    public void exportRating(){
        try {
            PrintStream out = new PrintStream(new FileOutputStream("ratings.csv"));
            int counter=0;
            for (Integer k : users.keySet()) {
                counter++;
                User user = users.get(k);
                if(counter%(users.size()/20)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / users.size()) + "%");
                }
                for (Integer prod : user.getRatings().keySet()) {
                    Rating rating = user.getRatings().get(prod);
                    out.println(k+","+prod+","+rating.getValue());
                }
            }
            out.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addRating(Integer userId, Integer productId, double value, long timestamp, boolean updateweights){
        User user = users.get(userId);
        if(user==null){
            user=addUser(userId,"");
        }
        Product product = products.get(productId);
        if(product==null){
            product=addProduct(productId,"");
        }
        Rating rating = new Rating(user, product,value,timestamp);
        updateAvgRating(user,product,value);

        if(updateweights){
                update(user, product, value);
                // LearningVector.updateBatch(user,1);
                // LearningVector.updateBatch(product,5);
            /*if(ratingsCount%loopIter==0){
                loopRatings();
            }*/
        }


    }

    private ArrayList<ArrayList<Product>> distribution=null;

    public void parallelLoopRatings(int numThreads){

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < distribution.size(); i++) {
            Runnable worker = new ParallelUpdateTask(distribution.get(i),this);
            executor.execute(worker);
        }
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        // Wait until all threads are finish
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void loopRatings(){
        for(int i=0;i<loopTimes;i++) {
            for (Integer k : users.keySet()) {
                User user = users.get(k);
                for (Integer prod : user.getRatings().keySet()) {
                    Rating r = user.getRatings().get(prod);
                    updateOnce(user, r.getProduct(), r.getValue());
                }
            }

           // System.out.println(i);
        }
    }

    public void setParameters(double alpha, double lambda, int iterations, int numOfFeatures, int loopIter, int loopTimes) {
        this.alpha = alpha;
        this.lambda = lambda;
        this.iterations = iterations;
        this.numOfFeatures = numOfFeatures;
        this.loopIter=loopIter;
        this.loopTimes=loopTimes;
    }


    public double error(Rating rating){
        return rating.getValue()-predict(rating.getUser(),rating.getProduct(),true);
    }


    public double getAverageError(){
        double avg=0;
        double variance=0;

        double avgRandom=0;
        double varRandom=0;
        int count=0;
        double err;
       // ArrayList<Double> errors = new ArrayList<Double>(ratingCounter);
        double[] errors= new double[(int)ratingsCount];
        double[] errorsRandom= new double[(int)ratingsCount];
        double[] ratings = new double[(int)ratingsCount];


        int i=0;

        for(Integer k: users.keySet()){
            User user = users.get(k);
            for(Integer prod: user.getRatings().keySet()){
                Rating rating= user.getRatings().get(prod);
                err=error(rating);
                errors[i] =err;
                errorsRandom[i]=predictRandom()-rating.getValue();
                ratings[i]=rating.getValue();


                avg+=Math.abs(err);
                variance+=err*err;

                avgRandom+=Math.abs(errorsRandom[i]);
                varRandom+=errorsRandom[i]*errorsRandom[i];

                count++;
                i++;
            }
        }
        if(count!=0){
            avg=avg/count;
            variance=Math.sqrt(variance/count);

            avgRandom=avgRandom/count;
            varRandom=Math.sqrt(varRandom/count);
        }
        //System.out.println(count);
        MathUtil.calcHistogramArray(errors,errorsRandom,ratings,20,"histogram.csv");

        System.out.println("Average error: "+avg+" , random: "+avgRandom);
        System.out.println("STD: "+variance+" , random: "+varRandom);
        return avg;
    }


    private void updateAvgRating(User user, Product product, double value){
        double prevusravg;
        double prevProdavg;

        if(user.getLv().counter==0){
            prevusravg=0;
            usersCount++;
        }
        else{
            prevusravg=user.getLv().getAverage();
        }

        if(product.getLv().counter==0){
            prevProdavg=0;
            productsCount++;
        }
        else{
            prevProdavg=product.getLv().getAverage();
        }

        user.getLv().sum += value;
        user.getLv().counter++;

        product.getLv().sum += value;
        product.getLv().counter++;

        ratingsSum+=value;
        ratingsCount++;

        usersSum += user.getLv().getAverage()-prevusravg;
        productsSum += product.getLv().getAverage()-prevProdavg;
    }

    private ArrayList<ArrayList<RatingVector>> testdistrib=null;

    private String getParrallelRecPerformance(int round) {
        double avg = 0;
        double variance = 0;

        double avgTest = 0;
        double vartest = 0;
        int count = 0;


        if(distribution==null){
            long ratperthread=ratingsCount/(core);
            distribution=new ArrayList<ArrayList<Product>>();
            long localcount=0;
            ArrayList<Product> tempProd = new ArrayList<Product>();

            for(Integer prod: products.keySet()){
                Product p=products.get(prod);
                tempProd.add(p);
                localcount+=p.getRatings().size();
                if(localcount>=ratperthread){
                    distribution.add(tempProd);
                    tempProd = new ArrayList<Product>();
                    localcount=0;
                }
            }
            if(tempProd.size()!=0){
                distribution.add(tempProd);
            }
            System.out.println("Created "+distribution.size()+" threads");
        }

        ExecutorService executor = Executors.newFixedThreadPool(core);
        ArrayList<ParallelEstimateTask> trains = new ArrayList<ParallelEstimateTask>();
        for (int i = 0; i < distribution.size(); i++) {
            trains.add(new ParallelEstimateTask(distribution.get(i),this));
            executor.execute(trains.get(i));
        }
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executor.shutdown();
        // Wait until all threads are finish
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i=0;i<trains.size();i++){
            avg+=trains.get(i).avg;
            variance+=trains.get(i).variance;
            count+=trains.get(i).count;
        }


        avg = avg / count;
        variance = Math.sqrt(variance / count);


        if(testdistrib==null){
            testdistrib=new ArrayList<ArrayList<RatingVector>>();
            int sz = testVector.size()/core;

            ArrayList<RatingVector> tempProd = new ArrayList<RatingVector>();

            for(RatingVector rating: testVector){
                tempProd.add(rating);
                if(tempProd.size()>=sz){
                    testdistrib.add(tempProd);
                    tempProd = new ArrayList<RatingVector>();
                }
            }
            if(tempProd.size()!=0){
                testdistrib.add(tempProd);
            }
            System.out.println("Created "+testdistrib.size()+" test threads");
        }


        ExecutorService executorTest = Executors.newFixedThreadPool(core);
        ArrayList<ParallelEstimateTest> tests = new ArrayList<ParallelEstimateTest>();
        for (int i = 0; i < testdistrib.size(); i++) {
            tests.add(new ParallelEstimateTest(testdistrib.get(i),this));
            executorTest.execute(tests.get(i));
        }
        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        executorTest.shutdown();
        // Wait until all threads are finish
        try {
            executorTest.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        count=0;
        for(int i=0;i<tests.size();i++){
            avgTest+=tests.get(i).avg;
            vartest+=tests.get(i).variance;
            count+=tests.get(i).count;
        }


        avgTest = avgTest / count;
        vartest = Math.sqrt(vartest / count);

        String s = round + " , " + new DecimalFormat("#0.00000000").format(avg) + " , " +new DecimalFormat("#0.00000000").format(variance)+" , "+new DecimalFormat("#0.00000000").format(avgTest)+" , "+new DecimalFormat("#0.00000000").format(vartest);
        return s;

    }

    private String getRecPerformance(int round) {
        double avg = 0;
        double variance = 0;

        double avgTest = 0;
        double vartest = 0;
        int count = 0;

        double err;


        for (Integer k : users.keySet()) {
            User user = users.get(k);
            for (Integer prod : user.getRatings().keySet()) {
                Rating rating = user.getRatings().get(prod);
                err = error(rating);

                avg += Math.abs(err);
                variance += err * err;
                count++;
            }
        }
        avg = avg / count;
        variance = Math.sqrt(variance / count);


        count=0;
        for(int i=0;i<testVector.size();i++){
            RatingVector rv= testVector.get(i);
            err=rv.value-predict(rv.uid,rv.pid);
            avgTest += Math.abs(err);
            vartest += err * err;
            count++;
        }
        avgTest = avgTest / count;
        vartest = Math.sqrt(vartest / count);

        String s = round + " , " + new DecimalFormat("#0.00000000").format(avg) + " , " +new DecimalFormat("#0.00000000").format(variance)+" , "+new DecimalFormat("#0.00000000").format(avgTest)+" , "+new DecimalFormat("#0.00000000").format(vartest);
        return s;

    }


    public void playRound(int rounds){

        try {
            int threads=24;
            PrintStream out = new PrintStream(new FileOutputStream("results.csv"));
            System.out.println("alpha: "+alpha+", lambda: "+lambda+" , features: "+numOfFeatures);
            out.println("alpha: "+alpha+", lambda: "+lambda+" , features: "+numOfFeatures);
            long starttime,endtime;
            double result;
            for(int r=0;r<rounds;r++) {
                starttime= System.nanoTime();
                //String s=getRecPerformance(r);
                String s=getParrallelRecPerformance(r);
                //loopRatings();
                parallelLoopRatings(threads);
                endtime=System.nanoTime();
                result=((double)(endtime-starttime))/1000000000;
                System.out.println(s+" , time: "+result+" s");
                out.println(s);
                out.flush();
            }
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


    }

    public ArrayList<PredictedRating> recommend(String userId){
        User user=users.get(userId);
        ArrayList<PredictedRating> predictions = new ArrayList<PredictedRating>();
        ArrayList<Integer> p = new ArrayList<Integer>();
        for(int i=0;i<products.size();i++){
            p.add(i);
        }
        for(Integer prod: user.getRatings().keySet()){
            p.remove(prod);
        }

        for(Integer i: p){
            PredictedRating pr= new PredictedRating();
            pr.setUser(user);
            pr.setProduct(products.get(i));
            pr.setValue(predict(user,products.get(i),true));
            predictions.add(pr);
        }
        predictions.sort(new PredictedRating());
        return predictions;
    }

    private double predictRandom(){
        Random rand =new Random();
        double x= rand.nextDouble()*5;
        return x;
    }

    public double getAvgProducts(){
        if(productsCount!=0){
            return productsSum/productsCount;
        }
        else
            return 0;
    }

    public double getAvgUsers(){
        if(usersCount!=0){
            return usersSum/usersCount;
        }
        else
            return 0;
    }

    public double getAvgRatings(){
        if(ratingsCount!=0){
            return ratingsSum/ratingsCount;
        }
        else
            return 0;
    }


    private double predict(User user, Product product, boolean truncate) {

        //with bias
        double val = getAvgRatings() + (product.getAverage() - getAvgProducts()) + (user.getAverage() - getAvgUsers()) + LearningVector.multiply(user.getLv(), product.getLv());

        //without bias
        // double val= LearningVector.multiply(user.getLv(), product.getLv());
        if (truncate) {
            if (val < 0) {
                val = 0;
            }
            if (val > 5) {
                val = 5;
            }
        }
        return val;

        /**/
    }

    public double predict(Integer userId, Integer productId){
        User user = users.get(userId);
        Product product = products.get(productId);
        return predict(user,product,true);
    }

    public void displayStats(){
        System.out.println("Num of products: "+products.size()+" / "+ productsCount);
        System.out.println("Num of users: "+users.size() +" / " + usersCount);
        int value=0;
        for(Integer k: users.keySet()) {
            User user = users.get(k);
            value+=user.getRatings().size();
        }
        System.out.println("Num of ratings: "+value+ " / "+ratingsCount);

        System.out.println("Avg of products: "+getAvgProducts());
        System.out.println("Avg of users: "+getAvgUsers());
        System.out.println("Avg of ratings: "+getAvgRatings());

    }


    public void displayPredictions() {
        try {
            PrintWriter out = new PrintWriter(new FileWriter("Results.txt"));
            for(Integer k: users.keySet()){
                User user = users.get(k);
                for(Integer prod: user.getRatings().keySet()){
                    Rating rating= user.getRatings().get(prod);
                    out.println(user.getName() + separator + rating.getProduct().getName() + separator + rating.getValue()+separator+predict(user,rating.getProduct(),true));
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void update(User user, Product product, double value){
        for(int iter=0; iter<iterations;iter++){
            updateOnce(user,product,value);
        }
    }

    public  void updateOnce(User user, Product product, double value) {
        double[] newProdWeights = new double[numOfFeatures];
        double[] newuserWeights = new double[numOfFeatures];
      // double diff = LearningVector.multiply(user.getLv(), product.getLv()) - value;
        double diff = predict(user,product,false)-value;
        for (int i = 0; i < numOfFeatures; i++) {
            newProdWeights[i] = product.getLv().taste[i] - alpha * (diff * user.getLv().taste[i] + lambda * product.getLv().taste[i]);
            newuserWeights[i] = user.getLv().taste[i] - alpha* (diff * product.getLv().taste[i] + lambda * user.getLv().taste[i]);
        }
        for (int i = 0; i < numOfFeatures; i++) {
            product.getLv().taste[i] = newProdWeights[i];
            user.getLv().taste[i] = newuserWeights[i];
        }

    }

    public void updateBatch(User user, int iterations){
        for(int i=0;i<iterations;i++) {
            for (Integer k : user.getRatings().keySet()) {
                Rating r = user.getRatings().get(k);
                updateOnce(user,r.getProduct(),r.getValue());
            }
        }
    }

    public void updateBatch(Product product, int iterations){
        for(int i=0;i<iterations;i++) {
            for (Integer k : product.getRatings().keySet()) {
                Rating r = product.getRatings().get(k);
                updateOnce(r.getUser(),r.getProduct(),r.getValue());
            }
        }
    }

    private int core;

    public void setCore(int core) {
        this.core = core;
    }
}
