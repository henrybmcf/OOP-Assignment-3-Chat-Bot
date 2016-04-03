package CB.Master;

class Cleaning {
    // List of punctuations marks
    private final static String punctuation = "?!.;";

    // Clean up user input: Remove white space and punctuation & convert to lower case
    static String cleanInput(String str) {
        StringBuilder cleaning = new StringBuilder(str.length());

        char prevChar = 0;

        for(int i = 0; i < str.length(); i++) {
            if((str.charAt(i) == ' ' && prevChar == ' ') || !puncCheck(str.charAt(i))) {
                cleaning.append(str.charAt(i));
                prevChar = str.charAt(i);
            }
            else if(prevChar != ' ' && puncCheck(str.charAt(i)))
                cleaning.append("");
        }
        str = cleaning.toString().toLowerCase();

        if (str.contains("'"))
            str = apostropheReplace(str);

        return str;
    }

    static String cleanOutput() {
        return ChatBot.bOutput.substring(1);
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
    static String initCap(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // indexOf returns -1 if the character does not occur in the string
    // If ch is not in punctuation, -1 will be returned (true)
    private static boolean puncCheck(char ch) {
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
}
