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

    
    public DataUnits.DataBlock getDataBlock(int FoldIndex) {
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
        DataUnits.DataSet trainSet = new DataUnits.DataSet(trainingData, trainingLabels);
        
        // get Test Set
        double[][] testData = MathEx.slice(masterData, testingIndices);
        int[] testLabels = MathEx.slice(masterLabels, testingIndices);
        DataUnits.DataSet testSet = new DataUnits.DataSet(testData, testLabels);
        
        return new DataUnits.DataBlock(trainSet, testSet);        
    }



    
}
