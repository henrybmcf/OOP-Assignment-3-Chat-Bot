import processing.core.PApplet;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PVector;

import java.util.ArrayList;

public class Visuals extends PApplet {
    private AudioInput in;
    private FFT fft;
    private float centX;
    private float centY;

    private float radius;

    float step;

    ArrayList<Float> lineLengths = new ArrayList<>();

    ArrayList<GraphicLines> lines = new ArrayList<>();

    private static ArrayList<PVector> coordinates = new ArrayList<>();

    public void settings() {
        //size(800, 500);
        fullScreen();

        Minim minim = new Minim(this);
        int sampleRate = 44100;
        int frameSize = 1024;
        in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
        fft = new FFT(frameSize, sampleRate);
        centX = width * 0.5f;
        centY = height * 0.5f;

        centX = displayWidth * 0.5f;
        centY = displayHeight * 0.5f;

        radius = 200.0f;

        step = TWO_PI / (fft.specSize() * 0.5f);

        for (float alpha = 0.0f; alpha < TWO_PI; alpha += step) {
            float X = radius * sin(alpha) + centX;
            float Y = radius * cos(alpha) + centY;
            float lineLength = 10.0f;

            lines.add(new GraphicLines(X, Y, lineLength));
            lineLengths.add(lineLength * 10.0f);
            coordinates.add(new PVector(0, 0));
        }
    }

    public void draw() {
        background(0);
        fft.window(FFT.HAMMING);
        fft.forward(in.left);

        noFill();
        stroke(255);
        ellipse(centX, centY, radius * 2.0f, radius * 2.0f);

        for (float alpha = 0.0f, i = 0; alpha < TWO_PI; alpha += step, i++) {
            GraphicLines liney = lines.get((int) i);

            liney.lineChange(lineLengths.get((int) i));

            PVector pos = liney.loc;
            float size = liney.lineLength;

            PVector end = new PVector(size * sin(alpha), size * cos(alpha));

            pushMatrix();
            translate(pos.x, pos.y);
            line(0, 0, end.x, end.y);
            popMatrix();

            PVector endCo = new PVector(size * sin(alpha) + pos.x, size * cos(alpha) + pos.y);
            coordinates.set((int) i, endCo);
        }

        drawCurve();

        int bandReset = 16;
        int k = 0;
        for (int i = 0; i < bandReset; i ++) {
            for (int j = i; j < fft.specSize() * 0.5f; j += bandReset, k++) {
                float band = fft.getBand(j) * 150;
                if (band > 20.0f) {
                    if (band > centX * 0.4f)
                        band = band * 0.2f;
                    lineLengths.set(k, band);
                }
                else
                    lineLengths.set(k, random(20.0f, 80.0f));
            }
        }
    }

    private void drawCurve() {
        beginShape();
        for (PVector coordinate : coordinates)
            curveVertex(coordinate.x, coordinate.y);
        endShape();
    }

    public static void main(String[] args) { PApplet.main(Visuals.class.getName()); }
}
