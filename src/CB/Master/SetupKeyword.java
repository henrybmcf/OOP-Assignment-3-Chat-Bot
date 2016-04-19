package CB.Master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class SetupKeyword {
    static void setupNewKeyword() throws IOException {
        System.out.println("Hmm, I don't think I know this keyword.\nCould you give me a few example phrases and responses so I know in the future?");
        System.out.print("Yes or no will do\n> ");
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();
        String confirm = checkConfirm();

        if (confirm.equalsIgnoreCase("yes")) {
            do {
                System.out.print("What would be a typical phrase/question?\n> ");
                keys.add(Cleaning.cleanInput(scanner.nextLine()));
                System.out.print("And a response?\n> ");
                responses.add(Cleaning.cleanInput(scanner.nextLine()));
                System.out.print("Anymore?\n> ");

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
            ChatBot.grabResponses("Aggressive", 0, 'K', false);
        }
    }

    private static String checkConfirm() {
        Scanner scanner = new Scanner(System.in);
        String check = scanner.nextLine();
        while(!check.equalsIgnoreCase("yes") && !check.equalsIgnoreCase("no")) {
            System.out.print("No comprende amigo. Yes or no please.\n> ");
            check = scanner.nextLine();
        }
        return check;
    }
}