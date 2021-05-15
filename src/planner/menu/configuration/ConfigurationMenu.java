package planner.menu.configuration;
import java.util.ArrayList;
import multiblock.configuration.Configuration;
import planner.Core;
import planner.menu.MenuTransition;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public abstract class ConfigurationMenu extends Menu{
    public MenuComponentMinimaList sidebar;
    public ArrayList<MenuComponent> sidebarBottom = new ArrayList<>();
    public ArrayList<Menu> parents = new ArrayList<>();
    public ArrayList<String> parentNames = new ArrayList<>();
    public final Configuration configuration;
    public final String name;
    public ConfigurationMenu(GUI gui, Menu parent, Configuration configuration, String name){
        super(gui, parent);
        this.configuration = configuration;
        this.name = name;
        if(parent instanceof ConfigurationMenu){
            parents.addAll(((ConfigurationMenu)parent).parents);
            parentNames.addAll(((ConfigurationMenu)parent).parentNames);
        }
        parents.add(parent);
        if(parent instanceof ConfigurationMenu)parentNames.add(((ConfigurationMenu)parent).name);
        else parentNames.add("Done");
        sidebar = add(new MenuComponentMinimaList(0, 0, 256, 0, 0));
        sidebar.setBackgroundColor(Core.theme::getConfigurationSidebarColor);
        for(int i = 0; i<parents.size(); i++){
            Menu menu = parents.get(i);
            MenuComponentMinimalistButton configurationButton = addToSidebar(new MenuComponentMinimalistButton(0, 0, 0, 48, parentNames.get(i), true, true));
            configurationButton.addActionListener((e) -> {
                if(menu instanceof ConfigurationMenu)gui.open(menu);
                else gui.open(new MenuTransition(gui, this, menu, MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.helper.displayWidth()), 4));
            });
        }
        addToSidebar(new MenuComponentLabel(0, 0, 0, 48, name, true));
    }
    public <V extends MenuComponent> V addToSidebar(V component){
        component.width = sidebar.width;
        return sidebar.add(component);
    }
    public <V extends MenuComponent> V addToSidebarBottom(V component){
        component.width = sidebar.width;
        sidebarBottom.add(component);
        return add(component);
    }
    @Override
    public void render(int millisSinceLastTick){
        double h = 0;
        for(MenuComponent comp : sidebarBottom){
            h+=comp.height;
        }
        double Y = sidebar.height = gui.helper.displayHeight()-h;
        for(int i = 0; i<sidebarBottom.size(); i++){
            MenuComponent comp = sidebarBottom.get(i);
            comp.y = Y;
            Y+=comp.height;
        }
        super.render(millisSinceLastTick);
    }
}