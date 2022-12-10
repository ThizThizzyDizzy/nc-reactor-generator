package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class DropdownList extends Component{
    public float preferredHeight;
    public final TextBox searchBox;
    public final SingleColumnList list;
    public boolean isDown = false;
    public ArrayList<Component> allComponents = new ArrayList<>();
    private boolean showButton = true;
    public DropdownList(float x, float y, float width, float height){
        this(x, y, width, height, false);
    }
    public DropdownList(float x, float y, float width, float height, boolean searchable){
        super(x, y, width, height);
        preferredHeight = height;
        list = new SingleColumnList(0, 0, width, height, height){
            @Override
            public <T extends Component> T add(T component){
                T ret = super.add(component);
                if(getSelectedIndex()==-1)setSelectedIndex(0);
                return ret;
            }
            @Override
            public void onMouseButton(double x, double y, int button, int action, int mods){
                super.onMouseButton(x, y, button, action, mods);
                if(button==0&&action==GLFW_PRESS&&Core.isShiftPressed()){
                    //doing it again, but this time not actually passing events, just checking for pinned stuff to toggle
                    if(x>width-(hasVertScrollbar()?vertScrollbarWidth:0)||y>height-(hasHorizScrollbar()?horizScrollbarHeight:0)){//Click events on the scrollbar
                        x=y=Double.NaN;
                    }else{
                        x+=getHorizScroll();
                        y+=getVertScroll();
                    }
                    boolean clicked = false;
                    for(int i = components.size()-1; i>=0; i--){
                        if(i>=components.size()) continue;
                        Component component = components.get(i);
                        if(!Double.isNaN(x)&&!clicked&&MathUtil.isPointWithinRect(x, y, component.x, component.y, component.x+component.width, component.y+component.height)){
                            clicked = true;
                            if(component instanceof Pinnable){
                                Pinnable.togglePin((Pinnable)component);
                                refreshSearch();
                            }
                        }
                    }
                }
            }
        };
        searchBox = new TextBox(0, 0, width, searchable?height:0, "", searchable, "Search", 0){
            @Override
            public void onCharTyped(char c){
                super.onCharTyped(c);
                refreshSearch();
            }
            @Override
            public void onKeyEvent(int key, int scancode, int action, int mods){
                super.onKeyEvent(key, scancode, action, mods);
                refreshSearch();
            }
            @Override
            public void onMouseButton(double x, double y, int button, int action, int mods){
                super.onMouseButton(x, y, button, action, mods);
                if(button==GLFW_MOUSE_BUTTON_RIGHT&&action==GLFW_PRESS){
                    text = "";
                    refreshSearch();
                    DropdownList.this.focusedComponent = this;
                    isFocused = true;
                }
            }
        };
    }
    public DropdownList hideButton(){
        showButton = false;
        return this;
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(button==0&&action==GLFW_PRESS&&!isDown){
            isDown = true;
            doAdd(searchBox);
            doAdd(list);
        }else{
            super.onMouseButton(x, y, button, action, mods);
        }
    }
    @Override
    public void render2d(double deltaTime){
        if(!isFocused){
            isDown = false;
            components.remove(searchBox);
            components.remove(list);
        }
        synchronized(list){
            for(Component c : list.components){
                c.width = width-getVertScrollbarWidth();
                c.height = preferredHeight;
            }
        }
        if(isDown){
            synchronized(list){
                list.height = height = Math.min(preferredHeight*list.components.size(), preferredHeight*10);
            }
            double space = gui.getHeight()-(y+height);
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
        synchronized(list){
            super.render2d(deltaTime);
            if(list.getSelectedIndex()!=oldSelected){
                isDown = false;
                components.remove(list);
            }
        }
    }
    @Override
    public void draw(double deltaTime){
        if(!isDown){
            Renderer renderer = new Renderer();
            if(!list.components.isEmpty()&&list.getSelectedIndex()!=-1){
                Component c = list.components.get(list.getSelectedIndex());
                c.width = width-(showButton?getVertScrollbarWidth():0);
                c.height = height;
                c.x = x;
                c.y = y;
                c.isMouseFocused = isMouseFocused;
                c.draw(deltaTime);
            }
            if(showButton)list.drawDownwardScrollbarButton(renderer, x+width-getVertScrollbarWidth(), y, getVertScrollbarWidth(), height);
        }
    }
    public void setSelectedIndex(int indexOf){
        synchronized(list){
            if(allComponents.isEmpty())list.setSelectedIndex(-1);
            else list.setSelectedIndex(Math.max(0,list.components.indexOf(allComponents.get(Math.max(0,indexOf)))));
        }
    }
    public float getVertScrollbarWidth(){
        return list.vertScrollbarWidth;
    }
    public int getSelectedIndex(){
        synchronized(list){
            int id = list.getSelectedIndex();
            return id==-1?0:allComponents.indexOf(list.components.get(id));
        }
    }
    private <T extends Component> T doAdd(T component){
        return super.add(component);
    }
    @Override
    public <T extends Component> T add(T component){
        allComponents.add(component);
        refreshSearch();
        return component;
    }
    public Component getSelectedComponent(){
        if(allComponents.isEmpty())return null;
        return allComponents.get(Math.max(0,getSelectedIndex()));
    }
    public void clear(){
        synchronized(list){
            list.components.clear();
        }
        allComponents.clear();
    }
    public void refreshSearch(){
        ArrayList<Component> searched = Pinnable.searchAndSort(allComponents, searchBox.text);
        int idx = getSelectedIndex();
        synchronized(list){
            list.components.clear();
            for(Component comp : searched){
                list.add(comp);
            }
        }
        setSelectedIndex(idx);
    }
    @Override
    public String getTooltip(){
        if(!isDown){
            Component c = getSelectedComponent();
            if(c!=null)return c.getTooltip();
        }
        return super.getTooltip();
    }
}