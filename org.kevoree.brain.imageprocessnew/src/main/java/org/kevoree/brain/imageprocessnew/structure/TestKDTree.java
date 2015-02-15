package org.kevoree.brain.imageprocessnew.structure;



/**
 * Created by assaa_000 on 15/02/2015.
 */
public class TestKDTree {
    public static void main(String[] arg){
        try {
            KDTree ktest = new KDTree(2);
            double[] x = new double[2];
            String s = "Track";

            int id=0;
            for(int j=10; j<=90; j+=10){
                for(int i=10; i<=90;i+=10){
                    x[0]=i;
                    x[1]=j;
                    ktest.insertToTree(s+id,x);
                    id++;
                }
            }

            double[] range1=new double[2];
            double[] range2=new double[2];
            x[0]=15;
            x[1]=15;

            range1[0]=x[0]-9;
            range1[1]=x[1]-9;
            range2[0]=x[0]+9;
            range2[1]=x[1]+9;

            Object[] res;
           // res= ktest.range(range1,range2);
            res= ktest.nearest(x,2);
            for(Object o:res){
                System.out.println(o.toString());
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
