package net.ncplanner.plannerator.planner.gui.menu.component.generator;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentToggleBlock extends Component{
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
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        Color col = isMouseFocused?Core.theme.getEditorListBackgroundMouseoverColor(Core.getThemeIndex(this)):Core.theme.getEditorListBackgroundColor(Core.getThemeIndex(this));
        renderer.setColor(col);
        renderer.fillRect(x, y, x+width, y+height);
        drawText();
        float border = height/8;
        if(!enabled){
            renderer.setColor(Core.theme.getToggleBlockFadeout(Core.getThemeIndex(this)));
            renderer.fillRect(x, y, x+width, y+height);
        }
        if(isMouseFocused){
            renderer.setColor(Core.theme.getEditorListLightMouseoverColor(Core.getThemeIndex(this)), .6375f);
            renderer.fillRect(x, y, x+border, y+border);
            renderer.fillRect(x+width-border, y, x+width, y+border);
            renderer.fillRect(x, y+height-border, x+border, y+height);
            renderer.fillRect(x+width-border, y+height-border, x+width, y+height);
            renderer.setColor(Core.theme.getEditorListDarkMouseoverColor(Core.getThemeIndex(this)), .6375f);
            renderer.fillRect(x+border, y, x+width-border, y+border);
            renderer.fillRect(x+border, y+height-border, x+width-border, y+height);
            renderer.fillRect(x, y+border, x+border, y+height-border);
            renderer.fillRect(x+width-border, y+border, x+width, y+height-border);
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
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            drawText(renderer, text);
        }
    }
    @Override
    public boolean onScroll(double dx, double dy){
        if(super.onScroll(dx, dy))return true;
        if(isMouseFocused&&Core.isControlPressed()){
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
        Renderer renderer = new Renderer();
        if(enabled)block.render(renderer, x, y, width, height, null, null);
        else block.renderGrayscale(renderer, x, y, width, height, null, null);
    }
    public void drawText(Renderer renderer, String text){
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, width/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS)enabled = true;
        if(button==GLFW_MOUSE_BUTTON_RIGHT&&action==GLFW_PRESS)enabled = false;
    }
}