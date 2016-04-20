package CB.Master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static CB.Master.ChatBot.*;
import static CB.Master.Cleaning.output;
import static CB.Visuals.Visual.waitingIn;

class SetupKeyword {
    // If bot doesn't know a keyword/phrase, user can teach it and bot wil remember for future conversations
    static void setupNewKeyword() throws IOException {
        output("Hmm, I don't think I know this keyword.\nCould you give me a few example phrases and responses so I know in the future?\nYes or no will do");
        // Get user response for it wants to setup new keyword
        waiting();

        // Array lists for keywords and responses to be saved
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();
        String confirm = checkConfirm();

        if (confirm.equalsIgnoreCase("yes")) {
            do {
                // Get keyword and response from user
                output("What would be a typical phrase/question?");
                waiting();
                keys.add(uInput);
                output("And a response?");
                waiting();
                responses.add(uInput);
                output("Anymore?");
                waiting();

                confirm = checkConfirm();
            }
            while (confirm.equalsIgnoreCase("yes"));

            // Once no more keywords to be added, write to knowledge database
            File log = new File("Data" + File.separator + "KnowledgeBase.txt");
            FileWriter conLog = new FileWriter(log, true);
            for (String key : keys)
                conLog.write("\nK" + key);
            for (String resp : responses)
                conLog.write("\nR" + resp);
            conLog.write("\n#");
            conLog.flush();
            conLog.close();

            bOutput = "Thanks bae!";
        }
        else {
            understand = true;
            grabResponses("Aggressive", 0);
        }
    }

    // Check user input is yes or no
    private static String checkConfirm() {
        String check = uInput;
        while(!check.equalsIgnoreCase("yes") && !check.equalsIgnoreCase("no")) {
            output("No comprende amigo. Yes or no please.");
            waiting();
            check = uInput;
            waitingIn = true;
        }
        return check;
    }
}