package planner.menu;
import org.lwjgl.opengl.Display;
import planner.menu.component.MenuComponentEditorBlock;
import planner.menu.component.MenuComponentEditorListBlock;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuEdit extends Menu{
    private final Multiblock<Block> multiblock;
    private final MenuComponentMulticolumnMinimaList parts = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 64, 64, 32));
    private final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private int CELL_SIZE = 64;
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
    }
    @Override
    public void onGUIOpened(){
        parts.components.clear();
        for(Block availableBlock : multiblock.getAvailableBlocks()){
            parts.add(new MenuComponentEditorListBlock(availableBlock));
        }
        multibwauk.components.clear();
        for(int y = 0; y<multiblock.getY(); y++){
            for(int z = 0; z<multiblock.getZ(); z++){
                for(int x = 0; x<multiblock.getX(); x++){
                    multibwauk.add(new MenuComponentEditorBlock(x*CELL_SIZE, (int)((z+(y*(multiblock.getZ()+.25)))*CELL_SIZE), CELL_SIZE, CELL_SIZE, this, multiblock, x, y, z));
                }
            }
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        parts.height = Display.getHeight();
        parts.width = 288;
        multibwauk.width = (Display.getWidth()-parts.width)/2;
        multibwauk.x = parts.width;
        multibwauk.height = parts.height;
        super.render(millisSinceLastTick);
    }
    private Block getSelectedBlock(){
        if(parts.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorListBlock) parts.components.get(parts.getSelectedIndex())).block;
    }
    private MenuComponentEditorBlock dragStart = null;
    public void editorClicked(MenuComponentEditorBlock b, int button, boolean pressed){
        if(pressed){
            if(button==0)multiblock.blocks[b.blockX][b.blockY][b.blockZ] = getSelectedBlock();
            else if(button==1)multiblock.blocks[b.blockX][b.blockY][b.blockZ] = null;
            dragStart = b;
        }else{
            dragStart = null;
        }
    }
    public void editorDragged(MenuComponentEditorBlock b, int button){
        if(button!=0&&button!=1){
            return;
        }
        if(dragStart!=null){
            Block setTo = button==0?getSelectedBlock():null;
            if(dragStart.blockY!=b.blockY){
                multiblock.blocks[b.blockX][b.blockY][b.blockZ] = setTo;
            }else{
                //Y values are equal
                raytrace(dragStart, b, (x,y,z) -> {
                    multiblock.blocks[x][y][z] = setTo;
                });
            }
            dragStart = b;
        }
    }
    public void raytrace(MenuComponentEditorBlock from, MenuComponentEditorBlock to, TraceStep step){
        int xDiff = to.blockX-from.blockX;
        int yDiff = to.blockY-from.blockY;
        int zDiff = to.blockZ-from.blockZ;
        double dist = Math.sqrt(Math.pow(from.blockX-to.blockX, 2)+Math.pow(from.blockY-to.blockY, 2)+Math.pow(from.blockZ-to.blockZ, 2));
        for(float r = 0; r<1; r+=.25/dist){
            step.step(Math.round(from.blockX+xDiff*r), Math.round(from.blockY+yDiff*r), Math.round(from.blockZ+zDiff*r));
        }
    }
    private static interface TraceStep{
        public void step(int x, int y, int z);
    }
}