public class _TestMatrixMath {

    public static void test2() {
        double[][] A = {
            {1, 2, 3},
            {4, 5, 6}
        };

        double[][] B = {
            {10, 11},
            {20, 21},
            {30, 31}
        };

        double[][] res = MatrixMath.multiply(A, B);

        for (double[] row : res) {
            for (double val : row) {
                System.out.print(val + "\t");
            }
            System.out.println();
        }
    }

    public static void test3() {
        double[][] A = {
            {4, 2, 4},
            {8, 3, 1}
        };

        double[][] B = {
            {3, 5},
            {2, 8},
            {7, 9}
        };

        double[][] res = MatrixMath.multiply(A, B);
        printMatrix(res);

    }

    public static void test4() {
        double[][] A = {
            {1, 2},
            {5, 6},
            {7, 8}
        };

        double[] B = {3, 4};

        double[] res1 = MatrixMath.multiply(A, B);

        printVector(res1);
    }

    public static void test5() {
        double[] A = {2, 7, 1};
        double[] B = {8, 2, 8};
        double out = MatrixMath.dot(A, B);
        System.out.println(out);

    }

    public static void test6() {
        double[][] matrix = {
            {1, 80, 97, 70},
            {2, 81, 95, 71},
            {3, 86, 89, 76},
            {4, 90, 90, 68}
        };

        double scalar = 4;

        double[][] output = MatrixMath.scalarMultiply(scalar, matrix);
        printMatrix(output);
    }

    public static void test7() {
        double[] vector = {1, 80, 97, 70};
        double scalar = 4;

        double[] output = MatrixMath.scalarMultiply(scalar, vector);
        printVector(output);
    }


    public static void test8() {
        double[][] matrix = {
            {1, 80, 97, 70},
            {2, 81, 95, 71},
            {3, 86, 89, 76},
            {4, 90, 90, 68}
        };

        double[][] output = MatrixMath.transpose(matrix);
        printMatrix(output);
    }


    public static void test9() {
        double[][] matrix = {
            {5, 4, 2},
            {4, 2, 8},
            {4, 4, 1},
        };

        double[][] output = Softmax.apply(matrix);
        printMatrix(output);
        System.out.println();
        for (double[] row : output) {
            double rowTotal = 0;
            for (double val : row) {
                rowTotal += val;
            }
            System.out.println(rowTotal);
        }
    }


    public static void test10() {
        
    }

    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double val : row) {
                System.out.print(val + "\t");
            }
            System.out.println();
        }
    }

    public static void printVector(double[] vector){
        for (double val : vector) {
            System.out.print(val + "\t");
        } 
        System.out.println();
    }

    public static void main(String[] args) {
        test2();
    }

    
}
