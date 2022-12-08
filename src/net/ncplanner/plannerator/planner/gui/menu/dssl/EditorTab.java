package net.ncplanner.plannerator.planner.gui.menu.dssl;
import java.io.File;
public class EditorTab{
    public String name;
    public File file;
    public DsslEditor editor;
    public EditorTab(String name){
        this(name, "");
    }
    public EditorTab(String name, String scriptText){
        this(name, null, scriptText);
    }
    public EditorTab(File file, String scriptText){
        this(file.getName(), file, scriptText);
    }
    public EditorTab(String name, File file, String scriptText){
        this.name = name;
        this.file = file;
        editor = new DsslEditor(scriptText);
    }
}