package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.gui.Component;
public class SplitLayout extends Layout{
    public static final int X_AXIS = 0;
    public static final int Y_AXIS = 1;
    public final int axis;
    public float splitPos;
    public float minSize1;
    public float minSize2;
    private float borderSize;
    private Supplier<Color> borderColor;
    private boolean fitSize;
    private boolean fitContent;
    public SplitLayout(int axis, float splitPos){
        this(axis, splitPos, 0, 0);
    }
    public SplitLayout(int axis, float splitPos, float minSize1, float minSize2){
        this.axis = axis;
        this.splitPos = splitPos;
        this.minSize1 = minSize1;
        this.minSize2 = minSize2;
    }
    public SplitLayout fitContent(){
        fitContent = true;
        return this;
    }
    @Override
    public void arrangeComponents(){
        if(components.size()!=2)throw new RuntimeException("Split Layout must always have two components!");
        if(fitContent){
            if(axis==X_AXIS){
                height = Math.max(components.get(0).height, components.get(1).height);
            }else{
                width = Math.max(components.get(0).width, components.get(1).width);
            }
        }
        float size = axis==X_AXIS?width:height;
        float actualSize = size;
        if(fitSize){
            actualSize = 0;
            for(Component c : components.get(0).components){
                actualSize = Math.max(actualSize, (axis==X_AXIS)?(c.x+c.width):(c.y+c.height));
            }
        }
        float split = Math.max(minSize1, Math.min(Math.min(size-minSize2-borderSize, size*splitPos), actualSize));
        for(int i = 0; i<components.size(); i++){
            Component c = components.get(i);
            if(i==0){
                c.x = c.y = 0;
                if(axis==X_AXIS){
                    c.width = split;
                    c.height = height;
                }else{
                    c.width = width;
                    c.height = split;
                }
            }else{
                if(axis==X_AXIS){
                    c.x = split+borderSize;
                    c.y = 0;
                    c.width = width-split-borderSize;
                    c.height = height;
                }else{
                    c.x = 0;
                    c.y = split+borderSize;
                    c.width = width;
                    c.height = height-split-borderSize;
                }
            }
        }
        if(borderColor!=null&&borderSize>0){
            Renderer renderer = new Renderer();
            renderer.setColor(borderColor.get());
            if(axis==X_AXIS)renderer.fillRect(x+split, y, x+split+borderSize, y+height);
            else renderer.fillRect(x, y+split, x+width, y+split+borderSize);
        }
    }
    public SplitLayout setBorder(float borderSize, Supplier<Color> borderColor){
        this.borderSize = borderSize;
        this.borderColor = borderColor;
        return this;
    }
    public SplitLayout fitSize(){
        this.fitSize = true;
        return this;
    }
    @Override
    public <T extends Component> T add(T component){
        if(components.size()>=2)throw new RuntimeException("Split Layout can only hold two components!");
        return super.add(component);
    }
}