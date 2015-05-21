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
import org.kevoree.brain.Recommender.MathUtil;
import org.kevoree.brain.Recommender.Rating;
import org.kevoree.brain.Recommender.Recommender;
import org.kevoree.brain.Recommender.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by assaad on 19/05/15.
 */
public class TestLensKit {

    public static String dir="Movielens/";

    public static int total=21063128;
    // public static int total=1000209;

    public static Recommender getRec(){




        String csvfile="movies.csv";
        String line = "";
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();
        recommender.setParameters(0.005,0.001,5,50,100000,1);

        long starttime;
        long endtime;
        double result;


        csvfile="ratings.csv";
        starttime= System.nanoTime();
        int counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.addRating(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]), Double.parseDouble(vals[2]), Long.parseLong(vals[3]), true);
                counter++;
                if(counter%(total/20)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (counter * 100)) / total) + "%");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        recommender.displayStats();
        System.out.println("Model created in "+result+" s");
        //recommender.getAverageError();
        return recommender;
    }


    public static boolean test=true;

    public static double testErr(Recommender kevoree, LenskitRecommender lens){
        double[] avg=new double[3];
        double[] variance = new double[3];
        int count=0;
        double err;
        Random rand = new Random();
        // ArrayList<Double> errors = new ArrayList<Double>(ratingCounter);
        double[] errorsLens= new double[kevoree.getRatingCounter()];
        double[] errorsKev= new double[kevoree.getRatingCounter()];
        double[] errorsRand= new double[kevoree.getRatingCounter()];

        int i=0;

        RatingPredictor pred = lens.getRatingPredictor();



        for(Integer k: kevoree.getUsers().keySet()) {
            User user = kevoree.getUsers().get(k);
            for(Integer prod: user.getRatings().keySet()){
                Rating rating= user.getRatings().get(prod);
                err=pred.predict(k,prod)-rating.getValue();
                if(test){
                    test=false;
                    System.out.println("predicted lenskit error: "+ err+" "+rating.getValue());
                    System.out.println("predicted kevoree: "+ (kevoree.predict(k,prod)-rating.getValue())+" "+rating.getValue());
                }
                errorsLens[i] =err;
                errorsKev[i]=kevoree.predict(k,prod)-rating.getValue();
                errorsRand[i]=rand.nextDouble()*5-rating.getValue();


                avg[0]+=Math.abs(err);
                variance[0]+=err*err;

                avg[1]+=Math.abs(errorsKev[i]);
                variance[1]+=errorsKev[i]*errorsKev[i];

                avg[2]+=Math.abs( errorsRand[i]);
                variance[2]+= errorsRand[i]* errorsRand[i];

                i++;
                count++;
                if(count%(total/20)==0){
                    System.out.println(new DecimalFormat("##.##").format(((double) (count * 100)) / total) + "%");
                }
            }
        }
        if(count!=0){
            for(int j=0;j<3;j++) {
                avg[j] = avg[j] / count;
                variance[j] = Math.sqrt(variance[j] / count - avg[j] * avg[j]);
            }
        }
        //System.out.println(count);
        MathUtil.calcHistogramArray(errorsLens, errorsKev, errorsRand, 20, "lenskit.csv");

        System.out.println("Lenskit: "+avg[0]+" Std: "+variance[0]);
        System.out.println("Kevoree: "+avg[1]+" Std: "+variance[1]);
        System.out.println("Random: "+avg[2]+" Std: "+variance[2]);
        return avg[0];
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

        config.bind(EventDAO.class).to(new SimpleFileRatingDAO(new File(dir+"ratings.csv"), ","));

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
            testErr(kevoree,rec);

        } catch (RecommenderBuildException e) {
            e.printStackTrace();
        }



    }
}
