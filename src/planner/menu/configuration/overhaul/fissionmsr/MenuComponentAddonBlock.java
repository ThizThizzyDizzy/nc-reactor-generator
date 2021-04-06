package planner.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionmsr.Block;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentAddonBlock extends MenuComponent{
    public final Block parent;
    public final Block block;
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
    }.setTooltip("Modify block"));
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true, true).setTooltip("Remove block"));
    public MenuComponentAddonBlock(Block parent, Block block){
        super(0, 0, 0, 50);
        this.parent = parent;
        this.block = block;
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        delete.x = width-height/2-height;
        edit.x = delete.x - height*2;
        delete.width = delete.height = edit.width = edit.height = height;
    }
    @Override
    public void render(){
        if(isMouseOver)Core.applyColor(Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyWhite();
        if(parent.texture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(parent.displayTexture));
        Core.applyColor(Core.theme.getTextColor());
        ArrayList<String> strs = new ArrayList<>();
        strs.add(parent.getDisplayName());
        strs.add(block.recipes.size()+" Added Recipe"+(block.recipes.size()==1?"":"s"));
        while(strs.size()<2)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}