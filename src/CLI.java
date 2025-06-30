import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CLI {
    private static final int NUMBER_OF_FEATURES = 12;
    Question[] questions;
    Scanner cliScanner;

    private static class Question {
        String question;
        String[] answerOptions;

        public Question(String question, String[] answerOptions) {
            this.question = question;
            this.answerOptions = answerOptions;
        }
    }


    public CLI() {
        String[] commonAnswerOptions1 = new String[]{"Major problem.", "Somewhat of a problem.", "Not much of a problem."};
        String[] commonAnswerOptions2 = new String[]{"Extremely important.", "Somewhat important.", "Not very important."};

        Question question1 = new Question(
            "In recent elections, how often have you voted for candidates from both major parties on the same ballot (for different offices)?", 
            new String[]{"Often.", "Sometimes.", "Rarely.", "Never."}
        );

        Question question2 = new Question(
            "How well do either of the two major U.S. political parties represent your views overall?", 
            new String[]{"Very well.", "Somewhat well", "Not very well.", "Not at all."}
        );

        Question question3 = new Question(
            "Should more than two major parties have seats in Congress?", 
            new String[]{"No - two major parties are enough.", "Yes - minor/third parties should also have seats."}
        );

        Question question4 = new Question(
            "How big of a problem is inflation?", 
            commonAnswerOptions1
        );

        Question question5 = new Question(
            "How big of a problem is the federal budget deficit?", 
            commonAnswerOptions1
        );

        Question question6 = new Question(
            "How big of a problem is illegal immigration?", 
            commonAnswerOptions1
        );

        Question question7 = new Question(
            "How big of a problem is gun violence?", 
            commonAnswerOptions1
        );

        Question question8 = new Question(
            "How big of a problem is the current state of moral values?", 
            commonAnswerOptions1
        );

        Question question9 = new Question(
            "How big of a problem is climate change?", 
            commonAnswerOptions1
        );

        Question question10 = new Question(
            "How important of an issue is terrorism / national security?", 
            commonAnswerOptions2
        );

        Question question11 = new Question(
            "How important of an issue is the economy?", 
            commonAnswerOptions2
        );

        Question question12 = new Question(
            "How important of an issue is crime?", 
            commonAnswerOptions2
        );

        Question finalQuestion = new Question(
            "Please select party affiliation that best describes you:", 
            new String[]{
                "Democratic / lean Democratic.", 
                "Republican / lean Republican.", 
                "Independent.",
                "Other / 3rd Party (ex. Green, Libertarian, etc.)"
            }
        );

        this.questions = new Question[]{
            question1,
            question2,
            question3,
            question4,
            question5,
            question6,
            question7,
            question8,
            question9,
            question10,
            question11,
            question12,
            finalQuestion
        };

        cliScanner = new Scanner(System.in);
    }

    /*
     * Conduct the survey by presenting the user with the questionnaire and collecting
     * responces in to a numerical array, returning it in the end
     */
    public double[] conductSurvey() {
        String systemMessageMain = "For the purpose of this survey, if your response would have been different in the year 2024 than it is now due to a change in policy or events other than any change in your personal views, please provide the response as it would have been in 2024.";
        String systemMessage2 = "Note: if the screen seems frozen, press ENTER";
        String horizontalBar = "=========================";
        double[] responces = new double[NUMBER_OF_FEATURES+1];

        System.out.println("\n\n");
        System.out.println(systemMessage2);
        System.out.println("\n\n");
        System.out.println(horizontalBar);
        System.out.println(systemMessageMain);
        System.out.println("\n");
        for (int i=0; i<questions.length; i++) {
            
            Question question = questions[i];
            double userInput = -1;
            int NumberOfAnswerOpts = question.answerOptions.length;

            while (userInput <= 0 || userInput > NumberOfAnswerOpts) {
                System.out.println();
                System.out.println(question.question);
                for (int j=0; j<NumberOfAnswerOpts; j++) {
                    System.out.println("   " + (j+1) + ") " + question.answerOptions[j]);
                }
                System.out.println();
                System.out.print("Enter corresponding number: ");

                try {
                    String rawInput = cliScanner.nextLine();
                    if (rawInput.length() == 0) {
                        continue;
                    }
                    userInput = Double.valueOf(rawInput);
                    if (
                        userInput <= 0 || 
                        userInput > NumberOfAnswerOpts
                    ) {
                        throw new IllegalArgumentException();
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid Input: please enter a number between 1 and " + NumberOfAnswerOpts);
                    System.out.println();
                    userInput = -1;
                    continue;
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                    System.out.println("Invalid input: please try again");
                    System.out.println();
                    userInput = -1;
                    continue;
                } finally {
                    flushScanner();
                }
            }

            responces[i] = userInput;
            System.out.println();           
        }

        return responces;
    }

    // Private utility method to flush the scanner (of standard input), in case it contains any leftover characters
    private void flushScanner() {
        if (cliScanner.hasNextLine()) {
            cliScanner.nextLine();
        }
    }


    // For Testing
    public static void main(String[] args) {
        CLI cli = new CLI();
        double[] answers = cli.conductSurvey();
        System.out.println(Arrays.toString(answers));
    }
    
}
