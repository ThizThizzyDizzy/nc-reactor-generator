package net.ncplanner.plannerator.planner.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import simplelibrary.opengl.gui.components.MenuComponentScrollable;
public class MenuComponentScrollableCodeEditor extends MenuComponentScrollable{
    boolean scrollUpdatePending = false;
    public final MenuComponentCodeEditor editor;
    public MenuComponentScrollableCodeEditor(double x, double y, double width, double height, double horizScrollbarHeight, double vertScrollbarWidth, String text){
        super(x, y, width, height, horizScrollbarHeight, vertScrollbarWidth, false, false);
        editor = add(new MenuComponentCodeEditor(text){
        {
            setScrollMagnitude(20);
            setScrollWheelMagnitude(20);
        }
        @Override
        public void updateScroll(){
            scrollUpdatePending = true;
        }
    });
    }
    @Override
    public void render(int millisSinceLastTick){
        Renderer renderer = new Renderer();
        ArrayList<FormattedText> lines = editor.textDisplay.splitLines();
        int xOff = (lines.size()+"").length()*editor.textWidth;
        renderer.setColor(Core.theme.getCodeLineMarkerColor());
        drawRect(x, y, x+xOff+editor.border*3/2, y+height, 0);
        super.render(millisSinceLastTick);
    }
    @Override
    public void renderBackground(){
        Renderer renderer = new Renderer();
        selected = editor;
        editor.isSelected = isSelected;
        editor.width = Math.max(editor.width, width);
        editor.height = Math.max(editor.height, height);
        renderer.setColor(Core.theme.getCodeBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
        renderer.setWhite();
        super.renderBackground();
    }
    @Override
    public void render(){
        if(scrollUpdatePending){
            scrollUpdatePending = false;
            boolean scrolled;
            double pw = 0, ph = 0, px1 = 0, py1 = 0;
            do{
                scrolled = false;
                double w = width-vertScrollbarWidth;
                double h = height-horizScrollbarHeight;
                double x1 = editor.cursorX*editor.textWidth-getHorizScroll();
                double x2 = x1+editor.border*2+(editor.text.size()+"").length()*editor.textWidth+editor.textWidth+editor.border;
                double y1 = editor.cursorY*editor.textHeight-getVertScroll();
                double y2 = y1+editor.border+editor.textHeight+editor.border;
                if(x1<0){
                    scrollLeft();
                    scrolled = true;
                }
                if(y1<0){
                    scrollUp();
                    scrolled = true;
                }
                if(x2>w){
                    scrollRight();
                    scrolled = true;
                }
                if(y2>h){
                    scrollDown();
                    scrolled = true;
                }
                if(w==pw&&h==ph&&x1==px1&&y1==py1)scrolled = false;
                pw = w;
                ph = h;
                px1 = x1;
                py1 = y1;
            }while(scrolled);
        }
        super.render();
    }
    public String getText(){
        return editor.getText();
    }
    public void setText(String scriptText){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}