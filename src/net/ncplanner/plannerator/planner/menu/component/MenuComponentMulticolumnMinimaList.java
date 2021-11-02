package net.ncplanner.plannerator.planner.menu.component;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuComponentMulticolumnMinimaList extends MenuComponentMulticolumnList{
    public MenuComponentMulticolumnMinimaList(double x, double y, double width, double height, double columnWidth, double rowHeight, double scrollbarWidth){
        super(x, y, width, height, columnWidth, rowHeight, scrollbarWidth);
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
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getScrollbarBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawVerticalScrollbarForeground(double x, double y, double width, double height){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getScrollbarButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarBackground(double x, double y, double width, double height){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getScrollbarBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawHorizontalScrollbarForeground(double x, double y, double width, double height){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getScrollbarButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawButton(double x, double y, double width, double height){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getScrollbarButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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
//    @Override
//    public void persistMouseEvent(int button, boolean pressed, float x, float y){
//        if(button==-1&&Mouse.isButtonDown(0)){
//            button = 0;
//            pressed = true;
//        }
//        super.persistMouseEvent(button, pressed, x, y);
//    }
    @Override
    public void renderBackground(){
        setScrollMagnitude(Math.min(width, height)/20);
        for(int i = 0; i<components.size(); i++){
            components.get(i).isSelected = getSelectedIndex()==i;
        }
        super.renderBackground();
    }
    @Override
    public void setSelectedIndex(int index){
        super.setSelectedIndex(index);
        if(index<0||index>=components.size()) selected = null;
        else selected = components.get(index);
    }
}