package planner.menu.component;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponentScrollable;
public class MenuComponentMinimalistScrollable extends MenuComponentScrollable{
    public MenuComponentMinimalistScrollable(double x, double y, double width, double height, double horizScrollbarHeight, double vertScrollbarWidth){
        super(x, y, width, height, horizScrollbarHeight, vertScrollbarWidth, false, false);
        color = Core.theme.getListColor();
        foregroundColor = Core.theme.getTextColor();
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
        Core.applyColor(Core.theme.getListBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawVerticalScrollbarForeground(double x, double y, double width, double height){
        Core.applyColor(color);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarBackground(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getListBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarForeground(double x, double y, double width, double height){
        Core.applyColor(color);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawButton(double x, double y, double width, double height){
        Core.applyColor(color);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(foregroundColor);
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
    public void persistMouseEvent(int button, boolean pressed, float x, float y){
        if(button==-1&&Mouse.isButtonDown(0)){
            button = 0;
            pressed = true;
        }
        super.persistMouseEvent(button, pressed, x, y);
    }
    int lowestNonZeroWheel = Integer.MAX_VALUE;
    @Override
    public boolean mouseWheelChange(int wheelChange){
        if(!isClickWithinBounds(Mouse.getX(), Display.getHeight()-Mouse.getY(), x, y, x+width, y+height))return false;
        if(wheelChange!=0){
            lowestNonZeroWheel = Math.min(lowestNonZeroWheel, Math.abs(wheelChange));
        }
        int scroll = wheelChange/lowestNonZeroWheel;
        for(int i = 0; i<scroll; i++){
            scrollUp();
        }
        for(int i = 0; i<-scroll; i++){
            scrollDown();
        }
        return true;
    }
}