package CB.Master;

import CB.EditDist.EditDistance;

import java.util.Date;

class Checks {
    static boolean inputChecks() {
        return checkDate() || checkFavourite();
    }

    private static boolean checkDate() {
        if (ChatBot.uInput.contains("date") || ChatBot.uInput.contains("time") || ChatBot.uInput.contains("day")) {
            ChatBot.bOutput = "The date and time is " + new Date().toString().substring(0, 19);
            return true;
        }
        return false;
    }

    private static boolean checkFavourite() {
        if (ChatBot.uInput.contains("favourite")) {
            ChatBot.searchKeyword("Favourites", 2);
            return true;
        }
        return false;
    }
}
