package planner.menu.configuration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.file.FileReader;
import planner.file.NCPFFile;
import planner.menu.MenuTransition;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.configuration.underhaul.MenuUnderhaulConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulConfiguration;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuConfiguration extends Menu{
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration", true, true));
    private final MenuComponentMinimalistButton save = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save Configuration", true, true));
    private final MenuComponentMinimalistTextBox name = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, Core.configuration.name, true));
    private final MenuComponentMinimalistTextBox version = add(new MenuComponentMinimalistTextBox(0, 0, 0, 0, Core.configuration.version, true));
    private final MenuComponentMinimalistButton underhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Underhaul Configuration", Core.configuration.underhaul!=null, true));
    private final MenuComponentMinimalistButton overhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Overhaul Configuration", Core.configuration.overhaul!=null, true));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    public MenuConfiguration(GUI gui, Menu parent){
        super(gui, parent);
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
                            header.set("version", (byte)1);
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
    }
    @Override
    public void onGUIClosed(){
        Core.configuration.name = name.text.trim().isEmpty()?null:name.text;
        Core.configuration.version = version.text.trim().isEmpty()?null:version.text;
    }
    @Override
    public void render(int millisSinceLastTick){
        load.width = save.width = underhaul.width = overhaul.width = done.width = Display.getWidth();
        name.height = version.height = load.height = save.height = underhaul.height = overhaul.height = done.height = Display.getHeight()/16;
        name.width = version.width = Display.getWidth()*.75;
        name.x = version.x = Display.getWidth()*.25;
        save.y = load.y+load.height;
        name.y = save.y+save.height;
        version.y = name.y+name.height;
        underhaul.y = version.y+version.height;
        overhaul.y = underhaul.y+underhaul.height;
        done.y = Display.getHeight()-done.height;
        Core.applyColor(Core.theme.getTextColor());
        drawText(0, name.y, name.x, name.y+name.height, "Name");
        drawText(0, version.y, version.x, version.y+version.height, "Version");
        super.render(millisSinceLastTick);
    }
}