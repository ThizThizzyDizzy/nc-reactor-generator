package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
/**
 * Strange name; this is only used in the resize menu
 * @author Thiz
 */
public class MenuComponentVisibleBlock extends Component{
    public final Multiblock multiblock;
    public final BlockPos blockPos;
    public MenuComponentVisibleBlock(int x, int y, int width, int height, Multiblock multiblock, BlockPos blockPos){
        super(x, y, width, height);
        this.multiblock = multiblock;
        this.blockPos = blockPos;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getEditorBackgroundColor());
        renderer.fillRect(x, y, x+width, y+height);
        float border = height/8;
        renderer.setColor(Core.theme.getEditorGridColor());
        renderer.fillRect(x, y, x+width, y+border/4);
        renderer.fillRect(x, y+height-border/4, x+width, y+height);
        renderer.fillRect(x, y+border/4, x+border/4, y+height-border/4);
        renderer.fillRect(x+width-border/4, y+border/4, x+width, y+height-border/4);
        AbstractBlock block = multiblock.getBlock(blockPos);
        if(block==null)return;
        block.render(renderer, x, y, width, height, null, multiblock);
    }
}