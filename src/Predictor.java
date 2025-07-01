import smile.classification.LogisticRegression;

public class Predictor {
    private DataStore dataStorage;
    private ModelTrainer.TrainedModel trainedModel;

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
        switch(mode) {
            case DEFAULT:
                this.trainedModel = new ModelTrainer(trainingData, trainingLabels, ModelTrainer.TrainerOptions.PREPROCESS).getTrainedModel(); // <--Refactor later
                break;
            case REUSE_OPTIONS:
                if (trainedModel == null) {
                    throw new IllegalArgumentException("Cannot reuse options when trainedModel is null");
                }
                LogisticRegression.Options currentOptions = this.trainedModel.getOptions();
                this.trainedModel = new ModelTrainer(trainingData, trainingLabels).getTrainedModel(currentOptions);
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
    
}
