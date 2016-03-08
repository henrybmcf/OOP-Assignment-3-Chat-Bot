/**
 * Assignment 3 - Object Orientated Programming - ChatBot
 * Team: Henry Ballinger McFarlane & Lok-Woon Wan
 */

import processing.core.PApplet;

import java.util.*;
import java.io.*;

// TODO Introduce timer feature, so if user is idle for x seconds, generate prompt, ex: "Are you still there?"
@SuppressWarnings("serial")
public class ChatBot extends PApplet {
    public static String uInput = "Hello";
    public static String bOutput;

    //public static String uInputBackup = "";

    public static String bPrevious = "";
    public static String userPrev = "";

    public static boolean understand;
    public static boolean transposition = false;
    public static boolean exit;
    public static long check = System.currentTimeMillis();

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

    protected final static ArrayList<String> userRepetition = setURepeat();

    // Processing Methods
    public void settings() {
        fullScreen(2);
    }

    public void draw() {
        background(0);
        fill(255);
        rect(50, 50, 100, 100);
    }

    public void mousePressed() {
        exit();
    }

    public static FileWriter initLog() {
        Date dateUF = new Date();
        String date = dateUF.toString().replace(":", "_");
        File log = new File("Conversation Logs" + File.separator + date + ".txt");
        try {
            return new FileWriter(log, true);
        }
        catch (Exception e) {
            System.out.println("Having some trouble saving our conversation..");
            System.out.println("Official jargon is " + e);
        }
        return null;
    }

    public static void saveLog(FileWriter log, String ID, String str) throws IOException {
        log.write(ID + str + "\n");
    }


    public static void main(String[] args) {
        PApplet.main("ChatBot");

        Date dateUF = new Date();
        String date = dateUF.toString().replace(":", "_");
        FileWriter conLog;
        conLog = initLog();

        try {
            assert conLog != null;
            conLog.write("Start:\t" + dateUF.toString() + "\n\n");
        } catch (IOException e) {
            System.out.println("Having some trouble saving our conversation..");
            e.printStackTrace();
        }

        bOutput = assignSalutation();
        saveResponse(bOutput);
        System.out.println(bOutput);

        do {
            // Write the bot's response to the conversation log file
            try {
                saveLog(conLog, "Bot:\t", bOutput);
            } catch (IOException e) {
                System.out.println("Having some trouble saving our conversation..");
                e.printStackTrace();
            }
            System.out.print("> ");

            Scanner scanner = new Scanner(System.in);
            uInput = scanner.nextLine();


            // Remove unwanted white space and punctuation and convert to lower case
            uInput = clean(uInput);

            // Write the user's response to the conversation log file
            try {
                saveLog(conLog, "User:\t", uInput);
            } catch (IOException e) {
                System.out.println("Having some trouble saving our conversation..");
                e.printStackTrace();
            }

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

            } else {
                if (checkUserRepetition()) {
                    assignResponse(userRepetition);
                }
                else if (!checkUserBotSame()) {
                    try {
                        checkRepeat(searchKeyword());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                bOutput = initCap(bOutput);
                saveResponse(bOutput);
                System.out.println(bOutput);

                saveUserResponse(uInput);
                uInput = "";
            }
        }
        while (true);

        Date dateEnd = new Date();
        try {
            conLog.write("\n\nEnd\t" + dateEnd.toString());
            conLog.flush();
            conLog.close();
        } catch (IOException e) {
            System.out.println("Having some trouble saving our conversation...");
            e.printStackTrace();
        }
    }

    public static String cleanOutput() {
        String str;
        str = bOutput.substring(1);
        return str;
    }

    // Search knowledge database for match to user input
    public static int searchKeyword() {
        String line;
        String smallest = " ";
        int lineCount = 0;
        int smallLine = 0;

        try {
            FileReader fileReader = new FileReader("Responses" + File.separator + "KnowledgeBase.txt");
            BufferedReader buffRead = new BufferedReader(fileReader);

            float small = EditDistance.MinimumEditDistance(uInput, buffRead.readLine().substring(1));

            searching:
            while ((line = buffRead.readLine()) != null) {
                lineCount++;
                switch (line.charAt(0)) {
                    case 'K':
                        line = line.substring(1);
                        float dist = EditDistance.MinimumEditDistance(uInput, line);
                        if (dist < small) {
                            smallest = line;
                            smallLine = lineCount;
                            if (dist == 0)
                                break searching;
                        }
                        break;

                    default:
                        break;
                }
            }

            // Check to see if closest matching keyword is close enough match
            if ((EditDistance.MinimumEditDistance(uInput, smallest) <= 1) && !understand) {
                // In case strings aren't the exact same, assign keyword to input to allow exact searching for repetition checking
                uInput = smallest;
                understand = true;
            }

            buffRead.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }

        return smallLine;
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
                while ((line.charAt(0)) != 'R')
                    line = buffRead.readLine();

                // Go through all the responses
                while ((line.charAt(0)) != '#') {
                    responses.add(line);
                    line = buffRead.readLine();
                }

                buffRead.close();
            } catch (IOException ex) {
                fileErrorMessage();
            }

            assignResponse(responses);
        } else {
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
                // Loop through words file, to see if input is a word
                try {
                    FileReader fileReader = new FileReader("Words.txt");
                    BufferedReader buffRead = new BufferedReader(fileReader);
                    String word = buffRead.readLine();

                    while (word != null) {
                        word = buffRead.readLine();
                        // If match found, call function to get user to input phrases and responses
                        if (word.equalsIgnoreCase(uInput)) {
                            setupNewKeyword();
                            break;
                        }
                    }
                } catch (IOException ex) {
                    fileErrorMessage();
                }

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
                } catch (IOException ex) {
                    fileErrorMessage();
                }

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

        if (understand)
            bOutput = cleanOutput();
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

        for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i) == ' ' && prevChar == ' ') || !puncCheck(str.charAt(i))) {
                cleaning.append(str.charAt(i));
                prevChar = str.charAt(i);
            } else if (prevChar != ' ' && puncCheck(str.charAt(i))) {
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

    public static boolean checkUserBotSame() {
        if (uInput.equalsIgnoreCase(bOutput)) {
            bOutput = "That's what I said";
            return true;
        }
        return false;
    }

    public static void saveUserResponse(String current) {
        userPrev = current;
    }

    public static boolean checkUserRepetition() {
        return userPrev.length() > 0 && EditDistance.MinimumEditDistance(uInput, userPrev) <= 2;
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

    public static void setupNewKeyword() throws IOException {
        System.out.println("Hmm, I don't think I know this keyword.\nCould you give me a few example phrases and responses so I know in the future?");
        System.out.print("Yes or no will do :)\n> ");
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> responses = new ArrayList<>();
        String confirm = checkConfirm();

        if (confirm.equalsIgnoreCase("yes")) {
            do {
                System.out.print("What would be a typical phrase/question?\n> ");
                keys.add(clean(scanner.nextLine()));
                System.out.print("And a response?\n> ");
                responses.add(clean(scanner.nextLine()));
                System.out.print("Anymore?\n> ");

                confirm = checkConfirm();
            }
            while (confirm.equalsIgnoreCase("yes"));

            File log = new File("Responses" + File.separator + "KnowledgeBase.txt");
            FileWriter conLog = new FileWriter(log, true);
            for (String key : keys)
                conLog.write("\nK" + key);
            for (String resp : responses)
                conLog.write("\nR" + resp);
            conLog.write("\n#");
            conLog.flush();
            conLog.close();
        }
    }

    public static String checkConfirm() {
        Scanner scanner = new Scanner(System.in);
        String check = scanner.nextLine();
        while (!check.equalsIgnoreCase("yes") && !check.equalsIgnoreCase("no")) {
            System.out.print("No comprende amigo. Yes or no please.\n> ");
            check = scanner.nextLine();
        }
        return check;
    }


}