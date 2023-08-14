package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class SplitListLayout extends Layout{
    private ListLayout top;
    private ListLayout bottom;
    public SplitListLayout(){
        this(null);
    }
    public SplitListLayout(Number componentHeight){
        this(componentHeight, false, false);
    }
    public SplitListLayout(boolean topInverted, boolean bottomInverted){
        this(null, topInverted, bottomInverted);
    }
    public SplitListLayout(Number componentHeight, boolean topInverted, boolean bottomInverted){
        top = super.add(topInverted?new InvertedListLayout(componentHeight):new ListLayout(componentHeight));
        bottom = super.add(bottomInverted?new InvertedListLayout(componentHeight):new ListLayout(componentHeight));
    }
    @Override
    public <T extends Component> T add(T component){
        return top.add(component);
    }
    public <T extends Component> T addToBottom(T component){
        return bottom.add(component);
    }
    @Override
    public void arrangeComponents(){
        top.x = bottom.x = 0;
        top.width = bottom.width = width;
        top.height = top.getTotalHeight();
        top.y = 0;
        bottom.height = bottom.getTotalHeight();
        bottom.y = height-bottom.height;
    }
}