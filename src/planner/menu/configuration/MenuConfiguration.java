package planner.menu.configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.TextureManager;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import planner.Core;
import planner.ImageIO;
import planner.file.FileFormat;
import planner.file.FileReader;
import planner.file.JSON;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.overhaul.MenuOverhaulMSRConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulSFRConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulTurbineConfiguration;
import planner.menu.configuration.underhaul.MenuUnderhaulSFRConfiguration;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import simplelibrary.image.Image;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuConfiguration extends ConfigurationMenu{
    private final MenuComponentMinimalistTextBox name;
    private final MenuComponentLabel underhaulTitle, overhaulTitle;
    private final MenuComponentMinimalistTextBox overhaulVersion;
    private final MenuComponentMinimalistTextBox underhaulVersion;
    private final MenuComponentMinimalistButton deleteUnderhaul;
    private final MenuComponentMinimalistButton deleteOverhaul;
    private final MenuComponentMinimalistButton underhaulSFR, overhaulSFR, overhaulMSR, overhaulTurbine, overhaulFusion;
    private final MenuComponentMinimalistButton deleteUnderhaulSFR, deleteOverhaulSFR, deleteOverhaulMSR, deleteOverhaulTurbine, deleteOverhaulFusion;
    private final MenuComponentMinimalistButton configGuidelines;
    private final MenuComponentMinimalistButton saveAddon;
    private final MenuComponentLabel addonsLabel;
    private final MenuComponentMinimaList addonsList;
    private final MenuComponentMinimalistButton importAddon;
    private final MenuComponentMinimalistButton createAddon;
    private boolean refreshNeeded = false;
    private boolean threadShouldStop = false;
    private boolean threadHasStopped = true;
    public MenuConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, configuration.addon?configuration.name:"Configuration");
        name = add(new MenuComponentMinimalistTextBox(sidebar.width, 0, 0, 64, configuration.name, true, "Name")).setTooltip(configuration.addon?"The name of the addon\nThis should not change between versions":"The name of the modpack\nThis should not change between versions");
        underhaulTitle = add(new MenuComponentLabel(sidebar.width, name.y+name.height, 0, 48, "Underhaul"));
        overhaulTitle = add(new MenuComponentLabel(sidebar.width, name.y+name.height, 0, 48, "Overhaul"));
        configGuidelines = addToSidebarBottom(new MenuComponentMinimalistButton(0, 0, 0, 48, "Configuration Guidelines (Google doc)", true, true).setTooltip("Opens a webpage in your default browser containing configuration guidelines\nThese guidelines should be followed to ensure no conflicts arise with the default configurations"));
        configGuidelines.textInset = 0;
        overhaulVersion = add(new MenuComponentMinimalistTextBox(overhaulTitle.x, overhaulTitle.y+overhaulTitle.height, 0, 56, configuration.overhaulVersion, true, "Version")).setTooltip(configuration.addon?"The version string for the Overhaul version of this addon":"The modpack version");
        underhaulVersion = add(new MenuComponentMinimalistTextBox(underhaulTitle.x, underhaulTitle.y+underhaulTitle.height, 0, 56, configuration.underhaulVersion, true, "Version")).setTooltip(configuration.addon?"The version string for the Underhaul version of this addon":"The modpack version");
        deleteUnderhaul = add(new MenuComponentMinimalistButton(underhaulTitle.x, underhaulVersion.y+underhaulVersion.height, 0, 48, "Delete (Shift)", false, true).setTooltip("Delete the underhaul configuration\n(Press Shift)"));
        deleteOverhaul = add(new MenuComponentMinimalistButton(overhaulTitle.x, overhaulVersion.y+overhaulVersion.height, 0, 48, "Delete (Shift)", false, true).setTooltip("Delete the overhaul configuration\n(Press Shift)"));
        underhaulSFR = add(new MenuComponentMinimalistButton(deleteUnderhaul.x, deleteUnderhaul.y+deleteUnderhaul.height, 0, Core.hasUnderhaulSFR()?48:0, "Solid Fission Configuration", configuration.underhaul!=null&&configuration.underhaul.fissionSFR!=null, true).setTooltip("Modify the Underhaul SFR configuration"));
        overhaulSFR = add(new MenuComponentMinimalistButton(deleteOverhaul.x, deleteOverhaul.y+deleteOverhaul.height, 0, Core.hasOverhaulSFR()?48:0, "Solid Fission Configuration", configuration.overhaul!=null&&configuration.overhaul.fissionSFR!=null, true).setTooltip("Modify the Overhaul SFR configuration"));
        overhaulMSR = add(new MenuComponentMinimalistButton(deleteOverhaul.x, overhaulSFR.y+overhaulSFR.height, 0, Core.hasOverhaulMSR()?48:0, "Salt Fission Configuration", configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null, true).setTooltip("Modify the Overhaul MSR configuration"));
        overhaulTurbine = add(new MenuComponentMinimalistButton(deleteOverhaul.x, overhaulMSR.y+overhaulMSR.height, 0, Core.hasOverhaulTurbine()?48:0, "Turbine Configuration", configuration.overhaul!=null&&configuration.overhaul.turbine!=null, true).setTooltip("Modify the Overhaul Turbine configuration"));
        overhaulFusion = add(new MenuComponentMinimalistButton(deleteOverhaul.x, overhaulTurbine.y+overhaulTurbine.height, 0, Core.hasOverhaulFusion()?48:0, "Fusion Configuration", configuration.overhaul!=null&&configuration.overhaul.fusion!=null, true).setTooltip("Modify the Overhaul Fusion configuration"));
        deleteUnderhaulSFR = add(new MenuComponentMinimalistButton(deleteUnderhaul.x, deleteUnderhaul.y+deleteUnderhaul.height, 0, Core.hasUnderhaulSFR()?48:0, "Del", false, true).setTooltip("Delete the Underhaul SFR configuration\n(Press Shift)"));
        deleteOverhaulSFR = add(new MenuComponentMinimalistButton(deleteOverhaul.x, deleteOverhaul.y+deleteOverhaul.height, 0, Core.hasOverhaulSFR()?48:0, "Del", false, true).setTooltip("Delete the Overhaul SFR configuration\n(Press Shift)"));
        deleteOverhaulMSR = add(new MenuComponentMinimalistButton(deleteOverhaul.x, overhaulSFR.y+overhaulSFR.height, 0, Core.hasOverhaulMSR()?48:0, "Del", false, true).setTooltip("Delete the Overhaul MSR configuration\n(Press Shift)"));
        deleteOverhaulTurbine = add(new MenuComponentMinimalistButton(deleteOverhaul.x, overhaulMSR.y+overhaulMSR.height, 0, Core.hasOverhaulTurbine()?48:0, "Del", false, true).setTooltip("Delete the Overhaul Turbine configuration\n(Press Shift)"));
        deleteOverhaulFusion = add(new MenuComponentMinimalistButton(deleteOverhaul.x, overhaulTurbine.y+overhaulTurbine.height, 0, Core.hasOverhaulFusion()?48:0, "Del", false, true).setTooltip("Delete the Overhaul Fusion configuration\n(Press Shift)"));
        saveAddon = new MenuComponentMinimalistButton(sidebar.width, Math.max(overhaulFusion.y+overhaulFusion.height,underhaulSFR.y+underhaulSFR.height), 0, 48, "Save Addon", true, true).setTooltip("Save Addon");
        addonsLabel = new MenuComponentLabel(sidebar.width, Math.max(overhaulFusion.y+overhaulFusion.height,underhaulSFR.y+underhaulSFR.height), 0, 48, "Addons", true);
        addonsList = new MenuComponentMinimaList(sidebar.width, addonsLabel.y+addonsLabel.height, 0, 0, 16);
        importAddon = new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Import Addon", true, true);
        createAddon = new MenuComponentMinimalistButton(sidebar.width, 0, 0, 48, "Create Addon", true, true);
        if(configuration.addon){
            add(saveAddon);
            saveAddon.addActionListener((e) -> {
                if(configuration.addon){
                    try{
                        Core.createFileChooser(new File(configuration.name), (file) -> {
                            if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                            try(FileOutputStream stream = new FileOutputStream(file)){
                                Config header = Config.newConfig();
                                header.set("version", NCPFFile.SAVE_VERSION);
                                header.set("count", 0);
                                header.save(stream);
                                AddonConfiguration.generate(Core.configuration, configuration).save(null, Config.newConfig()).save(stream);
                            }catch(IOException ex){
                                Sys.error(ErrorLevel.severe, "Failed to save addon", ex, ErrorCategory.fileIO);
                            }
                        }, FileFormat.NCPF);
                    }catch(IOException ex){
                        Sys.error(ErrorLevel.severe, "Failed to save addon!", ex, ErrorCategory.fileIO);
                    }
                }else{
                    gui.open(new MenuConfiguration(gui, this, Core.configuration));
                }
            });
        }else{
            add(addonsLabel);
            add(addonsList);
            add(importAddon);
            add(createAddon);
            createAddon.addActionListener((e) -> {
                Configuration c = new Configuration("New Addon", null, null);
                c.addon = true;
                Core.configuration.addons.add(c);
                gui.open(new MenuConfiguration(gui, this, c));
            });
            importAddon.addActionListener((e) -> {
                try{
                    Core.createFileChooser((file) -> {
                        loadAddon(file);
                        onGUIOpened();
                    }, FileFormat.NCPF);
                }catch(IOException ex){
                    Sys.error(ErrorLevel.severe, "Failed to import addon!", ex, ErrorCategory.fileIO);
                }
            });
        }
        deleteOverhaul.addActionListener((e) -> {
            onGUIClosed();
            if(configuration.overhaul==null){
                configuration.overhaul = new OverhaulConfiguration();
                configuration.overhaulVersion = "0";
            }else{
                configuration.overhaul = null;
                configuration.overhaulVersion = null;
            }
            onGUIOpened();
        });
        deleteUnderhaul.addActionListener((e) -> {
            onGUIClosed();
            if(configuration.underhaul==null){
                configuration.underhaul = new UnderhaulConfiguration();
                configuration.underhaulVersion = "0";
            }else{
                configuration.underhaul = null;
                configuration.underhaulVersion = null;
            }
            onGUIOpened();
        });
        deleteUnderhaulSFR.addActionListener((e) -> {
            if(configuration.underhaul.fissionSFR==null){
                configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
            }else{
                configuration.underhaul.fissionSFR = null;
            }
            onGUIOpened();
        });
        deleteOverhaulSFR.addActionListener((e) -> {
            if(configuration.overhaul.fissionSFR==null){
                configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
            }else{
                configuration.overhaul.fissionSFR = null;
            }
            onGUIOpened();
        });
        deleteOverhaulMSR.addActionListener((e) -> {
            if(configuration.overhaul.fissionMSR==null){
                configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
            }else{
                configuration.overhaul.fissionMSR = null;
            }
            onGUIOpened();
        });
        deleteOverhaulTurbine.addActionListener((e) -> {
            if(configuration.overhaul.turbine==null){
                configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
            }else{
                configuration.overhaul.turbine = null;
            }
            onGUIOpened();
        });
        deleteOverhaulFusion.addActionListener((e) -> {
            if(configuration.overhaul.fusion==null){
                configuration.overhaul.fusion = new multiblock.configuration.overhaul.fusion.FusionConfiguration();
            }else{
                configuration.overhaul.fusion = null;
            }
            onGUIOpened();
        });
        configGuidelines.addActionListener((e) -> {
            Core.openURL("https://docs.google.com/document/d/1dzU2arDrD7n9doRua8laxzRy9_RtX-cuv1sUJBB5aGY/edit?usp=sharing");
        });
        underhaulSFR.addActionListener((e) -> {
            gui.open(new MenuUnderhaulSFRConfiguration(gui, this, configuration));
        });
        overhaulSFR.addActionListener((e) -> {
            gui.open(new MenuOverhaulSFRConfiguration(gui, this, configuration));
        });
        overhaulMSR.addActionListener((e) -> {
            gui.open(new MenuOverhaulMSRConfiguration(gui, this, configuration));
        });
        overhaulTurbine.addActionListener((e) -> {
            gui.open(new MenuOverhaulTurbineConfiguration(gui, this, configuration));
        });
        overhaulFusion.addActionListener((e) -> {
//            gui.open(new MenuOverhaulFusionConfiguration(gui, this, configuration));
        });
    }
    @Override
    public void onGUIOpened(){
        underhaulVersion.editable = configuration.underhaul!=null&&Core.configuration.underhaul!=null;
        overhaulVersion.editable = configuration.overhaul!=null&&Core.configuration.overhaul!=null;
        name.text = configuration.name==null?"":configuration.name;
        overhaulVersion.text = configuration.overhaulVersion==null?"":configuration.overhaulVersion;
        underhaulVersion.text = configuration.underhaulVersion==null?"":configuration.underhaulVersion;
        underhaulSFR.enabled = configuration.underhaul!=null&&configuration.underhaul.fissionSFR!=null&&Core.configuration.underhaul!=null&&Core.configuration.underhaul.fissionSFR!=null;
        overhaulSFR.enabled = configuration.overhaul!=null&&configuration.overhaul.fissionSFR!=null&&Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionSFR!=null;
        overhaulMSR.enabled = configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null&&Core.configuration.overhaul!=null&&Core.configuration.overhaul.fissionMSR!=null;
        overhaulTurbine.enabled = configuration.overhaul!=null&&configuration.overhaul.turbine!=null&&Core.configuration.overhaul!=null&&Core.configuration.overhaul.turbine!=null;
        overhaulFusion.enabled = configuration.overhaul!=null&&configuration.overhaul.fusion!=null&&Core.configuration.overhaul!=null&&Core.configuration.overhaul.fusion!=null;
        if(!configuration.addon&&!threadShouldStop){
            threadShouldStop = true;
            new Thread(() -> {
                Object sync = new Object();
                synchronized(sync){
                    while(!threadHasStopped){
                        try{
                            sync.wait(5);
                        }catch(InterruptedException ex){}
                    }
                }
                threadHasStopped = threadShouldStop = false;
                synchronized(addonsList){
                    addonsList.components.clear();
                    for(Configuration c : configuration.addons){
                        addonsList.add(new MenuComponentAddon(c));
                    }
                }
                C:for(Supplier<AddonConfiguration> c : Configuration.internalAddons){
                    AddonConfiguration got = Configuration.internalAddonCache.get(c);
                    if(got==null){
                        got = c.get();
                        Configuration.internalAddonCache.put(c, got);
                    }
                    for(Configuration cc : configuration.addons){
                        if(got.nameMatches(cc))continue C;//CC
                    }
                    if(threadShouldStop){
                        threadHasStopped = true;
                        return;
                    }
                    synchronized(addonsList){
                        addonsList.add(new MenuComponentInternalAddon(c, got));
                    }
                }
                threadHasStopped = true;
            }).start();
        }
    }
    @Override
    public void onGUIClosed(){
        configuration.name = name.text.trim().isEmpty()?null:name.text;
        if(configuration.overhaul!=null)configuration.overhaulVersion = overhaulVersion.text.trim().isEmpty()?null:overhaulVersion.text;
        if(configuration.underhaul!=null)configuration.underhaulVersion = underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text;
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIClosed();
            onGUIOpened();
            refreshNeeded = false;
        }
        synchronized(addonsList){
            super.tick();
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        addonsLabel.width = addonsList.width = saveAddon.width = name.width = gui.helper.displayWidth()-name.x;
        importAddon.width = createAddon.width = addonsLabel.width/2;
        createAddon.x = importAddon.x+importAddon.width;
        deleteUnderhaul.width = deleteOverhaul.width = overhaulVersion.width = underhaulVersion.width = overhaulTitle.width = underhaulTitle.width = name.width/2-4;
        overhaulSFR.x = overhaulMSR.x = overhaulTurbine.x = overhaulFusion.x = deleteOverhaul.x = overhaulVersion.x = overhaulTitle.x = underhaulTitle.x+underhaulTitle.width+8;
        underhaulSFR.width = overhaulSFR.width = overhaulMSR.width = overhaulTurbine.width = overhaulFusion.width = overhaulVersion.width*3/4;
        deleteUnderhaulSFR.width = deleteOverhaulSFR.width = deleteOverhaulMSR.width = deleteOverhaulTurbine.width = deleteOverhaulFusion.width = overhaulVersion.width/4;
        deleteUnderhaulSFR.x = underhaulSFR.x+underhaulSFR.width;
        deleteOverhaulSFR.x = overhaulSFR.x+overhaulSFR.width;
        deleteOverhaulMSR.x = overhaulMSR.x+overhaulMSR.width;
        deleteOverhaulTurbine.x = overhaulTurbine.x+overhaulTurbine.width;
        deleteOverhaulFusion.x = overhaulFusion.x+overhaulFusion.width;
        boolean badThing = false;
        for(Configuration c : Configuration.configurations){
            if(Objects.equals(name.text.trim().isEmpty()?null:name.text,c.name)){
                if(Objects.equals(overhaulVersion.text.trim().isEmpty()?null:overhaulVersion.text, c.overhaulVersion)&&!c.isOverhaulConfigurationEqual(configuration)
                        ||Objects.equals(underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text, c.underhaulVersion)&&!c.isUnderhaulConfigurationEqual(configuration)){
                    badThing = true;
                    Core.applyColor(Core.theme.getConfigurationWarningTextColor());
                    String str = "Configuration does not match stored configuration "+c.toString()+"!";
                    double Y = gui.helper.displayHeight()-64;
                    double H = 56;
                    double len = FontManager.getLengthForStringWithHeight(str, H/2)+16;
                    double scale = Math.min(1, (gui.helper.displayWidth()-sidebar.width)/len);
                    drawCenteredText(sidebar.width, Y, gui.helper.displayWidth(), Y+H/2*scale, str);
                    drawCenteredText(sidebar.width, Y+H/2*scale, gui.helper.displayWidth(), Y+H/2*scale*2, "Please review configuration guidelines");
                }
            }
        }
        importAddon.y = createAddon.y = Core.helper.displayHeight()-importAddon.height-(badThing?64:0);
        addonsList.height = importAddon.y-addonsList.y;
        Core.applyColor(Core.theme.getConfigurationDividerColor());
        drawRect(underhaulTitle.x+underhaulTitle.width, underhaulTitle.y, overhaulTitle.x, overhaulFusion.y+overhaulFusion.height, 0);
        if(configuration.overhaul==null){
            deleteOverhaul.enabled = (configuration.addon&&Core.configuration.overhaul==null)?false:Core.isShiftPressed();
        }else{
            deleteOverhaul.enabled = Core.isShiftPressed();
        }
        if(configuration.underhaul==null){
            deleteUnderhaul.enabled = (configuration.addon&&Core.configuration.underhaul==null)?false:Core.isShiftPressed();
        }else{
            deleteUnderhaul.enabled = Core.isShiftPressed();
        }
        deleteOverhaul.label = (configuration.overhaul==null?"Create":"Delete")+" (Shift)";
        deleteUnderhaul.label = (configuration.underhaul==null?"Create":"Delete")+" (Shift)";
        deleteOverhaul.setTooltip((configuration.overhaul==null?"Create":"Delete")+" the overhaul configuration\n(Press Shift)");
        deleteUnderhaul.setTooltip((configuration.underhaul==null?"Create":"Delete")+" the underhaul configuration\n(Press Shift)");
        if(Core.configuration.underhaul!=null&&configuration.underhaul!=null&&configuration.underhaul.fissionSFR==null){
            deleteUnderhaulSFR.enabled = (configuration.addon&&Core.configuration.underhaul.fissionSFR==null)?false:(Core.isShiftPressed());
        }else{
            deleteUnderhaulSFR.enabled = Core.configuration.underhaul==null?false:configuration.underhaul!=null&&Core.isShiftPressed();
        }
        deleteUnderhaulSFR.label = (configuration.underhaul==null||configuration.underhaul.fissionSFR==null?"Create":"Delete")+" (Shift)";
        deleteUnderhaulSFR.setTooltip((configuration.underhaul==null||configuration.underhaul.fissionSFR==null?"Create":"Delete")+" the Underhaul SFR configuration");
        if(Core.configuration.overhaul!=null&&configuration.overhaul!=null&&configuration.overhaul.fissionSFR==null){
            deleteOverhaulSFR.enabled = (configuration.addon&&Core.configuration.overhaul.fissionSFR==null)?false:(Core.isShiftPressed());
        }else{
            deleteOverhaulSFR.enabled = Core.configuration.overhaul==null?false:configuration.overhaul!=null&&Core.isShiftPressed();
        }
        if(Core.configuration.overhaul!=null&&configuration.overhaul!=null&&configuration.overhaul.fissionMSR==null){
            deleteOverhaulMSR.enabled = (configuration.addon&&Core.configuration.overhaul.fissionMSR==null)?false:(Core.isShiftPressed());
        }else{
            deleteOverhaulMSR.enabled = Core.configuration.overhaul==null?false:configuration.overhaul!=null&&Core.isShiftPressed();
        }
        if(Core.configuration.overhaul!=null&&configuration.overhaul!=null&&configuration.overhaul.turbine==null){
            deleteOverhaulTurbine.enabled = (configuration.addon&&Core.configuration.overhaul.turbine==null)?false:(Core.isShiftPressed());
        }else{
            deleteOverhaulTurbine.enabled = Core.configuration.overhaul==null?false:configuration.overhaul!=null&&Core.isShiftPressed();
        }
        if(Core.configuration.overhaul!=null&&configuration.overhaul!=null&&configuration.overhaul.fusion==null){
            deleteOverhaulFusion.enabled = (configuration.addon&&Core.configuration.overhaul.fusion==null)?false:(Core.isShiftPressed());
        }else{
            deleteOverhaulFusion.enabled = Core.configuration.overhaul==null?false:configuration.overhaul!=null&&Core.isShiftPressed();
        }
        deleteOverhaulSFR.label = (configuration.overhaul==null||configuration.overhaul.fissionSFR==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulMSR.label = (configuration.overhaul==null||configuration.overhaul.fissionMSR==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulTurbine.label = (configuration.overhaul==null||configuration.overhaul.turbine==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulFusion.label = (configuration.overhaul==null||configuration.overhaul.fusion==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulSFR.setTooltip((configuration.overhaul==null||configuration.overhaul.fissionSFR==null?"Create":"Delete")+" the Overhaul SFR configuration");
        deleteOverhaulMSR.setTooltip((configuration.overhaul==null||configuration.overhaul.fissionMSR==null?"Create":"Delete")+" the Overhaul MSR configuration");
        deleteOverhaulTurbine.setTooltip((configuration.overhaul==null||configuration.overhaul.turbine==null?"Create":"Delete")+" the Overhaul Turbine configuration");
        deleteOverhaulFusion.setTooltip((configuration.overhaul==null||configuration.overhaul.fusion==null?"Create":"Delete")+" the Overhaul Fusion Reactor configuration");
        synchronized(addonsList){
            super.render(millisSinceLastTick);
        }
    }
    @Override
    public boolean onFilesDropped(double x, double y, String[] files){
        if(!configuration.addon){
            for(String fil : files){
                if(fil.endsWith(".zip")){
                    try{
                        //load script addon
                        ZipFile file = new ZipFile(fil);
                        String[] root = new String[2];
                        file.stream().forEach((entry) -> {
                            String nam = entry.getName();
                            if(!nam.contains("scripts"))return;
                            String rt = nam.substring(0, nam.indexOf("scripts"));
                            root[root[0]==null||root[0].equals(rt)?0:1] = rt;
                        });
                        if(root[0]==null)throw new IllegalArgumentException("File contains no scripts folder!");
                        if(root[1]!=null)throw new IllegalArgumentException("File contains multiple script folders!");
                        ArrayList<ZipEntry> scripts = new ArrayList<>();//names not important
                        HashMap<String, JSON.JSONObject> ctBlockstates = new HashMap<>();
                        HashMap<String, JSON.JSONObject> blockstates = new HashMap<>();
                        HashMap<String, JSON.JSONObject> ctModels = new HashMap<>();
                        HashMap<String, Image> ctTextures = new HashMap<>();
                        HashMap<String, HashMap<String, String>> ctLangFiles = new HashMap<>();
                        HashMap<String, HashMap<String, String>> langFiles = new HashMap<>();
                        HashMap<String, Image> textures = new HashMap<>();
                        file.stream().forEach((entry) -> {
                            String nam = entry.getName();
                            if(!nam.startsWith(root[0]))return;
                            nam = nam.substring(root[0].length());//root removed
                            try{
                                if(nam.matches("scripts/[\\d\\w -]+\\.zs")){
                                    scripts.add(entry);
                                }else if(nam.matches("blockstates/[\\d\\w -]+\\.json")){
                                    blockstates.put(nam.substring("blockstates/".length(), nam.length()-5), JSON.parse(file.getInputStream(entry)));
                                }else if(nam.matches("contenttweaker/blockstates/[\\d\\w -]+\\.json")){
                                    ctBlockstates.put(nam.substring("contenttweaker/blockstates/".length(), nam.length()-5), JSON.parse(file.getInputStream(entry)));
                                }else if(nam.matches("contenttweaker/models/[\\d\\w /-]+\\.json")){
                                    ctModels.put(nam.substring("contenttweaker/models/".length(), nam.length()-5), JSON.parse(file.getInputStream(entry)));
                                }else if(nam.matches("contenttweaker/textures/[\\d\\w /-]+\\.png")){
                                    ctTextures.put(nam.substring("contenttweaker/textures/".length(), nam.length()-4), ImageIO.read(file.getInputStream(entry)));
                                }else if(nam.matches("lang/[\\d\\w /-]+\\.lang")){
                                    HashMap<String, String> lang = new HashMap<>();
                                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))){
                                        String line;
                                        while((line = reader.readLine())!=null){
                                            if(line.trim().startsWith("//"))continue;
                                            if(line.trim().isEmpty())continue;
                                            lang.put(line.split("=")[0], line.split("=", 2)[1]);
                                        }
                                    }
                                    langFiles.put(nam.substring("lang/".length(), nam.length()-5), lang);
                                }else if(nam.matches("contenttweaker/lang/[\\d\\w /-]+\\.lang")){
                                    HashMap<String, String> lang = new HashMap<>();
                                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))){
                                        String line;
                                        while((line = reader.readLine())!=null){
                                            if(line.trim().startsWith("//"))continue;
                                            if(line.trim().isEmpty())continue;
                                            lang.put(line.split("=")[0], line.split("=", 2)[1]);
                                        }
                                    }
                                    ctLangFiles.put(nam.substring("contenttweaker/lang/".length(), nam.length()-5), lang);
                                }else if(nam.matches("textures/[\\d\\w /-]+\\.png")){
                                    textures.put(nam.substring("textures/".length(), nam.length()-4), ImageIO.read(file.getInputStream(entry)));
                                }else if(nam.contains(".")&&!nam.endsWith("/"))System.err.println(nam);
                            }catch(IOException ex){
                                throw new RuntimeException(ex);
                            }
                        });
                        scripts.forEach((entry) -> {
                            try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))){
                                String s = "";
                                String line;
                                while((line = reader.readLine())!=null){
                                    s+=line+"\n";
                                }
                                HashMap<String, String> imports = new HashMap<>();
                                while(!s.isEmpty()){
                                    char c = s.charAt(0);
                                    for(String key : imports.keySet()){
                                        if(s.startsWith(key+"."))s = imports.get(key)+s.substring(key.length());
                                    }
                                    if(s.startsWith("//")){
                                        s = s.substring(s.indexOf("\n"));
                                        continue;
                                    }
                                    if(s.startsWith("import")){
                                        String theImport = s.substring("import ".length(), s.indexOf(";"));
                                        String[] split = theImport.split("\\.");
                                        imports.put(split[split.length-1], theImport);
                                        System.out.println("Imported "+theImport+" as "+split[split.length-1]);
                                        s = s.substring(s.indexOf(";")+1);
                                        continue;
                                    }
                                    if(s.startsWith("#modloaded")){
                                        String str = s.substring("#modloaded".length(), s.indexOf("\n")).trim();
                                        Sys.error(ErrorLevel.warning, "Script addon has conditional content for mod "+str+"! All content will be included!", null, ErrorCategory.fileIO);
                                        s = s.substring(s.indexOf("\n"));
                                        continue;
                                    }
                                    if(s.startsWith("#loader")){
                                        s = s.substring(s.indexOf("\n"));
                                        //TODO properly handle loaders
                                        continue;
                                    }
                                    if(s.startsWith("/*")){
                                        s = s.substring(2).substring(s.indexOf("*/"));
                                        continue;
                                    }
                                    if(Character.isWhitespace(c)){
                                        s = s.substring(1);
                                        continue;
                                    }
                                    throw new IllegalArgumentException("Unknown ZS bit: "+s.substring(0, Math.min(s.length(), 25)).replace("\n", "")+"...");
                                }
                                System.out.println(s);
                            }catch(Exception ex){
                                Sys.error(ErrorLevel.severe, "Failed to load script: "+entry.getName(), ex, ErrorCategory.fileIO);
                            }
                        });
                    }catch(Exception ex){
                        Sys.error(ErrorLevel.severe, "Failed to load script addon "+new File(fil).getName(), ex, ErrorCategory.fileIO);
                    }
                }else loadAddon(new File(fil));
            }
        }else{
            //load ZS because it's easier than the slow method
            ArrayList<multiblock.configuration.overhaul.turbine.Block> turbineBlocks = new ArrayList<>();
            HashMap<multiblock.configuration.overhaul.turbine.Block, String> turbineRules = new HashMap<>();
            ArrayList<multiblock.configuration.overhaul.fissionsfr.Block> fissionSFRBlocks = new ArrayList<>();
            ArrayList<multiblock.configuration.overhaul.fissionsfr.BlockRecipe> fissionSFRRecipes = new ArrayList<>();
            HashMap<multiblock.configuration.overhaul.fissionsfr.Block, String> fissionSFRRules = new HashMap<>();
            ArrayList<multiblock.configuration.overhaul.fissionmsr.Block> fissionMSRBlocks = new ArrayList<>();
            ArrayList<multiblock.configuration.overhaul.fissionmsr.BlockRecipe> fissionMSRRecipes = new ArrayList<>();
            HashMap<multiblock.configuration.overhaul.fissionmsr.Block, String> fissionMSRRules = new HashMap<>();
            for(String fil : files){
                if(fil.endsWith(".zs")){
                    //<editor-fold defaultstate="collapsed" desc="Loading ZS file">
                    if(configuration.overhaul==null){
                        Sys.error(ErrorLevel.severe, "Cannot load ZS file with no overhaul configuration!", null, ErrorCategory.fileIO);
                        continue;
                    }
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fil))))){
                        String line;
                        int lineNum = 0;
                        while((line = reader.readLine())!=null){
                            line = line.trim();
                            if(line.startsWith("mods.nuclearcraft."))line = line.substring("mods.nuclearcraft.".length());
                            lineNum++;
                            try{
                                //<editor-fold defaultstate="collapsed" desc="parsing line">
                                if(line.startsWith("SolidFission.")){
                                    String fission = line.substring("SolidFission.".length());
                                    if(fission.startsWith("addRecipe")){
                                        //<editor-fold defaultstate="collapsed" desc="addRecipe">
                                        if(configuration.overhaul.fissionSFR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot add fission fuel without SFR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = fission.substring(fission.indexOf('(')+1, fission.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String inputName = args[0].substring(1, args[0].length()-1);
                                        String outputName = args[1].substring(1, args[1].length()-1);
                                        int time = Integer.parseInt(args[2]);
                                        int heat = Integer.parseInt(args[3]);
                                        float efficiency = Float.parseFloat(args[4]);
                                        int criticality = Integer.parseInt(args[5]);
                                        boolean selfPriming = Boolean.parseBoolean(args[6]);
                                        for(multiblock.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
                                            if(block.recipes.isEmpty())continue;
                                            if(block.fuelCell){
                                                multiblock.configuration.overhaul.fissionsfr.Block fake = null;
                                                for(multiblock.configuration.overhaul.fissionsfr.Block possible : configuration.overhaul.fissionSFR.allBlocks){
                                                    if(possible.name.equals(block.name))fake = possible;
                                                }
                                                if(fake==null){
                                                    fake = new multiblock.configuration.overhaul.fissionsfr.Block(block.name);
                                                    fake.fuelCell = block.fuelCell;
                                                    fake.moderator = block.moderator;
                                                    fake.shield = block.shield;
                                                    fake.heatsink = block.heatsink;
                                                    fake.reflector = block.reflector;
                                                    fake.irradiator = block.irradiator;
                                                    configuration.overhaul.fissionSFR.allBlocks.add(fake);
                                                }
                                                multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionsfr.BlockRecipe(inputName, outputName);
                                                recipe.fuelCellTime = time;
                                                recipe.fuelCellHeat = heat;
                                                recipe.fuelCellEfficiency = efficiency;
                                                recipe.fuelCellCriticality = criticality;
                                                recipe.fuelCellSelfPriming = selfPriming;
                                                fake.recipes.add(recipe);
                                                block.allRecipes.add(recipe);
                                                fissionSFRRecipes.add(recipe);
                                            }
                                        }
                                        //</editor-fold>
                                    }
                                }
                                if(line.startsWith("SaltFission.")){
                                    String fission = line.substring("SaltFission.".length());
                                    if(fission.startsWith("addRecipe")){
                                        //<editor-fold defaultstate="collapsed" desc="addRecipe">
                                        if(configuration.overhaul.fissionMSR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot add fission fuel without MSR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = fission.substring(fission.indexOf('(')+1, fission.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String inputName = args[0].substring(1, args[0].length()-1);
                                        String outputName = args[1].substring(1, args[1].length()-1);
                                        float time = Float.parseFloat(args[2]);
                                        int heat = Integer.parseInt(args[3]);
                                        float efficiency = Float.parseFloat(args[4]);
                                        int criticality = Integer.parseInt(args[5]);
                                        //decay factor ???
                                        boolean selfPriming = Boolean.parseBoolean(args[7]);
                                        for(multiblock.configuration.overhaul.fissionmsr.Block block : Core.configuration.overhaul.fissionMSR.blocks){
                                            if(block.recipes.isEmpty())continue;
                                            if(block.fuelVessel){
                                                multiblock.configuration.overhaul.fissionmsr.Block fake = null;
                                                for(multiblock.configuration.overhaul.fissionmsr.Block possible : configuration.overhaul.fissionMSR.allBlocks){
                                                    if(possible.name.equals(block.name))fake = possible;
                                                }
                                                if(fake==null){
                                                    fake = new multiblock.configuration.overhaul.fissionmsr.Block(block.name);
                                                    fake.fuelVessel = block.fuelVessel;
                                                    fake.moderator = block.moderator;
                                                    fake.shield = block.shield;
                                                    fake.heater = block.heater;
                                                    fake.reflector = block.reflector;
                                                    fake.irradiator = block.irradiator;
                                                    configuration.overhaul.fissionMSR.allBlocks.add(fake);
                                                }
                                                multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputName, outputName);
                                                recipe.inputRate = recipe.outputRate = 1;
                                                recipe.fuelVesselTime = (int)time;
                                                recipe.fuelVesselHeat = heat;
                                                recipe.fuelVesselEfficiency = efficiency;
                                                recipe.fuelVesselCriticality = criticality;
                                                recipe.fuelVesselSelfPriming = selfPriming;
                                                fake.recipes.add(recipe);
                                                block.allRecipes.add(recipe);
                                                fissionMSRRecipes.add(recipe);
                                            }
                                        }
                                        //</editor-fold>
                                    }
                                }
                                if(line.startsWith("FissionIrradiator.")){
                                    String fission = line.substring("FissionIrradiator.".length());
                                    if(fission.startsWith("addRecipe")){
                                        //<editor-fold defaultstate="collapsed" desc="addRecipe">
                                        if(configuration.overhaul.fissionSFR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot add fission irradiator recipe without SFR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        if(configuration.overhaul.fissionMSR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot add fission irradiator recipe without MSR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = fission.substring(fission.indexOf('(')+1, fission.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String inputName = args[0].substring(1, args[0].length()-1);
                                        String outputName = args[1].substring(1, args[1].length()-1);
                                        float heatPerFlux = Float.parseFloat(args[3]);
                                        float efficiency = Float.parseFloat(args[4]);
                                        for(multiblock.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
                                            if(block.recipes.isEmpty())continue;
                                            if(block.irradiator){
                                                multiblock.configuration.overhaul.fissionsfr.Block fake = null;
                                                for(multiblock.configuration.overhaul.fissionsfr.Block possible : configuration.overhaul.fissionSFR.allBlocks){
                                                    if(possible.name.equals(block.name))fake = possible;
                                                }
                                                if(fake==null){
                                                    fake = new multiblock.configuration.overhaul.fissionsfr.Block(block.name);
                                                    fake.fuelCell = block.fuelCell;
                                                    fake.moderator = block.moderator;
                                                    fake.shield = block.shield;
                                                    fake.heatsink = block.heatsink;
                                                    fake.reflector = block.reflector;
                                                    fake.irradiator = block.irradiator;
                                                    configuration.overhaul.fissionSFR.allBlocks.add(fake);
                                                }
                                                multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionsfr.BlockRecipe(inputName, outputName);
                                                recipe.irradiatorEfficiency = efficiency;
                                                recipe.irradiatorHeat = heatPerFlux;
                                                fake.recipes.add(recipe);
                                                block.allRecipes.add(recipe);
                                                fissionSFRRecipes.add(recipe);
                                            }
                                        }
                                        for(multiblock.configuration.overhaul.fissionmsr.Block block : Core.configuration.overhaul.fissionMSR.blocks){
                                            if(block.recipes.isEmpty())continue;
                                            if(block.irradiator){
                                                multiblock.configuration.overhaul.fissionmsr.Block fake = null;
                                                for(multiblock.configuration.overhaul.fissionmsr.Block possible : configuration.overhaul.fissionMSR.allBlocks){
                                                    if(possible.name.equals(block.name))fake = possible;
                                                }
                                                if(fake==null){
                                                    fake = new multiblock.configuration.overhaul.fissionmsr.Block(block.name);
                                                    fake.fuelVessel = block.fuelVessel;
                                                    fake.moderator = block.moderator;
                                                    fake.shield = block.shield;
                                                    fake.heater = block.heater;
                                                    fake.reflector = block.reflector;
                                                    fake.irradiator = block.irradiator;
                                                    configuration.overhaul.fissionMSR.allBlocks.add(fake);
                                                }
                                                multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputName, outputName);
                                                recipe.irradiatorEfficiency = efficiency;
                                                recipe.irradiatorHeat = heatPerFlux;
                                                fake.recipes.add(recipe);
                                                block.allRecipes.add(recipe);
                                                fissionMSRRecipes.add(recipe);
                                            }
                                        }
                                        //</editor-fold>
                                    }
                                }
                                if(line.startsWith("Registration.")){
                                    String register = line.substring("Registration.".length());
                                    if(register.startsWith("registerFissionSink")){
                                        //<editor-fold defaultstate="collapsed" desc="registerFissionSink">
                                        if(configuration.overhaul.fissionSFR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register fission sink without SFR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String name = args[0].substring(1, args[0].length()-1);
                                        int cooling = Integer.parseInt(args[1]);
                                        String rule = args[2].substring(1, args[2].length()-1);
                                        multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block("nuclearcraft:solid_fission_sink_"+name);
                                        block.heatsink = true;
                                        block.heatsinkHasBaseStats = true;
                                        block.heatsinkCooling = cooling;
                                        block.functional = true;
                                        block.cluster = true;
                                        fissionSFRRules.put(block, rule);
                                        configuration.overhaul.fissionSFR.blocks.add(block);
                                        Core.configuration.overhaul.fissionSFR.allBlocks.add(block);
                                        fissionSFRBlocks.add(block);
                                        //</editor-fold>
                                    }else if(register.startsWith("registerFissionHeater")){
                                        //<editor-fold defaultstate="collapsed" desc="registerFissionHeater">
                                        if(configuration.overhaul.fissionMSR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register fission sink without MSR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String name = args[0].substring(1, args[0].length()-1);
                                        String inputName = args[1].substring(1, args[1].length()-1);
                                        int inputRate = Integer.parseInt(args[2]);
                                        String outputName = args[3].substring(1, args[3].length()-1);
                                        int outputRate = Integer.parseInt(args[4]);
                                        int cooling = Integer.parseInt(args[5]);
                                        String rule = args[6].substring(1, args[6].length()-1);
                                        multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block("nuclearcraft:salt_fission_heater_"+name);
                                        block.heater = true;
                                        block.moderator = true;
                                        block.moderatorHasBaseStats = true;
                                        block.functional = true;
                                        block.cluster = true;
                                        multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputName, outputName);
                                        recipe.inputRate = inputRate;
                                        recipe.outputRate = outputRate;
                                        recipe.heaterCooling = cooling;
                                        block.recipes.add(recipe);
                                        block.allRecipes.add(recipe);
                                        block.port = new multiblock.configuration.overhaul.fissionmsr.Block("nuclearcraft:fission_heater_port_"+name);
                                        fissionMSRRules.put(block, rule);
                                        configuration.overhaul.fissionMSR.blocks.add(block);
                                        Core.configuration.overhaul.fissionMSR.allBlocks.add(block);
                                        fissionMSRBlocks.add(block);
                                        //</editor-fold>
                                    }else if(register.startsWith("registerTurbineCoil")){
                                        //<editor-fold defaultstate="collapsed" desc="registerTurbineCoil">
                                        if(configuration.overhaul.turbine==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register turbine coil without turbine configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String name = args[0].substring(1, args[0].length()-1);
                                        float efficiency = Float.parseFloat(args[1]);
                                        String rule = args[2].substring(1, args[2].length()-1);
                                        multiblock.configuration.overhaul.turbine.Block block = new multiblock.configuration.overhaul.turbine.Block("nuclearcraft:turbine_dynamo_coil_"+name);
                                        block.coil = true;
                                        block.coilEfficiency = efficiency;
                                        turbineRules.put(block, rule);
                                        configuration.overhaul.turbine.blocks.add(block);
                                        Core.configuration.overhaul.turbine.allBlocks.add(block);
                                        turbineBlocks.add(block);
                                        //</editor-fold>
                                    }else if(register.startsWith("registerTurbineStator")){
                                        //<editor-fold defaultstate="collapsed" desc="registerTurbineStator">
                                        if(configuration.overhaul.turbine==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register turbine stator without turbine configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String name = args[0].substring(1, args[0].length()-1);
                                        float expansion = Float.parseFloat(args[1]);
                                        multiblock.configuration.overhaul.turbine.Block block = new multiblock.configuration.overhaul.turbine.Block("nuclearcraft:turbine_rotor_stator_"+name);
                                        block.blade = true;
                                        block.bladeExpansion = expansion;
                                        block.bladeStator = true;
                                        configuration.overhaul.turbine.blocks.add(block);
                                        Core.configuration.overhaul.turbine.allBlocks.add(block);
                                        turbineBlocks.add(block);
                                        //</editor-fold>
                                    }else if(register.startsWith("registerTurbineBlade")){
                                        //<editor-fold defaultstate="collapsed" desc="registerTurbineBlade">
                                        if(configuration.overhaul.turbine==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register turbine blade without turbine configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String name = args[0].substring(1, args[0].length()-1);
                                        float efficiency = Float.parseFloat(args[1]);
                                        float expansion = Float.parseFloat(args[2]);
                                        multiblock.configuration.overhaul.turbine.Block block = new multiblock.configuration.overhaul.turbine.Block("nuclearcraft:turbine_rotor_blade_"+name);
                                        block.blade = true;
                                        block.bladeEfficiency = efficiency;
                                        block.bladeExpansion = expansion;
                                        configuration.overhaul.turbine.blocks.add(block);
                                        Core.configuration.overhaul.turbine.allBlocks.add(block);
                                        turbineBlocks.add(block);
                                        //</editor-fold>
                                    }else if(register.startsWith("registerRTG")){
                                        //ignored
                                    }else if(register.startsWith("registerFissionSource")){
                                        if(configuration.overhaul.fissionSFR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register fission source without SFR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        if(configuration.overhaul.fissionMSR==null){
                                            Sys.error(ErrorLevel.severe, "Cannot register fission source without MSR configuration!", null, ErrorCategory.fileIO);
                                            continue;
                                        }
                                        String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                        for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                        String name = args[0].substring(1, args[0].length()-1);
                                        float efficiency = Float.parseFloat(args[1]);
                                        multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block("nuclearcraft:fission_source_"+name);
                                        block.casing = true;
                                        block.source = true;
                                        block.sourceEfficiency = efficiency;
                                        configuration.overhaul.fissionSFR.blocks.add(block);
                                        Core.configuration.overhaul.fissionSFR.allBlocks.add(block);
                                        fissionSFRBlocks.add(block);
                                        multiblock.configuration.overhaul.fissionmsr.Block mblock = new multiblock.configuration.overhaul.fissionmsr.Block("nuclearcraft:fission_source_"+name);
                                        mblock.casing = true;
                                        mblock.source = true;
                                        mblock.sourceEfficiency = efficiency;
                                        configuration.overhaul.fissionMSR.blocks.add(mblock);
                                        Core.configuration.overhaul.fissionMSR.allBlocks.add(mblock);
                                        fissionMSRBlocks.add(mblock);
                                    }else{
                                        Sys.error(ErrorLevel.severe, "Unknown ZS register: "+register, null, ErrorCategory.fileIO);
                                    }
                                }
    //</editor-fold>
                            }catch(Exception ex){
                                Sys.error(ErrorLevel.severe, "Failed to parse "+new File(fil).getName()+" line "+lineNum+"!", ex, ErrorCategory.fileIO);
                            }
                        }
                    }catch(IOException ex){
                        Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.fileIO);
                    }
//</editor-fold>
                }
            }
            for(multiblock.configuration.overhaul.fissionsfr.Block block : fissionSFRRules.keySet()){
                block.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(Core.configuration.overhaul.fissionSFR, fissionSFRRules.get(block)));
            }
            for(multiblock.configuration.overhaul.fissionmsr.Block block : fissionMSRRules.keySet()){
                block.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(Core.configuration.overhaul.fissionMSR, fissionMSRRules.get(block)));
            }
            for(multiblock.configuration.overhaul.turbine.Block block : turbineRules.keySet()){
                block.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(Core.configuration.overhaul.turbine, turbineRules.get(block)));
            }
            for(String fil : files){
                if(fil.endsWith("en_us.lang")){
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fil))))){
                        String line;
                        LINE:while((line = reader.readLine())!=null){
                            if(line.trim().startsWith("tile.nuclearcraft.")){
                                String lin = line.trim().substring("tile.nuclearcraft.".length());
                                String blockName = lin.split("\\=")[0];
                                blockName = "nuclearcraft:"+blockName.substring(0, blockName.length()-5);
                                String displayName = lin.split("\\=", 2)[1].replace("Turbine ", "").replace("Fission ", "");
                                for(multiblock.configuration.overhaul.fissionsfr.Block block : fissionSFRBlocks){
                                    if(block.name.equals(blockName)){
                                        block.displayName = displayName;
                                        break;
                                    }
                                    if(block.port!=null&&block.port.name.equals(blockName)){
                                        block.port.displayName = displayName;
                                        block.port.portOutputDisplayName = displayName+" (Output)";
                                    }
                                }
                                for(multiblock.configuration.overhaul.fissionmsr.Block block : fissionMSRBlocks){
                                    if(block.name.equals(blockName)){
                                        block.displayName = displayName;
                                        break;
                                    }
                                    if(block.port!=null&&block.port.name.equals(blockName)){
                                        block.port.displayName = displayName;
                                        block.port.portOutputDisplayName = displayName+" (Output)";
                                    }
                                }
                                for(multiblock.configuration.overhaul.turbine.Block block : turbineBlocks){
                                    if(block.name.equals(blockName)){
                                        block.displayName = displayName;
                                    }
                                }
                            }
                            if(line.trim().startsWith("item.")){
                                String lin = line.trim().substring("item.".length());
                                String blockName = lin.split("\\=")[0].replaceFirst("\\.", ":");
                                blockName = blockName.substring(0, blockName.length()-5);
                                String displayName = lin.split("\\=", 2)[1].replace(" Fuel Pellet", "");
                                for(multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : fissionSFRRecipes){
                                    if(recipe.inputName.equals(blockName)){
                                        recipe.inputDisplayName = displayName;
                                    }
                                    if(recipe.outputName.equals(blockName)){
                                        recipe.outputDisplayName = displayName;
                                    }
                                }
                                for(multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : fissionMSRRecipes){
                                    if(recipe.inputName.equals(blockName)){
                                        recipe.inputDisplayName = displayName;
                                    }
                                    if(recipe.outputName.equals(blockName)){
                                        recipe.outputDisplayName = displayName;
                                    }
                                }
                            }
                            if(line.trim().startsWith("fluid.")){
                                String lin = line.trim().substring("fluid.".length());
                                String fluidName = lin.split("\\=")[0];
                                String displayName = lin.split("\\=", 2)[1].replace("Molten FLiBe Salt Solution of ", "").replace(" Fuel", "");
                                for(multiblock.configuration.overhaul.fissionsfr.Block block : fissionSFRBlocks){
                                    for(multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : block.allRecipes){
                                        if(recipe.inputName.equals(fluidName)){
                                            recipe.inputDisplayName = displayName;
                                        }
                                        if(recipe.outputName.equals(fluidName)){
                                            recipe.outputDisplayName = displayName;
                                        }
                                    }
                                }
                                for(multiblock.configuration.overhaul.fissionmsr.Block block : fissionMSRBlocks){
                                    for(multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : block.allRecipes){
                                        if(recipe.inputName.equals(fluidName)){
                                            recipe.inputDisplayName = displayName;
                                        }
                                        if(recipe.outputName.equals(fluidName)){
                                            recipe.outputDisplayName = displayName;
                                        }
                                    }
                                }
                                for(multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : fissionMSRRecipes){
                                    if(recipe.inputName.equals(fluidName)){
                                        recipe.inputDisplayName = displayName;
                                    }
                                    if(recipe.outputName.equals(fluidName)){
                                        recipe.outputDisplayName = displayName;
                                    }
                                }
                            }
                        }
                    }catch(IOException ex){
                        Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.fileIO);
                    }
                }
            }
            for(String fil : files){
                if(fil.endsWith(".png")){
                    File file = new File(fil);
                    try{
                        String name = consolidateZSName(file.getName().substring(0, file.getName().length()-4));//cut of the .png
                        for(multiblock.configuration.overhaul.fissionsfr.Block b : fissionSFRBlocks){
                            if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(ImageIO.read(file));
                            if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("sink_", ""))){
                                Image portTexture = ImageIO.read(file);
                                b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                                b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                            }
                            for(multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : b.allRecipes){
                                if(recipe.inputName.equals(name)){
                                    recipe.setInputTexture(ImageIO.read(file));
                                }
                                if(recipe.outputName.equals(name)){
                                    recipe.setOutputTexture(ImageIO.read(file));
                                }
                            }
                        }
                        for(multiblock.configuration.overhaul.fissionmsr.Block b : fissionMSRBlocks){
                            if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(ImageIO.read(file));
                            if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("heater_", ""))){
                                Image portTexture = ImageIO.read(file);
                                b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                                b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                            }
                            for(multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : b.allRecipes){
                                if(recipe.inputName.equals(name)){
                                    recipe.setInputTexture(ImageIO.read(file));
                                }
                                if(recipe.outputName.equals(name)){
                                    recipe.setOutputTexture(ImageIO.read(file));
                                }
                            }
                        }
                        for(multiblock.configuration.overhaul.turbine.Block b : turbineBlocks){
                            if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(ImageIO.read(file));
                        }
                        for(multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : fissionSFRRecipes){
                            if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1)))recipe.setInputTexture(ImageIO.read(file));
                            if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1)))recipe.setOutputTexture(ImageIO.read(file));
                        }
                        for(multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : fissionMSRRecipes){
                            if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1)))recipe.setInputTexture(ImageIO.read(file));
                            if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1)))recipe.setOutputTexture(ImageIO.read(file));
                        }
                    }catch(IOException ex){
                        Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.fileIO);
                    }
                }
            }
        }
        onGUIOpened();
        return true;
    }
    private String consolidateZSName(String nam){
        nam = nam.replace("-", "_");
        if(nam.startsWith("turbine_"))nam = nam.substring("turbine_".length());
        if(nam.startsWith("dynamo_"))nam = nam.substring("dynamo_".length());
        if(nam.startsWith("rotor_"))nam = nam.substring("rotor_".length());
        if(nam.startsWith("blade_"))nam = nam.substring("blade_".length());
        if(nam.startsWith("stator_"))nam = nam.substring("stator_".length());
//        if(nam.startsWith("coil_"))nam = nam.substring("coil_".length());
        if(nam.startsWith("solid_"))nam = nam.substring("solid_".length());
        if(nam.startsWith("salt_"))nam = nam.substring("salt_".length());
        if(nam.startsWith("fission_"))nam = nam.substring("fission_".length());
//        if(nam.startsWith("sink_"))nam = nam.substring("sink_".length());
        if(nam.startsWith("heater_port"))nam = nam.substring("heater_".length());
//        if(nam.startsWith("heater_"))nam = nam.substring("heater_".length());
        return nam;
    }
    private void loadAddon(File file){
        try{
            NCPFFile ncpf = FileReader.read(file);
            if(ncpf==null)return;
            configuration.addAndConvertAddon(AddonConfiguration.convert(ncpf.configuration));
        }catch(Exception ex){
            Sys.error(ErrorLevel.severe, "Failed to load addon", ex, ErrorCategory.fileIO);
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : addonsList.components){
            if(c instanceof MenuComponentAddon){
                if(button==((MenuComponentAddon) c).remove){
                    button.enabled = false;
                    try{
                        configuration.removeAddon(((MenuComponentAddon) c).addon);
                    }catch(Exception ex){
                        Sys.error(ErrorLevel.severe, "Failed to remove addon", ex, ErrorCategory.other);
                    }
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentAddon) c).edit){
                    gui.open(new MenuConfiguration(gui, this, ((MenuComponentAddon) c).addon));
                    return;
                }
            }
            if(c instanceof MenuComponentInternalAddon){
                if(button==((MenuComponentInternalAddon) c).add){
                    button.enabled = false;
                    try{
                        Core.configuration.addAndConvertAddon((((MenuComponentInternalAddon)c).addon.get()));
                    }catch(Exception ex){
                        Sys.error(ErrorLevel.severe, "Failed to add and convert addon", ex, ErrorCategory.other);
                    }
                    refreshNeeded = true;
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
    private Image alphaOver(Image overlay, Image image){
        Image combined = new Image(image.getWidth(), image.getHeight());
        for(int x = 0; x<combined.getWidth(); x++){
            for(int y = 0; y<combined.getHeight(); y++){
                Color base = new Color(image.getRGB(x, y));
                Color over = new Color(overlay.getRGB(x, y));
                combined.setRGB(x, y, over.getAlpha()==0?base.getRGB():over.getRGB());
            }
        }
        return combined;
    }
}