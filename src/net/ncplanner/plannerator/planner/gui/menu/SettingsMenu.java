package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
public abstract class SettingsMenu extends Menu{
    public SingleColumnList sidebar;
    public ArrayList<Component> sidebarBottom = new ArrayList<>();
    public SettingsMenu(GUI gui, Menu parent){
        super(gui, parent);
        sidebar = add(new SingleColumnList(0, 0, 384, 0, 0));
        sidebar.setBackgroundColor(() -> {
            return Core.theme.getSettingsSidebarColor();
        });
        Button btn = addToSidebar(new Button(0, 0, 0, 48, "Done", true, true));
        btn.addAction(() -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.getWidth()), 4));
        });
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