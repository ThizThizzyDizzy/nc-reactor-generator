package planner.file;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
public enum FileFormat{
    PNG("PNG Image (.png)", "png"),
    HELLRAGE_REACTOR("Hellrage Reactor File (.json)", "json"),
    NCPF("NuclearCraft Planner File", "ncpf"),
    ALL_PLANNER_FORMATS("NuclearCraft Planner File", "ncpf", "json"),
    ALL_CONFIGURATION_FORMATS("NuclearCraft Configuration File", "ncpf", "cfg");//TODO hellrage .json
    public final String description;
    public final String[] extensions;
    private FileFormat(String description, String... extensions){
        this.description = description;
        this.extensions = extensions;
    }
    public FileFilter getFileFilter(){
        return new FileNameExtensionFilter(description, extensions);
    }
}