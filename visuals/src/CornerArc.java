import processing.core.PVector;

class CornerArc {
    PVector loc;
    float diameter;
    int colour;

    private float incSize;
    private float decSize;

    // Constructor
    CornerArc(float xCo, float yCo, float diameter, int colour) {
        loc = new PVector(xCo, yCo);
        this.diameter = diameter;
        this.colour = colour;

        incSize = 1.01f;
        decSize = 0.99f;
    }

    // Increase or decrease to desired diameter
    void sizeMatch(float desDiam) {
        if (diameter < desDiam)
            diameter *= incSize;
        else if (diameter > desDiam)
            diameter *= decSize;
    }
}
