package planner.menu.component;
import java.awt.Color;
import planner.Core;
import multiblock.Block;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentToggleBlock extends MenuComponent{
    public final Block block;
    public boolean enabled = true;
    public MenuComponentToggleBlock(Block block){
        super(0, 0, 0, 0);
        this.block = block;
        enabled = block.defaultEnabled();
    }
    @Override
    public void render(){
        Color col = Core.theme.getEditorListBorderColor();
        if(isMouseOver)col = col.brighter();
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
        double border = height/8;
        if(!enabled){
            Core.applyColor(Core.theme.getFadeout());
            drawRect(x, y, x+width, y+height, 0);
        }
        if(isMouseOver){
            Core.applyColor(Core.theme.getEditorListBorderColor(), .6375f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            Core.applyColor(Core.theme.getTextColor(), .6375f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
    }
    public void drawText(){
        if(enabled)block.render(x, y, width, height, false);
        else block.renderGrayscale(x, y, width, height, false);
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(button==0&&pressed)enabled = !enabled;
    }
}