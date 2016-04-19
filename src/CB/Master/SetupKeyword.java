package CB.Master;

import CB.Visuals.Visual;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static CB.Master.ChatBot.uInput;
import static CB.Master.Cleaning.output;
import static CB.Master.ChatBot.waiting;

class SetupKeyword {
    static void setupNewKeyword() throws IOException {
        output("Hmm, I don't think I know this keyword.\nCould you give me a few example phrases and responses so I know in the future?\nYes or no will do");
        //System.out.println("Hmm, I don't think I know this keyword.\nCould you give me a few example phrases and responses so I know in the future?");
//        System.out.print("Yes or no will do\n> ");
        //Scanner scanner = new Scanner(System.in);

        waiting();
        //Visual.waitingIn = true;

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();
        String confirm = checkConfirm();

        if (confirm.equalsIgnoreCase("yes")) {
            do {
//                System.out.print("What would be a typical phrase/question?\n> ");
//                keys.add(Cleaning.cleanInput(scanner.nextLine()));
                output("What would be a typical phrase/question?");
                waiting();
                keys.add(uInput);

//                System.out.print("And a response?\n> ");
//                responses.add(Cleaning.cleanInput(scanner.nextLine()));
                output("And a response?");
                waiting();
                responses.add(uInput);

//                System.out.print("Anymore?\n> ");
                output("Anymore?");
                waiting();

                confirm = checkConfirm();
            }
            while (confirm.equalsIgnoreCase("yes"));

            File log = new File("Data" + File.separator + "KnowledgeBase.txt");
            FileWriter conLog = new FileWriter(log, true);
            for (String key : keys)
                conLog.write("\nK" + key);
            for (String resp : responses)
                conLog.write("\nR" + resp);
            conLog.write("\n#");
            conLog.flush();
            conLog.close();

            ChatBot.bOutput = "Thanks bae!";
        }
        else {
            ChatBot.understand = true;
            ChatBot.grabResponses("Aggressive", 0);
        }
    }

    private static String checkConfirm() {
        String check = uInput;
        while(!check.equalsIgnoreCase("yes") && !check.equalsIgnoreCase("no")) {
            output("No comprende amigo. Yes or no please.");
            waiting();
            check = uInput;
            Visual.waitingIn = true;
        }

        return check;
    }
}