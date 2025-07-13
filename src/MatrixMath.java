/*
 * Utility class with static methods for basic linear algebra operations
 */
public class MatrixMath {

    /*
     * Matrix multiplication
     */
    public static double[][] multiply(double[][] matrixA, double[][] matrixB) {
        int heightA = matrixA.length;
        int heightB = matrixB.length;
        int widthA = matrixA[0].length;
        int widthB = matrixB[0].length;

        if (widthA != heightB) {
            throw new IllegalArgumentException("Mismatching size!");
        }

        double[][] outputMatrix = new double[heightA][widthB];
        for (int k=0; k<widthB; k++) {
            for (int i=0; i<heightA; i++) {
                for (int j=0; j<heightB; j++) {
                    outputMatrix[i][k] += matrixA[i][j] * matrixB[j][k];
                }
            }
        }

        return outputMatrix;
    }


    /*
     * Matrix vector multiplication
     */
    public static double[] multiply(double[][]matrixA, double[] vectorB) {
        int heightA = matrixA.length;
        int heightB = vectorB.length;
        int widthA = matrixA[0].length;

        if (widthA != heightB) {
            throw new IllegalArgumentException("Mismatching size!");
        }

        double[] outputVector = new double[heightA];
        for (int i=0; i<heightA; i++) {
            for (int j=0; j<heightB; j++) {
                outputVector[i] += matrixA[i][j] * vectorB[j];
            }
        }

        return outputVector;

    }


    /* 
     * Dot product
     */
    public static double dot(double[] vectorA, double[] vectorB) {
        int heightA = vectorA.length;
        int heightB = vectorB.length;

        if (heightA != heightB) {
            throw new IllegalArgumentException("Mismatching size!");
        }

        double sum = 0;
        for (int i=0; i<heightA; i++) {
            sum += vectorA[i] * vectorB[i];
        }

        return sum;
    }


    /*
     * Scalar Multiplication: scalar and vector
     */
    public static double[] scalarMultiply(double scalar, double[] vector) {
        int height = vector.length;
        double[] output = new double[height];
        for (int i=0; i<height; i++) {
            output[i] = scalar * vector[i];
        }

        return output;
    }


    /*
     * Scalar Multiplication: scalar and matrix
     */
    public static double[][] scalarMultiply(double scalar, double[][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        double[][] output = new double[height][width];
        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                output[i][j] = scalar * matrix[i][j];
            };
        }

        return output;
    }


    /*
     * Transpose matrix
     */
    public static double[][] transpose(double[][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        double[][] output = new double[width][height];
        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                output[j][i] = matrix[i][j];
            };
        }

        return output;
    }


    /*
     * Vector addition
     */
    public static double[] add(double[] vectorA, double[] vectorB) {
        int heightA = vectorA.length;
        int heightB = vectorB.length;
        if (heightA != heightB) {
            throw new IllegalArgumentException("Mismatching size!");
        }

        double[] output = new double[heightA];
        for (int i=0; i<heightA; i++) {
            output[i] = vectorA[i] + vectorB[i];
        }

        return output;
    }


    /*
     * Matrix addition
     */
    public static double[][] add(double[][] matrixA, double[][] matrixB) {
        int heightA = matrixA.length;
        int heightB = matrixB.length;
        int widthA = matrixA[0].length;
        int widthB = matrixB[0].length;
        if (heightA != heightB || widthA != widthB) {
            throw new IllegalArgumentException("Mismatching size!");
        }

        double[][] output = new double[heightA][widthA];
        for (int i=0; i<heightA; i++) {
            for (int j=0; j<widthA; j++) {
                output[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }

        return output;
    }


    /*
     * Return index the value at which is largest
     */
    public static int argMax(double[] vector) {
        int best = 0;
        for (int i=0; i<vector.length; i++) {
            if (vector[i] > vector[best]) {
                best = i;
            }
        }

        return best;
    }


    /*
     * Return one-hot matrix
     */
    public static int[][] oneHot(int[] vector, int numClasses) {
        int length = vector.length;
        int[][] output = new int[length][numClasses];
        for (int i=0; i<length; i++) {
            int element = vector[i];
            output[i][element] = 1;
        }

        return output;
    }

    /*
     * Return one-hot matrix
     */
    public static double[][] oneHotToDouble(int[] vector, int numClasses) {
        int length = vector.length;
        double[][] output = new double[length][numClasses];
        for (int i=0; i<length; i++) {
            int element = vector[i];
            output[i][element] = 1;
        }

        return output;
    }
    
}
