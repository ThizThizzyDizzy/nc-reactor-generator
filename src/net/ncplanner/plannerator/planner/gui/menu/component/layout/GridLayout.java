package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class GridLayout extends Component{
    private final float componentHeight;
    private final int columns;
    public GridLayout(float componentHeight, int columns){
        this.componentHeight = componentHeight;
        this.columns = columns;
    }
    public GridLayout addAll(Iterable<? extends Component> components){
        components.forEach(this::add);
        return this;
    }
    @Override
    public void draw(double deltaTime){
        try{
            int rows = (int)Math.ceil(components.size()/(double)columns);
            height = rows*componentHeight;
            int w = (int)Math.floor(width/(double)columns);
            int h = (int)Math.floor(height/(double)rows);
            for(int i = 0; i<components.size(); i++){
                Component component = components.get(i);
                component.width = w;
                component.height = h;
                component.x = w*(i%columns);
                component.y = h*(i/columns);
            }
        }catch(ArithmeticException ex){}
    }
}