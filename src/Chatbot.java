/**
 * Created by HenryBallingerMcFarlane on 23/02/2016.
 */

import java.util.*;
import java.util.Random;

public class Chatbot {
    public static String uInput;
    public static String bOutput;
    public static boolean respond;
    public static boolean exit;

    // First line is the key word/phrase
    // The rest are the responses
    static String[][][] knowledge = {
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
                            "hello.",
                            "hi, how are you?",
                            "nice to meet you",
                    }
            },

            // Default responses for when no keyword matches
            {
                    {
                            "I heard you!",
                            "So, you are talking to me.",
                            "Continue, I'm listening",
                            "Very interesting conversation.",
                            "Please, tell me more.."
                    }
            },
    };

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

        do {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            uInput = scanner.nextLine().toLowerCase();

            // Check user input against goodbye strings to see if program should exit
            int i = 0;
            while (i != goodbye.length) {
                if (Levenshtein.MinimumEditDistance(uInput, goodbye[i]) < 2) {
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
                search:
                for (j = 0; j < knowledge.length - 1; j++) {
                    for (k = 0; k < knowledge[j][0].length; k++) {
                        if (Levenshtein.MinimumEditDistance(uInput, knowledge[j][0][k]) < 3) {
                            respond = true;
                            break search;
                        }
                    }
                }

                int response;
                Random rand = new Random();
                if (respond)
                    response = rand.nextInt(knowledge[j][1].length);
                else
                    response = rand.nextInt(knowledge[knowledge.length - 1].length);
                bOutput = knowledge[j][1][response];
                bOutput = InitCap(bOutput);
                System.out.println(bOutput);
            }
        }
        while (true);
    }

    // Capitalise first letter of string
    public static String InitCap(String str) {
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }
}
