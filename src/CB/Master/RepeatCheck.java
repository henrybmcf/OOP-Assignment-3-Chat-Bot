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

public class RepeatCheck {
    private static String bPrevious = "";
    public static String userPrev = "";

    static void saveUserResponse(String userCurrent) {
        userPrev = userCurrent;
    }

    // Save bot's response to check if repeating itself
    static void saveResponse(String botCurrent) {
        bPrevious = botCurrent;
        understand = false;
        transposition = false;
    }

    // Check to see if bot is repeating itself
    static void checkRepeat(String fileName, int lineIndex) {
        if (understand) {
            ArrayList<String> responses = new ArrayList<>();
            String line = " ";

            try {
                FileReader fileReader = new FileReader("Data" + File.separator + fileName + ".txt");
                BufferedReader buffRead = new BufferedReader(fileReader);

                // Skip through all lines up to keyword line
                for (int i = 0; i <= lineIndex; i++)
                    line = buffRead.readLine();

                // Skip through anymore keywords until at responses
                while((line.charAt(0)) != 'R')
                    line = buffRead.readLine();

                // Go through all the responses
                while((line.charAt(0)) != '#') {
                    responses.add(line);
                    line = buffRead.readLine();
                }

                buffRead.close();
            } catch(IOException ex) { fileErrorMessage(); }

            ChatBot.assignResponse(responses);
        }
        else {
            // TODO split string when substring found, replace substring, concat string back together
            // TODO loop through string, splitting into number of elements of string to be replaced (i.e. You = 3), see if these match the string.
            // TODO then do the split and concat

            // Need this?
//            uInputBackup = uInput;
//
//            checkWithin:
//            for (int i = 0; i < transposeList.length; i++) {
//                // Split input backup when match to transpose list item found
//                // TODO Need to change to make sure ' ' (space) before and after transposition
//
//                StringBuilder checking = new StringBuilder(transposeList[i][0].length());
//
//                // Add space at start
//                checking.insert(0, " ");
//                // Add space at end
////                checking.insert(checking.length() + 1, " ");
//
//                for (int j = 0; j < uInputBackup.length(); j++) {
//
//                    if (j + transposeList[i][0].length() + 2 < uInputBackup.length()) {
//                        checking.insert(1, uInputBackup.substring(j, j + transposeList[i][0].length()));
//
//                        // checking.insert(checking.length() + 1, " ");
//
//                        // check = uInputBackup.substring(j, j + transposeList[i][0].length() + 2);
//                    }
////                    else if (j + transposeList[i][0].length() + 1 < uInputBackup.length()) {
////                        checking.insert(1, uInputBackup.substring(j, j + transposeList[i][0].length() + 1));
////
////                        checking.insert(checking.length() + 1, " ");
////                       // check = uInputBackup.substring(j, j + transposeList[i][0].length() + 1);
////                    }
//                    else if (j + transposeList[i][0].length() > uInputBackup.length()) {
//                        break;
//                    }
//
//                    // Convert back to string to check if contains transposeList item
//                    String check = checking.toString();
//
//                    // Trim removes white space at start and end
//                    if (check.trim().equals(transposeList[i][0])) {
//                        // If contains, then call function to split up input.
//
//                        // j is position within input
//
//                        splitInput(uInputBackup, i);
//                        break checkWithin;
//                    }
//                }
//            }

            // If input hasn't been transposed

            if (!Checks.inputChecks()) {
                if (!transposing()) {
                    // Loop through words file, to see if input is a word
                    try {
                        if (!uInput.contains(" ")) {
                            FileReader fileReader = new FileReader("Data" + File.separator + "Words.txt");
                            BufferedReader buffRead = new BufferedReader(fileReader);
                            String word = buffRead.readLine();

                            while (word != null) {
                                word = buffRead.readLine();
                                // If match found, call function to get user to input phrases and responses
                                if (word.equalsIgnoreCase(uInput)) {
                                    SetupKeyword.setupNewKeyword();
                                    break;
                                }
                            }
                        }
                    } catch (IOException ex) { fileErrorMessage(); }

                    ArrayList<String> responses = new ArrayList<>();

                    try {
                        FileReader fileReader = new FileReader("Data" + File.separator + "Default Responses.txt");
                        BufferedReader buffRead = new BufferedReader(fileReader);

                        String line = buffRead.readLine();
                        while (line != null) {
                            responses.add(line);
                            line = buffRead.readLine();
                        }

                        buffRead.close();
                    } catch (IOException ex) { fileErrorMessage(); }

                    ChatBot.assignResponse(responses);
                }
            }
        }
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
        return bPrevious.length() > 0 && bOutput.equalsIgnoreCase(bPrevious);
    }

    static boolean checkUserRepetition() {
        return userPrev.length() > 0 && EditDistance.MinimumEditDistance(uInput, userPrev) <= 2;
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