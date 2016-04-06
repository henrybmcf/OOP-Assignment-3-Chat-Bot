import processing.core.*;

public class Gradient extends PApplet {

    private int c1, c3, c2, c4, cBlack;

    private int gradWidth1;
    private int gradWidth2;

    private int gradHeight1;

    public void settings() {
        size(800, 600);

        c1 = color(204, 102, 0);

        c3 = color(100, 200, 100);
        c2 = color(0, 102, 253);
        c4 = color(255, 0, 127);
        cBlack = color(0);

//        gradWidth1 = 2000;
        gradWidth1 = 2000;
        gradWidth2 = 2000;

        gradHeight1 = 200;
//        noLoop();
    }

    public void draw() {
        background(0);

        // Top Left
//        pushMatrix();
//        rotate(PI * -0.25f);
//        translate(-gradWidth1 * 0.75f, 0);
//        setGradient(0, 0, gradWidth1, gradHeight1, c1, c2);
//        popMatrix();

        // Bottom Left
//        pushMatrix();
//        translate(gradWidth1 * 0.25f, height + gradHeight1 * 1.25f);
//        rotate(PI * 1.25f);
//        setGradient(0, 0, gradWidth1, gradHeight1, c3, c2);
//        popMatrix();


        setGradient(0, 0, height, c1, cBlack, 0, HALF_PI);

        pushMatrix();
        translate(0, height);
        setGradient(0, 0, height, c3, cBlack, PI + HALF_PI, TWO_PI);
        popMatrix();

        pushMatrix();
        translate(width, height);
        setGradient(0, 0, height, c2, cBlack, HALF_PI, TWO_PI);
        popMatrix();

        pushMatrix();
        translate(width, 0);
        setGradient(0, 0, height, c4, cBlack, HALF_PI, PI);
        popMatrix();

        // Corner 2 (Top Right)
//        pushMatrix();
//        //rotate(PI * 0.25f);
//        //translate(width - gradWidth2 * 0.75f, 0);
//        translate(width, 0);
//        setGradient(0, 0, gradWidth2, 400, c3, c2);
//        popMatrix();
    }

    private void setGradient(float startX, int startY, float gSize, int c1, int c2, float start, float stop) {
        for (float i = startY + gSize; i >= startY; i -= 0.1) {
            float inter = map(i, startY, startY + gSize, 0, 1);
            int c = lerpColor(c1, c2, inter);
            stroke(c);
            //line(startX, i, startX + gWidth, i);
            noFill();
            arc(startX, startY, i, i, start, stop);
        }
    }

    public static void main(String[] args) { PApplet.main(Gradient.class.getName()); }
}
