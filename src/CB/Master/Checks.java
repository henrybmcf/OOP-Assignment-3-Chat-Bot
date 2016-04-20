package CB.Master;

import CB.EditDist.EditDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static CB.FileCode.FileMethods.fileErrorMessage;
import static CB.Master.ChatBot.uInput;
import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.understand;

class Checks {
    // List of possible user inputs to end the conversation
    private final static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    static boolean inputChecks() { return checkDate() || checkFavourite() || aggressiveCheck(); }

    private static boolean checkDate() {
        if (uInput.contains("date") || uInput.contains("time") || uInput.contains("day")) {
            bOutput = "The date and time is " + new Date().toString().substring(0, 19);
            return true;
        }
        return false;
    }

    private static boolean checkFavourite() {
        if (uInput.contains("favourite")) {
            ChatBot.searchKeyword("Favourites", 2);
            return true;
        }
        return false;
    }

    private static boolean aggressiveCheck() {
        int line = ChatBot.searchKeyword("Aggressive", 1);

        if (line > 0 && understand) {
                ChatBot.grabResponses("Aggressive", line);
                return true;
        }
        return false;
    }

    // Check user input against goodbye strings to see if program should exit
    static boolean exitCheck() {
        int i = 0;
        while (i != goodbye.length) {
            if (EditDistance.MinimumEditDistance(uInput, goodbye[i]) < 2 || uInput.contains(goodbye[i]))
                return true;
            i++;
        }
        return false;
    }

    static void checkWordValidity() {
        // Loop through words file, to see if input is a word
        // If so, setup new keyword
        try {
            int wordCount = 0;

            // Split input on spaces ("The sky is blue" -> {"the", "sky", "is", "blue"})
            String[] tokens = ConvoContext.splitString(uInput, " ");

            // Load list of English words
            FileReader fileReader = new FileReader("Data" + File.separator + "Words.txt");
            BufferedReader buffRead = new BufferedReader(fileReader);
            String word;

            while ((word = buffRead.readLine()) != null) {
               // System.out.println(word);
                // If match found, call function to get user to input phrases and responses

                // Loop through tokens to check if each is a word, if so, increment counter
                for (String token : tokens) {
                    if (word.equalsIgnoreCase(token))
                        wordCount++;
                }

                // If all words have been verified, call setup new keyword function
                if (wordCount == tokens.length) {
                    SetupKeyword.setupNewKeyword();
                    return;
                }
            }

            // If not all tokens are words, call default "do not understand" responses
            if (wordCount != tokens.length)
                assignDefault();
        } catch (IOException ex) { fileErrorMessage(); }
    }

    private static void assignDefault() {
        ArrayList<String> responses = new ArrayList<>();

        // Assign default response (for when bot doesn't understand user)
        try {
            FileReader fileReader = new FileReader("Data" + File.separator + "Default Responses.txt");
            BufferedReader buffRead = new BufferedReader(fileReader);

            String line = buffRead.readLine();
            while (line != null) {
                responses.add(line);
                line = buffRead.readLine();
            }

            buffRead.close();
        } catch (IOException ex) { fileErrorMessage(); }

        ChatBot.assignResponse(responses);
    }
}