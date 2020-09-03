package planner.menu.configuration.overhaul.turbine;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Coil;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuCoilsConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Coil", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuCoilsConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            Coil b = new Coil("New Coil");
            configuration.overhaul.turbine.coils.add(b);
            configuration.overhaul.turbine.allCoils.add(b);
            gui.open(new MenuCoilConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Coil b : configuration.overhaul.turbine.coils){
            list.add(new MenuComponentCoilConfiguration(b));
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
        add.width = back.width = Core.helper.displayWidth();
        add.height = back.height = Core.helper.displayHeight()/16;
        back.y = Core.helper.displayHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(simplelibrary.opengl.gui.components.MenuComponent c : list.components){
            if(c instanceof MenuComponentCoilConfiguration){
                if(button==((MenuComponentCoilConfiguration) c).delete){
                    configuration.overhaul.turbine.coils.remove(((MenuComponentCoilConfiguration) c).coil);
                    Core.configuration.overhaul.turbine.allCoils.remove(((MenuComponentCoilConfiguration) c).coil);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentCoilConfiguration) c).edit){
                    gui.open(new MenuCoilConfiguration(gui, this, ((MenuComponentCoilConfiguration) c).coil));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}