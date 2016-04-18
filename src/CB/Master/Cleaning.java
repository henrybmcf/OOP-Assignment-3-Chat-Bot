package CB.Master;

import CB.FileCode.FileMethods;
import CB.Speech.TextSpeech;
import CB.Visuals.Visual;
import processing.core.PApplet;

import static CB.Master.ChatBot.bOutput;
import static CB.Master.ChatBot.keyWord;
import static CB.Master.ChatBot.uInput;
import static CB.Master.Checks.checkTruth;

public class Cleaning extends PApplet {
    // List of punctuations marks
    private final static String punctuation = "?!.;";

    private static TextSpeech speaking = new TextSpeech("kevin16");

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

    // Prepare bot response for output
    public static void output(String out, int src) {
        if (src != 1)
            out = preProcessOutput(out);
        if (out != null) {
            bOutput = initCap(out);
            RepeatCheck.saveResponse(bOutput);
            System.out.println(bOutput);
//            Visual.presentCheck = false;
//            Visual.presentCounter = 0;
//            Visual.exitCounter = 0;
//            speaking.speak(bOutput);

            Visual.outTextDisp = bOutput;
            Visual.outDisp = true;
        }
    }

    static String cleanOutput() {
        return bOutput.substring(1);
    }

    private static String apostropheReplace(String str) {
        StringBuilder strBuild = new StringBuilder(str.length());

        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != '\'')
                strBuild.append(str.charAt(i));
            else if (str.charAt(i + 1) == 's') {
                strBuild.append(" is");
                i++;
            }
        }

        return strBuild.toString();
    }

    // Capitalise first letter of string
    public static String initCap(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // indexOf returns -1 if the character does not occur in the string
    // If ch is not in punctuation, -1 will be returned (true)
    private static boolean punctuationCheck(char ch) {
        return punctuation.indexOf(ch) != -1;
    }

    static String toName(String str) {
        StringBuilder convert = new StringBuilder(str.length());

        String[] tokens = ConvoContext.splitString(str, " ");

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = initCap(tokens[i]);
            convert.append(tokens[i]);
            if (i < tokens.length - 1)
                convert.append(" ");
        }

        str = convert.toString();

        return str;
    }

    static String firstName(String name) {
        return ConvoContext.splitString(name, " ")[0];
    }

    private static String preProcessOutput(String resp) {
        String subject = "";

        if (resp.contains("*"))
            subject = findSubject();

        if (checkTruth(resp, subject))
            return resp.replaceFirst("\\*", " " + subject);

        return null;
    }

    // Find subject within user input, for use in output
    private static String findSubject() {
        String subject = "";

        int keyPos = uInput.indexOf(keyWord);

        if (keyPos != -1)
            subject = uInput.substring(keyPos + keyWord.length() + 1, uInput.length());

        return subject;
    }
}
