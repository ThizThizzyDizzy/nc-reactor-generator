package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class ListLayout extends Layout{
    public final Number componentHeight;
    public ListLayout(){
        this(null);
    }
    public ListLayout(Number componentHeight){
        this.componentHeight = componentHeight;
    }
    @Override
    public void arrangeComponents(){
        float Y = 0;
        for(Component c : components){
            if(componentHeight!=null)c.height = componentHeight.floatValue();
            c.x = 0;
            c.y = Y;
            c.width = width;
            Y+=c.height;
        }
    }
    public float getTotalHeight(){
        if(componentHeight!=null)return componentHeight.floatValue()*components.size();
        float h = 0;
        for(Component c : components){
            h+=c.height;
        }
        return h;
    }
}