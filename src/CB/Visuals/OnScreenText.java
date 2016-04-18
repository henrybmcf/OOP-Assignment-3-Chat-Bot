package CB.Visuals;

import CB.Master.ChatBot;
import processing.core.PApplet;
import processing.core.PVector;

public class OnScreenText extends PApplet {
    String content;
    PVector position;

    float moving;

    OnScreenText(String content, PVector position) {
        this.content = content;
        this.position = position;

        moving = 1.5f;
    }

    void render() {
        text(content, position.x, position.y);
    }

    void update() {
            position.y -= moving;
    }
}
