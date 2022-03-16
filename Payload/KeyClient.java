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
    private DataInputStream in     = null;
    private boolean success = false;

    public KeyClient(String address, int port)
    {
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // sends output to the socket
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        if (socket != null) {
          success = true;
        }
    }

    public boolean getSuccess() {
      return success;
    }

    public void sendString(String s) {
      try
      {
        out.writeUTF(s);
      }
      catch(IOException i)
      {
          System.out.println(i);
      }
    }

    public String recvString() {
      try
      {
        return in.readUTF();
      }
      catch(IOException i)
      {
          System.out.println(i);
      }
      return "";
    }

    public void close() {
      try
      {
          System.out.println("Closing connection");
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
