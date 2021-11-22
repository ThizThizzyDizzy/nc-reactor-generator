package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.MenuTransition;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.Component;
public abstract class ConfigurationMenu extends Menu{
    public SingleColumnList sidebar;
    public ArrayList<Component> sidebarBottom = new ArrayList<>();
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
        sidebar = add(new SingleColumnList(0, 0, 256, 0, 0));
        sidebar.setBackgroundColor(Core.theme::getConfigurationSidebarColor);
        for(int i = 0; i<parents.size(); i++){
            Menu menu = parents.get(i);
            Button configurationButton = addToSidebar(new Button(0, 0, 0, 48, parentNames.get(i), true, true));
            configurationButton.addAction(() -> {
                if(menu instanceof ConfigurationMenu)gui.open(menu);
                else gui.open(new MenuTransition(gui, this, menu, MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.getWidth()), 4));
            });
        }
        addToSidebar(new Label(0, 0, 0, 48, name, true));
    }
    public <V extends Component> V addToSidebar(V component){
        component.width = sidebar.width;
        return sidebar.add(component);
    }
    public <V extends Component> V addToSidebarBottom(V component){
        component.width = sidebar.width;
        sidebarBottom.add(component);
        return add(component);
    }
    @Override
    public void render2d(double deltaTime){
        float h = 0;
        for(Component comp : sidebarBottom){
            h+=comp.height;
        }
        float Y = sidebar.height = gui.getHeight()-h;
        for(int i = 0; i<sidebarBottom.size(); i++){
            Component comp = sidebarBottom.get(i);
            comp.y = Y;
            Y+=comp.height;
        }
        super.render2d(deltaTime);
    }
}