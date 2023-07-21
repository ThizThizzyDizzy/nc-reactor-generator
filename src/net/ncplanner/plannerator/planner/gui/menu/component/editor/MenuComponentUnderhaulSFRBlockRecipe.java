package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block;
public class MenuComponentUnderhaulSFRBlockRecipe extends Component implements Pinnable{
    private final Block block;
    public final ActiveCoolerRecipe recipe;
    public MenuComponentUnderhaulSFRBlockRecipe(Block block, ActiveCoolerRecipe recipe){
        super(0, 0, 0, 0);
        this.block = block;
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
        if(recipe.getTexture()!=null){
            renderer.setWhite();
            renderer.drawImage(recipe.getTexture(), x, y, x+height, y+height);
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer);
    }
    public void drawText(Renderer renderer){
        float textLength = renderer.getStringWidth(recipe.getDisplayName(), height);
        float scale = Math.min(1, (width-(recipe.getTexture()!=null?height:0))/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(recipe.getTexture()!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.getDisplayName());
    }
    @Override    
    public String getTooltip(){
        String ttp = "";
        ttp+="Cooling: "+recipe.stats.cooling+"\n";
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