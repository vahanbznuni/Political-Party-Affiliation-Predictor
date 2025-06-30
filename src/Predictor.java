// import smile.classification.LogisticRegression;

// public class Predictor {
//     private DataStore dataStorage;
//     private ModelTrainer.TrainedModel trainedModel;

//     public Predictor(DataStore dataStorage) {
//         this.dataStorage = dataStorage;
//         this.trainedModel = ModelTrainer.getTrainedModel(
//             DataStore.toDoubleMatrix(dataStorage.getData()), 
//             DataStore.toIntVector(dataStorage.getLabels())
//         );    
//     }

//     public int predict(double[] vector) {
//         return trainedModel.getModel().predict(vector);
//     }

//     /*
//      * Retrain a new model on current state of data, using previously saved
//      * hyperparemeters
//      */
//     public void retrain() {
//         LogisticRegression.Options currentOptions = trainedModel.getOptions();
//         this.trainedModel = ModelTrainer.getTrainedModel(
//             DataStore.toDoubleMatrix(dataStorage.getData()), 
//             DataStore.toIntVector(dataStorage.getLabels()),
//             currentOptions
//         );    
//     }
    
// }
