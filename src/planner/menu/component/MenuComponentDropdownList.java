package planner.menu.component;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentDropdownList extends MenuComponent{
    public double preferredHeight;
    private final MenuComponentMinimaList list;
    private boolean isDown = false;
    public MenuComponentDropdownList(double x, double y, double width, double height){
        super(x, y, width, height);
        preferredHeight = height;
        list = new MenuComponentMinimaList(x, y, width, height, height, true){
            @Override
            public <V extends MenuComponent> V add(V component){
                V ret = super.add(component);
                if(getSelectedIndex()==-1)setSelectedIndex(0);
                return ret;
            }
        };
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        if(Double.isNaN(x)||Double.isNaN(y))return;
        if(button==0&&pressed&&!isDown){
            isDown = true;
            doAdd(list);
        }else{
            super.onMouseButton(x, y, button, pressed, mods);
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        if(!isSelected){
            isDown = false;
            components.remove(list);
        }
        for(MenuComponent c : list.components){
            c.width = width-getVertScrollbarWidth();
            c.height = preferredHeight;
        }
        if(isDown){
            list.height = height = Math.min(preferredHeight*list.components.size(), preferredHeight*10);
            double space = gui.helper.displayHeight()-(y+height);
            if(space<0){
                height+=space;
                list.height+=space;
            }
            list.width = width;
        }
        else height = preferredHeight;
        int oldSelected = list.getSelectedIndex();
        super.render(millisSinceLastTick);
        if(list.getSelectedIndex()!=oldSelected){
            isDown = false;
            components.remove(list);
        }
    }
    @Override
    public void render(){
        if(!isDown){
            MenuComponent c = list.components.get(list.getSelectedIndex());
            c.width = width-getVertScrollbarWidth();
            c.height = height;
            c.x = x;
            c.y = y;
            c.isMouseOver = isMouseOver;
            c.render();
            drawDownwardScrollbarButton(x+width-getVertScrollbarWidth(), y, getVertScrollbarWidth(), height);
        }
    }
    public void drawDownwardScrollbarButton(double x, double y, double width, double height){
        drawButton(x, y, width, height);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width/4, y+height/4);
        GL11.glVertex2d(x+3*width/4, y+height/4);
        GL11.glVertex2d(x+width/2, y+3*height/4);
        GL11.glEnd();
    }
    public void drawButton(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getListColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
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
    public void setSelectedIndex(int indexOf){
        list.setSelectedIndex(indexOf);
    }
    public double getVertScrollbarWidth(){
        return list.vertScrollbarWidth;
    }
    public int getSelectedIndex(){
        return list.getSelectedIndex();
    }
    private <V extends MenuComponent> V doAdd(V component){
        return super.add(component);
    }
    @Override
    public <V extends MenuComponent> V add(V component){
        return list.add(component);
    }
    public MenuComponent getSelectedComponent(){
        return list.components.get(getSelectedIndex());
    }
    public void clear(){
        list.components.clear();
    }
}