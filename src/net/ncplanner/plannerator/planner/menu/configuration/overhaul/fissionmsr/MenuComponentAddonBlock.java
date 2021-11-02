package net.ncplanner.plannerator.planner.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import org.lwjgl.opengl.GL11;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentAddonBlock extends MenuComponent{
    public final Block parent;
    public final Block block;
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
    }.setTooltip("Modify block"));
    public final MenuComponentMinimalistButton delete = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true, true){
        @Override
        public void action(){
            onDeletePressed.run();
        }
    }.setTooltip("Remove block"));
    private final Runnable onEditPressed;
    private final Runnable onDeletePressed;
    public MenuComponentAddonBlock(Block parent, Block block, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 50);
        this.parent = parent;
        this.block = block;
        this.onEditPressed = onEditPressed;
        this.onDeletePressed = onDeletePressed;
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
        Renderer renderer = new Renderer();
        if(isMouseOver)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(parent.texture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(parent.displayTexture));
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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