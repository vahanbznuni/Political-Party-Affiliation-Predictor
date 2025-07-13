class MetricsMultinomial {
    private final int[] trueLabels;
    private final int[] predictedLabels;
    private final int numClasses;
    private final int sampleSize;
    private final int[][] confusionMatrix;
    private final int[] support;


    /*
     * Enum for averaging options when compiting global metrics
     */
    public static enum Averaging {
        WEIGHTED,
    }


    public MetricsMultinomial(
        int[] trueLabels, 
        int[] predictedLabels,
        int numClasses
    ) {
        this.sampleSize = trueLabels.length;
        if (sampleSize != predictedLabels.length) {
            throw new IllegalArgumentException("Sizes do not match!");
        }
        this.trueLabels = trueLabels;
        this.predictedLabels = predictedLabels;
        this.numClasses = numClasses;
        this.confusionMatrix = computeConfusionMatrix();
        this.support = computeSupport();
    }


    /*
     * Return Confusion Matrix
     */
    private int[][] computeConfusionMatrix() {
        int[][] matrix = new int[numClasses][numClasses];
        for (int i=0; i<sampleSize; i++) {
            int actual = trueLabels[i];
            int predicted = predictedLabels[i];
            matrix[actual][predicted]++;
        }

        return matrix;
    }

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }


    /* 
     * Return True Positive (TP) count for given class
     */
    public int getClassTP(int classID) {
        // Return CM index that holds number of predictions where True=Predicted
        return confusionMatrix[classID][classID];
    }


    /* 
     * Return False Positive (FP) count for given class
     */
    public int getClassFP(int classID) {
        int count = 0;
        // Accumulate values where given class was falsely predicted
        for (int i = 0; i < numClasses; i++) {
            if (i == classID) {
                continue;
            }
            count += confusionMatrix[i][classID];
        }
        return count;
    }


    /* 
     * Return True Negative (TN) count for given class
     */   
    public int getClassTN(int classID) {
        int count = 0;
        // Accumulate all values where given class was correctly not selected
        for (int i=0; i<numClasses; i++) {
            for (int j=0; j<numClasses; j++) {
                if (i == classID || j == classID) {
                    continue;
                }
                count += confusionMatrix[i][j];
            }
        }

        return count;
    }


    /* 
     * Return False Negativve (FN) count for given class
     */
    public int getClassFN(int classID) {
        int count = 0;
        // Accumulate all values where given class was incorrectly missed
        for (int j=0; j<numClasses; j++) {
            if (j == classID) {
                continue;
            }
            count += confusionMatrix[classID][j];
        }

        return count;
    }

    
    /*
     * Return array where each index holds the number of true labels corresponding
     * to the class who's ID is that index
     */
    private int[] computeSupport() {
        int[] support = new int[numClasses]; 
        for (int i=0; i<sampleSize; i++) {
            support[trueLabels[i]]++;
        }

        return support;
    }


    /*
     * Return ratio of correct predictions to total sample size
     */
    public double getAccuracy() {
        int correctPredictions = 0;
        for (int i=0; i<numClasses; i++) {
            correctPredictions += getClassTP(i);
        }

        return (double)correctPredictions / sampleSize;
    }


    /*
     * Return ratio of TP / (TP + FP) for given class
     */
    public double getClassPrecision(int classID) {
        double TP = getClassTP(classID);
        double FP = getClassFP(classID);
        return TP / (TP + FP);
    }


    /*
     * Return ratio of TP / (TP + FN) for given class
     */
    public double getClassRecall(int classID) {
        double TP = getClassTP(classID);
        double FN = getClassFN(classID);
        return TP / (TP + FN);
    }


    /*
     * Return harmonic mean of precision and recall for given class
     */
    public double getClassF1Score(int classID) {
        double precision = getClassPrecision(classID);
        double recall = getClassRecall(classID);

        // Guard against 0-denominaro. Note: this is by convenion; 
        // Mathematically F1 is undefined
        if (precision + recall == 0) {
            System.out.println("\n\n**UNDEFINED F1 (ZERO DEM): RETURNING 0**\n\n");
            return 0;
        }

        return 2 * (precision * recall) / (precision + recall);
    }

    
    /*
     * Return multi-class -- using provided averaging option
     * **Note**: Currently only WEIGHTED averaging is implemented
     */
    public double getPrecision(Averaging averaging) {
        switch (averaging) {
            case Averaging.WEIGHTED:
                double count = 0;
                for (int i=0; i<numClasses; i++) {
                    // Weight metric by total number of elements of this class (aka)
                    count += getClassPrecision(i) * support[i];
                }
                return count / sampleSize;
            default:
                // Guard against unexpected / uninmplemented Averaging option arguments
                throw new IllegalArgumentException("Not Implemented");
        }
    }
    
    
    /*
     * Return multi-class -- using provided averaging option
     * **Note**: Currently only WEIGHTED averaging is implemented
     */
    public double getRecall(Averaging averaging) {
        switch (averaging) {
            case Averaging.WEIGHTED:
                double count = 0;
                for (int i=0; i<numClasses; i++) {
                    // Weight metric by total number of elements of this class (aka)
                    count += getClassRecall(i) * support[i];
                }
                return count / sampleSize;
            default:
                // Guard against unexpected / uninmplemented Averaging option arguments
                throw new IllegalArgumentException("Not Implemented");
        }
    }
    
    
    /*
     * Return multi-class -- using provided averaging option
     * **Note**: Currently only WEIGHTED averaging is implemented
     */
    public double getF1Score(Averaging averaging) {
        switch (averaging) {
            case Averaging.WEIGHTED:
                double count = 0;
                for (int i=0; i<numClasses; i++) {
                    // Weight metric by total number of elements of this class (aka)
                    count += getClassF1Score(i) * support[i];
                }
                return count / sampleSize;
            default:
                // Guard against unexpected / uninmplemented Averaging option arguments
                throw new IllegalArgumentException("Not Implemented");
        }
    }




}