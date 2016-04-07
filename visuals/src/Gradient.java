import processing.core.*;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;

public class Gradient extends PApplet {
    private AudioInput in;
    private FFT fft;

    private int[] colours = new int[5];

//    private float[] frequencies = {
//            256f, 392f, 587f, 880f, 1272f, 1664f, 2060f, 2455f,
//            293f, 440f, 659f, 987f, 1370f, 1762f, 2158f, 2553f,
//            329f, 493f, 739f, 1108f, 1468f, 1860f, 2256f, 2652f,
//            369f, 554f, 783f, 1174f, 1566f, 1962f, 2354f, 2750f
//    };

//    private float[] frequencies = {
//            256f, 587f, 1272f, 2060f,
//            293f, 659f, 1370f, 2158f,
//            329f, 739f, 1468f, 2256f,
//            369f, 783f, 1566f, 2354f,
//    };

//    private float[] frequencies = {
//            256f, 293f, 329f, 369f,
//            587f, 659f, 739f, 783f,
//            1272f, 1370f, 1468f, 1566f,
//            2060f, 2158f, 2256f, 2354f
//    };

    private float[] frequencies = {
            256f, 293f, 1468f, 1566f,
            329f, 369f, 1272f, 1370f,
            587f, 659f, 2256f, 2354f,
            739f, 783f, 2060f, 2158f
    };

    private float diameter[] = new float[4];


    public void settings() {
        size(800, 600);
        //fullScreen();
        smooth(8);

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

        for (int i = 0; i < diameter.length; i++)
            diameter[i] = height * 0.3f;
    }

    public void draw() {
        background(0);

        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        int index = frequencies.length / 4;
        //int quart = index * 2;

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

                if (k == 16)
                    k--;

                if (checkFreq(freq, frequencies[j], frequencies[k])) {
                    sizing[i] = true;
                    diameter[i] = map(freq, range[i][0], range[i][1], height * 0.3f, height);
                }
            }
        }

        for (int i = 0; i < sizing.length; i++) {
            if (!sizing[i])
                diameter[i] = height * 0.3f;
        }

//        float size = displayHeight;

        noFill();
        strokeWeight(2);

        // Top Left, Top Right, Bottom Right, Bottom Left
        for (int i = 0; i < 4; i++) {
            pushMatrix();
            switch (i) {
                case 1:
                    translate(width, 0);
                    break;
                case 2:
                    translate(width, height);
                    break;
                case 3:
                    translate(0, height);
                    break;
            }
            setGradient(0, 0, diameter[i], colours[i], colours[colours.length - 1], i * HALF_PI, (i + 1) * HALF_PI);
            popMatrix();
        }
    }

    private boolean checkFreq(float freq, float low, float high) {
        return freq > low && freq < high;
    }

    private void setGradient(float startX, int startY, float gSize, int colour1, int colour2, float start, float stop) {
        for (float i = startY + gSize; i >= startY; i -= 1) {
            float inter = map(i, startY, startY + gSize, 0, 1);
//            int c = lerpColor(c1, c2, inter);
            stroke(lerpColor(colour1, colour2, inter));
            arc(startX, startY, i, i, start, stop);
        }
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

    public static void main(String[] args) { PApplet.main(Gradient.class.getName()); }
}