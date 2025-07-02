/*
 * Custom weights transform
 */
public class Weighter {

    /*
     * return weighted matrix using domain-specific feature weighting
     */
    public static double[][] toWeightedMatrix(double[][] data) {
        int m = data.length;
        int n = data[0].length;
        double[][] outputMatrix = new double[m][n];
        for (int i=0; i<m; i++) {
            for (int j=0; j<n; j++) {
                outputMatrix[i][j] = data[i][j];
                if ((j >= 3 && j <= 11)) {
                    outputMatrix[i][j] *= 1000;
                }
            }
        }
        
        return outputMatrix;
    }

    
}
