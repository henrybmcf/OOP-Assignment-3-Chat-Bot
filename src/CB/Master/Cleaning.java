package CB.Master;

import CB.Master.ChatBot;

public class Cleaning {
    // Clean up user input: Remove white space and punctuation & convert to lower case
    public static String cleanInput(String str) {
        StringBuilder cleaning = new StringBuilder(str.length());

        char prevChar = 0;

        for(int i = 0; i < str.length(); i++) {
            if((str.charAt(i) == ' ' && prevChar == ' ') || !puncCheck(str.charAt(i))) {
                cleaning.append(str.charAt(i));
                prevChar = str.charAt(i);
            }
            else if(prevChar != ' ' && puncCheck(str.charAt(i))) {
                cleaning.append("");
            }

        }
        str = cleaning.toString().toLowerCase();

        if (str.contains("'"))
            str = aposReplace(str);

        return str;
    }

    public static String cleanOutput() {
        String str;
        str = ChatBot.bOutput.substring(1);
        return str;
    }

    public static String aposReplace(String str) {
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
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }

    // indexOf returns -1 if the character does not occur in the string
    // If ch is not in punctuation, -1 will be returned (true)
    public static boolean puncCheck(char ch) {
        return ChatBot.punctuation.indexOf(ch) != -1;
    }
}
