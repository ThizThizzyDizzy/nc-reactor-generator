package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public abstract class Layout extends Component{
    @Override
    public void draw(double deltaTime){
        arrangeComponents();
    }
    public abstract void arrangeComponents();
}