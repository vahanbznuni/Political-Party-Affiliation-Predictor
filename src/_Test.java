import java.io.IOException;

public class _Test {
    public static void main(String[] args) {
        System.out.println("\n");

        String fileName = "data/data_1000_realistic.csv";
        DataStore testDataStore = new DataStore(fileName);
        try {
            testDataStore.loadData();
        } catch (CorruptDataException ex) {
            System.out.println("Error loading Data: corrupt data.");
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error loading Data: IO Exception occured.");
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        double[][] trainingData = DataStore.toDoubleMatrix(testDataStore.getData());
        int[] trainingLabels = DataStore.toIntVector(testDataStore.getLabels());
        
        // Train a model without scaling/weighting data
        ModelTrainer.TrainedModel trainedModel1 = new ModelTrainer(
            trainingData, trainingLabels).getTrainedModel();

        // Train a model with scaling/weighting data
        ModelTrainer.TrainedModel trainedModel2 = new ModelTrainer(
            trainingData, trainingLabels, ModelTrainer.TrainerOptions.PREPROCESS).getTrainedModel();

        System.out.println("\n");
        System.out.println("-----------------------");
        System.out.println("Model 1 Stats");
        getStats(trainedModel1);
        System.out.println("\n");
        System.out.println("-----------------------");
        System.out.println("Model 2 Stats");
        getStats(trainedModel2);


        System.out.println("\n");
        System.out.println("Basic Pass");
        System.out.println("\n");
    }

    public static void getStats(ModelTrainer.TrainedModel model) {
        System.out.println("\n");
        System.out.println(model.getModelMetrics().getMeasuredAccuracy());
        System.out.println(model.getModelMetrics().getMeasuredRecall());
        System.out.println(model.getModelMetrics().getPrecision());
        System.out.println(model.getModelMetrics().getF1Score());
        System.out.println("\n");
    }
    
}
