package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class LayeredLayout extends Layout{
    @Override
    public void arrangeComponents(){
        for(Component c : components){
            c.x = c.y = 0;
            c.width = width;
            c.height = height;
        }
    }
}