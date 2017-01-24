package org.kevoree.brain.learning;

/**
 * Created by assaad on 21/10/2016.
 */
public class GaussianProfile {
    private double[] _min;
    private double[] _max;
    private double[] _sum;
    private double[] _sumsq;
    private int total;

    public double[] getMin() {
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            return _sum.clone();
        } else {
            return _min.clone();
        }
    }

    public double[] getMax() {
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            return _sum.clone();
        } else {
            return _max.clone();
        }
    }

    public double[] getAvg() {
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            return _sum.clone();
        } else {
            double[] avg = _sum.clone();
            for (int i = 0; i < avg.length; i++) {
                avg[i] = avg[i] / total;
            }
            return avg;
        }
    }

    public double[][] getCovariance(double[] avg) {
        int features = avg.length;

        if (total == 0) {
            return null;
        }
        if (total > 1) {
            double[][] covariances = new double[features][features];

            double correction = total;
            correction = correction / (total - 1);

            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    covariances[i][j] = (_sumsq[count] / total - avg[i] * avg[j]) * correction;
                    covariances[j][i] = covariances[i][j];
                    count++;
                }
            }
            return covariances;
        } else {
            return null;
        }
    }

    public void learn(double[] values) {
        int features = values.length;

        //Create dirac
        if (total == 0) {
            _sum = new double[features];
            System.arraycopy(values, 0, _sum, 0, features);
            total = 1;
        } else {
            //Upgrade dirac to gaussian
            if (total == 1) {
                //Create getMin, getMax, sumsquares
                _min = new double[features];
                _max = new double[features];
                System.arraycopy(_sum, 0, _min, 0, features);
                System.arraycopy(_sum, 0, _max, 0, features);
                _sumsq = new double[features * (features + 1) / 2];
                int count = 0;
                for (int i = 0; i < features; i++) {
                    for (int j = i; j < features; j++) {
                        _sumsq[count] = _sum[i] * _sum[j];
                        count++;
                    }
                }
            }

            //Update the values
            for (int i = 0; i < features; i++) {
                if (values[i] < _min[i]) {
                    _min[i] = values[i];
                }

                if (values[i] > _max[i]) {
                    _max[i] = values[i];
                }
                _sum[i] += values[i];
            }

            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    _sumsq[count] += values[i] * values[j];
                    count++;
                }
            }
            total++;
        }
    }

    public double[] getSigma(double[] avg) {
        double[][] cov = getCovariance(avg);
        double[] res = new double[avg.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = Math.sqrt(cov[i][i]);
        }
        return res;
    }

    public double[] normalize(double[] input, double[] avg, double[] sigma) {
        double[] res = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            if (sigma[i] != 0) {
                res[i] = (input[i] - avg[i]) / sigma[i];
            } else {
                res[i] = 0;
            }
        }

        return res;
    }


    public double[] normalizeMinMax(double[] input) {
        double[] res = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            if (_max[i] != _min[i]) {
                res[i] = (input[i] - _min[i]) / (_max[i] - _min[i]);
            } else {
                res[i] = 0;
            }
        }
        return res;
    }

    public double[] inverseNormalizeMinMax(double[] input) {
        double[] res = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            res[i] = (input[i])*(_max[i] - _min[i])+_min[i];
        }
        return res;
    }

    public double[] inverseNormalize(double[] input, double[] avg, double[] sigma) {
        double[] res = new double[input.length];

        for (int i = 0; i < input.length; i++) {
            if (sigma[i] != 0) {
                res[i] = input[i] * sigma[i] + avg[i];
            } else {
                res[i] = avg[i];
            }
        }

        return res;
    }


    public static double[] predict(double[] output, double prediction, double err) {
        double[] result = new double[output.length];
        double total = 0;

        double d = 1 / Math.sqrt(2 * err * err * Math.PI);
        for (int i = 0; i < output.length; i++) {
            result[i] = d * Math.exp(-((output[i] - prediction) * (output[i] - prediction)) / (2 * err * err));
            total += result[i];
        }

        for (int i = 0; i < output.length; i++) {
            result[i] = result[i] / total;
        }

        return result;
    }
}
