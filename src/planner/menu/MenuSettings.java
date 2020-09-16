package planner.menu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.FileFormat;
import planner.Theme;
import planner.file.FileReader;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.configuration.MenuConfiguration;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.GUI;
public class MenuSettings extends Menu{
    private final MenuComponentLabel currentConfig = add(new MenuComponentLabel(0, 0, 0, 0, "Current Configuration", true));
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration", true, true).setTooltip("Load configuration from a file, replacing the current configuration\nAny existing multiblocks will be converted to the new configuration\nYou can load the following files:\nnuclearcraft.cfg in the game files\nany .ncpf configuration file"));
    private final MenuComponentMinimalistButton save = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save Configuration", true, true).setTooltip("Save the configuration to a .ncpf file"));
    private final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Modify Configuration", true, true).setTooltip("Modify the current configuration"));
    private final MenuComponentMinimalistOptionButton theme = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Theme", true, true, Theme.themes.indexOf(Core.theme), Theme.getThemeS())).setTooltip("Click to cycle through available themes\nRight click to cycle back");
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Close the settings menu"));
    private final MenuComponentMinimalistButton tutorials = new MenuComponentMinimalistButton(0, 0, 0, 0, "Tutorials", true, true);
    private final ArrayList<MenuComponentMinimalistButton> buttons = new ArrayList<>();
    public MenuSettings(GUI gui, Menu parent){
        super(gui, parent);
        for(Configuration config : Configuration.configurations){
            MenuComponentMinimalistButton b = new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration: "+config.toString(), true, true).setTooltip("Replace the current configuration with "+config.toString()+"\nAll multiblocks will be converted to the new configuration");
            b.addActionListener((e) -> {
                config.impose(Core.configuration);
                for(Multiblock multi : Core.multiblocks){
                    multi.convertTo(Core.configuration);
                }
                onGUIOpened();
            });
            buttons.add(add(b));
        }
        load.addActionListener((e) -> {
            Core.createFileChooser((file, format) -> {
                NCPFFile ncpf = FileReader.read(file);
                if(ncpf==null)return;
                Configuration.impose(ncpf.configuration, Core.configuration);
                for(Multiblock multi : Core.multiblocks){
                    multi.convertTo(Core.configuration);
                }
                onGUIOpened();
            }, FileFormat.ALL_CONFIGURATION_FORMATS);
        });
        save.addActionListener((e) -> {
            Core.createFileChooser(new File(Core.configuration.getFullName()), (file, format) -> {
                if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                file = Core.askForOverwrite(file);
                if(file==null)return;
                try(FileOutputStream stream = new FileOutputStream(file)){
                    Config header = Config.newConfig();
                    header.set("version", NCPFFile.SAVE_VERSION);
                    header.set("count", 0);
                    header.save(stream);
                    Core.configuration.save(null, Config.newConfig()).save(stream);
                }catch(IOException ex){
                    Sys.error(ErrorLevel.severe, "Failed to save configuration!", ex, ErrorCategory.fileIO);
                }
            }, FileFormat.NCPF);
        });
        tutorials.addActionListener((e) -> {
            gui.open(new MenuTutorial(gui, this));
        });
        edit.addActionListener((e) -> {
            gui.open(new MenuConfiguration(gui, this, Core.configuration));
        });
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(0, -1), 4));
        });
    }
    @Override
    public void onGUIOpened(){
        currentConfig.text = "Current Configuration: "+Core.configuration.toString();
    }
    @Override
    public void render(int millisSinceLastTick){
        for(int i = 0; i<buttons.size(); i++){
            MenuComponentMinimalistButton b = buttons.get(i);
            b.width = Core.helper.displayWidth();
            b.height = Core.helper.displayHeight()/16;
            b.y = b.height*i;
        }
        tutorials.width = currentConfig.width = theme.width = load.width = save.width = done.width = edit.width = Core.helper.displayWidth();
        tutorials.height = currentConfig.height = theme.height = load.height = save.height = done.height = edit.height = Core.helper.displayHeight()/16;
        currentConfig.y = load.height*(Configuration.configurations.size());
        load.y = currentConfig.y+currentConfig.height;
        save.y = load.y+load.height;
        edit.y = save.y+save.height;
        done.y = Core.helper.displayHeight()-done.height;
        theme.y = done.y-theme.height;
        tutorials.y = theme.y-theme.height;
        if(Theme.themes.indexOf(Core.theme)!=theme.getIndex()){
            try{
                Core.setTheme(Theme.themes.get(theme.getIndex()));
            }catch(IndexOutOfBoundsException ex){
                gui.open(new MenuSettings(gui, (Menu)parent));
            }
        }
        super.render(millisSinceLastTick);
    }
    
}