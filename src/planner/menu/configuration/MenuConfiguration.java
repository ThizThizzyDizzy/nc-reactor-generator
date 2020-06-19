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
import planner.menu.configuration.underhaul.MenuUnderhaulConfiguration;
import planner.menu.configuration.overhaul.MenuOverhaulConfiguration;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuConfiguration extends Menu{
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Configuration", true, true));
    private final MenuComponentMinimalistButton save = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Save Configuration", true, true));
    private final MenuComponentMinimalistButton underhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Underhaul Configuration", Core.configuration.underhaul!=null, true));
    private final MenuComponentMinimalistButton overhaul = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Overhaul Configuration", Core.configuration.overhaul!=null, true));
    private final MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Done", true, true));
    public MenuConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        load.addActionListener((e) -> {
            new Thread(() -> {
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf"));
                chooser.addActionListener((event) -> {
                    if(event.getActionCommand().equals("ApproveSelection")){
                        File file = chooser.getSelectedFile();
                        NCPFFile ncpf = FileReader.read(file);
                        if(ncpf==null)return;
                        Core.configuration = ncpf.configuration;
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
        });
        save.addActionListener((e) -> {
            new Thread(() -> {
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
    public void render(int millisSinceLastTick){
        load.width = save.width = underhaul.width = overhaul.width = done.width = Display.getWidth();
        load.height = save.height = underhaul.height = overhaul.height = done.height = Display.getHeight()/16;
        save.y = load.y+load.height;
        underhaul.y = save.y+save.height;
        overhaul.y = underhaul.y+underhaul.height;
        done.y = Display.getHeight()-done.height;
        super.render(millisSinceLastTick);
    }
}