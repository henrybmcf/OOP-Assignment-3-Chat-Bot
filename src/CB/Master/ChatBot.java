 /*
 * Assignment 3 - Object Orientated Programming - ChatBot
 * Team: Henry Ballinger McFarlane & The Asian
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
    public static String uInput;
    public static String bOutput = "";
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
    private final static String[] salutations = { "great to see you!", "such a nice day today!" };
    private final static ArrayList<String> userRepetition = RepeatCheck.setURepeat();

    private static boolean waitInput = true;

    public static boolean exit = false;

    private static boolean checkFutureContext = false;

    @SuppressWarnings({"unchecked", "deprecation"})
    public static void main(String[] args) {
//       PApplet.main(Visual.class.getName());

//        TextSpeech speaking = new TextSpeech("kevin16");
//        String date = new Date().toString().replace(":", "_");
//        File log = new File("Conversation Logs" + File.separator + date + ".txt");
//        FileWriter conLog = null;
//        try {
//            conLog = new FileWriter(log, true);
//            conLog.write("Start:\t" + date + "\n\n");
//        }
//        catch (IOException e) { e.printStackTrace(); }
//        output("Hello, what is your full name?", 1);
//
//          waiting();

        // Write the bot's response to the conversation log file
//        saveLog(conLog, botLogName, bOutput);

//        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
  //      uInput = Cleaning.cleanInput(scanner.nextLine());
 //       name = Cleaning.toName(uInput);
   //     String firstName = Cleaning.firstName(name);
//
//        //saveLog(conLog, firstName, uInput);
//
//        File prof = new File("Profiles" + File.separator + name + ".txt");
//
//        if (prof.exists() && !prof.isDirectory())
//            bOutput = "Welcome back " + firstName + ", " + assignSalutation();
//        else {
//            bOutput = "Oo, a new person! Hello " + firstName + ", " + assignSalutation();
//
//            try {
//                FileWriter profile = new FileWriter(prof, true);
//                profile.write("Profile:\t" + name + "\n*");
//                profile.flush();
//                profile.close();
//            } catch (IOException ex) { fileErrorMessage(); }
//        }
//
//        output(bOutput, 1);
//
//        Visual.waitingIn = true;

        //saveLog(conLog, botLogName, bOutput);

        do {
            System.out.print("> ");

            //waiting();
            /* while (Visual.waitingIn) {
                 try { Thread.sleep(500); }
                 catch (InterruptedException e) { e.printStackTrace(); }
            }*/

            // Remove unwanted white space and punctuation and convert to lower case from read in line
            uInput = Cleaning.cleanInput(scanner.nextLine());

            // Write the user's response to the conversation log file
//            saveLog(conLog, firstName, uInput);


            if (!exitCheck()) {
                // Check if user is repeating, choose relevant message
                if (RepeatCheck.checkUserRepetition())
                    assignResponse(userRepetition);

                // Check if user is repeating bot
                // Check user isn't talking in context to favourite
                // Check if user is asking for date, favourite or being aggressive

                // Search database for keyword match
                // If bot understands input, grab related responses from file
                // Else, do some transposition or see if it contains it
                else if (!RepeatCheck.checkUserBotSame() && !ConvoContext.favouriteContextChecks() && !Checks.inputChecks()) {
                    int line = searchKeyword("KnowledgeBase", 1, false);

                    if (understand)
                        grabResponses("KnowledgeBase", line);
                    else
                        Checks.checkWordValidity();
                }

                output(bOutput, 0);

                if (bOutput.contains("?"))
                    checkFutureContext = true;

//                saveLog(conLog, botLogName, bOutput);

                RepeatCheck.saveUserResponse(uInput);
                uInput = "";
                Visual.capturedText = "";
                Visual.waitingIn = true;
                waitInput = true;
            }
            else {
//                output("Goodbye " + firstName + ", it was nice talking to you.", 1);
//                saveLog(conLog, botLogName, bOutput);
                exit = true;
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
        return salutations[new Random().nextInt(salutations.length)];
    }

    @SuppressWarnings("ConstantConditions")
    static int searchKeyword(String fileName, int source, boolean context) {
        String line;
        String smallest = " ";
        int lineCount = 0;
        int smallLine = 0;

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
                                    understand = true;
                                    return lineCount;
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

            buffRead.close();
        }
        catch(IOException ex) { fileErrorMessage(); }

        return smallLine;
    }

    static void grabResponses(String fileName, int lineIndex) {
        ArrayList<String> responses = new ArrayList<>();
        String line = " ";

        try {
            FileReader fileReader = new FileReader("Data" + File.separator + fileName + ".txt");
            BufferedReader buffRead = new BufferedReader(fileReader);

            // Skip through all lines up to keyword line
            for (int i = 0; i <= lineIndex; i++)
                line = buffRead.readLine();

            // Skip through anymore keywords until at responses
            while ((line.charAt(0)) != 'R')
                    line = buffRead.readLine();

            // Go through all the responses, up to relevant stop sign (if in context)
            while((line.charAt(0)) != '#') {
                responses.add(line);
                line = buffRead.readLine();
            }

            buffRead.close();
        } catch(IOException ex) { fileErrorMessage(); }

        assignResponse(responses);
    }

    // Select random bot response
    static void assignResponse(ArrayList<String> responsesList) {
        do {
            Collections.shuffle(responsesList);
            bOutput = responsesList.get(0);
        }
        while (RepeatCheck.botRepeating());

        if (understand)
            bOutput = Cleaning.cleanOutput();

        checkFutureContext = false;
    }

    static void waiting() {
        while (Visual.waitingIn) {
            try { Thread.sleep(500); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
        Visual.waitingIn = true;
    }
}