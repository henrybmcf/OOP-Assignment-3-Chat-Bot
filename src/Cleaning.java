public class Cleaning {
    // Clean up user input: Remove white space and punctuation & convert to lower case
    public static String cleanInput(String str) {
        StringBuilder cleaning = new StringBuilder(str.length());

        char prevChar = 0;

        for(int i = 0; i < str.length(); i++) {
            if((str.charAt(i) == ' ' && prevChar == ' ') || !ChatBot.puncCheck(str.charAt(i))) {
                cleaning.append(str.charAt(i));
                prevChar = str.charAt(i);
            }
            else if(prevChar != ' ' && ChatBot.puncCheck(str.charAt(i))) {
                cleaning.append("");
            }

        }
        str = cleaning.toString().toLowerCase();

        if (str.contains("'"))
            str = ChatBot.aposReplace(str);

        return str;
    }

    public static String cleanOutput() {
        String str;
        str = ChatBot.bOutput.substring(1);
        return str;
    }
}
