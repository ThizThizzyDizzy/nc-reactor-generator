package planner.menu.configuration.overhaul.fissionsfr;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import planner.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentIrradiatorRecipeConfiguration extends MenuComponent{
    public final IrradiatorRecipe irradiatorRecipe;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, .55f){
        @Override
        public void renderForeground(){
            super.renderForeground();
            GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255f);
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
    });
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, .55f){
        @Override
        public void renderForeground(){
            super.renderForeground();
            GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255f);
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
    });
    public MenuComponentIrradiatorRecipeConfiguration(IrradiatorRecipe irradiatorRecipe){
        super(0, 0, 0, 100);
        color = new Color(.3f, .3f, .6f, 1f);
        selectedColor = new Color(.35f, .35f, .7f, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
        this.irradiatorRecipe = irradiatorRecipe;
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
        if(isMouseOver)GL11.glColor4f(selectedColor.getRed()/255f, selectedColor.getGreen()/255f, selectedColor.getBlue()/255f, selectedColor.getAlpha()/255f);
        else GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255f);
        drawText(x, y, x+width, y+height/4, irradiatorRecipe.name);
        drawText(x, y+height/4, x+width, y+height/4*2, "Efficiency: "+irradiatorRecipe.efficiency);
        drawText(x, y+height/4*2, x+width, y+height/4*3, "Heat: "+irradiatorRecipe.heat);
    }
}