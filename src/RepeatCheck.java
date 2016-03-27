import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RepeatCheck {
    public static void saveUserResponse(String current) {
        ChatBot.userPrev = current;
    }

    // Save bot's response to check if repeating itself
    public static void saveResponse(String current) {
        ChatBot.bPrevious = current;
        ChatBot.understand = false;
        ChatBot.transposition = false;
    }

    // Check to see if bot is repeating itself
    public static void checkRepeat(int lineIndex) throws IOException {
        if (ChatBot.understand) {
            ArrayList<String> responses = new ArrayList<>();
            String line = " ";

            try {
                FileReader fileReader = new FileReader("Responses" + File.separator + "KnowledgeBase.txt");
                BufferedReader buffRead = new BufferedReader(fileReader);

                // Skip through all lines up to keyword line
                for (int i = 0; i <= lineIndex; i++)
                    line = buffRead.readLine();

                // Skip through anymore keywords
                while((line.charAt(0)) != 'R')
                    line = buffRead.readLine();

                // Go through all the responses
                while((line.charAt(0)) != '#') {
                    responses.add(line);
                    line = buffRead.readLine();
                }

                buffRead.close();
            } catch(IOException ex) { FileMethods.fileErrorMessage(); }

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

            if (!ChatBot.inputChecks()) {
                if (!ChatBot.transposition) {
                    // Loop through words file, to see if input is a word
                    try {
                        if (!ChatBot.uInput.contains(" ")) {
                            FileReader fileReader = new FileReader("Words.txt");
                            BufferedReader buffRead = new BufferedReader(fileReader);
                            String word = buffRead.readLine();

                            while (word != null) {
                                word = buffRead.readLine();
                                // If match found, call function to get user to input phrases and responses
                                if (word.equalsIgnoreCase(ChatBot.uInput)) {
                                    SetupKeyword.setupNewKeyword();
                                    break;
                                }
                            }
                        }
                    } catch (IOException ex) { FileMethods.fileErrorMessage(); }

                    ArrayList<String> responses = new ArrayList<>();

                    try {
                        FileReader fileReader = new FileReader("Responses" + File.separator + "Default Responses.txt");
                        BufferedReader buffRead = new BufferedReader(fileReader);

                        String line = buffRead.readLine();
                        while (line != null) {
                            responses.add(line);
                            line = buffRead.readLine();
                        }

                        buffRead.close();
                    } catch (IOException ex) { FileMethods.fileErrorMessage(); }

                    ChatBot.assignResponse(responses);
                }
            }
        }
    }

    // Return true is previous bot response exists and is same as current response
    public static boolean bRepeating() {
        return ChatBot.bPrevious.length() > 0 && ChatBot.bOutput.equalsIgnoreCase(ChatBot.bPrevious);
    }

    public static boolean checkUserRepetition() {
        return ChatBot.userPrev.length() > 0 && EditDistance.MinimumEditDistance(ChatBot.uInput, ChatBot.userPrev) <= 2;
    }

    public static boolean checkUserBotSame() {
        if (ChatBot.uInput.equalsIgnoreCase(ChatBot.bOutput)) {
            ChatBot.bOutput = "That's what I said";
            return true;
        }
        return false;
    }

    public static ArrayList<String> setURepeat() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Why are you repeating yourself?");
        list.add("You just said that");
        list.add("Stop repeating yourself");
        list.add("Sounds awful familiar to what you just said");
        list.add("Didn't you just say that?");
        return list;
    }
}
