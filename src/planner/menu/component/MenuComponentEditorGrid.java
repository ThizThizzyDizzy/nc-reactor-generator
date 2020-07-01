package planner.menu.component;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import planner.Core;
import planner.menu.MenuEdit;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import planner.multiblock.action.SourceAction;
import planner.multiblock.overhaul.fissionsfr.OverhaulSFR;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorGrid extends MenuComponent{
    private final Multiblock multiblock;
    private final int layer;
    private final MenuEdit editor;
    private int blockSize;
    private int[] mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
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
    public void tick(){
        resonatingTick++;
        if(resonatingTick>resonatingTime)resonatingTick-=resonatingTime;
    }
    @Override
    public void render(int millisSinceLastTick){
        float tick = resonatingTick+(Math.max(0, Math.min(1, millisSinceLastTick/50)));
        resonatingAlpha = (float) (-Math.cos(2*Math.PI*tick/resonatingTime)/(2/(resonatingMax-resonatingMin))+(resonatingMax+resonatingMin)/2);
        super.render(millisSinceLastTick);
    }
    @Override
    public void render(){
        if(!(gui.mouseWereDown.contains(0))){
            editor.getSelectedTool().mouseReset(0);
        }
        if(!(gui.mouseWereDown.contains(1))){
            editor.getSelectedTool().mouseReset(1);
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
                }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)||Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)){
                    if(editor.isValid(editor.getSelectedBlock(), x, layer, z)){
                        editor.getSelectedBlock().render(X, Y, blockSize, blockSize, false, resonatingAlpha);
                    }
                }
            }
        }
        editor.getSelectedTool().drawGhosts(layer, x, y, width, height, blockSize, (editor.getSelectedBlock()==null?0:Core.getTexture(editor.getSelectedBlock().getTexture())));
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
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (y/blockSize)));
        if(isDown){
            if(multiblock instanceof OverhaulSFR&&(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)||Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))&&((planner.multiblock.overhaul.fissionsfr.Block)multiblock.getBlock(blockX, layer, blockZ)).isFuelCell()){
                planner.multiblock.overhaul.fissionsfr.Block b = (planner.multiblock.overhaul.fissionsfr.Block) multiblock.getBlock(blockX, layer, blockZ);
                if(b!=null){
                    int index = Core.configuration.overhaul.fissionSFR.sources.indexOf(b.source);
                    index--;
                    if(index>=Core.configuration.overhaul.fissionSFR.sources.size())index = 0;
                    if(index<-1)index = Core.configuration.overhaul.fissionSFR.sources.size()-1;
                    multiblock.action(new SourceAction(b, index==-1?null:Core.configuration.overhaul.fissionSFR.sources.get(index)));
                }
                editor.recalculate();
            }else{
                editor.getSelectedTool().mousePressed(blockX, layer, blockZ, button);
            }
        }else{
            editor.getSelectedTool().mouseReleased(blockX, layer, blockZ, button);
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
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (y/blockSize)));
        editor.getSelectedTool().mouseDragged(blockX, layer, blockZ, button);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}