package net.ncplanner.plannerator.planner.gui.menu.component.editor;
import java.util.ArrayList;
import java.util.Arrays;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.MenuEdit;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentSuggestor extends Component implements Pinnable{
    private final MenuEdit editor;
    public final Suggestor suggestor;
    public boolean enabled = false;
    public MenuComponentSuggestor(MenuEdit editor, Suggestor suggestor){
        super(0, 0, 0, 64);
        this.editor = editor;
        this.suggestor = suggestor;
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
        drawText(renderer, suggestor.name+" ("+(suggestor.isActive()?"On":"Off")+")");
    }
    public void drawText(Renderer renderer, String text){
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, width/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public String getTooltip(){
        return suggestor.getDescription();
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS){
            enabled = !enabled;
            suggestor.setActive(enabled);
            editor.recalculateSuggestions();
        }
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> lst = getSimpleSearchableNames();
        for(String s : getTooltip().split("\n"))lst.add(s.trim());
        return lst;
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        ArrayList<String> lst = new ArrayList<>(Arrays.asList(suggestor.name));
        return lst;
    }
    @Override
    public String getPinnedName(){
        return suggestor.name;
    }
}