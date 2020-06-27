package planner.menu.configuration.overhaul.fissionsfr;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.configuration.overhaul.fissionsfr.Fuel;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentFuelConfiguration extends MenuComponent{
    public final Fuel fuel;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(foregroundColor);
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
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(foregroundColor);
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
    public MenuComponentFuelConfiguration(Fuel fuel){
        super(0, 0, 0, 100);
        color = Core.theme.getButtonColor();
        selectedColor = Core.theme.getSelectedMultiblockColor();
        foregroundColor = Core.theme.getTextColor();
        this.fuel = fuel;
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
        if(isMouseOver)Core.applyColor(selectedColor);
        else Core.applyColor(color);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyColor(foregroundColor);
        drawText(x, y, x+width, y+height/6, fuel.name);
        drawText(x, y+height/6, x+width, y+height/6*2, "Criticality: "+fuel.criticality);
        drawText(x, y+height/6*2, x+width, y+height/6*3, "Efficiency: "+fuel.efficiency);
        drawText(x, y+height/6*3, x+width, y+height/6*4, "Heat: "+fuel.heat);
        drawText(x, y+height/6*4, x+width, y+height/6*5, "Time: "+fuel.time);
        if(fuel.selfPriming)drawText(x, y+height/6*5, x+width, y+height/6*6, "Self-Priming");
    }
}