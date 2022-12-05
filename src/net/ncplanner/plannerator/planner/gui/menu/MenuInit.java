package net.ncplanner.plannerator.planner.gui.menu;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.Updater;
import net.ncplanner.plannerator.planner.VersionManager;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.reader.NCPF10Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF11Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF1Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF2Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF3Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF4Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF5Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF6Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF7Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF8Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPF9Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageMSR1Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageMSR2Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageMSR3Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageMSR4Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageMSR5Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageMSR6Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageSFR1Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageSFR2Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageSFR3Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageSFR4Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageSFR5Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulHellrageSFR6Reader;
import net.ncplanner.plannerator.planner.file.reader.OverhaulNCConfigReader;
import net.ncplanner.plannerator.planner.file.reader.UnderhaulHellrage1Reader;
import net.ncplanner.plannerator.planner.file.reader.UnderhaulHellrage2Reader;
import net.ncplanner.plannerator.planner.file.reader.UnderhaulNCConfigReader;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuUpdate;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.module.PrimeFuelModule;
import net.ncplanner.plannerator.planner.module.RainbowFactorModule;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.theme.Theme;
public class MenuInit extends Menu{
    private final Task init;
    HashMap<String, Supplier<FormatReader>> readers = new HashMap<>();
    ArrayList<String> readerNames = new ArrayList<>();
    HashMap<String, Task> readerTasks = new HashMap<>();
    private final ProgressBar progressBar;
    {
        addReader("NCPF11Reader", ()->{return new NCPF11Reader();});// .ncpf version 11
        addReader("NCPF10Reader", ()->{return new NCPF10Reader();});// .ncpf version 10
        addReader("NCPF9Reader", ()->{return new NCPF9Reader();});// .ncpf version 9
        addReader("NCPF8Reader", ()->{return new NCPF8Reader();});// .ncpf version 8
        addReader("NCPF7Reader", ()->{return new NCPF7Reader();});// .ncpf version 7
        addReader("NCPF6Reader", ()->{return new NCPF6Reader();});// .ncpf version 6
        addReader("NCPF5Reader", ()->{return new NCPF5Reader();});// .ncpf version 5
        addReader("NCPF4Reader", ()->{return new NCPF4Reader();});// .ncpf version 4
        addReader("NCPF3Reader", ()->{return new NCPF3Reader();});// .ncpf version 3
        addReader("NCPF2Reader", ()->{return new NCPF2Reader();});// .ncpf version 2
        addReader("NCPF1Reader", ()->{return new NCPF1Reader();});// .ncpf version 1
        addReader("OverhaulHellrageSFR6Reader", ()->{return new OverhaulHellrageSFR6Reader();});// hellrage SFR .json 2.1.1-2.1.7 (present)
        addReader("OverhaulHellrageSFR5Reader", ()->{return new OverhaulHellrageSFR5Reader();});// hellrage SFR .json 2.0.32-2.0.37
        addReader("OverhaulHellrageSFR4Reader", ()->{return new OverhaulHellrageSFR4Reader();});// hellrage SFR .json 2.0.31
        addReader("OverhaulHellrageSFR3Reader", ()->{return new OverhaulHellrageSFR3Reader();});// hellrage SFR .json 2.0.30
        addReader("OverhaulHellrageSFR2Reader", ()->{return new OverhaulHellrageSFR2Reader();});// hellrage SFR .json 2.0.7-2.0.29
        addReader("OverhaulHellrageSFR1Reader", ()->{return new OverhaulHellrageSFR1Reader();});// hellrage SFR .json 2.0.1-2.0.6
        addReader("UnderhaulHellrage2Reader", ()->{return new UnderhaulHellrage2Reader();});// hellrage .json 1.2.23-1.2.25 (present)
        addReader("UnderhaulHellrage1Reader", ()->{return new UnderhaulHellrage1Reader();});// hellrage .json 1.2.5-1.2.22
        addReader("OverhaulHellrageMSR6Reader", ()->{return new OverhaulHellrageMSR6Reader();});// hellrage MSR .json 2.1.1-2.1.7 (present)
        addReader("OverhaulHellrageMSR5Reader", ()->{return new OverhaulHellrageMSR5Reader();});// hellrage MSR .json 2.0.32-2.0.37
        addReader("OverhaulHellrageMSR4Reader", ()->{return new OverhaulHellrageMSR4Reader();});// hellrage MSR .json 2.0.31
        addReader("OverhaulHellrageMSR3Reader", ()->{return new OverhaulHellrageMSR3Reader();});// hellrage MSR .json 2.0.30
        addReader("OverhaulHellrageMSR2Reader", ()->{return new OverhaulHellrageMSR2Reader();});// hellrage MSR .json 2.0.7-2.0.29
        addReader("OverhaulHellrageMSR1Reader", ()->{return new OverhaulHellrageMSR1Reader();});// hellrage MSR .json 2.0.1-2.0.6
        addReader("OverhaulNCConfigReader", ()->{return new OverhaulNCConfigReader();});// OVERHAUL nuclearcraft.cfg
        addReader("UnderhaulNCConfigReader", ()->{return new UnderhaulNCConfigReader();});// UNDERHAUL nuclearcraft.cfg
    }
    private  void addReader(String s, Supplier<FormatReader> reader){
        readerNames.add(s);
        readers.put(s, reader);
    }
    public MenuInit(GUI gui){
        super(gui, null);
        progressBar = add(new ProgressBar(0, 0, gui.getWidth(), gui.getHeight(), 3){
            @Override
            public Task getTask(){
                return init;
            }
        });
        init = new Task("Initializing...");
        Task t2 = init.addSubtask("Resetting Metadata");
        Task tf = init.addSubtask("Adding File Readers...");
        for(String s : readerNames){
            readerTasks.put(s, tf.addSubtask("Adding "+s+"..."));
        }
        Task tc = init.addSubtask("Initializing Configurations...");
        Task tc1 = tc.addSubtask("Initializing Nuclearcraft Configuration");
        Task tps = init.addSubtask("Preloading settings...");
        Task tm = init.addSubtask("Adding modules...");
        Task tmc = tm.addSubtask("Adding Core Module");
        Task tm1 = tm.addSubtask("Adding Underhaul Module");
        Task tm2 = tm.addSubtask("Adding Overhaul Module");
        Task tm3 = tm.addSubtask("Adding Fusion Test Module");
        Task tm4 = tm.addSubtask("Adding Rainbow Factor Module");
        Task tm5 = tm.addSubtask("Adding Prime Fuel Module");
        Task ts = init.addSubtask("Loading settings...");
        Task tmr = init.addSubtask("Refreshing modules...");
        Task tct = init.addSubtask("Adjusting MSR block textures...");
        Task tci = init.addSubtask("Imposing Configuration...");
        new Thread(() -> {
            try{
                System.out.println("Started Initialization Thread");
                Core.resetMetadata();
                System.out.println("Reset Metadata");
                t2.finish();
                for(String s : readerNames){
                    FileReader.formats.add(readers.get(s).get());
                    readerTasks.get(s).finish();
                }
                System.out.println("Loaded File Formats");
                attemptInit(Configuration::initNuclearcraftConfiguration, "Loaded NC Config", "Failed to load NuclearCraft configuration!", false);
                tc1.finish();
                
                File f = new File("settings.dat").getAbsoluteFile();
                if(f.exists()){
                    Config settings = Config.newConfig(f);
                    settings.load();
                    Config overlays = settings.get("overlays", Config.newConfig());
                    for(String key : overlays.properties()){
                        Core.overlays.put(key, overlays.getInt(key));
                    }
                }
                System.out.println("Preloaded Settings");
                tps.finish();
                Core.modules.add(new CoreModule());
                tmc.finish();
                Core.modules.add(new UnderhaulModule());
                tm1.finish();
                Core.modules.add(new OverhaulModule());
                tm2.finish();
                Core.modules.add(new FusionTestModule());
                tm3.finish();
                Core.modules.add(new RainbowFactorModule());
                tm4.finish();
                Core.modules.add(new PrimeFuelModule());
                tm5.finish();
                System.out.println("Added Modules");
                if(f.exists()){
                    Config settings = Config.newConfig(f);
                    settings.load();
                    System.out.println("Loading theme");
                    Object o = settings.get("theme");
                    if(o instanceof String){
                        Core.setTheme(Theme.getByName((String)o));
                    }else Core.setTheme(Theme.getByLegacyID((int)o));
                    try{
                        Config modules = settings.get("modules", Config.newConfig());
                        HashMap<Module, Boolean> moduleStates = new HashMap<>();
                        for(String key : modules.properties()){
                            for(Module m : Core.modules){
                                if(m.name.equals(key))moduleStates.put(m, modules.getBoolean(key));
                            }
                        }
                        for(Module m : Core.modules){
                            if(!moduleStates.containsKey(m))continue;
                            if(m.isActive()){
                                if(!moduleStates.get(m))m.deactivate();
                            }else{
                                if(moduleStates.get(m))m.activate();
                            }
                        }
                    }catch(Exception ex){}
                    Core.tutorialShown = settings.get("tutorialShown", false);
                    Core.invertUndoRedo = settings.get("invertUndoRedo", false);
                    Core.autoBuildCasing = settings.get("autoBuildCasing", true);
                    Core.imageExport3DView = settings.get("imageExport3DView", true);
                    Core.imageExportCasing = settings.get("imageExportCasing", true);
                    Core.imageExportCasing3D = settings.get("imageExportCasing3D", true);
                    Core.dssl = settings.get("dssl", false);
                    Config cursor = settings.get("cursor", Config.newConfig());
                    MenuCalibrateCursor.xMult = cursor.get("xMult", 1d);
                    MenuCalibrateCursor.yMult = cursor.get("yMult", 1d);
                    MenuCalibrateCursor.xGUIScale = cursor.get("xGUIScale", 1d);
                    MenuCalibrateCursor.yGUIScale = cursor.get("yGUIScale", 1d);
                    MenuCalibrateCursor.xOff = cursor.get("xOff", 1);
                    MenuCalibrateCursor.yOff = cursor.get("yOff", 1);
                    Core.setVsync(settings.get("vsync", true));
                    Core.editor3dView = settings.get("editor3dView", false);
                    ConfigList lst = settings.getConfigList("pins", new ConfigList());
                    for(int i = 0; i<lst.size(); i++){
                        Core.pinnedStrs.add(lst.getString(i));
                    }
                }
                System.out.println("Loaded Settings");
                ts.finish();
                Core.refreshModules();
                System.out.println("Refreshed Modules");
                tmr.finish();
                for(Configuration configuration : Configuration.configurations){
                    if(configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                            if(b.heater&&!b.getDisplayName().contains("Standard")){
                                try{
                                    b.setInternalTexture(TextureManager.getImage("overhaul/"+b.getDisplayName().toLowerCase(Locale.ROOT).replace(" coolant heater", "").replace("liquid ", "")));
                                }catch(Exception ex){
                                    Core.warning("Failed to load internal texture for MSR Block: "+b.name, ex);
                                }
                            }
                        }
                    }
                }
                System.out.println("Set MSR Textures");
                tct.finish();
                Configuration.configurations.get(0).impose(Core.configuration);
                System.out.println("Imposed Configuration");
                tci.finish();
            }catch(Throwable t){
                Core.criticalError("Initialization Failed!", t);
            }
            Menu dialog = null;
            Menu baseDialog = null;
            if(gui.menu instanceof MenuDialog){
                dialog = baseDialog = gui.menu;
                while(baseDialog.parent instanceof MenuDialog)baseDialog = baseDialog.parent;
            }
            if(Main.benchmark)gui.open(new MenuBenchmark(gui));
            else if(Main.isBot)gui.open(new MenuDiscord(gui));
            else{
                if(!Core.tutorialShown&&!Main.headless){
                    gui.open(new MenuTutorial(gui, new MenuMain(gui)));
                    Core.tutorialShown = true;
                }else gui.open(new MenuMain(gui));
                if(Main.os==Main.OS_MACOS){
                    gui.open(new MenuCalibrateCursor(gui, gui.menu));
                }
            }
            if(baseDialog!=null){
                baseDialog.parent = gui.menu;
                gui.menu = dialog;
            }
            System.out.println("Downloading patrons list...");
            File file = new File("patrons-list.txt");
            file.delete();
            Main.downloadFile(MenuCredits.patronsLink, file.getAbsoluteFile());
            ArrayList<String> patrons = new ArrayList<>();
            try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                String line;
                while((line = in.readLine())!=null){
                    if(line.isEmpty())continue;
                    patrons.add(line);
                }
            }catch(Exception ex){}
            if(!patrons.isEmpty()){
                MenuCredits.patrons.clear();
                MenuCredits.patrons.addAll(patrons);
            }
            file.delete();
            System.out.println("Checking for updates...");
            Updater updater = Updater.read("https://raw.githubusercontent.com/ThizThizzyDizzy/nc-reactor-generator/overhaul/versions.txt", VersionManager.currentVersion, "NC-Reactor-Plannerator");
            if(updater!=null&&updater.getVersionsBehindLatestDownloadable()>0){
                new MenuUpdate(gui, gui.menu, updater).open();
            }
            System.out.println("Update Check Complete.");
        }, "Initialization Thread").start();
    }
    @Override
    public void render2d(double deltaTime){
        progressBar.width = gui.getWidth();
        progressBar.height = gui.getHeight();
        super.render2d(deltaTime);
    }
    private void attemptInit(Runnable initFunc, String success, String errorMessage, boolean critical){
        try{
            initFunc.run();
            System.out.println(success);
        }catch(Throwable t){
            if(critical)Core.criticalError(errorMessage, t);
            else Core.error(errorMessage, t);
        }
    }
}