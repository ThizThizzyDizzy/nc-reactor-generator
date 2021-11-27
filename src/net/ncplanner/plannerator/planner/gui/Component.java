package net.ncplanner.plannerator.planner.gui;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import static org.lwjgl.glfw.GLFW.*;
public class Component{
    public GUI gui;
    public Component parent;
    public ArrayList<Component> components = new ArrayList<>();
    public float x, y, width, height;
    public boolean isMouseFocused, isFocused;
    public Component focusedComponent;
    public Component mouseFocusedComponent;
    public String tooltip;
    public boolean focusable = true;
    public Component(){
        this(0, 0, 0, 0);
    }
    public Component(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void render2d(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.bound(x, y, x+width, y+height);
        drawBackground(deltaTime);
        draw(deltaTime);
        renderer.translate(x, y);
        for(Component c : components){
            c.render2d(deltaTime);
        }
        renderer.unTranslate();
        drawForeground(deltaTime);
        renderer.unBound();
    }
    public void drawBackground(double deltaTime){}
    public void draw(double deltaTime){}
    public void drawForeground(double deltaTime){}
    public <T extends Component> T add(T component){
        components.add(component);
        component.parent = this;
        component.gui = gui;
        component.onAdded();
        return component;
    }
    public void onAdded(){}
    public void onCharTyped(char c){
        if(focusedComponent!=null)focusedComponent.onCharTyped(c);
    }
    public void onCharTypedWithModifiers(char c, int mods){
        if(focusedComponent!=null)focusedComponent.onCharTypedWithModifiers(c, mods);
    }
    public void onCursorEnteredWindow(){
        components.forEach((t) -> {t.onCursorEnteredWindow();});
    }
    public void onCursorExitedWindow(){
        components.forEach((t) -> {t.onCursorExitedWindow();});
        if(mouseFocusedComponent!=null){
            mouseFocusedComponent.isMouseFocused = false;
            mouseFocusedComponent = null;
        }
    }
    public void onCursorMoved(double xpos, double ypos){
        boolean foundFocus = false;
        for(Component comp : components){
            if(xpos>=comp.x&&ypos>=comp.y&&xpos<comp.x+comp.width&&ypos<comp.y+comp.height){
                if(comp.focusable){
                    foundFocus = true;
                    if(mouseFocusedComponent!=comp){
                        if(mouseFocusedComponent!=null){
                            mouseFocusedComponent.isMouseFocused = false;
                            mouseFocusedComponent.onCursorExited();
                        }
                        mouseFocusedComponent = comp;
                        mouseFocusedComponent.isMouseFocused = true;
                        mouseFocusedComponent.onCursorEntered();
                    }
                    foundFocus = true;
                }
            }
        }
        if(!foundFocus){
            if(mouseFocusedComponent!=null){
                mouseFocusedComponent.isMouseFocused = false;
                mouseFocusedComponent.onCursorExited();
            }
            mouseFocusedComponent = null;
        }
        if(mouseFocusedComponent!=null)mouseFocusedComponent.onCursorMoved(xpos-mouseFocusedComponent.x, ypos-mouseFocusedComponent.y);
    }
    public void onCursorEntered(){}
    public void onCursorExited(){}
    public void onFilesDropped(String[] files){
        if(mouseFocusedComponent!=null)mouseFocusedComponent.onFilesDropped(files);
    }
    public void onKeyEvent(int key, int scancode, int action, int mods){
        if(focusedComponent!=null)focusedComponent.onKeyEvent(key, scancode, action, mods);
    }
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(action==GLFW_PRESS)onCursorMoved(x, y);//workaround solution for mouseButton not doing mouse focus scanning itself; ensures mouse focus is accurate before press event
        if(mouseFocusedComponent!=null){
            if(action==GLFW_RELEASE&&focusedComponent!=null){
                focusedComponent.onMouseButton(x-focusedComponent.x, y-focusedComponent.y, button, action, mods);
                return;
            }//still get release event when mouse not over
            if(action==GLFW_PRESS){
                if(focusedComponent!=mouseFocusedComponent){
                    if(focusedComponent!=null){
                        focusedComponent.isFocused = false;
                        focusedComponent.onFocusLost();
                    }
                    focusedComponent = mouseFocusedComponent;
                    focusedComponent.isFocused = true;
                    focusedComponent.onFocusGained();
                }
            }
            mouseFocusedComponent.onMouseButton(x-mouseFocusedComponent.x, y-mouseFocusedComponent.y, button, action, mods);
        }
    }
    public void onFocusGained(){}
    public void onFocusLost(){}
    public boolean onScroll(double dx, double dy){
        if(mouseFocusedComponent!=null)return mouseFocusedComponent.onScroll(dx, dy);
        return false;
    }
    public void onWindowClosed(){
        components.forEach((comp)->{comp.onWindowClosed();});
    }
    public void onWindowFocusGained(){
        components.forEach((comp)->{comp.onWindowFocusGained();});
    }
    public void onWindowFocusLost(){
        components.forEach((comp)->{comp.onWindowFocusLost();});
    }
    public void onWindowIconified(){
        components.forEach((comp)->{comp.onWindowIconified();});
    }
    public void onWindowUniconified(){
        components.forEach((comp)->{comp.onWindowUniconified();});
    }
    public void onWindowMaximized(){
        components.forEach((comp)->{comp.onWindowMaximized();});
    }
    public void onWindowUnmaximized(){
        components.forEach((comp)->{comp.onWindowUnmaximized();});
    }
    public void onWindowMoved(int xpos, int ypos){
        components.forEach((comp)->{comp.onWindowMoved(xpos, ypos);});
    }
    public String getTooltip(){
        return tooltip;
    }
    public Component setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    public double getTooltipOffsetX(){
        return 0;
    }
    public double getTooltipOffsetY(){
        return height;
    }
    public void setFocusable(boolean focusable){
        this.focusable = focusable;
    }
}