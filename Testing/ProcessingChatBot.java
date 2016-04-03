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
}