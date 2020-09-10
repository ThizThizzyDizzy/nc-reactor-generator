package planner.menu.component.tutorial;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.tutorial.Tutorial;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTutorialDisplay extends MenuComponent{
    public Tutorial tutorial;
    public MenuComponentTutorialDisplay(Tutorial tutorial){
        super(0, 0, 0, 0);
        this.tutorial = tutorial;
    }
    @Override
    public void render(){
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(0, 0, width, height, 0);
        Renderer2D.pushAndClearBoundStack();
        GL11.glPushMatrix();
        GL11.glScaled(width, width, 1);
        tutorial.render();
        GL11.glPopMatrix();
        Renderer2D.popBoundStack();
    }
}