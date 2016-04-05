package CB.Master;

import CB.EditDist.EditDistance;

import java.util.Date;

import static CB.Master.ChatBot.uInput;
import static CB.Master.ChatBot.bOutput;

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
        return checkDate() || checkFavourite() || aggressiveCheck();
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

    static boolean aggressiveCheck() {
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
}
