package planner.menu.component.editor;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fusion.CoolantRecipe;
import planner.Core;
import planner.menu.component.Searchable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentFusionCoolantRecipe extends MenuComponent implements Searchable{
    private final CoolantRecipe coolantRecipe;
    public MenuComponentFusionCoolantRecipe(CoolantRecipe coolantRecipe){
        super(0, 0, 0, 0);
        this.coolantRecipe = coolantRecipe;
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
        double textLength = FontManager.getLengthForStringWithHeight(coolantRecipe.getInputDisplayName(), height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, coolantRecipe.getInputDisplayName());
    }
    @Override
    public String getTooltip(){
        return "Heat: "+coolantRecipe.heat+"\n"
             + "Output Ratio: "+coolantRecipe.outputRatio;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = coolantRecipe.getLegacyNames();
        lst.add(coolantRecipe.getInputDisplayName());
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
}