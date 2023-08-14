package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.FakeMenu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.BorderLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.GridLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuTask;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuConfiguration extends ConfigurationMenu{
    private final SingleColumnList addonsList;
    private final Configuration configuration;
    private final GridLayout addonButtons;
    private final SingleColumnList configList;
    public MenuConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, new FakeMenu(parent, () -> {
            Core.setConfigurationAndConvertMultiblocks(configuration);
        }), null, configuration.getName(), new SplitLayout(0.6f));
        this.configuration = configuration;
        configList = add(new SingleColumnList(16));
        BorderLayout addonsPanel = add(new BorderLayout());
        addonsPanel.add(new Label("Addons", true), BorderLayout.TOP, 48);
        addonsList = addonsPanel.add(new SingleColumnList(16), BorderLayout.CENTER);
        addonButtons = addonsPanel.add(new GridLayout(0, 1), BorderLayout.BOTTOM, 48);
        Button importAddon = addonButtons.add(new Button("Import Addon", true, true));
        Button createAddon = addonButtons.add(new Button("Create Addon", true, true));
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
    public void render2d(double deltaTime){
        float h = 0;
        for(Component c : configList.components)h+=c.height;
        float pos = h/height;
        ((SplitLayout)content).splitPos = Math.min(pos, 0.6f);//TODO make this part of SplitLayout
        super.render2d(deltaTime);
    }
    @Override
    public void onOpened(){
        configList.components.clear();
        for(String key : NCPFConfigurationContainer.configOrder){
            Supplier<NCPFConfiguration> cfg = NCPFConfigurationContainer.recognizedConfigurations.get(key);
            configuration.configuration.withConfiguration(cfg, (config) -> {
                configList.add(new Label(0, 0, 0, 48, config.getName(), true));
                GridLayout buttons = configList.add(new GridLayout(0, 1));
                buttons.height = 48;
                buttons.add(new Button("Edit", true).addAction(() -> {
                    gui.open(new MenuSpecificConfiguration(gui, this, config));
                }));
                buttons.add(new Button("Delete (Shift)", false){
                    @Override
                    public void render2d(double deltaTime){
                        enabled = Core.isShiftPressed();
                        super.render2d(deltaTime);
                    }
                }.addAction(() -> {
                    configuration.configuration.configurations.remove(key);
                    onOpened();
                }));
            });
            if(!configuration.configuration.hasConfiguration(cfg)){
                configList.add(new Label(0, 0, 0, 48, cfg.get().getName(), true));
                GridLayout buttons = configList.add(new GridLayout(0, 1));
                buttons.height = 48;
                buttons.add(new Button("Create (Shift)", false){
                    @Override
                    public void render2d(double deltaTime){
                        enabled = Core.isShiftPressed();
                        super.render2d(deltaTime);
                    }
                }.addAction(() -> {
                    configuration.configuration.setConfiguration(cfg.get());
                    onOpened();
                }));
            }
        }
        for(String key : configuration.configuration.configurations.keySet()){
            if(!NCPFConfigurationContainer.configOrder.contains(key)){
                configList.add(new Label(0, 0, 0, 48, configuration.configuration.configurations.get(key).getName()+" ("+key+")", true));
            }
        }
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
    public void onFilesDropped(String[] files){
        Task task = new Task("Importing addons...");
        ArrayList<Task> fileTasks = new ArrayList<>();
        for(String fil : files)fileTasks.add(task.addSubtask(new Task(fil)));
        new MenuTask(gui, this, task).open();
        Thread t = new Thread(() -> {
            for(String fil : files){
                loadAddon(new File(fil));
                fileTasks.remove(0).finish();
            }
            task.finish();
            onOpened();
        }, "Dropped File Loading Thread");
        t.setDaemon(true);
        t.start();
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