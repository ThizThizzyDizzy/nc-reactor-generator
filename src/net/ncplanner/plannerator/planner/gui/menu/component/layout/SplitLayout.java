package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class SplitLayout extends Layout{
    public float splitPos;
    public SplitLayout(float splitPos){
        this.splitPos = splitPos;
    }
    @Override
    public void arrangeComponents(){
        float split = height*splitPos;
        for(int i = 0; i<components.size(); i++){
            Component c = components.get(i);
            if(i==0){
                c.x = c.y = 0;
                c.width = width;
                c.height = split;
            }else{
                c.x = 0;
                c.y = split;
                c.width = width;
                c.height = height-split;
            }
        }
    }
    @Override
    public <T extends Component> T add(T component){
        if(components.size()>=2)throw new RuntimeException("Split Layout can only hold two components!");
        return super.add(component);
    }
}