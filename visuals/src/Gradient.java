import processing.core.*;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;

import java.util.ArrayList;

public class Gradient extends PApplet {
    private AudioInput in;
    private FFT fft;

    private int[] colours = new int[5];

    private float[] frequencies = {
            256f, 293f, 1468f, 1566f,
            329f, 369f, 1272f, 1370f,
            587f, 659f, 2256f, 2354f,
            739f, 783f, 2060f, 2158f
    };

    private float desDiameter[] = new float[4];

    private ArrayList<CornerArc> arcs = new ArrayList<>();

    private float scrHeight;

    public void settings() {
        //size(800, 600);
        fullScreen();
        smooth(8);

        // Fullscreen
        float scrWidth = displayWidth;
        scrHeight = displayHeight;

        // Windowed
//        scrWidth = width;
//        scrHeight = height;

        int frameSize = 1024;
        int sampleRate = 44100;

        Minim minim = new Minim(this);
        in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
        fft = new FFT(frameSize, sampleRate);

        colours[0] = color(204, 102, 0);
        colours[1] = color(150, 250, 50);
        colours[2] = color(50, 50, 200);
        colours[3] = color(204, 0, 204);
        colours[4] = color(0);

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
    }

    public void draw() {
        background(0);

        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        int index = frequencies.length / 4;

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

//        for (int i = 0; i < sizing.length; i++) {
//            if (!sizing[i])
//                desDiameter[i] = scrHeight * 0.4f;
//        }

        strokeWeight(4);

        // Top Left, Top Right, Bottom Right, Bottom Left
        // Size Match method call for all arcs
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

        noFill();
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