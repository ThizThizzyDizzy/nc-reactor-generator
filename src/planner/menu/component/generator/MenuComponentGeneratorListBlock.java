package planner.menu.component.generator;
import multiblock.Block;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.menu.MenuGenerator;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentGeneratorListBlock extends MenuComponent{//TODO incomplete
    private final MenuGenerator generator;
    public final Block block;
    public boolean enabled = true;
    public int min = 0;
    public int max = 0;
    public MenuComponentGeneratorListBlock(MenuGenerator generator, Block block){
        super(0, 0, 0, 0);
        this.generator = generator;
        this.block = block;
        enabled = block.defaultEnabled();
    }
    @Override
    public void render(){
        Color col = isMouseOver?Core.theme.getEditorListBackgroundMouseoverColor(Core.getThemeIndex(this)):Core.theme.getEditorListBackgroundColor(Core.getThemeIndex(this));
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        drawText();
        double border = height/8;
        if(!enabled){
            Core.applyColor(Core.theme.getToggleBlockFadeout(Core.getThemeIndex(this)));
            drawRect(x, y, x+width, y+height, 0);
        }
        if(isMouseOver){
            Core.applyColor(Core.theme.getEditorListLightMouseoverColor(Core.getThemeIndex(this)), .6375f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            Core.applyColor(Core.theme.getEditorListDarkMouseoverColor(Core.getThemeIndex(this)), .6375f);
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
            Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            drawText(text);
        }
    }
    @Override
    public boolean onMouseScrolled(double x, double y, double dx, double dy){
        if(super.onMouseScrolled(x, y, dx, dy))return true;
        if(isMouseOver&&Core.isControlPressed()){
            if(Core.isShiftPressed()){
                if(dy<0&&min+dy<max){
                    min = 0;
                }else{
                    min+=dy;
                }
            }else{
                max+=dy;
            }
            if(min<0)min = 0;
            if(max<0)max = 0;
            if(min>max&&max!=0)min = max;
            return true;
        }
        return false;
    }
    public void drawText(){
        if(enabled)block.render(x, y, width, height, false, null);
        else block.renderGrayscale(x, y, width, height, false, null);
    }
    public void drawText(String text){
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed)enabled = true;
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&pressed)enabled = false;
    }
}