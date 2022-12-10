package net.ncplanner.plannerator.planner.gui.menu.dssl;
import net.ncplanner.plannerator.planner.gui.menu.component.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.dssl.Script;
public class ScrollableDsslEditor extends Scrollable{
    boolean scrollUpdatePending = false;
    public DsslEditor editor;
    public boolean debug;
    public Script script;
    public HashMap<String, HashSet<String>> libraries;
    public ScrollableDsslEditor(float x, float y, float width, float height, float horizScrollbarHeight, float vertScrollbarWidth){
        super(x, y, width, height, horizScrollbarHeight, vertScrollbarWidth);
        scrollMagnitude = scrollWheelMagnitude = 20;
    }
    public void setEditor(DsslEditor editor){
        if(this.editor!=null)components.remove(this.editor);
        scrollUpdatePending = true;
        if(editor==null)return;
        editor.activeScript = script = null;
        editor.libraries = libraries;
        this.editor = add(editor);
        editor.onScrollUpdate(() -> {scrollUpdatePending = true;});
    }
    @Override
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        if(editor!=null){
            editor.debug = debug;
            editor.activeScript = script;
            ArrayList<FormattedText> lines = editor.textDisplay.splitLines();
            float xOff = (lines.size()+"").length()*editor.textWidth;
            renderer.setColor(Core.theme.getCodeLineMarkerColor());
            renderer.fillRect(x, y, x+xOff+editor.border*3/2, y+height);
        }
        super.render2d(deltaTime);
    }
    @Override
    public void drawBackground(double deltaTime){
        Renderer renderer = new Renderer();
        if(editor!=null){
            focusedComponent = editor;
            editor.isFocused = isFocused;
            editor.width = Math.max(editor.width, width);
            editor.height = Math.max(editor.height, height);
        }
        renderer.setColor(Core.theme.getCodeBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setWhite();
        super.drawBackground(deltaTime);
    }
    @Override
    public void draw(double deltaTime){
        if(scrollUpdatePending&&editor!=null){
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
        return editor==null?null:editor.getText();
    }
}