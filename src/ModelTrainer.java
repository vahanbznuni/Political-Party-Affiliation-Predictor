import smile.classification.LogisticRegression;
import smile.validation.metric.Accuracy;
import smile.validation.metric.Averaging;
import smile.validation.metric.Precision;
import smile.validation.metric.Recall;
import smile.validation.metric.FScore;


public class ModelTrainer {
    private static final int DEFAULT_NUM_FOLDS = 5;
    private final StratifiedDataSplitter.DataBlock masterDataBlock;
    // Search space for hyper-parameters
    private static final double[] DEFAULT_LAMBDA_RANGE = new double[]{1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1};
    private static final double[] DEFAULT_TOLERANCE_RANGE = new double[]{1e-4, 1e-5, 1e-6};
    private static final int[] DEFAULT_MAX_ITER_RANGE = new int[]{100, 300, 900};


    public ModelTrainer(double[][] masterData, int[] masterLabels) {
        // Split master data/labels into training/validation (80%) and hold-out test (20%) data sets       
        this.masterDataBlock = new StratifiedDataSplitter(masterData, masterLabels, DEFAULT_NUM_FOLDS).getDataBlock(0);
    }


    /*
     * Wrapper class for holding a trained model, performance metrix set, and options set
     */
    public static class TrainedModel {
        private final LogisticRegression.Multinomial model;
        private final LogisticRegression.Options options;
        private final ModelMetrics modelMetrics;

        public TrainedModel(LogisticRegression.Multinomial model, LogisticRegression.Options options, ModelMetrics modelMetrics) {
            this.model = model;
            this.options = options;
            this.modelMetrics = modelMetrics;
        }

        public LogisticRegression.Multinomial getModel() {
            return model;
        }

        public LogisticRegression.Options getOptions() {
            return options;
        }

        public ModelMetrics getModelMetrics() {
            return modelMetrics;
        }
    }

    /*
     * Wrapper class for model performance metrics
     */
    public static class ModelMetrics {
        private final double measuredAccuracy;
        private final double measuredRecall;
        private final double precision;
        private final double f1Score;

        public ModelMetrics(
            double measuredAccuracy, 
            double measuredRecall, 
            double precision, 
            double f1Score
        ) {
            this.measuredAccuracy = measuredAccuracy;
            this.measuredRecall = measuredRecall;
            this.precision = precision;
            this.f1Score = f1Score;
        }

        public double getMeasuredAccuracy() {
            return measuredAccuracy;
        }

        public double getMeasuredRecall() {
            return measuredRecall;
        }

        public double getPrecision() {
            return precision;
        }

        public double getF1Score() {
            return f1Score;
        }

    }


    /*
     * Get a packaged model with metrics, trained using provided options
     */
    public TrainedModel getTrainedModel(LogisticRegression.Options options) { 
        // Get master training data set (from stratified data split)
        StratifiedDataSplitter.DataSet masterTrainingSet = masterDataBlock.getTrainSet();
        double[][] masterTrainingData = masterTrainingSet.getData();
        int[] masterTrainingLabels = masterTrainingSet.getLabels();

        // Train a model using the master training data and provided options
        LogisticRegression.Multinomial finalModel = LogisticRegression.multinomial(masterTrainingData, masterTrainingLabels, options);
        
        // Get master testing data set (from stratified data split)
        StratifiedDataSplitter.DataSet masterTestingSet = masterDataBlock.getTestSet();
        double[][] masterTestingData = masterTestingSet.getData();
        int[] masterTestingLabels = masterTestingSet.getLabels();  

        // Measure performance metrics using holdout master test data set
        // package model and metrics into a TrainedModel wrapper, and return it
        int[] finalPrediction = finalModel.predict(masterTestingData);
        double finalAccuracy = Accuracy.of(masterTestingLabels, finalPrediction);
        double finalPrecision = Precision.of(masterTestingLabels, finalPrediction, Averaging.Weighted);
        double finalRecall = Recall.of(masterTestingLabels, finalPrediction, Averaging.Weighted);
        double finalF1Score = FScore.of(masterTestingLabels, finalPrediction, 1, Averaging.Weighted);
        ModelMetrics modelMetrics = new ModelMetrics(finalAccuracy, finalRecall, finalPrecision, finalF1Score);

        return new ModelTrainer.TrainedModel(finalModel, options, modelMetrics);
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
        StratifiedDataSplitter.DataSet masterTrainingSet = masterDataBlock.getTrainSet();
        double[][] masterTrainingData = masterTrainingSet.getData();
        int[] masterTrainingLabels = masterTrainingSet.getLabels();

        // Split traiing data further into training and testing subsets for validation
        StratifiedDataSplitter validationSplitter = new StratifiedDataSplitter(masterTrainingData, masterTrainingLabels, DEFAULT_NUM_FOLDS);
        


        // Search (i.e. tuning)
        double bestAccuracy = Integer.MIN_VALUE;
        double bestLambda = 0;
        double bestTolerance = 0;
        int bestMaxIter = 0;
        for (double lambda : DEFAULT_LAMBDA_RANGE) {
            for (double tolerance : DEFAULT_TOLERANCE_RANGE) {
                for (int maxIter : DEFAULT_MAX_ITER_RANGE) {
                    // Running total Accuracy measure for measuring mean across Cross-Validaiton folds
                    double runningTotalAccuracy = 0;
                    for (int i=0; i<DEFAULT_NUM_FOLDS; i++) {
                        StratifiedDataSplitter.DataBlock validationDataBlock = validationSplitter.getDataBlock(i);

                        // Get validation training data set (from 2nd stratified data split)
                        StratifiedDataSplitter.DataSet validationTrainingSet = validationDataBlock.getTrainSet();
                        double[][] validationTrainingData = validationTrainingSet.getData();
                        int[] validationTrainingLabels = validationTrainingSet.getLabels();
        
                        // Train a model using the validation training data and current options
                        LogisticRegression.Options options = new LogisticRegression.Options(lambda, tolerance, maxIter);
                        LogisticRegression.Multinomial model = LogisticRegression.multinomial(validationTrainingData, validationTrainingLabels, options);
                        
                        // Get validation testing data set (from 2nd stratified data split)
                        StratifiedDataSplitter.DataSet validationTestingSet = validationDataBlock.getTestSet();
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