package planner.menu.configuration.overhaul.turbine;
import org.lwjgl.opengl.Display;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Coil;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuCoilsConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Coil", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    public MenuCoilsConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        add.addActionListener((e) -> {
            Coil b = new Coil("New Coil");
            Core.configuration.overhaul.turbine.coils.add(b);
            gui.open(new MenuCoilConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Coil b : Core.configuration.overhaul.turbine.coils){
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
        list.width = Display.getWidth();
        list.height = Display.getHeight()-back.height-add.height;
        for(MenuComponent component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        add.width = back.width = Display.getWidth();
        add.height = back.height = Display.getHeight()/16;
        back.y = Display.getHeight()-back.height;
        add.y = back.y-add.height;
        super.render(millisSinceLastTick);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        for(MenuComponent c : list.components){
            if(c instanceof MenuComponentCoilConfiguration){
                if(button==((MenuComponentCoilConfiguration) c).delete){
                    Core.configuration.overhaul.turbine.coils.remove(((MenuComponentCoilConfiguration) c).coil);
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