package CB.Master;

import java.io.*;
import java.util.Scanner;

import static CB.FileCode.FileMethods.fileErrorMessage;
import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.name;
import static CB.Master.ChatBot.uInput;

class Favourites {
    static Object checkLoadFavourite(String favouriteTopic, int source) {
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Profiles" + File.separator + name + ".txt"));
            String line;

            buffRead.readLine();
            buffRead.readLine();

            while ((line = buffRead.readLine()) != null) {
                String splitLine[] = ConvoContext.splitString(line, ",");

                if (splitLine[1].equalsIgnoreCase(favouriteTopic)) {
                    if (source == 0)
                        return true;
                    else if (source == 1)
                        return splitLine[2];
                }

                // For check truth method
//                if (source == 2) {
//                    if (splitLine[1].equalsIgnoreCase(favouriteTopic))
//                        return splitLine[0] + "," + splitLine[1];
//                }
            }
        }
        catch (IOException ex) { fileErrorMessage(); }

        if (source == 0)
            return false;
        else
            return null;
    }

    static void saveNewFeel(String feeling, String faveObject) {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        uInput = scanner.nextLine();

        try {
            FileWriter profile = new FileWriter(new File("Profiles" + File.separator + name + ".txt"), true);

            //profile.write("\n" + faveObject + "," + uInput);
            profile.write("\n" + feeling + "," + faveObject + "," + uInput);

            profile.flush();
            profile.close();

            bOutput = "Okay, I'll remember that..";
        }
        catch (IOException ex) { fileErrorMessage(); }
    }
}