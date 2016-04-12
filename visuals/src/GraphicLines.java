import processing.core.PApplet;
import processing.core.PVector;

public class GraphicLines extends PApplet {
    PVector loc;
    float lineLength;
    private float incSize;
    private float decSize;

    GraphicLines(float startX, float startY, float lineLength) {
        loc = new PVector(startX, startY);
        this.lineLength = lineLength;

        incSize = 1.07f;
        decSize = 0.93f;
    }

    void lineChange(float desLength) {
        if (lineLength < desLength)
            lineLength *= incSize;
        else if (lineLength > desLength)
            lineLength *= decSize;
    }
}
