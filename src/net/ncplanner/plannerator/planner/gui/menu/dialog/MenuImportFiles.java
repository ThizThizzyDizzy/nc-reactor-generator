package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuImportFiles extends MenuDialog{
    private final Task loadTask = new Task("Loading Files");
    private boolean running = true;
    private final List<Project> loadedFiles = new ArrayList<>();
    public MenuImportFiles(GUI gui, Menu parent, List<File> files, Runnable onImport){
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
                    Core.warning("Failed to load file "+file.getName()+"!", ex);
                }
                loadTask.getCurrentSubtask().finish();
                if(!running)return;
            }
            close();
            new MenuImport(gui, parent, loadedFiles, onImport).open();
        }, "File Import Thread").start();
    }
}