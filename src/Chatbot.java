/**
 * Assignment 3 - Object Orientated Programming - ChatBot
 * Team: Henry Ballinger McFarlane & Lok-Woon Wan
 */

import java.util.*;
import java.util.Random;
import java.io.*;

// TODO Introduce timer feature, so if user is idle for x seconds, generate prompt, ex: "Are you still there?"

public class ChatBot {
    public static String uInput;
    public static String bOutput;

    public static String uInputBackup = "";

    public static String bPrevious = "";
    public static String userPrev = "";

    public static boolean understand;
    public static boolean transposition = false;
    public static boolean exit;

    // List of punctuations marks
    final static String punctuation = "?!.;";

//    public final static String transposeList[][] = {
//            {"i'm", "you're"},
//            {"i am", "you are"},
//            {"you are", "i am"},
//            {"am", "are"},
//            {"were", "was"},
//            {"me", "you"},
//            {"yours", "mine"},
//            {"your", "my"},
//            {"i've", "you've"},
//            {"i", "you"},
//            {"aren't", "am not"},
//            {"weren't", "wasn't"},
//            {"i'd", "you'd"},
//            {"dad", "father"},
//            {"mum", "mother"},
//            {"myself", "yourself"}
//    };

    protected final static String[] salutations = {
            "Hello there!",
            "Hi, how are you?",
            "Such a nice day today!"
    };

    // List of possible user inputs to end the conversation
    protected final static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    protected final static ArrayList<String> userRepition = setURepeat();

    public static ArrayList<String> setURepeat() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Why are you repeating yourself?");
        list.add("You just said that");
        list.add("Stop repeating yourself");
        list.add("Sounds awful familiar to what you just said");
        list.add("Didn't you just say that?");
        return list;
    }

    public static void main(String[] args) throws IOException {
        Date dateUF = new Date();
        String date = dateUF.toString().replace(":", "_");
        File log = new File("Conversation Logs" + File.separator + date + ".txt");
        FileWriter conLog = new FileWriter(log, true);
        conLog.write(dateUF.toString() + "\n\n");

        bOutput = assignSalutation();
        saveResponse(bOutput);
        System.out.println(bOutput);

        do {
            // Write the bot's response to the conversation log file
            saveLog(conLog, "Bot: ", bOutput);
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            uInput = scanner.nextLine();
            // Remove unwanted white space and punctuation and convert to lower case
            uInput = clean(uInput);

            // Write the user's response to the conversation log file
            saveLog(conLog, "User: ", uInput);

            // Check user input against goodbye strings to see if program should exit
            int i = 0;
            while (i != goodbye.length) {
                if (EditDistance.MinimumEditDistance(uInput, goodbye[i]) < 2) {
                    exit = true;
                    break;
                }
                i++;
            }

            if (exit) {
                System.out.println("Goodbye, it was nice talking to you.");
                break;
            }
            else {
                if (checkUserRepetition())
                    assignResponse(userRepition);
                else
                    checkRepeat(searchKeyword());

                bOutput = initCap(bOutput);
                saveResponse(bOutput);
                System.out.println(bOutput);

                saveUserResponse(uInput);
            }
        }
        while (true);

        conLog.write("\n\n---- End of conversation ----");
        conLog.flush();
        conLog.close();
    }

    public static void saveLog(FileWriter log, String ID, String str) throws IOException {
        log.write(ID + str + "\n");
    }

    // Search knowledge database for match to user input
    public static int searchKeyword() {
        String line;
        String smallest = " ";
        int lineCount = 0;

        try {
            FileReader fileReader = new FileReader("Responses" + File.separator + "KnowledgeBase.txt");
            BufferedReader buffRead = new BufferedReader(fileReader);

            // Skip to second line (First keyword)
            //line = buffRead.readLine();
           // System.out.println("line 1 = " + line);

            //lineCount++;
           // smallest = line;

            float small = EditDistance.MinimumEditDistance(uInput, buffRead.readLine().substring(1));

            searching:
            while((line = buffRead.readLine()) != null) {
                lineCount++;
                switch(line.charAt(0)) {
                    case 'K':
                      //  System.out.println("this line = " + line);
                       // System.out.println(lineCount);
                        line = line.substring(1);
                        float dist = EditDistance.MinimumEditDistance(uInput, line);
                        if (dist < small) {
                            smallest = line;

                            if (dist == 0)
                                break searching;
                        }
                        break;

                    default:
                        break;
                }
            }

            // Check to see if closest matching keyword is close enough match
            understand = (EditDistance.MinimumEditDistance(uInput, smallest) <= 1) && !understand;

            buffRead.close();
        }
        catch(FileNotFoundException ex) { System.out.println("Unable to open file"); }
        catch(IOException ex) { System.out.println("Error reading file"); }

        return lineCount;
    }

    public static void fileErrorMessage() {
        bOutput = "I seem to be having some trouble accessing my knowledge....";
    }

    // Check to see if bot is repeating itself
    public static void checkRepeat(int lineIndex) throws IOException {
        if (understand) {
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
            } catch(IOException ex) { fileErrorMessage(); }

            assignResponse(responses);
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
            if (!transposition) {
                ArrayList<String> responses = new ArrayList<>();

                try {
                    FileReader fileReader = new FileReader("Responses" + File.separator + "Default Responses.txt");
                    BufferedReader buffRead = new BufferedReader(fileReader);

                    String line = buffRead.readLine();
                    while(line != null) {
                        responses.add(line);
                        line = buffRead.readLine();
                    }

                    buffRead.close();
                } catch(IOException ex) { fileErrorMessage(); }

                assignResponse(responses);
            }
        }
    }

    // Select random bot response
    public static void assignResponse(ArrayList<String> responsesList) {
        do {
            Collections.shuffle(responsesList);
            bOutput = responsesList.get(0);
        }
        while (bRepeating());
    }

//    public static void splitInput(String str, int index) {
//        String[] tokens = str.split(transposeList[index][0]);
//
//        // If backup has been split (more then 1 element)
//        if (tokens.length > 1) {
//            str = "";
//
//            // For each token (section of input backup), add it + new transposition to input backup
//            for (String token : tokens) {
//                str = transposeList[index][1].concat(token);
//            }
//
//            bOutput = str;
//            transposition = true;
//        }
//    }

    // Clean up user input: Remove white space and punctuation & convert to lower case
    public static String clean(String str) {
            StringBuilder cleaning = new StringBuilder(str.length());

            char prevChar = 0;

            for(int i = 0; i < str.length(); i++) {
                if((str.charAt(i) == ' ' && prevChar == ' ') || !puncCheck(str.charAt(i))) {
                    cleaning.append(str.charAt(i));
                    prevChar = str.charAt(i);
                }
                else if(prevChar != ' ' && puncCheck(str.charAt(i))) {
                    cleaning.append(' ');
                }

            }
            str = cleaning.toString().toLowerCase();
            return str;
    }

    // indexOf returns -1 if the character does not occur in the string
    // If ch is not in punctuation, -1 will be returned (true)
    public static boolean puncCheck(char ch) {
        return punctuation.indexOf(ch) != -1;
    }

    // Capitalise first letter of string
    public static String initCap(String str) {
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }

    // Save bot's response to check if repeating itself
    public static void saveResponse(String current) {
        bPrevious = current;
        understand = false;
        transposition = false;
    }

    // Return true is previous bot response exists and is same as current response
    public static boolean bRepeating() {
        return bPrevious.length() > 0 && bOutput.equalsIgnoreCase(bPrevious);
    }

    // Select random salutation from array
    public static String assignSalutation() {
        Random rand = new Random();
        return salutations[rand.nextInt(salutations.length)];
    }

    public static void checkUserBotSame() {

    }

    public static void saveUserResponse(String current) {
        userPrev = current;
    }

    public static boolean checkUserRepetition() {
        return userPrev.length() > 0 && EditDistance.MinimumEditDistance(uInput, userPrev) <= 2;
    }
}
