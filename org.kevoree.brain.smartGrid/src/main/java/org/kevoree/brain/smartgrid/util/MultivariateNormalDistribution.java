package org.kevoree.brain.smartgrid.util;

import org.apache.commons.math3.util.FastMath;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * Created by assaad on 17/09/15.
 */
public class MultivariateNormalDistribution {
    double[] avg;

    DenseMatrix64F inv;
    double det;
    DenseMatrix64F conv;
    SolvePseudoInverseSvd solvePseudoInverseSvd;

    public MultivariateNormalDistribution(double[] avg, double[][] cov){
        this.avg=avg;
        conv= new DenseMatrix64F(cov);
        inv=new DenseMatrix64F(cov.length,cov.length);

        solvePseudoInverseSvd=new SolvePseudoInverseSvd(cov.length,cov.length);
        solvePseudoInverseSvd.setA(conv);
        solvePseudoInverseSvd.invert(inv);

        double[] r =solvePseudoInverseSvd.getDecomposition().getSingularValues();

        double d=1;
        for(int i=0;i<r.length;i++){
            if(r[i]!=0){
                d=r[i]*d;
            }
        }
        det= 1/d;

    }


    public double density(double[] features){
        return FastMath.pow(2 * FastMath.PI, -0.5 * features.length) *
                FastMath.pow(det, -0.5) * getExponentTerm(features);
    }

    private double getExponentTerm(double[] features) {
        DenseMatrix64F ft =new DenseMatrix64F(1,features.length);

        for(int i=0;i<features.length;i++){
            ft.set(0,i,features[i]-avg[i]);
        }

        DenseMatrix64F ft2=new DenseMatrix64F(1,features.length);
        CommonOps.mult(ft, inv, ft2);
        DenseMatrix64F ft3=new DenseMatrix64F(1,1);
        CommonOps.multTransB(1, ft2, ft, ft3);

        return FastMath.exp(-0.5 *ft3.get(0,0));
    }

}
