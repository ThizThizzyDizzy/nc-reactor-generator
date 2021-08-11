package planner.menu.component;
import java.util.function.Supplier;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.image.Color;
import simplelibrary.opengl.gui.components.MenuComponentList;
public class MenuComponentMinimaList extends MenuComponentList{
    private Supplier<Color> backgroundColor;
    private boolean enableSelection = true;
    public MenuComponentMinimaList(double x, double y, double width, double height, double scrollbarWidth){
        this(x, y, width, height, scrollbarWidth, false);
    }
    public MenuComponentMinimaList(double x, double y, double width, double height, double scrollbarWidth, boolean alwaysShow){
        super(x, y, width, height, scrollbarWidth, alwaysShow);
        setScrollMagnitude(32);setScrollWheelMagnitude(32);
    }
    @Override
    public void drawUpwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/2, y+height/4);
        GL11.glVertex2d(x+width/4, y+3*height/4);
        GL11.glVertex2d(x+3*width/4, y+3*height/4);
        GL11.glEnd();
    }
    @Override
    public void drawDownwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/4);
        GL11.glVertex2d(x+3*width/4, y+height/4);
        GL11.glVertex2d(x+width/2, y+3*height/4);
        GL11.glEnd();
    }
    @Override
    public void drawRightwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/4);
        GL11.glVertex2d(x+width/4, y+3*height/4);
        GL11.glVertex2d(x+3*width/4, y+height/2);
        GL11.glEnd();
    }
    @Override
    public void drawLeftwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/2);
        GL11.glVertex2d(x+3*width/4, y+height/4);
        GL11.glVertex2d(x+3*width/4, y+3*height/4);
        GL11.glEnd();
    }
    @Override
    public void drawVerticalScrollbarBackground(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getScrollbarBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawVerticalScrollbarForeground(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getScrollbarButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarBackground(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getScrollbarBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarForeground(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getScrollbarButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawButton(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getScrollbarButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x+1, y);
        GL11.glVertex2d(x+1, y+height-1);
        GL11.glVertex2d(x+1, y+height-1);
        GL11.glVertex2d(x+width, y+height-1);
        GL11.glVertex2d(x+width, y+height-1);
        GL11.glVertex2d(x+width, y);
        GL11.glVertex2d(x+width, y);
        GL11.glVertex2d(x+1, y);
        GL11.glEnd();
    }
    @Override
    public void renderBackground(){
        if(!enableSelection)selected = null;
        if(backgroundColor!=null){
            Color c = backgroundColor.get();
            GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
            drawRect(x, y, x+width, y+height, 0);
            GL11.glColor4f(1, 1, 1, 1);
        }
        setScrollMagnitude(Math.min(width, height)/20);
        for(int i = 0; i<components.size(); i++){
            components.get(i).isSelected = getSelectedIndex()==i;
        }
        super.renderBackground();
    }
    @Override
    public void setSelectedIndex(int index){
        if(!enableSelection)index = -1;
        super.setSelectedIndex(index);
        if(index<0||index>=components.size()) selected = null;
        else selected = components.get(index);
    }
    public void setBackgroundColor(Supplier<Color> c){
        backgroundColor = c;
    }
    public MenuComponentMinimaList disableSelection(){
        enableSelection = false;
        return this;
    }
}