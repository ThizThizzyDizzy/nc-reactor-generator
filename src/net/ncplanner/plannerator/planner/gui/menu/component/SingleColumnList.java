package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
public class SingleColumnList extends Scrollable{//TODO combine with ListLayout?
    private Supplier<Color> backgroundColor;
    private boolean enableSelection = true;
    public SingleColumnList(float scrollbarWidth){
        this(0, 0, 0, 0, scrollbarWidth);
    }
    public SingleColumnList(float x, float y, float width, float height, float scrollbarWidth){
        super(x, y, width, height, 0, scrollbarWidth);
    }
    @Override
    public void drawBackground(double deltaTime){
        Renderer renderer = new Renderer();
        if(!enableSelection)focusedComponent = null;
        if(backgroundColor!=null){
            Color c = backgroundColor.get();
            renderer.setColor(c);
            renderer.fillRect(x, y, x+width, y+height);
            renderer.setWhite();
        }
        scrollMagnitude = MathUtil.min(width, height)/20;
        for(int i = 0; i<components.size(); i++){
            components.get(i).isFocused = getSelectedIndex()==i;
        }
        float y = 0;
        for(Component c : components){
            c.x = 0;
            c.y = y;
            y+=c.height;
            c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
        }
        super.drawBackground(deltaTime);
    }
    public int getSelectedIndex(){
        return components.indexOf(focusedComponent);
    }
    public void setSelectedIndex(int index){
        if(!enableSelection)index = -1;
        if(index<0||index>=components.size()) focusedComponent = null;
        else focusedComponent = components.get(index);
    }
    public void setBackgroundColor(Supplier<Color> c){
        backgroundColor = c;
    }
    public SingleColumnList disableSelection(){
        enableSelection = false;
        return this;
    }
}