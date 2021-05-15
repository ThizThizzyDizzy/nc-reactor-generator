package planner.menu.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionsfr.Block;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistButton;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentPossibleAddonBlock extends MenuComponent{
    public final Block block;
    public final MenuComponentMinimalistButton add = add(new MenuComponentMinimalistButton(0, 0, 0, 0, "+", true, true, true).setTooltip("Add block (for block recipes)"));
    public MenuComponentPossibleAddonBlock(Block block){
        super(0, 0, 0, 50);
        this.block = block;
    }
    @Override
    public void renderBackground(){
        super.renderBackground();
        add.x = width-height/2-height;
        add.width = add.height = height;
    }
    @Override
    public void render(){
        if(isMouseOver)Core.applyColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void renderForeground(){
        Core.applyWhite();
        if(block.texture!=null)drawRect(x, y, x+height, y+height, Core.getTexture(block.displayTexture));
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.getDisplayName());
        while(strs.size()<2)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}