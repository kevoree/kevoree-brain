package org.kevoree.brain.Recommender.test;

import org.grouplens.lenskit.ItemScorer;
import org.grouplens.lenskit.RatingPredictor;
import org.grouplens.lenskit.RecommenderBuildException;
import org.grouplens.lenskit.baseline.BaselineScorer;
import org.grouplens.lenskit.baseline.ItemMeanRatingItemScorer;
import org.grouplens.lenskit.baseline.UserMeanBaseline;
import org.grouplens.lenskit.baseline.UserMeanItemScorer;
import org.grouplens.lenskit.core.LenskitConfiguration;
import org.grouplens.lenskit.core.LenskitRecommender;
import org.grouplens.lenskit.data.dao.EventDAO;
import org.grouplens.lenskit.data.dao.SimpleFileRatingDAO;
import org.grouplens.lenskit.knn.item.ItemItemScorer;
import org.grouplens.lenskit.transform.normalize.BaselineSubtractingUserVectorNormalizer;
import org.grouplens.lenskit.transform.normalize.UserVectorNormalizer;
import org.kevoree.brain.Recommender.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by assaad on 19/05/15.
 */
public class TestLensKitCross {

    public static String dir="./Movielens/";

    public static int total=21063128;
    // public static int total=1000209;

    public static Recommender getRec(){

        String csvfile;
        String line;
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();

        //alpha,lambda,iterations,numFeatures, loopiter
        recommender.setParameters(0.001,0.001,5,14,100000,1);
        long starttime;
        long endtime;
        double result;

        int total=18983578;

        csvfile="train.csv";
        starttime= System.nanoTime();
        int counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.addRating(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2]), 0, false);
                counter++;
                if(counter%(total/20)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / total) + "%");
                }
            }
            //recommender.loopRatings(100);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("train set of "+recommender.getRatingCounter()+" ratings loaded in "+result+" s");
        recommender.displayStats();



        csvfile="test.csv";
        total=2079550;
        starttime= System.nanoTime();
        counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.testVector.add(new RatingVector(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2])));
                counter++;
                if(counter%(total/20)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / total) + "%");
                }
            }
            //recommender.loopRatings(100);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("test set of "+recommender.testVector.size()+" ratings loaded in "+result+" s");
        return recommender;
    }


    public static boolean test=true;

    private static String getRecPerformance(Recommender kevoree, LenskitRecommender lenskit) {
        double avg = 0;
        double variance = 0;

        double avgTest = 0;
        double vartest = 0;
        int count = 0;

        double err;

        RatingPredictor pred = lenskit.getRatingPredictor();


        for (Integer k : kevoree.getUsers().keySet()) {
            User user = kevoree.getUsers().get(k);
            for (Integer prod : user.getRatings().keySet()) {


                Rating rating = user.getRatings().get(prod);
                err =rating.getValue()-pred.predict(k, prod);

                avg += Math.abs(err);
                variance += err * err;
                count++;

                if(count%(kevoree.getUsers().size()/100)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (count * 100)) / kevoree.getUsers().size()) + "%");
                }
            }
        }
        avg = avg / count;
        variance = Math.sqrt(variance / count);


        count=0;
        for(int i=0;i<kevoree.testVector.size();i++){
            RatingVector rv= kevoree.testVector.get(i);
            err=rv.value-pred.predict(rv.uid, rv.pid);
            avgTest += Math.abs(err);
            vartest += err * err;
            count++;
        }
        avgTest = avgTest / count;
        vartest = Math.sqrt(vartest / count - avgTest * avgTest);

        String s = new DecimalFormat("#0.00000000000000").format(avg) + " , " +new DecimalFormat("#0.00000000000000").format(variance)+" , "+new DecimalFormat("#0.00000000000000").format(avgTest)+" , "+new DecimalFormat("#0.00000000000000").format(vartest);
        return s;

    }



    public static void main(String[] args) {
        LenskitConfiguration config = new LenskitConfiguration();

        // Use item-item CF to score items
        config.bind(ItemScorer.class).to(ItemItemScorer.class);

        // let's use personalized mean rating as the baseline/fallback predictor.
        // 2-step process:
        // First, use the user mean rating as the baseline scorer
        config.bind(BaselineScorer.class, ItemScorer.class).to(UserMeanItemScorer.class);

        // Second, use the item mean rating as the base for user means
        config.bind(UserMeanBaseline.class, ItemScorer.class).to(ItemMeanRatingItemScorer.class);

        // and normalize ratings by baseline prior to computing similarities
        config.bind(UserVectorNormalizer.class).to(BaselineSubtractingUserVectorNormalizer.class);

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File(dir+"train.csv"), ","));

        long starttime;
        long endtime;
        double result;

        try {
            starttime= System.nanoTime();
            LenskitRecommender rec = LenskitRecommender.build(config);
            endtime= System.nanoTime();
            result= ((double)(endtime-starttime))/(1000000000);
            System.out.println("Trained in: "+result+" s");

            Recommender kevoree= getRec();
            System.out.println(getRecPerformance(kevoree, rec));

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }



    }
}
