package org.kevoree.brain.smartgrid.newexperiments;

import mikera.util.Rand;
import org.kevoree.brain.smartgrid.util.CsvLoader;
import org.kevoree.brain.smartgrid.util.ElectricMeasure;
import org.kevoree.brain.smartgrid.util.TemperatureLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by assaad on 09/02/16.
 */
public class Executor {
    public static void main (String[] args){
        //Load all electric consumptions
        String electricdir = "/Users/assaad/work/github/data/consumption/csv";

        String kmeandir = "/Users/assaad/work/github/data/consumption/kmean/";
        HashMap<String, TreeMap<Long,ElectricMeasure>> rawdata = CsvLoader.load(electricdir);

        //Load all temperature measurements
        String temperaturedir="/Users/assaad/work/github/data/Temperature/ELLX.txt";
        TreeMap<Long,Double> temperatureDb = TemperatureLoader.load(temperaturedir);

        HashMap<String,User> users = new HashMap<String, User>();

        long starttime=System.nanoTime();
        for(String k: rawdata.keySet()){
            User temp= new User(k);
            users.put(k,temp);
            TreeMap<Long,ElectricMeasure> timeserie= rawdata.get(k);
            for(Map.Entry<Long,ElectricMeasure> entry: timeserie.entrySet()){
                temp.insert(entry.getValue(),temperatureDb);
            }
        }
        long endtime=System.nanoTime();
        double restime = (endtime-starttime)/1000000;
        System.out.println("Training completed in "+restime+" ms!");




      /*  int count=0;
        Random random=new Random();
        for(String k: rawdata.keySet()) {
            User temp =  users.get(k);
            int x= random.nextInt(96);
            temp.predict(x);
            count++;
            if(count==10){
                break;
            }
        }*/


        /*
        int timestep=96;
        int features=4;

        //Train profilers
        long starttime=System.nanoTime();
        HashMap<String,Profiler> profilers=new HashMap<String, Profiler>();
        for(String k: smartmeters.keySet()){
            Profiler temp=new Profiler(timestep,features);
            profilers.put(k,temp);
            temp.setUserId(k);
            temp.feed(smartmeters.get(k));
        }
        long endtime=System.nanoTime();
        double restime = (endtime-starttime)/1000000;
        System.out.println("Training profilers is completed in "+restime+" ms!");

        //Export profilers to csv

        String csvdir= "/Users/assaad/work/github/data/consumption/csvprofiles/";
        starttime=System.nanoTime();
        try {
            PrintWriter out = new PrintWriter(new File(csvdir + "profiles.csv"));
            out.println(new Profiler(timestep,features).getHeader(4));
            for (String p : profilers.keySet()) {
                out.println(profilers.get(p).export(4));
            }
            out.flush();
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        endtime=System.nanoTime();
        restime = (endtime-starttime)/1000000;
        System.out.println("Exporting profilers is completed in "+restime+" ms!");


        starttime=System.nanoTime();
        double[][] profileArrays = new double[numOfUser][];
        int count=0;
        String[] names=new String[numOfUser];
        for(String k: profilers.keySet()) {
            Profiler temp = profilers.get(k);
            profileArrays[count]=temp.getFingerprint();
            names[count]=k;
            count++;
        }

        double[][] pearsons = new double[numOfUser][numOfUser];

        for(int i=0;i<numOfUser;i++){
            for(int j=0;j<numOfUser;j++){
                pearsons[i][j]=calculatePearson(profileArrays[i],profileArrays[j]);
            }
        }

        for(int i=0;i<numOfUser-2;i++){
            double max=-2;
            int pos=0;
            for(int j=i+1;j<numOfUser;j++){
                if(pearsons[j][i]>max){
                    max=pearsons[j][i];
                    pos=j;
                }
            }
            //swap i+1 with pos
            double[] temp= pearsons[i+1];
            pearsons[i+1]=pearsons[pos];
            pearsons[pos]=temp;

            for(int k=0;k<numOfUser;k++){
                double t=pearsons[k][i+1];
                pearsons[k][i+1]=pearsons[k][pos];
                pearsons[k][pos]=t;
            }

            String s= names[pos];
            names[pos]=names[i+1];
            names[i+1]=s;
        }

        try {
            PrintWriter out = new PrintWriter(new File(csvdir + "pearsons.csv"));

            out.print(" ,");
            for(int i=0;i<numOfUser;i++){
                out.print(names[i]+",");
            }
            out.println();
            for(int i=0;i<numOfUser;i++){
                out.print(names[i]+",");
                for(int j=0;j<numOfUser;j++){
                    out.print(pearsons[i][j]+",");
                }
                out.println();
            }
            out.flush();
            out.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        saveImage(pearsons,10,csvdir);
        endtime=System.nanoTime();
        restime = (endtime-starttime)/1000000;
        System.out.println("Exporting pearsons is completed in "+restime+" ms!");*/

    }

    private static double calculatePearson(double[] x, double[] y) {
        int n=x.length;
        double sumx=0;
        double sumy=0;
        double sumxx=0;
        double sumyy=0;
        double sumxy=0;

        for(int i=0;i<n;i++){
            sumx+=x[i];
            sumy+=y[i];
            sumxx+=x[i]*x[i];
            sumyy+=y[i]*y[i];
            sumxy+=x[i]*y[i];
        }
        double pearson = (n*sumxy-(sumx*sumy))/((Math.sqrt(n*sumxx-sumx*sumx))*(Math.sqrt(n*sumyy-sumy*sumy)));
        return pearson;
    }

    private static void saveImage(double[][] correlations, int zoom, String dir){
        BufferedImage bi = new BufferedImage(correlations.length*zoom,correlations.length*zoom,BufferedImage.TYPE_INT_RGB);

        for(int i=0;i<correlations.length;i++){
            for(int j=0;j<correlations.length;j++){
                int c=(int)((correlations[i][j]+1)/2*255); //to get between 0 and 255
                Color col =new Color(c,c,c);
                for(int k=0;k<zoom;k++){
                    for(int l=0;l<zoom;l++){
                        bi.setRGB(i*zoom+k,j*zoom+l,col.getRGB());
                    }
                }
            }
        }

        try {
            File outputfile = new File(dir+"result.png");
            ImageIO.write(bi, "png", outputfile);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
