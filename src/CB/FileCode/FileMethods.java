package CB.FileCode;

import CB.Master.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileMethods {
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
        } catch(IOException ex){ ex.printStackTrace(); }
    }

    public static void saveLog(FileWriter log, String ID, String str) throws IOException {
        log.write(ID + str + "\n");
    }

    public static void fileErrorMessage() {
        ChatBot.bOutput = "I seem to be having some trouble accessing my knowledge....";
    }
}
