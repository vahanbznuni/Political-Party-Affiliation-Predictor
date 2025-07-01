import java.io.IOException;

public class Controller {
    private static final String DATA_FILE_NAME = "data/data_1000_realistic.csv";
    
    /*
     * Core logic of the program
     */
    public static void execute() {

        // Initialize data input
        DataStore dataStore = new DataStore(DATA_FILE_NAME);
        try {
            dataStore.loadData();
        } catch (CorruptDataException ex) {
            System.out.println("Error loading Data: corrupt data.");
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error loading Data: IO Exception occured.");
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        // Initialize predictor
        Predictor predictor = new Predictor(dataStore);

        // Initialize the command-line interface
        CLI cli = new CLI();

        // Print introductory message(s)
        System.out.println("\n\n");
        System.out.println("Welcome to the survey.");
        System.out.println("\n\n");

        // Conduct survey(s) until user exists
        boolean loop = true;
        while (loop) {
            // Conduct main parto of the survey and output the prediction
            double[] mainResponces = cli.conductSurveyMain();
            PartyAffiliation prediction = DataStore.intToPartyAffiliation(predictor.predict(mainResponces));
            System.out.print("\nThe system predicts your party affiliation as follows: ");
            System.out.println(prediction);
            
            // Gather the final responce, save data, and retrain model on update data
            int finalResponce = cli.conductSurveyFinal();
            dataStore.addData(mainResponces, finalResponce);
            predictor.retrainModel();
            printStats(predictor.getModel());

            // Check if the user wants to conduct another survey
            int repeat = cli.checkRepeat();
            switch (repeat) {
                case 1:
                    continue;
                case 2:
                    loop = false;
                    break;
                default:
                    throw new RuntimeException("Unexpected Input received");
            }
        }



        try {
            dataStore.saveData();
        } catch (IOException ex) {
            System.out.println("Error saving Data: ");
            System.out.println(ex.getMessage());
            System.exit(1);
        } 

        System.out.println("Goodbye!");
        System.out.println("\n\n\n");
    }

    public static void printStats(ModelTrainer.TrainedModel model) {
        int places = 2; // Rounding precision
        double scale = Math.pow(10, places);
        ModelTrainer.ModelMetrics modelMetrics = model.getModelMetrics();
        String accuracy = Math.round(modelMetrics.getMeasuredAccuracy()*scale) + "%";
        String recall = Math.round(modelMetrics.getMeasuredRecall()*scale) + "%";
        String precision = Math.round(modelMetrics.getPrecision()*scale) + "%";
        String f1Score = Math.round(modelMetrics.getF1Score()*scale) + "%"; 

        System.out.println("\n");
        System.out.println("The following are the current (updated) model performance metrics:");
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Recall: " + recall);
        System.out.println("Precision: " + precision);
        System.out.println("F1 Score: " + f1Score);
        System.out.println("\n");
    }


}