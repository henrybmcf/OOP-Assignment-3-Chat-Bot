package CB.FileCode;

import CB.Master.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileMethods {
    // Writing to conversation log files
    public static void saveLog(FileWriter log, String ID, String str) {
        try { log.write(ID + str + "\n"); } catch (IOException ex) { fileErrorMessage(); }
    }

    // Method for zipping conversation log files to save space
    // Uses java util zip
    public static void zipLog(String fileName) {
        byte[] buffer = new byte[1024];

        try{
            FileOutputStream fos = new FileOutputStream(fileName + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(fileName + ".txt");
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(fileName + ".txt");

            int len;
            while ((len = in.read(buffer)) > 0)
                zos.write(buffer, 0, len);

            in.close();
            zos.closeEntry();
            zos.close();
        } catch(IOException ex) { fileErrorMessage(); }
    }

    // Error message for when bot can't access knowledge files
    public static void fileErrorMessage() {
        ChatBot.bOutput = "I seem to be having some trouble accessing my knowledge....";
    }
}