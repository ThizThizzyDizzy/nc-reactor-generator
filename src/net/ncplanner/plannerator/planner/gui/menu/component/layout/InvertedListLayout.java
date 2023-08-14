package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class InvertedListLayout extends ListLayout{
    public InvertedListLayout(){
        super();
    }
    public InvertedListLayout(Number componentHeight){
        super(componentHeight);
    }
    @Override
    public void arrangeComponents(){
        float Y = height;
        for(Component c : components){
            if(componentHeight!=null)c.height = componentHeight.floatValue();
            c.x = 0;
            c.y = Y-c.height;
            c.width = width;
            Y-=c.height;
        }
    }
}