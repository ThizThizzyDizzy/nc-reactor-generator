package planner.menu.component.editor;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fusion.Block;
import multiblock.configuration.overhaul.fusion.BlockRecipe;
import planner.Core;
import planner.menu.component.Searchable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentOverhaulFusionBlockRecipe extends MenuComponent implements Searchable{
    private final Block block;
    public final BlockRecipe recipe;
    public MenuComponentOverhaulFusionBlockRecipe(Block block, BlockRecipe recipe){
        super(0, 0, 0, 0);
        this.block = block;
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
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(recipe.getInputDisplayName(), height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.getInputDisplayName());
    }
    @Override    
    public String getTooltip(){
        String ttp = "";
        if(block.heatsink){
            ttp+="Heatsink Cooling: "+recipe.heatsinkCooling+"\n";
        }
        if(block.shielding){
            ttp+="Shieldiness: "+recipe.shieldingShieldiness+"\n";
        }
        if(block.reflector){
            ttp+="reflector Efficiency: "+recipe.reflectorEfficiency+"\n";
        }
        if(block.breedingBlanket){
            ttp+="Breeding Blanket Efficiency: "+recipe.breedingBlanketEfficiency+"\n";
            ttp+="Breeding Blanket Heat: "+recipe.breedingBlanketHeat+"\n";
            if(recipe.breedingBlanketAugmented)ttp+="Breeding Blanket Augmented"+"\n";
        }
        return ttp.trim();
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = recipe.getLegacyNames();
        lst.add(recipe.getInputDisplayName());
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
}