package net.ncplanner.plannerator.planner.gui.menu;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.config2.C2ConfigComponent;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.ListLayout;
import org.lwjgl.glfw.GLFW;
public class MenuConfig2Editor extends Menu{
    private final SingleColumnList list;
    private ArrayList<ArrayList<Config>> configFiles = new ArrayList<>();
    public MenuConfig2Editor(GUI gui, Menu parent){
        super(gui, parent);
        list = add(new SingleColumnList(32));
    }
    @Override
    public void render2d(double deltaTime){
        list.width = width;
        list.height = height;
        super.render2d(deltaTime);
    }
    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods){
        super.onKeyEvent(key, scancode, action, mods);
        if(key==GLFW.GLFW_KEY_S&&mods==(GLFW.GLFW_MOD_CONTROL)){
            for(int i = 0; i<configFiles.size(); i++){
                ArrayList<Config> configFile = configFiles.get(i);
                try(FileOutputStream out = new FileOutputStream(new File("recovery-"+i+".config2"))){
                    for(Config c : configFile)c.save(out);
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    @Override
    public void onFilesDropped(String[] files){
        if(files.length==0)return;
        list.components.clear();
        configFiles.clear();
        ListLayout layout = list.add(new ListLayout().fitContent());
        for(String file : files){
            ArrayList<Config> configFile = new ArrayList<>();
            configFiles.add(configFile);
            layout.add(new Label(file));
            try(FileInputStream in = new FileInputStream(new File(file))){
                while(true){
                    layout.add(new Label("Config"));
                    Config config = Config.newConfig(in).load();
                    configFile.add(config);
                    layout.add(new C2ConfigComponent(config));
                }
            }catch(Exception ex){}
        }
    }
}
