package planner.menu.component;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import multiblock.action.SetBearingDiameterAction;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.glfw.GLFW;
import planner.menu.MenuComponentTooltip;
import simplelibrary.opengl.Renderer2D;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTurbineCoilEditorGrid extends MenuComponent implements MenuComponentTooltip{
    private final OverhaulTurbine multiblock;
    private final int layer;
    private final MenuEdit editor;
    private int blockSize;
    private int[] mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    public MenuComponentTurbineCoilEditorGrid(int x, int y, int blockSize, MenuEdit editor, OverhaulTurbine multiblock, int layer){
        super(x, y, blockSize*multiblock.getX(), blockSize*multiblock.getY());
        this.multiblock = multiblock;
        this.layer = layer==1?multiblock.getZ()-1:layer;
        this.editor = editor;
        this.blockSize = blockSize;
    }
    @Override
    public void tick(){
        if(!(gui.mouseWereDown.contains(0))){
            editor.getSelectedTool().mouseReset(0);
        }
        if(!(gui.mouseWereDown.contains(1))){
            editor.getSelectedTool().mouseReset(1);
        }
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
        if(!isMouseOver)mouseover = null;
        if(mouseover!=null){
            if(mouseover[0]<0||mouseover[1]<0||mouseover[0]>=multiblock.getX()||mouseover[1]>=multiblock.getY())mouseover = null;
        }
        blockSize = (int) Math.min(width/multiblock.getX(), height/multiblock.getY());
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(x,y,x+width,y+height,0);
        if(mouseover!=null){
            Core.applyColor(Core.theme.getEditorListBorderColor().brighter());
            drawRect(x+mouseover[0]*blockSize, y+mouseover[1]*blockSize, x+(mouseover[0]+1)*blockSize, y+(mouseover[1]+1)*blockSize, 0);
        }
        Core.applyColor(Core.theme.getTextColor());
        for(int x = 0; x<=multiblock.getX(); x++){
            double border = blockSize/32d;
            double X = this.x+x*blockSize;
            drawRect(X-(x==0?0:border), y, X+(x==multiblock.getX()?0:border), y+height, 0);
        }
        for(int y = 0; y<=multiblock.getY(); y++){
            double border = blockSize/32d;
            double Y = this.y+y*blockSize;
            drawRect(x, Y-(y==0?0:border), x+width, Y+(y==multiblock.getZ()?0:border), 0);
        }
        for(int x = 0; x<multiblock.getX(); x++){
            for(int y = 0; y<multiblock.getY(); y++){
                Block block = multiblock.getBlock(x, y, layer);
                double X = this.x+x*blockSize;
                double Y = this.y+y*blockSize;
                double border = blockSize/8;
                if(block!=null){
                    block.render(X, Y, blockSize, blockSize, true);
                }
                if(Core.isControlPressed()){
                    if(block==null||(Core.isShiftPressed()&&block.canBeQuickReplaced())){
                        if(editor.isValid(editor.getSelectedBlock(), x, y, layer)){
                            editor.getSelectedBlock().render(X, Y, blockSize, blockSize, false, resonatingAlpha);
                        }
                    }
                }
                if(isSelected(x, y)){
                    Core.applyColor(Core.theme.getSelectionColor(), .5f);
                    Renderer2D.drawRect(X, Y, X+blockSize, Y+blockSize, 0);
                    Core.applyColor(Core.theme.getSelectionColor());
                    border = blockSize/8f;
                    boolean top = isSelected(x, y-1);
                    boolean right = isSelected(x+1, y);
                    boolean bottom = isSelected(x, y+1);
                    boolean left = isSelected(x-1, y);
                    if(!top||!left||!isSelected(x-1, y-1)){//top left
                        Renderer2D.drawRect(X, Y, X+border, Y+border, 0);
                    }
                    if(!top){//top
                        Renderer2D.drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
                    }
                    if(!top||!right||!isSelected(x+1, y-1)){//top right
                        Renderer2D.drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
                    }
                    if(!right){//right
                        Renderer2D.drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
                    }
                    if(!bottom||!right||!isSelected(x+1, y+1)){//bottom right
                        Renderer2D.drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
                    }
                    if(!bottom){//bottom
                        Renderer2D.drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
                    }
                    if(!bottom||!left||!isSelected(x-1, y+1)){//bottom left
                        Renderer2D.drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
                    }
                    if(!left){//left
                        Renderer2D.drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
                    }
                }
            }
        }
        editor.getSelectedTool().drawCoilGhosts(layer, x, y, width, height, blockSize, (editor.getSelectedBlock()==null?0:Core.getTexture(editor.getSelectedBlock().getTexture())));
        if(mouseover!=null){
            double X = this.x+mouseover[0]*blockSize;
            double Y = this.y+mouseover[1]*blockSize;
            double border = blockSize/8;
            Core.applyColor(Core.theme.getEditorListBorderColor(), .6375f);
            drawRect(X, Y, X+border, Y+border, 0);
            drawRect(X+blockSize-border, Y, X+blockSize, Y+border, 0);
            drawRect(X, Y+blockSize-border, X+border, Y+blockSize, 0);
            drawRect(X+blockSize-border, Y+blockSize-border, X+blockSize, Y+blockSize, 0);
            Core.applyColor(Core.theme.getTextColor(), .6375f);
            drawRect(X+border, Y, X+blockSize-border, Y+border, 0);
            drawRect(X+border, Y+blockSize-border, X+blockSize-border, Y+blockSize, 0);
            drawRect(X, Y+border, X+border, Y+blockSize-border, 0);
            drawRect(X+blockSize-border, Y+border, X+blockSize, Y+blockSize-border, 0);
        }
    }
    @Override
    public void onMouseMove(double x, double y){
        super.onMouseMove(x, y);
        mouseover = new int[]{(int)x/blockSize,(int)y/blockSize};
        for(int i : gui.mouseWereDown){
            mouseDragged(x, y, i);
        }
    }
    @Override
    public void onMouseMovedElsewhere(double x, double y){
        super.onMouseMovedElsewhere(x, y);
        mouseover = null;
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(Double.isNaN(x)||Double.isNaN(y)){
            return;
        }
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockY = Math.max(0, Math.min(multiblock.getY()-1, (int) (y/blockSize)));
        if(pressed){
            if(editor.getSelectedBlock() instanceof multiblock.overhaul.turbine.Block){
                multiblock.overhaul.turbine.Block b = (multiblock.overhaul.turbine.Block)editor.getSelectedBlock();
                if(b.isBearing()&&button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
                    int dist = Math.min(Math.min(blockX,multiblock.getX()-blockX-1), Math.min(blockY,multiblock.getY()-blockY-1));
                    if(dist==0)return;
                    multiblock.action(new SetBearingDiameterAction(multiblock.getX()-dist*2));
                    return;
                }
            }
            if(button==GLFW.GLFW_MOUSE_BUTTON_MIDDLE){
                editor.setSelectedBlock(multiblock.getBlock(blockX, blockY, layer));
            }
            editor.getSelectedTool().mousePressed(this, blockX, blockY, layer, button);
        }else{
            if(editor.getSelectedBlock() instanceof multiblock.overhaul.turbine.Block){
                multiblock.overhaul.turbine.Block b = (multiblock.overhaul.turbine.Block)editor.getSelectedBlock();
                if(b.isBearing()){
                    return;
                }
            }
            editor.getSelectedTool().mouseReleased(this, blockX, blockY, layer, button);
        }
    }
    public void mouseDragged(double x, double y, int button){
        if(button!=0&&button!=1)return;
        int blockX = Math.max(0, Math.min(multiblock.getX()-1, (int) (x/blockSize)));
        int blockY = Math.max(0, Math.min(multiblock.getY()-1, (int) (y/blockSize)));
        if(editor.getSelectedBlock() instanceof multiblock.overhaul.turbine.Block){
            multiblock.overhaul.turbine.Block b = (multiblock.overhaul.turbine.Block)editor.getSelectedBlock();
            if(b.isBearing()){
                return;
            }
        }
        editor.getSelectedTool().mouseDragged(this, blockX, blockY, layer, button);
    }
    public boolean isSelected(int x, int y){
        return editor.isSelected(x,y,layer);
    }
    @Override
    public String getTooltip(){
        if(mouseover==null)return null;
        Block block = multiblock.getBlock(mouseover[0],mouseover[1],layer);
        return block==null?null:block.getTooltip();
    }
    @Override
    public double getTooltipOffsetX(){
        return mouseover!=null?mouseover[0]*blockSize:0;
    }
    @Override
    public double getTooltipOffsetY(){
        return mouseover!=null?(mouseover[1]+1)*blockSize:height;
    }
}