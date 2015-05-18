package org.kevoree.brain.smartgrid;

import java.io.*;

/**
 * Created by assaad on 08/05/15.
 */
public class thomas {
        public static void main(String[] arg){
            String dirloc= "/Users/assaad/Desktop/test/";
            String s="";

            int filecounter=0;
            int step=10;
            int[][] counters = new int[5][356/step+1];

            try {
                File dir = new File(dirloc);
                File[] directoryListing = dir.listFiles();
                System.out.println(directoryListing.length);

                if (directoryListing != null) {
                    for (File file : directoryListing) {

                        s = file.getName();
                        System.out.println(s);
                        if (file.getName().equals(".DS_Store")) {
                            continue;
                        }

                        BufferedReader br = new BufferedReader(new FileReader(dirloc+s));


                        String sCurrentLine;

                        while ((sCurrentLine = br.readLine()) != null) {
                           int x = Integer.parseInt(sCurrentLine);
                            counters[filecounter][x/step]++;
                        }



                        filecounter++;
                    }
                }

                PrintStream out = new PrintStream(new FileOutputStream(dirloc+"result.txt"));

                for(int i=0; i<counters[0].length;i++){
                    out.print(i+" ");
                    for(int j=0;j<5;j++){
                      out.print(counters[j][i]+" ");
                    }
                    out.println();
                }
                out.close();
            }
            catch (Exception ex){
                ex.printStackTrace();

            }

        }


}
