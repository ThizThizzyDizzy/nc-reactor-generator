package net.ncplanner.plannerator.planner.gui.menu;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.MenuComponentModule;
import net.ncplanner.plannerator.planner.module.Module;
public class MenuModules extends Menu{
    private final SingleColumnList list = add(new SingleColumnList(0, 0, 0, 0, 50));
    private final Button done = add(new Button(0, 0, 0, 0, "Done", true, true));
    public MenuModules(GUI gui, Menu parent){
        super(gui, parent);
        done.addAction(() -> {
            gui.open(new MenuSettings(gui, parent.parent));
        });
    }
    @Override
    public void onOpened(){
        list.components.clear();
        for(Module m : Core.modules){
            list.add(new MenuComponentModule(m));
        }
    }
    @Override
    public void render2d(double deltaTime){
        list.width = gui.getWidth();
        list.height = gui.getHeight()-done.height;
        for(Component component : list.components){
            component.width = list.width-(list.hasVertScrollbar()?list.vertScrollbarWidth:0);
        }
        done.width = gui.getWidth();
        done.height = gui.getHeight()/16;
        done.y = gui.getHeight()-done.height;
        super.render2d(deltaTime);
    }
}