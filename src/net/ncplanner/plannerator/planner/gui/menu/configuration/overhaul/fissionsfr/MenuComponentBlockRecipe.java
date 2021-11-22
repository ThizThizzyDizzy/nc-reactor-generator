package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.Component;
public class MenuComponentBlockRecipe extends Component{
    private final Block block;
    public final BlockRecipe blockRecipe;
    public final Button edit = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillTri(x+width*.25f, y+height*.75f,
                    x+width*.375f, y+height*.75f,
                    x+width*.25f, y+height*.625f);
            renderer.fillQuad(x+width*.4f, y+height*.725f,
                    x+width*.275f, y+height*.6f,
                    x+width*.5f, y+height*.375f,
                    x+width*.625f, y+height*.5f);
            renderer.fillQuad(x+width*.525f, y+height*.35f,
                    x+width*.65f, y+height*.475f,
                    x+width*.75f, y+height*.375f,
                    x+width*.625f, y+height*.25f);
        }
    }.setTooltip("Modify block recipe"));
    public final Button delete = add(new Button(0, 0, 0, 0, "", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.fillQuad(x+width*.1f, y+height*.8f,
                    x+width*.2f, y+height*.9f,
                    x+width*.9f, y+height*.2f,
                    x+width*.8f, y+height*.1f);
            renderer.fillQuad(x+width*.1f, y+height*.2f,
                    x+width*.2f, y+height*.1f,
                    x+width*.9f, y+height*.8f,
                    x+width*.8f, y+height*.9f);
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
        if(block.fuelCell){
            strs.add("Fuel Efficiency: "+blockRecipe.fuelCellEfficiency);
            strs.add("Fuel Heat: "+blockRecipe.fuelCellHeat);
            strs.add("Fuel Criticality: "+blockRecipe.fuelCellCriticality);
            if(blockRecipe.fuelCellSelfPriming)strs.add("Fuel Self-Priming");
        }
        if(block.reflector){
            strs.add("Reflector Efficiency: "+blockRecipe.reflectorEfficiency);
            strs.add("Reflector Reflectivity: "+blockRecipe.reflectorReflectivity);
        }
        if(block.irradiator){
            strs.add("Irradiator Efficiency: "+blockRecipe.irradiatorEfficiency);
            strs.add("Irradiator Heat: "+blockRecipe.irradiatorHeat);
        }
        if(block.moderator){
            strs.add("Moderator Flux: "+blockRecipe.moderatorFlux);
            strs.add("Moderator Efficiency: "+blockRecipe.moderatorEfficiency);
            if(blockRecipe.moderatorActive)strs.add("Moderator Active");
        }
        if(block.shield){
            strs.add("Shield Efficiency: "+blockRecipe.shieldEfficiency);
            strs.add("Shield Heat: "+blockRecipe.shieldHeat);
        }
        while(strs.size()<5)strs.add("");
        for(int i = 0; i<strs.size(); i++){
            String str = strs.get(i);
            renderer.drawText(x+height*2, y+height/strs.size()*i, x+width, y+height/strs.size()*(i+1), str);
        }
//        drawText(x+height*2, y+height/4, x+width, y+height/4*2, blockRecipe.getOutputDisplayName());
    }
}
