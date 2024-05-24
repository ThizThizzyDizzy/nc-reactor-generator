package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuReadFiles extends MenuTaskDialog{
    private boolean running = true;
    private final List<Project> loadedFiles = new ArrayList<>();
    private final List<File> files;
    public MenuReadFiles(GUI gui, Menu parent, List<File> files, Consumer<List<Project>> onRead){
        super(gui, parent, new Task("Loading Files"));
        this.files = files;
        for(File f : files)task.addSubtask(new Task(f.getName()));
        onClose(()->onRead.accept(loadedFiles));
        addButton("Cancel", () -> {
            running = false;
        }, true);
    }
    @Override
    public void runTask(){
        for(File file : files){
            try{
                Project loaded = FileReader.read(file);
                loadedFiles.add(loaded);
            }catch(Exception ex){
                closeListeners.clear();
                close();
                running = false;
                Core.error("Failed to read file "+file.getName()+"!", ex);
            }
            task.getCurrentSubtask().finish();
            if(!running)return;
        }
    }
}