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
    private Bag[] bags;

    public StratifiedDataSplitter(double[][] data, int[] labels, int numberOfFolds) {
        this.masterData = data;
        this.masterLabels = labels;
        this.numberOfFolds = numberOfFolds;
        this.bags = CrossValidation.stratify(masterLabels, numberOfFolds);
    }

    
    public DataBlock getDataBlock(int FoldIndex) {
        if (FoldIndex >= numberOfFolds || FoldIndex < 0) {
            throw new IllegalArgumentException("FoldIndex needs to be between 0 and " + numberOfFolds);
        }

        // Get indices of the specified fold of the stratified split
        Bag split = bags[FoldIndex];
        int[] trainingIndices = split.samples(); // indices of training samples
        int[] testingIndices = split.oob(); // indices of holdout, aka out-of-bag (oob) test set        
        
        // get Train Set
        double[][] trainingData = MathEx.slice(masterData, trainingIndices);
        int[] trainingLabels = MathEx.slice(masterLabels, trainingIndices);
        DataSet trainSet = new DataSet(trainingData, trainingLabels);
        
        // get Test Set
        double[][] testData = MathEx.slice(masterData, testingIndices);
        int[] testLabels = MathEx.slice(masterLabels, testingIndices);
        DataSet testSet = new DataSet(testData, testLabels);
        
        return new DataBlock(trainSet, testSet);        
    }


    class DataBlock {
        private final DataSet trainSet;
        private final DataSet testSet;

        public DataBlock(DataSet trainSet, DataSet testSet) {
            this.trainSet = trainSet;
            this.testSet = testSet;
        }

        public DataSet getTrainSet() {
            return trainSet;
        }

        public DataSet getTestSet() {
            return testSet;
        }
    }


    class DataSet {
        private double[][] data;
        private int[] labels;

        public DataSet(double[][] data, int[] labels) {
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
