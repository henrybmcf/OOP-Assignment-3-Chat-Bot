package CB.Master;

import CB.EditDist.EditDistance;

import java.util.Date;

import static CB.Master.ChatBot.uInput;
import static CB.Master.ChatBot.bOutput;
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
            ChatBot.searchKeyword("Favourites", 2);
            return true;
        }
        return false;
    }

    private static boolean aggressiveCheck() {
        int line = ChatBot.searchKeyword("Aggressive", 1);
        if (line > 0) {
            RepeatCheck.checkRepeat("Aggressive", line);
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
}
