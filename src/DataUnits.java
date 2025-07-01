/*
 * Utility class for packaging traigning and testing data
 */
public class DataUnits {

    static class DataBlock {
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


    static class DataSet {
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


    static class ProcessedDataPacket {
        private final DataBlock processedDataBlock;
        private final Scaler.NormalDistParams normalDistParams;

        public ProcessedDataPacket(DataBlock processedBlock, Scaler.NormalDistParams normalDistParams) {
            this.processedDataBlock = processedBlock;
            this.normalDistParams = normalDistParams;
        }

        public DataBlock getProcessedDataBlock() {
            return processedDataBlock;
        }

        public Scaler.NormalDistParams getNormalDistParams() {
            return normalDistParams;
        }

    }
    
}
