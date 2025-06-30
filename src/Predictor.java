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
        try {
            dataStorage.loadData();
        } catch (CorruptDataException ex) {
            System.out.println("Error loading Data");
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        double[][] trainingData = DataStore.toDoubleMatrix(dataStorage.getData());
        int[] trainingLabels = DataStore.toIntVector(dataStorage.getLabels());
        switch(mode) {
            case DEFAULT:
                this.trainedModel = new ModelTrainer(trainingData, trainingLabels).getTrainedModel();
                break;
            case REUSE_OPTIONS:
                if (trainedModel == null) {
                    throw new IllegalArgumentException("Cannot reuse options when trainedModel is null");
                }
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
    
}
