import java.io.IOException;

public class _Test {

    public static void test1() {
        System.out.println("\n");
        System.out.println("Started Test1");

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
            // Automatically passes DO_NOT_PREPROCESS option
            trainingData, trainingLabels).getTrainedModel();

        // Train a model with scaling data
        ModelTrainer.TrainedModel trainedModel2 = new ModelTrainer(
            trainingData, trainingLabels, ModelTrainer.TrainerOptions.SCALE).getTrainedModel();

        // Train a model with scaling AND weighting data
        ModelTrainer.TrainedModel trainedModel3 = new ModelTrainer(
            trainingData, trainingLabels, ModelTrainer.TrainerOptions.SCALE_AND_WEIGHT).getTrainedModel();


        System.out.println("\n");
        System.out.println("-----------------------");
        System.out.println("Model 1 Stats");
        getStats(trainedModel1);
        System.out.println("\n");
        System.out.println("-----------------------");
        System.out.println("Model 2 Stats");
        getStats(trainedModel2);
        System.out.println("\n");
        System.out.println("-----------------------");
        System.out.println("Model 3 Stats");
        getStats(trainedModel3);

        System.out.println("\n");
        System.out.println("Basic Pass");
        System.out.println("\n");
    }

    public static void getStats(ModelTrainer.TrainedModel model) {
        System.out.println("\n");
        System.out.println("Accuracy: " + model.getModelMetrics().getMeasuredAccuracy());
        System.out.println("Recall: " + model.getModelMetrics().getMeasuredRecall());
        System.out.println("Precision: " + model.getModelMetrics().getPrecision());
        System.out.println("F1 Score: " + model.getModelMetrics().getF1Score());
        System.out.println("\n");
    }


    public static void main(String[] args) {
        test1();
    }

    
}
