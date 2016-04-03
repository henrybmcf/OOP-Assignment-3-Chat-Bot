 /*
 * Assignment 3 - Object Orientated Programming - ChatBot
 * Team: Henry Ballinger McFarlane & Lok-Woon Wan
 */

package CB.Master;

import CB.EditDist.EditDistance;
import CB.FileCode.FileMethods;
import CB.Speech.TextSpeech;
import CB.Visuals.Visual;

import java.util.*;
import java.io.*;

import processing.core.PApplet;

// TODO Introduce timer feature, so if user is idle for x seconds, generate prompt, ex: "Are you still there?"
// TODO Use processing's 60fps for this?

@SuppressWarnings("serial")
public class ChatBot extends PApplet {
  //  private static final String VOICENAME_kevin = "kevin16";

//    public static void speak(String text) {
//        Voice voice;
//        VoiceManager voiceManager = VoiceManager.getInstance();
//        voice = voiceManager.getVoice(VOICENAME_kevin);
//        voice.allocate();
//        voice.speak(text);
//    }

    static String uInput;
    public static String bOutput;

    static String bPrevious = "";
    static String userPrev = "";

    static boolean understand;
    static boolean transposition = false;
    private static boolean exit;

    static String name;

    static String keyWord;

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
            //"how are you?",
            "great to see you!",
            "such a nice day today!"
    };

    // List of possible user inputs to end the conversation
    private final static String[] goodbye = {
            "bye",
            "goodbye",
            "see you later",
            "i have to go",
            "see ya"
    };

    private final static ArrayList<String> userRepetition = RepeatCheck.setURepeat();


    @SuppressWarnings({"unchecked", "deprecation"})
    public static void main(String[] args) {
        //PApplet.main(Visual.class.getName());

        //TextSpeech speaking = new TextSpeech("kevin16");

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

        try {
            FileWriter profile = new FileWriter(prof, true);

            if (!prevProf) {
                profile.write("Profile:\t" + name + "\n*");
                profile.flush();
                profile.close();
            }
        }
        catch (IOException ex) { FileMethods.fileErrorMessage(); }

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

            // TODO Put into Checks class
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
                else if (!RepeatCheck.checkUserBotSame() && !ConvoContext.contextChecks())
                    RepeatCheck.checkRepeat(searchKeyword("KnowledgeBase", 1));

                prepOutput(bOutput);
//                bOutput = Cleaning.initCap(bOutput);
//                RepeatCheck.saveResponse(bOutput);
//                System.out.println(bOutput);
//                speaking.speak(bOutput);

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
                                        Favourites.saveNewFave(line);
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
        catch(IOException ex) { FileMethods.fileErrorMessage(); }

        return smallLine;
    }

    // Select random bot response
    static void assignResponse(ArrayList<String> responsesList) {
        do {
            Collections.shuffle(responsesList);
            bOutput = responsesList.get(0);
        }
        while (RepeatCheck.bRepeating());

        if (understand)
            bOutput = Cleaning.cleanOutput();
    }

    public static void prepOutput(String out) {
        out = preProcessOutput(out);
        bOutput = Cleaning.initCap(out);
        RepeatCheck.saveResponse(bOutput);
        System.out.println(bOutput);
//        Visual.presentCheck = false;
//        Visual.presentCounter = 0;
//        Visual.exitCounter = 0;
//        speaking.speak(bOutput);
    }

    public static String preProcessOutput(String resp) {
        String subject = "";
        if (resp.contains("*"))
            subject = findSubject();

        resp = resp.replaceFirst("\\*", subject);
        return resp;
    }

    // Find subject within user input, for use in output
    public static String findSubject() {
        String subject = "";

        int position = uInput.indexOf(keyWord);

        if (position != -1)
            subject = uInput.substring(position + keyWord.length(), uInput.length());

        return subject;
    }

}
