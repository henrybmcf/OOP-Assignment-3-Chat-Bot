import processing.core.PApplet;
import processing.core.PVector;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import java.util.ArrayList;

public class Gradient extends PApplet {
    private AudioInput in;
    private FFT fft;

    // Corner Visuals
    private int index = 4;
    private int[] colours = new int[index];
    private float[] frequencies = {
            256f, 293f, 1468f, 1566f,
            329f, 369f, 1272f, 1370f,
            587f, 659f, 2256f, 2354f,
            739f, 783f, 2060f, 2158f
    };
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

    public void settings() {
        //size(800, 600);
        fullScreen();
        smooth(8);

        // Fullscreen
        float scrWidth = displayWidth;
        scrHeight = displayHeight;
        centX = displayWidth * 0.5f;
        centY = displayHeight * 0.5f;

        // Windowed
//        float scrWidth = width;
//        scrHeight = height;

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
        for (int i = 0; i < desDiameter.length; i++)
            desDiameter[i] = scrHeight * 0.3f;
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
        frameRate(30);
        background(0);

        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        // Corner Visuals
        float[][] range = new float[index][2];
        for (int i = 0; i < index; i++) {
            range[i][0] = frequencies[i * index];
            range[i][1] = frequencies[i * index + (index - 1)];
        }

        float freq = FFTFreq();

        for (int i = 0 ; i < index; i++) {
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
        strokeWeight(4);

//         Top Left, Top Right, Bottom Right, Bottom Left
//         Size Match method call for all arcs
        for (int j = 0; j < arcs.length; j++ ) {
            CornerArc arcy = arcs[j];
            arcy.sizeMatch(desDiameter[j]);

            PVector pos = arcy.loc;
            float diam = arcy.diameter;
            int colour = arcy.colour;
            int black = color(0);

            pushMatrix();
            translate(pos.x, pos.y);
            for (float i = 0 + diam; i >= 0; i--) {
                float inter = map(i, 0, diam, 0, 1);
                stroke(lerpColor(colour, black, inter));
                arc(0, 0, i, i, j * HALF_PI, (j + 1) * HALF_PI);
            }
            popMatrix();
        }


        // Center Visual
        strokeWeight(1);
        noFill();
        stroke(255);
        ellipse(centX, centY, radius * 2.0f, radius * 2.0f);

        int bandReset = 16;
        int k = 0;
        for (int i = 0; i < bandReset; i ++) {
            for (int j = i; j < fft.specSize() * 0.5f; j += bandReset, k++) {
                float band = fft.getBand(j) * 150;
                if (band > 20.0f) {
                    if (band > centX * 0.4f)
                        band = band * 0.2f;
                    outLineLengths[k] = band;
                }
                else
                    outLineLengths[k] = random(20.0f, 80.0f);
            }
        }

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
            //line(0, 0, inEnd.x, inEnd.y);
            popMatrix();

            outCoordinates[i] = new PVector(outEnd.x + pos.x, outEnd.y + pos.y);
            inCoordinates[i] = new PVector(inEnd.x + pos.x, inEnd.y + pos.y);
        }

        drawCurve(outCoordinates);
        drawCurve(inCoordinates);
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

    // Returns frequency
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

    // Checks if frequency is within range
    private boolean checkFreq(float freq, float low, float high) {
        return freq > low && freq < high;
    }

    public static void main(String[] args) { PApplet.main(Gradient.class.getName()); }
}