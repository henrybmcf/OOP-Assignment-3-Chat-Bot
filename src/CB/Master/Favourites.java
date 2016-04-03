package CB.Master;

import CB.FileCode.FileMethods;

import java.io.*;
import java.util.Scanner;

class Favourites {
    static Object checkLoadFavourite(String favourite, int source) {
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Profiles" + File.separator + ChatBot.name + ".txt"));
            String line;

            while ((line = buffRead.readLine()) != null) {
                String splitLine[] = ConvoContext.splitString(line, ",");

                if (splitLine[0].equalsIgnoreCase(favourite)) {
                    if (source == 0)
                        return true;
                    else if (source == 1)
                        return splitLine[1];
                }
            }
        }
        catch (IOException ex) { FileMethods.fileErrorMessage(); }

        if (source == 0)
            return false;
        else
            return null;
    }

    static void saveNewFave(String faveObject) throws IOException {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        ChatBot.uInput = scanner.nextLine();
        // uInput = Cleaning.cleanInput(uInput);

        try {
            FileWriter profile = new FileWriter(new File("Profiles" + File.separator + ChatBot.name + ".txt"), true);
            profile.write("\n" + faveObject + "," + ChatBot.uInput);
            profile.flush();
            profile.close();

            ChatBot.bOutput = "Okay, I'll remember that..";
        }
        catch (IOException ex) { FileMethods.fileErrorMessage(); }
    }
}
