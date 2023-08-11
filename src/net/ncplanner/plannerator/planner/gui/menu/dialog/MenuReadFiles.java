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
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuReadFiles extends MenuDialog{
    private final Task loadTask = new Task("Loading Files");
    private boolean running = true;
    private final List<Project> loadedFiles = new ArrayList<>();
    public MenuReadFiles(GUI gui, Menu parent, List<File> files, Consumer<List<Project>> onRead){
        super(gui, parent);
        for(File f : files)loadTask.addSubtask(new Task(f.getName()));
        setContent(new ProgressBar(0, 0, 400, 0){
            @Override
            public Task getTask(){
                return loadTask;
            }
        });
        addButton("Cancel", () -> {
            running = false;
        }, true);
        new Thread(() -> {
            for(File file : files){
                try{
                    Project loaded = FileReader.read(file);
                    loadedFiles.add(loaded);
                }catch(Exception ex){
                    close();
                    running = false;
                    Core.warning("Failed to read file "+file.getName()+"!", ex);
                }
                loadTask.getCurrentSubtask().finish();
                if(!running)return;
            }
            close();
            onRead.accept(loadedFiles);
        }, "File Reading Thread").start();
    }
}