package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.gui.menu.MenuStackEditor;
public class ScrollableCodeEditor extends Scrollable{
    boolean scrollUpdatePending = false;
    public final CodeEditor editor;
    private final MenuStackEditor menu;
    public ScrollableCodeEditor(float x, float y, float width, float height, float horizScrollbarHeight, float vertScrollbarWidth, String text, MenuStackEditor menu){
        super(x, y, width, height, horizScrollbarHeight, vertScrollbarWidth);
        editor = add(new CodeEditor(text, menu){
            {
                scrollMagnitude = scrollWheelMagnitude = 20;
            }
            @Override
            public void updateScroll(){
                scrollUpdatePending = true;
            }
        });
        this.menu = menu;
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        ArrayList<FormattedText> lines = editor.textDisplay.splitLines();
        float xOff = (lines.size()+"").length()*editor.textWidth;
        renderer.setColor(Core.theme.getCodeLineMarkerColor());
        renderer.fillRect(x, y, x+xOff+editor.border*3/2, y+height);
        super.render2d(deltaTime);
    }
    @Override
    public void drawBackground(double deltaTime){
        Renderer renderer = new Renderer();
        focusedComponent = editor;
        editor.isFocused = isFocused;
        editor.width = Math.max(editor.width, width);
        editor.height = Math.max(editor.height, height);
        renderer.setColor(Core.theme.getCodeBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setWhite();
        super.drawBackground(deltaTime);
    }
    @Override
    public void draw(double deltaTime){
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
        super.draw(deltaTime);
    }
    public String getText(){
        return editor.getText();
    }
}