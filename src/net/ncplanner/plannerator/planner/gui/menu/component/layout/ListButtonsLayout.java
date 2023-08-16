package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class ListButtonsLayout extends Layout{
    @Override
    public void arrangeComponents(){
        for(int i = 0; i<components.size(); i++){
            Component c = components.get(i);
            c.y = height/4;
            c.width = c.height = height/2;
            c.x = width-i*height-height*3/4;
        }
    }
}