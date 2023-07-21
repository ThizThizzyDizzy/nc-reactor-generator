package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
public class MenuComponentTurbineRecipe extends Component implements Pinnable{
    private final Recipe recipe;
    public MenuComponentTurbineRecipe(Recipe recipe){
        super(0, 0, 0, 0);
        this.recipe = recipe;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(isFocused){
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer);
    }
    public void drawText(Renderer renderer){
        float textLength = renderer.getStringWidth(recipe.getDisplayName(), height);
        float scale = Math.min(1, width/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.getDisplayName());
    }
    @Override    
    public String getTooltip(){
        return "Expansion Coefficient: "+recipe.stats.coefficient+"\n"
             + "Energy Density (RF/mb): "+recipe.stats.power;
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