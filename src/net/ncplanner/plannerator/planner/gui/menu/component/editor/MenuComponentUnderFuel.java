package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.Component;
public class MenuComponentUnderFuel extends Component implements Pinnable{
    private final Fuel fuel;
    public MenuComponentUnderFuel(Fuel fuel){
        super(0, 0, 0, 0);
        this.fuel = fuel;
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
        if(fuel.texture!=null){
            renderer.setWhite();
            renderer.drawImage(fuel.displayTexture, x, y, x+height, y+height);
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer);
    }
    public void drawText(Renderer renderer){
        float textLength = renderer.getStringWidth(fuel.getDisplayName(), height);
        float scale = Math.min(1, (width-(fuel.texture!=null?height:0))/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(fuel.texture!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, fuel.getDisplayName());
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