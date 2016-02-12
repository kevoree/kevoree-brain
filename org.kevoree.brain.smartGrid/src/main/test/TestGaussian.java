import org.apache.commons.math4.distribution.MultivariateNormalDistribution;
import org.junit.Assert;
import org.junit.Test;
import org.kevoree.brain.smartgrid.newexperiments.mixture.Gaussian;

/**
 * Created by assaad on 12/02/16.
 */
public class TestGaussian {
    protected final double[] longleyData = new double[]{
            60323, 83.0, 234289, 2356, 1590, 107608, 1947,
            61122, 88.5, 259426, 2325, 1456, 108632, 1948,
            60171, 88.2, 258054, 3682, 1616, 109773, 1949,
            61187, 89.5, 284599, 3351, 1650, 110929, 1950,
            63221, 96.2, 328975, 2099, 3099, 112075, 1951,
            63639, 98.1, 346999, 1932, 3594, 113270, 1952,
            64989, 99.0, 365385, 1870, 3547, 115094, 1953,
            63761, 100.0, 363112, 3578, 3350, 116219, 1954,
            66019, 101.2, 397469, 2904, 3048, 117388, 1955,
            67857, 104.6, 419180, 2822, 2857, 118734, 1956,
            68169, 108.4, 442769, 2936, 2798, 120445, 1957,
            66513, 110.8, 444546, 4681, 2637, 121950, 1958,
            68655, 112.6, 482704, 3813, 2552, 123366, 1959,
            69564, 114.2, 502601, 3931, 2514, 125368, 1960,
            69331, 115.7, 518173, 4806, 2572, 127852, 1961,
            70551, 116.9, 554894, 4007, 2827, 130081, 1962
    };

    protected final double[] rData = new double[]{
            12333921.73333333246, 3.679666000000000e+04, 343330206.333333313,
            1649102.666666666744, 1117681.066666666651, 23461965.733333334, 16240.93333333333248,
            36796.66000000000, 1.164576250000000e+02, 1063604.115416667,
            6258.666250000000, 3490.253750000000, 73503.000000000, 50.92333333333334,
            343330206.33333331347, 1.063604115416667e+06, 9879353659.329166412,
            56124369.854166664183, 30880428.345833335072, 685240944.600000024, 470977.90000000002328,
            1649102.66666666674, 6.258666250000000e+03, 56124369.854166664,
            873223.429166666698, -115378.762499999997, 4462741.533333333, 2973.03333333333330,
            1117681.06666666665, 3.490253750000000e+03, 30880428.345833335,
            -115378.762499999997, 484304.095833333326, 1764098.133333333, 1382.43333333333339,
            23461965.73333333433, 7.350300000000000e+04, 685240944.600000024,
            4462741.533333333209, 1764098.133333333302, 48387348.933333330, 32917.40000000000146,
            16240.93333333333, 5.092333333333334e+01, 470977.900000000,
            2973.033333333333, 1382.433333333333, 32917.40000000, 22.66666666666667
    };


    @Test
    public void testDegenaration(){
        Gaussian g=new Gaussian();
        double[] ex={1,0,0};
        double[] ex2={1.0,0,0};
        double[] ex3={1,0,0};

        g.feed(ex);
        g.feed(ex2);
        g.feed(ex3);

        double[] avg=g.getAvg();
        double[][] cov= g.getCovariance(avg);

        System.out.print("avg: ");
        for(int i=0;i<avg.length;i++){
            System.out.print(avg[i]+" , ");
        }
        System.out.println();
        System.out.println();
        System.out.println("Cov");

        for(int i=0;i<avg.length;i++){
            for(int j=0;j<avg.length;j++){
                System.out.print(cov[i][j]+" , ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();


      //  MultivariateNormalDistribution mpn = new MultivariateNormalDistribution(avg,cov);
       // System.out.println("Apache: "+mpn.density(avg));

        double[] p={0.9,0,0};

        double d= g.getProbability(avg,avg,cov);
        System.out.println(d);

        //System.out.println("Apache: "+mpn.density(p));
        double x=g.getProbability(p,avg,cov);

        System.out.println(x);
        System.out.println("ratio: "+x/d);
    }

    //@Test
    public void testGaussian() {
        Gaussian g = new Gaussian();


        double[][] train = new double[16][7];

        int k = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 7; j++) {
                train[i][j] = longleyData[k];
                k++;
            }
            g.feed(train[i]);

        }

        double[][] cov = g.getCovariance(g.getAvg());

        k = 0;

        double eps = 1e-6;
        for (int i = 0; i < cov.length; i++) {
            for (int j = 0; j < cov.length; j++) {
                Assert.assertEquals(cov[i][j], rData[k], eps);
                k++;
            }
        }

    }
}

