import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/*
 * Utility class to perform stratified data splits
 */
public class StratifiedDataSplitter {
    private double[][] masterData;
    private int[] masterLabels;
    private int numberOfFolds;
    private int numberOfClasses;
    private DataUnits.DataIndicesSet[] dataIndicesSets;

    public StratifiedDataSplitter(double[][] data, 
        int[] labels, 
        int numberOfFolds,
        int numberOfClasses
    ) {
        this.masterData = data;
        this.masterLabels = labels;
        this.numberOfFolds = numberOfFolds;
        this.numberOfClasses = numberOfClasses;
        this.dataIndicesSets = getStratifiedFolds();
    }

    
    public DataUnits.DataBlock getDataBlock(int FoldIndex) {
        if (FoldIndex >= numberOfFolds || FoldIndex < 0) {
            throw new IllegalArgumentException("FoldIndex needs to be between 0 and " + numberOfFolds);
        }

        // Get indices of the specified fold of the stratified split
        DataUnits.DataIndicesSet split = dataIndicesSets[FoldIndex];
        int[] trainingIndices = split.getTrainIndices(); // indices of training samples
        int[] testingIndices = split.getTestIndices(); // indices of holdout, aka out-of-bag (oob) test set        
        
        // get Train Set
        double[][] trainingData = slice(masterData, trainingIndices);
        int[] trainingLabels = slice(masterLabels, trainingIndices);
        DataUnits.DataSet trainSet = new DataUnits.DataSet(trainingData, trainingLabels);
        
        // get Test Set
        double[][] testData = slice(masterData, testingIndices);
        int[] testLabels = slice(masterLabels, testingIndices);
        DataUnits.DataSet testSet = new DataUnits.DataSet(testData, testLabels);
        
        return new DataUnits.DataBlock(trainSet, testSet);        
    }


    private HashMap<Integer, List<Integer>> getAssortedIndices() {
        HashMap<Integer, List<Integer>> classIndices = new HashMap<>();
        int dataSize = masterLabels.length;
        for (int i=0; i<dataSize; i++) {
            int currentClass = masterLabels[i];
            if (classIndices.get(currentClass) == null) {
                classIndices.put(currentClass, new ArrayList<Integer>());
            }
            classIndices.get(currentClass).add(i);
        }

        return classIndices;
    }

    /*
     * Shuffle provided list of lists
     */
    private void applyShuffle(HashMap<Integer, List<Integer>> lists) {
        Random randomNumberGenerator = new Random(42);
        for (List<Integer> indicesList : lists.values()) {
            Collections.shuffle(indicesList, randomNumberGenerator);
        }
    }


    public DataUnits.DataIndicesSet[] getStratifiedFolds() {
        int dataSize = masterLabels.length;
        DataUnits.DataIndicesSet[] indicesSets = new DataUnits.DataIndicesSet[numberOfFolds];

        // Initialize a list of lists, each of which will hold 
        // one of the K folds of indices
        List<List<Integer>> folds = new ArrayList<>();        
        for (int i=0; i<numberOfFolds; i++) {
            folds.add(new ArrayList<Integer>());
        }
        
        // Get and shuffle map of indices, assorted by class
        HashMap<Integer, List<Integer>> classIndices = getAssortedIndices();
        applyShuffle(classIndices);

        
        // Distribute indices from each assorted list in a stratified manner
        for (int i=0; i<numberOfClasses; i++) {
            List<Integer> currentClassIndices = classIndices.get(i);
            for (int j=0; j<currentClassIndices.size(); j++) {
                int indexVal = currentClassIndices.get(j);
                int foldNumber = j % numberOfFolds;
                folds.get(foldNumber).add(indexVal);
            }
        }

        for (int i=0; i<numberOfFolds; i++) {
            List<Integer> testIndices = null;
            List<Integer> trainIndices = new ArrayList<>();            
            // Pack current as test and the rest joined as train indices sets
            for (int j=0; j<numberOfFolds; j++) {
                if (j==i) {
                    testIndices = folds.get(j);
                } else {
                    trainIndices.addAll(folds.get(j));
                }
            }

            // Sanity Check
            if (testIndices == null) {
                throw new RuntimeException("Unexpected empty list");
            }

            // Stream test and train indices lists into int[] arrays and pack them into
            // their slots within IndicesSet, within their DataIndicesSet wrappers 
            int[] trainIndicesFinal = trainIndices.stream().mapToInt(Integer::intValue).toArray();
            int[] testIndicesFinal = testIndices.stream().mapToInt(Integer::intValue).toArray();
            DataUnits.DataIndicesSet fold = new DataUnits.DataIndicesSet(trainIndicesFinal, testIndicesFinal);
            indicesSets[i] = fold;
        }

        return indicesSets;

    }


    public double[][] slice(double[][] data, int[] indices) {
        int newHeight = indices.length;
        int newWidth = data[0].length;
        double[][] output = new double[newHeight][newWidth];
        for (int i=0; i<newHeight; i++) {
            output[i] = data[indices[i]];
        }

        return output;
    }


    public int[] slice(int[] labels, int[] indices) {
        int newLength = indices.length;
        int[] output = new int[newLength];
        for (int i=0; i<newLength; i++) {
            output[i] = labels[indices[i]];
        }

        return output;
    }

    
}
