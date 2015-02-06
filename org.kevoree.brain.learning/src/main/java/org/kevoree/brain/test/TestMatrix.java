package org.kevoree.brain.test;



import org.ejml.alg.dense.linsol.qr.AdjLinearSolverQr;
import org.ejml.alg.dense.linsol.qr.SolvePseudoInverseQrp;
import org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import java.util.Random;

/**
 * Created by assaad on 27/01/15.
 */
public class TestMatrix {
    public static void main(String[] arg){
        /*int size =8;
        DenseMatrix64F test = new DenseMatrix64F(size, size);

        test.set(0,0,1);
        test.set(0,1,1);
        test.set(1,2,1);
        test.set(1,3,1);
        test.set(2,4,1);
        test.set(2,5,1);
        test.set(3,6,1);
        test.set(3,7,1);
        test.set(4,1,1);
        test.set(4,2,1);
        test.set(4,4,1);
        test.set(5,3,1);
        test.set(5,5,1);
        test.set(5,6,1);
        test.set(6,7,1);
        test.set(7,2,1);
        test.set(7,4,-1);

        DenseMatrix64F res= new DenseMatrix64F(size,1);
        res.set(0,0,10);
        res.set(1,0,30);
        res.set(2,0,50);
        res.set(3,0,100);

        CommonOps.invert(test);
        DenseMatrix64F var = new DenseMatrix64F(size,1);
        CommonOps.mult(test,res,var);

        var.print();*/

        Random rand = new Random();

        DenseMatrix64F A = new DenseMatrix64F(5, 4);
        for(int i=0; i<5;i++){
            for(int j=0;j<4;j++){
                A.set(i,j,rand.nextDouble()*10-5);
            }
        }
        A.print();

        DenseMatrix64F X= new DenseMatrix64F(4, 2);
        X.print();

        DenseMatrix64F Y = new DenseMatrix64F(5, 2);
        for(int i=0; i<5;i++){
            for(int j=0;j<2;j++){
                Y.set(i, j, rand.nextDouble() * 10 - 5);
            }
        }
        Y.print();

        org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd solver = new SolvePseudoInverseSvd();

        solver.setA(A);
        solver.solve(Y,X);

        X.print();

        DenseMatrix64F temp = new DenseMatrix64F(5, 2);

        CommonOps.mult(A,X,temp);
        System.out.println("Val");
        temp.print();
        Y.print();





    }
}
