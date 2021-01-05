package planner.menu.component.tutorial;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.editor.tutorial.Tutorial;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTutorialDisplay extends MenuComponent{
    private static final float fac = .01f;
    public Tutorial tutorial;
    private float actualVal;
    private int tick = 0;
    public MenuComponentTutorialDisplay(Tutorial tutorial){
        super(0, 0, 0, 0);
        this.tutorial = tutorial;
    }
    @Override
    public void tick(){
        super.tick();
        tick++;
        tutorial.tick(tick);
    }
    @Override
    public void render(int millisSinceLastTick){
        actualVal = tick+millisSinceLastTick/50f;
        super.render(millisSinceLastTick);
    }
    @Override
    public void render(){
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(0, 0, width, height, 0);
        Renderer2D.pushAndClearBoundStack();
        tutorial.preRender();
        GL11.glPushMatrix();
        GL11.glScaled(width, width, 1);
        tutorial.render((float)(Math.cos(2*Math.PI*actualVal*fac)+1)/2, actualVal);
        GL11.glPopMatrix();
        Renderer2D.popBoundStack();
    }
}