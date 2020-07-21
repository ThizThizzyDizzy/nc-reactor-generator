package planner.menu.component;
import java.awt.Color;
import planner.Core;
import multiblock.Block;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentToggleBlock extends MenuComponent{
    public final Block block;
    public boolean enabled = true;
    public int min = 0;
    public int max = 0;
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
        if(Core.isControlPressed()){
            String text;
            if(max==0){
                text = min+"+";
            }else if(min==max){
                text = min+"";
            }else{
                text = min+"-"+max;
            }
            Core.applyColor(Core.theme.getTextColor());
            drawText(text);
        }
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        int trueWheelChange = Core.calcWheelChange(wheelChange);
        if(isMouseOver&&Core.isControlPressed()){
            if(Core.isShiftPressed()){
                min+=trueWheelChange;
            }else{
                max+=trueWheelChange;
            }
            if(min<0)min = 0;
            if(max<0)max = 0;
            if(min>max)min = max;
        }
        return super.mouseWheelChange(wheelChange);
    }
    public void drawText(){
        if(enabled)block.render(x, y, width, height, false);
        else block.renderGrayscale(x, y, width, height, false);
    }
    public void drawText(String text){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(button==0&&pressed)enabled = !enabled;
    }
}