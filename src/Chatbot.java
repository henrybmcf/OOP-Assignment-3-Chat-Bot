/**
 * Created by HenryBallingerMcFarlane on 23/02/2016.
 */

import java.util.*;
import java.util.Random;

public class Chatbot {
    public static String uInput;
    public static String bOutput = "";
    public static boolean respond;
    public static boolean exit;

    // First line is the key word/phrase
    // The rest are the responses
    static String[][] knowledge = {
            {
                    "what is your name",
                    "what would you like my name to be?",
                    "why do you want to know?",
                    "you can call me Jarvis",
            },

            {
                    "hello",
                    "hello.",
                    "hi, how are you?",
                    "nice to meet you",
            },

            {
                    "I heard you!",
                    "So, you are talking to me.",
                    "Continue, I'm listening",
                    "Very interesting conversation.",
                    "Please, tell me more.."
            },
    };

    // List of possible user inputs to end the conversation
    static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go"
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
                int j;
                for (j = 0; j < knowledge.length - 1; j++) {
                    if (Levenshtein.MinimumEditDistance(uInput, knowledge[j][0]) < 3) {
                        respond = true;
                        break;
                    }
                }

                int response;
                Random rand = new Random();
                if (respond)
                    response = rand.nextInt(knowledge[j].length - 1) + 1;
                else
                    response = rand.nextInt(knowledge[knowledge.length - 1].length);
                bOutput = knowledge[j][response];
                bOutput = bOutput.substring(0, 1).toUpperCase() + bOutput.substring(1);
                System.out.println(bOutput);
            }
        }
        while (true);
    }
}
