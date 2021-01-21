package planner.menu.component.editor;
import java.awt.Color;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import multiblock.overhaul.turbine.OverhaulTurbine;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
import static simplelibrary.opengl.Renderer2D.drawRect;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentTurbineBladeEditorGrid extends MenuComponent{
    private final OverhaulTurbine multiblock;
    private final MenuEdit editor;
    public int blockSize;
    private int mouseover;
    private static final int resonatingTime = 60;
    private static final float resonatingMin = .25f;
    private static final float resonatingMax = .5f;
    private int resonatingTick = 0;
    private float resonatingAlpha = 0;
    public MenuComponentTurbineBladeEditorGrid(int x, int y, int blockSize, MenuEdit editor, OverhaulTurbine multiblock){
        super(x, y, blockSize*(multiblock.getZ()-2), blockSize*7);
        this.multiblock = multiblock;
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
        if(!isMouseOver)mouseover = -1;
        if(mouseover!=-1){
            if(mouseover<1||mouseover>=multiblock.getZ()-1)mouseover = -1;
        }
        blockSize = (int) width/(multiblock.getZ()-2);
        Core.applyColor(Core.theme.getEditorListBorderColor());
        drawRect(x,y,x+width,y+blockSize,0);
        if(mouseover!=-1){
            Core.applyColor(Core.theme.getEditorListBorderColor().brighter());
            drawRect(x+(mouseover-1)*blockSize, y, x+mouseover*blockSize, y+blockSize, 0);
        }
        Core.applyColor(Core.theme.getTextColor());
        for(int z = 0; z<=multiblock.getZ(); z++){
            double border = blockSize/32d;
            double Z = this.x+z*blockSize;
            drawRect(Z-(z==0?0:border), y, Z+(z==multiblock.getX()?0:border), y+blockSize, 0);
        }
        double brdr = blockSize/32d;
        drawRect(x, y, x+width, y+brdr, 0);
        drawRect(x, y+blockSize-brdr, x+width, y+blockSize, 0);
        for(int z = 1; z<multiblock.getZ()-1; z++){
            Block block = multiblock.getBlock(multiblock.getX()/2, 0, z);
            double X = this.x+(z-1)*blockSize;
            if(block!=null){
                block.render(X, y, blockSize, blockSize, true, multiblock);
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
        editor.getSelectedTool().drawBladeGhosts(x, y, width, blockSize, blockSize, (editor.getSelectedBlock(0)==null?0:Core.getTexture(editor.getSelectedBlock(0).getTexture())));
        if(mouseover!=-1){
            double X = this.x+(mouseover-1)*blockSize;
            double border = blockSize/8;
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
        if(multiblock.bladesValid){
            Core.applyWhite();
            drawRect(x, y+blockSize, x+width, y+blockSize*5, editor.turbineGraph.getTexture());
            double wideScale = 1;
            double len = FontManager.getLengthForStringWithHeight("Actual Expansion", blockSize/2);
            wideScale = Math.min(wideScale, width/len);
            drawText(x, y+blockSize*5.5, x+width, y+blockSize*(5.5+wideScale/2), "Actual Expansion");
            Core.applyColor(new Color(31,63,255));
            drawText(x, y+blockSize*5, x+width, y+blockSize*(5+wideScale/2), "Ideal Expansion");
            double blockWidth = width/10;
            float tint = .75f;
            for(int i = 0; i<10; i++){
                int I = 9-i;
                String text = ">"+I*10+"%";
                double scale = 1;
                double slen = FontManager.getLengthForStringWithHeight(text.length()==2?"0"+text:text, blockSize)+1;
                scale = Math.min(scale, blockWidth/slen);
                if(scale<.25){
                    text = I*10+"%";
                    scale = 1;
                    slen = FontManager.getLengthForStringWithHeight(text.length()==2?"0"+text:text, blockSize)+1;
                    scale = Math.min(scale, blockWidth/slen);
                }
                float eff = I/10f;
                GL11.glColor4d(tint*Math.max(0,Math.min(1,-Math.abs(3*eff-1.5)+1.5)), tint*Math.max(0,Math.min(1,3*eff-1)), 0, 1);
                drawRect(x+i*blockWidth, y+blockSize*6, x+(i+1)*blockWidth, y+blockSize*7, 0);
                Core.applyWhite();
                drawText(x+i*blockWidth, y+blockSize*6, x+(i+1)*blockWidth, y+blockSize*(6+scale), text);
            }
        }
    }
    @Override
    public void onMouseMove(double x, double y){
        super.onMouseMove(x, y);
        mouseover = ((int)x/blockSize)+1;
        for(int i : gui.mouseWereDown){
            mouseDragged(x, y, i);
        }
        if(Double.isNaN(x)||Double.isNaN(y)){
            return;
        }
        int blockZ = Math.max(0, Math.min(multiblock.getZ()-1, (int) (x/blockSize)));
        editor.getSelectedTool().mouseMoved(selected, multiblock.getX()/2, 0, blockZ);
    }
    @Override
    public void onMouseMovedElsewhere(double x, double y){
        super.onMouseMovedElsewhere(x, y);
        if(mouseover!=-1)editor.getSelectedTool().mouseMovedElsewhere(selected);
        mouseover = -1;
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(Double.isNaN(x)||Double.isNaN(y)){
            return;
        }
        int blockZ = Math.max(1, Math.min(multiblock.getZ()-2, (int) (x/blockSize)+1));
        if(pressed){
            if(button==GLFW.GLFW_MOUSE_BUTTON_MIDDLE){
                editor.setSelectedBlock(multiblock.getBlock(multiblock.getX()/2, 0, blockZ));
            }
            editor.getSelectedTool().mousePressed(this, multiblock.getX()/2, 0, blockZ, button);
        }else{
            editor.getSelectedTool().mouseReleased(this, multiblock.getX()/2, 0, blockZ, button);
        }
    }
    public void mouseDragged(double x, double y, int button){
        if(button!=0&&button!=1)return;
        int blockZ = Math.max(1, Math.min(multiblock.getZ()-2, (int) (x/blockSize)+1));
        editor.getSelectedTool().mouseDragged(this, multiblock.getX()/2, 0, blockZ, button);
    }
    public boolean isSelected(int z){
        return editor.isSelected(0, multiblock.getX()/2, 0, z);
    }
    @Override
    public String getTooltip(){
        if(mouseover==-1)return null;
        Block block = multiblock.getBlock(multiblock.getX()/2, 0, mouseover);
        return block==null?null:block.getTooltip(multiblock);
    }
    @Override
    public double getTooltipOffsetX(){
        return mouseover==-1?0:mouseover*blockSize;
    }
    @Override
    public double getTooltipOffsetY(){
        return mouseover==-1?height:blockSize;
    }
}