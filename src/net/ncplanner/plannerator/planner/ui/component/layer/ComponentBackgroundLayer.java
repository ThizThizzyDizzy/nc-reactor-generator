package net.ncplanner.plannerator.planner.ui.component.layer;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.ui.component.Component;
import com.thizthizzydizzy.dizzyengine.ui.component.layer.ComponentLayer;
import net.ncplanner.plannerator.planner.Core;
public class ComponentBackgroundLayer extends ComponentLayer{
    @Override
    public void draw(Component c, double deltaTime){
        int index = Core.getThemeIndex(c);
        Color color;
        if(c.isCursorFocused()){
            color = Core.theme.getComponentMouseoverColor(index);
        }else{
            color = Core.theme.getComponentColor(index);
        }
        
        Renderer.setColor(color);
        Renderer.fillRect(c.x, c.y, c.x+c.getWidth(), c.y+c.getHeight());
    }
}
