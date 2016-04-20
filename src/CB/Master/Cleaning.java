package CB.Master;

import CB.Speech.TextSpeech;
import processing.core.PApplet;

import static CB.Master.ChatBot.bOutput;
import static CB.Visuals.Visual.outTextDisplay;
import static CB.Visuals.Visual.presentCheck;
import static CB.Visuals.Visual.presentCounter;
import static CB.Visuals.Visual.exitCounter;
import static CB.Master.RepeatCheck.saveResponse;

public class Cleaning extends PApplet {
    // List of punctuations marks
    private final static String punctuation = "?!.;";
    // Text-to-Speech
    private static TextSpeech speaking = new TextSpeech();

    // Clean up user input: Remove white space and punctuation & convert to lower case
    public static String cleanInput(String str) {
        StringBuilder cleaning = new StringBuilder(str.length());

        char prevChar = 0;

        for(int i = 0; i < str.length(); i++) {
            if((str.charAt(i) == ' ' && prevChar == ' ') || !punctuationCheck(str.charAt(i))) {
                cleaning.append(str.charAt(i));
                prevChar = str.charAt(i);
            }
            else if(prevChar != ' ' && punctuationCheck(str.charAt(i)))
                cleaning.append("");
        }
        str = cleaning.toString().toLowerCase();

        if (str.contains("'"))
            str = apostropheReplace(str);

        return str;
    }

    // Prepare and output bot response
    public static void output(String out) {
        assert out != null;
        bOutput = initCap(out);
        saveResponse(bOutput);
        outTextDisplay = bOutput;
        presentCheck = false;
        presentCounter = 0;
        exitCounter = 0;
        speaking.speak(bOutput);
    }

    // Remove first character of output ('R' for response)
    static String cleanOutput() {
        return bOutput.substring(1);
    }

    // Replace apostrophes in user input ('s -> is, 'm -> am)
    private static String apostropheReplace(String str) {
        StringBuilder strBuild = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != '\'')
                strBuild.append(str.charAt(i));
            else if (str.charAt(i + 1) == 's') {
                strBuild.append(" is");
                i++;
            }
            else if (str.charAt(i + 1) == 'm') {
                strBuild.append(" am");
                i++;
            }
        }

        return strBuild.toString();
    }

    // Capitalise first letter of string
    private static String initCap(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // indexOf returns -1 if the character does not occur in the string
    // If ch is not in punctuation, -1 will be returned (true)
    private static boolean punctuationCheck(char ch) {
        return punctuation.indexOf(ch) != -1;
    }

    // Convert input to name (capitalise each word)
    static String toName(String str) {
        StringBuilder convert = new StringBuilder(str.length());

        String[] tokens = splitString(str, " ");

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = initCap(tokens[i]);
            convert.append(tokens[i]);
            if (i < tokens.length - 1)
                convert.append(" ");
        }

        str = convert.toString();

        return str;
    }

    // Convert to first name (take first word of name)
    static String firstName(String name) {
        return splitString(name, " ")[0];
    }

    // Split string on passed on splitter
    static String[] splitString(String str, String splitter) {
        return str.split(splitter);
    }
}