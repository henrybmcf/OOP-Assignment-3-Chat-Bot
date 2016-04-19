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
import static CB.Master.Cleaning.output;

class Checks {
    // List of possible user inputs to end the conversation
    private final static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    static boolean inputChecks() {
        return checkDate() || checkFavourite();// || aggressiveCheck();
    }

    private static boolean checkDate() {
        if (uInput.contains("date") || uInput.contains("time") || uInput.contains("day")) {
            bOutput = "The date and time is " + new Date().toString().substring(0, 19);
            return true;
        }
        return false;
    }

    private static boolean checkFavourite() {
        if (uInput.contains("favourite")) {
            ChatBot.searchKeyword("Favourites", 2, false);
            return true;
        }
        return false;
    }

    private static boolean aggressiveCheck() {
        int line = ChatBot.searchKeyword("Aggressive", 1, false);

        if (line > 0 && understand) {
                ChatBot.grabResponses("Aggressive", line, 'K', false);
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


    private final static String[] likeVocab = {
            "love",
            "like",
            "enjoy"
    };
    private final static String[] dislikeVocab = {
            "hate",
            "dislike",
            "do not like",
            "do not enjoy"
    };

    @SuppressWarnings("ConstantConditions")
    static boolean checkTruth(String str, String subj) {
        boolean like = false;
        boolean dis = false;

        for (String pos : likeVocab) {
            if (str.contains(pos))
                like = true;
        }
        for (String neg : dislikeVocab) {
            if (str.contains(neg))
                dis = true;
        }

        if (like || dis) {
            String fav;

            if (Favourites.checkLoadFavourite(subj, 2) != null) {
                fav = Favourites.checkLoadFavourite(subj, 2).toString();

                String splitLine[] = ConvoContext.splitString(fav, ",");

                String lieCatch = "";
                if (like)
                    lieCatch = "you're lying, you said that you didn't like " + splitLine[1] + ". Don't lie to me!";
                else if (dis)
                    lieCatch = "you're lying, you said that you're favourite " + splitLine[0] + " is " + splitLine[1] + ". I hate it when you lie to me!";

                output(lieCatch, 1);

                return false;
            }
        }
        return true;
    }


    // Check to see if bot is repeating itself
    static void checkWordValidity() {
//            // TODO split string when substring found, replace substring, concat string back together
//            // TODO loop through string, splitting into number of elements of string to be replaced (i.e. You = 3), see if these match the string.
//            // TODO then do the split and concat
//
//            // Need this?
////            uInputBackup = uInput;
////
////            checkWithin:
////            for (int i = 0; i < transposeList.length; i++) {
////                // Split input backup when match to transpose list item found
////                // TODO Need to change to make sure ' ' (space) before and after transposition
////
////                StringBuilder checking = new StringBuilder(transposeList[i][0].length());
////
////                // Add space at start
////                checking.insert(0, " ");
////                // Add space at end
//////                checking.insert(checking.length() + 1, " ");
////
////                for (int j = 0; j < uInputBackup.length(); j++) {
////
////                    if (j + transposeList[i][0].length() + 2 < uInputBackup.length()) {
////                        checking.insert(1, uInputBackup.substring(j, j + transposeList[i][0].length()));
////
////                        // checking.insert(checking.length() + 1, " ");
////
////                        // check = uInputBackup.substring(j, j + transposeList[i][0].length() + 2);
////                    }
//////                    else if (j + transposeList[i][0].length() + 1 < uInputBackup.length()) {
//////                        checking.insert(1, uInputBackup.substring(j, j + transposeList[i][0].length() + 1));
//////
//////                        checking.insert(checking.length() + 1, " ");
//////                       // check = uInputBackup.substring(j, j + transposeList[i][0].length() + 1);
//////                    }
////                    else if (j + transposeList[i][0].length() > uInputBackup.length()) {
////                        break;
////                    }
////
////                    // Convert back to string to check if contains transposeList item
////                    String check = checking.toString();
////
////                    // Trim removes white space at start and end
////                    if (check.trim().equals(transposeList[i][0])) {
////                        // If contains, then call function to split up input.
////
////                        // j is position within input
////
////                        splitInput(uInputBackup, i);
////                        break checkWithin;
////                    }
////                }
////            }
//
//            // If input hasn't been transposed

       // if (!transposing()) {
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
       // }
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