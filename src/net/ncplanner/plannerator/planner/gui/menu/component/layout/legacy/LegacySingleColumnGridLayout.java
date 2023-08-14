package net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy;
import net.ncplanner.plannerator.planner.gui.Component;
public class LegacySingleColumnGridLayout extends Component{
    private final float componentHeight;
    public LegacySingleColumnGridLayout(float componentHeight){
        this.componentHeight = componentHeight;
    }
    public LegacySingleColumnGridLayout(float x, float y, float width, float componentHeight){
        super(x, y, width, 0);
        this.componentHeight = componentHeight;
    }
    public LegacySingleColumnGridLayout addAll(Iterable<? extends Component> components){
        components.forEach(this::add);
        return this;
    }
    @Override
    public void draw(double deltaTime){
        try{
            int rows = rows();
            height = rows*componentHeight;
            float Y = 0;
            for(int i = 0; i<components.size(); i++){
                Component component = components.get(i);
                component.width = width;
                component.height = (component instanceof LegacySingleColumnGridLayout)?(((LegacySingleColumnGridLayout)component).rows()*componentHeight):componentHeight;
                component.x = 0;
                component.y = Y;
                Y+=component.height;
            }
        }catch(ArithmeticException ex){}
    }
    private int rows(){
        int rows = 0;
        for(Component c : components){
            if(c instanceof LegacySingleColumnGridLayout){
                rows+=((LegacySingleColumnGridLayout)c).rows();
            }else rows++;
        }
        return rows;
    }
}