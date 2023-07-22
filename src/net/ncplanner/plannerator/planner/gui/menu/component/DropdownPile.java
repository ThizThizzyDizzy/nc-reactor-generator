package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.planner.gui.Component;
public class DropdownPile extends Component{
    private final float headerSpace;
    public DropdownPile(float headerSpace){
        this.headerSpace = headerSpace;
    }
    @Override
    public void render2d(double deltaTime){
        float y = headerSpace;
        for(int i = 0; i<components.size(); i++){
            DropdownList c = (DropdownList)components.get(i);
            c.x = 0;
            c.y = y;
            c.width = width;
            y+=c.height+headerSpace;
        }
        height = y;
        super.render2d(deltaTime);
    }
    public DropdownList get(int idx){
        return (DropdownList)components.get(idx);
    }
}