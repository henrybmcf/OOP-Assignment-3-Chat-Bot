/**
 * Created by HenryBallingerMcFarlane on 23/02/2016.
 */

import com.sun.deploy.util.StringUtils;

import java.util.*;
import java.util.Random;

public class Chatbot {
    public static String uInput;
    public static String bOutput;

    public static String uInputBackup = "";

    public static String bPrevious = "";

    public static int small1 = 0, small2 = 0;

    public static boolean understand;
    public static boolean transposition = false;
    public static boolean exit;

    // List of punctuations marks
    final static String punctuation = "?!.;";

    // Set up the knowledge database
    public static String[][][] knowledge = setKnowledge();

    public final static String transposeList[][] = {
            {"i'm", "you're"},
            {"am", "are"},
            {"were", "was"},
            {"me", "you"},
            {"yours", "mine"},
            {"your", "my"},
            {"i've", "you've"},
            {"i", "you"},
            {"aren't", "am not"},
            {"weren't", "wasn't"},
            {"i'd", "you'd"},
            {"dad", "father"},
            {"mum", "mother"},
            {"dreams", "dream"},
            {"myself", "yourself"}
    };

    protected final static String[] salutations = {
            "Hello",
            "Hi, how are you?",
            "Such a nice day today!"
    };

    // List of possible user inputs to end the conversation
    protected final static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    public static void main(String[] args) {
        bOutput = assignSalutation();
        saveResponse(bOutput);
        System.out.println(bOutput);

        do {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            uInput = scanner.nextLine();
            // Remove unwanted white space and punctuation and convert to lower case
            uInput = clean(uInput);

            // Check user input against goodbye strings to see if program should exit
            int i = 0;
            while (i != goodbye.length) {
                if (EditDistance.MinimumEditDistance(uInput, goodbye[i]) < 2) {
                    exit = true;
                    break;
                }
                i++;
            }

            if (exit) {
                System.out.println("Goodbye, it was nice talking to you.");
                break;
            }
            else {
                searchKeyword();
                checkRepeat();

                bOutput = initCap(bOutput);
                saveResponse(bOutput);
                System.out.println(bOutput);
            }
        }
        while (true);
    }

    // Search knowledge database for match to user input
    public static void searchKeyword() {
        int j, k;
        search:
        for (j = 0; j < knowledge.length - 1; j++) {
            for (k = 0; k < knowledge[j][0].length; k++) {
                // Find the closest matching keyword (that with the smallest edit distance)
                float x = EditDistance.MinimumEditDistance(uInput, knowledge[j][0][k]);
                float s = EditDistance.MinimumEditDistance(uInput, knowledge[small1][0][small2]);

                // If keyword search reveals a closer matching keyword, set locators
                if (x < s) {
                    small1 = j;
                    small2 = k;
                }
                // If keyword exact match, set variables and break from both loops
                if (s == 0) {
                    small1 = j;
                    small2 = k;
                    understand = true;
                    break search;
                }
            }
        }

        // Check to see if closest matching keyword is close enough match
        if (EditDistance.MinimumEditDistance(uInput, knowledge[small1][0][small2]) < 3 && !understand)
            understand = true;
        else
            understand = false;
    }

    // Check to see if bot is repeating itself
    public static void checkRepeat() {

        // TODO Check if input matches any keywords, if not, check to see if the input has any sub strings that match the transposition list. If not, then go to default responses

        if (understand) {
            bOutput = assignResponse(small1, 1, knowledge[small1][1].length);

            // If bot's response if the same as previous, keep changing until different
            if (bRepeating()) {
                do {
                    bOutput = assignResponse(small1, 1, knowledge[small1][1].length);
                }
                while (bRepeating());
            }
        }
        else {
            // TODO split string when substring found, replace substring, concat string back together
            // TODO loop through string, splitting into number of elements of string to be replaced (i.e. You = 3), see if these match the string.
            // TODO then do the split and concat

            boolean goForSplit = false;
            // Need this?
            uInputBackup = uInput;

            for (int i = 0; i < transposeList.length; i++) {
                // Split input backup when match to transpose list item found
                // TODO Need to change to make sure ' ' (space) before and after transposition

                for (int j = 0; j < uInputBackup.length(); j++) {
                    StringBuilder checking = new StringBuilder(transposeList[i][0].length());
                    // TODO Add whitespace at start of check
                    checking.insert(0, " ");

                    if (j + transposeList[i][0].length() + 2 < uInputBackup.length()) {
                        checking.insert(1, uInputBackup.substring(j, j + transposeList[i][0].length() + 2));
                       // check = uInputBackup.substring(j, j + transposeList[i][0].length() + 2);
                    }
                    else if (j + transposeList[i][0].length() + 1 < uInputBackup.length()) {
                        checking.insert(1, uInputBackup.substring(j, j + transposeList[i][0].length() + 1));
                       // check = uInputBackup.substring(j, j + transposeList[i][0].length() + 1);
                    }
                    else if (j + transposeList[i][0].length() > uInputBackup.length()) {
                        break;
                    }

                    String check = checking.toString();
                    //if (check.contains(transposeList[i][0]) && check.charAt(0) == ' ' && check .charAt(check.length() - 1) == ' ') {
                    // Trim removes white space at start and end
                    if (check.trim().equals(transposeList[i][0])) {
                        goForSplit = true;
                        break;
                    }
                }

                if (goForSplit) {
                    String[] tokens = uInputBackup.split(transposeList[i][0]);

                    // If backup has been split (more then 1 element)
                    if (tokens.length > 1) {
                        uInputBackup = "";

                        // For each token (section of input backup), add it + new transposition to input backup
                        for (String token : tokens) {
                            uInputBackup = token.concat(transposeList[i][1]);
                        }

                        bOutput = uInputBackup;
                        transposition = true;
                        break;
                    }
                }
            }

            // If input hasn't been transposed
            if (!transposition) {
                bOutput = assignResponse(knowledge.length - 1, 1, knowledge[knowledge.length - 1].length);

                if (bRepeating()) {
                    do {
                        bOutput = assignResponse(knowledge.length - 1, 1, knowledge[knowledge.length - 1].length);
                    }
                    while (bRepeating());
                }
            }
        }
    }

/*
    public static String transpose(String input, String replacement, int el) {;
        return uInput.replace("re", transposeList[1][1]);
    }
*/

    // Clean up user input: Remove white space and punctuation & convert to lower case
    public static String clean(String str) {
            StringBuilder cleaning = new StringBuilder(str.length());

            char prevChar = 0;

            for(int i = 0; i < str.length(); i++) {
                if((str.charAt(i) == ' ' && prevChar == ' ') || !puncCheck(str.charAt(i))) {
                    cleaning.append(str.charAt(i));
                    prevChar = str.charAt(i);
                }
                else if(prevChar != ' ' && puncCheck(str.charAt(i))) {
                    cleaning.append(' ');
                }

            }
            str = cleaning.toString().toLowerCase();
            return str;
    }

    // indexOf returns -1 if the character does not occur in the string
    // If ch is not in punctuation, -1 will be returned (true)
    public static boolean puncCheck(char ch) {
        return punctuation.indexOf(ch) != -1;
    }

    // Capitalise first letter of string
    public static String initCap(String str) {
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }

    // Save bot's response to check if repeating itself
    public static void saveResponse(String current) {
        bPrevious = current;
        understand = false;
        transposition = false;
    }

    // Return true is previous bot response exists and is same as current response
    public static boolean bRepeating() {
        return bPrevious.length() > 0 && bOutput.equalsIgnoreCase(bPrevious);
    }

    // Select random salutation from array
    public static String assignSalutation() {
        Random rand = new Random();
        return salutations[rand.nextInt(salutations.length)];
    }

    // Select random bot response
    public static String assignResponse(int x, int y, int range) {
        Random rand = new Random();
        int z = rand.nextInt(range);
        return knowledge[x][y][z];
    }

    // First line is the key word/phrase
    // The rest are the responses
    private static String[][][] setKnowledge() {
        knowledge = new String[][][]{
                {
                        {
                                "what is your name",
                                "what's your name",
                                "your name"
                        },
                        {
                                "what would you like my name to be?",
                                "why do you want to know?",
                                "you can call me Jarvis",
                        }
                },

                {
                        {
                                "hello",
                                "hi"
                        },
                        {
                                "hello",
                                "hi, how are you?",
                                "nice to meet you",
                        }
                },

                {
                        {
                                "how are you"
                        },
                        {
                                "i'm good thank you, how are you?",
                                "fantastic",
                                "life is amazing, so am I"
                        },
                },

                // Default responses for when no keyword matches
                {
                        {},
                        {
                                "I heard you!",
                                "So, you are talking to me",
                                "Continue, I'm listening",
                                "Very interesting conversation",
                                "Please, tell me more.."
                        }
                },
        };
        return knowledge;
    }
}
