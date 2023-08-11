package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuLoadFile extends MenuDialog{
    public Consumer<Project> onLoad;
    public MenuLoadFile(GUI gui, Menu parent, Consumer<Project> onLoad){
        super(gui, parent);
        addButton("Cancel", () -> {
            close();
        });
        addButton("System File Chooser", () -> {
            try{
                Core.createFileChooser((file) -> {
                    close();
                    readFile(file);
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Core.error("Failed to load file!", ex);
            }
        });
        this.onLoad = onLoad;
    }
    protected void readFile(File file){
        new MenuReadFiles(gui, parent, Arrays.asList(file), (loadedFiles) -> {
            if(loadedFiles.size()!=1)throw new RuntimeException("Tried to load one file, found "+loadedFiles.size()+"!");
            onLoad.accept(loadedFiles.get(0));
        }).open();
    }
}