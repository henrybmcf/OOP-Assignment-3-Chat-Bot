import processing.core.PApplet;

//public class Visuals extends PApplet {
//    public void settings() {
//        size(200, 200);
//    }
//
//    public void draw() {
//        background(125);
//        System.out.println("HelloWorld");
//    }
//
//    public static void main(String[] args) {
//        PApplet.main("Visuals");
//    }
//}



import java.util.ArrayList;


import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.WindowFunction;

public class Visuals extends PApplet
{
    Minim minim;
    AudioInput in;
    float min;
    float max;

    int sampleRate = 44100;
    int frameSize = 1024;
    FFT fft;


    public void settings()
    {
        size(frameSize, 500);
        smooth();
        minim = new Minim(this);

        in = minim.getLineIn(Minim.MONO, frameSize, sampleRate, 16);
        fft = new FFT(frameSize, sampleRate);
        min = Float.MAX_VALUE;
        max = Float.MIN_VALUE;
    }


    public int countZeroCrossings()
    {
        int count = 0;

        for (int i = 1 ; i < in.bufferSize(); i ++)
        {
            if (in.left.get(i - 1) > 0 && in.left.get(i) <= 0)
            {
                count ++;
            }
        }
        return count;
    }

    public float FFTFreq()
    {
        // Find the higest entry in the FFT and convert to a frequency
        float maxValue = Float.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0 ; i < fft.specSize() ; i ++)
        {
            if (fft.getBand(i) > maxValue)
            {
                maxValue = fft.getBand(i);
                maxIndex = i;
            }
        }
        return fft.indexToFreq(maxIndex);
    }

    public void draw() {
        background(0);
        stroke(255);
        float average = 0;

        for (int i = 0; i < in.bufferSize(); i++) {
            float sample = in.left.get(i);

            if (sample < min) {
                min = sample;
            }

            if (sample > max) {
                max = sample;
            }
            sample *= 100.0;

            average += Math.abs(in.left.get(i));

        }


        average /= in.bufferSize();

        stroke(0, 255, 255);


        fill(255);

        float smallRadius = 50;
        float bigRadius = (smallRadius * 2) + (average * 500);

        stroke(0, 255, 0);
        fill(0, 255, 0);
        ellipse(width / 2, height / 2, bigRadius, bigRadius);
        stroke(0);
        fill(0);
        ellipse(width / 2, height / 2, smallRadius, smallRadius);
    }

    public static void main(String[] args)
    {
        PApplet.main(Visuals.class.getName());
    }
}
