package CB.Visuals;

import CB.Master.ChatBot;
import CB.Master.Cleaning;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import controlP5.*;

import java.util.ArrayList;
import java.util.Random;

import static CB.Master.ChatBot.uInput;
import static CB.Master.Cleaning.output;

public class Visual extends PApplet {
    public static int presentCounter;
    public static int exitCounter;
    public static boolean presentCheck;
    private static String prevMess;
    private static int frames;
    private static int minute;
    private final static String[] userPresent = {
            "are you still there?",
            "you take a long time to reply..",
            "kind of getting bored here..",
            "hello..?" };
    private AudioInput in;
    private FFT fft;

    // Corner Visuals
    //declaring
    private int index = 4; //4 colours for the corners
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
    //declaring
    private static float centX;
    private static float centY;
    private static float radius;
    private float step;
    private Float[] outLineLengths;
    private GraphicLines[] outLines;
    private PVector[] outCoordinates;
    private PVector[] inCoordinates;

    // Text Visuals
    public static boolean waitingIn;
    private float dist;
    private static boolean moveText;
    private static ArrayList<OnScreenText> OST;
    private float prevPos;
    public static String outTextDisplay = "";
    private int outCount = 0;
    private float xCo;
    private ControlP5 cp5;
    private int colourTimer = 0;
    private boolean colourSwitch = true;

    public void settings() {
        fullScreen();
        smooth(8);
    }

    public void setup() {
        float scrWidth = displayWidth;
        scrHeight = displayHeight;
        centX = displayWidth * 0.5f;
        centY = displayHeight * 0.5f;

        presentCounter = 0;
        exitCounter = 0;
        presentCheck = false;
        prevMess = "";
        frames = 30;
        minute = frames * 60;

        int frameSize = 1024;
        int sampleRate = 44100;
        Minim minim = new Minim(this);
        in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
        fft = new FFT(frameSize, sampleRate);

        // Corner Visuals
        //setting colours into array
        colours[0] = color(204, 102, 0);
        colours[1] = color(150, 250, 50);
        colours[2] = color(50, 50, 200);
        colours[3] = color(204, 0, 204);

        //desired diameter = 30% of screen
        desDiameter[0] = scrHeight * 0.3f;
        //positioning and colours of each arc at each corner
        arcs[0] = new CornerArc(0, 0, desDiameter[0], colours[0]);
        arcs[1] = new CornerArc(scrWidth, 0, desDiameter[0], colours[1]);
        arcs[2] = new CornerArc(scrWidth, scrHeight, desDiameter[0], colours[2]);
        arcs[3] = new CornerArc(0, scrHeight, desDiameter[0], colours[3]);

        // Center Visual

        radius = 200.0f;
        step = TWO_PI / (fft.specSize() * 0.5f); // gap between each line
        int arraySize = (int) (TWO_PI / step) + 1;
        outLineLengths = new Float[arraySize]; //line length
        outLines = new GraphicLines[arraySize]; //actual line
        outCoordinates = new PVector[arraySize]; //outside coordinates
        inCoordinates = new PVector[arraySize]; //inside coordinates

        int i = 0;
        //set coordinates for start of outside lines
        for (float alpha = 0.0f; alpha < TWO_PI; alpha += step, i++)
            outLines[i] = new GraphicLines(radius * sin(alpha) + centX, radius * cos(alpha) + centY, 10.0f);

        // Text Visuals
        waitingIn = true;
        moveText = false;
        dist = radius * 0.6f;
        xCo = centX - (radius * 0.8f);
        OST = new ArrayList<>();
        OST.add(new OnScreenText("..", new PVector(xCo, centY + dist)));
        OST.add(new OnScreenText("..", new PVector(xCo, centY)));
        OST.add(new OnScreenText("..", new PVector(xCo, centY - dist)));

        cp5 = new ControlP5(this);
        PFont font = createFont("DigiFont.TTF", 16);
        textAlign(CENTER);
        cp5.addTextfield("input")
                .setPosition(centX - dist, centY + (radius * 0.45f))
                .setSize((int)(radius * 1.2f), (int)(radius * 0.2f))
                .setFont(font)
                .setFocus(true)
                .getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE)
        ;
        cp5.setColorForeground(0);
        cp5.setColorBackground(color(0, 100));
        cp5.setColorActive(color(0, 0, 100));
        textFont(font);
    }

    public void draw() {
        frameRate(frames);
        background(0);

        if (ChatBot.exit && !moveText)
            exit();

        presentCounter++;
        exitCounter ++;
        // After 10 seconds, display message checking is the user is still there
        if (presentCounter == 300 && !presentCheck)
            output(stillThereMessage());
        // After two minutes, if no response from user, exit
        if (exitCounter == minute * 2.0f) {
            output("Okay, well I'm kind of busy and I have other stuff to do, so bye!");
            exit();
        }

        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        drawCorners();
        drawCenter();

        // Text Visuals
        // Pulsating colours for text box
        if (colourSwitch) colourTimer++;
        else colourTimer--;
        if (colourTimer == 45 || colourTimer == 0) colourSwitch =! colourSwitch;
        cp5.setColorActive(color(0, colourTimer * 3, colourTimer * 4));

        // Text motion
        float pos = OST.get(2).position.y;
        if (moveText && (prevPos - pos) < dist * 2.0f) {
            for (int i = 1; i < OST.size(); i++)
                OST.get(i).update();
        }
        else
            moveText = false;

        if (outTextDisplay.length() > 0 && outCount != outTextDisplay.length()) {
            try {
                Thread.sleep(5);
                outCount++;
\                OST.set(1, new OnScreenText(outTextDisplay.substring(0, outCount), new PVector(xCo, OST.get(1).position.y)));
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        else {
            outTextDisplay = "";
            outCount = 0;
        }

        writeText();
    }

    public void keyPressed() {
        presentCheck = true;
        presentCounter = 0;
        exitCounter = 0;

        // Prevent exit via escape key
        if (key == ESC) key = 0;
    }

    public void input(String input) {
        while(input.charAt(0) == ' ')
            input = input.substring(1);

        uInput = Cleaning.cleanInput(input);

        waitingIn = false;
        moveText = true;
        prevPos = centY + dist;

        OST.get(0).content = "";
        OST.get(1).content = "";

        OST.get(1).position.y = centY + (dist * 2.0f);
        OST.set(2, new OnScreenText(input, new PVector(xCo, centY + dist)));
    }

    private void writeText() {
        fill(255);
        textAlign(CENTER);

        OST.stream().filter(ost -> ost.position.y < centY + (radius * 0.6f)).filter(ost -> ost.content.length() != 0).forEach(ost -> text(ost.content, ost.position.x, ost.position.y, radius * 1.6f, radius));
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
        //taking one freq to another to make range
        for (int i = 0; i < index; i++) {
            range[i][0] = frequencies[i * index];
            range[i][1] = frequencies[i * index + (index - 1)];
        }

        //detecting frequencies
        float freq = FFTFreq();

        //checks which range it belongs to, which corner
        for (int i = 0; i < index; i++) {
            for (int j = i * index; j < (i + 1) * index; j++) {
                int k = j + 1;

                // Prevent array out of bounds exception
                if (k == 16)
                    k--;
                //maps freq to get desired freq if needed
                if (checkFreq(freq, frequencies[j], frequencies[k])) {
                    float mapped = map(freq, range[i][0], range[i][1], scrHeight * 0.3f, scrHeight);
                    if (mapped < desDiameter[i] * 0.7f || mapped > desDiameter[i] * 1.3f)
                        desDiameter[i] = mapped;
                }
            }
        }

        // Top Left, Top Right, Bottom Right, Bottom Left

        //
        for (int j = 0; j < arcs.length; j++ ) {
            CornerArc arcy = arcs[j];
            arcy.sizeMatch(desDiameter[j]); //increment + decrement arcs

            //set location, position and colour
            PVector pos = arcy.loc;
            float diam = arcy.diameter;
            int colour = arcy.colour;
            int black = color(0);

            //translate to arc positions (corners)
            pushMatrix();
            translate(pos.x, pos.y);
            //larger diameter = darker, smaller diameter = lighter
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
        //repeat bandReset till it finishes circle
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
        //going thru each step to change lengths and coordinates of line
        for (float alpha = 0.0f; alpha < TWO_PI; alpha += step, i++) {
            GraphicLines outLiney = outLines[i];
            outLiney.lineChange(outLineLengths[i]);
            PVector pos = outLiney.loc;
            //new coordinates for inside and outside line
            float outSize = outLiney.lineLength;
            float inSize = -outLiney.lineLength * 0.3f;
            PVector outEnd = new PVector(outSize * sin(alpha), outSize * cos(alpha));
            PVector inEnd = new PVector(inSize * sin(alpha), inSize * cos(alpha));

            //push and pop to new position
            pushMatrix();
            translate(pos.x, pos.y);
            line(0, 0, outEnd.x, outEnd.y);
            popMatrix();

            //new coordinates
            outCoordinates[i] = new PVector(outEnd.x + pos.x, outEnd.y + pos.y);
            inCoordinates[i] = new PVector(inEnd.x + pos.x, inEnd.y + pos.y);
        }

        drawCenterCurve(outCoordinates);
        drawCenterCurve(inCoordinates);

        //circle for amplitude
        strokeWeight(3);
        ellipse(centX, centY, radius * 2.0f, radius * 2.0f);
        //circle for text display
        fill(0);
        noStroke();
        ellipse(centX, centY, radius * 1.2f, radius * 1.2f);
    }

    private void drawCenterCurve(PVector[] coordinates) {
        //drawing curve between all the lines
        beginShape();
        //filling in gaps
        curveVertex(coordinates[coordinates.length - 1].x, coordinates[coordinates.length - 1].y);
        for (PVector coordinate : coordinates)
            curveVertex(coordinate.x, coordinate.y);
        curveVertex(coordinates[0].x, coordinates[0].y);
        curveVertex(coordinates[1].x, coordinates[1].y);
        //draws curve
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