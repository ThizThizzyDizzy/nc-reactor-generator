package planner.menu.component.editor;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fusion.Recipe;
import planner.Core;
import planner.menu.component.Searchable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentOverhaulFusionRecipe extends MenuComponent implements Searchable{
    public final Recipe recipe;
    public MenuComponentOverhaulFusionRecipe(Recipe recipe){
        super(0, 0, 0, 0);
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
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.getInputDisplayName());
    }
    @Override    
    public String getTooltip(){
        return "Efficiency: "+recipe.efficiency+"\n"
             + "Base Heat: "+recipe.heat+"\n"
             + "Fluxiness: "+recipe.fluxiness+"\n"
             + "Base Time: "+recipe.time;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = recipe.getLegacyNames();
        lst.add(recipe.getInputDisplayName());
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
}