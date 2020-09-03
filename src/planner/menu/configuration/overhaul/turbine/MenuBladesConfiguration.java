package planner.menu.configuration.overhaul.turbine;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.overhaul.turbine.Blade;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuBladesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Blade", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuBladesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            Blade b = new Blade("New Blade");
            configuration.overhaul.turbine.blades.add(b);
            Core.configuration.overhaul.turbine.allBlades.add(b);
            gui.open(new MenuBladeConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Blade b : configuration.overhaul.turbine.blades){
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
            if(c instanceof MenuComponentBladeConfiguration){
                if(button==((MenuComponentBladeConfiguration) c).delete){
                    configuration.overhaul.turbine.blades.remove(((MenuComponentBladeConfiguration) c).blade);
                    Core.configuration.overhaul.turbine.allBlades.remove(((MenuComponentBladeConfiguration) c).blade);
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