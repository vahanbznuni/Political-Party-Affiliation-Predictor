import smile.classification.LogisticRegression;
import smile.validation.metric.Accuracy;


public class ModelTrainer {
    private static final int DEFAULT_NUM_FOLDS = 5;

    public static class TrainedModel {
        private final LogisticRegression.Multinomial model;
        private final LogisticRegression.Options options;
        private final double measuredAccuracy;

        public TrainedModel(LogisticRegression.Multinomial model, LogisticRegression.Options options, double measuredAccuracy) {
            this.model = model;
            this.options = options;
            this.measuredAccuracy = measuredAccuracy;
        }

        public LogisticRegression.Multinomial getModel() {
            return model;
        }

        public LogisticRegression.Options getOptions() {
            return options;
        }

        public double getMeasuredAccuracy() {
            return measuredAccuracy;
        }

    }

    public static TrainedModel getTrainedModel(double[][] data, int[] labels, LogisticRegression.Options options) {
        // Split data into training/validation (80%) and hold-out test (20%) sets       
        StratifiedDataSplitter dataSplitter = new StratifiedDataSplitter(data, labels, DEFAULT_NUM_FOLDS);
        double[][] trainingData = dataSplitter.getTrainSet().getData();
        int[] trainingLabels = dataSplitter.getTrainSet().getLabels();
        double[][] testData = dataSplitter.getTestSet().getData();
        int[] testLabels = dataSplitter.getTestSet().getLabels();
        
        LogisticRegression.Multinomial finalModel = LogisticRegression.multinomial(trainingData, trainingLabels, options);
        
        // Measure accuracy, package model into a TrainedModel wrapper, and return it
        int[] finalPrediction = finalModel.predict(testData);
        double finalAccuracy = Accuracy.of(testLabels, finalPrediction);

        return new ModelTrainer.TrainedModel(finalModel, options, finalAccuracy);
    }

    public static TrainedModel getTrainedModel(double[][] data, int[] labels) {
        // Split data into training/validation (80%) and hold-out test (20%) sets       
        StratifiedDataSplitter dataSplitter = new StratifiedDataSplitter(data, labels, DEFAULT_NUM_FOLDS);
        double[][] trainingData = dataSplitter.getTrainSet().getData();
        int[] trainingLabels = dataSplitter.getTrainSet().getLabels();

        // Find hyper-parameters through tuning
        LogisticRegression.Options finalOptions = getTunedOptions(trainingData, trainingLabels); // performs grid search and returns hyper-params

        // Call overloaded method to train and return final model using found hyperparameters
        return getTrainedModel(data, labels, finalOptions);

    }

    public static LogisticRegression.Options getTunedOptions(double[][] trainingData, int[] trainingLabels) {
        // Split data further into training and validation sets     
        int numberOfFolds = 5; 
        StratifiedDataSplitter dataSplitter = new StratifiedDataSplitter(trainingData, trainingLabels, numberOfFolds);
        double[][] trainingDataInternal = dataSplitter.getTrainSet().getData();
        int[] trainingLabelsInternal = dataSplitter.getTrainSet().getLabels();
        double[][] testDataInternal = dataSplitter.getTestSet().getData();
        int[] testLabelsInternal = dataSplitter.getTestSet().getLabels();
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
                       
                    LogisticRegression.Options options = new LogisticRegression.Options(lambda, tolerance, maxIter);
                    LogisticRegression.Multinomial model = LogisticRegression.multinomial(trainingDataInternal, trainingLabelsInternal, options);
                    int[] prediction = model.predict(testDataInternal);
                    double currentAccuracy = Accuracy.of(testLabelsInternal, prediction);
                    System.out.println("\nCurrent Accuracy: " + currentAccuracy);

                    if (currentAccuracy > bestAccuracy) {
                        bestAccuracy = currentAccuracy;
                        bestLambda = lambda;
                        bestTolerance = tolerance;
                        bestMaxIter = maxIter;
                    }
                }
            }
        }

        return new LogisticRegression.Options(bestLambda, bestTolerance, bestMaxIter);
    }



}