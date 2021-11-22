package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.Component;
public class MulticolumnList extends Scrollable{
    public float columnWidth;
    public float rowHeight;
    public int columnCount;
    private Supplier<Color> backgroundColor;
    private boolean enableSelection = true;
    public MulticolumnList(float x, float y, float width, float height, float columnWidth, float rowHeight, float scrollbarWidth){
        super(x, y, width, height, 0, scrollbarWidth);
        this.columnWidth = columnWidth;
        this.rowHeight = rowHeight;
        columnCount = MathUtil.max(1, (int)((width-(width%columnWidth))/columnWidth));
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
        float width = this.width-(hasVertScrollbar()?vertScrollbarWidth:0);
        columnCount = MathUtil.max(1, (int)((width-(width%columnWidth))/columnWidth));
        int column = 0;
        float y = 0;
        for(Component c : components){
            c.x = column*columnWidth;
            c.y = y;
            c.width = columnWidth;
            c.height = rowHeight;
            column = (column+1)%columnCount;
            if(column==0) y+=rowHeight;
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
    public MulticolumnList disableSelection(){
        enableSelection = false;
        return this;
    }
}