import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

public class DataStore {
    String dataFileName;
    private int numberOfFeatures;
    private List<List<Integer>> data;
    private List<Integer> labels;
    
    public DataStore(String dataFileName, int numberOfFeatures) {
        this.dataFileName = dataFileName;
        this.numberOfFeatures = numberOfFeatures;
        this.data = new ArrayList<List<Integer>>();
        this.labels = new ArrayList<Integer>();
    }

    /*
     * Load data from a provided CSV file
     * Note, this method iterates over all the rows in the provided file and adds the data
     * to the in-memory data structures of this class.
     * 
     * It throws an error if there are any rows that do not contain the right number of columns
     */
    public void loadData() throws CorruptDataException {
        File file = new File(dataFileName);
        List<String> rows = new ArrayList<>();
        
        try {
            int expectedColumns = numberOfFeatures + 1;
            rows = Files.readAllLines(file.toPath());
            // start iteration at index 1, since we expect a header row at idx 0
            
            for (int i=1; i<rows.size(); i++) {
                // Read line of comma-separated numerical strings, convert them into Integers
                // and store them in a list of Integers called sample
                List<Integer> sample = Arrays.stream(rows.get(i).split(","))
                                                .map(String::trim)
                                                .map(Integer::valueOf)
                                                .collect(Collectors.toList());
                
                // Check to ensure the row contains the expected number of elements
                if (sample.size()!=expectedColumns) {
                    String rawMsg = "Unexpected input file: extected row length of %s but found %s";
                    throw new CorruptDataException(String.format(rawMsg, expectedColumns, sample.size()));
                }
                
                // Extract input vector and label from sample list and save them to their respective lists
                data.add(sample.subList(0, numberOfFeatures));
                labels.add(sample.get(numberOfFeatures));
            }

        } catch (IOException ex) {
            System.err.println("Error reading file: " + ex.getMessage());
            System.err.println("\n\n");
            System.err.println(ex);
            System.err.println("\n");
        }

        System.out.println();

    }

    /*
     * Return the list of Integer lists that represent the data
     */
    List<List<Integer>> getData() {
        return this.data;
    }

    /*
     * Return the list of Integers that represents the labels
     */
    List<Integer> getLabels() {
        return this.labels;
    }
    
    /*
     * Convert party affiliation label code into its corresponding affiliation label (enum)
     */
    PartyAffiliation intToPartyAffiliation(int labelCode) {
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
    int PartyAffiliationToInt(PartyAffiliation affiliationLabel) {
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
    
    

}
