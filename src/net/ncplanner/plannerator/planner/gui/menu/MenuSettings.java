package net.ncplanner.plannerator.planner.gui.menu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MenuConfiguration;
import net.ncplanner.plannerator.planner.module.Module;
import static org.lwjgl.glfw.GLFW.*;
public class MenuSettings extends SettingsMenu{
    private final Label quickLoadLabel = add(new Label(0, 0, 0, 48, "Internal Configurations", true));
    private final SingleColumnList quickLoadList = add(new SingleColumnList(0, 0, 0, 0, 32));
    private final Label currentConfigLabel = add(new Label(0, 0, 0, 48, "Current Configuration", true));
    private final Button load = add(new Button(0, 0, 0, 48, "Load", true, true).setTooltip("Load configuration from a file, replacing the current configuration\nAny existing multiblocks will be converted to the new configuration\nYou can load the following files:\nnuclearcraft.cfg in the game files\nany .ncpf configuration file"));
    private final Button save = add(new Button(0, 0, 0, 48, "Save", true, true).setTooltip("Save the configuration to a .ncpf file"));
    private final Button modify = add(new Button(0, 0, 0, 48, "Modify", true, true).setTooltip("Modify the current configuration"));
    private final Button theme;
    private final Button modules;
    private final ToggleBox invertUndoRedo;
    private final ToggleBox autoBuildCasing;
    public MenuSettings(GUI gui, Menu parent){
        super(gui, parent);
        addToSidebar(new Label(0, 0, 0, 48, "Settings", true));
        addToSidebar(invertUndoRedo = new ToggleBox(0, 0, 0, 48, "Invert Undo/Redo", false));
        addToSidebar(autoBuildCasing = new ToggleBox(0, 0, 0, 48, "Auto-build Casings", false));
        addToSidebar(modules = new Button(0, 0, 0, 48, "Modules", true, true));
        modules.addAction(() -> {
            gui.open(new MenuModules(gui, this));
        });
        Button tutorials;
        addToSidebar(tutorials = new Button(0, 0, 0, 48, "Tutorials", true, true));
        tutorials.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuTutorial(gui, this), MenuTransition.SplitTransitionX.slideIn(300f/gui.getWidth()), 4));
        });
        theme = addToSidebar(new Button(0, 0, 0, 48, "Change Theme", true, true));
        for(Configuration config : Configuration.configurations){
            Button b = new Button(0, 0, 0, 48, "Load "+config.toString(), true, true).setTooltip("Replace the current configuration with "+config.toString()+"\nAll multiblocks will be converted to the new configuration");
            b.addAction(() -> {
                config.impose(Core.configuration);
                for(Multiblock multi : Core.multiblocks){
                    try{
                        multi.convertTo(Core.configuration);
                    }catch(MissingConfigurationEntryException ex){
                        throw new RuntimeException(ex);
                    }
                }
                onOpened();
            });
            quickLoadList.add(b);
        }
        theme.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuThemes(gui, this), MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.getWidth()), 4));
        });
        load.addAction(() -> {
            try{
                Core.createFileChooser((file) -> {
                    NCPFFile ncpf = FileReader.read(file);
                    if(ncpf==null)return;
                    Configuration.impose(ncpf.configuration, Core.configuration);
                    for(Multiblock multi : Core.multiblocks){
                        try{
                            multi.convertTo(Core.configuration);
                        }catch(MissingConfigurationEntryException ex){
                            throw new RuntimeException(ex);
                        }
                    }
                    onOpened();
                }, FileFormat.ALL_CONFIGURATION_FORMATS);
            }catch(IOException ex){
                Core.error("Failed to load configuration!", ex);
            }
        });
        save.addAction(() -> {
            try{
                Core.createFileChooser(new File(Core.configuration.getFullName()), (file) -> {
                    if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                    if(file==null)return;
                    try(FileOutputStream stream = new FileOutputStream(file)){
                        Config header = Config.newConfig();
                        header.set("version", NCPFFile.SAVE_VERSION);
                        header.set("count", 0);
                        header.save(stream);
                        Core.configuration.save(null, Config.newConfig()).save(stream);
                    }catch(IOException ex){
                        Core.error("Failed to save configuration!", ex);
                    }
                }, FileFormat.NCPF);
            }catch(IOException ex){
                Core.error("Failed to save configuration!", ex);
            }
        });
        modify.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuConfiguration(gui, this, Core.configuration), MenuTransition.SplitTransitionX.slideIn(sidebar.width/gui.getWidth()), 4));
        });
    }
    @Override
    public void onOpened(){
        invertUndoRedo.isToggledOn = Core.invertUndoRedo;
        autoBuildCasing.isToggledOn = Core.autoBuildCasing;
        currentConfigLabel.text = "Current Configuration: "+Core.configuration.toString();
        int active = 0;
        for(Module m : Core.modules)if(m.isActive())active++;
        modules.text = "Modules ("+active+"/"+Core.modules.size()+" Active)";
    }
    @Override
    public void onClosed(){
        Core.invertUndoRedo = invertUndoRedo.isToggledOn;
        Core.autoBuildCasing = autoBuildCasing.isToggledOn;
        super.onClosed();
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        quickLoadLabel.width = quickLoadList.width = currentConfigLabel.width = gui.getWidth()-sidebar.width;
        modify.width = load.width = save.width = currentConfigLabel.width/3;
        quickLoadLabel.x = quickLoadList.x = currentConfigLabel.x = modify.x = load.x = sidebar.width;
        save.x = load.x+load.width;
        modify.x = save.x+save.width;
        save.x+=3;
        modify.x+=6;
        modify.width-=6;
        quickLoadList.y = quickLoadLabel.height;
        quickLoadList.height = gui.getHeight()/3-quickLoadLabel.height;
        currentConfigLabel.y = quickLoadList.y+quickLoadList.height;
        save.y = load.y = modify.y = currentConfigLabel.y+currentConfigLabel.height;
        if(Core.isShiftPressed()&&Core.isControlPressed()&&Core.configuration.name.equals("NuclearCraft Info")){
            renderer.setColor(Core.theme.getSettingsMergeTextColor());
            renderer.drawCenteredText(sidebar.width, gui.getHeight()-50, gui.getWidth(), gui.getHeight(), "Ctrl+Shift+MMB to convert to addon");
            renderer.setWhite();
        }
        super.render2d(deltaTime);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(button==GLFW_MOUSE_BUTTON_MIDDLE&&action==GLFW_PRESS&&Core.isShiftPressed()&&Core.isControlPressed()&&Core.configuration.name.equals("NuclearCraft Info")){
            Configuration config = Configuration.configurations.get(0).copy();
            config.addons.add(Core.configuration.makeAddon(config));
            Core.configuration = config;
            onOpened();
        }
    }
}