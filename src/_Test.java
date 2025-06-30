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
        ModelTrainer.TrainedModel trainedModel = new ModelTrainer(trainingData, trainingLabels).getTrainedModel();

        System.out.println("\n");
        System.out.println("\n");
        System.out.println(trainedModel.getModelMetrics().getMeasuredAccuracy());
        System.out.println(trainedModel.getModelMetrics().getMeasuredRecall());
        System.out.println(trainedModel.getModelMetrics().getPrecision());
        System.out.println(trainedModel.getModelMetrics().getF1Score());

        System.out.println("\n");
        System.out.println("Basic Pass");
        System.out.println("\n");
    }
    
}
