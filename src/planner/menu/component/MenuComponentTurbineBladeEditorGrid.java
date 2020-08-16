package planner.menu.component;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import multiblock.overhaul.turbine.OverhaulTurbine;
import simplelibrary.opengl.Renderer2D;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTurbineBladeEditorGrid extends MenuComponent{
    private final OverhaulTurbine multiblock;
    private final MenuEdit editor;
    private int blockSize;
    private int mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    public MenuComponentTurbineBladeEditorGrid(int x, int y, int blockSize, MenuEdit editor, OverhaulTurbine multiblock){
        super(x, y, blockSize*(multiblock.getZ()-2), blockSize);
        this.multiblock = multiblock;
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
        if(!isMouseOver)mouseover = -1;
        if(mouseover!=-1){
            if(mouseover<1||mouseover>=multiblock.getZ()-1)mouseover = -1;
        }
        blockSize = (int) width/(multiblock.getZ()-2);
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(x,y,x+width,y+height,0);
        if(mouseover!=-1){
            Core.applyColor(Core.theme.getEditorListBorderColor().brighter());
            drawRect(x+(mouseover-1)*blockSize, y, x+mouseover*blockSize, y+blockSize, 0);
        }
        Core.applyColor(Core.theme.getTextColor());
        for(int z = 0; z<=multiblock.getZ(); z++){
            double border = blockSize/32d;
            double Z = this.x+z*blockSize;
            drawRect(Z-(z==0?0:border), y, Z+(z==multiblock.getX()?0:border), y+height, 0);
        }
//        for(int z = 0; z<=multiblock.getZ(); z++){
//            double border = blockSize/32d;
//            double Y = this.y+z*blockSize;
//            drawRect(x, Y-(z==0?0:border), x+width, Y+(z==multiblock.getZ()?0:border), 0);
//        }
        for(int z = 1; z<multiblock.getZ()-1; z++){
            Block block = multiblock.getBlock(multiblock.getX()/2, 0, z);
            double X = this.x+(z-1)*blockSize;
            if(block!=null){
                block.render(X, y, blockSize, blockSize, true);
            }
            if(Core.isControlPressed()){
                if(block==null||(Core.isShiftPressed()&&block.canBeQuickReplaced())){
                    if(editor.isValid(editor.getSelectedBlock(), multiblock.getX()/2, 0, z)){
                        editor.getSelectedBlock().render(X, y, blockSize, blockSize, false, resonatingAlpha);
                    }
                }
            }
            if(isSelected(z)){
                Core.applyColor(Core.theme.getSelectionColor(), .5f);
                Renderer2D.drawRect(X, y, X+blockSize, y+blockSize, 0);
                Core.applyColor(Core.theme.getSelectionColor());
                double border = blockSize/8f;
                boolean right = isSelected(z+1);
                boolean left = isSelected(z-1);
                //top
                Renderer2D.drawRect(X, y, X+border, y+border, 0);
                Renderer2D.drawRect(X+border, y, X+blockSize-border, y+border, 0);
                Renderer2D.drawRect(X+blockSize-border, y, X+blockSize, y+border, 0);
                if(!right){//right
                    Renderer2D.drawRect(X+blockSize-border, y+border, X+blockSize, y+blockSize-border, 0);
                }
                //bottom
                Renderer2D.drawRect(X+blockSize-border, y+blockSize-border, X+blockSize, y+blockSize, 0);
                Renderer2D.drawRect(X+border, y+blockSize-border, X+blockSize-border, y+blockSize, 0);
                Renderer2D.drawRect(X, y+blockSize-border, X+border, y+blockSize, 0);
                if(!left){//left
                    Renderer2D.drawRect(X, y+border, X+border, y+blockSize-border, 0);
                }
            }
        }
        editor.getSelectedTool().drawBladeGhosts(x, y, width, height, blockSize, (editor.getSelectedBlock()==null?0:Core.getTexture(editor.getSelectedBlock().getTexture())));
        if(mouseover!=-1){
            Block block = multiblock.getBlock(multiblock.getX()/2, 0, mouseover);
            double X = this.x+(mouseover-1)*blockSize;
            double border = blockSize/8;
            editor.setTooltip((block==null?"":block.getTooltip()));
            Core.applyColor(Core.theme.getEditorListBorderColor(), .6375f);
            drawRect(X, y, X+border, y+border, 0);
            drawRect(X+blockSize-border, y, X+blockSize, y+border, 0);
            drawRect(X, y+blockSize-border, X+border, y+blockSize, 0);
            drawRect(X+blockSize-border, y+blockSize-border, X+blockSize, y+blockSize, 0);
            Core.applyColor(Core.theme.getTextColor(), .6375f);
            drawRect(X+border, y, X+blockSize-border, y+border, 0);
            drawRect(X+border, y+blockSize-border, X+blockSize-border, y+blockSize, 0);
            drawRect(X, y+border, X+border, y+blockSize-border, 0);
            drawRect(X+blockSize-border, y+border, X+blockSize, y+blockSize-border, 0);
        }
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(isMouseOver)mouseover = ((int)x/blockSize)+1;
        else mouseover = -1;
    }
    @Override
    public void mouseEvent(double x, double y, int button, boolean isDown){
        super.mouseEvent(x, y, button, isDown);
        int blockZ = Math.max(1, Math.min(multiblock.getZ()-2, (int) (x/blockSize)+1));
        if(isDown){
            if(button==2){
                editor.setSelectedBlock(multiblock.getBlock(multiblock.getX()/2, 0, blockZ));
            }
            editor.getSelectedTool().mousePressed(this, multiblock.getX()/2, 0, blockZ, button);
        }else{
            editor.getSelectedTool().mouseReleased(this, multiblock.getX()/2, 0, blockZ, button);
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
        int blockZ = Math.max(1, Math.min(multiblock.getZ()-2, (int) (x/blockSize)+1));
        editor.getSelectedTool().mouseDragged(this, multiblock.getX()/2, 0, blockZ, button);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
    public boolean isSelected(int z){
        return editor.isSelected(multiblock.getX()/2, 0, z);
    }
}