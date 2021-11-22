package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.ThemeButton;
import net.ncplanner.plannerator.planner.theme.Theme;
import net.ncplanner.plannerator.planner.theme.ThemeCategory;
public class MenuThemes extends Menu{
    public Button done = add(new Button(0, 0, 0, 64, "Done", true, true));
    public ArrayList<SingleColumnList> themesLists = new ArrayList<>();
    public MenuThemes(GUI gui, Menu parent){
        super(gui, parent);
        done.addAction(() -> {
            gui.open(new MenuTransition(gui, this, new MenuSettings(gui, parent.parent), MenuTransition.SplitTransitionX.slideIn(384f/gui.getWidth()), 4));
        });
    }
    @Override
    public void onOpened(){
        components.clear();
        themesLists.clear();
        add(done);
        for(ThemeCategory cat : Theme.themes){
            SingleColumnList list = add(new SingleColumnList(0, done.height, 0, 0, 32));
            themesLists.add(list);
            list.add(new Label(0, 0, 0, 48, cat.name, true));
            for(Theme t : cat)list.add(new ThemeButton(t));
        }
    }
    @Override
    public void render2d(double deltaTime){
        done.width = gui.getWidth();
        for(int i = 0; i<themesLists.size(); i++){
            SingleColumnList list = themesLists.get(i);
            list.width = gui.getWidth()/(float)themesLists.size();
            list.x = list.width*i;
            list.height = gui.getHeight()-list.y;
        }
        super.render2d(deltaTime);
    }
}