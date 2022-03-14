import java.util.*;

public class MainServer {
public static void main(String[] args) {
        System.out.println("Welcome to JavaCry Main Control Server\n");
        KeyServer ks = new KeyServer(6666);
        DecryptRequestHandler ds = new DecryptRequestHandler(5555);
        Scanner input = new Scanner(System.in);
        System.out.println("\nType help to show all the commands");

        while (true) {
                System.out.print(">> ");
                String cmd = input.nextLine();
                if (cmd.equals("")) {
                  continue;
                } else {
                  if (cmd.split(":")[0].equals("DR")) {
                    ds.command(cmd.split(":")[1]);
                  }
                  else if (cmd.split(":")[0].equals("KS")) {
                    ;
                  } else {
                    switch (cmd) {
                      case "clear":
                        System.out.print("\033[H\033[2J");
                        System.out.flush();
                        break;
                      case "help":
                        System.out.println("Main Server Commands:\n");
                        System.out.println("\thelp: show this page");
                        System.out.println("\tclear: clear the screen");
                        System.out.println("\tctrl-c: exit");
                        System.out.println("KeyServer Commands:");
                        System.out.println("DecryptionRequestHandler Commands:");
                        System.out.println("\tDR:<command>: run commands for the DecryptionRequestHandler module");
                        break;

                    }
                  }

                }
        }
}
}
