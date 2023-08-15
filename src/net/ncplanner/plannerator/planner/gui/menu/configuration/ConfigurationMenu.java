package net.ncplanner.plannerator.planner.gui.menu.configuration;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.LayoutMenu;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuTransition;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.LayoutPanel;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.Layout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitLayout;
import net.ncplanner.plannerator.planner.gui.menu.component.layout.SplitListLayout;
public abstract class ConfigurationMenu extends LayoutMenu{
    public ArrayList<Menu> parents = new ArrayList<>();
    public ArrayList<String> parentNames = new ArrayList<>();
    public final NCPFConfigurationContainer configuration;
    public final String name;
    public final LayoutPanel sidebar;
    public final Layout content;
    public ConfigurationMenu(GUI gui, Menu parent, NCPFConfigurationContainer configuration, String name, Layout content){
        super(gui, parent, new SplitLayout(SplitLayout.X_AXIS, 0, 256, 0));
        this.configuration = configuration;
        this.name = name;
        if(parent instanceof ConfigurationMenu){
            parents.addAll(((ConfigurationMenu)parent).parents);
            parentNames.addAll(((ConfigurationMenu)parent).parentNames);
        }
        parents.add(parent);
        if(parent instanceof ConfigurationMenu)parentNames.add(((ConfigurationMenu)parent).name);
        else parentNames.add("Done");
        sidebar = super.add(new LayoutPanel(new SplitListLayout(48)).setBackgroundColor(Core.theme::getConfigurationSidebarColor));
        for(int i = 0; i<parents.size(); i++){
            Menu menu = parents.get(i);
            Button configurationButton = sidebar.add(new Button(parentNames.get(i), true, true));
            configurationButton.addAction(() -> {
                if(menu instanceof ConfigurationMenu)gui.open(menu);
                else gui.open(new MenuTransition(gui, this, menu, MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.getWidth()), 4));
            });
        }
        sidebar.add(new Label(name, true));
        this.content = super.add(content);
    }
    @Override
    public <T extends Component> T add(T component){
        return content.add(component);
    }
}