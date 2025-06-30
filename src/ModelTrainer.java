import smile.classification.LogisticRegression;
import smile.validation.metric.Accuracy;


public class ModelTrainer {
    private static final int DEFAULT_NUM_FOLDS = 5;
    private final double[][] masterData; // <-- Delete if ultimately not utilized
    private final int[] masterLabels; // <-- Delete if ultimately not utilized
    private final StratifiedDataSplitter masterDataSplitter;


    public ModelTrainer(double[][] masterData, int[] masterLabels) {
        this.masterData = masterData;
        this.masterLabels = masterLabels;
        // Split master data/labels into training/validation (80%) and hold-out test (20%) data sets       
        this.masterDataSplitter = new StratifiedDataSplitter(masterData, masterLabels, DEFAULT_NUM_FOLDS);
    }


    /*
     * Wrapper class for a trained model and its performance metrics
     */
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


    /*
     * Get a packaged model with metrics, trained using provided options
     */
    public TrainedModel getTrainedModel(LogisticRegression.Options options) { 
        // Get master training data set (from stratified data split)
        StratifiedDataSplitter.DataSet masterTrainingSet = masterDataSplitter.getDataBlock(0).getTrainSet();
        double[][] masterTrainingData = masterTrainingSet.getData();
        int[] masterTrainingLabels = masterTrainingSet.getLabels();

        // Train a model using the master training data and provided options
        LogisticRegression.Multinomial finalModel = LogisticRegression.multinomial(masterTrainingData, masterTrainingLabels, options);
        
        // Get master testing data set (from stratified data split)
        StratifiedDataSplitter.DataSet masterTestingSet = masterDataSplitter.getDataBlock(0).getTestSet();
        double[][] masterTestingData = masterTestingSet.getData();
        int[] masterTestingLabels = masterTestingSet.getLabels();  

        // Measure performance metrics using holdout master test data set
        // package model and metrics into a TrainedModel wrapper, and return it
        int[] finalPrediction = finalModel.predict(masterTestingData);
        double finalAccuracy = Accuracy.of(masterTestingLabels, finalPrediction);
        // OTHER METRICS HERE ... <------


        return new ModelTrainer.TrainedModel(finalModel, options, finalAccuracy);
    }


    /*
    * Get a packaged model with metrics, trained using options found with grid-search tuning
    */
    public TrainedModel getTrainedModel() {
        // Find hyper-parameters through tuning (using grid-search)
        LogisticRegression.Options finalOptions = getTunedOptions();

        // Call overloaded method to train and return final model using found hyperparameters
        return getTrainedModel(finalOptions);
    }


    /*
     * Perform model tuning using hyper-parameter grid search and get best parameters (i.e. options)
     */
    public LogisticRegression.Options getTunedOptions() {
        // Get master training data set (from stratified data split)
        StratifiedDataSplitter.DataSet masterTrainingSet = masterDataSplitter.getDataBlock(0).getTrainSet();
        double[][] masterTrainingData = masterTrainingSet.getData();
        int[] masterTrainingLabels = masterTrainingSet.getLabels();

        // Split traiing data further into training and testing subsets for validation
        StratifiedDataSplitter validationSplitter = new StratifiedDataSplitter(masterTrainingData, masterTrainingLabels, DEFAULT_NUM_FOLDS);
        
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
                    // Running total Accuracy measure for measuring mean across Cross-Validaiton folds
                    double runningTotalAccuracy = 0;
                    for (int i=0; i<DEFAULT_NUM_FOLDS; i++) {
                        // Get validation training data set (from 2nd stratified data split)
                        StratifiedDataSplitter.DataSet validationTrainingSet = validationSplitter.getDataBlock(i).getTrainSet();
                        double[][] validationTrainingData = validationTrainingSet.getData();
                        int[] validationTrainingLabels = validationTrainingSet.getLabels();
        
                        // Train a model using the validation training data and current options
                        LogisticRegression.Options options = new LogisticRegression.Options(lambda, tolerance, maxIter);
                        LogisticRegression.Multinomial model = LogisticRegression.multinomial(validationTrainingData, validationTrainingLabels, options);
                        
                        // Get validation testing data set (from 2nd stratified data split)
                        StratifiedDataSplitter.DataSet validationTestingSet = validationSplitter.getDataBlock(i).getTestSet();
                        double[][] validationTestingData = validationTestingSet.getData();
                        int[] validationTestingLabels = validationTestingSet.getLabels();

                        // Measure performance metrics using holdout validation testing data set and add to the running total
                        int[] prediction = model.predict(validationTestingData);
                        double currentAccuracy = Accuracy.of(validationTestingLabels, prediction);
                        runningTotalAccuracy += currentAccuracy;
                    }
                    
                    // Measure mean accuracy of cross-validation for current options and update best parameters if performance gain detected
                    double meanCurrentAccuracy = runningTotalAccuracy / DEFAULT_NUM_FOLDS;
                    if (meanCurrentAccuracy > bestAccuracy) {
                        bestAccuracy = meanCurrentAccuracy;
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