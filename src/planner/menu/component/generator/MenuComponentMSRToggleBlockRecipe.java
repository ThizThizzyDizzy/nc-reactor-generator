package planner.menu.component.generator;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMSRToggleBlockRecipe extends MenuComponent{
    public final BlockRecipe recipe;
    public boolean enabled = false;
    public int min = 0;
    public int max = 0;
    public MenuComponentMSRToggleBlockRecipe(BlockRecipe recipe){
        super(0, 0, 0, 32);
        this.recipe = recipe;
    }
    @Override
    public void render(){
        if(isSelected){
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        if(recipe.inputTexture!=null){
            Core.applyWhite();
            drawRect(x, y, x+height, y+height, Core.getTexture(recipe.inputDisplayTexture));
        }
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(recipe.getInputDisplayName());
    }
    public void drawText(String text){
        if(Core.isControlPressed()){
            if(max==0){
                text = min+"+";
            }else if(min==max){
                text = min+"";
            }else{
                text = min+"-"+max;
            }
        }
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-(recipe.inputTexture!=null?height:0))/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(recipe.inputTexture!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public boolean onMouseScrolled(double x, double y, double dx, double dy){
        if(super.onMouseScrolled(x, y, dx, dy))return true;
        if(isMouseOver&&Core.isControlPressed()){
            if(Core.isShiftPressed()){
                min+=dy;
            }else{
                max+=dy;
            }
            if(min<0)min = 0;
            if(max<0)max = 0;
            if(min>max)min = max;
            return true;
        }
        return false;
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed){
            enabled = !enabled;
        }
    }
}