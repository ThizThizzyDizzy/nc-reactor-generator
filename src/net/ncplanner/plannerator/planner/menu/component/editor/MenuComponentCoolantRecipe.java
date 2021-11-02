package net.ncplanner.plannerator.planner.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentCoolantRecipe extends MenuComponent implements Pinnable{
    private final CoolantRecipe coolantRecipe;
    public MenuComponentCoolantRecipe(CoolantRecipe coolantRecipe){
        super(0, 0, 0, 0);
        this.coolantRecipe = coolantRecipe;
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
        if(coolantRecipe.inputTexture!=null){
            renderer.setWhite();
            drawRect(x, y, x+height, y+height, Core.getTexture(coolantRecipe.inputDisplayTexture));
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
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
        ArrayList<String> lst = coolantRecipe.getSearchableNames();
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return coolantRecipe.getSimpleSearchableNames();
    }
    @Override
    public String getPinnedName(){
        return coolantRecipe.getPinnedName();
    }
}