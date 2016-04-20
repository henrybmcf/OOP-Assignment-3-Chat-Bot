package CB.Master;

import java.io.*;
import java.util.*;
import processing.core.PApplet;

import CB.Visuals.Visual;
import static CB.EditDist.EditDistance.MinimumEditDistance;
import static CB.FileCode.FileMethods.fileErrorMessage;
import static CB.FileCode.FileMethods.saveLog;
import static CB.FileCode.FileMethods.zipLog;
import static CB.Master.Checks.checkWordValidity;
import static CB.Master.Checks.exitCheck;
import static CB.Master.Cleaning.cleanOutput;
import static CB.Master.Cleaning.output;
import static CB.Master.Favourites.checkLoadFavourite;
import static CB.Master.Favourites.saveNewFeel;
import static CB.Master.RepeatCheck.botRepeating;
import static CB.Master.RepeatCheck.saveUserResponse;
import static CB.Master.RepeatCheck.setURepeat;
import static CB.Visuals.Visual.waitingIn;

@SuppressWarnings("serial")
public class ChatBot {
    public static String uInput;
    public static String bOutput = "";
    static boolean understand;
    static FileWriter conLog = null;
    static String name;
    static String userName;
    private final static String botLogName = "Bot:\t";
    private final static String[] salutations = {"great to see you!", "such a nice day today!"};
    private final static ArrayList<String> userRepetition = setURepeat();
    public static boolean exit = false;

    @SuppressWarnings({"unchecked", "deprecation"})
    public static void main(String[] args) {
        // Call Visuals to start drawing
        PApplet.main(Visual.class.getName());

        // Set up conversation log file. Filename is date and time
        String date = new Date().toString().replace(":", "_");
        File log = new File("Conversation Logs" + File.separator + date + ".txt");
        try {
            conLog = new FileWriter(log, true);
            conLog.write("Start:\t" + date + "\n\n");
        }
        catch (IOException e) { e.printStackTrace(); }

        // Give program time to create OST ArrayList
        try { Thread.sleep(1000); }
        catch (InterruptedException e) { e.printStackTrace(); }

        // Ask user for their name for profiling
        output("Hello, what is your full name?");
        // Wait for user input
        waiting();

        // Write the bot's response to the conversation log file
        saveLog(conLog, botLogName, bOutput);

        // Transform input to name
        name = Cleaning.toName(uInput);
        String firstName = Cleaning.firstName(name);
        userName = firstName + ":\t ";
        // Write user's response to conversation log file
        saveLog(conLog, userName, uInput);

        // Set up profile file named after user
        File prof = new File("Profiles" + File.separator + name + ".txt");

        // If profile previously exists, response with welcome back
        if (prof.exists() && !prof.isDirectory())
            bOutput = "Welcome back " + firstName + ", " + assignSalutation();
        // If profile doesn't exist, set up new profile file
        else {
            bOutput = "Oo, a new person! Hello " + firstName + ", " + assignSalutation();

            try {
                FileWriter profile = new FileWriter(prof, true);
                profile.write("Profile:\t" + name + "\n*");
                profile.flush();
                profile.close();
            } catch (IOException ex) { fileErrorMessage(); }
        }

        // Output and save to conversation file
        output(bOutput);
        saveLog(conLog, botLogName, bOutput);

        do {
            // Wait for user input
            waiting();
            // Write the user's response to the conversation log file
            saveLog(conLog, userName, uInput);

            // Check user has asked to 'exit' (said goodbye)
            if (!exitCheck()) {
                // Check if user is repeating, choose relevant message
                if (RepeatCheck.checkUserRepetition())
                    assignResponse(userRepetition);

                // Check if user is: Repeating bot, is talking in context to favourite or is asking for date, favourite or being aggressive
                else if (!RepeatCheck.checkUserBotSame() && !ConvoContext.favouriteContextChecks() && !Checks.inputChecks()) {
                    // Search database for keyword match
                    // If bot understands input, grab related responses from file
                    // Else, do some transposition or see if it contains it
                    int line = searchKeyword("KnowledgeBase", 1);

                    if (understand)
                        grabResponses("KnowledgeBase", line);
                    else
                        checkWordValidity();
                }

                // Output and save to conversation file
                output(bOutput);
                saveLog(conLog, botLogName, bOutput);

                // Save user response for checking if they are repeating themselves
                saveUserResponse(uInput);
                uInput = "";
            }
            else {
                output("Goodbye " + firstName + ", it was nice talking to you.");
                saveLog(conLog, botLogName, bOutput);
                exit = true;
                break;
            }
        }
        while (true);

        // Write time of end of conversation to conversation file and close file
        Date dateEnd = new Date();
        try {
            assert conLog != null;
            conLog.write("\n\nEnd\t" + dateEnd.toString());
            conLog.flush();
            conLog.close();
        }
        catch (IOException ex) { fileErrorMessage(); }
        // Zip file to save space
        zipLog("Conversation Logs" + File.separator + date);
        // Delete unzipped file
        //noinspection ResultOfMethodCallIgnored
        log.delete();
    }

    // Select random salutation from array
    private static String assignSalutation() {
        return salutations[new Random().nextInt(salutations.length)];
    }

    // Search database for match to user input
    // Pass in filename to load relevant knowledge file
    @SuppressWarnings("ConstantConditions")
    static int searchKeyword(String fileName, int source) {
        String line;
        String smallest = " ";
        int lineCount = 0;
        int smallLine = 0;

        try {
            BufferedReader buffRead = new BufferedReader(new FileReader("Data" + File.separator + fileName + ".txt"));
            float small = MinimumEditDistance(uInput, buffRead.readLine().substring(1));
            searching:
            while((line = buffRead.readLine()) != null) {
                lineCount++;

                switch(line.charAt(0)) {
                    // K represents keyword/phrase in database
                    case 'K':
                        // Remove K from line for testing
                        line = line.substring(1);

                        switch (source) {
                            // For general keyword matching
                            case 1:
                                float dist = MinimumEditDistance(uInput, line);
                                if (dist < small) {
                                    smallest = line;
                                    smallLine = lineCount;

                                    // If exact match found, break out of loops
                                    if (dist == 0)
                                        break searching;
                                }

                                // Check to see if user input contains the keyword
                                if (uInput.contains(line)) {
                                    understand = true;
                                    return lineCount;
                                }
                                break;

                            // For matching favourites
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
                                        output(bOutput);
                                        saveNewFeel(line);
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
            if ((MinimumEditDistance(uInput, smallest) <= 1) && !understand) {
                // In case strings aren't the exact same, assign keyword to input to allow exact searching for repetition checking
                uInput = smallest;
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
        // While bot is repeating, select new response
        do {
            Collections.shuffle(responsesList);
            bOutput = responsesList.get(0);
        }
        while (botRepeating());

        if (understand)
            bOutput = cleanOutput();
    }

    // Waiting for user input
    static void waiting() {
        while (waitingIn) {
            try { Thread.sleep(500); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
        waitingIn = true;
    }
}