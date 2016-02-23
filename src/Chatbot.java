/**
 * Created by HenryBallingerMcFarlane on 23/02/2016.
 */

import java.util.*;
import java.util.Random;

public class Chatbot {
    public static String uInput;
    public static String bOutput;

    static String[] knowledge = {
            "I heard you!",
            "So, you are talking to me.",
            "Continue, I'm listening",
            "Very interesting conversation.",
            "Please, tell me more.."
    };

    public static void main(String[] args) {
        System.out.println("Hello!");
        do {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String uInput = scanner.nextLine();

            if (uInput.equalsIgnoreCase("bye")) {
                System.out.println("Goodbye, it was nice talking to you.");
                break;
            }
            else {
                Random rand = new Random();
                int response = rand.nextInt(3);
                bOutput = knowledge[response];
                System.out.println(bOutput);
            }

        }
        while (true);
    }
}
