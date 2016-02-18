package org.kevoree.brain.smartgrid.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by assaad on 09/02/16.
 */
public class CsvLoader {

    public static ArrayList<ElectricMeasure> loadFile(String filename) throws Exception{
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<ElectricMeasure> ad=null;
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            ad = new ArrayList<ElectricMeasure>();
            while ((line = br.readLine()) != null) {
                String[] val = line.split(cvsSplitBy);
                ElectricMeasure em = new ElectricMeasure();
                em.setTime(Long.parseLong(val[0]));
                em.aplus = Double.parseDouble(val[1]);
                em.aminus = Double.parseDouble(val[2]);
                em.rplus = Double.parseDouble(val[3]);
                em.rminus = Double.parseDouble(val[4]);
                ad.add(em);
            }
            br.close();
        System.out.println("Loaded "+filename);
        return ad;
    }

    public static HashMap<String,TreeMap<Long,ElectricMeasure>> load(String directory){
        HashMap<String,TreeMap<Long,ElectricMeasure>> result = new HashMap<String,TreeMap<Long,ElectricMeasure>>();

        long starttime=System.nanoTime();

        int globaltotal=0;

        String line = "";
        String cvsSplitBy = ",";
        try {
            File dir = new File(directory);

            File[] directoryListing = dir.listFiles();
            //  System.out.println("Found " + directoryListing.length + " files");
            if (directoryListing != null) {
                for (File file : directoryListing) {

                    if(file.isDirectory()||file.getName().equals(".DS_Store")){
                        continue;
                    }
                    String user=file.getName().split("\\.")[0];

                    BufferedReader br = new BufferedReader(new FileReader(file));
                    TreeMap<Long,ElectricMeasure> ad = new TreeMap<Long,ElectricMeasure>();
                    result.put(user,ad);
                    while ((line = br.readLine()) != null) {
                        String[] val=line.split(cvsSplitBy);
                        ElectricMeasure em = new ElectricMeasure();
                        em.setTime(Long.parseLong(val[0]));
                        em.aplus=Double.parseDouble(val[1]);
                        em.aminus=Double.parseDouble(val[2]);
                        em.rplus =Double.parseDouble(val[3]);
                        em.rminus=Double.parseDouble(val[4]);
                        ad.put(em.getTime(),em);
                        globaltotal++;
                    }
                    br.close();



                    //  System.out.println("file "+file.getName()+" read "+rowNum);
                    //fileInputStream.close();
                }
            }
        }
        catch (Exception e)
        {
            //  System.out.println("Error in file: "+s);
            e.printStackTrace();
        }
        long endtime=System.nanoTime();
        double restime = (endtime-starttime)/1000000;
        System.out.println("Loaded "+globaltotal+" power records in "+restime+" ms !");




        return result;
    }
}
