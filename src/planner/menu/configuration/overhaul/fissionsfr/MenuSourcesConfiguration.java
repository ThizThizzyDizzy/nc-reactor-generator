package planner.menu.configuration.overhaul.fissionsfr;
import multiblock.configuration.Configuration;
import planner.Core;
import multiblock.configuration.overhaul.fissionsfr.Source;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import planner.menu.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuSourcesConfiguration extends Menu{
    private final MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, 0, 0, 0, 50));
    private final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Add Source", true, true));
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private boolean refreshNeeded = false;
    private final Configuration configuration;
    public MenuSourcesConfiguration(GUI gui, Menu parent, Configuration configuration){
        super(gui, parent);
        add.addActionListener((e) -> {
            Source b = new Source("New Source", 0);
            configuration.overhaul.fissionSFR.sources.add(b);
            Core.configuration.overhaul.fissionSFR.allSources.add(b);
            gui.open(new MenuSourceConfiguration(gui, this, b));
        });
        back.addActionListener((e) -> {
            gui.open(parent);
        });
        this.configuration = configuration;
    }
    @Override
    public void onGUIOpened(){
        list.components.clear();
        for(Source b : configuration.overhaul.fissionSFR.sources){
            list.add(new MenuComponentSourceConfiguration(b));
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
            if(c instanceof MenuComponentSourceConfiguration){
                if(button==((MenuComponentSourceConfiguration) c).delete){
                    configuration.overhaul.fissionSFR.sources.remove(((MenuComponentSourceConfiguration) c).source);
                    Core.configuration.overhaul.fissionSFR.allSources.remove(((MenuComponentSourceConfiguration) c).source);
                    refreshNeeded = true;
                    return;
                }
                if(button==((MenuComponentSourceConfiguration) c).edit){
                    gui.open(new MenuSourceConfiguration(gui, this, ((MenuComponentSourceConfiguration) c).source));
                    return;
                }
            }
        }
        super.buttonClicked(button);
    }
}