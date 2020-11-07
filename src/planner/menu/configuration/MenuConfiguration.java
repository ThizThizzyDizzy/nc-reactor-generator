package planner.menu.configuration;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import multiblock.configuration.AddonConfiguration;
import planner.Core;
import multiblock.configuration.Configuration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import planner.file.FileFormat;
import planner.Main;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.underhaul.MenuUnderhaulConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulConfiguration;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
public class MenuConfiguration extends Menu{
    private final Configuration configuration;
    private final MenuComponentMinimalistTextBox name;
    private final MenuComponentMinimalistTextBox overhaulVersion;
    private final MenuComponentMinimalistTextBox underhaulVersion;
    private final MenuComponentMinimalistButton underhaul;
    private final MenuComponentMinimalistButton overhaul;
    private final MenuComponentMinimalistButton deleteUnderhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete (Ctrl+Shift)", false, true).setTooltip("Delete the underhaul configuration"));
    private final MenuComponentMinimalistButton deleteOverhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Delete (Alt+Shift)", false, true).setTooltip("Delete the overhaul configuration"));
    private final MenuComponentMinimalistButton configGuidelines = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Configuration Guidelines (Google doc)", true, true).setTooltip("Opens a webpage in your default browser containing configuration guidelines\nThese guidelines should be followed to ensure no conflicts arise with the default configurations"));
    private final MenuComponentMinimalistButton addons = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Addons", true, true).setTooltip("Manage addons"));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Finish modifying the configuration and return to the settings screen"));
    public MenuConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        this.configuration = configuration;
        name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, configuration.name, true)).setTooltip(configuration.addon?"The name of the addon\nThis should not change between versions":"The name of the modpack\nThis should not change between versions");
        overhaulVersion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, configuration.overhaulVersion, true)).setTooltip(configuration.addon?"The version string for the Overhaul version of this addon":"The modpack version");
        underhaulVersion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, configuration.underhaulVersion, true)).setTooltip(configuration.addon?"The version string for the Underhaul version of this addon":"The modpack version");
        underhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Underhaul Configuration", configuration.underhaul!=null, true).setTooltip("Modify the Underhaul configuration"));
        overhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Overhaul Configuration", configuration.overhaul!=null, true).setTooltip("Modfiy the Overhaul configuration"));
        underhaul.addActionListener((e) -> {
            gui.open(new MenuUnderhaulConfiguration(gui, this, configuration));
        });
        overhaul.addActionListener((e) -> {
            gui.open(new MenuOverhaulConfiguration(gui, this, configuration));
        });
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
        configGuidelines.addActionListener((e) -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try{
                    Desktop.getDesktop().browse(new URI("https://docs.google.com/document/d/1dzU2arDrD7n9doRua8laxzRy9_RtX-cuv1sUJBB5aGY/edit?usp=sharing"));
                }catch(URISyntaxException|IOException ex){
                    if(Main.hasAWT){
                        javax.swing.JOptionPane.showMessageDialog(null, "https://docs.google.com/document/d/1dzU2arDrD7n9doRua8laxzRy9_RtX-cuv1sUJBB5aGY/edit?usp=sharing", "Failed to open webpage", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }else{
                        Sys.error(ErrorLevel.minor, "Failed to open webpage\nhttps://docs.google.com/document/d/1dzU2arDrD7n9doRua8laxzRy9_RtX-cuv1sUJBB5aGY/edit?usp=sharing", null, ErrorCategory.InternetIO, false);
                    }
                }
            }else{
                Sys.error(ErrorLevel.minor, "Desktop Browse is not supported\nhttps://docs.google.com/document/d/1dzU2arDrD7n9doRua8laxzRy9_RtX-cuv1sUJBB5aGY/edit?usp=sharing", null, ErrorCategory.InternetIO, false);
            }
        });
        addons.addActionListener((e) -> {
            if(configuration.addon){
                Core.createFileChooser(new File(configuration.name), (file, format) -> {
                    if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                    file = Core.askForOverwrite(file);
                    if(file==null)return;
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
            }else{
                gui.open(new MenuAddonsConfiguration(gui, this));
            }
        });
        done.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        underhaulVersion.editable = underhaul.enabled = configuration.underhaul!=null&&Core.configuration.underhaul!=null;
        overhaulVersion.editable = overhaul.enabled = configuration.overhaul!=null&&Core.configuration.overhaul!=null;
        name.text = configuration.name==null?"":configuration.name;
        overhaulVersion.text = configuration.overhaulVersion==null?"":configuration.overhaulVersion;
        underhaulVersion.text = configuration.underhaulVersion==null?"":configuration.underhaulVersion;
        addons.label = configuration.addon?"Save Addon":"Addons ("+configuration.addons.size()+")";
    }
    @Override
    public void onGUIClosed(){
        configuration.name = name.text.trim().isEmpty()?null:name.text;
        if(configuration.overhaul!=null)configuration.overhaulVersion = overhaulVersion.text.trim().isEmpty()?null:overhaulVersion.text;
        if(configuration.underhaul!=null)configuration.underhaulVersion = underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text;
    }
    @Override
    public void render(int millisSinceLastTick){
        if(configuration.overhaul==null){
            deleteOverhaul.enabled = (configuration.addon&&Core.configuration.overhaul==null)?false:(Core.isAltPressed()&&Core.isShiftPressed());
        }else{
            deleteOverhaul.enabled = Core.isAltPressed()&&Core.isShiftPressed();
        }
        if(configuration.underhaul==null){
            deleteUnderhaul.enabled = (configuration.addon&&Core.configuration.underhaul==null)?false:(Core.isControlPressed()&&Core.isShiftPressed());
        }else{
            deleteUnderhaul.enabled = Core.isControlPressed()&&Core.isShiftPressed();
        }
        deleteOverhaul.label = (configuration.overhaul==null?"Create":"Delete")+" (Alt+Shift)";
        deleteUnderhaul.label = (configuration.underhaul==null?"Create":"Delete")+" (Ctrl+Shift)";
        configGuidelines.width = underhaul.width = overhaul.width = done.width = addons.width = gui.helper.displayWidth();
        configGuidelines.height = name.height = overhaulVersion.height = underhaulVersion.height = addons.height = underhaul.height = overhaul.height = done.height = deleteUnderhaul.height = deleteOverhaul.height = gui.helper.displayHeight()/16;
        name.width = gui.helper.displayWidth()*.75;
        overhaulVersion.width = underhaulVersion.width = gui.helper.displayWidth()/3;
        name.x = overhaulVersion.x = underhaulVersion.x = gui.helper.displayWidth()*.25;
        overhaul.y = name.y+name.height;
        deleteOverhaul.y = overhaulVersion.y = overhaul.y+overhaul.height;
        underhaul.y = overhaulVersion.y+overhaulVersion.height;
        deleteUnderhaul.y = underhaulVersion.y = underhaul.y+underhaul.height;
        deleteUnderhaul.x = underhaulVersion.x+underhaulVersion.width;
        deleteOverhaul.x = overhaulVersion.x+overhaulVersion.width;
        deleteOverhaul.width = gui.helper.displayWidth()-deleteOverhaul.x;
        deleteUnderhaul.width = gui.helper.displayWidth()-deleteUnderhaul.x;
        addons.y = deleteUnderhaul.y+deleteUnderhaul.height;
        configGuidelines.y = addons.y+addons.height;
        done.y = gui.helper.displayHeight()-done.height;
        for(Configuration c : Configuration.configurations){
            if(Objects.equals(name.text.trim().isEmpty()?null:name.text,c.name)){
                if(Objects.equals(overhaulVersion.text.trim().isEmpty()?null:overhaulVersion.text, c.overhaulVersion)&&!c.isOverhaulConfigurationEqual(configuration)
                        ||Objects.equals(underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text, c.underhaulVersion)&&!c.isUnderhaulConfigurationEqual(configuration)){
                    Core.applyColor(Color.red);
                    String str = "Error: Configuration does not match stored configuration "+c.toString()+"!";
                    double len = FontManager.getLengthForStringWithHeight(str, configGuidelines.height)+3;
                    double scale = gui.helper.displayWidth()/len;
                    drawCenteredText(0, configGuidelines.y+configGuidelines.height, gui.helper.displayWidth(), configGuidelines.y+configGuidelines.height+configGuidelines.height*Math.min(1, scale), str);
                    drawCenteredText(0, configGuidelines.y+configGuidelines.height+configGuidelines.height*Math.min(1, scale), gui.helper.displayWidth(), configGuidelines.y+configGuidelines.height+configGuidelines.height*Math.min(1, scale)*2, "Please review configuration guidelines");
                }
            }
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, name.y, name.x, name.y+name.height, "Name");
        drawText(0, overhaulVersion.y, overhaulVersion.x, overhaulVersion.y+overhaulVersion.height, "Version");
        drawText(0, underhaulVersion.y, underhaulVersion.x, underhaulVersion.y+underhaulVersion.height, "Version");
        super.render(millisSinceLastTick);
    }
}