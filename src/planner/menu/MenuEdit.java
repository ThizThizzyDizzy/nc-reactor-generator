package planner.menu;
import org.lwjgl.opengl.Display;
import planner.Core;
import planner.menu.component.MenuComponentEditorBlock;
import planner.menu.component.MenuComponentEditorListBlock;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentMulticolumnMinimaList;
import planner.menu.component.MenuComponentUnderFuel;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import planner.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuEdit extends Menu{
    private final Multiblock multiblock;
    private final MenuComponentMinimalistButton back = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Back", true, true));
    private final MenuComponentMulticolumnMinimaList parts = add(new MenuComponentMulticolumnMinimaList(0, 0, 0, 0, 64, 64, 32));
    private final MenuComponentMinimalistScrollable multibwauk = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 32, 32));
    private final MenuComponentMinimalistButton zoomOut = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "-", true, true));
    private final MenuComponentMinimalistButton zoomIn = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true));
    private final MenuComponentMinimalistButton resize = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Resize", true, true));
    private final MenuComponentMinimaList underFuel = new MenuComponentMinimaList(0, 0, 0, 0, 32);
    private double scale = 4;
    private double minScale = 0.5;
    private double maxScale = 16;
    private int CELL_SIZE = (int) (16*scale);
    private int LAYER_GAP = CELL_SIZE/2;
    public String tooltip = "";
    private int multisPerRow = 0;
    public MenuEdit(GUI gui, Menu parent, Multiblock multiblock){
        super(gui, parent);
        if(multiblock instanceof UnderhaulSFR)add(underFuel);
        this.multiblock = multiblock;
        multibwauk.setScrollMagnitude(CELL_SIZE/2);
        back.addActionListener((e) -> {
            gui.open(new MenuTransition(gui, this, parent, MenuTransition.SlideTransition.slideTo(1, 0), 5));
        });
        resize.addActionListener((e) -> {
            gui.open(new MenuResize(gui, this, multiblock));
        });
        zoomOut.addActionListener((e) -> {
            scale = Math.max(minScale, Math.min(maxScale, scale/1.5));
            CELL_SIZE = (int) (16*scale);
            LAYER_GAP = CELL_SIZE/2;
            onGUIOpened();
        });
        zoomIn.addActionListener((e) -> {
            scale = Math.max(minScale, Math.min(maxScale, scale*1.5));
            CELL_SIZE = (int) (16*scale);
            LAYER_GAP = CELL_SIZE/2;
            onGUIOpened();
        });
        for(Block availableBlock : ((Multiblock<Block>)multiblock).getAvailableBlocks()){
            parts.add(new MenuComponentEditorListBlock(availableBlock));
        }
        for(planner.configuration.underhaul.fissionsfr.Fuel fuel : Core.configuration.underhaul.fissionSFR.fuels){
            underFuel.add(new MenuComponentUnderFuel(fuel));
        }
    }
    @Override
    public void onGUIOpened(){
        multisPerRow = Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)));
        multibwauk.components.clear();
        for(int y = 0; y<multiblock.getY(); y++){
            for(int z = 0; z<multiblock.getZ(); z++){
                for(int x = 0; x<multiblock.getX(); x++){
                    int column = y%multisPerRow;
                    int row = y/multisPerRow;
                    int layerWidth = multiblock.getX()*CELL_SIZE+LAYER_GAP;
                    int layerHeight = multiblock.getZ()*CELL_SIZE+LAYER_GAP;
                    multibwauk.add(new MenuComponentEditorBlock(x*CELL_SIZE+column*layerWidth, z*CELL_SIZE+row*layerHeight, CELL_SIZE, CELL_SIZE, this, multiblock, x, y, z));
                }
            }
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        if(multisPerRow!=Math.max(1, (int)((multibwauk.width-multibwauk.horizScrollbarHeight)/(CELL_SIZE*multiblock.getX()+LAYER_GAP)))){
            onGUIOpened();
        }
        back.width = parts.width = 288;
        back.height = 64;
        parts.y = back.height;
        parts.height = Display.getHeight()-back.height;
        multibwauk.width = (Display.getWidth()-parts.width)/2;
        zoomIn.height = zoomOut.height = resize.height = back.height;
        resize.width = (Display.getWidth()-multibwauk.width-parts.width)/2;
        zoomIn.width = zoomOut.width = resize.width/2;
        resize.x = Display.getWidth()-resize.width;
        zoomOut.x = resize.x-zoomOut.width;
        zoomIn.x = zoomOut.x-zoomIn.width;
        underFuel.x = resize.x;
        underFuel.y = resize.height;
        underFuel.width = resize.width;
        underFuel.height = Display.getHeight()-resize.height;
        for(MenuComponent c : underFuel.components){
            c.width = underFuel.width-underFuel.vertScrollbarWidth;
            c.height = 64;
        }
        if(multiblock instanceof UnderhaulSFR){
            if(underFuel.getSelectedIndex()>-1){
                planner.configuration.underhaul.fissionsfr.Fuel fuel = Core.configuration.underhaul.fissionSFR.fuels.get(underFuel.getSelectedIndex());
                if(((UnderhaulSFR)multiblock).fuel!=fuel){
                    ((UnderhaulSFR)multiblock).fuel = fuel;
                    recalculate();
                }
            }
        }
        multibwauk.x = parts.width;
        multibwauk.height = parts.height;
        super.render(millisSinceLastTick);
        Core.applyColor(Core.theme.getTextColor());
        double ty = resize.height;
        double th = 20;
        String tip = multiblock.getTooltip()+"\n\n"+tooltip;
        String[] strs = tip.split("\n");
        for(int i = 0; i<strs.length; i++){
            String str = strs[i];
            drawText(multibwauk.x+multibwauk.width, ty, resize.x, ty+th, str);
            ty+=th;
        }
    }
    private Block getSelectedBlock(){
        if(parts.getSelectedIndex()==-1)return null;
        return ((MenuComponentEditorListBlock) parts.components.get(parts.getSelectedIndex())).block;
    }
    private MenuComponentEditorBlock dragStart = null;
    public void editorClicked(MenuComponentEditorBlock b, int button, boolean pressed){
        if(pressed){
            Block selected = getSelectedBlock();
            if(button==0)multiblock.blocks[b.blockX][b.blockY][b.blockZ] = selected==null?null:selected.newInstance(b.blockX, b.blockY, b.blockZ);
            else if(button==1)multiblock.blocks[b.blockX][b.blockY][b.blockZ] = null;
            recalculate();
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
                multiblock.blocks[b.blockX][b.blockY][b.blockZ] = setTo==null?null:setTo.newInstance(b.blockX, b.blockY, b.blockZ);
            }else{
                //Y values are equal
                raytrace(dragStart, b, (x,y,z) -> {
                    multiblock.blocks[x][y][z] = setTo==null?null:setTo.newInstance(x, y, z);
                });
            }
            recalculate();
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
    private void recalculate(){
        multiblock.clearData();
        multiblock.calculate();
    }
    private static interface TraceStep{
        public void step(int x, int y, int z);
    }
}