package CB.Master;

import java.util.ArrayList;

import CB.EditDist.EditDistance;
import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.uInput;
import static CB.Master.ChatBot.understand;

class RepeatCheck {
    private static String botPrevious = "";
    static String userPrevious = "";

    // Save user input for checking if repeating
    static void saveUserResponse(String userCurrent) {
        userPrevious = userCurrent;
    }

    // Save bot's response to check if repeating itself
    static void saveResponse(String botCurrent) {
        botPrevious = botCurrent;
        understand = false;
    }

    // Return true is previous bot response exists and is same as current response
    static boolean botRepeating() {
        return botPrevious.length() > 0 && bOutput.equalsIgnoreCase(botPrevious);
    }

    // Return true is previous bot response exists and is same/similar as current response
    static boolean checkUserRepetition() {
        return userPrevious.length() > 0 && EditDistance.MinimumEditDistance(uInput, userPrevious) <= 2;
    }

    // Check is user is repeating what bot is saying
    static boolean checkUserBotSame() {
        if (uInput.equalsIgnoreCase(bOutput)) {
            bOutput = "That's what I said";
            return true;
        }
        return false;
    }

    // Setup Array list of responses for when user is repeating
    static ArrayList<String> setURepeat() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Why are you repeating yourself?");
        list.add("You just said that");
        list.add("Stop repeating yourself");
        list.add("Sounds awful familiar to what you just said");
        list.add("Didn't you just say that?");
        return list;
    }
}