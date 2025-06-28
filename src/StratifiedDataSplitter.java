import smile.math.MathEx;
import smile.validation.Bag;
import smile.validation.CrossValidation;

/*
 * Utility class to perform stratified data splits
 */
public class StratifiedDataSplitter {
    private double[][] masterData;
    private int[] masterLabels;
    private int numberOfFolds;
    private DataBlock trainSet;
    private DataBlock testSet;

    public StratifiedDataSplitter(double[][] data, int[] labels, int numberOfFolds) {
        this.masterData = data;
        this.masterLabels = labels;
        this.initializeSplit();

    }

    private void initializeSplit() {
        // Split data into training/validation and hold-out test sets
        // Get stratified indices for the split
        Bag[] bags = CrossValidation.stratify(this.masterLabels, numberOfFolds);
        Bag split = bags[0];
        int[] trainingIndices = split.samples(); // indices of training samples
        int[] testingIndices = split.oob(); // indices of holdout, aka out-of-bag (oob) test set

        // Setup data using the indices of the split

        // Train
        double[][] trainingData = MathEx.slice(masterData, trainingIndices);
        int[] trainingLabels = MathEx.slice(masterLabels, trainingIndices);
        this.trainSet = new DataBlock(trainingData, trainingLabels);
        
        // Test
        double[][] testData = MathEx.slice(masterData, testingIndices);
        int[] testLabels = MathEx.slice(masterLabels, testingIndices);
        this.testSet = new DataBlock(testData, testLabels);      
    }

    public DataBlock getTrainSet() {
        return this.trainSet;
    }

    public DataBlock getTestSet() {
        return this.testSet;
    }


    class DataBlock {
        private double[][] data;
        private int[] labels;

        public DataBlock(double[][] data, int[] labels) {
            this.data = data;
            this.labels = labels;
        }    

        public double[][] getData() {
            return this.data;
        }

        public int[] getLabels() {
            return this.labels;
        }
    }

    
}
