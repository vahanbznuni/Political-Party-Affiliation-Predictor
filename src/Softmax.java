public class Softmax {

    /*
     * Compute max-adjusted exponential of each item in matrix new matrix
     */
    public static double[][] getExpMatrix(double[][] input) {
        double[][] output = new double[input.length][input[0].length];

        for (int i=0; i<input.length; i++) {
            double[] row = input[i];
            double rowMax = Double.NEGATIVE_INFINITY;
            
            // Determine row max
            for (int j=0; j<row.length; j++) {
                double current = row[j];
                if (current > rowMax) {
                    rowMax = current;
                }
            }

            for (int j=0; j<row.length; j++) {
                double current = row[j];
                output[i][j] = Math.exp(current-rowMax); // For stabilization
            }
        }

        return output;
    }

    /*
     * Return matrix with softmax applied
     */
    public static double[][] apply(double[][] input) {
        double[][] output = new double[input.length][input[0].length];
        double[][] expMatrix = getExpMatrix(input);        

        for (int i=0; i<expMatrix.length; i++) {
            double[] row = expMatrix[i];
            double rowSum = 0;

            // Calculate row total
            for (int j=0; j<row.length; j++) {
                rowSum += row[j];
            }

            // Compute softmax values
            for (int j=0; j<row.length; j++) {
                double current = row[j];
                output[i][j] = current / rowSum;
            }
        }

        return output;
    }


}
