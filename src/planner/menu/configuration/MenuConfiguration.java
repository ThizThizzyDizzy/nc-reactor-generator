package planner.menu.configuration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.opengl.Display;
import planner.Core;
import multiblock.configuration.Configuration;
import planner.file.FileReader;
import planner.file.NCPFFile;
import planner.menu.MenuTransition;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.underhaul.MenuUnderhaulConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulConfiguration;
import multiblock.Multiblock;
import planner.Theme;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuConfiguration extends Menu{
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration", true, true));
    private final MenuComponentMinimalistButton save = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save Configuration", true, true));
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, Core.configuration.name, true));
    private final MenuComponentMinimalistTextBox version = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, Core.configuration.version, true));
    private final MenuComponentMinimalistTextBox underhaulVersion = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, Core.configuration.underhaulVersion, true));
    private final MenuComponentMinimalistButton underhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Underhaul Configuration", Core.configuration.underhaul!=null, true));
    private final MenuComponentMinimalistButton overhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Overhaul Configuration", Core.configuration.overhaul!=null, true));
    private final MenuComponentMinimalistOptionButton theme = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Theme", true, true, Theme.themes.indexOf(Core.theme), Theme.getThemeS()));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    private final ArrayList<MenuComponentMinimalistButton> buttons = new ArrayList<>();
    public MenuConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        for(Configuration config : Configuration.configurations){
            MenuComponentMinimalistButton b = new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration: "+config.name+" ("+config.version+")", Core.configuration!=config, true);
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
            new Thread(() -> {
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Configuration File", "ncpf", "cfg", "json"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        NCPFFile ncpf = FileReader.read(file);
                        if(ncpf==null)return;
                        ncpf.configuration.impose(Core.configuration);
                        for(Multiblock multi : Core.multiblocks){
                            multi.convertTo(Core.configuration);
                        }
                        onGUIOpened();
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
        });
        save.addActionListener((e) -> {
            new Thread(() -> {
                Core.configuration.name = name.text.trim().isEmpty()?null:name.text;
                Core.configuration.version = version.text.trim().isEmpty()?null:version.text;
                Core.configuration.underhaulVersion = underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text;
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                        if(file.exists()){
                            if(JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "File already exists!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)!=JOptionPane.OK_OPTION)return;
                            file.delete();
                        }
                        try(FileOutputStream stream = new FileOutputStream(file)){
                            Config header = Config.newConfig();
                            header.set("version", NCPFFile.SAVE_VERSION);
                            header.set("count", 0);
                            header.save(stream);
                            Core.configuration.save(stream);
                        }catch(IOException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                chooser.showSaveDialog(null);
            }).start();
        });
        underhaul.addActionListener((e) -> {
            gui.open(new MenuUnderhaulConfiguration(gui, this));
        });
        overhaul.addActionListener((e) -> {
            gui.open(new MenuOverhaulConfiguration(gui, this));
        });
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(0, -1), 4));
        });
    }
    @Override
    public void onGUIOpened(){
        underhaul.enabled = Core.configuration.underhaul!=null;
        overhaul.enabled = Core.configuration.overhaul!=null;
        name.text = Core.configuration.name==null?"":Core.configuration.name;
        version.text = Core.configuration.version==null?"":Core.configuration.version;
        underhaulVersion.text = Core.configuration.underhaulVersion==null?"":Core.configuration.underhaulVersion;
        for(int i = 0; i<buttons.size(); i++){
            MenuComponentMinimalistButton b = buttons.get(i);
            Configuration c = Configuration.configurations.get(i);
            b.enabled = Core.configuration!=c;
        }
    }
    @Override
    public void onGUIClosed(){
        Core.configuration.name = name.text.trim().isEmpty()?null:name.text;
        Core.configuration.version = version.text.trim().isEmpty()?null:version.text;
        Core.configuration.underhaulVersion = underhaulVersion.text.trim().isEmpty()?null:underhaulVersion.text;
    }
    @Override
    public void render(int millisSinceLastTick){
        for(int i = 0; i<buttons.size(); i++){
            MenuComponentMinimalistButton b = buttons.get(i);
            b.width = Display.getWidth();
            b.height = Display.getHeight()/16;
            b.y = b.height*i;
        }
        theme.width = load.width = save.width = underhaul.width = overhaul.width = done.width = Display.getWidth();
        theme.height = name.height = version.height = underhaulVersion.height = load.height = save.height = underhaul.height = overhaul.height = done.height = Display.getHeight()/16;
        name.width = version.width = underhaulVersion.width = Display.getWidth()*.75;
        name.x = version.x = underhaulVersion.x = Display.getWidth()*.25;
        load.y = load.height*Configuration.configurations.size();
        save.y = load.y+load.height;
        name.y = save.y+save.height;
        version.y = name.y+name.height;
        underhaulVersion.y = version.y+version.height;
        underhaul.y = underhaulVersion.y+underhaulVersion.height;
        overhaul.y = underhaul.y+underhaul.height;
        done.y = Display.getHeight()-done.height;
        theme.y = done.y-theme.height;
        if(Theme.themes.indexOf(Core.theme)!=theme.getIndex()){
            try{
                Core.setTheme(Theme.themes.get(theme.getIndex()));
            }catch(IndexOutOfBoundsException ex){
                gui.open(new MenuConfiguration(gui, parent));
            }
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, name.y, name.x, name.y+name.height, "Name");
        drawText(0, version.y, version.x, version.y+version.height, "Version");
        drawText(0, underhaulVersion.y, underhaulVersion.x, underhaulVersion.y+underhaulVersion.height, "Underhaul Version");
        super.render(millisSinceLastTick);
    }
}