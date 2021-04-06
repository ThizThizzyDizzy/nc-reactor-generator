package planner.menu.component;
import java.util.ArrayList;
import java.util.Locale;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentDropdownList extends MenuComponent{
    public double preferredHeight;
    public final MenuComponentMinimalistTextBox searchBox;
    private final MenuComponentMinimaList list;
    public boolean isDown = false;
    public ArrayList<MenuComponent> allComponents = new ArrayList<>();
    public MenuComponentDropdownList(double x, double y, double width, double height){
        this(x, y, width, height, false);
    }
    public MenuComponentDropdownList(double x, double y, double width, double height, boolean searchable){
        super(x, y, width, height);
        preferredHeight = height;
        list = new MenuComponentMinimaList(0, 0, width, height, height, true){
            @Override
            public <V extends MenuComponent> V add(V component){
                V ret = super.add(component);
                if(getSelectedIndex()==-1)setSelectedIndex(0);
                return ret;
            }
        };
        searchBox = new MenuComponentMinimalistTextBox(0, 0, width, searchable?height:0, "", searchable, "Search", 0){
            @Override
            public void onCharTyped(char c){
                super.onCharTyped(c);
                refreshSearch();
            }
            @Override
            public void keyEvent(int key, int scancode, boolean isPress, boolean isRepeat, int modifiers){
                super.keyEvent(key, scancode, isPress, isRepeat, modifiers);
                refreshSearch();
            }
            @Override
            public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
                super.onMouseButton(x, y, button, pressed, mods);
                if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&pressed){
                    text = "";
                    refreshSearch();
                    MenuComponentDropdownList.this.selected = this;
                    isSelected = true;
                }
            }
        };
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        if(Double.isNaN(x)||Double.isNaN(y))return;
        if(button==0&&pressed&&!isDown){
            isDown = true;
            doAdd(searchBox);
            doAdd(list);
        }else{
            super.onMouseButton(x, y, button, pressed, mods);
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        if(!isSelected){
            isDown = false;
            components.remove(searchBox);
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
            height+=searchBox.height;
            list.y = searchBox.height;
            searchBox.width = width;
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
            if(!list.components.isEmpty()&&list.getSelectedIndex()!=-1){
                MenuComponent c = list.components.get(list.getSelectedIndex());
                c.width = width-getVertScrollbarWidth();
                c.height = height;
                c.x = x;
                c.y = y;
                c.isMouseOver = isMouseOver;
                c.render();
            }
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
        if(allComponents.isEmpty())list.setSelectedIndex(-1);
        else list.setSelectedIndex(Math.max(0,list.components.indexOf(allComponents.get(Math.max(0,indexOf)))));
    }
    public double getVertScrollbarWidth(){
        return list.vertScrollbarWidth;
    }
    public int getSelectedIndex(){
        int id = list.getSelectedIndex();
        return id==-1?0:allComponents.indexOf(list.components.get(id));
    }
    private <V extends MenuComponent> V doAdd(V component){
        return super.add(component);
    }
    @Override
    public <V extends MenuComponent> V add(V component){
        allComponents.add(component);
        refreshSearch();
        return component;
    }
    public MenuComponent getSelectedComponent(){
        if(allComponents.isEmpty())return null;
        return allComponents.get(Math.max(0,getSelectedIndex()));
    }
    public void clear(){
        list.components.clear();
        allComponents.clear();
    }
    public void refreshSearch(){
        ArrayList<MenuComponent> searched = new ArrayList<>();
        String regex = ".*";
        for(char c : searchBox.text.trim().toLowerCase(Locale.ENGLISH).toCharArray()){
            if(Character.isLetterOrDigit(c)){
                regex+=c+".*";
            }else regex+="\\"+c+".*";
        }
        for(MenuComponent c : allComponents){
            if(c instanceof Searchable){
                for(String nam : ((Searchable)c).getSearchableNames()){
                    if(nam.toLowerCase(Locale.ENGLISH).matches(regex)){
                        searched.add(c);
                        break;
                    }
                }
            }else searched.add(c);
        }
        int idx = getSelectedIndex();
        list.components.clear();
        for(MenuComponent comp : searched){
            list.add(comp);
        }
        setSelectedIndex(idx);
    }
}