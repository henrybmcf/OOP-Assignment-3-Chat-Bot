package CB.Master;

import CB.EditDist.EditDistance;
import CB.FileCode.FileMethods;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static CB.FileCode.FileMethods.fileErrorMessage;
import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.transposition;
import static CB.Master.ChatBot.uInput;
import static CB.Master.ChatBot.understand;

class RepeatCheck {
    private static String botPrevious = "";
    static String userPrevious = "";

    static void saveUserResponse(String userCurrent) {
        userPrevious = userCurrent;
    }

    // Save bot's response to check if repeating itself
    static void saveResponse(String botCurrent) {
        botPrevious = botCurrent;
        understand = false;
        transposition = false;
    }

    private final static String[] trans = {"you are", "youre"};
    private final static String[] transRep = {"You think I'm", "!? Well, "};

    private static boolean transposing() {
        StringBuilder str = new StringBuilder(uInput.length());
        StringBuilder subject = new StringBuilder(uInput.length());


        for (String tr : trans) {
            int in = uInput.indexOf(tr);

            int end = uInput.indexOf(",");
            if (end == -1) end = uInput.length();

            if (in != -1) {
                str.append(transRep[0]);
                str.append(uInput.substring(in + tr.length(), end));
                str.append(transRep[1]);
                understand = true;
                //checkRepeat("Aggressive", 0);
                str.append(bOutput);

                bOutput= str.toString();
                return true;
            }
        }
        return false;
    }

    // Return true is previous bot response exists and is same as current response
    static boolean botRepeating() {
        return botPrevious.length() > 0 && bOutput.equalsIgnoreCase(botPrevious);
    }

    static boolean checkUserRepetition() {
        return userPrevious.length() > 0 && EditDistance.MinimumEditDistance(uInput, userPrevious) <= 2;
    }

    static boolean checkUserBotSame() {
        if (uInput.equalsIgnoreCase(bOutput)) {
            bOutput = "That's what I said";
            return true;
        }
        return false;
    }

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