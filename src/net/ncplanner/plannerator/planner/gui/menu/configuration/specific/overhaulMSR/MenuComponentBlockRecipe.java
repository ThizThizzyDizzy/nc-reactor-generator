package net.ncplanner.plannerator.planner.gui.menu.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
public class MenuComponentBlockRecipe extends Component{
    private final Block block;
    public final BlockRecipe blockRecipe;
    public final Button edit = add(new Button("", true, true){
        @Override
        public void drawForeground(double deltaTime){
            super.drawForeground(deltaTime);
            Renderer renderer = new Renderer();
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawElement("pencil", x, y, width, height);
        }
    }.setTooltip("Modify block recipe"));
    public final Button delete = add(new Button("", true, true){
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
        if(block.heater){
            if(block.heaterHasBaseStats)strs.add("Heater Cooling: "+block.heaterCooling+" H/t");
            else strs.add("Heater");
        }
        if(block.fuelVessel){
            strs.add("Fuel Efficiency: "+blockRecipe.fuelVesselEfficiency);
            strs.add("Fuel Heat: "+blockRecipe.fuelVesselHeat);
            strs.add("Fuel Criticality: "+blockRecipe.fuelVesselCriticality);
            if(blockRecipe.fuelVesselSelfPriming)strs.add("Fuel Self-Priming");
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
