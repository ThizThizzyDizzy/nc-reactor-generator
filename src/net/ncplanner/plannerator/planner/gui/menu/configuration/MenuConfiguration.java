package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.configuration.AddonConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.OverhaulConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.UnderhaulConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.ProgressBar;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextBox;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.MenuOverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.MenuOverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.MenuOverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.MenuOverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.configuration.underhaul.MenuUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
public class MenuConfiguration extends ConfigurationMenu{
    private final TextBox name;
    private final Label underhaulTitle, overhaulTitle;
    private final TextBox overhaulVersion;
    private final TextBox underhaulVersion;
    private final Button deleteUnderhaul;
    private final Button deleteOverhaul;
    private final Button underhaulSFR, overhaulSFR, overhaulMSR, overhaulTurbine, overhaulFusion;
    private final Button deleteUnderhaulSFR, deleteOverhaulSFR, deleteOverhaulMSR, deleteOverhaulTurbine, deleteOverhaulFusion;
    private final Button configGuidelines;
    private final Button saveAddon;
    private final Label addonsLabel;
    private final SingleColumnList addonsList;
    private final Button importAddon;
    private final Button createAddon;
    private final ProgressBar scriptImportProgress;
    private boolean refreshNeeded = false;
    private boolean threadShouldStop = false;
    private boolean threadHasStopped = true;
    private Task importTask = null;
    private final Button validate;
    public MenuConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent, configuration, configuration.addon?configuration.name:"Configuration");
        name = add(new TextBox(sidebar.width, 0, 0, 64, configuration.name, true, "Name")).setTooltip(configuration.addon?"The name of the addon\nThis should not change between versions":"The name of the modpack\nThis should not change between versions");
        underhaulTitle = add(new Label(sidebar.width, name.y+name.height, 0, 48, "Underhaul"));
        overhaulTitle = add(new Label(sidebar.width, name.y+name.height, 0, 48, "Overhaul"));
        validate = addToSidebarBottom(new Button(0, 0, 0, 48, "Validate "+(configuration.addon?"Addon":"Configuration"), true, true));
        configGuidelines = addToSidebarBottom(new Button(0, 0, 0, 48, "Configuration Guidelines (Google doc)", true, true).setTooltip("Opens a webpage in your default browser containing configuration guidelines\nThese guidelines should be followed to ensure no conflicts arise with the default configurations"));
        configGuidelines.textInset = 0;
        overhaulVersion = add(new TextBox(overhaulTitle.x, overhaulTitle.y+overhaulTitle.height, 0, 56, configuration.overhaulVersion, true, "Version")).setTooltip(configuration.addon?"The version string for the Overhaul version of this addon":"The modpack version");
        underhaulVersion = add(new TextBox(underhaulTitle.x, underhaulTitle.y+underhaulTitle.height, 0, 56, configuration.underhaulVersion, true, "Version")).setTooltip(configuration.addon?"The version string for the Underhaul version of this addon":"The modpack version");
        deleteUnderhaul = add(new Button(underhaulTitle.x, underhaulVersion.y+underhaulVersion.height, 0, 48, "Delete (Shift)", false, true).setTooltip("Delete the underhaul configuration\n(Press Shift)"));
        deleteOverhaul = add(new Button(overhaulTitle.x, overhaulVersion.y+overhaulVersion.height, 0, 48, "Delete (Shift)", false, true).setTooltip("Delete the overhaul configuration\n(Press Shift)"));
        underhaulSFR = add(new Button(deleteUnderhaul.x, deleteUnderhaul.y+deleteUnderhaul.height, 0, Core.hasUnderhaulSFR()?48:0, "Solid Fission Configuration", configuration.underhaul!=null&&configuration.underhaul.fissionSFR!=null, true).setTooltip("Modify the Underhaul SFR configuration"));
        overhaulSFR = add(new Button(deleteOverhaul.x, deleteOverhaul.y+deleteOverhaul.height, 0, Core.hasOverhaulSFR()?48:0, "Solid Fission Configuration", configuration.overhaul!=null&&configuration.overhaul.fissionSFR!=null, true).setTooltip("Modify the Overhaul SFR configuration"));
        overhaulMSR = add(new Button(deleteOverhaul.x, overhaulSFR.y+overhaulSFR.height, 0, Core.hasOverhaulMSR()?48:0, "Salt Fission Configuration", configuration.overhaul!=null&&configuration.overhaul.fissionMSR!=null, true).setTooltip("Modify the Overhaul MSR configuration"));
        overhaulTurbine = add(new Button(deleteOverhaul.x, overhaulMSR.y+overhaulMSR.height, 0, Core.hasOverhaulTurbine()?48:0, "Turbine Configuration", configuration.overhaul!=null&&configuration.overhaul.turbine!=null, true).setTooltip("Modify the Overhaul Turbine configuration"));
        overhaulFusion = add(new Button(deleteOverhaul.x, overhaulTurbine.y+overhaulTurbine.height, 0, Core.hasOverhaulFusion()?48:0, "Fusion Configuration", configuration.overhaul!=null&&configuration.overhaul.fusion!=null, true).setTooltip("Modify the Overhaul Fusion configuration"));
        deleteUnderhaulSFR = add(new Button(deleteUnderhaul.x, deleteUnderhaul.y+deleteUnderhaul.height, 0, Core.hasUnderhaulSFR()?48:0, "Del", false, true).setTooltip("Delete the Underhaul SFR configuration\n(Press Shift)"));
        deleteOverhaulSFR = add(new Button(deleteOverhaul.x, deleteOverhaul.y+deleteOverhaul.height, 0, Core.hasOverhaulSFR()?48:0, "Del", false, true).setTooltip("Delete the Overhaul SFR configuration\n(Press Shift)"));
        deleteOverhaulMSR = add(new Button(deleteOverhaul.x, overhaulSFR.y+overhaulSFR.height, 0, Core.hasOverhaulMSR()?48:0, "Del", false, true).setTooltip("Delete the Overhaul MSR configuration\n(Press Shift)"));
        deleteOverhaulTurbine = add(new Button(deleteOverhaul.x, overhaulMSR.y+overhaulMSR.height, 0, Core.hasOverhaulTurbine()?48:0, "Del", false, true).setTooltip("Delete the Overhaul Turbine configuration\n(Press Shift)"));
        deleteOverhaulFusion = add(new Button(deleteOverhaul.x, overhaulTurbine.y+overhaulTurbine.height, 0, Core.hasOverhaulFusion()?48:0, "Del", false, true).setTooltip("Delete the Overhaul Fusion configuration\n(Press Shift)"));
        saveAddon = new Button(sidebar.width, Math.max(overhaulFusion.y+overhaulFusion.height,underhaulSFR.y+underhaulSFR.height), 0, 48, "Save Addon", true, true).setTooltip("Save Addon");
        addonsLabel = new Label(sidebar.width, Math.max(overhaulFusion.y+overhaulFusion.height,underhaulSFR.y+underhaulSFR.height), 0, 48, "Addons", true);
        addonsList = new SingleColumnList(sidebar.width, addonsLabel.y+addonsLabel.height, 0, 0, 16);
        importAddon = new Button(sidebar.width, 0, 0, 48, "Import Addon", true, true);
        createAddon = new Button(sidebar.width, 0, 0, 48, "Create Addon", true, true);
        scriptImportProgress = add(new ProgressBar(sidebar.width, 0, 0, 160){
            @Override
            public Task getTask(){
                return importTask;
            }
        });
        if(configuration.addon){
            add(saveAddon);
            saveAddon.addAction(() -> {
                if(configuration.addon){
                    onClosed();
                    try{
                        Core.createFileChooser(new File(configuration.name), (file) -> {
                            if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                            try(FileOutputStream stream = new FileOutputStream(file)){
                                Config header = Config.newConfig();
                                header.set("version", LegacyNCPFFile.SAVE_VERSION);
                                header.set("count", 0);
                                header.save(stream);
                                AddonConfiguration.generate(Core.configuration, configuration).save(null, Config.newConfig()).save(stream);
                            }catch(IOException ex){
                                Core.error("Failed to save addon", ex);
                            }
                        }, FileFormat.NCPF, "addon");
                    }catch(IOException ex){
                        Core.error("Failed to save addon!", ex);
                    }
                    onOpened();
                }else{
                    gui.open(new MenuConfiguration(gui, this, Core.configuration));
                }
            });
        }else{
            add(addonsLabel);
            add(addonsList);
            add(importAddon);
            add(createAddon);
            createAddon.addAction(() -> {
                Configuration c = new Configuration("New Addon", null, null);
                c.addon = true;
                Core.configuration.addons.add(c);
                gui.open(new MenuConfiguration(gui, this, c));
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
                    }, FileFormat.NCPF, "addon");
                }catch(IOException ex){
                    Core.error("Failed to import addon!", ex);
                }
            });
        }
        deleteOverhaul.addAction(() -> {
            onClosed();
            if(configuration.overhaul==null){
                configuration.overhaul = new OverhaulConfiguration();
                configuration.overhaulVersion = "0";
            }else{
                configuration.overhaul = null;
                configuration.overhaulVersion = null;
            }
            onOpened();
        });
        deleteUnderhaul.addAction(() -> {
            onClosed();
            if(configuration.underhaul==null){
                configuration.underhaul = new UnderhaulConfiguration();
                configuration.underhaulVersion = "0";
            }else{
                configuration.underhaul = null;
                configuration.underhaulVersion = null;
            }
            onOpened();
        });
        deleteUnderhaulSFR.addAction(() -> {
            if(configuration.underhaul.fissionSFR==null){
                configuration.underhaul.fissionSFR = new net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
            }else{
                configuration.underhaul.fissionSFR = null;
            }
            onOpened();
        });
        deleteOverhaulSFR.addAction(() -> {
            if(configuration.overhaul.fissionSFR==null){
                configuration.overhaul.fissionSFR = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
            }else{
                configuration.overhaul.fissionSFR = null;
            }
            onOpened();
        });
        deleteOverhaulMSR.addAction(() -> {
            if(configuration.overhaul.fissionMSR==null){
                configuration.overhaul.fissionMSR = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
            }else{
                configuration.overhaul.fissionMSR = null;
            }
            onOpened();
        });
        deleteOverhaulTurbine.addAction(() -> {
            if(configuration.overhaul.turbine==null){
                configuration.overhaul.turbine = new net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.TurbineConfiguration();
            }else{
                configuration.overhaul.turbine = null;
            }
            onOpened();
        });
        deleteOverhaulFusion.addAction(() -> {
            if(configuration.overhaul.fusion==null){
                configuration.overhaul.fusion = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.FusionConfiguration();
            }else{
                configuration.overhaul.fusion = null;
            }
            onOpened();
        });
        configGuidelines.addAction(() -> {
            Core.openURL("https://docs.google.com/document/d/1dzU2arDrD7n9doRua8laxzRy9_RtX-cuv1sUJBB5aGY/edit?usp=sharing");
        });
        validate.addAction(() -> {
            if(Core.isShiftPressed()){
                new MenuMessageDialog(gui, this, "Explore which?").addButton("Cancel", true).addButton("Choose File", () -> {
                    try{
                        Core.createFileChooser((file)->{
                            try{
                                gui.open(new MenuExploreLegacyNCPF(gui, this, configuration, file));
                            }catch(FileNotFoundException ex){
                                Core.warning("Unable to load file!", ex);
                            }
                        }, FileFormat.NCPF, "explore");
                    }catch(Exception ex){
                        Core.warning("Unable to load file!", ex);
                    }
                }, true).addButton("This "+(configuration.addon?"Addon":"Configuration"), () -> {
                    ArrayList<Config> configs = new ArrayList<>();
                    if(configuration.addon){
                        onClosed();
                        Config header = Config.newConfig();
                        header.set("version", LegacyNCPFFile.SAVE_VERSION);
                        header.set("count", 0);
                        configs.add(header);
                        configs.add(AddonConfiguration.generate(Core.configuration, configuration).save(null, Config.newConfig()));
                    }else{
                        LegacyNCPFFile ncpf = new LegacyNCPFFile();
                        ncpf.configuration = configuration;
                        FileWriter.NCPF.writeToConfigs(ncpf, configs);
                    }
                    gui.open(new MenuExploreLegacyNCPF(gui, this, configuration, configs));
                }, true).open();
            }
            else gui.open(new MenuValidateConfiguration(gui, this, configuration));
        });
        underhaulSFR.addAction(() -> {
            gui.open(new MenuUnderhaulSFRConfiguration(gui, this, configuration));
        });
        overhaulSFR.addAction(() -> {
            gui.open(new MenuOverhaulSFRConfiguration(gui, this, configuration));
        });
        overhaulMSR.addAction(() -> {
            gui.open(new MenuOverhaulMSRConfiguration(gui, this, configuration));
        });
        overhaulTurbine.addAction(() -> {
            gui.open(new MenuOverhaulTurbineConfiguration(gui, this, configuration));
        });
        overhaulFusion.addAction(() -> {
            gui.open(new MenuOverhaulFusionConfiguration(gui, this, configuration));
        });
    }
    @Override
    public void onOpened(){
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
                synchronized(gui){
                    addonsList.components.clear();
                    for(Configuration c : configuration.addons){
                        addonsList.add(new MenuComponentAddon(c, () -> {
                            gui.open(new MenuConfiguration(gui, this, c));
                        }, () -> {
                            try{
                                configuration.removeAddon(c);
                            }catch(Exception ex){
                                Core.error("Failed to remove addon", ex);
                            }
                            refreshNeeded = true;
                        }));
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
                    synchronized(gui){
                        addonsList.add(new MenuComponentInternalAddon(c, got, () -> {
                            try{
                                Core.configuration.addAndConvertAddon(c.get());
                            }catch(Exception ex){
                                Core.error("Failed to add and convert addon", ex);
                            }
                            refreshNeeded = true;
                        }));
                    }
                }
                threadHasStopped = true;
            }, "Addon caching thread").start();
        }
    }
    @Override
    public void onClosed(){
        configuration.name = name.text.trim().isEmpty()?null:name.text;
        if(configuration.overhaul!=null)configuration.overhaulVersion = overhaulVersion.text.trim().isEmpty()?null:overhaulVersion.text;
        if(configuration.underhaul!=null)configuration.underhaulVersion = underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text;
    }
    @Override
    public void render2d(double deltaTime){
        if(refreshNeeded){
            onClosed();
            onOpened();
            refreshNeeded = false;
        }
        Renderer renderer = new Renderer();
        validate.text = Core.isShiftPressed()?"Explore NCPF":("Validate "+(configuration.addon?"Addon":"Configuration"));
        scriptImportProgress.width = addonsLabel.width = addonsList.width = saveAddon.width = name.width = gui.getWidth()-name.x;
        scriptImportProgress.y = gui.getHeight()-scriptImportProgress.height;
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
                    renderer.setColor(Core.theme.getConfigurationWarningTextColor());
                    String str = "Configuration does not match stored configuration "+c.toString()+"!";
                    float Y = gui.getHeight()-64;
                    float H = 56;
                    float len = renderer.getStringWidth(str, H/2)+16;
                    float scale = Math.min(1, (gui.getWidth()-sidebar.width)/len);
                    renderer.drawCenteredText(sidebar.width, Y, gui.getWidth(), Y+H/2*scale, str);
                    renderer.drawCenteredText(sidebar.width, Y+H/2*scale, gui.getWidth(), Y+H/2*scale*2, "Please review configuration guidelines");
                }
            }
        }
        importAddon.y = createAddon.y = gui.getHeight()-importAddon.height-(badThing?64:0);
        addonsList.height = importAddon.y-addonsList.y;
        renderer.setColor(Core.theme.getConfigurationDividerColor());
        renderer.fillRect(underhaulTitle.x+underhaulTitle.width, underhaulTitle.y, overhaulTitle.x, overhaulFusion.y+overhaulFusion.height);
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
        deleteOverhaul.text = (configuration.overhaul==null?"Create":"Delete")+" (Shift)";
        deleteUnderhaul.text = (configuration.underhaul==null?"Create":"Delete")+" (Shift)";
        deleteOverhaul.setTooltip((configuration.overhaul==null?"Create":"Delete")+" the overhaul configuration\n(Press Shift)");
        deleteUnderhaul.setTooltip((configuration.underhaul==null?"Create":"Delete")+" the underhaul configuration\n(Press Shift)");
        if(Core.configuration.underhaul!=null&&configuration.underhaul!=null&&configuration.underhaul.fissionSFR==null){
            deleteUnderhaulSFR.enabled = (configuration.addon&&Core.configuration.underhaul.fissionSFR==null)?false:(Core.isShiftPressed());
        }else{
            deleteUnderhaulSFR.enabled = Core.configuration.underhaul==null?false:configuration.underhaul!=null&&Core.isShiftPressed();
        }
        deleteUnderhaulSFR.text = (configuration.underhaul==null||configuration.underhaul.fissionSFR==null?"Create":"Delete")+" (Shift)";
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
        deleteOverhaulSFR.text = (configuration.overhaul==null||configuration.overhaul.fissionSFR==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulMSR.text = (configuration.overhaul==null||configuration.overhaul.fissionMSR==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulTurbine.text = (configuration.overhaul==null||configuration.overhaul.turbine==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulFusion.text = (configuration.overhaul==null||configuration.overhaul.fusion==null?"Create":"Delete")+" (Shift)";
        deleteOverhaulSFR.setTooltip((configuration.overhaul==null||configuration.overhaul.fissionSFR==null?"Create":"Delete")+" the Overhaul SFR configuration");
        deleteOverhaulMSR.setTooltip((configuration.overhaul==null||configuration.overhaul.fissionMSR==null?"Create":"Delete")+" the Overhaul MSR configuration");
        deleteOverhaulTurbine.setTooltip((configuration.overhaul==null||configuration.overhaul.turbine==null?"Create":"Delete")+" the Overhaul Turbine configuration");
        deleteOverhaulFusion.setTooltip((configuration.overhaul==null||configuration.overhaul.fusion==null?"Create":"Delete")+" the Overhaul Fusion Reactor configuration");
        super.render2d(deltaTime);
    }
    @Override
    public void onFilesDropped(String[] files){
        importTask = new Task(configuration.addon?"Importing addon files...":"Importing addons...");
        Task readTask = configuration.addon?importTask.addSubtask(new Task("Reading files...")):importTask;
        ArrayList<Task> fileTasks = new ArrayList<>();
        for(String fil : files)fileTasks.add(readTask.addSubtask(new Task(fil)));
        if(!configuration.addon){
            Thread t = new Thread(() -> {
                for(String fil : files){
                    loadAddon(new File(fil));
                    fileTasks.remove(0).finish();
                }
                readTask.finish();
            }, "Dropped File Loading Thread");
            t.setDaemon(true);
            t.start();
        }else{
            try{
                ArrayList<InputStream> zsFiles = new ArrayList<>();
                ArrayList<String> zsNames = new ArrayList<String>(){
                    @Override
                    public boolean add(String value){
                        while(value.contains("/"))value = value.substring(value.indexOf("/")+1);
                        System.out.println("Found zs file "+value);
                        return super.add(value);
                    }
                };
                ArrayList<InputStream> langFiles = new ArrayList<>();
                ArrayList<String> langNames = new ArrayList<String>(){
                    @Override
                    public boolean add(String value){
                        while(value.contains("/"))value = value.substring(value.indexOf("/")+1);
                        System.out.println("Found lang file "+value);
                        return super.add(value);
                    }
                };
                ArrayList<InputStream> pngFiles = new ArrayList<>();
                ArrayList<String> pngNames = new ArrayList<String>(){
                    @Override
                    public boolean add(String value){
                        while(value.contains("/"))value = value.substring(value.indexOf("/")+1);
                        System.out.println("Found png file "+value);
                        return super.add(value);
                    }
                };
                for(int i = 0; i<files.length; i++){
                    String fil = files[i];
                    if(fil.endsWith(".zip")){
                        Task zipTask = fileTasks.get(0).addSubtask("Reading zip file...");
                        try{
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
                            //these lists are currently unused
                            HashMap<String, JSON.JSONObject> ctBlockstates = new HashMap<>();
                            HashMap<String, JSON.JSONObject> blockstates = new HashMap<>();
                            HashMap<String, JSON.JSONObject> ctModels = new HashMap<>();
                            HashMap<String, Image> ctTextures = new HashMap<>();
                            HashMap<String, HashMap<String, String>> ctLangFiles = new HashMap<>();
                            HashMap<String, HashMap<String, String>> theLangFiles = new HashMap<>();
                            HashMap<String, Image> textures = new HashMap<>();
                            int[] progress = new int[]{0};
                            file.stream().forEach((entry) -> {
                                progress[0]++;
                                zipTask.progress = progress[0]/(double)file.size();
                                String nam = entry.getName();
                                if(!nam.startsWith(root[0]))return;
                                nam = nam.substring(root[0].length());//root removed
                                try{
                                    if(nam.matches("scripts/[\\d\\w -]+\\.zs")){
                                        scripts.add(entry);
                                        zsFiles.add(file.getInputStream(entry));
                                        zsNames.add(nam.substring("scripts/".length()));
                                    }else if(nam.matches("blockstates/[\\d\\w -]+\\.json")){
                                        blockstates.put(nam.substring("blockstates/".length(), nam.length()-5), JSON.parse(file.getInputStream(entry)));
                                    }else if(nam.matches("contenttweaker/blockstates/[\\d\\w -]+\\.json")){
                                        ctBlockstates.put(nam.substring("contenttweaker/blockstates/".length(), nam.length()-5), JSON.parse(file.getInputStream(entry)));
                                    }else if(nam.matches("contenttweaker/models/[\\d\\w /-]+\\.json")){
                                        ctModels.put(nam.substring("contenttweaker/models/".length(), nam.length()-5), JSON.parse(file.getInputStream(entry)));
                                    }else if(nam.matches("contenttweaker/textures/[\\d\\w /-]+\\.png")){
                                        pngFiles.add(file.getInputStream(entry));
                                        pngNames.add(nam.substring("contenttweaker/textures/".length()));
                                        ctTextures.put(nam.substring("contenttweaker/textures/".length(), nam.length()-4), ImageIO.read(file.getInputStream(entry)));
                                    }else if(nam.matches("lang/[\\d\\w /-]+\\.lang")){
                                        langFiles.add(file.getInputStream(entry));
                                        langNames.add(nam.substring("lang/".length()));
                                        HashMap<String, String> lang = new HashMap<>();
                                        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(entry)))){
                                            String line;
                                            while((line = reader.readLine())!=null){
                                                if(line.trim().startsWith("//"))continue;
                                                if(line.trim().isEmpty())continue;
                                                lang.put(line.split("=")[0], line.split("=", 2)[1]);
                                            }
                                        }
                                        theLangFiles.put(nam.substring("lang/".length(), nam.length()-5), lang);
                                    }else if(nam.matches("contenttweaker/lang/[\\d\\w /-]+\\.lang")){
                                        langFiles.add(file.getInputStream(entry));
                                        langNames.add(nam.substring("contenttweaker/lang/".length()));
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
                                        pngFiles.add(file.getInputStream(entry));
                                        pngNames.add(nam.substring("textures/".length()));
                                        textures.put(nam.substring("textures/".length(), nam.length()-4), ImageIO.read(file.getInputStream(entry)));
                                    }else if(nam.contains(".")&&!nam.endsWith("/"))System.err.println(nam);
                                }catch(Exception ex){
                                    Core.warning("Failed to load file "+entry.getName(), ex);
                                }
                            });
                        }catch(Exception ex){
                            Core.error("Failed to load script addon "+new File(fil).getName(), ex);
                        }
                    }
                    if(fil.endsWith(".zs")){
                        zsFiles.add(new FileInputStream(new File(fil)));
                        zsNames.add(fil);
                    }
                    if(fil.endsWith("en_us.lang")){
                        langFiles.add(new FileInputStream(new File(fil)));
                        langNames.add(fil);
                    }
                    if(fil.endsWith(".png")){
                        pngFiles.add(new FileInputStream(new File(fil)));
                        pngNames.add(fil);
                    }
                    fileTasks.remove(0).finish();
                    readTask.progress = i/(double)files.length;
                }
                readTask.finish();
                loadScriptAddonContent(importTask.addSubtask(new Task("Loading Script addon content...")), zsFiles, zsNames, langFiles, langNames, pngFiles, pngNames);
            }catch(Exception ex){
                Core.error(null, ex);
            }
        }
        importTask = null;
        onOpened();
    }
    private void loadScriptAddonContent(Task task, ArrayList<InputStream> zsFiles, ArrayList<String> zsNames, ArrayList<InputStream> langFiles, ArrayList<String> langNames, ArrayList<InputStream> pngFiles, ArrayList<String> pngNames){
        Task readZS = task.addSubtask("Parsing ZS files");
        Task readLang = task.addSubtask("Parsing lang files");
        Task setPlacementRules = task.addSubtask("Setting placement rules");
        Task gatherTextures = task.addSubtask("Gathering Textures");
        Task readPNG = task.addSubtask("Reading PNG files");
        Task gatherLegacyNames = task.addSubtask("Gathering legacy names");
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block> turbineBlocks = new ArrayList<>();
        HashMap<net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block, String> turbineRules = new HashMap<>();
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block> fissionSFRBlocks = new ArrayList<>();
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe> fissionSFRRecipes = new ArrayList<>();
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe> fissionSFRCoolantRecipes = new ArrayList<>();
        HashMap<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block, String> fissionSFRRules = new HashMap<>();
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block> fissionMSRBlocks = new ArrayList<>();
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe> fissionMSRRecipes = new ArrayList<>();
        HashMap<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block, String> fissionMSRRules = new HashMap<>();
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe> turbineRecipes = new ArrayList<>();
        HashMap<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe, String[]> fissionSFRLangKeys = new HashMap<>();
        HashMap<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe, String[]> fissionSFRSpecialTextureKeys = new HashMap<>();
        //<editor-fold defaultstate="collapsed" desc="ZS files">
        HashMap<String, Integer> fissionFuelMetas = new HashMap<>();
        for(int idx = 0; idx<zsFiles.size(); idx++){
            InputStream in = zsFiles.get(idx);
            if(configuration.overhaul==null){
                Core.error("Cannot load ZS file with no overhaul configuration!", null);
                continue;
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                String line;
                int lineNum = 0;
                while((line = reader.readLine())!=null){
                    line = line.trim();
                    if(line.startsWith("mods.nuclearcraft."))line = line.substring("mods.nuclearcraft.".length());
                    lineNum++;
                    try{
                        //<editor-fold defaultstate="collapsed" desc="parsing line">
                        if(line.startsWith("FissionHeating.")){
                            String turbine = line.substring("FissionHeating.".length());
                            if(turbine.startsWith("addRecipe")){
                                //<editor-fold defaultstate="collapsed" desc="addRecipe">
                                if(configuration.overhaul.fissionSFR==null){
                                    Core.error("Cannot add SFR coolant recipe without Overhaul SFR configuration!", null);
                                    continue;
                                }
                                String[] args = turbine.substring(turbine.indexOf('(')+1, turbine.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String input = args[0];
                                String output = args[1];
                                String inputName = input.substring(1, input.indexOf('>'));
                                String outputName = output.substring(1, output.indexOf('>'));
                                if(inputName.startsWith("liquid:"))inputName = inputName.substring("liquid:".length());
                                if(outputName.startsWith("liquid:"))outputName = outputName.substring("liquid:".length());
                                if(inputName.startsWith("fluid:"))inputName = inputName.substring("fluid:".length());
                                if(outputName.startsWith("fluid:"))outputName = outputName.substring("fluid:".length());
                                input = input.substring(input.indexOf('>')+1);
                                output = output.substring(output.indexOf('>')+1);
                                int inputCount = input.startsWith("*")?Integer.parseInt(input.substring(1)):0;
                                int outputCount = output.startsWith("*")?Integer.parseInt(output.substring(1)):0;
                                //heatPerInputMb
                                int heat = Integer.parseInt(args[2]);
                                float outputRatio = outputCount/(float)inputCount;
                                heat*=inputCount;
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(inputName, outputName, heat, outputRatio);
                                configuration.overhaul.fissionSFR.allCoolantRecipes.add(recipe);
                                configuration.overhaul.fissionSFR.allCoolantRecipes.add(recipe);
                                fissionSFRCoolantRecipes.add(recipe);
                                //</editor-fold>
                            }
                        }
                        if(line.startsWith("SolidFission.")){
                            String fission = line.substring("SolidFission.".length());
                            if(fission.startsWith("addRecipe")){
                                //<editor-fold defaultstate="collapsed" desc="addRecipe">
                                if(configuration.overhaul.fissionSFR==null){
                                    Core.error("Cannot add fission fuel without SFR configuration!", null);
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
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
                                    if(block.recipes.isEmpty())continue;
                                    if(block.fuelCell){
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block fake = null;
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block possible : configuration.overhaul.fissionSFR.allBlocks){
                                            if(possible.name.equals(block.name))fake = possible;
                                        }
                                        if(fake==null){
                                            fake = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block(block.name);
                                            fake.fuelCell = block.fuelCell;
                                            fake.moderator = block.moderator;
                                            fake.shield = block.shield;
                                            fake.heatsink = block.heatsink;
                                            fake.reflector = block.reflector;
                                            fake.irradiator = block.irradiator;
                                            configuration.overhaul.fissionSFR.allBlocks.add(fake);
                                        }
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe(inputName, outputName);
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
                                    Core.error("Cannot add fission fuel without MSR configuration!", null);
                                    continue;
                                }
                                String[] args = fission.substring(fission.indexOf('(')+1, fission.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String inputName = args[0].substring(1, args[0].length()-1).replace("liquid:", "").replace("fluid:", "");
                                String outputName = args[1].substring(1, args[1].length()-1).replace("liquid:", "").replace("fluid:", "");
                                float time = Float.parseFloat(args[2]);
                                int heat = Integer.parseInt(args[3]);
                                float efficiency = Float.parseFloat(args[4]);
                                int criticality = Integer.parseInt(args[5]);
                                //decay factor ???
                                boolean selfPriming = Boolean.parseBoolean(args[7]);
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block : Core.configuration.overhaul.fissionMSR.blocks){
                                    if(block.recipes.isEmpty())continue;
                                    if(block.fuelVessel){
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block fake = null;
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block possible : configuration.overhaul.fissionMSR.allBlocks){
                                            if(possible.name.equals(block.name))fake = possible;
                                        }
                                        if(fake==null){
                                            fake = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block(block.name);
                                            fake.fuelVessel = block.fuelVessel;
                                            fake.moderator = block.moderator;
                                            fake.shield = block.shield;
                                            fake.heater = block.heater;
                                            fake.reflector = block.reflector;
                                            fake.irradiator = block.irradiator;
                                            configuration.overhaul.fissionMSR.allBlocks.add(fake);
                                        }
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputName, outputName);
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
                                    Core.error("Cannot add fission irradiator recipe without SFR configuration!", null);
                                    continue;
                                }
                                if(configuration.overhaul.fissionMSR==null){
                                    Core.error("Cannot add fission irradiator recipe without MSR configuration!", null);
                                    continue;
                                }
                                String[] args = fission.substring(fission.indexOf('(')+1, fission.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String inputName = args[0].substring(1, args[0].length()-1);
                                String outputName = args[1].substring(1, args[1].length()-1);
                                float heatPerFlux = Float.parseFloat(args[3]);
                                float efficiency = Float.parseFloat(args[4]);
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
                                    if(block.recipes.isEmpty())continue;
                                    if(block.irradiator){
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block fake = null;
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block possible : configuration.overhaul.fissionSFR.allBlocks){
                                            if(possible.name.equals(block.name))fake = possible;
                                        }
                                        if(fake==null){
                                            fake = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block(block.name);
                                            fake.fuelCell = block.fuelCell;
                                            fake.moderator = block.moderator;
                                            fake.shield = block.shield;
                                            fake.heatsink = block.heatsink;
                                            fake.reflector = block.reflector;
                                            fake.irradiator = block.irradiator;
                                            configuration.overhaul.fissionSFR.allBlocks.add(fake);
                                        }
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe(inputName, outputName);
                                        recipe.irradiatorEfficiency = efficiency;
                                        recipe.irradiatorHeat = heatPerFlux;
                                        fake.recipes.add(recipe);
                                        block.allRecipes.add(recipe);
                                        fissionSFRRecipes.add(recipe);
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block : Core.configuration.overhaul.fissionMSR.blocks){
                                    if(block.recipes.isEmpty())continue;
                                    if(block.irradiator){
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block fake = null;
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block possible : configuration.overhaul.fissionMSR.allBlocks){
                                            if(possible.name.equals(block.name))fake = possible;
                                        }
                                        if(fake==null){
                                            fake = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block(block.name);
                                            fake.fuelVessel = block.fuelVessel;
                                            fake.moderator = block.moderator;
                                            fake.shield = block.shield;
                                            fake.heater = block.heater;
                                            fake.reflector = block.reflector;
                                            fake.irradiator = block.irradiator;
                                            configuration.overhaul.fissionMSR.allBlocks.add(fake);
                                        }
                                        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputName, outputName);
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
                        if(line.startsWith("Turbine.")){
                            String turbine = line.substring("Turbine.".length());
                            if(turbine.startsWith("addRecipe")){
                                //<editor-fold defaultstate="collapsed" desc="addRecipe">
                                if(configuration.overhaul.turbine==null){
                                    Core.error("Cannot add turbine recipe without Turbine configuration!", null);
                                    continue;
                                }
                                String[] args = turbine.substring(turbine.indexOf('(')+1, turbine.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String input = args[0];
                                String output = args[1];
                                String inputName = input.substring(1, input.indexOf('>'));
                                String outputName = output.substring(1, output.indexOf('>'));
                                if(inputName.startsWith("liquid:"))inputName = inputName.substring("liquid:".length());
                                if(outputName.startsWith("liquid:"))outputName = outputName.substring("liquid:".length());
                                if(inputName.startsWith("fluid:"))inputName = inputName.substring("fluid:".length());
                                if(outputName.startsWith("fluid:"))outputName = outputName.substring("fluid:".length());
                                input = input.substring(input.indexOf('>')+1);
                                output = output.substring(output.indexOf('>')+1);
                                int inputCount = input.startsWith("*")?Integer.parseInt(input.substring(1)):0;
                                int outputCount = output.startsWith("*")?Integer.parseInt(output.substring(1)):0;
                                //pow exp spinup
                                double power = Double.parseDouble(args[2]);
                                double expansion = Double.parseDouble(args[3]);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe(inputName, outputName, power, expansion);
                                configuration.overhaul.turbine.allRecipes.add(recipe);
                                configuration.overhaul.turbine.recipes.add(recipe);
                                turbineRecipes.add(recipe);
                                //</editor-fold>
                            }
                        }
                        if(line.startsWith("Registration.")){
                            String register = line.substring("Registration.".length());
                            if(register.startsWith("registerFissionFuel")){
                                //<editor-fold defaultstate="collapsed" desc="registerFissionFuel">
                                String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String itemID = args[0].substring(1, args[0].length()-1);
                                String fuelName = args[1].substring(1, args[1].length()-1).toLowerCase(Locale.ROOT);
                                String itemModel = args[2].substring(1, args[2].length()-1);
                                String oreDict = args[3].substring(1, args[3].length()-1);
                                int time = Integer.parseInt(args[4]);
                                int heat = Integer.parseInt(args[5]);
                                double efficiency = Double.parseDouble(args[6]);
                                int crit = Integer.parseInt(args[7]);
                                double decay = Double.parseDouble(args[8]);
                                boolean prime = Boolean.parseBoolean(args[9]);
                                double fissionRadiation = Double.parseDouble(args[10]);
                                double fuelRadiation = Double.parseDouble(args[11]);
                                double depletedRadiation = Double.parseDouble(args[12]);
                                boolean raw = Boolean.parseBoolean(args[13]);
                                boolean carbide = Boolean.parseBoolean(args[14]);
                                boolean triso = Boolean.parseBoolean(args[15]);
                                boolean oxide = Boolean.parseBoolean(args[16]);
                                boolean nitride = Boolean.parseBoolean(args[17]);
                                boolean zirconiumAlloy = Boolean.parseBoolean(args[18]);
                                Integer fluidColor = args.length>19?Integer.parseInt(args[19]):null;
                                Integer depletedFluidColor = args.length>20?Integer.parseInt(args[20]):null;
                                int meta = fissionFuelMetas.getOrDefault(itemID, -1);
                                if(raw)meta++;
                                if(carbide)meta++;
                                if(triso)meta++;
                                if(oxide)meta++;
                                int oxMeta = meta;
                                if(nitride)meta++;
                                int niMeta = meta;
                                if(zirconiumAlloy)meta++;
                                int zaMeta = meta;
                                if(triso)meta++;
                                if(oxide)meta++;
                                int oxMetaDepleted = meta;
                                if(nitride)meta++;
                                int niMetaDepleted = meta;
                                if(zirconiumAlloy)meta++;
                                int zaMetaDepleted = meta;
                                fissionFuelMetas.put(itemID, meta);
                                if(raw||carbide||triso||oxide||nitride||zirconiumAlloy){
                                    //<editor-fold defaultstate="collapsed" desc="SFR Fuels">
                                    if(configuration.overhaul.fissionSFR==null){
                                        Core.error("Cannot register fission fuel without SFR configuration!", null);
                                        continue;
                                    }
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block : Core.configuration.overhaul.fissionSFR.blocks){
                                        if(block.recipes.isEmpty())continue;
                                        if(block.fuelCell){
                                            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block fake = null;
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block possible : configuration.overhaul.fissionSFR.allBlocks){
                                                if(possible.name.equals(block.name))fake = possible;
                                            }
                                            if(fake==null){
                                                fake = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block(block.name);
                                                fake.fuelCell = block.fuelCell;
                                                fake.moderator = block.moderator;
                                                fake.shield = block.shield;
                                                fake.heatsink = block.heatsink;
                                                fake.reflector = block.reflector;
                                                fake.irradiator = block.irradiator;
                                                configuration.overhaul.fissionSFR.allBlocks.add(fake);
                                            }
                                            if(oxide){
                                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe ox = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe("nuclearcraft:"+itemID+":"+oxMeta, "nuclearcraft:"+itemID+":"+oxMetaDepleted);
                                                ox.fuelCellTime = time;
                                                ox.fuelCellHeat = heat;
                                                ox.fuelCellEfficiency = (float)efficiency;
                                                ox.fuelCellCriticality = crit;
                                                ox.fuelCellSelfPriming = prime;
                                                fake.recipes.add(ox);
                                                block.allRecipes.add(ox);
                                                fissionSFRRecipes.add(ox);
                                                fissionSFRLangKeys.put(ox, new String[]{"item.nuclearcraft."+itemID+"."+fuelName+"_ox.name", "item.nuclearcraft."+itemID+".depleted_"+fuelName+"_ox.name"});
                                                fissionSFRSpecialTextureKeys.put(ox, new String[]{fuelName+"_ox", "depleted_"+fuelName+"_ox"});
                                            }
                                            if(nitride){
                                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe ni = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe("nuclearcraft:"+itemID+":"+niMeta, "nuclearcraft:"+itemID+":"+niMetaDepleted);
                                                ni.fuelCellTime = (int)(time*1.25);
                                                ni.fuelCellHeat = (int)(heat*.8);
                                                ni.fuelCellEfficiency = (float)efficiency;
                                                ni.fuelCellCriticality = (int)(crit*1.25);
                                                ni.fuelCellSelfPriming = prime;
                                                fake.recipes.add(ni);
                                                block.allRecipes.add(ni);
                                                fissionSFRRecipes.add(ni);
                                                fissionSFRLangKeys.put(ni, new String[]{"item.nuclearcraft."+itemID+"."+fuelName+"_ni.name", "item.nuclearcraft."+itemID+".depleted_"+fuelName+"_ni.name"});
                                                fissionSFRSpecialTextureKeys.put(ni, new String[]{fuelName+"_ni", "depleted_"+fuelName+"_ni"});
                                            }
                                            if(zirconiumAlloy){
                                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe za = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe("nuclearcraft:"+itemID+":"+zaMeta, "nuclearcraft:"+itemID+":"+zaMetaDepleted);
                                                za.fuelCellTime = (int)(time*.8);
                                                za.fuelCellHeat = (int)(heat*1.25);
                                                za.fuelCellEfficiency = (float)efficiency;
                                                za.fuelCellCriticality = (int)(crit*.85);
                                                za.fuelCellSelfPriming = prime;
                                                fake.recipes.add(za);
                                                block.allRecipes.add(za);
                                                fissionSFRRecipes.add(za);
                                                fissionSFRLangKeys.put(za, new String[]{"item.nuclearcraft."+itemID+"."+fuelName+"_za.name", "item.nuclearcraft."+itemID+".depleted_"+fuelName+"_za.name"});
                                                fissionSFRSpecialTextureKeys.put(za, new String[]{fuelName+"_za", "depleted_"+fuelName+"_za"});
                                            }
                                        }
                                    }
                                    //</editor-fold>
                                }
                                if(fluidColor!=null&&depletedFluidColor!=null){
                                    //<editor-fold defaultstate="collapsed" desc="MSR Fuel">
                                    if(configuration.overhaul.fissionMSR==null){
                                        Core.error("Cannot register fission fuel without MSR configuration!", null);
                                        continue;
                                    }
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block : Core.configuration.overhaul.fissionMSR.blocks){
                                        if(block.recipes.isEmpty())continue;
                                        if(block.fuelVessel){
                                            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block fake = null;
                                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block possible : configuration.overhaul.fissionMSR.allBlocks){
                                                if(possible.name.equals(block.name))fake = possible;
                                            }
                                            if(fake==null){
                                                fake = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block(block.name);
                                                fake.fuelVessel = block.fuelVessel;
                                                fake.moderator = block.moderator;
                                                fake.shield = block.shield;
                                                fake.heater = block.heater;
                                                fake.reflector = block.reflector;
                                                fake.irradiator = block.irradiator;
                                                configuration.overhaul.fissionMSR.allBlocks.add(fake);
                                            }
                                            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe(name+"_fluoride_flibe", "depleted_"+name+"_fluoride_flibe");
                                            recipe.inputRate = recipe.outputRate = 1;
                                            recipe.fuelVesselTime = (int)(time*1.25);
                                            recipe.fuelVesselHeat = (int)(heat*.8);
                                            recipe.fuelVesselEfficiency = (float)efficiency;
                                            recipe.fuelVesselCriticality = crit;
                                            recipe.fuelVesselSelfPriming = prime;
                                            fake.recipes.add(recipe);
                                            block.allRecipes.add(recipe);
                                            fissionMSRRecipes.add(recipe);
                                        }
                                    }
                                    //</editor-fold>
                                }
                                //</editor-fold>
                            }else if(register.startsWith("registerFissionSink")){
                                //<editor-fold defaultstate="collapsed" desc="registerFissionSink">
                                if(configuration.overhaul.fissionSFR==null){
                                    Core.error("Cannot register fission sink without SFR configuration!", null);
                                    continue;
                                }
                                String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String name = args[0].substring(1, args[0].length()-1);
                                int cooling = Integer.parseInt(args[1]);
                                String rule = args[2].substring(1, args[2].length()-1);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block("nuclearcraft:solid_fission_sink_"+name);
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
                                    Core.error("Cannot register fission sink without MSR configuration!", null);
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
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block("nuclearcraft:salt_fission_heater_"+name);
                                block.heater = true;
                                block.moderator = true;
                                block.moderatorHasBaseStats = true;
                                block.functional = true;
                                block.cluster = true;
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputName, outputName);
                                recipe.inputRate = inputRate;
                                recipe.outputRate = outputRate;
                                recipe.heaterCooling = cooling;
                                block.recipes.add(recipe);
                                block.allRecipes.add(recipe);
                                block.port = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block("nuclearcraft:fission_heater_port_"+name);
                                fissionMSRRules.put(block, rule);
                                configuration.overhaul.fissionMSR.blocks.add(block);
                                Core.configuration.overhaul.fissionMSR.allBlocks.add(block);
                                fissionMSRBlocks.add(block);
                                //</editor-fold>
                            }else if(register.startsWith("registerTurbineCoil")){
                                //<editor-fold defaultstate="collapsed" desc="registerTurbineCoil">
                                if(configuration.overhaul.turbine==null){
                                    Core.error("Cannot register turbine coil without turbine configuration!", null);
                                    continue;
                                }
                                String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String name = args[0].substring(1, args[0].length()-1);
                                float efficiency = Float.parseFloat(args[1]);
                                String rule = args[2].substring(1, args[2].length()-1);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block block = new net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block("nuclearcraft:turbine_dynamo_coil_"+name);
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
                                    Core.error("Cannot register turbine stator without turbine configuration!", null);
                                    continue;
                                }
                                String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String name = args[0].substring(1, args[0].length()-1);
                                float expansion = Float.parseFloat(args[1]);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block block = new net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block("nuclearcraft:turbine_rotor_stator_"+name);
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
                                    Core.error("Cannot register turbine blade without turbine configuration!", null);
                                    continue;
                                }
                                String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String name = args[0].substring(1, args[0].length()-1);
                                float efficiency = Float.parseFloat(args[1]);
                                float expansion = Float.parseFloat(args[2]);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block block = new net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block("nuclearcraft:turbine_rotor_blade_"+name);
                                block.blade = true;
                                block.bladeEfficiency = efficiency;
                                block.bladeExpansion = expansion;
                                configuration.overhaul.turbine.blocks.add(block);
                                Core.configuration.overhaul.turbine.allBlocks.add(block);
                                turbineBlocks.add(block);
                                //</editor-fold>
                            }else if(register.startsWith("registerBattery")){//ignored
                            }else if(register.startsWith("registerRTG")){//ignored
                            }else if(register.startsWith("registerFissionSource")){
                                if(configuration.overhaul.fissionSFR==null){
                                    Core.error("Cannot register fission source without SFR configuration!", null);
                                    continue;
                                }
                                if(configuration.overhaul.fissionMSR==null){
                                    Core.error("Cannot register fission source without MSR configuration!", null);
                                    continue;
                                }
                                String[] args = register.substring(register.indexOf('(')+1, register.indexOf(')')).split(",");
                                for(int i = 0; i<args.length; i++)args[i] = args[i].trim();
                                String name = args[0].substring(1, args[0].length()-1);
                                float efficiency = Float.parseFloat(args[1]);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block("nuclearcraft:fission_source_"+name);
                                block.casing = true;
                                block.source = true;
                                block.sourceEfficiency = efficiency;
                                configuration.overhaul.fissionSFR.blocks.add(block);
                                Core.configuration.overhaul.fissionSFR.allBlocks.add(block);
                                fissionSFRBlocks.add(block);
                                net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block mblock = new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block("nuclearcraft:fission_source_"+name);
                                mblock.casing = true;
                                mblock.source = true;
                                mblock.sourceEfficiency = efficiency;
                                configuration.overhaul.fissionMSR.blocks.add(mblock);
                                Core.configuration.overhaul.fissionMSR.allBlocks.add(mblock);
                                fissionMSRBlocks.add(mblock);
                            }else{
                                Core.error("Unknown ZS register: "+register, null);
                            }
                        }
//</editor-fold>
                    }catch(Exception ex){
                        Core.error("Failed to parse "+zsNames.get(idx)+" line "+lineNum+"!", ex);
                    }
                }
            }catch(IOException ex){
                Core.error(null, ex);
            }
        }
        readZS.finish();
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Lang files">
        for(int idx = 0; idx<langFiles.size(); idx++){
            InputStream in = langFiles.get(idx);
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                String line;
                LINE:while((line = reader.readLine())!=null){
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe rec : fissionSFRLangKeys.keySet()){
                        String[] langKeys = fissionSFRLangKeys.get(rec);
                        if(line.trim().startsWith(langKeys[0]+"="))rec.inputDisplayName = line.trim().substring((langKeys[0]+"=").length()).replace(" Fuel Pellet", "");
                        if(line.trim().startsWith(langKeys[1]+"="))rec.outputDisplayName = line.trim().substring((langKeys[1]+"=").length()).replace(" Fuel Pellet", "");
                    }
                    if(line.trim().startsWith("tile.nuclearcraft.")){
                        String lin = line.trim().substring("tile.nuclearcraft.".length());
                        String blockName = lin.split("\\=")[0];
                        blockName = "nuclearcraft:"+blockName.substring(0, blockName.length()-5);
                        String displayName = lin.split("\\=", 2)[1].replace("Turbine ", "").replace("Fission ", "");
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block : fissionSFRBlocks){
                            if(block.name.equals(blockName)){
                                block.displayName = displayName;
                                break;
                            }
                            if(block.port!=null&&block.port.name.equals(blockName)){
                                block.port.displayName = displayName;
                                block.port.portOutputDisplayName = displayName+" (Output)";
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block : fissionMSRBlocks){
                            if(block.name.equals(blockName)){
                                block.displayName = displayName;
                                break;
                            }
                            if(block.port!=null&&block.port.name.equals(blockName)){
                                block.port.displayName = displayName;
                                block.port.portOutputDisplayName = displayName+" (Output)";
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block block : turbineBlocks){
                            if(block.name.equals(blockName))block.displayName = displayName;
                        }
                    }
                    if(line.trim().startsWith("item.")){
                        String lin = line.trim().substring("item.".length());
                        String blockName = lin.split("\\=")[0].replaceFirst("\\.", ":");
                        blockName = blockName.substring(0, blockName.length()-5);
                        String displayName = lin.split("\\=", 2)[1].replace(" Fuel Pellet", "");
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : fissionSFRRecipes){
                            if(recipe.inputName.equals(blockName))recipe.inputDisplayName = displayName;
                            if(recipe.outputName.equals(blockName))recipe.outputDisplayName = displayName;
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : fissionMSRRecipes){
                            if(recipe.inputName.equals(blockName))recipe.inputDisplayName = displayName;
                            if(recipe.outputName.equals(blockName))recipe.outputDisplayName = displayName;
                        }
                    }
                    if(line.trim().startsWith("fluid.")){
                        String lin = line.trim().substring("fluid.".length());
                        String fluidName = lin.split("\\=")[0];
                        String displayName = lin.split("\\=", 2)[1].replace("Molten FLiBe Salt Solution of ", "").replace(" Fuel", "");
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block : fissionSFRBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : block.allRecipes){
                                if(recipe.inputName.equals(fluidName))recipe.inputDisplayName = displayName;
                                if(recipe.outputName.equals(fluidName))recipe.outputDisplayName = displayName;
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block : fissionMSRBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : block.allRecipes){
                                if(recipe.inputName.equals(fluidName))recipe.inputDisplayName = displayName;
                                if(recipe.outputName.equals(fluidName))recipe.outputDisplayName = displayName;
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : fissionMSRRecipes){
                            if(recipe.inputName.equals(fluidName))recipe.inputDisplayName = displayName;
                            if(recipe.outputName.equals(fluidName))recipe.outputDisplayName = displayName;
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recipe : fissionSFRCoolantRecipes){
                            if(recipe.inputName.equals(fluidName))recipe.inputDisplayName = displayName;
                            if(recipe.outputName.equals(fluidName))recipe.outputDisplayName = displayName;
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe recipe : turbineRecipes){
                            if(recipe.inputName.equals(fluidName))recipe.inputDisplayName = displayName;
                            if(recipe.outputName.equals(fluidName))recipe.outputDisplayName = displayName;
                        }
                    }
                }
            }catch(IOException ex){
                Core.error("Could not read lang file "+langNames.get(idx)+"!", ex);
            }
        }
        readLang.finish();
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Set placement rules">
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block : fissionSFRRules.keySet()){
            block.rules.add(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(Core.configuration.overhaul.fissionSFR, fissionSFRRules.get(block)));
        }
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block : fissionMSRRules.keySet()){
            block.rules.add(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(Core.configuration.overhaul.fissionMSR, fissionMSRRules.get(block)));
        }
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block block : turbineRules.keySet()){
            block.rules.add(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(Core.configuration.overhaul.turbine, turbineRules.get(block)));
        }
        setPlacementRules.finish();
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Gather textures">
        for(Supplier<AddonConfiguration> supplier : Configuration.internalAddonCache.keySet()){
            AddonConfiguration addon = Configuration.internalAddonCache.get(supplier);
            gatherTextures.name = "Gathering textures from "+addon.toString();
            if(addon.self.overhaul!=null&&configuration.overhaul!=null){
                if(addon.self.overhaul.fissionSFR!=null&&configuration.overhaul.fissionSFR!=null){
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : addon.self.overhaul.fissionSFR.blocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : configuration.overhaul.fissionSFR.blocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);//probably should copy instead of using the same object, but this shouldn't break anything, right?
                                if(b.shieldClosedTexture!=null)b2.setPortOutputTexture(b.shieldClosedTexture);
                                if(b.coolantVentOutputTexture!=null)b2.setPortOutputTexture(b.coolantVentOutputTexture);
                                if(b.port!=null&&b2.port!=null){
                                    if(b.port.name.equals(b2.port.name)){
                                        if(b.port.texture!=null)b2.port.setTexture(b.port.texture);
                                        if(b.port.portOutputTexture!=null)b2.port.setPortOutputTexture(b.port.portOutputTexture);
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r : b.recipes){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                        if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                                        if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                                    }
                                }
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : addon.self.overhaul.fissionSFR.allBlocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : configuration.overhaul.fissionSFR.allBlocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                                if(b.shieldClosedTexture!=null)b2.setPortOutputTexture(b.shieldClosedTexture);
                                if(b.coolantVentOutputTexture!=null)b2.setPortOutputTexture(b.coolantVentOutputTexture);
                                if(b.port!=null&&b2.port!=null){
                                    if(b.port.name.equals(b2.port.name)){
                                        if(b.port.texture!=null)b2.port.setTexture(b.port.texture);
                                        if(b.port.portOutputTexture!=null)b2.port.setPortOutputTexture(b.port.portOutputTexture);
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r : b.recipes){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                        if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                                        if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                                    }
                                }
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c : addon.self.overhaul.fissionSFR.coolantRecipes){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c2 : configuration.overhaul.fissionSFR.coolantRecipes){
                            if(c.inputName.equals(c2.inputName))if(c.inputTexture!=null)c2.setInputTexture(c.inputTexture);
                            if(c.outputName.equals(c2.outputName))if(c.outputTexture!=null)c2.setOutputTexture(c.outputTexture);
                        }
                    }
                }
                if(addon.self.overhaul.fissionMSR!=null&&configuration.overhaul.fissionMSR!=null){
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : addon.self.overhaul.fissionMSR.blocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : configuration.overhaul.fissionMSR.blocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                                if(b.shieldClosedTexture!=null)b2.setPortOutputTexture(b.shieldClosedTexture);
                                if(b.port!=null&&b2.port!=null){
                                    if(b.port.name.equals(b2.port.name)){
                                        if(b.port.texture!=null)b2.port.setTexture(b.port.texture);
                                        if(b.port.portOutputTexture!=null)b2.port.setPortOutputTexture(b.port.portOutputTexture);
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r : b.recipes){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                        if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                                        if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                                    }
                                }
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : addon.self.overhaul.fissionMSR.allBlocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : configuration.overhaul.fissionMSR.allBlocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                                if(b.shieldClosedTexture!=null)b2.setPortOutputTexture(b.shieldClosedTexture);
                                if(b.port!=null&&b2.port!=null){
                                    if(b.port.name.equals(b2.port.name)){
                                        if(b.port.texture!=null)b2.port.setTexture(b.port.texture);
                                        if(b.port.portOutputTexture!=null)b2.port.setPortOutputTexture(b.port.portOutputTexture);
                                    }
                                }
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r : b.recipes){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                        if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                                        if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                                    }
                                }
                            }
                        }
                    }
                }
                if(addon.self.overhaul.turbine!=null&&configuration.overhaul.turbine!=null){
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : addon.self.overhaul.turbine.blocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : configuration.overhaul.turbine.blocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : addon.self.overhaul.turbine.allBlocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : configuration.overhaul.turbine.allBlocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe r : addon.self.overhaul.turbine.recipes){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe r2 : configuration.overhaul.turbine.recipes){
                            if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                            if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                        }
                    }
                }
                if(addon.self.overhaul.fusion!=null&&configuration.overhaul.fusion!=null){
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b : addon.self.overhaul.fusion.blocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b2 : configuration.overhaul.fusion.blocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r : b.recipes){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r2 : b2.recipes){
                                        if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                                        if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                                    }
                                }
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b : addon.self.overhaul.fusion.allBlocks){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b2 : configuration.overhaul.fusion.allBlocks){
                            if(b.name.equals(b2.name)){
                                if(b.texture!=null)b2.setTexture(b.texture);
                                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r : b.recipes){
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r2 : b2.recipes){
                                        if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                                        if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                                    }
                                }
                            }
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe c : addon.self.overhaul.fusion.coolantRecipes){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe c2 : configuration.overhaul.fusion.coolantRecipes){
                            if(c.inputName.equals(c2.inputName))if(c.inputTexture!=null)c2.setInputTexture(c.inputTexture);
                            if(c.outputName.equals(c2.outputName))if(c.outputTexture!=null)c2.setOutputTexture(c.outputTexture);
                        }
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe r : addon.self.overhaul.fusion.recipes){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe r2 : configuration.overhaul.fusion.recipes){
                            if(r.inputName.equals(r2.inputName))if(r.inputTexture!=null)r2.setInputTexture(r.inputTexture);
                            if(r.outputName.equals(r2.outputName))if(r.outputTexture!=null)r2.setOutputTexture(r.outputTexture);
                        }
                    }
                }
            }
        }
        gatherTextures.finish();
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="PNG files">
        for(int idx = 0; idx<pngFiles.size(); idx++){
            String filename = pngNames.get(idx);
            try{
                Image img = ImageIO.read(pngFiles.get(idx));
                String name = consolidateZSName(filename.substring(0, filename.length()-4));//cut of the .png
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe rec : fissionSFRSpecialTextureKeys.keySet()){
                    String[] texKeys = fissionSFRSpecialTextureKeys.get(rec);
                    if(name.equals(texKeys[0]))rec.setInputTexture(img);
                    if(name.equals(texKeys[1]))rec.setOutputTexture(img);
                }
                if(configuration.overhaul.fissionSFR!=null)for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : configuration.overhaul.fissionSFR.blocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                    if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("sink_", ""))){
                        Image portTexture = img;
                        b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                        b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : b.recipes){
                        if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1))){
                            recipe.setInputTexture(img);
                        }
                        if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1))){
                            recipe.setOutputTexture(img);
                        }
                    }
                }
                if(configuration.overhaul.fissionSFR!=null)for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : configuration.overhaul.fissionSFR.allBlocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                    if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("sink_", ""))){
                        Image portTexture = img;
                        b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                        b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : b.recipes){
                        if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1))){
                            recipe.setInputTexture(img);
                        }
                        if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1))){
                            recipe.setOutputTexture(img);
                        }
                    }
                }
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : fissionSFRBlocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                    if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("sink_", ""))){
                        Image portTexture = img;
                        b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                        b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : b.recipes){
                        if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1))){
                            recipe.setInputTexture(img);
                        }
                        if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1))){
                            recipe.setOutputTexture(img);
                        }
                    }
                }
                if(configuration.overhaul.fissionMSR!=null)for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.blocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                    if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("heater_", ""))){
                        Image portTexture = img;
                        b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                        b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : b.recipes){
                        if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1))){
                            recipe.setInputTexture(img);
                        }
                        if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1))){
                            recipe.setOutputTexture(img);
                        }
                    }
                }
                if(configuration.overhaul.fissionMSR!=null)for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                    if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("heater_", ""))){
                        Image portTexture = img;
                        b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                        b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : b.recipes){
                        if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1))){
                            recipe.setInputTexture(img);
                        }
                        if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1))){
                            recipe.setOutputTexture(img);
                        }
                    }
                }
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : fissionMSRBlocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                    if(b.port!=null&&name.equals("port_"+consolidateZSName(b.name.substring(b.name.indexOf(":")+1)).replace("heater_", ""))){
                        Image portTexture = img;
                        b.port.setTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/input"), portTexture));
                        b.port.setPortOutputTexture(alphaOver(TextureManager.getImage("overhaul/msr/port/output"), portTexture));
                    }
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : b.recipes){
                        if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1))){
                            recipe.setInputTexture(img);
                        }
                        if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1))){
                            recipe.setOutputTexture(img);
                        }
                    }
                }
                if(configuration.overhaul.turbine!=null)for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : configuration.overhaul.turbine.blocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                }
                if(configuration.overhaul.turbine!=null)for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : configuration.overhaul.turbine.allBlocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                }
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : turbineBlocks){
                    if(name.equals(consolidateZSName(b.name.substring(b.name.indexOf(":")+1))))b.setTexture(img);
                }
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe : fissionSFRRecipes){
                    if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1)))recipe.setInputTexture(img);
                    if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1)))recipe.setOutputTexture(img);
                }
                for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe : fissionMSRRecipes){
                    if(name.equals(recipe.inputName.substring(recipe.inputName.indexOf(":")+1)))recipe.setInputTexture(img);
                    if(name.equals(recipe.outputName.substring(recipe.outputName.indexOf(":")+1)))recipe.setOutputTexture(img);
                }
            }catch(IOException ex){
                Core.error("Could not read image file "+filename+"!", ex);
            }
        }
        readPNG.finish();
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Gather legacy names">
        for(Supplier<AddonConfiguration> supplier : Configuration.internalAddonCache.keySet()){
            AddonConfiguration addon = Configuration.internalAddonCache.get(supplier);
            if(addon.nameMatches(configuration)){
                gatherLegacyNames.name = "Gathering legacy names from "+addon.toString();
                if(addon.self.overhaul!=null&&configuration.overhaul!=null){
                    if(addon.self.overhaul.fissionSFR!=null&&configuration.overhaul.fissionSFR!=null){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : addon.self.overhaul.fissionSFR.blocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : configuration.overhaul.fissionSFR.blocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r : b.recipes){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                            if(r.inputName.equals(r2.inputName)){
                                                r2.inputLegacyNames.addAll(r.inputLegacyNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b : addon.self.overhaul.fissionSFR.allBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block b2 : configuration.overhaul.fissionSFR.allBlocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r : b.recipes){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe r2 : b2.recipes){
                                            if(r.inputName.equals(r2.inputName)){
                                                r2.inputLegacyNames.addAll(r.inputLegacyNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c : addon.self.overhaul.fissionSFR.coolantRecipes){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe c2 : configuration.overhaul.fissionSFR.coolantRecipes){
                                if(c.inputName.equals(c2.inputName))c2.inputLegacyNames.addAll(c.inputLegacyNames);
                            }
                        }
                    }
                    if(addon.self.overhaul.fissionMSR!=null&&configuration.overhaul.fissionMSR!=null){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : addon.self.overhaul.fissionMSR.blocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : configuration.overhaul.fissionMSR.blocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r : b.recipes){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                            if(r.inputName.equals(r2.inputName)){
                                                r2.inputLegacyNames.addAll(r.inputLegacyNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b : addon.self.overhaul.fissionMSR.allBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block b2 : configuration.overhaul.fissionMSR.allBlocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r : b.recipes){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe r2 : b2.recipes){
                                            if(r.inputName.equals(r2.inputName)){
                                                r2.inputLegacyNames.addAll(r.inputLegacyNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(addon.self.overhaul.turbine!=null&&configuration.overhaul.turbine!=null){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : addon.self.overhaul.turbine.blocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : configuration.overhaul.turbine.blocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b : addon.self.overhaul.turbine.allBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block b2 : configuration.overhaul.turbine.allBlocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe r : addon.self.overhaul.turbine.recipes){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe r2 : configuration.overhaul.turbine.recipes){
                                if(r.inputName.equals(r2.inputName))r2.inputLegacyNames.addAll(r.inputLegacyNames);
                            }
                        }
                    }
                    if(addon.self.overhaul.fusion!=null&&configuration.overhaul.fusion!=null){
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b : addon.self.overhaul.fusion.blocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b2 : configuration.overhaul.fusion.blocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r : b.recipes){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r2 : b2.recipes){
                                            if(r.inputName.equals(r2.inputName)){
                                                r2.inputLegacyNames.addAll(r.inputLegacyNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b : addon.self.overhaul.fusion.allBlocks){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block b2 : configuration.overhaul.fusion.allBlocks){
                                if(b.name.equals(b2.name)){
                                    b2.legacyNames.addAll(b.legacyNames);
                                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r : b.recipes){
                                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe r2 : b2.recipes){
                                            if(r.inputName.equals(r2.inputName)){
                                                r2.inputLegacyNames.addAll(r.inputLegacyNames);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe c : addon.self.overhaul.fusion.coolantRecipes){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe c2 : configuration.overhaul.fusion.coolantRecipes){
                                if(c.inputName.equals(c2.inputName))c2.inputLegacyNames.addAll(c.inputLegacyNames);
                            }
                        }
                        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe c : addon.self.overhaul.fusion.recipes){
                            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe c2 : configuration.overhaul.fusion.recipes){
                                if(c.inputName.equals(c2.inputName))c2.inputLegacyNames.addAll(c.inputLegacyNames);
                            }
                        }
                    }
                }
                gatherLegacyNames.finish();
                break;
            }
        }
//</editor-fold>
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
            LegacyNCPFFile ncpf = FileReader.read(file);
            if(ncpf==null)return;
            configuration.addAndConvertAddon(AddonConfiguration.convert(ncpf.configuration));
        }catch(Exception ex){
            Core.error("Failed to load addon", ex);
        }
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