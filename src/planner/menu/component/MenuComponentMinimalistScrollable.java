package planner.menu.component;
import java.awt.Color;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.components.MenuComponentScrollable;
public class MenuComponentMinimalistScrollable extends MenuComponentScrollable{
    private Color backgroundColor = new Color(.4f, .4f, .8f, 1f);
    public MenuComponentMinimalistScrollable(double x, double y, double width, double height, double horizScrollbarHeight, double vertScrollbarWidth){
        super(x, y, width, height, horizScrollbarHeight, vertScrollbarWidth);
        color = new Color(.25f, .25f, .5f, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
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
        GL11.glColor3f(backgroundColor.getRed()/255F, backgroundColor.getGreen()/255F, backgroundColor.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawVerticalScrollbarForeground(double x, double y, double width, double height){
        GL11.glColor3f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarBackground(double x, double y, double width, double height){
        GL11.glColor3f(backgroundColor.getRed()/255F, backgroundColor.getGreen()/255F, backgroundColor.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarForeground(double x, double y, double width, double height){
        GL11.glColor3f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawButton(double x, double y, double width, double height){
        GL11.glColor3f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F);
        drawRect(x, y, x+width, y+height, 0);
        GL11.glColor3f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F);
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