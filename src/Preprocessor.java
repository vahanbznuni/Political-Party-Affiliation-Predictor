/*
 * Utility class for applying scaling and weighting to data
 */
public class Preprocessor {
    
    public static DataUnits.DataBlock getProcessed(DataUnits.DataBlock rawDataBlock) {
        // Extract raw features data
        double[][] trainDataRaw = rawDataBlock.getTrainSet().getData();
        double[][] testDataRaw = rawDataBlock.getTestSet().getData();

        // Fit normalizer
        Scaler.NormalDistParams params = Scaler.computeNormalDistParams(trainDataRaw);

        // Scale both train and test raw data using parameters learned from train
        double[][] trainScaled = Scaler.toNormalizedMatrix(trainDataRaw, params);
        double[][] testScaled = Scaler.toNormalizedMatrix(testDataRaw, params);

        // Apply weighting
        double[][] trainScaledWeighted = Weighter.toWeightedMatrix(trainScaled);
        double[][] testScaledWeighted = Weighter.toWeightedMatrix(testScaled);

        // Package into new DataBlocks
        DataUnits.DataSet transformedTrainSet = new DataUnits.DataSet(
            trainScaledWeighted, rawDataBlock.getTrainSet().getLabels());
        DataUnits.DataSet transformedTestSet = new DataUnits.DataSet(
            testScaledWeighted, rawDataBlock.getTestSet().getLabels());

        return new DataUnits.DataBlock(transformedTrainSet, transformedTestSet);
    }
    
}
