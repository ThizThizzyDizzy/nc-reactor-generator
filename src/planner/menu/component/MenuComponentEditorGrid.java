package planner.menu.component;
import org.lwjgl.input.Keyboard;
import planner.Core;
import planner.menu.MenuEdit;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import planner.multiblock.overhaul.fissionsfr.OverhaulSFR;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorGrid extends MenuComponent{
    private final Multiblock multiblock;
    private final int layer;
    private final MenuEdit editor;
    private int blockSize;
    private int[] dragStart;//TODO ADD MORE TOOLS; NOT JUST PENCIL!
    private int[] mouseover;
    public MenuComponentEditorGrid(int x, int y, int blockSize, MenuEdit editor, Multiblock multiblock, int layer){
        super(x, y, blockSize*multiblock.getX(), blockSize*multiblock.getZ());
        this.multiblock = multiblock;
        color = Core.theme.getEditorListBorderColor();
        foregroundColor = Core.theme.getTextColor();
        this.layer = layer;
        this.editor = editor;
        this.blockSize = blockSize;
    }
    @Override
    public void render(){
        if(!(gui.mouseWereDown.contains(0)||gui.mouseWereDown.contains(1))){
            dragStart = null;
        }
        if(!isMouseOver)mouseover = null;
        if(mouseover!=null){
            if(mouseover[0]<0||mouseover[1]<0||mouseover[0]>=multiblock.getX()||mouseover[1]>=multiblock.getZ())mouseover = null;
        }
        blockSize = (int) Math.min(width/multiblock.getX(), height/multiblock.getZ());
        Core.applyColor(color);
        drawRect(x,y,x+width,y+height,0);
        if(mouseover!=null){
            Core.applyColor(color.brighter());
            drawRect(x+mouseover[0]*blockSize, y+mouseover[1]*blockSize, x+(mouseover[0]+1)*blockSize, y+(mouseover[1]+1)*blockSize, 0);
        }
        for(int x = 0; x<multiblock.getX(); x++){
            for(int z = 0; z<multiblock.getZ(); z++){
                Block block = multiblock.getBlock(x, layer, z);
                double X = this.x+x*blockSize;
                double Y = this.y+z*blockSize;
                double border = blockSize/8;
                Core.applyColor(foregroundColor);
                drawRect(X, Y, X+blockSize, Y+border/4, 0);
                drawRect(X, Y+blockSize-border/4, X+blockSize, Y+blockSize, 0);
                drawRect(X, Y+border/4, X+border/4, Y+blockSize-border/4, 0);
                drawRect(X+blockSize-border/4, Y+border/4, X+blockSize, Y+blockSize-border/4, 0);
                if(block!=null){
                    block.render(X, Y, blockSize, blockSize, true);
                }
            }
        }
        if(mouseover!=null){
            Block block = multiblock.getBlock(mouseover[0],layer,mouseover[1]);
            double X = this.x+mouseover[0]*blockSize;
            double Y = this.y+mouseover[1]*blockSize;
            double border = blockSize/8;
            editor.setTooltip((block==null?"":block.getTooltip()));
            Core.applyColor(color, .6375f);
            drawRect(X, Y, X+border, Y+border, 0);
            drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
            drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
            drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
            Core.applyColor(foregroundColor, .6375f);
            drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
            drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
            drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
            drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
        }
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(isMouseOver)mouseover = new int[]{(int)x/blockSize,(int)y/blockSize};
        else mouseover = null;
    }
    @Override
    public void mouseEvent(double x, double y, int button, boolean isDown){
        super.mouseEvent(x, y, button, isDown);
        int blockX = (int) (x/blockSize);
        int blockZ = (int) (y/blockSize);
        if(isDown){
            if(multiblock instanceof OverhaulSFR&&(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)||Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))&&((planner.multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ)).isFuelCell()){
                planner.multiblock.overhaul.fissionsfr.Block b = (planner.multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, layer, blockZ);
                if(b!=null){
                    int index = Core.configuration.overhaul.fissionSFR.sources.indexOf(b.source);
                    index--;
                    if(index>=Core.configuration.overhaul.fissionSFR.sources.size())index = 0;
                    if(index<-1)index = Core.configuration.overhaul.fissionSFR.sources.size()-1;
                    b.source = index==-1?null:Core.configuration.overhaul.fissionSFR.sources.get(index);
                }
            }else{
                Block selected = editor.getSelectedBlock();
                if(button==0||button==1)editor.setblock(blockX,layer,blockZ,button==0?selected:null);
            }
            editor.recalculate();
            dragStart = new int[]{blockX,blockZ};
        }else{
            dragStart = null;
        }
    }
    @Override
    public void mouseover(double x, double y, boolean isMouseOver){
        super.mouseover(x, y, isMouseOver);
    }
    @Override
    public void mouseDragged(double x, double y, int button){
        super.mouseDragged(x, y, button);
        if(button!=0&&button!=1)return;
        int blockX = (int) (x/blockSize);
        int blockZ = (int) (y/blockSize);
        if(dragStart!=null){
            if(dragStart[0]==blockX&&dragStart[1]==blockZ)return;
            Block setTo = button==0?editor.getSelectedBlock():null;
            raytrace(dragStart[0], dragStart[1], blockX, blockZ, (X,Z) -> {
                editor.setblock(X, layer, Z, setTo);
            });
            editor.recalculate();
            dragStart = new int[]{blockX,blockZ};
        }
    }
    public void raytrace(int fromX, int fromZ, int toX, int toZ, TraceStep step){
        int xDiff = toX-fromX;
        int zDiff = toZ-fromZ;
        double dist = Math.sqrt(Math.pow(fromX-toX, 2)+Math.pow(fromZ-toZ, 2));
        for(float r = 0; r<1; r+=.25/dist){
            step.step(Math.round(fromX+xDiff*r), Math.round(fromZ+zDiff*r));
        }
    }
    private static interface TraceStep{
        public void step(int x, int z);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}