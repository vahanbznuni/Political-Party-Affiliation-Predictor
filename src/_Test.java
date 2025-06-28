import smile.classification.LogisticRegression;

public class _Test {
    public static void main(String[] args) {
        System.out.println("\n");

        String fileName = "data/data_1000.csv";
        int numberOfFeatures = 12;
        DataStore testDataStore = new DataStore(fileName, numberOfFeatures);
        try {
            testDataStore.loadData();
        } catch (CorruptDataException ex) {
            System.out.println(ex.getMessage());
        }

        LogisticRegression model = ModelTrainer.getTrainedModel(
            DataStore.toDoubleMatrix(testDataStore.getData()), 
            DataStore.toIntVector(testDataStore.getLabels())
        );

        System.out.println("\n");
        System.out.println("Basic Pass");
        System.out.println("\n");
    }
    
}
