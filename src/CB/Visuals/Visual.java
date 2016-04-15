package CB.Visuals;

import CB.Master.ChatBot;
import CB.Master.Cleaning;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PVector;
import java.util.Random;

import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.waitInput;
import static CB.Master.Cleaning.output;
import static CB.Master.RepeatCheck.userPrevious;
import static CB.Master.RepeatCheck.botPrevious;

public class Visual extends PApplet {
    private static int presentCounter;
    private static int exitCounter;
    private static boolean presentCheck;
    private static String prevMess;
    private static int frames;
    private static int minute;
    private final static String[] userPresent = {"are you still there?", "you take a long time to reply..", "kind of getting bored here..", "hello..?" };
    private AudioInput in;
    private FFT fft;
    // Corner Visuals
    private int index = 4;
    private int[] colours = new int[index];
    private float[] frequencies = {
            256f, 293f, 1468f, 1566f,
            329f, 369f, 1272f, 1370f,
            587f, 659f, 2256f, 2354f,
            739f, 783f, 2060f, 2158f };
    private float desDiameter[] = new float[index];
    private CornerArc[] arcs = new CornerArc[index];
    private float scrHeight;
    // Center Visual
    private float centX;
    private float centY;
    private float radius;
    private float step;
    private Float[] outLineLengths;
    private GraphicLines[] outLines;
    private PVector[] outCoordinates;
    private PVector[] inCoordinates;

    public static String capturedText = "";
    public static boolean waitingIn = true;

    public void settings() {
        presentCounter = 0;
        exitCounter = 0;
        presentCheck = false;
        prevMess = "";
        //frames = 60;
        frames = 30;
        minute = frames * 2 * 60;

        size(800, 600);
        //fullScreen();
        smooth(8);

        // Fullscreen
        //float scrWidth = displayWidth;
        scrHeight = displayHeight;
        centX = displayWidth * 0.5f;
        centY = displayHeight * 0.5f;
        centX = width * 0.5f;
        centY = height * 0.5f;

        // Windowed
        float scrWidth = width;
        scrHeight = height;

        int frameSize = 1024;
        int sampleRate = 44100;
        Minim minim = new Minim(this);
        in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
        fft = new FFT(frameSize, sampleRate);

        // Corner Visuals
        colours[0] = color(204, 102, 0);
        colours[1] = color(150, 250, 50);
        colours[2] = color(50, 50, 200);
        colours[3] = color(204, 0, 204);

        desDiameter[0] = scrHeight * 0.3f;
        arcs[0] = new CornerArc(0, 0, desDiameter[0], colours[0]);
        arcs[1] = new CornerArc(scrWidth, 0, desDiameter[0], colours[1]);
        arcs[2] = new CornerArc(scrWidth, scrHeight, desDiameter[0], colours[2]);
        arcs[3] = new CornerArc(0, scrHeight, desDiameter[0], colours[3]);

        // Center Visual
        radius = 200.0f;
        step = TWO_PI / (fft.specSize() * 0.5f);
        int arraySize = (int) (TWO_PI / step) + 1;
        outLineLengths = new Float[arraySize];
        outLines = new GraphicLines[arraySize];
        outCoordinates = new PVector[arraySize];
        inCoordinates = new PVector[arraySize];

        int i = 0;
        for (float alpha = 0.0f; alpha < TWO_PI; alpha += step, i++)
            outLines[i] = new GraphicLines(radius * sin(alpha) + centX, radius * cos(alpha) + centY, 10.0f);
    }

    public void draw() {
        frameRate(frames);
        background(0);

        presentCounter++;
        exitCounter ++;

        // After 10 seconds, display message checking is the user is still there
        if (presentCounter == 600 && !presentCheck)
            output(stillThereMessage(), 0);

        // After two minutes, if no response from user, exit
        if (exitCounter == minute * 2.0f) {
            output("Okay, well I'm kind of busy and I have other stuff to do, so bye!", 0);
            exit();
        }

        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        drawCorners();
        drawCenter();

        writeText();
    }

    public void keyPressed() {
        presentCheck = true;
        presentCounter = 0;
        exitCounter = 0;

        if (ChatBot.captureInput && keyCode != SHIFT && keyCode != CONTROL && keyCode != ALT && keyCode != ENTER && keyCode != RETURN && keyCode != BACKSPACE)
            capturedText = capturedText + key;
        else if (keyCode == BACKSPACE && capturedText.length() > 0)
            capturedText = capturedText.substring(0, capturedText.length() - 1);
        else if ((keyCode == ENTER || keyCode == RETURN) && capturedText.length() > 0) {
            ChatBot.uInput = Cleaning.cleanInput(capturedText);
            waitingIn = false;
        }
    }

    // TODO As each new repsonse/input is shown, move previosu slowly up screen (Star Wars style)

    private void writeText() {
        fill(255);
        textAlign(CENTER);
        pushMatrix();
        translate(centX, centY - radius * 0.6f);

        float dist = radius * 0.4f;
        // Previous User
        text(userPrevious, 0, 0);
        // Previous Bot
        text(botPrevious, 0, dist);
        // Current User
        text(capturedText, 0, dist * 2.0f);
        // Current Bot
        text(bOutput, 0, dist * 3.0f);
        popMatrix();
    }

    private static String stillThereMessage() {
        Random rand = new Random();
        String mess = userPresent[rand.nextInt(userPresent.length)];

        while (prevMess.equalsIgnoreCase(mess))
            mess = userPresent[rand.nextInt(userPresent.length)];

        prevMess = mess;
        return mess;
    }

    private void drawCorners() {
        strokeWeight(10);
        float[][] range = new float[index][2];
        for (int i = 0; i < index; i++) {
            range[i][0] = frequencies[i * index];
            range[i][1] = frequencies[i * index + (index - 1)];
        }

        float freq = FFTFreq();

        for (int i = 0; i < index; i++) {
            for (int j = i * index; j < (i + 1) * index; j++) {
                int k = j + 1;

                // Prevent array out of bounds exception
                if (k == 16)
                    k--;

                if (checkFreq(freq, frequencies[j], frequencies[k])) {
                    float mapped = map(freq, range[i][0], range[i][1], scrHeight * 0.3f, scrHeight);
                    if (mapped < desDiameter[i] * 0.7f || mapped > desDiameter[i] * 1.3f)
                        desDiameter[i] = mapped;
                }
            }
        }

//         Top Left, Top Right, Bottom Right, Bottom Left
        for (int j = 0; j < arcs.length; j++ ) {
            CornerArc arcy = arcs[j];
            arcy.sizeMatch(desDiameter[j]);

            PVector pos = arcy.loc;
            float diam = arcy.diameter;
            int colour = arcy.colour;
            int black = color(0);

            pushMatrix();
            translate(pos.x, pos.y);
            for (float i = diam; i >= 0; i -= 2.5f) {
                float inter = map(i, 0, diam, 0, 1);
                stroke(lerpColor(colour, black, inter));
                arc(0, 0, i, i, j * HALF_PI, (j + 1) * HALF_PI);
            }
            popMatrix();
        }
    }
    private void drawCenter() {
        int bandReset = 16;
        int k = 0;
        for (int i = 0; i < bandReset; i ++) {
            for (int j = i; j < fft.specSize() * 0.5f; j += bandReset, k++) {
                outLineLengths[k] = map(fft.getBand(j) * 150, 0.0f, centX * 0.6f, 30.0f, centX * 0.08f);
                if (outLineLengths[k] < 40.0f)
                    outLineLengths[k] = random(30.0f, 40.0f);
            }
        }

        noFill();
        stroke(220);
        strokeWeight(1.5f);

        int i = 0;
        for (float alpha = 0.0f; alpha < TWO_PI; alpha += step, i++) {
            GraphicLines outLiney = outLines[i];
            outLiney.lineChange(outLineLengths[i]);
            PVector pos = outLiney.loc;
            float outSize = outLiney.lineLength;
            float inSize = -outLiney.lineLength * 0.3f;
            PVector outEnd = new PVector(outSize * sin(alpha), outSize * cos(alpha));
            PVector inEnd = new PVector(inSize * sin(alpha), inSize * cos(alpha));

            pushMatrix();
            translate(pos.x, pos.y);
            line(0, 0, outEnd.x, outEnd.y);
            popMatrix();

            outCoordinates[i] = new PVector(outEnd.x + pos.x, outEnd.y + pos.y);
            inCoordinates[i] = new PVector(inEnd.x + pos.x, inEnd.y + pos.y);
        }

        drawCurve(outCoordinates);
        drawCurve(inCoordinates);

        strokeWeight(3);
        ellipse(centX, centY, radius * 2.0f, radius * 2.0f);
        fill(0);
        noStroke();
        ellipse(centX, centY, radius * 1.2f, radius * 1.2f);
    }

    private void drawCurve(PVector[] coordinates) {
        beginShape();
        curveVertex(coordinates[coordinates.length - 1].x, coordinates[coordinates.length - 1].y);
        for (PVector coordinate : coordinates)
            curveVertex(coordinate.x, coordinate.y);
        curveVertex(coordinates[0].x, coordinates[0].y);
        curveVertex(coordinates[1].x, coordinates[1].y);
        endShape();
    }
    private float FFTFreq() {
        float maxValue = Float.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0; i < fft.specSize(); i++) {
            if (fft.getBand(i) > maxValue) {
                maxValue = fft.getBand(i);
                maxIndex = i;
            }
        }
        return fft.indexToFreq(maxIndex);
    }
    private boolean checkFreq(float freq, float low, float high) {
        return freq > low && freq < high;
    }
}