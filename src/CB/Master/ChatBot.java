 /**
 * Assignment 3 - Object Orientated Programming - ChatBot
 * Team: Henry Ballinger McFarlane & Lok-Woon Wan
 */

package CB.Master;

import CB.EditDist.EditDistance;
import CB.FileCode.FileMethods;
import CB.Speech.TextSpeech;

import java.util.*;
import java.io.*;

// TODO Introduce timer feature, so if user is idle for x seconds, generate prompt, ex: "Are you still there?"
// TODO Use processing's 60fps for this?

public class ChatBot {
  //  private static final String VOICENAME_kevin = "kevin16";

//    public static void speak(String text) {
//        Voice voice;
//        VoiceManager voiceManager = VoiceManager.getInstance();
//        voice = voiceManager.getVoice(VOICENAME_kevin);
//        voice.allocate();
//        voice.speak(text);
//    }

    public static String uInput;
    public static String bOutput;

    public static String bPrevious = "";
    public static String userPrev = "";

    public static boolean understand;
    public static boolean transposition = false;
    public static boolean exit;

    public static String name;

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
            "how are you?",
            "such a nice day today!"
    };

    // List of possible user inputs to end the conversation
    protected final static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    protected final static ArrayList<String> userRepetition = RepeatCheck.setURepeat();

    @SuppressWarnings({"unchecked", "deprecation"})
    public static void main(String[] args) throws IOException {
        TextSpeech speaking = new TextSpeech("kevin16");

//        String date = new Date().toString().replace(":", "_");
//        File log = new File("Conversation Logs" + File.separator + date + ".txt");
//        FileWriter conLog = new FileWriter(log, true);
//        conLog.write("Start:\t" + date + "\n\n");

        bOutput = "Hello, what is your full name?";
        System.out.println(bOutput);
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        uInput = scanner.nextLine();
        uInput = Cleaning.cleanInput(uInput);
        name = Cleaning.toName(uInput);

        File prof = new File("Profiles" + File.separator + name + ".txt");

        String firstName = Cleaning.firstName(name);

        boolean prevProf = true;

        if (prof.exists() && !prof.isDirectory())
            bOutput = "Welcome back " + firstName + ", " + assignSalutation();
        else {
            bOutput = "Oo, a new person! Hello " + firstName + ", " + assignSalutation();
            prevProf = false;
        }

        FileWriter profile = new FileWriter(prof, true);
        if (!prevProf) {
            profile.write("Profile:\t" + name + "\n*");
            profile.flush();
            profile.close();
        }

        System.out.println(bOutput);

        //bOutput = assignSalutation();
        RepeatCheck.saveResponse(bOutput);
        //System.out.println(bOutput);
        //speaking.speak(bOutput);

        do {
            // Write the bot's response to the conversation log file
            //FileMethods.saveLog(conLog, "Bot:\t", bOutput);
            System.out.print("> ");

            //Scanner scanner = new Scanner(System.in);
            scanner = new Scanner(System.in);
            uInput = scanner.nextLine();

            // Remove unwanted white space and punctuation and convert to lower case
            uInput = Cleaning.cleanInput(uInput);

            // Write the user's response to the conversation log file
            //FileMethods.saveLog(conLog, "User:\t", uInput);

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
                String goodbyeMessage = "Goodbye " + firstName + ", it was nice talking to you.";
                System.out.println(goodbyeMessage);
                //speaking.speak(goodbyeMessage);
                break;
            }
            else {
                if (RepeatCheck.checkUserRepetition())
                    assignResponse(userRepetition);
                else if (!RepeatCheck.checkUserBotSame() && !ConvContext.contextChecks())
                    RepeatCheck.checkRepeat(searchKeyword("KnowledgeBase", 1));

                bOutput = Cleaning.initCap(bOutput);
                RepeatCheck.saveResponse(bOutput);
                System.out.println(bOutput);
                //speaking.speak(bOutput);

                RepeatCheck.saveUserResponse(uInput);
                uInput = "";
            }
        }
        while (true);

//        Date dateEnd = new Date();
//        conLog.write("\n\nEnd\t" + dateEnd.toString());
//        conLog.flush();
//        conLog.close();
//        FileMethods.zipLog("Conversation Logs" + File.separator + date);
//        log.delete();
    }

    // Select random salutation from array
    public static String assignSalutation() {
        Random rand = new Random();
        return salutations[rand.nextInt(salutations.length)];
    }

    public static boolean inputChecks() {
        return checkDate() || checkFavourite();
    }
    public static boolean checkDate() {
        if (uInput.contains("date") || uInput.contains("time") || uInput.contains("day")) {
            bOutput = "The date and time is " + new Date().toString().substring(0, 19);
            return true;
        }
        return false;
    }
    public static boolean checkFavourite() {
        if (uInput.contains("favourite")) {
            searchKeyword("Favourites", 2);
            return true;
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public static int searchKeyword(String fileName, int source) {
        String line;
        String smallest = " ";
        int lineCount = 0;
        int smallLine = 0;

        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Responses" + File.separator + fileName + ".txt"));

            float small = EditDistance.MinimumEditDistance(uInput, buffRead.readLine().substring(1));

            searching:
            while((line = buffRead.readLine()) != null) {
                lineCount++;
                switch(line.charAt(0)) {
                    case 'K':
                        line = line.substring(1);

                        switch (source) {
                            case 1:
                                float dist = EditDistance.MinimumEditDistance(uInput, line);
                                if (dist < small) {
                                    smallest = line;
                                    smallLine = lineCount;
                                    if (dist == 0)
                                        break searching;
                                }
                                break;

                            case 2:
                                Boolean check = true;
                                if (uInput.contains(line)) {
                                    String fave;
                                    if (Boolean.parseBoolean(checkLoadFavourite(line, 0).toString())) {
                                        fave = ", I think yours is " + checkLoadFavourite(line, 1).toString();
                                        check = false;
                                    }
                                    else
                                        fave = ", what is yours?";

                                    bOutput = buffRead.readLine().substring(1) + fave;

                                    if (check) {
                                        bOutput = Cleaning.initCap(bOutput);
                                        RepeatCheck.saveResponse(bOutput);
                                        System.out.println(bOutput);
                                        saveNewFave(line);
                                    }
                                    break searching;
                                }
                                break;
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
        }
        catch(IOException ex) { FileMethods.fileErrorMessage(); }

        return smallLine;
    }

    // Select random bot response
    public static void assignResponse(ArrayList<String> responsesList) {
        do {
            Collections.shuffle(responsesList);
            bOutput = responsesList.get(0);
        }
        while (RepeatCheck.bRepeating());

        if (understand)
            bOutput = Cleaning.cleanOutput();
    }

    public static Object checkLoadFavourite(String favourite, int source) {
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Profiles" + File.separator + name + ".txt"));
            String line;

            while ((line = buffRead.readLine()) != null) {
                String splitLine[] = ConvContext.splitString(line, ",");

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

    public static void saveNewFave(String faveObject) throws IOException {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        uInput = scanner.nextLine();
       // uInput = Cleaning.cleanInput(uInput);

        try {
            FileWriter profile = new FileWriter(new File("Profiles" + File.separator + name + ".txt"), true);
            profile.write("\n" + faveObject + "," + uInput);
            profile.flush();
            profile.close();

            bOutput = "Okay, I'll remember that..";
        }
        catch (IOException ex) { FileMethods.fileErrorMessage(); }
    }
}
