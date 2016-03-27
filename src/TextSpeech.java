import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TextSpeech {
    private static String voiceName;

    public TextSpeech(String voice) {
        voiceName = voice;
    }

    public void speak(String text) {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice(voiceName);
        voice.allocate();
        voice.speak(text);
    }
}