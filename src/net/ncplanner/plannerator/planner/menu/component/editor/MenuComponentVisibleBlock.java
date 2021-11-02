package net.ncplanner.plannerator.planner.menu.component.editor;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import simplelibrary.opengl.gui.components.MenuComponent;
/**
 * Strange name; this is only used in the resize menu
 * @author Thiz
 */
public class MenuComponentVisibleBlock extends MenuComponent{
    public final Multiblock multiblock;
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public MenuComponentVisibleBlock(int x, int y, int width, int height, Multiblock multiblock, int blockX, int blockY, int blockZ){
        super(x, y, width, height);
        this.multiblock = multiblock;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getEditorBackgroundColor());
        drawRect(x, y, x+width, y+height, 0);
        double border = height/8;
        renderer.setColor(Core.theme.getEditorGridColor());
        drawRect(x, y, x+width, y+border/4, 0);
        drawRect(x, y+height-border/4, x+width, y+height, 0);
        drawRect(x, y+border/4, x+border/4, y+height-border/4, 0);
        drawRect(x+width-border/4, y+border/4, x+width, y+height-border/4, 0);
        Block block = multiblock.getBlock(blockX, blockY, blockZ);
        if(block==null)return;
        block.render(renderer, x, y, width, height, false, multiblock);
    }
}