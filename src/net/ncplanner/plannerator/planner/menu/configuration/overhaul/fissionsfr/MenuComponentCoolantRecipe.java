package net.ncplanner.plannerator.planner.menu.configuration.overhaul.fissionsfr;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import org.lwjgl.opengl.GL11;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentCoolantRecipe extends MenuComponent{
    public final CoolantRecipe coolantRecipe;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            Renderer renderer = new Renderer();
            super.renderForeground();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            GL11.glBegin(GL11.GL_TRIANGLES);
            GL11.glVertex2d(x+width*.25, y+height*.75);
            GL11.glVertex2d(x+width*.375, y+height*.75);
            GL11.glVertex2d(x+width*.25, y+height*.625);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d(x+width*.4, y+height*.725);
            GL11.glVertex2d(x+width*.275, y+height*.6);
            GL11.glVertex2d(x+width*.5, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.5);

            GL11.glVertex2d(x+width*.525, y+height*.35);
            GL11.glVertex2d(x+width*.65, y+height*.475);
            GL11.glVertex2d(x+width*.75, y+height*.375);
            GL11.glVertex2d(x+width*.625, y+height*.25);
            GL11.glEnd();
        }
        @Override
        public void action(){
            onEditPressed.run();
        }
    }.setTooltip("Modify coolant recipe"));
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            Renderer renderer = new Renderer();
            super.renderForeground();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2d(x+width*.1, y+height*.8);
            GL11.glVertex2d(x+width*.2, y+height*.9);
            GL11.glVertex2d(x+width*.9, y+height*.2);
            GL11.glVertex2d(x+width*.8, y+height*.1);
            
            GL11.glVertex2d(x+width*.1, y+height*.2);
            GL11.glVertex2d(x+width*.2, y+height*.1);
            GL11.glVertex2d(x+width*.9, y+height*.8);
            GL11.glVertex2d(x+width*.8, y+height*.9);
            GL11.glEnd();
        }
        @Override
        public void action(){
            onDeletePressed.run();
        }
    }.setTooltip("Delete coolant recipe"));
    private final Runnable onEditPressed;
    private final Runnable onDeletePressed;
    public MenuComponentCoolantRecipe(CoolantRecipe coolantRecipe, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.coolantRecipe = coolantRecipe;
        this.onEditPressed = onEditPressed;
        this.onDeletePressed = onDeletePressed;
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        delete.x = width-height/2-height/4;
        edit.x = delete.x - height;
        delete.y = edit.y = height/4;
        delete.width = delete.height = edit.width = edit.height = height/2;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        if(isMouseOver)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(coolantRecipe.inputTexture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(coolantRecipe.inputDisplayTexture));
        if(coolantRecipe.outputTexture!=null)drawRect(x+height, y, x+height*2, y+height, Core.getTexture(coolantRecipe.outputDisplayTexture));
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(x+height*2, y, x+width, y+height/4, coolantRecipe.getInputDisplayName());
        drawText(x+height*2, y+height/4, x+width, y+height/4*2, coolantRecipe.getOutputDisplayName());
        drawText(x+height*2, y+height/4*2, x+width, y+height/4*3, "Heat: "+coolantRecipe.heat);
        drawText(x+height*2, y+height/4*3, x+width, y+height/4*4, "Output Ratio: "+coolantRecipe.outputRatio);
    }
}
