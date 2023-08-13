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
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Main;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.Updater;
import net.ncplanner.plannerator.planner.VersionManager;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF10Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF11Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF1Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF2Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF3Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF4Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF5Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF6Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF7Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF8Reader;
import net.ncplanner.plannerator.planner.file.reader.LegacyNCPF9Reader;
import net.ncplanner.plannerator.planner.file.reader.NCPFReader;
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
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuUpdate;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.module.Module;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.module.PrimeFuelModule;
import net.ncplanner.plannerator.planner.module.QuantumTraversedEfficiencyModule;
import net.ncplanner.plannerator.planner.module.RainbowFactorModule;
import net.ncplanner.plannerator.planner.module.TiConModule;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.Configurations;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.theme.Theme;
public class MenuInit extends Menu{
    private final Task init;
    HashMap<String, Supplier<FormatReader>> readers = new HashMap<>();
    ArrayList<String> readerNames = new ArrayList<>();
    HashMap<String, Task> readerTasks = new HashMap<>();
    private final ProgressBar progressBar;
    {
        addReader("LegacyNCPF11Reader", LegacyNCPF11Reader::new);// .ncpf version 11
        addReader("LegacyNCPF10Reader", LegacyNCPF10Reader::new);// .ncpf version 10
        addReader("LegacyNCPF9Reader", LegacyNCPF9Reader::new);// .ncpf version 9
        addReader("LegacyNCPF8Reader", LegacyNCPF8Reader::new);// .ncpf version 8
        addReader("LegacyNCPF7Reader", LegacyNCPF7Reader::new);// .ncpf version 7
        addReader("LegacyNCPF6Reader", LegacyNCPF6Reader::new);// .ncpf version 6
        addReader("LegacyNCPF5Reader", LegacyNCPF5Reader::new);// .ncpf version 5
        addReader("LegacyNCPF4Reader", LegacyNCPF4Reader::new);// .ncpf version 4
        addReader("LegacyNCPF3Reader", LegacyNCPF3Reader::new);// .ncpf version 3
        addReader("LegacyNCPF2Reader", LegacyNCPF2Reader::new);// .ncpf version 2
        addReader("LegacyNCPF1Reader", LegacyNCPF1Reader::new);// .ncpf version 1
        addReader("OverhaulHellrageSFR6Reader", OverhaulHellrageSFR6Reader::new);// hellrage SFR .json 2.1.1-2.1.7 (present)
        addReader("OverhaulHellrageSFR5Reader", OverhaulHellrageSFR5Reader::new);// hellrage SFR .json 2.0.32-2.0.37
        addReader("OverhaulHellrageSFR4Reader", OverhaulHellrageSFR4Reader::new);// hellrage SFR .json 2.0.31
        addReader("OverhaulHellrageSFR3Reader", OverhaulHellrageSFR3Reader::new);// hellrage SFR .json 2.0.30
        addReader("OverhaulHellrageSFR2Reader", OverhaulHellrageSFR2Reader::new);// hellrage SFR .json 2.0.7-2.0.29
        addReader("OverhaulHellrageSFR1Reader", OverhaulHellrageSFR1Reader::new);// hellrage SFR .json 2.0.1-2.0.6
        addReader("UnderhaulHellrage2Reader", UnderhaulHellrage2Reader::new);// hellrage .json 1.2.23-1.2.25 (present)
        addReader("UnderhaulHellrage1Reader", UnderhaulHellrage1Reader::new);// hellrage .json 1.2.5-1.2.22
        addReader("OverhaulHellrageMSR6Reader", OverhaulHellrageMSR6Reader::new);// hellrage MSR .json 2.1.1-2.1.7 (present)
        addReader("OverhaulHellrageMSR5Reader", OverhaulHellrageMSR5Reader::new);// hellrage MSR .json 2.0.32-2.0.37
        addReader("OverhaulHellrageMSR4Reader", OverhaulHellrageMSR4Reader::new);// hellrage MSR .json 2.0.31
        addReader("OverhaulHellrageMSR3Reader", OverhaulHellrageMSR3Reader::new);// hellrage MSR .json 2.0.30
        addReader("OverhaulHellrageMSR2Reader", OverhaulHellrageMSR2Reader::new);// hellrage MSR .json 2.0.7-2.0.29
        addReader("OverhaulHellrageMSR1Reader", OverhaulHellrageMSR1Reader::new);// hellrage MSR .json 2.0.1-2.0.6
        addReader("OverhaulNCConfigReader", OverhaulNCConfigReader::new);// OVERHAUL nuclearcraft.cfg
        addReader("UnderhaulNCConfigReader", UnderhaulNCConfigReader::new);// UNDERHAUL nuclearcraft.cfg
        addReader("NCPFReader", NCPFReader::new);//legacy NCPF reader
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
        Task tm6 = tm.addSubtask("Adding Quantum Traversed Efficiency Module");
        Task tmX = tm.addSubtask("Adding [REDACTED] Modules");
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
                Core.modules.add(new QuantumTraversedEfficiencyModule());
                tm6.finish();
                Core.modules.add(new TiConModule());
                tmX.finish();
                System.out.println("Added Modules");
                if(f.exists()){
                    Config settings = Config.newConfig(f);
                    settings.load();
                    System.out.println("Loading theme");
                    Object o = settings.get("theme");
                    if(o instanceof String){
                        Core.setTheme(Theme.getByName((String)o));
                    }else Core.setTheme(Theme.getByLegacyID((int)o));
                    Config modules = settings.get("modules", Config.newConfig());
                    HashMap<Module, Boolean> moduleStates = new HashMap<>();
                    for(String key : modules.properties()){
                        for(Module m : Core.modules){
                            if(m.name.equals(key))moduleStates.put(m, modules.getBoolean(key));
                        }
                    }
                    for(Module m : Core.modules){
                        moduleStates.put(m, true);
                        if(!moduleStates.containsKey(m))continue;
                        if(m.isActive()){
                            if(!moduleStates.get(m))m.deactivate();
                        }else{
                            if(moduleStates.get(m))m.activate();
                        }
                    }
                    Core.tutorialShown = settings.get("tutorialShown", false);
                    Core.invertUndoRedo = settings.get("invertUndoRedo", false);
                    Core.autoBuildCasing = settings.get("autoBuildCasing", true);
                    Core.imageExport3DView = settings.get("imageExport3DView", true);
                    Core.imageExportCasing = settings.get("imageExportCasing", true);
                    Core.imageExportCasing3D = settings.get("imageExportCasing3D", true);
                    Core.dssl = settings.get("dssl", false);
                    Core.rememberConfig = settings.get("rememberConfig", false);
                    Core.lastLoadedConfig = settings.get("lastLoadedConfig", "default");
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
                attemptInit(Configurations::initNuclearcraftConfiguration, "Loaded NC Config", "Failed to load NuclearCraft configuration!", false);
                tc1.finish();
                for(NCPFConfigurationContainer configuration : Configurations.configurations){
                    configuration.withConfiguration(OverhaulMSRConfiguration::new, (msr)->{
                        for(BlockElement b : msr.blocks){
                            if(b.heater!=null&&!b.getDisplayName().contains("Standard")){
                                try{
                                    Image other = TextureManager.getImage("overhaul/"+b.getDisplayName().toLowerCase(Locale.ROOT).replace(" coolant heater", "").replace("liquid ", ""));
                                    Image texture = b.texture.texture;
                                    int left = Math.max(0,texture.getWidth()*5/16-1);
                                    int right = Math.min(texture.getWidth()*11/16, texture.getWidth()-1);
                                    int up = Math.max(0,texture.getHeight()*5/16-1);
                                    int down = Math.min(texture.getHeight()*11/16, texture.getHeight()-1);
                                    Image displayImg = new Image(texture.getWidth(), texture.getHeight());
                                    for(int x = 0; x<texture.getWidth(); x++){
                                        for(int y = 0; y<texture.getHeight(); y++){
                                            if(x>left&&y>up&&x<right&&y<down){
                                                displayImg.setColor(x, y, TextureManager.convert(new Color(other.getRGB(x, y))));
                                            }else{
                                                displayImg.setColor(x, y, TextureManager.convert(new Color(texture.getRGB(x, y))));
                                            }
                                        }
                                    }
                                    b.texture.displayTexture = displayImg;
                                }catch(Exception ex){
                                    Core.warning("Failed to load internal texture for MSR Block: "+b.getDisplayName(), ex);
                                }
                            }
                        }
                    });
                }
                System.out.println("Set MSR Textures");
                Core.setConfiguration(Configurations.NUCLEARCRAFT);
                System.out.println("Imposed Configuration");
                //TODO remember config, but for real this time
                if(Core.rememberConfig){
                    boolean bad = false;
                    String message = "Unknown location!";
                    if(Core.lastLoadedConfig==null)bad = true;//should never be null, but just to make sure
                    else{
                        String[] split = Core.lastLoadedConfig.split("/");
                        switch(split[0]){
                            case "modules":
                                if(split.length!=3){
                                    bad = true;
                                    message = "Invalid module path!";
                                    break;
                                }
                                Module module = null;
                                for(Module m : Core.modules){
                                    if(m.name.equals(split[1]))module = m;
                                }
                                if(module==null){
                                    bad = true;
                                    message = "Unknown module "+split[1]+"!";
                                    break;
                                }
                                NCPFConfigurationContainer config = null;
                                for(Object o : module.ownConfigs){
                                    NCPFConfigurationContainer c = (NCPFConfigurationContainer)o;
//                                    if(c.name.equals(split[2]))config = c;
                                }
                                if(config==null){
                                    bad = true;
                                    message = "Unknown configuration "+split[2]+" of module "+split[1]+"!";
                                    break;
                                }
                                Core.setConfiguration(config);
                                break;
                            case "external":
                                if(split.length<2){
                                    bad = true;
                                    break;
                                }
                                String path = Core.lastLoadedConfig.substring(split[0].length()+1);
                                File file = new File(path);
                                if(!file.exists()){
                                    bad = true;
                                    message = "Could not find external configuration!";
                                    break;
                                }
                                Project ncpf = FileReader.read(file);
                                Core.setConfiguration(ncpf.conglomeration);
                                break;
                            case "default": //just do nothing, already done
                                break;
                            default:
                                bad = true;
                                break;
                        }
                    }
                    if(bad)Core.warning("Failed to load last configuration: "+message+" ("+Core.lastLoadedConfig+")!", null);
                }
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