import processing.core.*;
        import ddf.minim.AudioInput;
        import ddf.minim.Minim;
        import ddf.minim.analysis.FFT;

public class Gradient extends PApplet {
    Minim minim;
    AudioInput in;
    private FFT fft;

    private int[] colours = new int[5];

    public void settings() {
        size(800, 600);
        //fullScreen();

        int frameSize = 1024;
        int sampleRate = 44100;

        minim = new Minim(this);
        in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
        fft = new FFT(frameSize, sampleRate);

        colours[0] = color(204, 102, 0);
        colours[1] = color(150, 250, 50);
        colours[2] = color(50, 50, 200);
        colours[3] = color(204, 0, 204);
        colours[4] = color(0);
    }

    public void draw() {
        background(0);

        fft.window(FFT.HAMMING);
        fft.forward(in.left);


        float[] frequencies = {
                293.66f, 329.63f, 369.99f, 392.00f,
                440.00f, 493.88f, 554.37f, 587.33f,
                659.25f, 739.99f, 783.99f, 880.00f,
                987.77f, 1108.73f, 1174.66f};

//        float size = displayHeight;
        float size = height;

        noFill();

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
            setGradient(0, 0, size, colours[i], colours[colours.length - 1], i * HALF_PI, (i + 1) * HALF_PI);
            popMatrix();
        }


    }

    private void setGradient(float startX, int startY, float gSize, int colour1, int colour2, float start, float stop) {
        for (float i = startY + gSize; i >= startY; i -= 0.1) {
            float inter = map(i, startY, startY + gSize, 0, 1);
//            int c = lerpColor(c1, c2, inter);
            stroke(lerpColor(colour1, colour2, inter));
            arc(startX, startY, i, i, start, stop);
        }
    }

    // Returns frequency
    public float FFTFreq() {
        float maxValue = Float.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0 ; i < fft.specSize() ; i ++) {
            if (fft.getBand(i) > maxValue) {
                maxValue = fft.getBand(i);
                maxIndex = i;
            }
        }
        return fft.indexToFreq(maxIndex);
    }

    public static void main(String[] args) { PApplet.main(Gradient.class.getName()); }
}
