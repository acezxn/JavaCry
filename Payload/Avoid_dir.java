import java.util.ArrayList;
import java.util.List;

public class Avoid_dir {
    public List<String> avoidDir = new ArrayList<>();

    public AvoidedDir() {
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
    }
}
