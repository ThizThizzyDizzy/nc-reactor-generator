package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.IOException;
import java.util.function.Consumer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
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
                    Thread t = new Thread(() -> {
                        Project ncpf = FileReader.read(file);
                        onLoad.accept(ncpf);
                        close();
                    });
                    t.setDaemon(true);
                    t.start();
                }, FileFormat.ALL_PLANNER_FORMATS);
            }catch(IOException ex){
                Core.error("Failed to load file!", ex);
            }
        });
        this.onLoad = onLoad;
    }
}