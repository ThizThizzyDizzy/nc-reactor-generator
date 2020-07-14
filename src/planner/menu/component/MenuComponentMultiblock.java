package planner.menu.component;
import org.lwjgl.opengl.GL11;
import planner.Core;
import multiblock.Multiblock;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMultiblock extends MenuComponent{
    public final Multiblock multiblock;
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
    });
    public MenuComponentMultiblock(Multiblock multiblock){
        super(0, 0, 0, 100);
        this.multiblock = multiblock;
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        edit.x = width-height/2-height/4;
        edit.y = height/4;
        edit.width = edit.height = height/2;
    }
    @Override
    public void render(){
        if(isMouseOver&&!isSelected)Core.applyAverageColor(Core.theme.getButtonColor(), Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyColor(Core.theme.getTextColor());
        drawText(x, y, x+width, y+height/4, multiblock.getName());
        drawText(x, y+height/4, x+width, y+height/2, multiblock.getDefinitionName());
    }
}