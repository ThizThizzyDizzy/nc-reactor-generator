package planner.menu;
import java.util.ArrayList;
import planner.Core;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public abstract class SettingsMenu extends Menu{
    public MenuComponentMinimaList sidebar;
    public ArrayList<MenuComponent> sidebarBottom = new ArrayList<>();
    public SettingsMenu(GUI gui, Menu parent){
        super(gui, parent);
        sidebar = add(new MenuComponentMinimaList(0, 0, 384, 0, 0));
        sidebar.setBackgroundColor(() -> {
            return Core.theme.getSidebarColor();
        });
        MenuComponentMinimalistButton btn = addToSidebar(new MenuComponentMinimalistButton(0, 0, 0, 48, "Done", true, true));
        btn.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SplitTransitionX.slideOut(sidebar.width/gui.helper.displayWidth()), 4));
        });
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