package planner.menu.configuration.overhaul.turbine;
import org.lwjgl.opengl.Display;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Blade;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBladesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Blade", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    public MenuBladesConfiguration(GUI gui, Menu parent){
        super(gui, parent);
        add.addActionListener((e) -> {
            Blade b = new Blade("New Blade");
            Core.configuration.overhaul.turbine.blades.add(b);
            gui.open(new MenuBladeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Blade b : Core.configuration.overhaul.turbine.blades){
            list.add(new MenuComponentBladeConfiguration(b));
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
            if(c instanceof MenuComponentBladeConfiguration){
                if(button==((MenuComponentBladeConfiguration) c).delete){
                    Core.configuration.overhaul.turbine.blades.remove(((MenuComponentBladeConfiguration) c).blade);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentBladeConfiguration) c).edit){
                    gui.open(new MenuBladeConfiguration(gui, this, ((MenuComponentBladeConfiguration) c).blade));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}