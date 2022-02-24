package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fusion;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentBlockRecipe extends Component{
    private final Block block;
    public final BlockRecipe blockRecipe;
    public final Button edit = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify block recipe"));
    public final Button delete = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("delete", x, y, width, height);
        }
    }.setTooltip("Delete block recipe"));
    public MenuComponentBlockRecipe(Block block, BlockRecipe blockRecipe, Runnable onEditPressed, Runnable onDeletePressed){
        super(0, 0, 0, 100);
        this.block = block;
        this.blockRecipe = blockRecipe;
        edit.addAction(onEditPressed);
        delete.addAction(onDeletePressed);
    }
    @Override
    public void drawBackground(double deltaTime){
        super.drawBackground(deltaTime);
        delete.x = width-height/2-height/4;
        edit.x = delete.x - height;
        delete.y = edit.y = height/4;
        delete.width = delete.height = edit.width = edit.height = height/2;
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
        if(blockRecipe.inputTexture!=null)renderer.drawImage(blockRecipe.inputDisplayTexture, x, y, x+height, y+height);
        if(blockRecipe.outputTexture!=null)renderer.drawImage(blockRecipe.outputDisplayTexture, x+height, y, x+height*2, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<String> strs = new ArrayList<>();
        strs.add(blockRecipe.getInputDisplayName());
        if(block.heatsink){
            if(block.heatsinkHasBaseStats)strs.add("Heatsink Cooling: "+block.heatsinkCooling+" H/t");
            else strs.add("Heatsink");
        }
        if(block.reflector){
            strs.add("Reflector Efficiency: "+blockRecipe.reflectorEfficiency);
        }
        if(block.breedingBlanket){
            strs.add("Breeding Blanket Heat: "+blockRecipe.breedingBlanketHeat);
            strs.add("Breeding Blanket Efficiency: "+blockRecipe.breedingBlanketEfficiency);
            if(blockRecipe.breedingBlanketAugmented)strs.add("Breeding Blanket Augmented");
        }
        if(block.shielding){
            strs.add("Shielding Shieldiness: "+blockRecipe.shieldingShieldiness);
        }
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            renderer.drawText(x+height*2, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
//        drawText(x+height*2, y+height/4, x+width, y+height/4*2, blockRecipe.getOutputDisplayName());
    }
}
