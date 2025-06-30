import smile.classification.LogisticRegression;

public class _Test {
    public static void main(String[] args) {
        System.out.println("\n");

        String fileName = "data/data_1000_realistic.csv";
        DataStore testDataStore = new DataStore(fileName);
        try {
            testDataStore.loadData();
        } catch (CorruptDataException ex) {
            System.out.println(ex.getMessage());
        }

        ModelTrainer.TrainedModel trainedModel = ModelTrainer.getTrainedModel(
            DataStore.toDoubleMatrix(testDataStore.getData()), 
            DataStore.toIntVector(testDataStore.getLabels())
        );

        System.out.println("\n");
        System.out.println("\n");
        System.out.println(trainedModel.getMeasuredAccuracy());

        System.out.println("\n");
        System.out.println("Basic Pass");
        System.out.println("\n");
    }
    
}
