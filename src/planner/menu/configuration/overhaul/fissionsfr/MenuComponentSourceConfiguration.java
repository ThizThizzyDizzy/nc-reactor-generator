package planner.menu.configuration.overhaul.fissionsfr;
import multiblock.configuration.overhaul.fissionsfr.Source;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentSourceConfiguration extends MenuComponent{
    public final Source source;
    public final MenuComponentMinimalistButton edit = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(Core.theme.getTextColor());
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
    }.setTooltip("Modify neutron source"));
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "", true, true, true){
        @Override
        public void renderForeground(){
            super.renderForeground();
            Core.applyColor(Core.theme.getTextColor());
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
    }.setTooltip("Delete neutron source\nWARNING: This cannot be undone!"));
    public MenuComponentSourceConfiguration(Source source){
        super(0, 0, 0, 100);
        this.source = source;
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
        if(isMouseOver)Core.applyColor(Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyColor(Core.theme.getTextColor());
        drawText(x, y, x+width, y+height/2, source.name);
        drawText(x, y+height/2, x+width, y+height, "Efficiency: "+source.efficiency);
    }
}