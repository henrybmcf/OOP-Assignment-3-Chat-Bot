package CB.Visuals;

import processing.core.PApplet;

import CB.Master.ChatBot;

import java.util.Random;

public class Visual extends PApplet {
    public static int presentCounter = 0;

    public static int exitCounter = 0;

    public static boolean presentCheck = false;

    private final static String[] userPresent = {
            "are you still there?",
            "you take a long time to reply..",
            "kind of getting bored here..",
            "hello..?"
    };

    private static String prevMess = "";

    private static int frames;
    private static int minute;

    public void settings() {
        size(100, 100);

        frames = 60;
        minute = frames * 60;
    }

    public void draw() {
        frameRate(frames);
        background(0);
        presentCounter++;

        exitCounter ++;

        // After 10 seconds, display message checking is the user is still there
        if (presentCounter == 600 && !presentCheck) {
            ChatBot.prepOutput(stillThereMessage());
            presentCheck = true;
            presentCounter = 0;
        }

        if (exitCounter == minute * 1.0f) {
            ChatBot.prepOutput("Okay, well I'm kind of busy and I have other stuff to do, so bye!");
            exit();
        }

    }

    public void keyPressed() {
        presentCheck = true;
        presentCounter = 0;
    }

    private static String stillThereMessage() {
        Random rand = new Random();
        String mess = userPresent[rand.nextInt(userPresent.length)];

        while (prevMess.equalsIgnoreCase(mess))
            mess = userPresent[rand.nextInt(userPresent.length)];

        prevMess = mess;
        return mess;
    }
}
