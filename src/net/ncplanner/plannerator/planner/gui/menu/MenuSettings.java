package net.ncplanner.plannerator.planner.gui.menu;
import java.io.File;
import java.io.IOException;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.gui.menu.configuration.MenuConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuSaveDialog;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuSettings extends SettingsMenu{
    private final Label internalLabel = add(new Label(0, 0, 0, 48, "Internal Configurations", true));
    private final SingleColumnList internalList = add(new SingleColumnList(0, 0, 0, 0, 32));
    private final Label externalLabel = add(new Label(0, 0, 0, 48, "External Configurations", true));
    private final SingleColumnList externalList = add(new SingleColumnList(0, 0, 0, 0, 32));
    private final Label currentConfigLabel = add(new Label(0, 0, 0, 48, "Current Configuration", true));
    private final Button load = add(new Button(0, 0, 0, 48, "Load", true).setTooltip("Load configuration from a file, replacing the current configuration\nAny existing multiblocks will be converted to the new configuration\nYou can load the following files:\nnuclearcraft.cfg in the game files\nany .ncpf configuration file"));
    private final Button save = add(new Button(0, 0, 0, 48, "Save", true).setTooltip("Save the configuration to a .ncpf file"));
    private final Button modify = add(new Button(0, 0, 0, 48, "Modify", true).setTooltip("Modify the current configuration"));
    private final ProgressBar externalConfigBar = add(new ProgressBar(0, 0, 0, 64) {
        @Override
        public Task getTask() {
            return externalConfigTask;
        }
    });
    private final Button theme;
    private final Button modules;
    private final ToggleBox invertUndoRedo;
    private final ToggleBox autoBuildCasing;
    private final ToggleBox vsync;
    private final ToggleBox rememberConfig;
    private File[] loadingExternalConfigurations;
    private Task externalConfigTask;
    public MenuSettings(GUI gui, Menu parent){
        super(gui, parent);
        addToSidebar(new Label(0, 0, 0, 48, "Settings", true));
        addToSidebar(invertUndoRedo = new ToggleBox(0, 0, 0, 48, "Invert Undo/Redo", false));
        addToSidebar(autoBuildCasing = new ToggleBox(0, 0, 0, 48, "Auto-build Casings", false));
        addToSidebar(vsync = new ToggleBox(0, 0, 0, 48, "V-Sync", true));
        addToSidebar(rememberConfig = new ToggleBox(0, 0, 0, 48, "Remember Selected Configuration", true).setTooltip("Remember the selected configuration, and load it on startup.\nThis will not remember changes to the configuration, you still have to save those!"));
        rememberConfig.onChange((remember) -> {
            Core.rememberConfig = remember;
        });
        addToSidebar(modules = new Button(0, 0, 0, 48, "Modules", true));
        modules.addAction(() -> {
            gui.open(new MenuModules(gui, this));
        });
        Button tutorials;
        addToSidebar(tutorials = new Button(0, 0, 0, 48, "Tutorials", true));
        tutorials.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuTutorial(gui, this), MenuTransition.SplitTransitionX.slideIn(300f/gui.getWidth()), 4));
        });
        theme = addToSidebar(new Button(0, 0, 0, 48, "Change Theme", true));
        for(Configuration config : Configuration.configurations){
            Button b = new Button(0, 0, 0, 48, "Load "+config.toString(), true).setTooltip("Replace the current configuration with "+config.toString()+"\nAll multiblocks will be converted to the new configuration");
            b.addAction(() -> {
                if(config.path!=null)Core.lastLoadedConfig = config.path;
                Core.setConfigurationAndConvertMultiblocks(config);
                onOpened();
            });
            internalList.add(b);
        }
        File configs = new File("configurations");
        if(configs.exists()&&configs.isDirectory()){
            File[] files = configs.listFiles();
            loadingExternalConfigurations = files;
            externalConfigTask = new Task("Loading External Configurations...");
            Thread t = new Thread(() -> {
                int i = 0;
                for(File f : loadingExternalConfigurations){
                    try{
                        externalConfigTask.progress = i/(float)loadingExternalConfigurations.length;
                        i++;
                        Configuration config = new Configuration(FileReader.read(f), "external/configurations/"+f.getName());
                        Button b = new Button(0, 0, 0, 48, "Load "+config.toString(), true).setTooltip("Replace the current configuration with "+config.toString()+"\nAll multiblocks will be converted to the new configuration");
                        b.addAction(() -> {
                            Core.lastLoadedConfig = config.path;
                            Core.setConfigurationAndConvertMultiblocks(config);
                            onOpened();
                        });
                        externalList.add(b);
                    }catch(Exception ex){
                        Core.warning("Failed to read external configuration "+f.getName()+"!", ex);
                    }
                }
                externalConfigTask.finish();
                loadingExternalConfigurations = null;
                externalConfigTask = null;
            }, "External Configuration Loader");
            t.setDaemon(true);
            t.start();
        }
        theme.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuThemes(gui, this), MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.getWidth()), 4));
        });
        load.addAction(() -> {
            try{
                Core.createFileChooser((file) -> {
                    Thread t = new Thread(() -> {
                        Configuration config = new Configuration(FileReader.read(file), null);
                        Core.setConfigurationAndConvertMultiblocks(config);
                        onOpened();
                    }, "File Loading Thread");
                    t.setDaemon(true);
                    t.start();
                }, FileFormat.ALL_CONFIGURATION_FORMATS, "configuration");
            }catch(IOException ex){
                Core.error("Failed to load configuration!", ex);
            }
        });
        save.addAction(() -> {
            Project ncpf = new Project();
            ncpf.configuration = Core.project.configuration;
            ncpf.addons.addAll(Core.project.addons);
            new MenuSaveDialog(gui, this, ncpf, this::onOpened).open();
        });
        modify.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuConfiguration(gui, this, new Configuration(Core.project)), MenuTransition.SplitTransitionX.slideIn(sidebar.width/gui.getWidth()), 4));
        });
    }
    @Override
    public void onOpened(){
        invertUndoRedo.isToggledOn = Core.invertUndoRedo;
        autoBuildCasing.isToggledOn = Core.autoBuildCasing;
        vsync.isToggledOn = Core.vsync;
        rememberConfig.isToggledOn = Core.rememberConfig;
        currentConfigLabel.text = "Current Configuration: "+Core.project.getConfigName();
        int active = 0;
        for(Module m : Core.modules)if(m.isActive())active++;
        modules.text = "Modules ("+active+"/"+Core.modules.size()+" Active)";
    }
    @Override
    public void onClosed(){
        Core.invertUndoRedo = invertUndoRedo.isToggledOn;
        Core.autoBuildCasing = autoBuildCasing.isToggledOn;
        Core.rememberConfig = rememberConfig.isToggledOn;
        Core.setVsync(vsync.isToggledOn);
        super.onClosed();
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        externalConfigBar.width = externalLabel.width = externalList.width = internalLabel.width = internalList.width = currentConfigLabel.width = gui.getWidth()-sidebar.width;
        modify.width = load.width = save.width = currentConfigLabel.width/3;
        externalConfigBar.x = externalLabel.x = externalList.x = internalLabel.x = internalList.x = currentConfigLabel.x = modify.x = load.x = sidebar.width;
        save.x = load.x+load.width;
        modify.x = save.x+save.width;
        save.x+=3;
        modify.x+=6;
        modify.width-=6;
        internalList.y = internalLabel.height;
        externalList.height = internalList.height = gui.getHeight()/3-internalLabel.height;
        externalLabel.y = externalList.components.size()>0?internalList.y+internalList.height:-externalLabel.height;
        externalList.y = externalList.components.size()>0?externalLabel.y+externalLabel.height:-externalList.height;
        currentConfigLabel.y = Math.max(internalList.y+internalList.height, externalList.y+externalList.height);
        save.y = load.y = modify.y = currentConfigLabel.y+currentConfigLabel.height;
        externalConfigBar.y = save.y+save.height;
        super.render2d(deltaTime);
    }
}