package org.kevoree.brain.eurusd.learners;

import org.kevoree.brain.learning.livelearning.LinearRegressionLive;
import org.kevoree.brain.util.TimeStamp;

import java.util.TreeMap;

/**
 * Created by assaad on 06/02/15.
 */
public class LinearTraining {
    private TreeMap<Long, Double> eurUsd;
    private int size=10;
    private LinearRegressionLive trainer=new LinearRegressionLive(size);

    private Long initTimeStamp = TimeStamp.getTimeStamp(2000, 5, 30, 17, 27);
    private Long finalTimeStamp = TimeStamp.getTimeStamp(2015, 01, 31, 23, 59);


    private double alpha=0.0001;
    private int iteration =50;
    private int space=24*60*60*1000;

    public LinearTraining(Long initTimeStamp, Long finalTimeStamp, int size, double alpha, int iteration, int space,TreeMap<Long, Double> eurUsd){
        this.initTimeStamp=initTimeStamp;
        this.finalTimeStamp=finalTimeStamp;
        this.size=size;
        this.alpha=alpha;
        this.iteration=iteration;
        this.eurUsd=eurUsd;
        this.space=space;
    }

    public void  train(int times){
      try {


          trainer.setAlpha(alpha);
          trainer.setIteration(iteration);

          for (int j = 0; j < times; j++) {
              for (long i = initTimeStamp + (size + 1) * space; i < finalTimeStamp - space; i += space) {
                  double[] features = new double[size];
                  double res;

                  for (int k = 0; k < size; k++) {
                      features[k] = eurUsd.get(eurUsd.floorKey(i - (size - k) * space));
                  }
                  res = eurUsd.get(eurUsd.floorKey(i + space));
                  trainer.feed(features, res);
              }
          }
          //System.out.println("done");
      }
      catch (Exception ex){
          ex.printStackTrace();
      }
    }

    public double predict(long i){

        double[] features= new double[size];
        double res;

        for(int k=0;k<size;k++){
            features[k]=eurUsd.get(eurUsd.floorKey(i-(size-k)*space));
        }
        double pred=trainer.calculate(features);
        return pred;

    }


}
