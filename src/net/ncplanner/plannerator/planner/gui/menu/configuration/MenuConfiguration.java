package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.FakeMenu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuConfiguration extends ConfigurationMenu{
    private final Button saveAddon;
    private final Label addonsLabel;
    private final SingleColumnList addonsList;
    private final Button importAddon;
    private final Button createAddon;
    private final ProgressBar scriptImportProgress;
    private Task importTask = null;
    private final Configuration configuration;
    public MenuConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, new FakeMenu(parent, () -> {
            Core.setConfigurationAndConvertMultiblocks(configuration);
        }), null, configuration.getName());
        this.configuration = configuration;
        saveAddon = new Button(sidebar.width, 0, 0, 48, "Save Addon", true, true).setTooltip("Save Addon");
        addonsLabel = add(new Label(sidebar.width, 0, 0, 48, "Addons", true));
        addonsList = add(new SingleColumnList(sidebar.width, addonsLabel.y+addonsLabel.height, 0, 0, 16));
        importAddon = add(new Button(sidebar.width, 0, 0, 48, "Import Addon", true, true));
        createAddon = add(new Button(sidebar.width, 0, 0, 48, "Create Addon", true, true));
        scriptImportProgress = add(new ProgressBar(sidebar.width, 0, 0, 160){
            @Override
            public Task getTask(){
                return importTask;
            }
        });
        add(addonsLabel);
        add(addonsList);
        add(importAddon);
        add(createAddon);
        createAddon.addAction(() -> {
            Addon addon = new Addon();
            configuration.addons.add(addon);
            gui.open(new MenuAddon(gui, this, addon));
        });
        importAddon.addAction(() -> {
            try{
                Core.createFileChooser((file) -> {
                    Thread t = new Thread(() -> {
                        loadAddon(file);
                        onOpened();
                    }, "Addon Import Thread");
                    t.setDaemon(true);
                    t.start();
                }, FileFormat.LEGACY_NCPF, "addon");
            }catch(IOException ex){
                Core.error("Failed to import addon!", ex);
            }
        });
    }
    @Override
    public void onOpened(){
        addonsList.components.clear();
        for(Addon addon : configuration.addons){
            addonsList.add(new MenuComponentAddon(addon, () -> {
                gui.open(new MenuAddon(gui, this, addon));
            }, () -> {
                configuration.addons.remove(addon);
                onOpened();
            }));
        }
        FOR:for(Addon addon : Configuration.internalAddons){
            for(Addon a : configuration.addons){
                if(a.getName().equals(addon.getName()))continue FOR;
            }
            addonsList.add(new MenuComponentInternalAddon(addon, () -> {
                configuration.addons.add(addon.copyTo(Addon::new));
                onOpened();
            }));
        }
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        scriptImportProgress.width = addonsLabel.width = addonsList.width = saveAddon.width = gui.getWidth()-saveAddon.x;
        scriptImportProgress.y = gui.getHeight()-scriptImportProgress.height;
        importAddon.width = createAddon.width = addonsLabel.width/2;
        createAddon.x = importAddon.x+importAddon.width;
        boolean badThing = false;
        //TODO validate config
        importAddon.y = createAddon.y = gui.getHeight()-importAddon.height-(badThing?64:0);
        addonsList.height = importAddon.y-addonsList.y;
        renderer.setColor(Core.theme.getConfigurationDividerColor());
        super.render2d(deltaTime);
    }
    @Override
    public void onFilesDropped(String[] files){
        importTask = new Task("Importing addons...");
        ArrayList<Task> fileTasks = new ArrayList<>();
        for(String fil : files)fileTasks.add(importTask.addSubtask(new Task(fil)));
        Thread t = new Thread(() -> {
            for(String fil : files){
                loadAddon(new File(fil));
                fileTasks.remove(0).finish();
            }
            importTask.finish();
        }, "Dropped File Loading Thread");
        t.setDaemon(true);
        importTask = null;
        onOpened();
    }
    private void loadAddon(File file){
        try{
            Project ncpf = FileReader.read(file);
            configuration.addons.add(ncpf.addons.get(0));
        }catch(Exception ex){
            Core.error("Failed to load addon", ex);
        }
    }
}