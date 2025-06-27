

public class _Test {
    public static void main(String[] args) {
        System.out.println("\n");

        String fileName = "data/data.csv";
        int numberOfFeatures = 12;
        DataStore testDataStore = new DataStore(fileName, numberOfFeatures);
        try {
            testDataStore.loadData();
        } catch (CorruptDataException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("\n");
        System.out.println("Basic Pass");
        System.out.println("\n");
    }
    
}
