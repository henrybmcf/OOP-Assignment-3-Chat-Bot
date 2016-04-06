import processing.core.*;

public class Gradient extends PApplet {

    private int c1, c3, c2, c4, cBlack;
    private int [] colours = new int [5];

    public void settings() {
        size(800, 600);

        colours[0] = color(204, 102, 0);

        colours[1] = color(100, 200, 100);
        colours[2] = color(0, 102, 253);
        colours[3] = color(255, 0, 127);
        colours[4]= color(0);

//        gradWidth1 = 2000;
//        gradWidth1 = 2000;
//        gradWidth2 = 2000;
//
//        gradHeight1 = 200;
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

        int size = height;
        for (int i = 0; i < 4; i++)
        {
            pushMatrix();
            switch (i)
            {
                case 1: //top right
                    translate(width, 0);
                    break;

                case 2: //bottom right
                    translate (width, height);
                    break;

                case 3: //bottom left
                    translate (0, height);
                    break;
            }//end switch
            setGradient(0, 0, size, colours[i], colours[colours.length - 1], i * HALF_PI, (i+1)*HALF_PI);

            popMatrix();
        }
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
