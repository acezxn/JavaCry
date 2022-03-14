import java.net.*;
import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;




public class KeyClient
{
    private Socket socket            = null;
    private DataInputStream input    = null;
    private DataOutputStream out     = null;

    public KeyClient(String address, int port)
    {
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
            String line = "";
            while (!line.equals("Over"))
            {
                try
                {
                    line = input.readLine();
                    out.writeUTF(line);
                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public void sendKey(String key) {
      try
      {
        out.writeUTF(key);
      }
      catch(IOException i)
      {
          System.out.println(i);
      }
    }

    public void close() {
      try
      {
          input.close();
          out.close();
          socket.close();
      }
      catch(IOException i)
      {
          System.out.println(i);
      }
    }

    public static void main(String args[])
    {
        KeyClient client = new KeyClient("127.0.0.1", 6666);
    }
}
