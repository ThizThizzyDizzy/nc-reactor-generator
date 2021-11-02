package net.ncplanner.plannerator.planner.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentOverhaulSFRBlockRecipe extends MenuComponent implements Pinnable{
    private final Block block;
    public final BlockRecipe recipe;
    public MenuComponentOverhaulSFRBlockRecipe(Block block, BlockRecipe recipe){
        super(0, 0, 0, 0);
        this.block = block;
        this.recipe = recipe;
    }
    @Override
    public void render(){
        Renderer renderer = new Renderer();
        if(isSelected){
            if(isMouseOver)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        if(recipe.inputTexture!=null){
            renderer.setWhite();
            drawRect(x, y, x+height, y+height, Core.getTexture(recipe.inputDisplayTexture));
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(recipe.getInputDisplayName(), height);
        double scale = Math.min(1, (width-(recipe.inputTexture!=null?height:0))/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(recipe.inputTexture!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.getInputDisplayName());
    }
    @Override    
    public String getTooltip(){
        String ttp = "";
        if(block.heatsink){
            ttp+="Heatsink Cooling: "+recipe.heatsinkCooling+"\n";
        }
        if(block.fuelCell){
            ttp+="Fuel Efficiency: "+recipe.fuelCellEfficiency+"\n";
            ttp+="Fuel Heat: "+recipe.fuelCellHeat+"\n";
            ttp+="Fuel Time: "+recipe.fuelCellTime+"\n";
            ttp+="Fuel Criticality: "+recipe.fuelCellCriticality+"\n";
            if(recipe.fuelCellSelfPriming)ttp+="Fuel Self-Priming\n";
        }
        if(block.reflector){
            ttp+="Reflector Efficiency: "+recipe.reflectorEfficiency+"\n";
            ttp+="Reflector Reflectivity: "+recipe.reflectorReflectivity+"\n";
        }
        if(block.irradiator){
            ttp+="Irradiator Efficiency: "+recipe.irradiatorEfficiency+"\n";
            ttp+="Irradiator Heat: "+recipe.irradiatorHeat+"\n";
        }
        if(block.moderator){
            ttp+="Moderator Flux: "+recipe.moderatorFlux+"\n";
            ttp+="Moderator Efficiency: "+recipe.moderatorEfficiency+"\n";
            if(recipe.moderatorActive)ttp+="Moderator Active"+"\n";
        }
        if(block.shield){
            ttp+="Shield Efficiency: "+recipe.shieldEfficiency+"\n";
            ttp+="Shield Heat: "+recipe.shieldHeat+"\n";
        }
        return ttp.trim();
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = recipe.getSearchableNames();
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return recipe.getSimpleSearchableNames();
    }
    @Override
    public String getPinnedName(){
        return recipe.getPinnedName();
    }
}