package org.kevoree.brain.test;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;
import org.la4j.Matrix;
import org.la4j.decomposition.SingularValueDecompositor;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;


/**
 * Created by assaad on 19/05/15.
 */
public class TestSparse {

    public static void main(String[] arg){
        int i=100000;
        int j=20000;
        long starttime;
        long endtime;
        double result;

        DenseMatrix matA = new DenseMatrix(i,j);

        Random rand = new Random();
        double d=0;

        starttime= System.nanoTime();
        for(int x=0;x<i;x+=100){
            for(int y=0;y<j;y+=200){
                d=rand.nextDouble()*5;
                matA.set(x, y, d);
            }
        }
        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Matrix decomposed in "+result+" s");


     /*   starttime= System.nanoTime();
        try {
            SVD svdGoogle = new SVD(matA.numRows(),matA.numColumns());
            SVD s = svdGoogle.factor(matA);
            DenseMatrix U = s.getU();
            double[] S = s.getS();
            DenseMatrix Vt = s.getVt();
            System.out.println(U.toString());
            System.out.println(S.length);
            System.out.println(Vt.toString());

            save(U,"U.txt");
            save(S,"s.txt");
            save(Vt,"V.txt");
        } catch (NotConvergedException e) {
            e.printStackTrace();
        }

        endtime= System.nanoTime();
        result= ((double)(endtime-starttime))/(1000000000);
        System.out.println("Matrix decomposed in "+result+" s");*/
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

