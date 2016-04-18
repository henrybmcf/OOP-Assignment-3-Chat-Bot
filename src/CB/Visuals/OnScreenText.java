package CB.Visuals;

import processing.core.PApplet;
import processing.core.PVector;

public class OnScreenText extends PApplet {
    String content;
    PVector position;

    private float moving;

    OnScreenText(String content, PVector position) {
        this.content = content;
        this.position = position;

        moving = 7.5f;
    }

    void update() {
            position.y -= moving;
    }
}
