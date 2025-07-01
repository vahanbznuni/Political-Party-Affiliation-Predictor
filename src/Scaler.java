public class Scaler {

    /*
     * Wrapper for storing parameters of a Normal Distribution
     */
    public static class NormalDistParams {
        private final double[] meanVector;
        private final double[] stdDevVector;

        public NormalDistParams(double[] meanVector, double[] stdDevVector) {
            this.meanVector = meanVector;
            this.stdDevVector = stdDevVector;
        }

        public double[] getMeanVector() {
            return meanVector;
        }

        public double[] getStdDevVector() {
            return stdDevVector;
        }

    }

    /*
     * Compute normal distribution parameters of the columns of the input matrix
     */
    public static Scaler.NormalDistParams computeNormalDistParams(double[][] data) {
        int n = data[0].length;
        double[] meansVector = new double[n];
        double[] stdDevsVector = new double[n];
        for (int j=0; j<data[0].length; j++) {
            double mu = 0;
            double sigma = 0;
            double sigmaSquared = 0;
            for (int i=0; i<data.length; i++) {
                mu += data[i][j];
            }
            mu /= data.length;
            meansVector[j] = mu;

            for (int i=0; i<data.length; i++) {
                sigmaSquared += Math.pow((mu - data[i][j]), 2);
            }
            sigmaSquared /= data.length-1; // bias adjustment
            sigma = Math.sqrt(sigmaSquared);
            stdDevsVector[j] = sigma;
        }

        return new NormalDistParams(meansVector, stdDevsVector);
    }

    /*
     * return 2D array rescaled to standard normal
     */
    public static double[][] toNormalizedMatrix(double[][] data, Scaler.NormalDistParams params) {
        int m = data.length;
        int n = data[0].length;
        double[][] outputMatrix = new double[m][n];
        for (int j=0; j<data[0].length; j++) {
            for (int i=0; i<data.length; i++) {
                double mu = params.getMeanVector()[j];
                double sigma = params.getStdDevVector()[j];
                outputMatrix[i][j] = (data[i][j] - mu) / (sigma == 0 ? 1 : sigma);
            }
        }

        return outputMatrix;
    }

}
