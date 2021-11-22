package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.Component;
public class MenuComponentPossibleAddonBlock extends Component{
    public final Block block;
    public final Button add = add(new Button(0, 0, 0, 0, "+", true, true).setTooltip("Add block (for block recipes)"));
    public MenuComponentPossibleAddonBlock(Block block, Runnable onAddPressed){
        super(0, 0, 0, 50);
        this.block = block;
        add.addAction(onAddPressed);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        add.x = width-height/2-height;
        add.width = add.height = height;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverUnselectableComponentColor(Core.getThemeIndex(this)));
        else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        renderer.fillRect(x, y, x+width, y+height);
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        renderer.setWhite();
        if(block.texture!=null)renderer.drawImage(block.displayTexture, x, y, x+height, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(block.getDisplayName());
        while(strs.size()<2)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            renderer.drawText(x+height, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
    }
}