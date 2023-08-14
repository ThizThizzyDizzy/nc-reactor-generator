package net.ncplanner.plannerator.planner.gui.menu.component.layout.legacy;
import net.ncplanner.plannerator.planner.gui.Component;
public class LegacyExpandingGridLayout extends Component{
    private final int minHeight;
    private final float widthRatio;
    private final int minWidth;
    public LegacyExpandingGridLayout(int minWidth, int minHeight, float widthRatio){
        super(0, 0, minWidth, minHeight);
        this.minHeight = minHeight;
        this.widthRatio = widthRatio;
        this.minWidth = minWidth;
    }
    public LegacyExpandingGridLayout addAll(Iterable<? extends Component> components){
        components.forEach(this::add);
        return this;
    }
    @Override
    public void draw(double deltaTime){
        try{
            int columns = Math.min(components.size(),(int)Math.ceil(width/Math.sqrt(height*width/(double)components.size())));
            columns/=widthRatio;
            if(columns<=0)columns = 1;
            int rows = (int)Math.ceil(components.size()/(double)columns);
            int w = (int)Math.floor(width/(double)columns);
            int h = (int)Math.floor(height/(double)rows);
            float expansion = Math.max(minHeight/h, minWidth/w);
            if(expansion>1){
                width*=expansion;
                height*=expansion;
                w*=expansion;
                h*=expansion;
            }
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