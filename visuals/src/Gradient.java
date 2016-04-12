import processing.core.PApplet;
import processing.core.PVector;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;

import java.util.ArrayList;

public class Gradient extends PApplet {
    private AudioInput in;
    private FFT fft;
    private int index = 4;
    private int[] colours = new int[index];
    private float[] frequencies = {
            256f, 293f, 1468f, 1566f,
            329f, 369f, 1272f, 1370f,
            587f, 659f, 2256f, 2354f,
            739f, 783f, 2060f, 2158f
    };
    private float desDiameter[] = new float[index];
    private ArrayList<CornerArc> arcs = new ArrayList<>();
    private float scrHeight;

    private float centX;
    private float centY;
    private float radius;
    private float step;
    private ArrayList<Float> outLineLengths = new ArrayList<>();
    private ArrayList<Float> inLineLengths = new ArrayList<>();
    private ArrayList<GraphicLines> outLines = new ArrayList<>();
    private ArrayList<GraphicLines> inLines = new ArrayList<>();
    private static ArrayList<PVector> outCoordinates = new ArrayList<>();
    private static ArrayList<PVector> inCoordinates = new ArrayList<>();

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

        // Assign colours for each corner
        colours[0] = color(204, 102, 0);
        colours[1] = color(150, 250, 50);
        colours[2] = color(50, 50, 200);
        colours[3] = color(204, 0, 204);

        for (int i = 0; i < desDiameter.length; i++)
            desDiameter[i] = scrHeight * 0.3f;

        CornerArc cArc = new CornerArc(0, 0, desDiameter[0], colours[0]);
        arcs.add(cArc);
        cArc = new CornerArc(scrWidth, 0, desDiameter[0], colours[1]);
        arcs.add(cArc);
        cArc = new CornerArc(scrWidth, scrHeight, desDiameter[0], colours[2]);
        arcs.add(cArc);
        cArc = new CornerArc(0, scrHeight, desDiameter[0], colours[3]);
        arcs.add(cArc);

        radius = 200.0f;

        step = TWO_PI / (fft.specSize() * 0.5f);

        for (float alpha = 0.0f; alpha < TWO_PI; alpha += step) {
            float X = radius * sin(alpha) + centX;
            float Y = radius * cos(alpha) + centY;
            float lineLength = 10.0f;

            outLines.add(new GraphicLines(X, Y, lineLength));
            inLines.add(new GraphicLines(X, Y, lineLength * 0.5f));

            outLineLengths.add(lineLength * 10.0f);
            inLineLengths.add(lineLength * 5.0f);

            outCoordinates.add(new PVector(0, 0));
            inCoordinates.add(new PVector(0, 0));
        }
    }

    public void draw() {
        background(0);

        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        float[][] range = new float[index][2];

        for (int i = 0; i < index; i++) {
            int j = i * index;
            int k = j + (index - 1);
            range[i][0] = frequencies[j];
            range[i][1] = frequencies[k];
        }

        boolean[] sizing = new boolean[index];

        float freq = FFTFreq();

        for (int i = 0 ; i < index; i++) {
            for (int j = i * index; j < (i + 1) * index; j++) {
                int k = j + 1;

                // Prevent array out of bounds exception
                if (k == 16)
                    k--;

                if (checkFreq(freq, frequencies[j], frequencies[k])) {
                    sizing[i] = true;
                    float mapped = map(freq, range[i][0], range[i][1], scrHeight * 0.3f, scrHeight);
                    if (mapped < desDiameter[i] * 0.7f || mapped > desDiameter[i] * 1.3f)
                        desDiameter[i] = mapped;
                }
            }
        }

        for (int i = 0; i < sizing.length; i++) {
            if (!sizing[i])
                desDiameter[i] = scrHeight * 0.4f;
        }

        strokeWeight(4);

//         Top Left, Top Right, Bottom Right, Bottom Left
//         Size Match method call for all arcs
        for (int j = 0; j < arcs.size(); j++ ) {
            CornerArc arcy = arcs.get(j);
            arcy.sizeMatch(desDiameter[j]);

            PVector pos = arcy.loc;
            float diam = arcy.diameter;
            int colour = arcy.colour;
            int black = color(0);

            pushMatrix();
            translate(pos.x, pos.y);

            for (float i = 0 + diam; i >= 0; i -= 1) {
                float inter = map(i, 0, diam, 0, 1);
                stroke(lerpColor(colour, black, inter));
                arc(0, 0, i, i, j * HALF_PI, (j + 1) * HALF_PI);
            }
            popMatrix();
        }

        strokeWeight(1);
        noFill();
        stroke(255);
        ellipse(centX, centY, radius * 2.0f, radius * 2.0f);

        for (float alpha = 0.0f, i = 0; alpha < TWO_PI; alpha += step, i++) {
            GraphicLines outLiney = outLines.get((int) i);
            GraphicLines inLiney = inLines.get((int) i);

            outLiney.lineChange(outLineLengths.get((int) i));
            inLiney.lineChange(inLineLengths.get((int) i));

            PVector pos = outLiney.loc;

            float outSize = outLiney.lineLength;
            float inSize = -inLiney.lineLength;

            PVector outEnd = new PVector(outSize * sin(alpha), outSize * cos(alpha));
            PVector inEnd = new PVector(inSize * sin(alpha), inSize * cos(alpha));

            pushMatrix();
            translate(pos.x, pos.y);
            line(0, 0, outEnd.x, outEnd.y);
            line(0, 0, inEnd.x, inEnd.y);
            popMatrix();

            PVector outEndCo = new PVector(outEnd.x + pos.x, outEnd.y + pos.y);
            outCoordinates.set((int) i, outEndCo);

            PVector inEndCo = new PVector(inEnd.x + pos.x, inEnd.y + pos.y);
            inCoordinates.set((int) i, inEndCo);
        }

        drawCurve(outCoordinates);
        drawCurve(inCoordinates);

        int bandReset = 16;
        int k = 0;
        for (int i = 0; i < bandReset; i ++) {
            for (int j = i; j < fft.specSize() * 0.5f; j += bandReset, k++) {
                float band = fft.getBand(j) * 150;
                if (band > 20.0f) {
                    if (band > centX * 0.4f)
                        band = band * 0.2f;
                    outLineLengths.set(k, band);
                    if (band * 0.3f < 25.0f)
                        inLineLengths.set(k, band * 0.3f);
                }
                else {
                    outLineLengths.set(k, random(20.0f, 80.0f));
                    inLineLengths.set(k, outLineLengths.get(k));
                }
            }
        }
    }

    private void drawCurve(ArrayList<PVector> coordinates) {
        beginShape();
        for (PVector coordinate : coordinates)
            curveVertex(coordinate.x, coordinate.y);
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