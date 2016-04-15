package edu.cmu.sphinx.demo.hellowrld;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Tts;

    public class stt {

        public static void main(String[] args) {
            ConfigurationManager cm;

            if (args.length > 0) {
                cm = new ConfigurationManager(args[0]);
            } else {
                ///tmp/helloworld.config.xml
                cm = new ConfigurationManager(Speech.class.getResource("speech.config.xml"));

            }
            Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
            recognizer.allocate();

            Microphone microphone = (Microphone) cm.lookup("microphone");
            if (!microphone.startRecording()) {
                System.out.println("Cannot start microphone.");
                recognizer.deallocate();
                System.exit(1);
            }

            System.out.println("Say: (Hello | call) ( Naam | Baam | Caam | Some )");

            while (true) {
                System.out.println("Start speaking. Press Ctrl-C to quit.\n");

                Result result = recognizer.recognize();

                if (result != null) {
                    String resultText = result.getBestFinalResultNoFiller();
                    System.out.println("You said: " + resultText + '\n');

                    Tts ts = new Tts();
                    try {
                        ts.load();
                        ts.say("Did you said: " + resultText);
                    } catch (IOException ex) {

                    }
                } else {
                    System.out.println("I can't hear what you said.\n");
                }
            }
        }
    }

