package planner.menu.component.editor;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.Core;
import planner.menu.component.Searchable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentCoolantRecipe extends MenuComponent implements Searchable{
    private final CoolantRecipe coolantRecipe;
    public MenuComponentCoolantRecipe(CoolantRecipe coolantRecipe){
        super(0, 0, 0, 0);
        this.coolantRecipe = coolantRecipe;
    }
    @Override
    public void render(){
        if(isMouseOver&&!isSelected)Core.applyAverageColor(Core.theme.getButtonColor(), Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(isSelected?Core.theme.getSelectedMultiblockColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        if(coolantRecipe.inputTexture!=null){
            Core.applyWhite();
            drawRect(x, y, x+height, y+height, Core.getTexture(coolantRecipe.inputDisplayTexture));
        }
        Core.applyColor(Core.theme.getTextColor());
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(coolantRecipe.getInputDisplayName(), height);
        double scale = Math.min(1, (width-(coolantRecipe.inputTexture!=null?height:0))/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(coolantRecipe.inputTexture!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, coolantRecipe.getInputDisplayName());
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