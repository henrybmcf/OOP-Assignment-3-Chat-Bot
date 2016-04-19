package CB.Master;

import java.io.*;

import static CB.FileCode.FileMethods.fileErrorMessage;
import static CB.FileCode.FileMethods.saveLog;
import static CB.Master.ChatBot.*;

class Favourites {
    static Object checkLoadFavourite(String favouriteTopic, int source) {
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Profiles" + File.separator + name + ".txt"));
            String line;

            buffRead.readLine();
            buffRead.readLine();

            while ((line = buffRead.readLine()) != null) {
                String splitLine[] = ConvoContext.splitString(line, ",");

                if (splitLine[0].equalsIgnoreCase(favouriteTopic)) {
                    if (source == 0)
                        return true;
                    else if (source == 1)
                        return splitLine[1];
                }
            }
        }
        catch (IOException ex) { fileErrorMessage(); }

        if (source == 0)
            return false;
        else
            return null;
    }

    static void saveNewFeel(String faveObject) {
        ChatBot.waiting();
        saveLog(conLog, userName, uInput);

        try {
            FileWriter profile = new FileWriter(new File("Profiles" + File.separator + name + ".txt"), true);
            profile.write("\n" + faveObject + "," + uInput);
            profile.flush();
            profile.close();

            Cleaning.output("Okay, I'll remember that..");
        }
        catch (IOException ex) { fileErrorMessage(); }
    }
}