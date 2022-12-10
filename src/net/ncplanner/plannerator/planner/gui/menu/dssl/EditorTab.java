package net.ncplanner.plannerator.planner.gui.menu.dssl;
import java.io.File;
public class EditorTab{
    public String name;
    public File file;
    public DsslEditor editor;
    public boolean unsavedChanges = false;
    public EditorTab(String name){
        this(name, "");
        unsavedChanges = true;
    }
    public EditorTab(String name, String scriptText){
        this(name, null, scriptText);
        unsavedChanges = true;
    }
    public EditorTab(File file, String scriptText){
        this(file.getName(), file, scriptText);
    }
    public EditorTab(String name, File file, String scriptText){
        this.name = name;
        this.file = file;
        editor = new DsslEditor(scriptText);
        editor.onChange = () -> {
            unsavedChanges = true;
        };
    }
    public String getName(){
        return name+(unsavedChanges?"*":"");
    }
}