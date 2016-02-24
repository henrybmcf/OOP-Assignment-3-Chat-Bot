/**
 * Created by HenryBallingerMcFarlane on 23/02/2016.
 */

import java.util.*;
import java.util.Random;

public class Chatbot {
    public static String uInput;
    public static String bOutput;

    public static String bPrevious = "";

    public static boolean respond;
    public static boolean exit;

    // List of punctuations marks
    final static String punctuation = "?!.;";

    // Set up the knowledge database
    public static String[][][] knowledge = setKnowledge();

    // List of possible user inputs to end the conversation
    static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    public static void main(String[] args) {
        System.out.println("Hello!");
        saveResponse("Hello");

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
                int j, k;
                int small1 = 0, small2 = 0;

                search:
                for (j = 0; j < knowledge.length - 1; j++) {
                    for (k = 0; k < knowledge[j][0].length; k++) {

                        /* TODO Change to find keyword with smallest Edit Distance instead of first one that matches. */

                        // Find the closest matching keyword (that with the smallest edit distance)
                        float x = EditDistance.MinimumEditDistance(uInput, knowledge[j][0][k]);
                        float s = EditDistance.MinimumEditDistance(uInput, knowledge[small1][0][small2]);
                        if (x < s) {
                            small1 = j;
                            small2 = k;
                        }
//                        if (EditDistance.MinimumEditDistance(uInput, knowledge[j][0][k]) < 3) {
//                            respond = true;
//                            break search;
//                        }
                    }
                }

                // Check to see if closest matching keyword is close enough match
                if (EditDistance.MinimumEditDistance(uInput, knowledge[small1][0][small2]) < 3)
                    respond = true;

                if (respond) {
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
                    bOutput = assignResponse(knowledge.length - 1, 1, knowledge[knowledge.length - 1].length);

                    if (bRepeating()) {
                        do {
                            bOutput = assignResponse(knowledge.length - 1, 1, knowledge[knowledge.length - 1].length);
                        }
                        while (bRepeating());
                    }
                }

                bOutput = initCap(bOutput);

                saveResponse(bOutput);

                System.out.println(bOutput);
            }
        }
        while (true);
    }

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
        respond = false;
    }

    // Return true is previous bot response exists and is same as current response
    public static boolean bRepeating() {
        return bPrevious.length() > 0 && bOutput.equalsIgnoreCase(bPrevious);
    }

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
                                "So, you are talking to me.",
                                "Continue, I'm listening",
                                "Very interesting conversation.",
                                "Please, tell me more.."
                        }
                },
        };
        return knowledge;
    }
}
