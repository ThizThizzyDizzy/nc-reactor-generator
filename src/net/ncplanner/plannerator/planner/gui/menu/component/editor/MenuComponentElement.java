package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class MenuComponentElement extends Component implements Pinnable{
    public final NCPFElement element;
    public MenuComponentElement(NCPFElement element){
        super(0, 0, 0, 0);
        this.element = element;
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
        if(element.getTexture()!=null){
            renderer.setWhite();
            renderer.drawImage(element.getTexture(), x, y, x+height, y+height);
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer);
    }
    public void drawText(Renderer renderer){
        String text = element.getDisplayName();
        if(text==null)text = "null";
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-(element.getTexture()!=null?height:0))/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(element.getTexture()!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override    
    public String getTooltip(){
        String ttp = "";
        for(NCPFModule module : element.modules.modules.values()){
            if(module instanceof ElementStatsModule){
                ElementStatsModule stats = (ElementStatsModule)module;
                ttp+="\n"+stats.getTooltip();
            }
        }
        return ttp.trim();
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = element.getSearchableNames();
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return element.getSimpleSearchableNames();
    }
    @Override
    public String getPinnedName(){
        return element.getPinnedName();
    }
}