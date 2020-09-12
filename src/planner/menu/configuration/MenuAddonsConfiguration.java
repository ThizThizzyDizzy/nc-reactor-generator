package planner.menu.configuration;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.file.FileReader;
import planner.file.NCPFFile;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuAddonsConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton load = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Load Addon", true, true).setTooltip("Loads an addon from a .ncpf file"));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Create New Addon", true, true).setTooltip("Creates a new blank addon"));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    public MenuAddonsConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        add.addActionListener((e) -> {
            Configuration c = new Configuration("New Addon", null, null);
            c.addon = true;
            Core.configuration.addons.add(c);
            gui.open(new MenuConfiguration(gui, this, c));
        });
        load.addActionListener((e) -> {
            new Thread(() -> {
                JFileChooser chooser = new JFileChooser(new File("file").getAbsoluteFile().getParentFile());
                chooser.setFileFilter(new FileNameExtensionFilter("NuclearCraft Planner File", "ncpf"));
                chooser.addActionListener((event) -> {
                    try{
                        if(event.getActionCommand().equals("ApproveSelection")){
                            File file = chooser.getSelectedFile();
                            NCPFFile ncpf = FileReader.read(file);
                            if(ncpf==null)return;
                            Core.configuration.addAndConvertAddon(AddonConfiguration.convert(ncpf.configuration));
                            onGUIOpened();
                        }
                    }catch(Exception ex){
                        Sys.error(ErrorLevel.severe, "Failed to load addon", ex, ErrorCategory.fileIO);
                    }
                });
                chooser.showOpenDialog(null);
            }).start();
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Configuration c : Core.configuration.addons){
            list.add(new MenuComponentAddonConfiguration(c));
        }
    }
    @Override
    public void tick(){
        if(refreshNeeded){
            onGUIOpened();
            refreshNeeded = false;
        }
        super.tick();
    }
    @Override
    public void render(int millisSinceLastTick){
        list.width = Core.helper.displayWidth();
        list.height = Core.helper.displayHeight()-back.height-add.height;
        for(simplelibrary.opengl.gui.components.MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        load.width = add.width = back.width = Core.helper.displayWidth();
        load.height = add.height = back.height = Core.helper.displayHeight()/16;
        back.y = Core.helper.displayHeight()-back.height;
        add.y = back.y-add.height;
        load.y = add.y-load.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : list.components){
            if(c instanceof MenuComponentAddonConfiguration){
                if(button==((MenuComponentAddonConfiguration) c).delete){
                    Core.configuration.addons.remove(((MenuComponentAddonConfiguration) c).addon);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentAddonConfiguration) c).edit){
                    gui.open(new MenuConfiguration(gui, this, ((MenuComponentAddonConfiguration) c).addon));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}