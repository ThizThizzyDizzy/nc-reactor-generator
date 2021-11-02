package net.ncplanner.plannerator.planner.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentLabel;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimaList;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentThemeButton;
import net.ncplanner.plannerator.planner.theme.Theme;
import net.ncplanner.plannerator.planner.theme.ThemeCategory;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuThemes extends Menu{
    public MenuComponentMinimalistButton done = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Done", true, true));
    public ArrayList<MenuComponentMinimaList> themesLists = new ArrayList<>();
    public MenuThemes(GUI gui, Menu parent){
        super(gui, parent);
        done.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, new MenuSettings(gui, parent.parent), MenuTransition.SplitTransitionX.slideIn(384d/gui.helper.displayWidth()), 4));
        });
    }
    @Override
    public void onGUIOpened(){
        components.clear();
        themesLists.clear();
        add(done);
        for(ThemeCategory cat : Theme.themes){
            MenuComponentMinimaList list = add(new MenuComponentMinimaList(0, done.height, 0, 0, 32));
            themesLists.add(list);
            list.add(new MenuComponentLabel(0, 0, 0, 48, cat.name, true));
            for(Theme t : cat)list.add(new MenuComponentThemeButton(t));
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        done.width = gui.helper.displayWidth();
        for(int i = 0; i<themesLists.size(); i++){
            MenuComponentMinimaList list = themesLists.get(i);
            list.width = gui.helper.displayWidth()/(double)themesLists.size();
            list.x = list.width*i;
            list.height = gui.helper.displayHeight()-list.y;
        }
        super.render(millisSinceLastTick);
    }
}