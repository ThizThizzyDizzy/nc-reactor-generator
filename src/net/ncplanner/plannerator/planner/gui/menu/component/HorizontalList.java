package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
public class HorizontalList extends Scrollable{
    private Supplier<Color> backgroundColor;
    private boolean enableSelection = true;
    public HorizontalList(float x, float y, float width, float height, float scrollbarHeight){
        super(x, y, width, height, scrollbarHeight, 0);
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
        float x = 0;
        for(Component c : components){
            c.x = x;
            c.y = 0;
            x+=c.width;
            c.height = height-(hasHorizScrollbar()?horizScrollbarHeight:0);
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
    public HorizontalList disableSelection(){
        enableSelection = false;
        return this;
    }
}