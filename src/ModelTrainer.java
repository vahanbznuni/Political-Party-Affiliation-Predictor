public class ModelTrainer {
    private static final int DEFAULT_NUM_FOLDS = 5;
    private final DataUnits.DataBlock masterDataBlock;
    private Scaler.NormalDistParams masterDataScaledParams;
    // Search space for hyper-parameters
    private static final double[] DEFAULT_LAMBDA_RANGE = new double[]{1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1};
    private static final double[] DEFAULT_TOLERANCE_RANGE = new double[]{1e-4, 1e-5, 1e-6};
    private static final int[] DEFAULT_MAX_ITER_RANGE = new int[]{100, 300, 900};
    private static final int _TEST_NUM_CLASSES = 4; // <-- This should be refactored to be a passable param.


    /*
     * Apply data splitting, normalization scaling, weighting, and packaging into 
     * test and train sets, and save the packaged data block as an instanced variable
     */
    public ModelTrainer(double[][] masterData, int[] masterLabels, TrainerOptions option) {
        DataUnits.DataBlock rawDataBlock = new StratifiedDataSplitter(masterData, masterLabels, DEFAULT_NUM_FOLDS, _TEST_NUM_CLASSES).getDataBlock(0); 
        DataUnits.DataBlock finalDataBlock = rawDataBlock;
        
        // Applying processing as selected
        if (option != TrainerOptions.DO_NOT_PREPROCESS) {
            DataUnits.ProcessedDataPacket dataPacket = null;
            switch(option) {
                case TrainerOptions.SCALE:
                    dataPacket = Preprocessor.getProcessed(rawDataBlock, true, false);
                    break;
                case TrainerOptions.WEIGHT:
                    dataPacket = Preprocessor.getProcessed(rawDataBlock, false, true);
                    break;
                case TrainerOptions.SCALE_AND_WEIGHT:
                    dataPacket = Preprocessor.getProcessed(rawDataBlock, true, true);
                    break;
                default:
                    // This shouldn't happen currently
                    System.out.println("\n\n***WARNING: UNEXPECTED CODE BLOCK REACHED***\n\n");
                    break;
            }
            finalDataBlock = dataPacket.getProcessedDataBlock();
            this.masterDataScaledParams = dataPacket.getNormalDistParams();
        }

        // Save final processed (or unprocessed) block
        this.masterDataBlock = finalDataBlock;
    }

    /*
     * Apply data splitting, packaging into test and train sets, and save the packaged 
     * data block as an instanced variable. This flow does not apply scalling and weighting
     */
    public ModelTrainer(double[][] masterData, int[] masterLabels) {
        this(masterData, masterLabels, TrainerOptions.DO_NOT_PREPROCESS);      
    }

    public Scaler.NormalDistParams getMasterDataScaledParams() {
        return masterDataScaledParams;
    }


    /*
     * Wrapper class for holding a trained model, performance metrix set, and options set
     */
    public static class TrainedModel {
        private final LogisticRegressionMultinomial model;
        private final LogisticRegressionMultinomial.Options options;
        private final ModelMetrics modelMetrics;

        public TrainedModel(LogisticRegressionMultinomial model, LogisticRegressionMultinomial.Options options, ModelMetrics modelMetrics) {
            this.model = model;
            this.options = options;
            this.modelMetrics = modelMetrics;
        }

        public LogisticRegressionMultinomial getModel() {
            return model;
        }

        public LogisticRegressionMultinomial.Options getOptions() {
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
     * Enum for controlling preprocessing choice
     */
    public static enum TrainerOptions {
        SCALE,
        WEIGHT,
        SCALE_AND_WEIGHT,
        DO_NOT_PREPROCESS
    }


    /*
     * Get a packaged model with metrics, trained using provided options
     */
    public TrainedModel getTrainedModel(LogisticRegressionMultinomial.Options options) { 
        // Get master training data set (from stratified data split)
        DataUnits.DataSet masterTrainingSet = masterDataBlock.getTrainSet();
        double[][] masterTrainingData = masterTrainingSet.getData();
        int[] masterTrainingLabels = masterTrainingSet.getLabels();

        // Train a model using the master training data and provided options
        LogisticRegressionMultinomial finalModel = new LogisticRegressionMultinomial(masterTrainingData, masterTrainingLabels, _TEST_NUM_CLASSES, options);
        
        // Get master testing data set (from stratified data split)
        DataUnits.DataSet masterTestingSet = masterDataBlock.getTestSet();
        double[][] masterTestingData = masterTestingSet.getData();
        int[] masterTestingLabels = masterTestingSet.getLabels();  

        // Measure performance metrics using holdout master test data set
        // package model and metrics into a TrainedModel wrapper, and return it
        int[] finalPrediction = finalModel.predict(masterTestingData);
        MetricsMultinomial metrics = new MetricsMultinomial(masterTestingLabels, finalPrediction, _TEST_NUM_CLASSES);
        double finalAccuracy = metrics.getAccuracy();
        double finalPrecision = metrics.getPrecision(MetricsMultinomial.Averaging.WEIGHTED);
        double finalRecall = metrics.getRecall(MetricsMultinomial.Averaging.WEIGHTED);
        double finalF1Score = metrics.getF1Score(MetricsMultinomial.Averaging.WEIGHTED);
        ModelMetrics modelMetrics = new ModelMetrics(finalAccuracy, finalRecall, finalPrecision, finalF1Score);

        return new ModelTrainer.TrainedModel(finalModel, options, modelMetrics);
    }


    /*
    * Get a packaged model with metrics, trained using options found with grid-search tuning
    */
    public TrainedModel getTrainedModel() {
        // Find hyper-parameters through tuning (using grid-search)
        LogisticRegressionMultinomial.Options finalOptions = getTunedOptions();

        // Call overloaded method to train and return final model using found hyperparameters
        return getTrainedModel(finalOptions);
    }


    /*
     * Perform model tuning using hyper-parameter grid search and get best parameters (i.e. options)
     */
    public LogisticRegressionMultinomial.Options getTunedOptions() {
        // Get master training data set (from stratified data split)
        DataUnits.DataSet masterTrainingSet = masterDataBlock.getTrainSet();
        double[][] masterTrainingData = masterTrainingSet.getData();
        int[] masterTrainingLabels = masterTrainingSet.getLabels();

        // Split traiing data further into training and testing subsets for validation
        StratifiedDataSplitter validationSplitter = new StratifiedDataSplitter(masterTrainingData, masterTrainingLabels, DEFAULT_NUM_FOLDS, _TEST_NUM_CLASSES);
        


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
                        DataUnits.DataBlock validationDataBlock = validationSplitter.getDataBlock(i);

                        // Get validation training data set (from 2nd stratified data split)
                        DataUnits.DataSet validationTrainingSet = validationDataBlock.getTrainSet();
                        double[][] validationTrainingData = validationTrainingSet.getData();
                        int[] validationTrainingLabels = validationTrainingSet.getLabels();
        
                        // Train a model using the validation training data and current options
                        LogisticRegressionMultinomial.Options options = new LogisticRegressionMultinomial.Options(lambda, tolerance, maxIter);
                        LogisticRegressionMultinomial model = new LogisticRegressionMultinomial(validationTrainingData, validationTrainingLabels, _TEST_NUM_CLASSES, options);
                        
                        // Get validation testing data set (from 2nd stratified data split)
                        DataUnits.DataSet validationTestingSet = validationDataBlock.getTestSet();
                        double[][] validationTestingData = validationTestingSet.getData();
                        int[] validationTestingLabels = validationTestingSet.getLabels();

                        // Measure performance metrics using holdout validation testing data set and add to the running total
                        int[] prediction = model.predict(validationTestingData);
                        MetricsMultinomial metrics = new MetricsMultinomial(validationTestingLabels, prediction, _TEST_NUM_CLASSES);
                        double currentAccuracy = metrics.getAccuracy();
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

        return new LogisticRegressionMultinomial.Options(bestLambda, bestTolerance, bestMaxIter);
    }



}