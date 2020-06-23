package planner.menu;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentEditorBlock;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuEdit extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMulticolumnMinimaList parts = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 64, 64, 32));
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
    }
    @Override
    public void onGUIOpened(){
        parts.components.clear();
        for(Block availableBlock : multiblock.getAvailableBlocks()){
            parts.add(new MenuComponentEditorBlock(availableBlock));
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        parts.height = Display.getHeight();
        parts.width = 288;
        super.render(millisSinceLastTick);
    }
}