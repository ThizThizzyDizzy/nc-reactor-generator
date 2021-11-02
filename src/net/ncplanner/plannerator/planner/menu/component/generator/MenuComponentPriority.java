package net.ncplanner.plannerator.planner.menu.component.generator;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentPriority extends MenuComponent{
    public final Priority priority;
    public MenuComponentPriority(Priority priority){
        super(0, 0, 0, 32);
        this.priority = priority;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        if(isSelected){
            if(isMouseOver)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(priority.name, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, priority.name);
    }
}