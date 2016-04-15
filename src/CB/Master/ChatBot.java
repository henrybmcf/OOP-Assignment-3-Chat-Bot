 /*
 * Assignment 3 - Object Orientated Programming - ChatBot
 * Team: Henry Ballinger McFarlane & Lok-Woon Wan
 */

package CB.Master;

import CB.EditDist.EditDistance;
import CB.Speech.TextSpeech;
import CB.Visuals.Visual;

import java.util.*;
import java.io.*;

import processing.core.PApplet;

import static CB.FileCode.FileMethods.fileErrorMessage;
import static CB.FileCode.FileMethods.saveLog;
import static CB.FileCode.FileMethods.zipLog;
import static CB.Master.Checks.exitCheck;
import static CB.Master.Cleaning.output;


@SuppressWarnings("serial")
public class ChatBot extends PApplet {

    static String uInput;
    public static String bOutput;

    static boolean understand;
    static boolean transposition = false;

    static String name;
    static String keyWord;

    private final static String botLogName = "Bot:\t";

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

    private final static String[] salutations = {
            "great to see you!",
            "such a nice day today!"
    };

    private final static ArrayList<String> userRepetition = RepeatCheck.setURepeat();


    @SuppressWarnings({"unchecked", "deprecation"})
    public static void main(String[] args) {
       PApplet.main(Visual.class.getName());
//        TextSpeech speaking = new TextSpeech("kevin16");
//        String date = new Date().toString().replace(":", "_");
//        File log = new File("Conversation Logs" + File.separator + date + ".txt");
//        FileWriter conLog = null;
//        try {
//            conLog = new FileWriter(log, true);
//            conLog.write("Start:\t" + date + "\n\n");
//        }
//        catch (IOException e) { e.printStackTrace(); }
//        prepOutput("Hello, what is your full name?", 1);

        // Write the bot's response to the conversation log file
//        saveLog(conLog, botLogName, bOutput);

//        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
//        uInput = Cleaning.cleanInput(scanner.nextLine());
//        name = Cleaning.toName(uInput);
//        String firstName = Cleaning.firstName(name);
//
//        //saveLog(conLog, firstName, uInput);
//
//        File prof = new File("Profiles" + File.separator + name + ".txt");
//
//        boolean prevProf = true;
//
//        if (prof.exists() && !prof.isDirectory())
//            bOutput = "Welcome back " + firstName + ", " + assignSalutation();
//        else {
//            bOutput = "Oo, a new person! Hello " + firstName + ", " + assignSalutation();
//            prevProf = false;
//        }
//
//        if (!prevProf) {
//            try {
//                FileWriter profile = new FileWriter(prof, true);
//                profile.write("Profile:\t" + name + "\n*");
//                profile.flush();
//                profile.close();
//            } catch (IOException ex) { fileErrorMessage(); }
//        }
//
//        prepOutput(bOutput, 1);
        //saveLog(conLog, botLogName, bOutput);

        do {
            System.out.print("> ");

            // Remove unwanted white space and punctuation and convert to lower case from read in line
            uInput = Cleaning.cleanInput(scanner.nextLine());

            // Write the user's response to the conversation log file
//            saveLog(conLog, firstName, uInput);

            if (!exitCheck()) {
                if (RepeatCheck.checkUserRepetition())
                    assignResponse(userRepetition);
                else if (!RepeatCheck.checkUserBotSame() && !ConvoContext.contextChecks())
                    RepeatCheck.checkRepeat("KnowledgeBase", searchKeyword("KnowledgeBase", 1));

                output(bOutput, 0);

//                saveLog(conLog, botLogName, bOutput);

                RepeatCheck.saveUserResponse(uInput);
                uInput = "";
            }
            else {
//                prepOutput("Goodbye " + firstName + ", it was nice talking to you.", 1);
//                saveLog(conLog, botLogName, bOutput);
                break;
            }
        }
        while (true);

//        Date dateEnd = new Date();
//        try {
//            assert conLog != null;
//            conLog.write("\n\nEnd\t" + dateEnd.toString());
//            conLog.flush();
//            conLog.close();
//        }
//        catch (IOException ex) { fileErrorMessage(); }
//        zipLog("Conversation Logs" + File.separator + date);
//        //noinspection ResultOfMethodCallIgnored
//        log.delete();
    }

    // Select random salutation from array
    private static String assignSalutation() {
        Random rand = new Random();
        return salutations[rand.nextInt(salutations.length)];
    }

    @SuppressWarnings("ConstantConditions")
    static int searchKeyword(String fileName, int source) {
        String line;
        String smallest = " ";
        int lineCount = 0;
        int smallLine = 0;
        boolean contains = false;
        int containsLine = 0;

        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Data" + File.separator + fileName + ".txt"));

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

                                // Check to see if user input contains the keyword
                                if (uInput.contains(line)) {
                                    keyWord = line;
                                    contains = true;
                                    containsLine = lineCount;
                                }
                                break;

                            case 2:
                                Boolean check = true;
                                if (uInput.contains(line)) {
                                    String fave;
                                    if (Boolean.parseBoolean(Favourites.checkLoadFavourite(line, 0).toString())) {
                                        fave = ", I think yours is " + Favourites.checkLoadFavourite(line, 1).toString();
                                        check = false;
                                    }
                                    else
                                        fave = ", what is yours?";

                                    bOutput = buffRead.readLine().substring(1) + fave;

                                    if (check) {
                                        bOutput = Cleaning.initCap(bOutput);
                                        RepeatCheck.saveResponse(bOutput);
                                        System.out.println(bOutput);
                                        Favourites.saveNewFeel("favourite", line);
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
                keyWord = smallest;
                understand = true;
            }
            // If match is not close enough, check to see if input contains any keywords
            else if (contains) {
                understand = true;
                return containsLine;
            }

            buffRead.close();
        }
        catch(IOException ex) { fileErrorMessage(); }

        return smallLine;
    }

    // Select random bot response
    static void assignResponse(ArrayList<String> responsesList) {
        System.out.println(responsesList);
        do {
            Collections.shuffle(responsesList);
            bOutput = responsesList.get(0);
        }
        while (RepeatCheck.botRepeating());

        if (understand)
            bOutput = Cleaning.cleanOutput();
    }
}