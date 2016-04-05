package CB.Visuals;


import processing.core.PApplet;
import java.util.Random;

import static CB.Master.Cleaning.prepOutput;


public class Visual extends PApplet {
    private static int presentCounter;
    private static int exitCounter;
    private static boolean presentCheck;
    private static String prevMess;
    private static int frames;
    private static int minute;

    private final static String[] userPresent = {
            "are you still there?",
            "you take a long time to reply..",
            "kind of getting bored here..",
            "hello..?"
    };

    public void settings() {
        size(100, 100);

        presentCounter = 0;
        exitCounter = 0;
        presentCheck = false;
        prevMess = "";
        frames = 60;
        minute = frames * 60;
    }

    public void draw() {
        frameRate(frames);
        background(0);

        presentCounter++;
        exitCounter ++;

        // After 10 seconds, display message checking is the user is still there
        if (presentCounter == 600 && !presentCheck)
            prepOutput(stillThereMessage(), 0);

        // After two minutes, if no response from user, exit
        if (exitCounter == minute * 2.0f) {
            prepOutput("Okay, well I'm kind of busy and I have other stuff to do, so bye!", 0);
            exit();
        }

    }

    public void keyPressed() {
        presentCheck = true;
        presentCounter = 0;
        exitCounter = 0;
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
