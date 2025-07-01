import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataStore {
    String dataFileName;
    private static final int NUMBER_OF_FEATURES = 12;
    private List<List<Double>> data;
    private List<Integer> labels;

    private static enum EncodingDirection {
        FORWARD,
        REVERSE
    }

    
    public DataStore(String dataFileName) {
        this.dataFileName = dataFileName;
        this.data = new ArrayList<List<Double>>();
        this.labels = new ArrayList<Integer>();
    }

    public int getSize() {
        return data.size();
    }

    /*
     * Load data from a provided CSV file
     * Note, this method iterates over all the rows in the provided file and adds the data
     * to the in-memory data structures of this class.
     * 
     * It throws an error if there are any rows that do not contain the right number of columns
     */
    public void loadData() throws CorruptDataException, IOException {
        File file = new File(dataFileName);
        List<String> rows = new ArrayList<>();
        
        try {
            int expectedColumns = NUMBER_OF_FEATURES + 1;
            rows = Files.readAllLines(file.toPath());
            // start iteration at index 1, since we expect a header row at idx 0
            
            for (int i=1; i<rows.size(); i++) {
                // Read line of comma-separated numerical strings, convert them into Doubles
                // and store them in a list of Doubles called sample
                List<Double> sample = Arrays.stream(rows.get(i).split(","))
                                                .map(String::trim)
                                                .map(Double::valueOf)
                                                .collect(Collectors.toList());
                
                // Check to ensure the row contains the expected number of elements
                if (sample.size()!=expectedColumns) {
                    String rawMsg = "Unexpected input file: extected row length of %s but found %s";
                    throw new CorruptDataException(String.format(rawMsg, expectedColumns, sample.size()));
                }
                
                // Extract input vector and label from sample list and save them to their respective lists
                data.add(sample.subList(0, NUMBER_OF_FEATURES));
                labels.add(sample.get(NUMBER_OF_FEATURES).intValue());
            }

        } catch (IOException ex) {
            System.err.println("Error reading file: " + ex.getMessage());
            System.err.println("\n\n");
            System.err.println(ex);
            System.err.println("\n");
            throw ex;
        }

        System.out.println();

    }

    /*
     * Save in-memory data from class data structure to persistent memory on disk
     */
    public void saveData() throws IOException {
        String backupFileName = getBackupFileName(dataFileName);
        File file = new File(dataFileName);

        // Make a backup copy of the data file
        File backupFile = new File(backupFileName);
        Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        StringBuilder dataString = new StringBuilder();

        // Write header row
        dataString.append("ticket_split,party_fit,more_parties,inflation,deficit,")
        .append("immigration,guns,morals,climate,terrorism,economy,crime,label")
        .append(System.lineSeparator());

        for (int i=0; i<getSize(); i++) {
            for (int j=0; j<NUMBER_OF_FEATURES; j++) {
                dataString.append(getData().get(i).get(j).intValue())
                .append(",");
            }
            dataString.append(getLabels().get(i)).append(System.lineSeparator());
        }
        Files.writeString(
            file.toPath(), 
            dataString,
            StandardOpenOption.CREATE, // Becuase file has been moved to a backup
            StandardOpenOption.APPEND
        );
    }

    /*
     * Utility method to generate file name for backup file
     */
    private String getBackupFileName(String fileName) throws IllegalArgumentException {
        String[] fileNameElements = fileName.split("\\.");
        if (fileNameElements.length != 2) {
            throw new IllegalArgumentException("Unexpected file name.");
        }
        String baseName = fileNameElements[0];
        String extension = fileNameElements[1];
        String backupFileName = baseName + "_bak." + extension;
        return backupFileName;
    }


    /*
     * Return the list of Double lists that represent the data
     */
    public List<List<Double>> getData() {
        return this.data;
    }

    /*
     * Return the list of Integers that represents the labels
     */
    public List<Integer> getLabels() {
        return this.labels;
    }

    /*
     * Add new entry to internal data structures
     */
    public void addData(double[] vector, int label) {
        if (vector.length != NUMBER_OF_FEATURES) {
            throw new IllegalArgumentException("Unexpected number of features");
        }
        List<Double> newEntry = new ArrayList<Double>();
        for (double number : vector) {
            newEntry.add((Double) number);
        }
        this.data.add(newEntry);
        this.labels.add(label);

    }
    
    /*
     * Convert party affiliation label code into its corresponding affiliation label (enum)
     */
    public static PartyAffiliation intToPartyAffiliation(int labelCode) {
        switch(labelCode) {
            case 0:
                return PartyAffiliation.DEMOCRAT;
            case 1:
                return PartyAffiliation.REPUBLICAN;
            case 2:
                return PartyAffiliation.INDEPENDENT;
            case 3:
                return PartyAffiliation.THIRD_PARTY_OTHER;
            default:
                String msgRaw = "Unexpected input received. %s is not a valid label code";
                throw new IllegalArgumentException(String.format(msgRaw, labelCode));
        }
    }

    /*
     * Convert party affiliation label (enum) into its corresponding label code (int)
     */
    public static int partyAffiliationToInt(PartyAffiliation affiliationLabel) {
        switch(affiliationLabel) {
            case PartyAffiliation.DEMOCRAT:
                return 0;
            case PartyAffiliation.REPUBLICAN:
                return 1;
            case PartyAffiliation.INDEPENDENT:
                return 2;
            case PartyAffiliation.THIRD_PARTY_OTHER:
                return 3;
            default:
                String msgRaw = "Unexpected input received. %s is not a valid party affiliation label";
                throw new IllegalArgumentException(String.format(msgRaw, affiliationLabel));
        }
    }
    
    /*
     * Convert nested list of data into a 2D array for ML processing
     */
    public static double[][] toDoubleMatrix(List<List<Double>> data) {
        int m = data.size();
        int n = data.get(0).size();
        double[][] dataMatrix = new double[m][n];
        for (int i=0; i<m; i++) {
            for (int j=0; j<n; j++) {
                dataMatrix[i][j] = data.get(i).get(j);
            }
        }
        return dataMatrix;
    }


    /*
     * Convert list of labels into a vector (i.e. array) for ML processing
     */
    public static int[] toIntVector(List<Integer> labels) {
        return labels.stream()
                        .mapToInt(Integer::intValue)
                        .toArray();
    }

    
    public static EncodingDirection getEncodingDirection(int featureNumber) {
        switch(featureNumber) {
            case 0, 3, 4, 5, 6, 7, 8, 9, 10, 11:
                return EncodingDirection.REVERSE;
            case 1, 2, 12:
                return EncodingDirection.FORWARD;
            default:
                throw new IllegalArgumentException("Unexpected input received");
        }
    }

    public static int getFeatureRange(int featureNumber) {
        switch(featureNumber) {
            case 0, 1, 12:
                return 4;
            case 2:
                return 2;
            case 3, 4, 5, 6, 7, 8, 9, 10, 11:
                return 3;
            default:
                throw new IllegalArgumentException("Unexpected input received");
        }
    }

    public static double[] encodeFeaturesVector(double[] rawVector) {
        double[] encodedVector = new double[rawVector.length];
        for (int i=0; i<rawVector.length; i++) {
            EncodingDirection encodingDirection = getEncodingDirection(i);
            double value = rawVector[i];
            switch(encodingDirection) {
                case EncodingDirection.FORWARD:
                    encodedVector[i] = value - 1;
                    break;
                case EncodingDirection.REVERSE:
                    encodedVector[i] = getFeatureRange(i) - value;
            }
        }

        return encodedVector;
    }

}
