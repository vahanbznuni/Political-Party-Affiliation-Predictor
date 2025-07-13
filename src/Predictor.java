// import smile.classification.LogisticRegression;

public class Predictor {
    private DataStore dataStorage;
    private ModelTrainer.TrainedModel trainedModel;
    private Scaler.NormalDistParams trainingDataScalingParams;

    private static enum TrainingMode {
        DEFAULT,
        REUSE_OPTIONS
    }

    public Predictor(DataStore dataStorage) {
        this.dataStorage = dataStorage;
        setModel(TrainingMode.DEFAULT);
    }

    private void setModel(TrainingMode mode) {
        double[][] trainingData = DataStore.toDoubleMatrix(dataStorage.getData());
        int[] trainingLabels = DataStore.toIntVector(dataStorage.getLabels());
        ModelTrainer trainer = new ModelTrainer(trainingData, trainingLabels, ModelTrainer.TrainerOptions.PREPROCESS);
        this.trainingDataScalingParams = trainer.getMasterDataScaledParams();
        switch(mode) {
            case DEFAULT:
                // Consider redesigning
                this.trainedModel = trainer.getTrainedModel();
                break;
            case REUSE_OPTIONS:
                if (trainedModel == null) {
                    throw new IllegalArgumentException("Cannot reuse options when trainedModel is null");
                }
                LogisticRegressionMultinomial.Options currentOptions = this.trainedModel.getOptions();
                this.trainedModel = trainer.getTrainedModel(currentOptions);
                break;
            default:
                throw new IllegalArgumentException("Unknown Mode: " + mode);    
        }   
    }


    public int predict(double[] vector) {
        return trainedModel.getModel().predict(vector);
    }

    /*
     * Retrain a new model on current state of data, using previously saved
     * hyperparemeters
     */
    public void retrainModel() {
        setModel(TrainingMode.REUSE_OPTIONS);  
    }

    public ModelTrainer.TrainedModel getModel() {
        return trainedModel;
    }

    public Scaler.NormalDistParams getTrainingDataScalingParams() {
        return trainingDataScalingParams;
    }
    
}
