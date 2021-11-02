package net.ncplanner.plannerator.planner.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentUnderFuel extends MenuComponent implements Pinnable{
    private final Fuel fuel;
    public MenuComponentUnderFuel(Fuel fuel){
        super(0, 0, 0, 0);
        this.fuel = fuel;
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
        if(fuel.texture!=null){
            renderer.setWhite();
            drawRect(x, y, x+height, y+height, Core.getTexture(fuel.displayTexture));
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(fuel.getDisplayName(), height);
        double scale = Math.min(1, (width-(fuel.texture!=null?height:0))/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(fuel.texture!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, fuel.getDisplayName());
    }
    @Override
    public String getTooltip(){
        return "Base Power: "+fuel.power+"\n"
             + "Base Heat: "+fuel.heat+"\n"
             + "Base Time: "+fuel.time;
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = fuel.getSearchableNames();
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return fuel.getSimpleSearchableNames();
    }
    @Override
    public String getPinnedName(){
        return fuel.getPinnedName();
    }
}