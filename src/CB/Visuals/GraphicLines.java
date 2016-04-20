package CB.Visuals;

import processing.core.PApplet;
import processing.core.PVector;

public class GraphicLines extends PApplet {
    PVector loc;
    float lineLength;
    private float incSize;
    private float decSize;

    //Constructor
    //line increment or decrement
    GraphicLines(float startX, float startY, float lineLength) {
        loc = new PVector(startX, startY);
        this.lineLength = lineLength;

        incSize = 1.05f;
        decSize = 0.95f;
    }

    // Increase or decrease to desired length if line is not ideal
    void lineChange(float desLength) {
        if (lineLength < desLength)
            lineLength *= incSize;
        else if (lineLength > desLength)
            lineLength *= decSize;
    }
}