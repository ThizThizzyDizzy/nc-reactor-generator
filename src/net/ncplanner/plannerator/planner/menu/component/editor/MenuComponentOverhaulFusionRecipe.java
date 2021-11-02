package net.ncplanner.plannerator.planner.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentOverhaulFusionRecipe extends MenuComponent implements Pinnable{
    public final Recipe recipe;
    public MenuComponentOverhaulFusionRecipe(Recipe recipe){
        super(0, 0, 0, 0);
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
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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