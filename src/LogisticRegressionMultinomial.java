/*
 * Multinomial (softmax) logistic regression model
 */
class LogisticRegressionMultinomial {
        private static final double DEFAULT_LAMBDA = 1e-4;
        private static final double DEFAULT_TOLERANCE = 1e-4;
        private static final int DEFAULT_MAX_ITER = 300;
        private static final double LEARNING_RATE = 0.01;
        private double[][] trainingData;
        private int[] trainingLabels;
        private int numClasses;
        private int numExamples;
        private int numFeatures;
        private Options options;
        private double[][] weights;
        private double[] biases;

    
    public LogisticRegressionMultinomial(
        double[][] trainingData,
        int[] trainingLabels,
        int numClasses,
        Options options
    ) {
        this.options = options;
        this.numClasses = numClasses;
        this.numExamples = trainingData.length;
        this.numFeatures = trainingData[0].length;
        this.trainingData = trainingData;
        this.trainingLabels = trainingLabels;
        this.weights = new double[numClasses][numFeatures];
        this.biases = new double[numClasses];
        performGradientDescent();
    }


    public LogisticRegressionMultinomial(
        double[][] trainingData,
        int[] trainingLabels,
        int numClasses
    ) {
        this(
            trainingData,
            trainingLabels,
            numClasses,
            new Options(DEFAULT_LAMBDA, DEFAULT_TOLERANCE, DEFAULT_MAX_ITER)
        );
    }


    /*
     * Wrapper static class to hold options hyperparameters
     */
    public static class Options {
        private final double lambda;
        private final double tolerance;
        private final int maxIter;

        public Options(double lambda, double tolerance, int maxIter) {
            this.lambda = lambda;
            this.tolerance = tolerance;
            this.maxIter = maxIter;
        }

        public double getLambda() {
            return lambda;
        }

        public double getTolerance() {
            return tolerance;
        }

        public int getMaxIter() {
            return maxIter;
        }
    }


    /*
     * Return logit scores for input vector
     */
    private double[] scoreOf(double[] vector) {
        return MatrixMath.add(
            MatrixMath.multiply(weights, vector),
            biases
        );
    }


    /*
     * Returns matrix of scores: XW^T + b-broadcast
     */
    private double[][] getScores() {
        double[][] scores = MatrixMath.multiply(trainingData, MatrixMath.transpose(weights));
        for (int i=0; i<scores.length; i++) {
            scores[i] = MatrixMath.add(scores[i], biases);
        }

        return scores;
    }


    /*
     * Return matrix predicting class probabilities
     */
    private double[][] getProbabilities() {
        return Softmax.apply(getScores());
    }


    /*
     * Return matrix containing Ŷ − oneHot(Y)
     */
    private double[][] getDelta() {
        return MatrixMath.add(
            getProbabilities(),
            MatrixMath.scalarMultiply(
                -1, 
                MatrixMath.oneHotToDouble(trainingLabels, numClasses)
            )
        );
    }

    /*
     * Return gradient of the cross-entropy loss function w.r.t. the weights
     */
    private double[][] getWeightsGradient() {
        double[][] rawWeightsGradient = MatrixMath.scalarMultiply(
            1.0/numExamples, 
            MatrixMath.multiply(
                MatrixMath.transpose(getDelta()), 
                trainingData
            )
        );

        double[][] regularization = MatrixMath.scalarMultiply(
            options.getLambda() / numExamples, 
            weights
        );

        double[][] output = MatrixMath.add(rawWeightsGradient, regularization);

        return output;
    }


    /*
     * Return gradient of the cross-entropy loss function w.r.t. the biases
     */
    private double[] getBiasesGradient() {
        double[] gradient = new double[numClasses];
        double[][] delta = getDelta();

        // Accumulate row sums
        for (int i=0; i<numExamples; i++) {
            for (int k=0; k<numClasses; k++) {
                gradient[k] += delta[i][k];
            }
        }

        // Take the average of the sums at each index
        double scalar = 1.0 / numExamples;
        for (int k=0; k<numClasses; k++) {
            gradient[k] *= scalar;
        }

        return gradient;
    }

    /*
     * Check for convergence: stopping condition for Gradient Descent
     */
    private boolean hasConverged(
        double[][] weightsGradient,
        double[] biasesGradient
    ) {
        double max = 0;
        for (double[] row : weightsGradient) {
            for (double val : row) {
                if (Math.abs(val) > max) {
                    max = val;
                }
            }
        }

        for (double val : biasesGradient) {
            if (Math.abs(val) > max) {
                max = val;
            }
        }

        return max < options.getTolerance();
    }


    /*
     * Batch Gradient Descent algorithm
     */
    private void performGradientDescent() {
        for (int i=0; i<options.getMaxIter(); i++) {
            // Get gradients for weights and biases
            double[][] weightsGradient = getWeightsGradient();
            double[] biasesGradient = getBiasesGradient();
            
            // Update weights
            weights = MatrixMath.add(
                weights,
                MatrixMath.scalarMultiply(-LEARNING_RATE, weightsGradient)
            );
            // Update biases
            biases = MatrixMath.add(
                biases,
                MatrixMath.scalarMultiply(-LEARNING_RATE, biasesGradient)
            );

            // Check for stopping condition
            if (hasConverged(weightsGradient, biasesGradient)) {
                return;
            }
        }
    }


    /*
     * Return integer representing class predicted by the model, given input vector x
     */
    public int predict(double[] x) {
        return MatrixMath.argMax(scoreOf(x));
    }


    /*
     * Return an array of integers representing classes predicted by the model, 
     * given an input matrix X
     */
    public int[] predict(double[][] X) {
        int[] output = new int[X.length];
        for (int i=0; i<X.length; i++) {
            output[i] = MatrixMath.argMax(scoreOf(X[i]));
        }

        return output;
    }

}