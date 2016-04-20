package CB.Master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import CB.FileCode.FileMethods;

import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.uInput;
import static CB.Master.Cleaning.splitString;
import static CB.Master.RepeatCheck.userPrevious;

class ConvoContext {
    private final static String context = "Why like";
    private final static String contextSubj = "it this that them that";

    // Check if user is talking in context to a previously mentioned favourite
    static boolean favouriteContextChecks() {
        if (checkContextWordMatch()) {
            inContext();
            return true;
        }
        return false;
    }

    // Check is user input contains enough matches to context strings
    private static boolean checkContextWordMatch() {
        int matchCounter = 0;
        int subCounter = 0;
        String[] inTokens = splitString(uInput, " ");
        String[] conTokens = splitString(context, " ");
        String[] subTokens = splitString(contextSubj, " ");
        for (String conToken : conTokens) {
            for (String inToken : inTokens) {
                if (conToken.equalsIgnoreCase(inToken))
                    matchCounter++;
            }
        }

        for (String inToken : inTokens) {
            for (String subToken : subTokens) {
                if (subToken.equalsIgnoreCase(inToken))
                    subCounter++;
            }
        }

        return matchCounter == 2 && subCounter >= 1;
    }

    // Check if user is talking in context
    private static void inContext() {
        if (userPrevious.contains("favourite")) {
            String line;
            try {
                BufferedReader buffRead = new BufferedReader(new FileReader("Responses" + File.separator + "Favourites.txt"));

                while ((line = buffRead.readLine()) != null) {
                    line = line.substring(1);

                    if (userPrevious.contains(line)) {
                        buffRead.readLine();
                        buffRead.readLine();
                        bOutput = buffRead.readLine().substring(1);
                        return;
                    }
                }
            }
            catch (IOException ex) { FileMethods.fileErrorMessage(); }
        }

        bOutput = "What are you talking about??";
    }
}