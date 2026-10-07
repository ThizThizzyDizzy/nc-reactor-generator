package net.ncplanner.plannerator.planner.gui.menu.component.tutorial;
import net.ncplanner.plannerator.graphics.PlanneratorRenderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
public class MenuComponentTutorial extends Component{
    private final Tutorial tutorial;
    public MenuComponentTutorial(Tutorial tutorial){
        super(0, 0, 0, 0);
        this.tutorial = tutorial;
    }
    @Override
    public void draw(double deltaTime){
        PlanneratorRenderer renderer = new PlanneratorRenderer();
        if(isFocused){
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer);
    }
    public void drawText(PlanneratorRenderer renderer){
        float textLength = renderer.getStringWidth(tutorial.name, height);
        float scale = Math.min(1, width/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, tutorial.name);
    }
}
