import java.util.Base64;
import java.io.File;
import java.nio.file.Files;
import java.io.*;

public class Base64Tools {
    public static String FileToB64(File f) {
        try {
            return Base64.getEncoder().encodeToString(Files.readAllBytes(f.toPath()));
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
        
    }
    public static void main(String[] args) {
        System.out.println(FileToB64(new File("../Payload/Decryptor.java")));
    }
}