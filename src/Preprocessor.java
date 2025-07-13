/*
 * Utility class for applying scaling and weighting to data
 */
public class Preprocessor {

    // Single-argument processor that scales and weights
    public static DataUnits.ProcessedDataPacket getProcessed(
        DataUnits.DataBlock rawDataBlock
    ) {
        return getProcessed(rawDataBlock, true, true);
    }
    
    // Multi-argument processor where scaling and weighting is selected
    public static DataUnits.ProcessedDataPacket getProcessed(
        DataUnits.DataBlock rawDataBlock,
        boolean scale,
        boolean weight
    ) {
        // Extract raw features data
        double[][] trainDataRaw = rawDataBlock.getTrainSet().getData();
        double[][] testDataRaw = rawDataBlock.getTestSet().getData();

        // Set output data to raw
        double[][] outputTrainData = trainDataRaw;
        double[][] outputTestData = testDataRaw;

        // Declare params that will be populated with normal distribution
        // parameters if scaling is performed
        Scaler.NormalDistParams params = null;

        // Scale data no standard normal, if selected
        if (scale) {        
            // Fit normalizer
            params = Scaler.computeNormalDistParams(trainDataRaw);

            // Scale both train and test raw data using parameters learned from train
            outputTrainData = Scaler.toNormalizedMatrix(trainDataRaw, params);
            outputTestData = Scaler.toNormalizedMatrix(testDataRaw, params);
        }

        // Weight data using experimental weights, if selected
        if (weight) {       
            // Apply weighting
            outputTrainData = Weighter.toWeightedMatrix(outputTrainData);
            outputTestData = Weighter.toWeightedMatrix(outputTestData);
        }

        // Package into new DataBlocks
        DataUnits.DataSet transformedTrainSet = new DataUnits.DataSet(
            outputTrainData, rawDataBlock.getTrainSet().getLabels());
        DataUnits.DataSet transformedTestSet = new DataUnits.DataSet(
            outputTestData, rawDataBlock.getTestSet().getLabels());

        
        DataUnits.DataBlock processedDataBlock = new DataUnits.DataBlock(transformedTrainSet, transformedTestSet);

        return new DataUnits.ProcessedDataPacket(processedDataBlock, params);
    }
    
}
