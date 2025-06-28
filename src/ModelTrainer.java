import smile.classification.LogisticRegression;
import smile.validation.Bag;
import smile.validation.CrossValidation;
import smile.validation.metric.Accuracy;
import smile.math.MathEx;


public class ModelTrainer {

    public static LogisticRegression.Multinomial getTrainedModel(double[][] data, int[] labels) {
        // EXTERNAL SPLIT------------------------------------------------------
        // Split data into training/validation (80%) and hold-out test (20%) sets       
        // Get stratified indices for the split
        int numberOfFolds = 5;
        Bag[] bags = CrossValidation.stratify(labels, numberOfFolds);
        Bag split = bags[0];
        int[] trainingIndices = split.samples(); // indices of training samples
        int[] testingIndices = split.oob(); // indices of holdout, aka out-of-bag (oob) test set
        
        // Setup data using the indices of the split
        double[][] trainingData = MathEx.slice(data, trainingIndices);
        int[] trainingLabels = MathEx.slice(labels, trainingIndices);
        double[][] testData = MathEx.slice(data, testingIndices);
        int[] testLabels = MathEx.slice(labels, testingIndices);
        // ------------------------------------------------------


        // INTERNAL SPLIT------------------------------------------------------
        // Split training/validation data further into training and testing (i.e. validation) (another 80-20)  
        // Get stratified indices for the split
        int numberOfFoldsInternal = 5;
        Bag[] bagsInternal = CrossValidation.stratify(trainingLabels, numberOfFoldsInternal);
        // ------------------------------------------------------

        // Search space for hyper-parameters
        double[] lambdaRange = new double[]{1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1};
        double[] toleranceRange = new double[]{1e-4, 1e-5, 1e-6};
        int[] maxIterRange = new int[]{100, 300, 900};

        // Search (i.e. tuning)
        double bestAccuracy = Integer.MIN_VALUE;
        double bestLambda = 0;
        double bestTolerance = 0;
        int bestMaxIter = 0;
        for (double lambda : lambdaRange) {
            for (double tolerance : toleranceRange) {
                for (int maxIter : maxIterRange) {
                    double totalAccuracy = 0;
                    int numberOfInternalBags = bagsInternal.length;
                    
                    for (int i=0; i<numberOfInternalBags; i++) {
                        // ------------------------------------------------------
                        Bag splitInternal = bagsInternal[i];
                        int[] trainingIndicesInternal = splitInternal.samples(); // indices of internal training samples
                        int[] testingIndicesInternal = splitInternal.oob(); // indices of testing (validation)
                        
                        // Setup data using the indices of the internal split
                        double[][] trainingDataInternal = MathEx.slice(trainingData, trainingIndicesInternal);
                        int[] trainingLabelsInternal = MathEx.slice(trainingLabels, trainingIndicesInternal);
                        double[][] testDataInternal = MathEx.slice(trainingData, testingIndicesInternal);
                        int[] testLabelsInternal = MathEx.slice(trainingLabels, testingIndicesInternal);
                        // ------------------------------------------------------
                    
                        LogisticRegression.Options options = new LogisticRegression.Options(lambda, tolerance, maxIter);
                        LogisticRegression.Multinomial model = LogisticRegression.multinomial(trainingDataInternal, trainingLabelsInternal, options);
                        int[] prediction = model.predict(testDataInternal);
                        double currentAccuracy = Accuracy.of(testLabelsInternal, prediction);
                        totalAccuracy += currentAccuracy;
                    }

                    double meanCurrentAccuracy = totalAccuracy / numberOfInternalBags;
                    System.out.println("\nMean Current Accuracy: " + meanCurrentAccuracy);
                    if (meanCurrentAccuracy > bestAccuracy) {
                        bestAccuracy = meanCurrentAccuracy;
                        bestLambda = lambda;
                        bestTolerance = tolerance;
                        bestMaxIter = maxIter;
                    }
                }
            }
        }

        // Train final model using best found hyper-parameters
        LogisticRegression.Options finalOptions = new LogisticRegression.Options(bestLambda, bestTolerance, bestMaxIter);
        LogisticRegression.Multinomial finalModel = LogisticRegression.multinomial(trainingData, trainingLabels, finalOptions);
        
        // For Optional Use; Comment-out when not needed
        int[] finalPrediction = finalModel.predict(testData);
        double finalAccuracy = Accuracy.of(testLabels, finalPrediction);
        System.out.println("\nFinal Accuracy: " + finalAccuracy);

        return finalModel;

    }

}