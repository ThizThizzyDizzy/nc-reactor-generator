package planner.menu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.Theme;
import planner.file.FileReader;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistOptionButton;
import planner.menu.configuration.MenuConfiguration;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.GUI;
public class MenuSettings extends Menu{
    private final MenuComponentLabel currentConfig = add(new MenuComponentLabel(0, 0, 0, 0, "Current Configuration", true));
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration", true, true).setTooltip("Load configuration from a file, replacing the current configuration\nAny existing multiblocks will be converted to the new configuration\nYou can load the following files:\nnuclearcraft.cfg in the game files\nany .ncpf configuration file"));
    private final MenuComponentMinimalistButton save = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save Configuration", true, true).setTooltip("Save the configuration to a .ncpf file"));
    private final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Modify Configuration", true, true).setTooltip("Modify the current configuration"));
    private final MenuComponentMinimalistOptionButton theme = add(new MenuComponentMinimalistOptionButton(0, 0, 0, 0, "Theme", true, true, Theme.themes.indexOf(Core.theme), Theme.getThemeS())).setTooltip("Click to cycle through available themes\nRight click to cycle back");
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true).setTooltip("Close the settings menu"));
    private final ArrayList<MenuComponentMinimalistButton> buttons = new ArrayList<>();
    public MenuSettings(GUI gui, Menu parent){
        super(gui, parent);
        for(Configuration config : Configuration.configurations){
            MenuComponentMinimalistButton b = new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration: "+config.toString(), true, true).setTooltip("Replace the current configuration with "+config.toString()+"\nAll multiblocks will be converted to the new configuration");
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
                        Configuration.impose(ncpf.configuration, Core.configuration);
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
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf"));
                chooser.setSelectedFile(new File(Core.configuration.getFullName()));
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
                            Core.configuration.save(null, Config.newConfig()).save(stream);
                        }catch(IOException ex){
                            JOptionPane.showMessageDialog(null, ex.getMessage(), ex.getClass().getName(), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                chooser.showSaveDialog(null);
            }).start();
        });
        edit.addActionListener((e) -> {
            gui.open(new MenuConfiguration(gui, this, Core.configuration));
        });
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(0, -1), 4));
        });
    }
    @Override
    public void onGUIOpened(){
        currentConfig.text = "Current Configuration: "+Core.configuration.toString();
    }
    @Override
    public void render(int millisSinceLastTick){
        for(int i = 0; i<buttons.size(); i++){
            MenuComponentMinimalistButton b = buttons.get(i);
            b.width = Core.helper.displayWidth();
            b.height = Core.helper.displayHeight()/16;
            b.y = b.height*i;
        }
        currentConfig.width = theme.width = load.width = save.width = done.width = edit.width = Core.helper.displayWidth();
        currentConfig.height = theme.height = load.height = save.height = done.height = edit.height = Core.helper.displayHeight()/16;
        currentConfig.y = load.height*(Configuration.configurations.size());
        load.y = currentConfig.y+currentConfig.height;
        save.y = load.y+load.height;
        edit.y = save.y+save.height;
        done.y = Core.helper.displayHeight()-done.height;
        theme.y = done.y-theme.height;
        if(Theme.themes.indexOf(Core.theme)!=theme.getIndex()){
            try{
                Core.setTheme(Theme.themes.get(theme.getIndex()));
            }catch(IndexOutOfBoundsException ex){
                gui.open(new MenuSettings(gui, (Menu)parent));
            }
        }
        super.render(millisSinceLastTick);
    }
    
}