package org.kevoree.brain.test;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;
import org.kevoree.brain.learning.livelearning.Recommender.LearningVector;
import org.kevoree.brain.learning.livelearning.Recommender.Rating;
import org.kevoree.brain.learning.livelearning.Recommender.Recommender;
import org.kevoree.brain.learning.livelearning.Recommender.User;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;


/**
 * Created by assaad on 19/05/15.
 */
public class TestCiprMatrix {

    public static void main(String[] arg){

        //alpha,lambda,iterations,numFeatures


        String dir="/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.learning/src/main/resources/Movielens/1m/";

        String csvfile="movies.csv";
        String line = "";
        String cvsSplitBy = ",";

        Recommender recommender=new Recommender();
        recommender.setParameters(0.005, 0.001, 10, 50,10000);
        long starttime;
        long endtime;
        double result;



        starttime= System.nanoTime();
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {

                String[] vals = line.split(cvsSplitBy);
                recommender.addProduct(vals[0],vals[1]);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Loaded: "+recommender.getProducts().size()+" movies in "+result+" s");



          int total=21063128;
      //  int total=1000209;

        csvfile="ratings.csv";
        starttime= System.nanoTime();
        int counter=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dir + csvfile));
            while ((line = br.readLine()) != null) {
                String[] vals = line.split(cvsSplitBy);
                recommender.addRating(vals[0], vals[1], Double.parseDouble(vals[2]), Long.parseLong(vals[3]), false);
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
        recommender.displayStats();
        System.out.println("Model created in "+result+" s");
        //recommender.getAverageError();


        int maxuser=1000000;
        int maxmovie=30000;

        DenseMatrix ratingsMatrix = new DenseMatrix(Math.min(recommender.getUsers().size(), maxuser), Math.min(maxmovie,recommender.getProducts().size()));

        System.out.println("Matrix size "+recommender.getUsers().size()+" , "+recommender.getProducts().size() );





        starttime= System.nanoTime();
        int rating=0;
        for(String user: recommender.getUsers().keySet()){
            User u=recommender.getUsers().get(user);
            for(String product: u.getRatings().keySet() ){
                Rating r= u.getRatings().get(product);
                if(u.incrementalId<maxuser && r.getProduct().incrementalId<maxmovie) {

                    // System.out.println(uid+" , "+productid+ " , "+u.getRatings().get(product).getValue());
                    ratingsMatrix.set(u.incrementalId, r.getProduct().incrementalId, u.getRatings().get(product).getValue() - u.getLv().getAverage());

                    rating++;
                    if (rating % 500000 == 0) {
                        System.out.println(rating);
                    }
                }
            }
           // System.out.println(u.incrementalId);
        }
        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Matrix created in "+result+" s");
        save(ratingsMatrix,"Rating.txt");
        recommender=null;
        System.gc();


        starttime= System.nanoTime();
        try {
            SVD svdGoogle = new SVD(ratingsMatrix.numRows(),ratingsMatrix.numColumns());
            SVD s = svdGoogle.factor(ratingsMatrix);
            DenseMatrix U = s.getU();
            double[] S = s.getS();
            DenseMatrix Vt = s.getVt();

            save(U,"U.txt");
            save(S,"S.txt");
            save(Vt,"V.txt");


        } catch (NotConvergedException e) {
            e.printStackTrace();
        }

        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Matrix decomposed in "+result+" s");


    }


    public static void save(Recommender recommender){
        try {
            PrintWriter out = new PrintWriter(new FileWriter("movies.csv"));

            out.close();
            out = new PrintWriter(new FileWriter("ratings.csv"));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static void save(double[] a, String filename){
        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            out.println(a.length);
            for(int i=0 ;i<a.length; i++){
                out.print(a[i]+" ");
            }

            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void save(DenseMatrix a, String filename){
        try {
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            out.println(a.numRows() + " , " + a.numColumns());
            out.println(a.toString());
            out.close();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
