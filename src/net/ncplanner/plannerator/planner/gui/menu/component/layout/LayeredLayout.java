package net.ncplanner.plannerator.planner.gui.menu.component.layout;
import net.ncplanner.plannerator.planner.gui.Component;
public class LayeredLayout extends Layout{
    @Override
    public void arrangeComponents(){
        for(Component c : components){
            c.x = c.y = 0;
            c.width = width;
            c.height = height;
        }
    }
    @Override
    public void onCursorMoved(double xpos, double ypos){
        boolean foundFocus = false;
        for(int i = components.size()-1; i>=0; i--){
            Component comp = components.get(i);
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
                break;
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
}