package planner.menu.component.tutorial;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.tutorial.Tutorial;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTutorialDisplay extends MenuComponent{
    public Tutorial tutorial;
    public float val = 0;
    public float inc = .01f;
    public float actualVal;
    public MenuComponentTutorialDisplay(Tutorial tutorial){
        super(0, 0, 0, 0);
        this.tutorial = tutorial;
    }
    @Override
    public void tick(){
        super.tick();
        val+=inc;
    }
    @Override
    public void render(int millisSinceLastTick){
        actualVal = val+inc*millisSinceLastTick/50f;
        super.render(millisSinceLastTick);
    }
    @Override
    public void render(){
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(0, 0, width, height, 0);
        Renderer2D.pushAndClearBoundStack();
        GL11.glPushMatrix();
        GL11.glScaled(width, width, 1);
        tutorial.render((float)(Math.cos(2*Math.PI*actualVal)+1)/2);
        GL11.glPopMatrix();
        Renderer2D.popBoundStack();
    }
}