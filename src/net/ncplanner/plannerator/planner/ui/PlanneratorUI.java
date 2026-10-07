package net.ncplanner.plannerator.planner.ui;
import com.thizthizzydizzy.dizzyengine.ui.FlatUI;
import net.ncplanner.plannerator.planner.Core;
public class PlanneratorUI extends FlatUI{
    @Override
    public void render(double deltaTime){
        Core.processUITasks();
        if(Core.gui.menu==null){
            if(menu!=null)open(null);
        }else if(!(menu instanceof PlanneratorMenu)||((PlanneratorMenu)menu).content!=Core.gui.menu){
            open(new PlanneratorMenu(Core.gui.menu));
        }
        super.render(deltaTime);
    }
}
