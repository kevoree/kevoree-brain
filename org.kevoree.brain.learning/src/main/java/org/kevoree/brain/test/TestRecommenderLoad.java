package org.kevoree.brain.test;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLUInt8;
import org.kevoree.brain.learning.livelearning.Recommender.PredictedRating;
import org.kevoree.brain.learning.livelearning.Recommender.Rating;
import org.kevoree.brain.learning.livelearning.Recommender.Recommender;
import org.kevoree.brain.learning.livelearning.Recommender.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by assaad on 19/01/15.
 */
public class TestRecommenderLoad {
    public static void main(String[] args) throws IOException {
        String basedir = "/Users/assaad/work/github/kevoree-brain/org.kevoree.brain.learning/src/main/java/org/kevoree/brain/test/data/";


        Recommender rec = Recommender.load(basedir+"Rec.txt");
        System.out.println("Average error "+rec.getAverageError());

        rec.displayPredictions();
        System.out.println("done");
        ArrayList<PredictedRating> pr =rec.recommend(0);
    }
}
